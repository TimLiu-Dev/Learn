# ADR-0001: Transactional Outbox, Idempotency Keys, and Optimistic Concurrency Control

- **Status:** Accepted
- **Date:** 2026-11-15
- **Scope:** Part 1 Capstone — Order API & Async Ledger Worker (`capstone-1-order-backend`)
- **Related Lessons:** Lesson 0007 (PostgreSQL Indexes & Pooling), Lesson 0008 (ACID & Optimistic Locking), Lesson 0010 (Backoff & Jitter), Lesson 0011 (Idempotency, Outbox & DLQ), Lesson 0014 (ADRs)

---

## 1. Context

Mobile checkout clients (e.g., Android `OkHttp` + `WorkManager`) experience frequent transient network drops, proxy timeouts, and automatic retries. Simultaneously, flash-sale bursts cause concurrent requests to contend for the same SKU inventory rows.

A naive implementation that:
1. Checks `SELECT available FROM inventory` in application memory and then issues `UPDATE`,
2. Commits the order to PostgreSQL and then publishes an async event to a separate broker (Redis/SQS/Kafka) outside the transaction, or
3. Treats `POST /orders` without client-supplied idempotency semantics

violates three core business invariants under failure and concurrency:
- **Dual-write split-brain:** A crash or network blip between `COMMIT` and broker publish silently orphans paid/reserved orders (zero downstream ledger/fulfillment event).
- **Double-charging / duplicate reservation:** A client timeout retry creates two separate orders and deducts inventory twice.
- **Overselling under `READ COMMITTED`:** Concurrent reads observe stale `available` stock and drive inventory negative.

## 2. Decision Drivers

1. **Zero lost events:** Every committed order must guarantee eventual downstream event emission.
2. **Strict idempotency:** Retrying `POST /orders` with the same `Idempotency-Key` and canonical payload must replay the original response without re-executing side effects; reusing a key with a modified payload must fail loudly (`422 IDEMPOTENCY_CONFLICT`).
3. **Zero overselling without coarse table locks:** Concurrent buyers must not push `inventory.available < 0`, while avoiding long-lived `SERIALIZABLE` retry storms or pessimistic locks across HTTP boundaries.
4. **Bounded failure isolation:** Poison-pill downstream events must not block healthy events in the queue.
5. **Cloud portability:** Must run identically on local PostgreSQL 16 + Docker Compose today and migrate cleanly to Amazon RDS/Aurora + SQS/EventBridge in Part 2.

## 3. Options Considered

### Option A: Direct Dual-Write (PostgreSQL `COMMIT` + Best-Effort Broker Publish)
- **Pros:** Low implementation complexity; no `outbox_events` table or poller.
- **Cons:** Unrecoverable split-brain window between DB commit and broker ack; broker outages directly fail checkout writes or lose events.
- **Verdict:** Rejected.

### Option B: Two-Phase Commit (2PC / XA Distributed Transactions)
- **Pros:** Cross-system atomicity in theory.
- **Cons:** High coordinator latency, lock-holding during network partitions, poor support across managed cloud queues/webhooks.
- **Verdict:** Rejected.

### Option C: Transactional Outbox + Advisory-Locked Idempotency + Optimistic Locking (`version`)
- **Pros:** Single local ACID transaction atomically persists `inventory` deduction, `orders` row, `outbox_events` row, and `idempotency_keys` snapshot. Concurrent workers drain `outbox_events` via `FOR UPDATE SKIP LOCKED` and deduplicate via `processed_events`.
- **Cons:** Introduces polling latency (e.g., 500 ms) unless replaced by CDC/Debezium later; requires periodic retention pruning of `idempotency_keys` and processed `outbox_events`.
- **Verdict:** **Accepted.**

## 4. Decision

We enforce correctness at the PostgreSQL transaction boundary and worker consumer boundary using four interlocking mechanisms:

