"""Scoped MCP tool dispatcher enforcing OIDC scopes and Human-in-the-Loop (HITL) write gates."""

from __future__ import annotations

from typing import Any


TOOL_CATALOG = {
    "lookup_order": {"required_scope": "orders:read", "requires_hitl": False},
    "issue_refund": {"required_scope": "orders:refund", "requires_hitl": True},
}


def dispatch_mcp_tool(
    tool_name: str,
    arguments: dict[str, Any],
    session: dict[str, Any],
    hitl_store: dict[str, dict[str, Any]],
) -> dict[str, Any]:
    """Authorize caller scope and pause high-impact write tools in HITL queue."""
    if not session or session.get("active") is not True:
        return {"status": "denied", "code": "SESSION_INACTIVE"}

    spec = TOOL_CATALOG.get(tool_name)
    if not spec:
        return {"status": "denied", "code": "UNKNOWN_TOOL"}

    scopes = set(session.get("scopes") or [])
    if spec["required_scope"] not in scopes:
        return {"status": "denied", "code": "MISSING_SCOPE"}

    if spec["requires_hitl"]:
        approval_id = f"hitl_{session['tenant_id']}_{tool_name}_{arguments.get('order_id', 'unknown')}"
        if approval_id not in hitl_store:
            hitl_store[approval_id] = {
                "approval_id": approval_id,
                "tenant_id": session["tenant_id"],
                "tool_name": tool_name,
                "arguments": dict(arguments),
                "status": "pending",
            }
        return {"status": "awaiting_hitl", "approval_id": approval_id}

    return {
        "status": "executed",
        "tool_name": tool_name,
        "result": {"order_id": arguments.get("order_id"), "tenant_id": session["tenant_id"]},
    }


def resolve_hitl_approval(
    approval_id: str,
    decision: str,
    operator_sub: str,
    hitl_store: dict[str, dict[str, Any]],
) -> dict[str, Any]:
    """Race-safe single-transition approval resolver (cannot double-execute)."""
    item = hitl_store.get(approval_id)
    if not item:
        return {"ok": False, "code": "NOT_FOUND"}
    if item["status"] != "pending":
        return {"ok": False, "code": "ALREADY_DECIDED", "status": item["status"]}
    if decision not in ("approved", "rejected"):
        return {"ok": False, "code": "INVALID_DECISION"}

    item["status"] = "executed" if decision == "approved" else "rejected"
    item["decided_by"] = operator_sub
    return {"ok": True, "status": item["status"], "approval_id": approval_id}
