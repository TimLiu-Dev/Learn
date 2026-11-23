# Project 1A Starter Blueprint — PulseOrder Spring Boot (Part 1A Milestone)

Companion starter guide for [Lesson 0031: Project 1A — PulseOrder Spring Boot](../../lessons/0031-project-1a-senior-backend-suite.html) (Java 21+ & Kotlin, Spring Boot 3.4+/4.x).

## Target Module Layout

```text
pulseorder-backend/
  src/main/resources/db/migration/
    V1__init_orders_and_stock.sql
  src/test/
    arch/ArchitectureRulesTest.*        # ArchUnit: domain Spring-free, web !-> persistence
    domain/OrderPricingUnitTest.*       # Plain JUnit 5 (0ms Spring context) + Pitest >= 88%
    web/OrderControllerWebMvcTest.*     # @WebMvcTest + MockMvcTester + @MockitoBean/@MockkBean + jwt()
    client/PaymentClientRestTest.*      # @RestClientTest + MockRestServiceServer / WireMock
  src/integrationTest/
    ContainersConfig.*                  # Shared @TestConfiguration @ServiceConnection (Postgres + Redis + Kafka)
    persistence/OrderRepoPostgresTest.* # @DataJpaTest + Replace.NONE + Flyway + N+1 statement count
    events/KafkaOrderConsumerIT.*       # Awaitility + idempotency + poison-pill DLT
    concurrency/StockRaceConditionIT.*  # No @Transactional! CountDownLatch + @Version optimistic lock
```

## Verification Checklist Before Marking Part 1A Complete

1. **Zero `@DirtiesContext`:** All integration classes share `ContainersConfig` so Spring's TestContext cache stays warm.
2. **Real Postgres & Flyway:** `@AutoConfigureTestDatabase(replace = NONE)` + `@ServiceConnection` PostgreSQLContainer verifies constraints and Flyway SQL.
3. **Hibernate Traps Caught:** `em.flush(); em.clear();` before query assertions, and `Statistics.getPrepareStatementCount()` guards against N+1 regressions.
4. **Security, Kafka & Races:** 401/403 RBAC matrix via `jwt()`, Awaitility + DLT on poison messages, and `CountDownLatch` multi-threaded race confirming `@Version` blocks overselling.
