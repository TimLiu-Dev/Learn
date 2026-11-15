# Part 1 Capstone: Distributed Order & Ledger Backend

A cloud-portable, production-hardened order intake and asynchronous ledger worker built with zero-framework Node.js (`node:http`), PostgreSQL 16, Redis 7, and Docker Compose. It synthesizes the systems engineering foundations from **Lessons 0001–0015** ahead of AWS deployment in Part 2.

---

## 1. Architecture Overview

```
                     W3C traceparent + Idempotency-Key
  [Client / curl] ──────────────────────────────────────► [Order API (src/server.js)]
                                                             │          │
                      Cache-aside (TTL + Jitter)             │          │ Single ACID TX:
                      & post-commit invalidation             ▼          │ 1. pg_advisory_xact_lock
                                                        [Redis 7]       │ 2. idempotency_keys check
                                                                        │ 3. inventory optimistic lock
                                                                        │    (version + available >= qty)
                                                                        │ 4. orders + outbox_events insert
                                                                        ▼
                                                                [PostgreSQL 16]
                                                                        │
                                             SKIP LOCKED poll           │
                                             (partial B-tree index)     ▼
                                                               [Async Worker (src/worker.js)]
                                                                 │                  │
                                               Idempotent commit │                  │ attempts >= max
                                               (processed_events)▼                  ▼
                                                         [ledger_entries]         [DLQ]
```

### Core Guarantees (System Invariants)
1. **No Dual-Write Split-Brain:** `orders` and `outbox_events` are written in the same PostgreSQL `COMMIT`.
2. **Strict Idempotency:** Concurrent retries with the same `Idempotency-Key` serialize on `pg_advisory_xact_lock(hashtext(key))`. Identical canonical payloads replay the original response (`X-Idempotent-Replay: true`); modified payloads return `422 IDEMPOTENCY_CONFLICT`.
3. **Zero Overselling:** Inventory updates use optimistic locking (`WHERE sku = $2 AND version = $3 AND available >= $1`) backed by `CHECK (available >= 0)`.
4. **At-Least-Once Worker Deduplication & Bounded DLQ:** Workers lease rows with `FOR UPDATE SKIP LOCKED`, deduplicate via `processed_events`, retry transient errors using exponential backoff with full jitter, and quarantine poison messages in `DLQ` after `max_attempts`.
5. **Graceful Drain & Timeout Alignment:** `keepAliveTimeout = 65000` and `headersTimeout = 66000` outlast a 60s upstream proxy/ALB idle timeout; `SIGTERM` fails `/readyz`, closes idle sockets, and drains in-flight requests within `SHUTDOWN_GRACE_MS`.

---

## 2. Repository Layout

- [docker-compose.yml](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/docker-compose.yml) — PostgreSQL 16, Redis 7, `api`, and `worker` services with healthchecks and 30s stop grace periods.
- [Dockerfile](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/Dockerfile) — Multi-stage non-root Alpine build with built-in `HEALTHCHECK`.
- [db/migrations/0001_init.sql](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/db/migrations/0001_init.sql) — Schema for `inventory`, `orders`, `idempotency_keys`, `outbox_events`, `processed_events`, and `ledger_entries` with B-tree and partial indexes.
- [db/migrations/0002_seed.sql](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/db/migrations/0002_seed.sql) — Idempotent seed SKUs (`SKU-KB-01`, `SKU-MS-02`, `SKU-POISON-99`).
- [src/domain.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/domain.js) — Pure dependency-free domain rules shared by runtime services and tests.
- [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) — Express-free `node:http` API server.
- [src/worker.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/worker.js) — Outbox poller, idempotent consumer, backoff + jitter scheduler, and DLQ router.
- [docs/adr/0001-transactional-outbox-and-idempotency.md](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/docs/adr/0001-transactional-outbox-and-idempotency.md) — Senior Architecture Decision Record.
- [tests/verify-invariants.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/tests/verify-invariants.js) — Zero-DB domain invariant test suite runnable with `node` or macOS `jsc`.

---

## 3. Quickstart

### Run the Full Stack (Docker Compose)

```bash
cd /Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend
docker compose up --build -d
docker compose ps
```

Verify all four services (`postgres`, `redis`, `api`, `worker`) report `healthy`.

### Run Instant Invariant Tests (No Docker / DB Required)

```bash
# With Node.js
node tests/verify-invariants.js

# Or with macOS JavaScriptCore (jsc)
/System/Library/Frameworks/JavaScriptCore.framework/Versions/Current/Helpers/jsc tests/verify-invariants.js
```

---

## 4. Verification Walkthrough (`curl` + SQL Reconciliation)

1. **Health & Readiness Probes**
   ```bash
   curl -i http://localhost:3000/healthz
   curl -i http://localhost:3000/readyz
   ```

2. **Cache-Aside Inventory Read (MISS then HIT)**
   ```bash
   curl -s http://localhost:3000/inventory/SKU-KB-01
   curl -s http://localhost:3000/inventory/SKU-KB-01
   ```

3. **Create an Order (Idempotent Write + Outbox Emit)**
   ```bash
   curl -i -X POST http://localhost:3000/orders \
     -H "Content-Type: application/json" \
     -H "Idempotency-Key: order-req-001" \
     -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01" \
     -d '{"sku":"SKU-KB-01","qty":2}'
   ```
   Copy the returned `orderId` and inspect its transition to `CONFIRMED` after the worker polls (~500ms):
   ```bash
   curl -s http://localhost:3000/orders/<ORDER_ID>
   ```

