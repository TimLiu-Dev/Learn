# Mission: Senior Spring Boot Backend Developer (2027)

## Why
Transition from a junior Android Kotlin developer with zero backend experience into a senior-caliber, autonomous backend software engineer by 2027, capable of designing, architecting, optimizing, securing, and operating resilient high-scale distributed systems with modern Spring Boot (primarily Java 21+, with full Kotlin fluency).

## Success looks like
- Lead backend system design: Architect clean Domain-Driven Designs (DDD), Modular Monoliths, and Event-Driven Microservices with clear architectural boundaries and Architectural Decision Records (ADRs).
- Master server concurrency & JVM internals: Harness Java 21 Virtual Threads (Loom), prevent carrier thread pinning, profile JVM heap/thread dumps, and tune modern garbage collectors (Generational ZGC / Shenandoah).
- Architect resilient distributed systems: Implement Circuit Breakers (Resilience4j), Rate Limiting (Token Bucket with Redis), Distributed Sagas, the Transactional Outbox pattern, and bulkheading.
- High-scale database & storage engineering: Optimize PostgreSQL query plans (`EXPLAIN ANALYZE`), configure dynamic read-replica routing (`AbstractRoutingDataSource`), handle partitioning, tune HikariCP pools, and manage multi-tier caching (L1 Caffeine in-memory + L2 Redis distributed).
- Event-driven streaming with Apache Kafka: Design robust pub/sub topologies, manage partition rebalancing, enforce Exactly-Once Semantics (EOS), and maintain contract schemas with Schema Registry.
- Zero-trust enterprise security: Architect Spring Security 6 filter chains, Mutual TLS (mTLS), stateless JWT token rotation, OAuth2 token exchange, and fine-grained Role-Based Access Control (RBAC).
- Comprehensive testing & automated CI/CD: Author isolated unit tests, slice tests, WireMock contract tests, HMAC-SHA256 webhook checks, and production-identical Testcontainers suites in GitHub Actions.
- Stage 1 (Mid-Level Core — 23 Lessons → Project 1): Master modern Java 21/25 for Kotlin devs, Gradle/Maven BOMs & layered Fat JARs, Spring Core/Profiles/AOP, REST/Validation/ProblemDetail/OpenAPI, PostgreSQL/HikariCP/Indexes/Flyway/Keyset Pagination, JPA N+1/Projections/Specifications/Auditing/`@SoftDelete`/Transactions, and `@WebMvcTest` + Testcontainers, culminating in **Project 1 (Core Order & Catalog API)**.
- Stage 2 (Senior Backend Core — 27 Lessons → Project 2): Master edge reverse proxies (`X-Forwarded-*`)/CORS/`ETag`, SSE/WebSockets + Redis fan-out, CAP/PACELC/Partitioning/Sharding, Redis caching/locks/idempotency/rate-limiting, Resilience4j, Kafka + Transactional Outbox + Schema Registry/EOS, Spring Security 6/JWT/RTR/RBAC/mTLS/Argon2id/OWASP API Top 10, Virtual Threads/Locking/ShedLock/JFR, DDD/Spring Modulith/Sagas, Webhooks/WireMock, Spring Boot 4 / Framework 7 + Java 25 (`ScopedValue`, `ApiVersionConfigurer`, `@ImportHttpServices`), and Kotlin Coroutines + `MockK`, culminating in **Project 2 (NexusCommerce Core Engine)**.
- Stage 3 ("Just-Enough" Production Engineering — 18 Lessons): Production SQL mastery & fluent `JdbcClient` (CTEs, Window Functions, atomic `UPSERT`, `JSONB`, partial indexes), Linux & container diagnostics (`SIGTERM` 143 vs 137, `ulimit -n`, `ss`/`lsof`, `jq`), financial/temporal/PII correctness (`BigDecimal`, UTC `Instant`, log masking), backend networking essentials (DNS/JVM cache TTL, TCP Keep-Alive pools, timeouts, TLS 1.3 & Spring SSL Bundles, L4 vs L7), Actuator/OTel/ECS JSON logs/cardinality guards/Boot 4 `@Observed`, `k6` load testing & `StreamingResponseBody`, bounded `@Async` pools & `TaskDecorator`, GitHub Actions CI, multi-stage Distroless Docker, JVM container memory (`-XX:MaxRAMPercentage` + ZGC), K8s Pods/Deployments/Services literacy, graceful shutdown/`preStop`, Expand-and-Contract Flyway migrations, AWS S3 presigned URLs, and RDS/Secrets Manager/LocalStack.
- Stage 4 (Optional On-Demand Deep-Dive Shelf — 17 Lessons): PromQL SLO burn-rate alerts, K8s CFS CPU throttling/HPA/PDB, Helm/Kustomize/Cosign/SBOM, AWS VPC/IAM Pod Identity, SQS/SNS vs Kafka, Terraform IaC, gRPC/GraphQL, Postgres FTS/pgvector/Elasticsearch/CQRS, Multi-Tenancy (`@TenantId` & Postgres RLS) + OpenFeature, Spring Data MongoDB & AWS DynamoDB, Debezium WAL CDC & Event Sourcing, GraalVM Native vs AppCDS, Spring AI, Spring Data JDBC/jOOQ, Spring Cloud Gateway, Spring Batch, and Spring Cloud Contract + ArchUnit Guardrails.
- Deliver progressive portfolio milestones: **Project 1 (Mid-Level Order & Catalog API)**, **Project 2 ("NexusCommerce: Distributed Event-Driven Order & Ledger Engine")**, and optional **Stage 4 Platform Extension ("NexusCommerce Production Platform")**.

## Constraints
- Learner is currently a junior Android developer fluent in Kotlin; bridge client-side mental models (Retrofit, Room, Hilt, Coroutines) to accelerate backend mastery.
- Every lesson must include dual Java 21 & Kotlin code examples, multiple active recall quizzes, and interactive in-browser coding practice with automated validation.
- Ground all theory in real-world failure scenarios, profiling drills, and architectural tradeoffs.

## Out of scope
- Complex reactive streaming architectures (WebFlux / Project Reactor) — superseded in 2027 by Java 21 Virtual Threads (Loom) for blocking-style simplicity with high scalability.
- Bare-metal cluster networking / kernel sysadmin (e.g. low-level Kubernetes CNI setup, deep Terraform VPC routing) — focus strictly on developer-facing cloud architecture ("You Build It, You Run It").
- Legacy Spring XML configuration or pre-Spring Boot 3 / pre-Java 17 patterns.
- Client-side frontend frameworks (React, Angular, Vue) and Android UI code.
