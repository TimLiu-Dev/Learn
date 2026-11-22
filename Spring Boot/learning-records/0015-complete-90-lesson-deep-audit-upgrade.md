# Learning Record 0015: Complete 90-Lesson End-to-End Senior Spring Boot Audit & Expansion

**Timestamp:** 2026-11-21  
**Goal:** Close every remaining gap across Dependency Injection, Redis internals, Kafka operations, PostgreSQL MVCC/locking, Grafana/PromQL visibility, System Design capacity math, and Spring AI MCP—bringing the full curriculum to **90 lessons (74 Core Stages 1–3 + 16 Optional Stage 4)**.

---

### Additions & Promotions
1. **Stage 1 • Phase 1:** `0086-advanced-di-qualifiers-scopes-and-beanpostprocessor.html` — `@Qualifier`, `@Primary`, Strategy `Map<String, T>` injection, `ObjectProvider<T>`, `@RequestScope`/`@Prototype` proxy traps, refactoring circular dependencies, and `BeanPostProcessor` vs `BeanFactoryPostProcessor`.
2. **Stage 2 • Phase 7:** `0087-redis-internals-eviction-serialization-lua-and-streams.html` — `allkeys-lru` vs `noeviction` isolation, banning `JdkSerializationRedisSerializer`, atomic `DefaultRedisScript` Lua scripts & pipelining, and Redis Streams (`XADD`/`XREADGROUP`) vs Pub/Sub.
3. **Stage 2 • Phase 9:** `0088-kafka-ops-offsets-rebalancing-lag-and-compaction.html` — `AckMode.MANUAL_IMMEDIATE`, `CooperativeStickyAssignor`, `max.poll.interval.ms` / `max.poll.records` rebalance loops, consumer lag monitoring, and log compaction (`cleanup.policy=compact` + tombstones).
4. **Stage 2 • Phase 12:** `0090-backend-system-design-playbook-and-sizing.html` — Back-of-the-envelope DAU/RPS/storage math, Little's Law concurrency sizing (`L = λW`), Kafka partition sizing, and hybrid fan-out tradeoffs.
5. **Stage 3 • Phase 14:** `0089-postgresql-mvcc-vacuum-lock-timeouts-and-deadlocks.html` — PostgreSQL MVCC dead tuples & `AUTOVACUUM` bloat, `SET lock_timeout`, `statement_timeout`, `CREATE INDEX CONCURRENTLY`, and live lock triage via `pg_stat_activity`.
6. **Stage 3 • Phase 15 (Promoted from Stage 4):** `0051-slis-slos-error-budgets-and-promql.html` — Moved Grafana RED dashboards, P99 histogram queries, and PromQL/SLOs into Stage 3 alongside `0050` and `0052` so Grafana/Prometheus/Loki/Tempo mastery is complete in Core Stages 1–3.
7. **Stage 4 • Phase 20 (Updated):** `0067-spring-ai-chatclient-rag-and-tool-calling.html` — Added 2027 Spring AI Model Context Protocol (`@McpTool` / `spring-ai-starter-mcp-server-webmvc`).
