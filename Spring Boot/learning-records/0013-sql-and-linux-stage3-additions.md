# Learning Record 0013: Production SQL & Linux Essentials Added to Stage 3

**Timestamp:** 2026-11-21  
**Goal:** Add dedicated Production SQL (`JdbcClient`, CTEs, Window Functions, `UPSERT`, `JSONB`) and Linux & Container Diagnostics (`SIGTERM` 143 vs 137, `ulimit -n`, `ss`/`lsof`, `jq`) to Stage 3 Phase 14, bringing the complete curriculum to 79 lessons (66 Core Stages 1–3 + 13 Optional Stage 4).

---

### Additions in Stage 3 • Phase 14 (Production SQL, Linux & Networking Foundations)
- `0078-production-sql-ctes-window-functions-and-jdbcclient.html`: Fluent Spring `JdbcClient`, PostgreSQL CTEs, `ROW_NUMBER() OVER (...)` window rankings, atomic `INSERT ... ON CONFLICT DO UPDATE`, `JSONB` + `GIN`, and partial indexes.
- `0079-linux-and-container-diagnostics-for-java-backends.html`: JSON exec-form `ENTRYPOINT` PID 1 `SIGTERM` (143 vs 137), open file descriptors (`ulimit -n`), non-root `--chown`/`chmod`, `ss`/`lsof` socket triage, and `jq` log filtering.
