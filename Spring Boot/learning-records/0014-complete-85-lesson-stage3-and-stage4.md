# Learning Record 0014: Complete 85-Lesson 4-Stage Curriculum (Stage 3 & Stage 4 Final Expansion)

**Timestamp:** 2026-11-21  
**Goal:** Add the remaining high-ROI production correctness, async context propagation, multi-tenancy/OpenFeature, NoSQL hands-on, Debezium CDC/Event Sourcing, and Spring Cloud Contract/ArchUnit lessons—bringing the complete curriculum to **85 lessons (68 Core Stages 1–3 + 17 Optional Stage 4)**.

---

### Stage 3 Additions (Core Production Engineering — Now 18 Lessons)
- `0080-money-timezones-and-pii-safety.html` (Phase 14): Financial precision (`BigDecimal` scale vs `compareTo`, `RoundingMode.HALF_EVEN`, integer minor units), unambiguous UTC `Instant` + PostgreSQL `TIMESTAMPTZ` vs `LocalDateTime`, and PII/credential log masking.
- `0081-spring-async-thread-pools-and-task-decorator.html` (Phase 15): Bounded `ThreadPoolTaskExecutor` sizing, `CallerRunsPolicy` backpressure, graceful task shutdown, and leak-free `TaskDecorator` for SLF4J `MDC` & `SecurityContext` propagation.

### Stage 4 Additions (Optional Deep-Dive Shelf — Now 17 Lessons)
- `0082-multi-tenancy-hibernate-rls-and-openfeature.html` (Phase 19): Hibernate 6 `@TenantId`, PostgreSQL Row-Level Security (`CREATE POLICY`), leak-free `ThreadLocal` cleanup, and vendor-neutral feature flags via OpenFeature.
- `0083-nosql-hands-on-spring-data-mongodb-and-dynamodb.html` (Phase 19): Access-pattern-driven NoSQL modeling, Spring Data MongoDB `@Document`, TTL `@Indexed(expireAfter = ...)` & `@Version` optimistic locks, and DynamoDB `Query` vs `Scan` defense.
- `0084-change-data-capture-debezium-and-event-sourcing.html` (Phase 19): Debezium PostgreSQL WAL (`pgoutput`) + Outbox `EventRouter` SMT, replication slot disk safety, and Event Sourcing with Snapshots.
- `0085-contract-testing-and-archunit-guardrails.html` (Phase 20): Consumer-Driven Contract Testing (`Spring Cloud Contract` + `@AutoConfigureStubRunner`) and repo-wide ArchUnit bytecode rules banning `@Autowired` field injection and controller-to-repository layer skips.
