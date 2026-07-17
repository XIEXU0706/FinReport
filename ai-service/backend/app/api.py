import json
import re
import asyncio
from datetime import datetime
from urllib.parse import quote
from fastapi import FastAPI, Depends
from fastapi.responses import StreamingResponse, Response
from pydantic import BaseModel
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from typing import Optional

from app.database import get_db
from app.models import ChatSession, Message
from app.agent import run_agent_stream
from app.export import markdown_to_docx

app = FastAPI()


class QuestionRequest(BaseModel):
    query: str
    session_id: str
    deep_think: Optional[bool] = False


class SessionUpdate(BaseModel):
    title: str


class ExportRequest(BaseModel):
    title: str = "经营分析报告"
    query: str = ""
    content: str


# ── 会话 CRUD ──────────────────────────────────────────────
@app.get("/sessions")
async def get_all_sessions(db: AsyncSession = Depends(get_db)):
    # 子查询：统计每个会话的消息数
    count_sub = (
        select(Message.session_id, func.count().label("cnt"))
        .group_by(Message.session_id)
        .subquery()
    )
    result = await db.execute(
        select(ChatSession, count_sub.c.cnt)
        .outerjoin(count_sub, ChatSession.id == count_sub.c.session_id)
        .order_by(ChatSession.create_time.desc())
    )
    return [
        {
            "id": s.id,
            "title": s.title,
            "create_time": s.create_time.isoformat() if s.create_time else None,
            "message_count": cnt or 0,
        }
        for s, cnt in result.all()
    ]


@app.post("/sessions")
async def create_new_session(db: AsyncSession = Depends(get_db)):
    new_session = ChatSession(title="新会话")
    db.add(new_session)
    await db.commit()
    await db.refresh(new_session)
    return new_session


@app.put("/sessions/{session_id}")
async def update_session(session_id: str, request: SessionUpdate, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(ChatSession).where(ChatSession.id == session_id))
    session = result.scalar_one_or_none()
    if session:
        session.title = request.title
        await db.commit()
        return {"status": "success", "title": session.title}
    return {"status": "error", "msg": "not found"}


@app.delete("/sessions/{session_id}")
async def delete_session(session_id: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(ChatSession).where(ChatSession.id == session_id))
    session = result.scalar_one_or_none()
    if session:
        await db.delete(session)
        await db.commit()
        return {"status": "success"}
    return {"status": "error", "msg": "not found"}


@app.get("/sessions/{session_id}/messages")
async def get_session_messages(session_id: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Message).where(Message.session_id == session_id).order_by(Message.create_time)
    )
    return result.scalars().all()


# ── AI 对话 ──────────────────────────────────────
@app.post("/fin_agent/ask")
async def ask_finance(request: QuestionRequest, db: AsyncSession = Depends(get_db)):
    # 保存用户问题
    user_msg = Message(session_id=request.session_id, role="user", content=request.query)
    db.add(user_msg)
    await db.commit()

    # 如果会话标题是默认的「新会话」，用第一句话更新
    session_result = await db.execute(select(ChatSession).where(ChatSession.id == request.session_id))
    current_session = session_result.scalar_one_or_none()
    if current_session and current_session.title == "新会话":
        current_session.title = (request.query[:15] + "...") if len(request.query) > 15 else request.query
        await db.commit()

    async def generate():
        clean_content = ""
        loop = asyncio.get_running_loop()
        queue: asyncio.Queue = asyncio.Queue()
        done_sentinel = object()

        def producer():
            try:
                for chunk in run_agent_stream(request.query, deep_think=request.deep_think):
                    loop.call_soon_threadsafe(queue.put_nowait, chunk)  # 将数据放入队列
                loop.call_soon_threadsafe(queue.put_nowait, done_sentinel)  # 发送结束标记
            except Exception as exc:
                loop.call_soon_threadsafe(queue.put_nowait, exc)

        producer_task = asyncio.create_task(asyncio.to_thread(producer))

        while True:
            item = await queue.get()
            if item is done_sentinel:
                break
            if isinstance(item, Exception):
                # 将异常转为 SSE 消息通知前端
                err_msg = json.dumps({"type": "error", "content": f"查询出错：{str(item)}"}, ensure_ascii=False)
                yield f"data: {err_msg}\n\n"
                break
            # item 是 JSON 字符串（来自 agent），解析后处理
            try:
                data = json.loads(item)
                if data.get("type") == "content":
                    clean_content += data["content"]
                yield f"data: {item}\n\n"
            except json.JSONDecodeError:
                yield f"data: {item}\n\n"

        await producer_task  # 等待生产者线程结束

        # 保存 AI 回答到数据库（只存纯文本）
        if clean_content:
            async for session in get_db():
                ai_msg = Message(session_id=request.session_id, role="ai", content=clean_content)
                session.add(ai_msg)
                await session.commit()
                break

    return StreamingResponse(generate(), media_type="text/event-stream")


@app.post("/export/word")
async def export_word(request: ExportRequest):
    # 从内容或问题中提取文件名
    filename = _extract_title(request.content, request.query)
    buf = markdown_to_docx(filename, request.content)
    # 中文文件名需要用 RFC 5987 编码
    encoded = quote(f"{filename}.docx")
    return Response(
        content=buf.read(),
        media_type="application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        headers={"Content-Disposition": f"attachment; filename*=UTF-8''{encoded}"},
    )


def _extract_title(content: str, query: str) -> str:
    """从内容或问题中提取文件名"""
    # 1. 从内容的第一行标题提取
    for line in content.split('\n'):
        line = line.strip()
        if line.startswith('# ') or line.startswith('## '):
            t = line.lstrip('#').strip().rstrip('。，！？.,!?')
            if 3 < len(t) < 40:
                return t
    # 2. 从用户问题提取（去掉常见前缀）
    q = query.strip()
    for prefix in ['写一份', '生成', '分析一下', '分析', '给我', '请', '帮我']:
        if q.startswith(prefix):
            q = q[len(prefix):].strip()
            break
    if q:
        # 去掉 "要求500字" "，要求xxx" 这类尾巴
        q = re.sub(r'[，,]\s*要求.*$', '', q)
        q = q[:30].rstrip('。，！？.,!?')
        if len(q) > 3:
            return q
    # 3. 兜底
    return f"{datetime.now().strftime('%Y-%m-%d')}-经营分析报告"
