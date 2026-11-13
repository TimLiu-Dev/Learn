# Master Curriculum Plan: Java 25 LTS & Spring Boot 4.0 (81 Lessons)

This workspace transitions an Android developer who can read Kotlin into writing, debugging, and architecting production backend Java systems for 2027. Core Java 25, standard utilities, JVM internals, and Spring Boot 4.0 + Kafka form the **Main Track**; DSA Interview Patterns (`0041`–`0053`) and Spark/Flink Big Data (`0071`–`0073`) live in **Demoted Optional Tracks** at the end so they never block backend mastery.

## Recommended Study Sequence

1. **Main Track — Phase 1: Core Java 25 LTS, Kotlin Bridge & Standard Utilities (47 entries: `0001`–`0040`, `0074`–`0080`)**
   - `0001`: [Master Plan & Curriculum Hub](lessons/0001-master-plan-and-curriculum.html) ([Reference](reference/0001-master-plan-and-legend.html))
   - `0002`: [The Java Execution Mental Model & Toolchain](lessons/0002-the-java-execution-mental-model.html) ([Reference](reference/0002-jvm-execution-and-toolchain.html))
   - `0074`: [Kotlin-to-Java 25 Mental-Model Bridge](lessons/0074-kotlin-to-java-mental-model-bridge.html) ([Reference](reference/0074-kotlin-to-java-bridge.html)) **NEW**
   - `0003`: [Primitive Data Types & Memory](lessons/0003-primitive-data-types-and-memory.html) ([Reference](reference/0003-primitive-types-and-conversions.html))
   - `0004`: [Control Flow & Modern Switch](lessons/0004-control-flow-and-modern-switch.html) ([Reference](reference/0004-control-flow-and-switch.html))
   - `0005`: [Arrays & Command-Line Args](lessons/0005-arrays-and-command-line-args.html) ([Reference](reference/0005-arrays-and-utilities.html))
   - `0006`: [Classes, Heap Allocation & Lifecycle](lessons/0006-classes-heap-allocation-and-lifecycle.html) ([Reference](reference/0006-classes-and-lifecycle.html))
   - `0075`: [Packages, `package-private`, Inner Classes & Flexible Constructors (JEP 513)](lessons/0075-packages-access-inner-classes-and-initializers.html) ([Reference](reference/0075-packages-inner-classes-and-jep513.html)) **NEW**
   - `0007`: [Pass-by-Value Semantics](lessons/0007-pass-by-value-semantics.html) ([Reference](reference/0007-pass-by-value-semantics.html))
   - `0008`: [Encapsulation & Defensive Copying](lessons/0008-encapsulation-and-defensive-copying.html) ([Reference](reference/0008-encapsulation-and-immutability.html))
   - `0009`: [Inheritance, Polymorphism & Dispatch](lessons/0009-inheritance-polymorphism-dispatch.html) ([Reference](reference/0009-inheritance-and-polymorphism.html))
   - `0010`: [Interfaces & Composition](lessons/0010-interfaces-and-composition.html) ([Reference](reference/0010-interfaces-and-composition.html))
   - `0011`: [The Object Contract](lessons/0011-the-object-contract.html) ([Reference](reference/0011-the-object-contract.html))
   - `0012`: [Modern Types: Records, Enums & Sealed](lessons/0012-modern-types-records-enums-sealed.html) ([Reference](reference/0012-records-enums-and-sealed-types.html))
   - `0013`: [Pattern Matching & Domain Modeling](lessons/0013-pattern-matching-and-domain-modeling.html) ([Reference](reference/0013-pattern-matching-and-adts.html))
   - `0014`: [Robust Exception Handling](lessons/0014-robust-exception-handling.html) ([Reference](reference/0014-exception-handling.html))
   - `0015`: [Strings, Immutability & Text Blocks](lessons/0015-strings-immutability-and-text-blocks.html) ([Reference](reference/0015-strings-and-text-blocks.html))
   - `0077`: [Regular Expressions (`Pattern`/`Matcher`) & ReDoS Defense](lessons/0077-regular-expressions-and-redos-safety.html) ([Reference](reference/0077-regex-and-redos-safety.html)) **NEW**
   - `0016`: [Production Date/Time (`java.time`) & Financial Math (`BigDecimal`)](lessons/0016-temporal-java-time-and-bigdecimal.html) ([Reference](reference/0016-java-time-and-bigdecimal.html))
   - `0017`: [Generics & Type Erasure](lessons/0017-generics-and-type-erasure.html) ([Reference](reference/0017-generics-and-erasure.html))
   - `0018`: [Generic Wildcards & The PECS Principle](lessons/0018-generic-wildcards-and-pecs.html) ([Reference](reference/0018-wildcards-and-pecs.html))
   - `0019`: [Collections Framework Internals](lessons/0019-collections-framework-core.html) ([Reference](reference/0019-collections-cheat-sheet.html))
   - `0020`: [Sequenced Collections & Comparators](lessons/0020-sequenced-collections-and-comparators.html) ([Reference](reference/0020-sequenced-collections-and-sorting.html))
   - `0021`: [Lambdas & Functional Interfaces](lessons/0021-lambdas-and-functional-interfaces.html) ([Reference](reference/0021-functional-interfaces-and-lambdas.html))
   - `0022`: [Method References, `Optional<T>` & Null Contracts](lessons/0022-method-references-and-optional.html) ([Reference](reference/0022-method-references-and-optional.html))
   - `0023`: [Stream Pipelines & Transformations](lessons/0023-streams-pipelines-and-transformations.html) ([Reference](reference/0023-stream-pipelines.html))
   - `0024`: [Advanced Collectors & Reductions](lessons/0024-advanced-collectors-and-reductions.html) ([Reference](reference/0024-collectors-and-reductions.html))
   - `0076`: [Java 25 Compact Sources (JEP 512), Module Imports (JEP 511) & Stream Gatherers (JEP 485)](lessons/0076-java25-compact-sources-and-stream-gatherers.html) ([Reference](reference/0076-java25-compact-sources-and-gatherers.html)) **NEW**
   - `0078`: [Classic `java.io` Streams, Encodings & Raw TCP Sockets](lessons/0078-classic-java-io-and-tcp-sockets.html) ([Reference](reference/0078-classic-io-and-sockets.html)) **NEW**
   - `0025`: [Java NIO.2 & File Systems](lessons/0025-java-nio-and-file-systems.html) ([Reference](reference/0025-nio-and-files.html))
   - `0026`: [Modern HTTP Client (`java.net.http`)](lessons/0026-modern-http-client.html) ([Reference](reference/0026-http-client.html))
   - `0079`: [Java Cryptography (`JCA`), AES-GCM & Java 25 KDF API (JEP 510)](lessons/0079-java-cryptography-kdf-and-password-hashing.html) ([Reference](reference/0079-cryptography-and-kdf.html)) **NEW**
   - `0080`: [Markdown Javadoc (JEP 467) & Production Logging (`SLF4J`, `Logback`, `System.Logger`)](lessons/0080-javadoc-and-production-logging-facades.html) ([Reference](reference/0080-javadoc-and-logging.html)) **NEW**
   - `0027`: [Threads, Happens-Before & JMM](lessons/0027-threads-and-jmm.html) ([Reference](reference/0027-threads-and-jmm.html))
   - `0028`: [Synchronization & Locks](lessons/0028-synchronization-and-locks.html) ([Reference](reference/0028-locks-and-monitors.html))
   - `0029`: [Thread Pools & Executors](lessons/0029-concurrency-utilities-executors.html) ([Reference](reference/0029-executors-and-pools.html))
   - `0030`: [Concurrent Collections & High Contention](lessons/0030-concurrent-collections-and-contention.html) ([Reference](reference/0030-concurrent-collections-and-locks.html))
   - `0031`: [Asynchronous Composition (`CompletableFuture`)](lessons/0031-asynchronous-completable-future.html) ([Reference](reference/0031-completable-future.html))
   - `0032`: [Virtual Threads (Java 25 JEP 491 Unpinning)](lessons/0032-java-21-virtual-threads.html) ([Reference](reference/0032-virtual-threads-loom.html))
   - `0033`: [Structured Concurrency (JEP 505) & Scoped Values (JEP 506)](lessons/0033-structured-concurrency-and-scoped-values.html) ([Reference](reference/0033-structured-concurrency-and-scoped-values.html))
   - `0034`: [JVM Memory Layout & Heap Generations](lessons/0034-jvm-memory-and-heap-generations.html) ([Reference](reference/0034-jvm-memory-and-heap-generations.html))
   - `0035`: [Garbage Collection: G1GC & Generational ZGC](lessons/0035-garbage-collection-g1-and-zgc.html) ([Reference](reference/0035-garbage-collection-g1-and-zgc.html))
   - `0036`: [HotSpot JIT Compilation, JFR & Profiling](lessons/0036-jit-compilation-jfr-and-profiling.html) ([Reference](reference/0036-jit-jfr-and-profiling.html))
   - `0037`: [ClassLoaders, MethodHandles & Reflection](lessons/0037-classloading-reflection-annotations.html) ([Reference](reference/0037-classloading-reflection-annotations.html))
   - `0038`: [Build Automation: Gradle 9 & Maven BOMs](lessons/0038-build-automation-maven-and-gradle.html) ([Reference](reference/0038-build-automation-maven-and-gradle.html))
   - `0039`: [Unit & Contract Testing: JUnit 5, AssertJ, Mockito & REST Assured](lessons/0039-modern-unit-testing-junit5-mockito.html) ([Reference](reference/0039-unit-testing.html))
   - `0040`: [Strong Encapsulation (`--add-opens`) & Striped-Concurrency Capstone](lessons/0040-encapsulation-jpms-and-core-capstone.html) ([Reference](reference/0040-encapsulation-and-core-capstone.html))

