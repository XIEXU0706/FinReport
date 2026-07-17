"""
    金融报表数据查询工具
    通过 function calling 供 LLM 调用，直接查询 fin_report 库
"""
import json
import os
from datetime import datetime
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from langchain_core.tools import tool

DATABASE_URL = "mysql+pymysql://root:123456@localhost:3306/fin_report?charset=utf8mb4"
engine = create_engine(DATABASE_URL, pool_size=5, max_overflow=10)
SessionLocal = sessionmaker(bind=engine)


def get_session():
    return SessionLocal()


def _exec(sql: str, params: dict = None) -> str:
    """执行 SQL 并返回 JSON 字符串"""
    try:
        with get_session() as sess:
            result = sess.execute(text(sql), params or {})
            rows = [dict(row._mapping) for row in result]
            return json.dumps(rows, ensure_ascii=False, default=str)
    except Exception as e:
        return json.dumps({"error": str(e)}, ensure_ascii=False)


@tool
def get_transaction_summary(start_date: str, end_date: str) -> str:
    """
    查交易汇总。获取指定日期范围内的每日交易总额、总笔数、平均交易额。
    参数格式：YYYY-MM-DD
    """
    sql = """
        SELECT
            DATE(trans_time) AS trans_date,
            COUNT(*)        AS total_count,
            SUM(amount)     AS total_amount,
            AVG(amount)     AS avg_amount
        FROM trans_log
        WHERE DATE(trans_time) BETWEEN :start_date AND :end_date
        GROUP BY DATE(trans_time)
        ORDER BY trans_date
    """
    return _exec(sql, {"start_date": start_date, "end_date": end_date})


@tool
def get_loan_summary(start_date: str, end_date: str) -> str:
    """
    查贷款汇总。获取指定日期范围内的每日贷款发放笔数、总额，以及不良贷款笔数和金额。
    参数格式：YYYY-MM-DD
    """
    sql = """
        SELECT
            loan_date,
            COUNT(*)                                         AS total_count,
            SUM(amount)                                      AS total_amount,
            SUM(CASE WHEN status = 'BAD' THEN 1 ELSE 0 END)  AS bad_count,
            SUM(CASE WHEN status = 'BAD' THEN amount ELSE 0 END) AS bad_amount
        FROM loan_record
        WHERE loan_date BETWEEN :start_date AND :end_date
        GROUP BY loan_date
        ORDER BY loan_date
    """
    return _exec(sql, {"start_date": start_date, "end_date": end_date})


@tool
def get_alert_list(status: str = "", start_date: str = "", end_date: str = "") -> str:
    """
    查预警记录。可按状态筛选（NEW=未处理, ACKNOWLEDGED=已确认, RESOLVED=已解决），也可按日期范围过滤。
    不传参时返回最近 100 条预警。
    """
    sql = "SELECT * FROM alert_log WHERE 1=1"
    params = {}
    if status:
        sql += " AND status = :status"
        params["status"] = status
    if start_date:
        sql += " AND DATE(created_at) >= :start_date"
        params["start_date"] = start_date
    if end_date:
        sql += " AND DATE(created_at) <= :end_date"
        params["end_date"] = end_date
    sql += " ORDER BY created_at DESC LIMIT 100"
    return _exec(sql, params)


@tool
def get_customer_summary(start_date: str, end_date: str) -> str:
    """
    查客户增长。获取指定日期范围内的每日新增客户数。
    参数格式：YYYY-MM-DD
    """
    sql = """
        SELECT
            DATE(created_at)          AS reg_date,
            COUNT(*)                  AS new_customer_count
        FROM customer
        WHERE DATE(created_at) BETWEEN :start_date AND :end_date
        GROUP BY DATE(created_at)
        ORDER BY reg_date
    """
    return _exec(sql, {"start_date": start_date, "end_date": end_date})


FINANCIAL_TOOLS = [
    get_transaction_summary,
    get_loan_summary,
    get_alert_list,
    get_customer_summary,
]
