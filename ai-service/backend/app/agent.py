"""
    金融报表 AI 代理
    使用 function calling 机制：LLM 自主决定查哪些数据 → 工具查询 MySQL → LLM 生成回答
"""
import os
import json
import re
from datetime import date
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, ToolMessage
from app.tools import FINANCIAL_TOOLS
from dotenv import load_dotenv

load_dotenv()

TODAY = date.today()

SYSTEM_PROMPT = f"""你是一位专业的金融报表分析助手。你可以查询 MySQL 数据库中的金融数据来回答用户的问题。

今天是 {TODAY.isoformat()}。

可用查询工具：
1. get_transaction_summary(start_date, end_date) — 查交易汇总（总额、笔数）
2. get_loan_summary(start_date, end_date) — 查贷款汇总（发放额、不良贷款）
3. get_alert_list(status, start_date, end_date) — 查预警记录
4. get_customer_summary(start_date, end_date) — 查客户增长

请根据用户问题判断需要查询哪些数据，调用相应工具获取数据后，生成清晰、专业的分析回答。
如果用户没有明确日期，请基于今天的日期推算合理的时间范围（如「上个月」→ 上个月整月，「上周」→ 上周一到上周日）。

注意：
1. 回答中不要使用任何 emoji 表情符号，使用纯文字表达。
2. 必须使用可用的工具函数来查询数据，**不要以文字描述工具调用过程**，直接调用工具获取真实数据。"""


def _parse_text_tool_calls(text: str):
    """
    解析 LLM 以文本形式输出的工具调用（DeepSeek 有时不返回结构化 tool_calls）。
    格式示例：
    <tool_calls>
    <invoke name="get_loan_summary">
    <parameter name="start_date" string="true">2026-07-13</parameter>
    <parameter name="end_date" string="true">2026-07-17</parameter>
    </invoke>
    </tool_calls>
    """
    calls = []
    # 匹配 <invoke name="xxx">...</invoke>
    pattern = r'<invoke\s+name="([^"]+)"\s*>(.*?)</invoke>'
    for match in re.finditer(pattern, text, re.DOTALL):
        name = match.group(1)
        body = match.group(2)
        args = {}
        # 匹配 <parameter name="xxx" string="true">value</parameter>
        for pm in re.finditer(r'<parameter\s+name="([^"]+)"[^>]*>([^<]*)</parameter>', body):
            args[pm.group(1)] = pm.group(2)
        if name and args:
            calls.append((name, args))
    return calls


def get_llm(deep_think=False):
    model = "deepseek-reasoner" if deep_think else "deepseek-chat"
    return ChatOpenAI(
        model=model,
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url="https://api.deepseek.com/v1",
        temperature=0.3,
    )


def run_agent_stream(query: str, deep_think: bool = False):
    """
    流式执行 agent。
    1. LLM 决定是否调用工具
    2. 若调用工具，执行后将结果送回 LLM
    3. 最终回答以流式（逐 chunk）方式 yield 返回
    yield 格式：{"type": "content", "content": "..."}
               {"type": "status", "content": "..."}
    """
    llm = get_llm(deep_think)

    # 如果 deep_think 模型不支持 tool calling，回退到非深度思考模式
    if deep_think:
        try:
            llm_with_tools = llm.bind_tools(FINANCIAL_TOOLS)
            # 发一条简单消息测试
            test = llm_with_tools.invoke([HumanMessage(content="test")])
        except Exception:
            llm = get_llm(deep_think=False)
            llm_with_tools = llm.bind_tools(FINANCIAL_TOOLS)
            yield json.dumps({"type": "status", "content": "深度思考模式不支持工具调用，已切换为普通模式。"}, ensure_ascii=False)
    else:
        llm_with_tools = llm.bind_tools(FINANCIAL_TOOLS)

    messages = [
        SystemMessage(content=SYSTEM_PROMPT),
        HumanMessage(content=query),
    ]

    # 第一轮：LLM 决定是否要调工具
    result = llm_with_tools.invoke(messages)
    messages.append(result)

    if hasattr(result, "tool_calls") and result.tool_calls:
        yield json.dumps({"type": "status", "content": "正在查询数据库..."}, ensure_ascii=False)

        for tool_call in result.tool_calls:
            tool_name = tool_call["name"]
            tool_args = tool_call["args"]

            for tool in FINANCIAL_TOOLS:
                if tool.name == tool_name:
                    try:
                        tool_result = tool.invoke(tool_args)
                        messages.append(
                            ToolMessage(content=tool_result, tool_call_id=tool_call["id"])
                        )
                    except Exception as e:
                        messages.append(
                            ToolMessage(
                                content=json.dumps({"error": str(e)}, ensure_ascii=False),
                                tool_call_id=tool_call["id"],
                            )
                        )
                    break

        # 第二轮：LLM 基于查询结果生成最终回答（流式）
        yield json.dumps({"type": "status", "content": "正在生成分析结果..."}, ensure_ascii=False)

        stream = llm.stream(messages)
        for chunk in stream:
            if chunk.content:
                yield json.dumps({"type": "content", "content": chunk.content}, ensure_ascii=False)
    else:
        # LLM 没有返回结构化 tool_calls，检查是否以文本形式输出了工具调用
        content = result.content if hasattr(result, "content") else str(result)
        parsed_calls = _parse_text_tool_calls(content)
        if parsed_calls:
            yield json.dumps({"type": "status", "content": "正在查询数据库..."}, ensure_ascii=False)
            for tool_name, tool_args in parsed_calls:
                for tool in FINANCIAL_TOOLS:
                    if tool.name == tool_name:
                        try:
                            tool_result = tool.invoke(tool_args)
                            messages.append(
                                ToolMessage(content=tool_result, tool_call_id="fallback")
                            )
                        except Exception as e:
                            messages.append(
                                ToolMessage(content=json.dumps({"error": str(e)}, ensure_ascii=False), tool_call_id="fallback")
                            )
                        break
            # 重新请求 LLM 生成最终回答
            yield json.dumps({"type": "status", "content": "正在生成分析结果..."}, ensure_ascii=False)
            stream = llm.stream(messages)
            for chunk in stream:
                if chunk.content:
                    yield json.dumps({"type": "content", "content": chunk.content}, ensure_ascii=False)
        else:
            # 真的没有调工具，直接返回 LLM 回答
            yield json.dumps({"type": "content", "content": content}, ensure_ascii=False)