2. **Main Track — Phase 2: Enterprise & Framework Internals (`0054`–`0059`, 6 Lessons)**
   - `0054`: [Dynamic Proxies vs CGLIB/ByteBuddy & Self-Invocation](lessons/0054-dynamic-proxies-and-bytecode-manipulation.html) ([Reference](reference/0054-proxies-and-aop.html))
   - `0055`: [Runtime Generics, Super Type Tokens & Jackson Serialization](lessons/0055-runtime-generics-type-tokens-json.html) ([Reference](reference/0055-type-tokens-and-json.html))
   - `0056`: [JDBC Batching, PreparedStatement Plans & HikariCP](lessons/0056-jdbc-and-connection-pooling-mechanics.html) ([Reference](reference/0056-jdbc-and-pools.html))
   - `0057`: [JPA / Hibernate 7 Entity Lifecycle & N+1 Prevention](lessons/0057-jpa-hibernate-entity-lifecycle.html) ([Reference](reference/0057-jpa-and-hibernate.html))
   - `0058`: [Transactions, Isolation, Locking & Flyway Migrations](lessons/0058-transactions-locking-isolation-and-flyway.html) ([Reference](reference/0058-transactions-locking-and-flyway.html))
   - `0059`: [Servlet 6.1 Containers, Filters & Tomcat Virtual Threads](lessons/0059-servlet-spec-web-containers-virtual-threads.html) ([Reference](reference/0059-servlet-and-virtual-threads.html))