1. **Advisory-Serialized Idempotency (`idempotency_keys`):**
   - `POST /orders` requires an `Idempotency-Key` header and computes a SHA-256 digest of the canonicalized JSON body (`{ qty, sku }`).
   - Inside the transaction, `SELECT pg_advisory_xact_lock(hashtext($1))` serializes concurrent retries carrying the same key without blocking unrelated keys.
   - Matching `(idempotency_key, payload_hash)` replays the stored `status_code` and `response_body` (`X-Idempotent-Replay: true`). Mismatched `payload_hash` returns `422 IDEMPOTENCY_CONFLICT`.

2. **Optimistic Locking + Conditional Stock Guard (`inventory.version` & `CHECK (available >= 0)`):**
   - The API reads `(available, version)` and executes:
     `UPDATE inventory SET available = available - $1, version = version + 1 WHERE sku = $2 AND version = $3 AND available >= $1`.
   - Zero updated rows due to concurrent `version` advancement triggers up to 3 bounded in-transaction re-reads before returning `409 OPTIMISTIC_LOCK_CONFLICT`. Insufficient stock deterministically records `409 INSUFFICIENT_STOCK` and rolls back reservation changes.

3. **Transactional Outbox (`outbox_events`) + Partial B-Tree Index:**
   - `orders` and `outbox_events` are inserted in the same `COMMIT`, carrying W3C `traceparent` / `traceId` metadata.
   - A partial B-tree index `ON outbox_events (next_run_at ASC, created_at ASC) WHERE status = 'PENDING'` keeps worker polling `O(log N_pending)` rather than scanning historical `PROCESSED` rows.

4. **Idempotent `SKIP LOCKED` Worker + Exponential Backoff with Full Jitter + DLQ:**
   - Workers claim one due event per short transaction with `FOR UPDATE SKIP LOCKED LIMIT 1` to avoid head-of-line blocking and cross-event rollback coupling.
   - Downstream delivery checks `processed_events` (`PRIMARY KEY (event_id)`) and records `ledger_entries` (`UNIQUE (order_id)`) atomically upon success, ensuring at-least-once delivery yields exactly-once ledger state.
   - Transient failures schedule `next_run_at = now() + random(0, min(maxMs, baseMs * 2^(attempts-1)))`. Events reaching `max_attempts` transition to `status = 'DLQ'` and mark the order `FAILED_DLQ` for operator triage.

## 5. Consequences

- **Positive:**
  - Provable conservation invariant: `initial_stock == remaining_stock + SUM(orders.qty)`.
  - Provable ledger invariant: `COUNT(orders WHERE status = 'CONFIRMED') == COUNT(ledger_entries) == COUNT(processed_events)`.
  - Zero dependency on external message brokers during the synchronous checkout critical path.
- **Tradeoffs & Mitigations:**
  - *Table growth:* `idempotency_keys` (`expires_at` index) and `outbox_events` (`status = 'PROCESSED'`) grow linearly with order volume. **Mitigation:** Schedule batched deletion/archival of expired `idempotency_keys` (`expires_at < now()`) and old `PROCESSED` outbox rows (e.g., `> 7 days`), or partition `outbox_events` by `created_at` in high-volume production.
  - *Polling overhead:* Polling every 500 ms creates steady idle `SELECT` load. **Mitigation:** Partial index keeps index-only/tiny scans sub-millisecond; in AWS (Part 2), an EventBridge Scheduler / RDS CDC / Debezium / DynamoDB Streams relay can replace polling without changing the core table contract.
  - *Connection exhaustion:* Bounded `pg.Pool` (`max: 20` on API, `max: 10` on Worker) caps backend connections ahead of RDS Proxy / PgBouncer adoption.

## 6. Verification Criteria

1. Pure domain invariants (`tests/verify-invariants.js`) pass under both Node.js and JavaScriptCore (`jsc`) with zero external DB.
2. Concurrent `POST /orders` bursts against a constrained SKU never produce `available < 0` and conserve stock 100%.
3. Duplicate `Idempotency-Key` retries return identical `orderId` and `201` status; altered payloads return `422`.
4. Poison events (`SKU-POISON-99` or `simulateFailure: true`) retry with jittered exponential backoff and settle in `DLQ` after `max_attempts` without stalling valid orders.
