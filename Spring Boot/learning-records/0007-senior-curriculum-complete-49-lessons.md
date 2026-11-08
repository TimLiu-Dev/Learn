# Learning Record 0007: Senior Spring Boot Curriculum (49 Lessons & Interactive Engine)

**Timestamp:** 2026-11-07  
**Goal:** Comprehensive senior upgrade before starting self-guided learning.

---

### 1. Curriculum Architecture Overhaul
The curriculum has been elevated from 36 introductory lessons to a full **49-Lesson Senior 2027 Curriculum** across 15 structured architectural phases:
- **Phase 0: Absolute Foundations (Lessons 0001–0002):** Process models, ports, sockets, HTTP/1.1 vs HTTP/2/3, Jackson streaming serialization.
- **Phase 1: Spring Framework Core (Lessons 0003–0005):** IoC, ApplicationContext, Java 21 Records `@ConfigurationProperties`, custom Auto-Configuration.
- **Phase 2: RESTful Web Layer & Client Integration (Lessons 0006–0010):** Retrofit mirroring, Jakarta validation, RFC 7807 ProblemDetails, RestClient / `@HttpExchange`, OpenAPI 3 contracts.
- **Phase 3: Relational Persistence, Flyway & Performance (Lessons 0011–0014):** HikariCP connection tuning, EXPLAIN ANALYZE index optimization, Flyway migrations, keyset cursor pagination with read replicas.
- **Phase 4: Spring Data JPA & Persistence (Lessons 0015–0017):** Hibernate internals, avoiding N+1 query storms with JOIN FETCH, Java 21 Record constructor projections, Spring transaction isolation boundaries.
- **Phase 5: High-Performance Distributed Caching (Lessons 0018–0022):** Redis caching, Redisson distributed locks with fencing tokens, distributed idempotency keys, multi-tier Caffeine L1 + Redis L2 caching, Bucket4j rate limiting.
- **Phase 6: Resilience & Fault Tolerance (Lesson 0023):** Resilience4j Circuit Breakers, Bulkheads, Retries, and Fallback handlers.
- **Phase 7: Event-Driven Architecture & Kafka (Lessons 0024–0027):** RabbitMQ vs Kafka, consumer groups, partitions, rebalance listeners, Transactional Outbox pattern with Debezium CDC, Confluent Schema Registry with Exactly-Once Semantics (EOS).
- **Phase 8: Spring Security 6 & Enterprise Identity (Lessons 0028–0031):** SecurityFilterChain architecture, stateless JWT with Refresh Token Rotation (RTR), Method Security with SpEL, OAuth2 Resource Server & mTLS.
- **Phase 9: High-Throughput Concurrency, Threading & JVM Diagnostics (Lessons 0032–0035):** Java 21 Virtual Threads (Loom), carrier pinning defense, Optimistic (`@Version`) vs Pessimistic (`SELECT FOR UPDATE`) locking, ShedLock distributed cron scheduling, JVM diagnostics (JFR, JSON thread dumps via `jcmd`).
- **Phase 10: Enterprise Architecture: Modular Monoliths & Distributed Sagas (Lessons 0036–0037):** Spring Modulith, ArchUnit boundary enforcement, transactional event outboxes, Distributed Sagas (Orchestration vs Choreography with compensating transactions).
- **Phase 11: Production Verification: Testing, Testcontainers & CI/CD (Lessons 0038–0040):** `@WebMvcTest` with `@MockitoBean`, Testcontainers with Spring Boot 3.1+ `@ServiceConnection`, automated GitHub Actions CI/CD.
- **Phase 12: Production Observability: Metrics, Probes & Tracing (Lessons 0041–0042):** Actuator custom HealthIndicators, Micrometer business metrics (Prometheus), distributed tracing with OpenTelemetry & W3C `traceparent` headers.
- **Phase 13: Cloud-Native Packaging, Containers & JVM Ergonomics (Lessons 0043–0044):** Multi-stage Docker with Google Distroless non-root runtimes, Paketo Cloud-Native Buildpacks, container cgroup memory ergonomics (`-XX:MaxRAMPercentage=75.0`), Generational ZGC.
- **Phase 14: Cloud Infrastructure: Kubernetes & AWS Production Architecture (Lessons 0045–0048):** Kubernetes Pods, Deployments & Services networking, zero-downtime rolling updates with `server.shutdown=graceful` & `preStop` hooks, AWS S3 Presigned URLs for mobile uploads, AWS RDS Multi-AZ with Secrets Manager.
- **Phase 15: Polyglot Mastery & Capstone Architecture (Lesson 0049 & Capstone):** Migrating Spring Boot from Java 21 to Kotlin 2.x, Kotlin compiler plugins (`all-open`, `no-arg`), coroutines vs virtual threads, NexusCommerce Capstone Specification.

---

### 2. Interactive In-Browser Learning Features
- **Dual Language Duality:** Every lesson features Java 21 and Kotlin 2.x code snippets with tab switching and syntax highlighting.
- **In-Browser Test Runner:** Zero-setup browser-native code editor with local execution simulation, multi-assertion regex/AST verification, and immediate terminal console output.
- **Progressive Hints (3 Tiers):** Tier 1 architectural nudge, Tier 2 syntax signature, Tier 3 concrete snippet.
- **Complete Reference Solutions:** Full production solutions in both Java 21 and Kotlin 2.x with architectural breakdowns.
- **Active Recall Code Quizzes:** 3 multiple-choice code quizzes per lesson (147 total quizzes) with balanced option lengths and comprehensive per-option explanations for both correct answers and distractors.
- **Real-Time Progress Tracking:** Course table of contents in `index.html` dynamically saves checkbox state to `localStorage` and renders a live completion progress bar.

---

### 3. Automated Quality Verification Results
- 49 of 49 HTML files generated and validated on disk.
- 0 broken links; deterministic bidirectional navigation links verified.
- 100% test pass rate across all 15 automated validation checks.