3. **Main Track — Phase 3: Spring Boot 4.0, Framework 7.0 & Event-Driven Services (`0060`–`0070`, `0081`, 12 Lessons)**
   - `0060`: [IoC, Bean Scopes & `BeanPostProcessor` Internals](lessons/0060-ioc-and-bean-lifecycle.html) ([Reference](reference/0060-spring-ioc-basics.html))
   - `0061`: [Spring Boot 4 Modular Starters, Auto-Configuration & Record Properties](lessons/0061-spring-boot-auto-configuration-and-properties.html) ([Reference](reference/0061-auto-configuration-and-properties.html))
   - `0062`: [REST APIs, Validation, JSpecify & RFC 9457 `ProblemDetail`](lessons/0062-rest-apis-dtos-validation-problemdetail.html) ([Reference](reference/0062-rest-dtos-and-validation.html))
   - `0063`: [Spring Data JPA: Projections, Keyset Pagination & OSIV Off](lessons/0063-spring-data-jpa-repositories-transactions.html) ([Reference](reference/0063-data-jpa-and-transactions.html))
   - `0064`: [Resilient HTTP: `RestClient`, Framework 7 `@Retryable`, Resilience4j & Idempotency](lessons/0064-restclient-resilience4j-and-idempotency.html) ([Reference](reference/0064-restclient-and-resilience4j.html))
   - `0065`: [Multi-Tier Caching: Caffeine (`W-TinyLFU`) & Redis](lessons/0065-caching-architecture-caffeine-and-redis.html) ([Reference](reference/0065-caffeine-and-redis-caching.html))
   - `0066`: [Testing Pyramid: `RestTestClient`, `MockMvcTester`, WireMock & Testcontainers](lessons/0066-production-testing-mockmvc-testcontainers.html) ([Reference](reference/0066-testing-and-testcontainers.html))
   - `0067`: [Spring Security 7 Filter Chains, OAuth2 JWT & Layered Docker](lessons/0067-security-oauth2-jwt-and-containerization.html) ([Reference](reference/0067-security-oauth2-and-docker.html))
   - `0068`: [Observability: OpenTelemetry Starter, Micrometer Tracing & MDC](lessons/0068-observability-opentelemetry-and-logging.html) ([Reference](reference/0068-opentelemetry-and-logging.html))
   - `0081`: [Spring Boot 4.0 Deep Delta: Jackson 3, JSpecify, Native API Versioning, `RestTestClient`, Quarkus & Javalin](lessons/0081-spring-boot-4-and-modern-web-ecosystem.html) ([Reference](reference/0081-spring-boot-4-and-web-alternatives.html)) **NEW**
   - `0069`: [Apache Kafka Internals: KRaft, ISR & Idempotent Producers](lessons/0069-apache-kafka-architecture-and-clients.html) ([Reference](reference/0069-kafka-architecture.html))
   - `0070`: [Spring Kafka Async Publishing, Retry/DLT & Transactional Outbox](lessons/0070-spring-kafka-outbox-and-kafka-streams.html) ([Reference](reference/0070-spring-kafka-and-outbox.html))

