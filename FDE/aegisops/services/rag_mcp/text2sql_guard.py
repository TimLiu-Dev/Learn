"""Deterministic read-only AST/allowlist Text2SQL guard enforcing tenant scope and row limits."""

from __future__ import annotations

import re

FORBIDDEN_KEYWORDS = re.compile(
    r"\b(INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|GRANT|REVOKE|COPY|CREATE|REPLACE|EXECUTE|CALL|ATTACH)\b",
    re.IGNORECASE,
)
TABLE_REF_RE = re.compile(r"\b(?:FROM|JOIN)\s+([a-zA-Z_][a-zA-Z0-9_.]*)", re.IGNORECASE)
LIMIT_RE = re.compile(r"\bLIMIT\s+(\d+)\b", re.IGNORECASE)


def validate_safe_text2sql(
    raw_sql: str,
    tenant_id: str,
    allowed_tables: set[str],
    max_limit: int = 100,
) -> dict:
    """Validate LLM-generated SQL before hitting a read-only Postgres connection."""
    sql = (raw_sql or "").strip()
    if not sql:
        return {"safe": False, "reason": "EMPTY_SQL", "sql": None}

    if ";" in sql.rstrip(";"):
        return {"safe": False, "reason": "MULTIPLE_STATEMENTS", "sql": None}
    sql = sql.rstrip(";").strip()

    if "--" in sql or "/*" in sql or "*/" in sql:
        return {"safe": False, "reason": "COMMENTS_FORBIDDEN", "sql": None}

    if not re.match(r"^SELECT\b", sql, re.IGNORECASE):
        return {"safe": False, "reason": "MUST_START_WITH_SELECT", "sql": None}

    if FORBIDDEN_KEYWORDS.search(sql):
        return {"safe": False, "reason": "MUTATION_OR_DDL_FORBIDDEN", "sql": None}

    referenced = {m.group(1).lower() for m in TABLE_REF_RE.finditer(sql)}
    if not referenced or not referenced.issubset({t.lower() for t in allowed_tables}):
        return {"safe": False, "reason": "UNLISTED_TABLE_FORBIDDEN", "sql": None}

    # Require explicit tenant_id predicate matching authenticated caller tenant
    tenant_pattern = re.compile(rf"\btenant_id\s*=\s*'{re.escape(tenant_id)}'", re.IGNORECASE)
    if not tenant_pattern.search(sql):
        return {"safe": False, "reason": "MISSING_TENANT_PREDICATE", "sql": None}

    limit_match = LIMIT_RE.search(sql)
    if not limit_match:
        sql = f"{sql} LIMIT {max_limit}"
    elif int(limit_match.group(1)) > max_limit:
        return {"safe": False, "reason": "LIMIT_EXCEEDS_MAX", "sql": None}

    return {"safe": True, "reason": None, "sql": sql}
