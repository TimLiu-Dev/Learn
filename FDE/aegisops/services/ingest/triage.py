"""Config-driven 3-bucket dirty-data ingestion, PII scrubbing, DLQ, and monotonic upsert."""

from __future__ import annotations

import json
import math
import re
from typing import Any

EMAIL_RE = re.compile(r"[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}", re.IGNORECASE)
SSN_RE = re.compile(r"\b\d{3}-\d{2}-\d{4}\b")
ALLOWED_STATUSES = {"pending", "paid", "shipped", "cancelled"}


def redact_pii(text: str) -> str:
    """Scrub emails and SSNs deterministically before writing DLQ summaries or logs."""
    cleaned = EMAIL_RE.sub("[REDACTED_EMAIL]", str(text or ""))
    return SSN_RE.sub("[REDACTED_SSN]", cleaned)


def remap_fields(raw: dict[str, Any], field_map: dict[str, str]) -> dict[str, Any]:
    """Map customer-specific column names into canonical AegisOps fields without code forks."""
    return {
        "order_id": raw.get(field_map.get("order_id", "order_id")),
        "amount": raw.get(field_map.get("amount", "amount")),
        "status": raw.get(field_map.get("status", "status")),
        "updated_at_ms": raw.get(field_map.get("updated_at_ms", "updated_at_ms")),
        "account_ref": raw.get(field_map.get("account_ref", "account_ref")),
        "note": raw.get("note", ""),
    }


def _parse_amount(raw_amount: Any) -> tuple[float | None, list[str]]:
    warnings: list[str] = []
    if isinstance(raw_amount, bool):
        return None, warnings
    if isinstance(raw_amount, (int, float)):
        val = float(raw_amount)
        return (val, warnings) if math.isfinite(val) and val > 0 else (None, warnings)
    if isinstance(raw_amount, str):
        stripped = re.sub(r"[$,\s]", "", raw_amount)
        if not stripped:
            return None, warnings
        try:
            val = float(stripped)
        except ValueError:
            return None, warnings
        if math.isfinite(val) and val > 0:
            warnings.append("COERCED_AMOUNT_STRING")
            return val, warnings
    return None, warnings


def ingest_webhook_event(
    raw_body: bytes,
    tenant_config: dict[str, Any],
    store: dict[str, Any],
    now_ms: int,
) -> dict[str, Any]:
    """Parse raw JSON event, triage dirty values into DLQ, and enforce monotonic upserts."""
    tenant_id = str(tenant_config["tenant_id"])
    dlq = store.setdefault("dlq", [])
    orders = store.setdefault("orders", {})

    try:
        parsed = json.loads(raw_body.decode("utf-8"))
    except (UnicodeDecodeError, json.JSONDecodeError):
        dlq.append(
            {
                "tenant_id": tenant_id,
                "order_id": None,
                "reason_code": "MALFORMED_JSON",
                "safe_summary": "Invalid JSON webhook payload",
            }
        )
        return {"http_status": 400, "bucket": "dlq", "reason_code": "MALFORMED_JSON"}

    if not isinstance(parsed, dict):
        dlq.append(
            {
                "tenant_id": tenant_id,
                "order_id": None,
                "reason_code": "MALFORMED_ROW",
                "safe_summary": "Expected JSON object payload",
            }
        )
        return {"http_status": 202, "bucket": "dlq", "reason_code": "MALFORMED_ROW"}

    mapped = remap_fields(parsed, tenant_config.get("field_map", {}))
    order_id = str(mapped["order_id"]).strip() if isinstance(mapped["order_id"], str) else ""
    if not order_id:
        dlq.append(
            {
                "tenant_id": tenant_id,
                "order_id": None,
                "reason_code": "MISSING_IDENTITY",
                "safe_summary": redact_pii(mapped["note"] or "Missing canonical order_id"),
            }
        )
        return {"http_status": 202, "bucket": "dlq", "reason_code": "MISSING_IDENTITY"}

    amount, warnings = _parse_amount(mapped["amount"])
    if amount is None:
        dlq.append(
            {
                "tenant_id": tenant_id,
                "order_id": order_id,
                "reason_code": "INVALID_AMOUNT",
                "safe_summary": redact_pii(mapped["note"] or f"Invalid amount on order {order_id}"),
            }
        )
        return {"http_status": 202, "bucket": "dlq", "reason_code": "INVALID_AMOUNT"}

    raw_status = mapped["status"]
    if raw_status is None or (isinstance(raw_status, str) and not raw_status.strip()):
        status = "pending"
        warnings.append("DEFAULTED_EMPTY_STATUS")
    elif isinstance(raw_status, str) and raw_status.strip().lower() in ALLOWED_STATUSES:
        status = raw_status.strip().lower()
    else:
        dlq.append(
            {
                "tenant_id": tenant_id,
                "order_id": order_id,
                "reason_code": "UNKNOWN_STATUS",
                "safe_summary": redact_pii(mapped["note"] or f"Unknown status on order {order_id}"),
            }
        )
        return {"http_status": 202, "bucket": "dlq", "reason_code": "UNKNOWN_STATUS"}

    raw_ts = mapped["updated_at_ms"]
    updated_at_ms = int(raw_ts) if isinstance(raw_ts, (int, float)) else int(now_ms)

    composite_key = f"{tenant_id}::{order_id}"
    existing = orders.get(composite_key)
    if existing and existing["updated_at_ms"] >= updated_at_ms:
        return {"http_status": 200, "bucket": "skipped_stale", "order_id": order_id}

    orders[composite_key] = {
        "tenant_id": tenant_id,
        "order_id": order_id,
        "amount": amount,
        "status": status,
        "updated_at_ms": updated_at_ms,
        "warnings": warnings,
    }
    return {"http_status": 200, "bucket": "upserted", "order_id": order_id, "warnings": warnings}