4. **`[Optional Track A]` Coding Interviews & Algorithmic Patterns (`0041`–`0053`, 13 Lessons — Demoted for Post-Backend Interview Prep)**
   - `0041`: [Java Interview Traps, Autoboxing & Fast I/O](lessons/0041-java-interview-traps-autoboxing-io.html) ([Reference](reference/0041-java-interview-traps-cheatsheet.html))
   - `0042`: [Two Pointers & Sliding Window](lessons/0042-arrays-two-pointers-sliding-window.html) ([Reference](reference/0042-two-pointers-sliding-window.html))
   - `0043`: [Prefix Sums, Difference Arrays & String Rolling Hash / KMP](lessons/0043-prefix-sums-difference-arrays-string-hashing.html) ([Reference](reference/0043-prefix-sums-and-string-hashing.html))
   - `0044`: [Monotonic Stacks, Queues & Deques](lessons/0044-monotonic-stacks-queues-deques.html) ([Reference](reference/0044-monotonic-stack-patterns.html))
   - `0045`: [Linked Lists & `O(1)` LRU Cache](lessons/0045-fast-linked-lists-and-lru-cache.html) ([Reference](reference/0045-linked-list-and-lru-patterns.html))
   - `0046`: [Binary Search & Answer Space](lessons/0046-binary-search-and-answer-space.html) ([Reference](reference/0046-binary-search-templates.html))
   - `0047`: [Heaps & Two-Heap Streaming Median](lessons/0047-heaps-priority-queues-two-heaps.html) ([Reference](reference/0047-heaps-and-priority-queues.html))
   - `0048`: [Binary Trees, BSTs & LCA](lessons/0048-binary-trees-bsts-traversals.html) ([Reference](reference/0048-trees-and-bst-patterns.html))
   - `0049`: [Prefix Tries & Backtracking](lessons/0049-tries-prefix-trees-backtracking.html) ([Reference](reference/0049-tries-and-backtracking.html))
   - `0050`: [Graphs: BFS, DFS & Cycle Detection](lessons/0050-graph-algorithms-bfs-dfs-cycles.html) ([Reference](reference/0050-graph-bfs-dfs-cycles.html))
   - `0051`: [Topological Sort, DSU & Dijkstra](lessons/0051-advanced-graphs-topological-dsu-dijkstra.html) ([Reference](reference/0051-topological-dsu-dijkstra.html))
   - `0052`: [Dynamic Programming Mastery](lessons/0052-dynamic-programming-mastery.html) ([Reference](reference/0052-dynamic-programming-patterns.html))
   - `0053`: [Sweep-Line Intervals & Bitmask DP](lessons/0053-intervals-and-bit-manipulation.html) ([Reference](reference/0053-intervals-and-bit-tricks.html))

5. **`[Optional Track B]` Distributed Data & Streaming Engines (`0071`–`0073`, 3 Lessons — Demoted Big Data Track)**
   - `0071`: [Apache Spark: Catalyst, `Dataset<Row>` & Tungsten](lessons/0071-apache-spark-dataset-jvm-memory.html) ([Reference](reference/0071-spark-dataset-and-memory.html))
   - `0072`: [Apache Flink: Watermarks, `KeyedProcessFunction` & RocksDB](lessons/0072-apache-flink-stateful-stream-processing.html) ([Reference](reference/0072-flink-stateful-processing.html))
   - `0073`: [End-to-End Streaming Capstone (Gateway + Outbox + Kafka + Flink)](lessons/0073-distributed-streaming-capstone.html) ([Reference](reference/0073-capstone-pipeline.html))
