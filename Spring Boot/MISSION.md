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
- Comprehensive testing & automated CI/CD: Author isolated unit tests, slice tests, and production-identical integration test suites using Testcontainers (real PostgreSQL, Kafka, Redis) running in GitHub Actions.
- Cloud-native essentials (Part 1 — 80/20): Configure GitHub Actions CI with Testcontainers, Actuator health probes, OpenTelemetry `traceparent` tracing, multi-stage Distroless Docker images, container JVM memory ergonomics (`-XX:MaxRAMPercentage`), graceful shutdown, and AWS S3 presigned URLs.
- Core edge, real-time, database scale & security hardening (Part 1 Phase 15B): Master reverse proxy headers (`X-Forwarded-*`), strict CORS, HTTP `ETag`/`304` caching, SSE vs. WebSockets with Redis Pub/Sub multi-pod fan-out, CAP/PACELC, table partitioning vs. sharding, NoSQL tradeoffs, Argon2id/BCrypt password hashing, OWASP API Top 10 (BOLA, Mass Assignment, SSRF), Spring Profiles (`@Profile`), and custom AOP `@Aspect` interceptors.
- Professional Cloud, DevOps, Observability & Modern Spring Ecosystem expansion (Part 2 — 20% Specialist Track): Master structured JSON logging & high-cardinality metric defense, SLIs/SLOs/PromQL, Boot 4 native OTel + Grafana LGTM, Kubernetes networking/HPA/CPU-throttling/PDBs, expand-and-contract zero-downtime releases, Helm/Kustomize & SBOM supply-chain security, AWS VPC/IAM Pod Identity, RDS Multi-AZ/Proxy/Secrets Manager, SQS/SNS vs Kafka, Terraform IaC, gRPC/Protobuf vs. GraphQL (`@BatchMapping`), Search Engines (Postgres FTS/pgvector vs. Elasticsearch) + CQRS, GraalVM Native vs. AppCDS/AOT, Spring AI (`ChatClient`, RAG, `@Tool`), Spring Data JDBC/jOOQ, Spring Cloud Gateway, and Spring Batch.
- Deliver two undeniable portfolio capstones: Part 1 ("NexusCommerce: Distributed Event-Driven Order & Ledger Engine") and Part 2 ("NexusCommerce Production Platform: Cloud, DevOps & LGTM Observability").

## Constraints
- Learner is currently a junior Android developer fluent in Kotlin; bridge client-side mental models (Retrofit, Room, Hilt, Coroutines) to accelerate backend mastery.
- Every lesson must include dual Java 21 & Kotlin code examples, multiple active recall quizzes, and interactive in-browser coding practice with automated validation.
- Ground all theory in real-world failure scenarios, profiling drills, and architectural tradeoffs.

## Out of scope
- Complex reactive streaming architectures (WebFlux / Project Reactor) — superseded in 2027 by Java 21 Virtual Threads (Loom) for blocking-style simplicity with high scalability.
- Bare-metal cluster networking / kernel sysadmin (e.g. low-level Kubernetes CNI setup, deep Terraform VPC routing) — focus strictly on developer-facing cloud architecture ("You Build It, You Run It").
- Legacy Spring XML configuration or pre-Spring Boot 3 / pre-Java 17 patterns.
- Client-side frontend frameworks (React, Angular, Vue) and Android UI code.
