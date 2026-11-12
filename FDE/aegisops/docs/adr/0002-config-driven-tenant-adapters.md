# ADR 0002: Declarative Per-Tenant Configs Over Customer Code Forks

- **Status:** Accepted
- **Context:** Every enterprise customer exports different ERP column names (`erp_ref` vs. `claim_batch_id`), rate limits, and ZDR settings. Branching code per client creates the Consulting Trap.
- **Decision:** Isolate all customer schema mapping and policy flags inside `configs/tenants/*.json`, consumed by one shared ingestion binary (`services/ingest/triage.py`).
- **Consequences:** Security patches, DLQ replay fixes, and monotonic upserts ship to all customers simultaneously without maintaining per-tenant git branches.
