-- AegisOps Core Operational Ontology + Hybrid RAG Schema (PostgreSQL 16 + pgvector)
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS ontology_entities (
    tenant_id TEXT NOT NULL,
    canonical_id TEXT NOT NULL,
    entity_type TEXT NOT NULL,
    source_aliases JSONB NOT NULL DEFAULT '[]'::jsonb,
    display_name TEXT NOT NULL,
    updated_at_ms BIGINT NOT NULL,
    PRIMARY KEY (tenant_id, canonical_id)
);

CREATE TABLE IF NOT EXISTS orders (
    tenant_id TEXT NOT NULL,
    order_id TEXT NOT NULL,
    canonical_account_id TEXT NOT NULL,
    amount_usd NUMERIC(12, 2) NOT NULL CHECK (amount_usd > 0),
    status TEXT NOT NULL,
    updated_at_ms BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, order_id),
    FOREIGN KEY (tenant_id, canonical_account_id)
        REFERENCES ontology_entities (tenant_id, canonical_id)
);

-- Tenant-first composite index supporting deterministic keyset cursor pagination
CREATE INDEX IF NOT EXISTS idx_orders_tenant_cursor
    ON orders (tenant_id, created_at DESC, order_id DESC);

CREATE TABLE IF NOT EXISTS dlq_events (
    id BIGSERIAL PRIMARY KEY,
    tenant_id TEXT NOT NULL,
    order_id TEXT,
    reason_code TEXT NOT NULL,
    safe_summary TEXT NOT NULL,
    attempt_count INT NOT NULL DEFAULT 1,
    dead_lettered BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_dlq_tenant_pending
    ON dlq_events (tenant_id, dead_lettered, created_at DESC);

CREATE TABLE IF NOT EXISTS doc_chunks (
    chunk_id TEXT PRIMARY KEY,
    tenant_id TEXT NOT NULL,
    doc_id TEXT NOT NULL,
    section_path TEXT NOT NULL,
    page_num INT NOT NULL DEFAULT 1,
    allowed_principals TEXT[] NOT NULL,
    enriched_text TEXT NOT NULL,
    tsv tsvector GENERATED ALWAYS AS (to_tsvector('english', enriched_text)) STORED,
    embedding vector(384),
    injection_flagged BOOLEAN NOT NULL DEFAULT FALSE
);

-- GIN index for BM25/full-text SKU & error-code search + HNSW for semantic embeddings
CREATE INDEX IF NOT EXISTS idx_chunks_tenant_principals ON doc_chunks (tenant_id) INCLUDE (allowed_principals);
CREATE INDEX IF NOT EXISTS idx_chunks_tsv ON doc_chunks USING gin (tsv);
CREATE INDEX IF NOT EXISTS idx_chunks_vec ON doc_chunks USING hnsw (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS hitl_approvals (
    approval_id TEXT PRIMARY KEY,
    tenant_id TEXT NOT NULL,
    actor_sub TEXT NOT NULL,
    tool_name TEXT NOT NULL,
    payload_json JSONB NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('pending', 'approved', 'rejected', 'executed')),
    decided_by TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    decided_at TIMESTAMPTZ
);
