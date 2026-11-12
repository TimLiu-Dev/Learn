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

## Complete Senior Curriculum — Two-Part 80/20 Structure (64 Lessons across 19 Phases)

### PART 1: Core Senior Backend Engineer (51 Lessons • Phases 0–15B)
*80/20 high-ROI core: untouched backend foundations (`0001`–`0040` + `0049`), developer-facing observability/container/S3 essentials (`0041`–`0044`, `0046`, `0047`), and core edge/realtime/scaling/security hardening (`0059`–`0062`).*

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

### Phase 12: Core Observability Essentials (Part 1 — 80/20)
- `0041-spring-boot-actuator-health-probes-and-metrics.html`: Kubernetes liveness/readiness probes, custom HealthIndicators, Micrometer business metrics.
- `0042-distributed-tracing-micrometer-opentelemetry.html`: W3C `traceparent` header propagation, Slf4j MDC log correlation, OpenTelemetry spans.

### Phase 13: Container & Production Lifecycle Essentials (Part 1 — 80/20)
- `0043-docker-multi-stage-builds-and-distroless-containers.html`: Multi-stage Docker builds, Google Distroless non-root runtimes.
- `0044-cloud-native-buildpacks-and-jvm-memory-tuning.html`: Paketo `bootBuildImage`, container cgroups v2 memory ergonomics (`-XX:MaxRAMPercentage=75.0`), Generational ZGC.
- `0046-kubernetes-probes-graceful-shutdown-and-secrets.html`: Zero-downtime rolling updates, `server.shutdown=graceful`, `preStop` hooks, External Secrets concept.

### Phase 14: Essential Cloud Integration (Part 1 — 80/20)
- `0047-aws-s3-presigned-urls-mobile-upload-pattern.html`: Direct-to-S3 uploads, AWS SDK v2 `S3Presigner`, SigV4 signatures.

### Phase 15: Polyglot Mastery & Part 1 Capstone
- `0049-migrating-spring-boot-to-kotlin-idioms-and-coroutines.html`: Kotlin migration playbook, `all-open`/`no-arg` compiler plugins, Coroutines vs Loom.

### Phase 15B: Core Edge, Real-Time, Data Scale & Security Hardening (Part 1 — roadmap.sh/backend Gaps)
- `0059-edge-http-reverse-proxies-cors-and-etag-caching.html`: Reverse proxies (`X-Forwarded-*` / `server.forward-headers-strategy`), strict CORS, conditional `ETag` + `304 Not Modified`.
- `0060-realtime-backends-sse-websockets-and-redis-fanout.html`: SSE (`SseEmitter`) vs. WebSockets, leak-free cleanup callbacks, Redis Pub/Sub multi-pod fan-out.
- `0061-database-scaling-cap-sharding-and-nosql-tradeoffs.html`: CAP/PACELC theorem, PostgreSQL range partitioning vs. horizontal sharding, consistent hashing, NoSQL matrix.
- `0062-security-hardening-argon2-bcrypt-and-owasp-api-top-10.html`: Memory-hard `Argon2id`/`BCrypt`, BOLA/IDOR, banning Mass Assignment, and SSRF defense.
- `reference/capstone-project-spec.html`: **NexusCommerce** Part 1 core backend architecture specification.

---

### PART 2: Professional Cloud, DevOps & Observability Track (13 Lessons • Phases 16–19)
*20% deep-dive specialization for Staff/Platform autonomy (`0045` & `0048` moved here from Part 1 + 11 new lessons `0050`–`0058`, `0063`–`0064` + Part 2 Capstone).*

### Phase 16: Track 2A — Professional Observability
- `0050-structured-json-logging-and-cardinality-traps.html`: Spring Boot 3.4+ structured JSON logging (ECS/Logstash), MDC fields, Micrometer high-cardinality tag bombs, and trace exemplars.
- `0051-slis-slos-error-budgets-and-promql.html`: SLIs, SLOs, error budgets, `serviceLevelObjectives` histograms (P95/P99 vs averages), RED/USE dashboards, PromQL burn-rate alerts.
- `0052-boot4-native-opentelemetry-and-lgtm-stack.html`: Spring Boot 4.0 `spring-boot-starter-opentelemetry`, `@Observed`, tail-based sampling, and Docker Compose Grafana LGTM stack.

### Phase 17: Track 2B — Professional DevOps & Kubernetes Operations
- `0045-kubernetes-pods-deployments-and-services.html`: *(Moved from Part 1)* Pods, Deployments (rolling updates), ClusterIP Services, CoreDNS discovery, ConfigMaps.
- `0053-kubernetes-cpu-throttling-hpa-and-pdbs.html`: `requests` vs `limits` (JVM CFS CPU throttling spikes), Horizontal Pod Autoscaler (HPA), PodDisruptionBudgets (PDB).
- `0054-zero-downtime-releases-expand-contract-and-canary.html`: Expand-and-Contract Flyway DB migrations, Canary vs Blue-Green deployments, Feature Flags.
- `0055-helm-kustomize-and-supply-chain-security.html`: Helm/Kustomize parameterization across environments, CycloneDX SBOMs, Cosign image signing & Trivy gates.

### Phase 18: Track 2C — Professional AWS Cloud Architecture
- `0048-aws-rds-postgresql-secrets-manager-localstack.html`: *(Moved from Part 1)* AWS RDS Multi-AZ, RDS Proxy, Secrets Manager rotation, IAM DB Auth, LocalStack Testcontainers.
- `0056-aws-vpc-networking-and-iam-pod-identity.html`: VPC public/private subnets, NAT Gateways, Security Groups, IAM least privilege, EKS Pod Identity / IRSA.
- `0057-aws-managed-messaging-and-compute-tradeoffs.html`: AWS SQS + SNS + EventBridge vs Kafka/MSK tradeoffs, Spring Cloud AWS SQS + DLQs, ECS Fargate vs EKS vs App Runner.
- `0058-infrastructure-as-code-terraform-for-spring-boot.html`: Declarative Terraform/OpenTofu for RDS, S3, SQS, and IAM roles via Git PRs.

### Phase 19: Track 2D — Advanced Distributed APIs & Search Architecture & Part 2 Capstone
- `0063-grpc-protobuf-vs-rest-and-graphql-tradeoffs.html`: gRPC/Protobuf binary contracts & HTTP/2 load-balancing pitfalls vs. REST, plus GraphQL `@BatchMapping` N+1 defense.
- `0064-search-engines-postgres-fts-elasticsearch-and-cqrs.html`: Inverted indexes (`GIN(tsvector)` / `pgvector` vs. Elasticsearch/OpenSearch) and CQRS read-model sync via Kafka/Outbox.
- `reference/pro-cloud-capstone-spec.html`: **NexusCommerce Production Platform** Part 2 Cloud/DevOps/LGTM Capstone specification.
- `reference/spring-boot-4-java-25-delta.html`: **2027 Delta Guide** (Spring Boot 4.0 + Spring Framework 7 + Java 25 LTS).
