# ADR 0001: Pre-Filter ReBAC ACLs Inside Retrieval Queries

- **Status:** Accepted
- **Context:** Post-filtering top-K search results in Python leaks restricted snippets if an entire top-K batch contains unauthorized executive chunks (returning 0 usable docs) or risks prompt leakage if filtering is bypassed.
- **Decision:** Enforce `tenant_id` and SCIM/ReBAC `allowed_principals` predicates *before* `LIMIT` and RRF fusion (`services/rag_mcp/retrieval.py` and `db/migrations/001_init_ontology_pgvector.sql`).
- **Consequences:** Single-database PostgreSQL (`pgvector` + `tsvector`) keeps transactional ACL consistency across ingestion and retrieval without syncing separate Pinecone/Elasticsearch ACL clusters.
