# Teaching Notes & User Preferences

## Learner Profile & Elevated Target
- **Current Role:** Junior Android Developer (Kotlin).
- **Target Role:** **Senior Spring Boot Backend Developer (2027 Industry Standard)**.
- **Starting Point:** Strong in Kotlin/Android client concepts; starting from zero on server-side architecture.
- **Key Pedagogical Directives:**
  - **No abstract knowledge hubs:** Teach through sequential, highly practical HTML lessons with immediate active recall checks and live coding editors.
  - **Demystify buzzwords:** Ground Kafka, RabbitMQ, Redis, Docker, Kubernetes, and AWS in concrete architectural reality.
  - **Cloud standard:** **AWS** (S3, RDS, Secrets Manager, LocalStack, ECS/EKS concepts).
  - **Mandatory lesson format:**
    1. Plain-English conceptual mechanics & mobile analogies.
    2. Dual production code snippets (Java 21 primary + Kotlin 2.x migration).
    3. Multiple active recall quizzes (3 questions, equal word count options, per-option explanations).
    4. Interactive in-browser coding practice lab with automated multi-assertion validation.

## The Senior Backend Engineer Standard (What Makes a Senior in 2027)
1. **Architectural Autonomy:**
   - Designing for failure: Circuit Breakers (Resilience4j), Rate Limiting (Bucket4j/Redis), Bulkheading, Distributed Sagas.
   - Transactional Outbox Pattern for guaranteed at-least-once messaging without two-phase commit overhead.
   - Domain-Driven Design (DDD) & Modular Monoliths with Spring Modulith & ArchUnit.
2. **JVM & High-Performance Runtime:**
   - Deep memory management: ZGC Generational tuning, JFR profiling, JSON thread dumps via `jcmd`, detecting carrier thread pinning in Virtual Threads.
3. **Advanced Data & Caching:**
   - Dynamic datasource routing (read-replicas vs primary write DB).
   - Multi-tier caching (L1 in-memory Caffeine + L2 distributed Redis) with probabilistic early expiration.
4. **Streaming & Event Sourcing:**
   - Apache Kafka exactly-once semantics (EOS), consumer rebalance protocols, and contract governance with Schema Registry.
5. **Zero-Trust Security & Observability:**
   - Mutual TLS (mTLS), token rotation (RTR), distributed tracing correlation (W3C traceparent) across HTTP and Kafka boundaries.

---

## Complete Senior Curriculum (49 Lessons across 15 Architectural Phases)

### Phase 0: Absolute Foundations
- `0001-what-is-a-backend-and-how-servers-work.html`: Sockets, ports, Tomcat, 15-line bare-metal server, statelessness.
- `0002-http-protocol-status-codes-and-json.html`: Raw HTTP messages, GET vs POST DB semantics, status codes, Jackson ObjectMapper.

### Phase 1: Spring Framework Core
- `0003-ioc-container-and-bean-lifecycle.html`: IoC, ApplicationContext, constructor injection, Hilt comparison.
- `0004-configuration-properties-and-records.html`: Type-safe `@ConfigurationProperties`, Java 21 Records, `@Validated`.
- `0005-auto-configuration-and-conditional-beans.html`: `@ConditionalOnClass`, `@ConditionalOnMissingBean`, starter mechanics.

### Phase 2: RESTful Web Layer & Client Integration
- `0006-rest-controllers-and-routing.html`: `@RestController`, routing, ResponseEntity, Retrofit mirroring.
- `0007-dto-validation-and-custom-constraints.html`: "Never trust the client", Jakarta Validation, `@Valid`.
- `0008-global-exception-handling-problem-details.html`: `@RestControllerAdvice`, RFC 7807 `ProblemDetail`.
- `0009-outbound-http-clients-restclient-httpexchange.html`: Synchronous `RestClient` and declarative `@HttpExchange` interfaces.
- `0010-api-contracts-openapi-versioning.html`: OpenAPI 3 (SpringDoc), Swagger UI, URI and header versioning strategies.

### Phase 3: Relational Persistence, Flyway & Performance
- `0011-relational-modeling-postgresql-and-hikaricp.html`: Networked DB vs SQLite, connection pool starvation, pool sizing formula.
- `0012-indexing-and-query-optimization-explain-analyze.html`: B-Tree indexes, composite leftmost prefix rule, `EXPLAIN ANALYZE`.
- `0013-database-migrations-with-flyway.html`: Flyway migrations, versioning, immutable checksums.
- `0014-keyset-pagination-and-read-replica-routing.html`: Keyset cursor pagination (preventing `OFFSET` quadratic degradation) & dynamic read-replica routing.

### Phase 4: Spring Data JPA & Persistence
- `0015-jpa-entities-and-preventing-n-plus-one.html`: Entity mapping, `fetch = LAZY`, banning `data class`, `JOIN FETCH`.
- `0016-spring-data-repositories-and-projections.html`: `JpaRepository`, derived queries, constructor record projections.
- `0017-transaction-management-and-acid-boundaries.html`: ACID, AOP proxies, self-invocation trap, read-only optimization.

### Phase 5: High-Performance Distributed Caching
- `0018-redis-caching-and-spring-cache.html`: RAM vs Disk, `@Cacheable`, `@CacheEvict`, TTL, stampede prevention.
- `0019-redis-data-structures-and-distributed-locks.html`: Multi-instance JVM lock failure, Redisson `tryLock`, fencing tokens & Kleppmann critique.
- `0020-distributed-idempotency-with-redis.html`: Flaky mobile elevator retry trap, `Idempotency-Key`, atomic `SETNX`.
- `0021-multitier-caching-caffeine-and-redis.html`: L1 Caffeine + L2 Redis multi-tier caching with Pub/Sub invalidation.
- `0022-distributed-rate-limiting-redis-bucket4j.html`: Distributed Token Bucket algorithm with Redis and Bucket4j.