4. **Verify Idempotent Replay (No Double Deduction)**
   ```bash
   curl -i -X POST http://localhost:3000/orders \
     -H "Content-Type: application/json" \
     -H "Idempotency-Key: order-req-001" \
     -d '{"qty":2,"sku":"SKU-KB-01"}'
   ```
   Expect `201 Created`, header `x-idempotent-replay: true`, and identical `orderId` & `remainingStock`.

5. **Verify Payload Conflict Detection (`422 IDEMPOTENCY_CONFLICT`)**
   ```bash
   curl -i -X POST http://localhost:3000/orders \
     -H "Content-Type: application/json" \
     -H "Idempotency-Key: order-req-001" \
     -d '{"sku":"SKU-KB-01","qty":5}'
   ```

6. **Verify Insufficient Stock Guard (`409 INSUFFICIENT_STOCK`)**
   ```bash
   curl -i -X POST http://localhost:3000/orders \
     -H "Content-Type: application/json" \
     -H "Idempotency-Key: order-req-oversell" \
     -d '{"sku":"SKU-KB-01","qty":9999}'
   ```

7. **Verify Exponential Backoff + DLQ Routing**
   ```bash
   curl -i -X POST http://localhost:3000/orders \
     -H "Content-Type: application/json" \
     -H "Idempotency-Key: order-req-poison" \
     -d '{"sku":"SKU-POISON-99","qty":1}'
   ```
   Watch worker logs (`docker compose logs -f worker`) as it retries 5 times with jittered backoff and marks the event `DLQ` and the order `FAILED_DLQ`.

8. **Run End-to-End Conservation & Ledger Reconciliation Query**
   ```bash
   docker compose exec postgres psql -U postgres -d orders -c "
   SELECT
     (SELECT COUNT(*) FROM orders WHERE status = 'CONFIRMED') AS confirmed_orders,
     (SELECT COUNT(*) FROM ledger_entries) AS ledger_entries,
     (SELECT COUNT(*) FROM processed_events) AS processed_events,
     (SELECT COUNT(*) FROM outbox_events WHERE status = 'DLQ') AS dlq_events;
   "
   ```
   `confirmed_orders`, `ledger_entries`, and `processed_events` must always be equal.

9. **Tear Down**
   ```bash
   docker compose down -v
   ```

---

## 5. Mapping to Lessons 0001–0015

| Lesson | Concept | Where Implemented in This Capstone |
| :--- | :--- | :--- |
| **0001** Request Lifecycle | Keep-alive timeout > proxy timeout (avoid 502 race) | `server.keepAliveTimeout = 65000`, `server.headersTimeout = 66000` in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) |
| **0002** CIDR, Routing & Ports | Explicit service ports & container bridge isolation | Service DNS (`postgres`, `redis`) & port bindings in [docker-compose.yml](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/docker-compose.yml) |
| **0003** Linux Processes & Signals | Graceful `SIGTERM` socket draining inside stop grace window | `SIGTERM`/`SIGINT` handlers in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) and [src/worker.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/worker.js); `stop_grace_period: 30s` |
| **0004** API Design | Deterministic status codes (`201`, `400`, `404`, `409`, `422`, `503`) | Request validation & JSON error contracts in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) |
| **0005** Auth & Trust Boundaries | Non-root execution & strict input bounds | `USER appuser` in [Dockerfile](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/Dockerfile); body size cap & schema validation in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) |
| **0006** Caching Patterns | Cache-aside with TTL + jitter & post-commit invalidation | `GET /inventory/:sku` (`30 + rand(0,6)s` TTL) & `redis.del(...)` on order commit in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) |
| **0007** PostgreSQL Internals | B-tree / partial indexes & bounded connection pooling | `idx_outbox_pending_next_run` (`WHERE status = 'PENDING'`) in [db/migrations/0001_init.sql](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/db/migrations/0001_init.sql); `PG_POOL_MAX` in [src/db.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/db.js) |
| **0008** ACID & Optimistic Locking | Single TX boundary, `version` check & `CHECK (available >= 0)` | Optimistic update loop & advisory key lock in [src/server.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/server.js) and [src/domain.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/domain.js) |
| **0009** SQL vs NoSQL Modeling | Relational ACID core + Redis KV ephemeral cache | Normalized relational tables in Postgres; key-value TTL cache in Redis |
| **0010** Timeouts, Backoff & Jitter | Full-jitter exponential backoff & bounded DB/HTTP timeouts | `computeBackoffWithJitterMs` in [src/domain.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/domain.js); `requestTimeout` & `connectionTimeoutMillis` |
| **0011** Queues, Outbox & DLQ | Transactional Outbox, `SKIP LOCKED`, `processed_events`, DLQ | `outbox_events` + `processed_events` in [src/worker.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/worker.js) |
| **0012** Docker Multi-Stage & Health | Multi-stage Alpine, non-root `USER`, `/healthz` vs `/readyz` | [Dockerfile](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/Dockerfile) and [docker-compose.yml](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/docker-compose.yml) |
| **0013** Observability | Structured JSON logs & W3C `traceparent` / `traceId` propagation | `parseOrCreateTraceContext` in [src/domain.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/domain.js); [src/logger.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/src/logger.js) |
| **0014** Testing & ADRs | Zero-DB invariant suite & Architecture Decision Record | [tests/verify-invariants.js](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/tests/verify-invariants.js) & [docs/adr/0001-transactional-outbox-and-idempotency.md](file:///Users/tl/Documents/Learns/AWS/projects/capstone-1-order-backend/docs/adr/0001-transactional-outbox-and-idempotency.md) |
| **0015** Part 1 Capstone Synthesis | End-to-end invariant conservation from HTTP to Ledger | Complete runnable stack & `verifyOrderPipeline` reference verifier |
