# Mission: End-to-End Database Engineering (Junior Android &rarr; Senior 2027)

## Why
Transition from a junior Android developer (whose only database exposure is basic Room DB) to a Senior-level Database & Backend Engineer in 2027, capable of designing, tuning, scaling, securing, and operating production database systems and landing a job with a resume-grade end-to-end capstone project.

## Success looks like
- Design strict, normalized relational schemas (and intentional denormalizations / `JSONB`) that enforce business invariants at the database boundary rather than relying solely on app code.
- Write and optimize complex SQL (CTEs, window functions, `UPSERT`, keyset pagination) and diagnose slow queries using `EXPLAIN ANALYZE` and composite/partial/covering indexes.
- Reason accurately about storage engines (B+Trees vs LSM-Trees, WAL, MVCC, vacuum), concurrency anomalies (write skew, lost updates, deadlocks, `SKIP LOCKED`), and zero-downtime schema migrations.
- Architect production data infrastructure: connection pooling (`PgBouncer`), replication & PITR backups (RPO/RTO), partitioning/sharding, caching (Redis), CDC/OLAP (DuckDB/ClickHouse), and 2027 AI hybrid search (`pgvector` + BM25 RRF).
- Ship and defend **PulseLedger**, an end-to-end multi-tenant FinTech ledger, job queue, analytics pipeline, and hybrid search capstone worthy of a resume and senior technical interviews.

## Constraints
- Starting baseline: Junior Android developer (familiar with Room `@Entity`, `@Dao`, `@Query`, basic local SQLite).
- Preferred learning format: Interactive HTML lessons with live runnable SQL code, auto-checked coding labs with progressive hints and final answers, and diagnostic multiple-choice quizzes explaining why each option is right or wrong.
- Must run cleanly offline in-browser via `sql-asm.js` (SQLite 3.45.2) with explicit PostgreSQL 17/18 production parity callouts.

## Out of scope
- Proprietary vendor-specific DBA GUI tooling unrelated to core engine internals or standard SQL/PostgreSQL workflows.
- Mobile-only Room UI bindings (`LiveData`/`Compose` UI plumbing) beyond using Room as a mental-model bridge.