### Phase 6: Resilience & Fault Tolerance
- `0023-fault-tolerance-resilience4j-circuit-breaker.html`: Resilience4j Circuit Breakers, Bulkheads, Retries, and Fallback handlers.

### Phase 7: Event-Driven Architecture & Kafka
- `0024-message-brokers-rabbitmq-vs-kafka.html`: Point-to-point queues vs distributed commit log, smart broker vs smart consumer.
- `0025-spring-kafka-producers-consumers-partitions.html`: Partition key hashing, ordering guarantees, consumer groups, rebalancing.
- `0026-resilient-event-pipelines-transactional-outbox.html`: Dual-write trap, Transactional Outbox pattern, Debezium CDC.
- `0027-kafka-schema-registry-and-exactly-once.html`: Avro & Confluent Schema Registry contract governance and Kafka EOS.

### Phase 8: Spring Security 6 & Enterprise Identity
- `0028-spring-security-filter-chain-architecture.html`: Modern SecurityFilterChain, stateless REST, CSRF disabling.
- `0029-stateless-jwt-authentication-refresh-tokens.html`: OncePerRequestFilter, Bearer tokens, token rotation (RTR) in Redis.
- `0030-role-based-access-control-method-security.html`: `@EnableMethodSecurity`, `@PreAuthorize`, SpEL IDOR defense.
- `0031-oauth2-resource-server-and-mtls.html`: OAuth2 Resource Server with JWT validation and Mutual TLS (mTLS).

### Phase 9: High-Throughput Concurrency, Threading & JVM Diagnostics
- `0032-virtual-threads-loom-and-carrier-pinning.html`: Project Loom, carrier thread pinning, ReentrantLock vs synchronized, Semaphore bounds.
- `0033-concurrency-defense-optimistic-vs-pessimistic-locking.html`: `@Version` optimistic locking vs `SELECT FOR UPDATE` pessimistic locking.
- `0034-background-jobs-and-distributed-scheduling-shedlock.html`: Distributed cron execution across Kubernetes pods with ShedLock.
- `0035-jvm-diagnostics-profiling-and-thread-dumps.html`: Production diagnostics, JFR continuous recordings, JSON thread dumps with `jcmd`.

### Phase 10: Enterprise Architecture: Modular Monoliths & Distributed Sagas
- `0036-domain-driven-design-and-spring-modulith.html`: Modular monoliths, ArchUnit verification, `@ApplicationModuleListener` transactional outbox.
- `0037-distributed-sagas-orchestration-vs-choreography.html`: Distributed transactions, Orchestration vs Choreography, compensating transactions.

### Phase 11: Production Verification: Testing, Testcontainers & CI/CD
- `0038-unit-and-web-slice-testing.html`: Test pyramid, `@WebMvcTest` with Spring Boot 3.4+ `@MockitoBean`, MockMvc fluent assertions.
- `0039-integration-testing-testcontainers-service-connection.html`: Banning H2, real PostgreSQL in Docker, Spring Boot 3.1+ `@ServiceConnection`.
- `0040-automated-cicd-github-actions-docker-testcontainers.html`: Production GitHub Actions CI/CD pipeline, Docker socket mounts, Trivy scans.

### Phase 12: Production Observability: Metrics, Probes & Tracing
- `0041-spring-boot-actuator-health-probes-and-metrics.html`: Kubernetes liveness/readiness probes, custom HealthIndicators, Micrometer business metrics.
- `0042-distributed-tracing-micrometer-opentelemetry.html`: W3C `traceparent` header propagation, Slf4j MDC log correlation, OpenTelemetry spans.

### Phase 13: Cloud-Native Packaging, Containers & JVM Ergonomics
- `0043-docker-multi-stage-builds-and-distroless-containers.html`: Multi-stage Docker builds, Google Distroless non-root runtimes.
- `0044-cloud-native-buildpacks-and-jvm-memory-tuning.html`: Paketo `bootBuildImage`, container cgroups v2 memory ergonomics (`-XX:MaxRAMPercentage=75.0`), Generational ZGC.

### Phase 14: Cloud Infrastructure: Kubernetes & AWS Production Architecture
- `0045-kubernetes-pods-deployments-and-services.html`: Pods, Deployments (rolling updates), ClusterIP Services, CoreDNS discovery.
- `0046-kubernetes-probes-graceful-shutdown-and-secrets.html`: Zero-downtime rolling updates, `server.shutdown=graceful`, `preStop` hooks, External Secrets.
- `0047-aws-s3-presigned-urls-mobile-upload-pattern.html`: Direct-to-S3 uploads, AWS SDK v2 `S3Presigner`, SigV4 signatures.
- `0048-aws-rds-postgresql-secrets-manager-localstack.html`: AWS RDS Multi-AZ, RDS Proxy, AWS Secrets Manager rotation, LocalStack Testcontainers.

### Phase 15: Polyglot Mastery & Capstone Architecture
- `0049-migrating-spring-boot-to-kotlin-idioms-and-coroutines.html`: Kotlin migration playbook, `all-open`/`no-arg` compiler plugins, Coroutines vs Loom.
- `reference/capstone-project-spec.html`: **NexusCommerce** full enterprise architecture specification.
