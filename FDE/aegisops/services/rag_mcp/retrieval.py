"""Pre-filter ReBAC ACL Hybrid Retrieval (BM25 + Dense RRF) & Prompt-Injection Screen."""

from __future__ import annotations

import re
from typing import Any

INJECTION_PATTERNS = [
    re.compile(r"ignore\s+(all\s+)?previous\s+instructions", re.IGNORECASE),
    re.compile(r"system\s+override", re.IGNORECASE),
    re.compile(r"!\[[^\]]*\]\(https?://", re.IGNORECASE),
    re.compile(r"reveal\s+(your\s+)?system\s+prompt", re.IGNORECASE),
]


def is_indirect_injection(text: str) -> bool:
    """Detect common indirect prompt-injection payloads and markdown beacon URLs."""
    return any(p.search(text or "") for p in INJECTION_PATTERNS)


def is_chunk_authorized(chunk: dict[str, Any], tenant_id: str, principals: set[str]) -> bool:
    """Enforce tenant isolation and SCIM/ReBAC principal overlap before ranking/truncation."""
    if chunk.get("tenant_id") != tenant_id:
        return False
    allowed = set(chunk.get("allowed_principals") or [])
    if not (allowed & principals):
        return False
    if chunk.get("injection_flagged") or is_indirect_injection(chunk.get("text", "")):
        return False
    return True


def prefilter_hybrid_rrf(
    bm25_ranked_ids: list[str],
    dense_ranked_ids: list[str],
    chunk_store: dict[str, dict[str, Any]],
    tenant_id: str,
    principals: list[str],
    top_k: int = 5,
    rrf_k: int = 60,
) -> list[dict[str, Any]]:
    """Pre-filter authorized chunks FIRST, then compute RRF scores across BM25 and dense lists."""
    principal_set = set(principals)
    if not principal_set:
        return []

    filtered_bm25 = [
        cid
        for cid in bm25_ranked_ids
        if cid in chunk_store and is_chunk_authorized(chunk_store[cid], tenant_id, principal_set)
    ]
    filtered_dense = [
        cid
        for cid in dense_ranked_ids
        if cid in chunk_store and is_chunk_authorized(chunk_store[cid], tenant_id, principal_set)
    ]

    scores: dict[str, float] = {}
    for ranked in (filtered_bm25, filtered_dense):
        for idx, cid in enumerate(ranked, start=1):
            scores[cid] = scores.get(cid, 0.0) + 1.0 / (rrf_k + idx)

    ordered_ids = sorted(scores.keys(), key=lambda cid: (-scores[cid], cid))[:top_k]
    return [{**chunk_store[cid], "chunk_id": cid, "rrf_score": round(scores[cid], 6)} for cid in ordered_ids]
