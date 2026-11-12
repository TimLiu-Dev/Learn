# AegisOps — Multi-Tenant Enterprise AI Operations Platform

One evolving portfolio repository built across **two job-ready milestones**:

- **Milestone 1 — Capstone I (`AegisOps Core` · End of Track 1, Units `0020`–`0021`):**
  Proves Mid-Level FDE readiness so you can **start applying to Mid-Level / Startup FDE roles** right away:
  1. `services/ingest/` + `configs/tenants/`: Raw-body HMAC-SHA256 webhook verification, declarative `fieldMap` configs, PII-scrubbed DLQs, and monotonic idempotent upserts.
  2. `db/migrations/` + `docker-compose.yml`: PostgreSQL 16 + `pgvector` schema with tenant-first composite B-tree, GIN (`tsvector`), and HNSW (`vector`) indexes.
  3. `services/rag_mcp/retrieval.py` + `apps/workbench/`: Tenant-scoped Hybrid Search (BM25 + dense RRF), grounded citation verification, React/TS SSE streaming UI, and baseline unit/golden checks.

- **Milestone 2 — Capstone II (`AegisOps Enterprise` · End of Track 2, Units `0038`–`0039`):**
  Upgrades the exact same repo to clear **Senior FDE ($310k–$750k+ TC) & Frontier AI Lab** security/scale bars:
  4. `services/rag_mcp/`: Pre-filter SCIM/ReBAC ACL enforcement *before* top-K truncation, AST/allowlist read-only Text2SQL guard, and scoped MCP tools with race-proof HITL approvals (`hitl_approvals`).
  5. `apps/workbench/src/reducer.ts`: Split-chunk Markdown external-image beacon sanitizer + OTel `traceId` links.
  6. `infra/`, `evals/`, `.github/workflows/`, `docs/`: Parameterized VPC/KMS Terraform, K8s `startupProbe` + default-deny `NetworkPolicy`, golden + OWASP red-team CI gate (`run_evals.py`), 3 ADRs, and UAT/incident runbooks.

---

## Quickstart & Local Verification

Run all ingestion, ACL retrieval, Text2SQL, MCP/HITL, and CI evaluation gate tests with Python stdlib (`unittest`)—zero external cloud keys required:

```bash
cd aegisops
python3 -m unittest discover -s services/ingest/tests -v
python3 -m unittest discover -s services/rag_mcp/tests -v
python3 evals/run_evals.py --min-recall 0.85 --min-redteam-pass 1.00
```

Launch PostgreSQL 16 + `pgvector` locally for full database schema testing:

```bash
docker compose up -d db
```

---

## 2-Minute Executive Demo Script (For Hiring Panels)

1. **0:00–0:25 (Messy Reality):** Send a malformed ERP webhook containing `"$1,240.50"`, a stale timestamp, and an email PII note into `services/ingest/`; show monotonic upsert + PII-redacted DLQ quarantine instead of crashing the batch.
2. **0:25–1:05 (Zero-Leak ACL + Hybrid RAG):** Query the same SKU code (`ERR-409-X`) as `eng` vs. `exec`; show pre-filter ReBAC blocking confidential chunks before top-K and Hybrid RRF ranking exact error codes #1.
3. **1:05–1:40 (MCP + HITL + Injection Defense):** Trigger a high-value write action pausing cleanly in `hitl_approvals`, and show an indirect ticket injection blocked before rendering.
4. **1:40–2:00 (Eval CI & ADRs):** Run `evals/run_evals.py` and `.github/workflows/aegisops-ci.yml` + walk through `docs/adr/` explaining architectural trade-offs.
