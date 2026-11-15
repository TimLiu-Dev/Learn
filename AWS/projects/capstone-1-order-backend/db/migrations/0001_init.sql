-- 0001_init.sql
-- Core schema for Part 1 Capstone: Distributed Order & Ledger Backend
-- Enforces ACID invariants, optimistic locking, idempotency, transactional outbox,
-- worker deduplication, and double-entry ledger reconciliation.

BEGIN;

CREATE TABLE IF NOT EXISTS inventory (
  sku         TEXT PRIMARY KEY,
  name        TEXT NOT NULL,
  available   INTEGER NOT NULL CHECK (available >= 0),
  version     BIGINT NOT NULL DEFAULT 1 CHECK (version >= 1),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS orders (
  order_id    UUID PRIMARY KEY,
  sku         TEXT NOT NULL REFERENCES inventory(sku),
  qty         INTEGER NOT NULL CHECK (qty > 0),
  status      TEXT NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'FAILED_DLQ')),
  trace_id    TEXT NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_orders_sku_created_at
  ON orders (sku, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_orders_status_created_at
  ON orders (status, created_at DESC);

CREATE TABLE IF NOT EXISTS idempotency_keys (
  idempotency_key TEXT PRIMARY KEY,
  payload_hash    TEXT NOT NULL,
  status_code     INTEGER NOT NULL,
  order_id        UUID NULL REFERENCES orders(order_id),
  response_body   JSONB NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at      TIMESTAMPTZ NOT NULL DEFAULT (now() + interval '24 hours')
);

CREATE INDEX IF NOT EXISTS idx_idempotency_keys_expires_at
  ON idempotency_keys (expires_at);

CREATE TABLE IF NOT EXISTS outbox_events (
  event_id      UUID PRIMARY KEY,
  order_id      UUID NOT NULL REFERENCES orders(order_id),
  event_type    TEXT NOT NULL DEFAULT 'ORDER_CREATED',
  payload       JSONB NOT NULL,
  status        TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSED', 'DLQ')),
  attempts      INTEGER NOT NULL DEFAULT 0 CHECK (attempts >= 0),
  max_attempts  INTEGER NOT NULL DEFAULT 5 CHECK (max_attempts >= 1),
  next_run_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_error    TEXT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  processed_at  TIMESTAMPTZ NULL
);

-- Partial B-tree index supporting high-throughput SKIP LOCKED polling
CREATE INDEX IF NOT EXISTS idx_outbox_pending_next_run
  ON outbox_events (next_run_at ASC, created_at ASC)
  WHERE status = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_outbox_order_id
  ON outbox_events (order_id);

-- Partial B-tree index for DLQ operator triage
CREATE INDEX IF NOT EXISTS idx_outbox_dlq_created_at
  ON outbox_events (created_at DESC)
  WHERE status = 'DLQ';

-- Worker deduplication table for at-least-once delivery safety
CREATE TABLE IF NOT EXISTS processed_events (
  event_id      UUID PRIMARY KEY REFERENCES outbox_events(event_id),
  processed_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Downstream side-effect table for 1:1 order-to-ledger reconciliation
CREATE TABLE IF NOT EXISTS ledger_entries (
  entry_id    UUID PRIMARY KEY,
  order_id    UUID NOT NULL UNIQUE REFERENCES orders(order_id),
  event_id    UUID NOT NULL UNIQUE REFERENCES outbox_events(event_id),
  sku         TEXT NOT NULL,
  qty         INTEGER NOT NULL CHECK (qty > 0),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_ledger_entries_created_at
  ON ledger_entries (created_at DESC);

COMMIT;
