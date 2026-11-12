"""Executable CI evaluation and OWASP red-team gate for AegisOps."""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from services.rag_mcp.retrieval import prefilter_hybrid_rrf
from services.rag_mcp.text2sql_guard import validate_safe_text2sql

FIXTURE_CHUNKS = {
    "c_exec": {"tenant_id": "acme", "allowed_principals": ["group:exec"], "text": "Executive comp memo"},
    "c_other_tenant": {"tenant_id": "globex", "allowed_principals": ["group:eng"], "text": "Globex ERR-409-X"},
    "c_valid": {"tenant_id": "acme", "allowed_principals": ["group:eng"], "text": "Retry backoff policy"},
    "c_ops_sku": {"tenant_id": "acme", "allowed_principals": ["group:eng"], "text": "SKU ERR-409-X remediation"},
}


def compute_cohens_kappa(pairs: list[tuple[bool, bool]]) -> float:
    n = len(pairs)
    if n == 0:
        return 0.0
    p_obs = sum(1 for h, j in pairs if h == j) / n
    p_h_yes = sum(1 for h, _ in pairs if h) / n
    p_j_yes = sum(1 for _, j in pairs if j) / n
    p_exp = (p_h_yes * p_j_yes) + ((1 - p_h_yes) * (1 - p_j_yes))
    if p_exp == 1.0:
        return 1.0
    return round((p_obs - p_exp) / (1.0 - p_exp), 4)


def run_gate(min_recall: float, min_redteam_pass: float, min_kappa: float = 0.70) -> int:
    eval_dir = Path(__file__).resolve().parent
    golden = [json.loads(line) for line in (eval_dir / "golden_eval.jsonl").read_text().splitlines() if line.strip()]
    redteam = [json.loads(line) for line in (eval_dir / "owasp_redteam.jsonl").read_text().splitlines() if line.strip()]

    hits = 0
    faith_scores = []
    kappa_pairs = []
    for case in golden:
        res = prefilter_hybrid_rrf(
            case["bm25"], case["dense"], FIXTURE_CHUNKS, case["tenant_id"], case["principals"], top_k=5
        )
        if any(r["chunk_id"] == case["expected_chunk"] for r in res):
            hits += 1
        faith_scores.append(case["faithfulness"])
        kappa_pairs.append((case["human_label"], case["judge_label"]))

    recall_at_5 = hits / len(golden)
    avg_faithfulness = sum(faith_scores) / len(faith_scores)
    kappa = compute_cohens_kappa(kappa_pairs)

    rt_passed = 0
    for rt in redteam:
        if rt["attack"] in ("indirect_injection", "markdown_beacon"):
            store = {
                **FIXTURE_CHUNKS,
                "c_evil": {"tenant_id": rt["tenant_id"], "allowed_principals": rt["principals"], "text": rt["text"]},
            }
            res = prefilter_hybrid_rrf(["c_evil", "c_valid"], ["c_evil"], store, rt["tenant_id"], rt["principals"], top_k=5)
            if all(r["chunk_id"] != "c_evil" for r in res):
                rt_passed += 1
        else:
            verdict = validate_safe_text2sql(rt["sql"], rt["tenant_id"], set(rt["allowed_tables"]))
            if not verdict["safe"]:
                rt_passed += 1

    redteam_pass_rate = rt_passed / len(redteam)
    print(
        json.dumps(
            {
                "recall_at_5": round(recall_at_5, 4),
                "avg_faithfulness": round(avg_faithfulness, 4),
                "judge_kappa": kappa,
                "redteam_pass_rate": round(redteam_pass_rate, 4),
            },
            indent=2,
        )
    )

    if recall_at_5 < min_recall or redteam_pass_rate < min_redteam_pass or kappa < min_kappa:
        print("EVAL CI GATE FAILED", file=sys.stderr)
        return 1
    print("EVAL CI GATE PASSED")
    return 0


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--min-recall", type=float, default=0.85)
    parser.add_argument("--min-redteam-pass", type=float, default=1.00)
    args = parser.parse_args()
    sys.exit(run_gate(args.min_recall, args.min_redteam_pass))
