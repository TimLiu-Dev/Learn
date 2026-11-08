# Senior Java 21 LTS Master Plan: Core JVM, DSA, Spring Boot 3 & Distributed Streaming

A comprehensive, 73-lesson Senior Software Engineer curriculum covering modern Java 21 LTS from JVM memory layout and Project Loom concurrency to LeetCode algorithmic mastery, Spring Framework internals, resilient microservices, and distributed streaming systems.

Every lesson (`./lessons/0001..0073`) is a self-contained interactive HTML workshop featuring:
1. **Production Code Examples**: Contrasting naive/buggy implementations, under-the-hood bytecode/SQL/memory mechanics, and idiomatic Java 21 solutions.
2. **Runnable & Checkable In-Browser Code Practices (2 per Lesson)**: An implementation drill + a senior bug-fix challenge with multi-assertion checks, simulated JVM console output, **Progressive Hints**, and a toggleable **Reference Final Answer** (with one-click *Load into Editor & Run*).
3. **Multiple-Choice Code & Architecture Quizzes**: Code-puzzle and design questions that explain both **why the correct choice works** and **why every wrong choice fails**.
4. **Printable Reference Cheat Sheets (`./reference/0001..0073`)**: High-density lookup tables,canonical templates, and production pitfall checklists.

---

## Phase 1: Core Java 21 LTS, Concurrency & JVM Internals (Lessons 0001–0040)
*Goal: Master Java 21 syntax, algebraic domain modeling, pattern matching, `java.time`/`BigDecimal`, generics, collections internals, streams, JMM concurrency, Virtual Threads, Structured Concurrency, GC, HotSpot JIT, and JFR production profiling.*

- **[Lesson 0001: Master Plan, Legend & Senior Java Curriculum](lessons/0001-master-plan-and-curriculum.html)** &bull; [Reference Sheet](reference/0001-master-plan-and-legend.html)
- **[Lesson 0002: The Java Execution Mental Model & Toolchain](lessons/0002-the-java-execution-mental-model.html)** &bull; [Reference Sheet](reference/0002-jvm-execution-and-toolchain.html)
- **[Lesson 0003: Primitive Data Types, Memory Layout & Arithmetic Hazards](lessons/0003-primitive-data-types-and-memory.html)** &bull; [Reference Sheet](reference/0003-primitive-types-and-conversions.html)
- **[Lesson 0004: Control Flow & Modern Switch Expressions](lessons/0004-control-flow-and-modern-switch.html)** &bull; [Reference Sheet](reference/0004-control-flow-and-switch.html)
- **[Lesson 0005: Arrays, Contiguous Memory & java.util.Arrays](lessons/0005-arrays-and-command-line-args.html)** &bull; [Reference Sheet](reference/0005-arrays-and-utilities.html)
- **[Lesson 0006: Classes, Heap Allocation & Object Lifecycle](lessons/0006-classes-heap-allocation-and-lifecycle.html)** &bull; [Reference Sheet](reference/0006-classes-and-lifecycle.html)
- **[Lesson 0007: Pass-by-Value Reference Semantics in Java](lessons/0007-pass-by-value-semantics.html)** &bull; [Reference Sheet](reference/0007-pass-by-value-semantics.html)
- **[Lesson 0008: Encapsulation, Immutability & Defensive Copying](lessons/0008-encapsulation-and-defensive-copying.html)** &bull; [Reference Sheet](reference/0008-encapsulation-and-immutability.html)
- **[Lesson 0009: Inheritance, Polymorphism & Dynamic Dispatch](lessons/0009-inheritance-polymorphism-dispatch.html)** &bull; [Reference Sheet](reference/0009-inheritance-and-polymorphism.html)
- **[Lesson 0010: Interfaces, Default Methods & Composition over Inheritance](lessons/0010-interfaces-and-composition.html)** &bull; [Reference Sheet](reference/0010-interfaces-and-composition.html)
- **[Lesson 0011: The Object Contract: equals(), hashCode() & toString()](lessons/0011-the-object-contract.html)** &bull; [Reference Sheet](reference/0011-the-object-contract.html)
- **[Lesson 0012: Modern Algebraic Types: Records, Enums & Sealed Classes](lessons/0012-modern-types-records-enums-sealed.html)** &bull; [Reference Sheet](reference/0012-records-enums-and-sealed-types.html)
- **[Lesson 0013: Deep Java 21 Pattern Matching & Algebraic Domain Modeling](lessons/0013-pattern-matching-and-domain-modeling.html)** &bull; [Reference Sheet](reference/0013-pattern-matching-and-adts.html)
- **[Lesson 0014: Robust Exception Handling, Suppressed Exceptions & AutoCloseable](lessons/0014-robust-exception-handling.html)** &bull; [Reference Sheet](reference/0014-exception-handling.html)
- **[Lesson 0015: Strings, Compact Strings, String Pool & Text Blocks](lessons/0015-strings-immutability-and-text-blocks.html)** &bull; [Reference Sheet](reference/0015-strings-and-text-blocks.html)
- **[Lesson 0016: Production Date/Time (java.time) & Financial Math (BigDecimal)](lessons/0016-temporal-java-time-and-bigdecimal.html)** &bull; [Reference Sheet](reference/0016-java-time-and-bigdecimal.html)
- **[Lesson 0017: Generics, Bridge Methods, Heap Pollution & Type Erasure](lessons/0017-generics-and-type-erasure.html)** &bull; [Reference Sheet](reference/0017-generics-and-erasure.html)
- **[Lesson 0018: Generic Wildcards, Variance & The PECS Principle](lessons/0018-generic-wildcards-and-pecs.html)** &bull; [Reference Sheet](reference/0018-wildcards-and-pecs.html)
- **[Lesson 0019: Collections Internals: HashMap Treeification, Load Factor & ArrayDeque](lessons/0019-collections-framework-core.html)** &bull; [Reference Sheet](reference/0019-collections-cheat-sheet.html)
- **[Lesson 0020: Java 21 Sequenced Collections & Null-Safe Chained Comparators](lessons/0020-sequenced-collections-and-comparators.html)** &bull; [Reference Sheet](reference/0020-sequenced-collections-and-sorting.html)
- **[Lesson 0021: Lambdas, invokedynamic, Variable Capture & Functional Interfaces](lessons/0021-lambdas-and-functional-interfaces.html)** &bull; [Reference Sheet](reference/0021-functional-interfaces-and-lambdas.html)
- **[Lesson 0022: Method References & Idiomatic Optional<T> Pipelines](lessons/0022-method-references-and-optional.html)** &bull; [Reference Sheet](reference/0022-method-references-and-optional.html)
- **[Lesson 0023: Stream Pipelines, Lazy Spliterators & Parallel Stream Hazards](lessons/0023-streams-pipelines-and-transformations.html)** &bull; [Reference Sheet](reference/0023-stream-pipelines.html)
- **[Lesson 0024: Advanced Collectors, Downstream Grouping, teeing() & Reductions](lessons/0024-advanced-collectors-and-reductions.html)** &bull; [Reference Sheet](reference/0024-collectors-and-reductions.html)
- **[Lesson 0025: Java NIO.2, ByteBuffers, Channels & Atomic File Operations](lessons/0025-java-nio-and-file-systems.html)** &bull; [Reference Sheet](reference/0025-nio-and-files.html)
- **[Lesson 0026: Modern HTTP Client (java.net.http), HTTP/2 & Timeouts](lessons/0026-modern-http-client.html)** &bull; [Reference Sheet](reference/0026-http-client.html)
- **[Lesson 0027: Threads, Happens-Before & The Java Memory Model (JMM)](lessons/0027-threads-and-jmm.html)** &bull; [Reference Sheet](reference/0027-threads-and-jmm.html)
- **[Lesson 0028: Intrinsic Monitors, ReentrantLock, Conditions & Deadlock Prevention](lessons/0028-synchronization-and-locks.html)** &bull; [Reference Sheet](reference/0028-locks-and-monitors.html)
- **[Lesson 0029: Thread Pools, Bounded Queues, Backpressure & Executor Sizing](lessons/0029-concurrency-utilities-executors.html)** &bull; [Reference Sheet](reference/0029-executors-and-pools.html)
- **[Lesson 0030: High-Contention Concurrency: ConcurrentHashMap, LongAdder, StampedLock & Semaphore](lessons/0030-concurrent-collections-and-contention.html)** &bull; [Reference Sheet](reference/0030-concurrent-collections-and-locks.html)
- **[Lesson 0031: Asynchronous Composition, Timeouts & Custom Pools with CompletableFuture](lessons/0031-asynchronous-completable-future.html)** &bull; [Reference Sheet](reference/0031-completable-future.html)
- **[Lesson 0032: Project Loom Virtual Threads: Carrier Mounting, Continuations & Pinning](lessons/0032-java-21-virtual-threads.html)** &bull; [Reference Sheet](reference/0032-virtual-threads-loom.html)
- **[Lesson 0033: Structured Concurrency (StructuredTaskScope) & ScopedValue vs ThreadLocal](lessons/0033-structured-concurrency-and-scoped-values.html)** &bull; [Reference Sheet](reference/0033-structured-concurrency-and-scoped-values.html)
- **[Lesson 0034: JVM Memory Layout, Object Headers, Compressed OOPs & Heap Generations](lessons/0034-jvm-memory-and-heap-generations.html)** &bull; [Reference Sheet](reference/0034-jvm-memory-and-heap-generations.html)
- **[Lesson 0035: Garbage Collection Engineering: G1GC & Generational ZGC](lessons/0035-garbage-collection-g1-and-zgc.html)** &bull; [Reference Sheet](reference/0035-garbage-collection-g1-and-zgc.html)
- **[Lesson 0036: HotSpot JIT (C1/C2, Escape Analysis) & Production Profiling (JFR, Async-Profiler, jcmd)](lessons/0036-jit-compilation-jfr-and-profiling.html)** &bull; [Reference Sheet](reference/0036-jit-jfr-and-profiling.html)
- **[Lesson 0037: ClassLoaders, MethodHandles, Reflection & Custom Runtime Annotations](lessons/0037-classloading-reflection-annotations.html)** &bull; [Reference Sheet](reference/0037-classloading-reflection-annotations.html)
- **[Lesson 0038: Enterprise Build Engineering: Maven & Gradle Dependency Resolution & BOMs](lessons/0038-build-automation-maven-and-gradle.html)** &bull; [Reference Sheet](reference/0038-build-automation-maven-and-gradle.html)
- **[Lesson 0039: Modern Unit Testing: JUnit 5, Parameterized Tests, AssertJ & Mockito](lessons/0039-modern-unit-testing-junit5-mockito.html)** &bull; [Reference Sheet](reference/0039-unit-testing.html)
- **[Lesson 0040: JDK 21 Strong Encapsulation (--add-opens) & Striped-Concurrency Core Capstone](lessons/0040-encapsulation-jpms-and-core-capstone.html)** &bull; [Reference Sheet](reference/0040-encapsulation-and-core-capstone.html)

---

## Phase 2: Java for Data Structures & Algorithms (DSA) Coding Interviews (Lessons 0041–0053)
*Goal: Solve LeetCode Medium/Hard algorithmic challenges with optimal complexity while avoiding Java-specific autoboxing, sorting, and comparator traps.*

- **[Lesson 0041: Java Interview Traps: Autoboxing Overhead, Sorting Adversaries & Fast I/O](lessons/0041-java-interview-traps-autoboxing-io.html)** &bull; [Reference Sheet](reference/0041-java-interview-traps-cheatsheet.html)
- **[Lesson 0042: Arrays, Two Pointers & Dynamic Sliding Window](lessons/0042-arrays-two-pointers-sliding-window.html)** &bull; [Reference Sheet](reference/0042-two-pointers-sliding-window.html)
- **[Lesson 0043: Prefix Sums + HashMap, Difference Arrays & String Rolling Hash / KMP](lessons/0043-prefix-sums-difference-arrays-string-hashing.html)** &bull; [Reference Sheet](reference/0043-prefix-sums-and-string-hashing.html)
- **[Lesson 0044: Monotonic Stacks, Queues & ArrayDeque Sliding Window Maximum](lessons/0044-monotonic-stacks-queues-deques.html)** &bull; [Reference Sheet](reference/0044-monotonic-stack-patterns.html)
- **[Lesson 0045: Fast/Slow Linked List Manipulation & Custom O(1) LRU Cache](lessons/0045-fast-linked-lists-and-lru-cache.html)** &bull; [Reference Sheet](reference/0045-linked-list-and-lru-patterns.html)
- **[Lesson 0046: Binary Search Boundaries & Monotonic Answer-Space Optimization](lessons/0046-binary-search-and-answer-space.html)** &bull; [Reference Sheet](reference/0046-binary-search-templates.html)
- **[Lesson 0047: Heaps, Custom PriorityQueues & The Two-Heap Streaming Median Pattern](lessons/0047-heaps-priority-queues-two-heaps.html)** &bull; [Reference Sheet](reference/0047-heaps-and-priority-queues.html)
- **[Lesson 0048: Binary Trees, BST Bounds Validation, Iterative Traversals & LCA](lessons/0048-binary-trees-bsts-traversals.html)** &bull; [Reference Sheet](reference/0048-trees-and-bst-patterns.html)
- **[Lesson 0049: Prefix Tries & Pruned Backtracking (Word Search II)](lessons/0049-tries-prefix-trees-backtracking.html)** &bull; [Reference Sheet](reference/0049-tries-and-backtracking.html)
- **[Lesson 0050: Graph Algorithms: Multi-Source BFS, DFS & 3-Color Cycle Detection](lessons/0050-graph-algorithms-bfs-dfs-cycles.html)** &bull; [Reference Sheet](reference/0050-graph-bfs-dfs-cycles.html)
- **[Lesson 0051: Advanced Graphs: Kahn's Topological Sort, Union-Find (DSU) & Dijkstra](lessons/0051-advanced-graphs-topological-dsu-dijkstra.html)** &bull; [Reference Sheet](reference/0051-topological-dsu-dijkstra.html)
- **[Lesson 0052: Dynamic Programming Mastery: 1D/2D States, Knapsack, LIS & Space Rolling](lessons/0052-dynamic-programming-mastery.html)** &bull; [Reference Sheet](reference/0052-dynamic-programming-patterns.html)
- **[Lesson 0053: Sweep-Line Intervals, Greedy Scheduling & Bitwise / Bitmask DP](lessons/0053-intervals-and-bit-manipulation.html)** &bull; [Reference Sheet](reference/0053-intervals-and-bit-tricks.html)

---

## Phase 3: Enterprise & Spring Framework Internals (Lessons 0054–0059)
*Goal: Understand the JVM mechanics powering Spring and Hibernate: dynamic proxies, super type tokens, HikariCP pool sizing, JPA entity states, N+1 fixes, ACID locking/isolation, and zero-downtime Flyway migrations.*

- **[Lesson 0054: JDK Dynamic Proxies vs CGLIB/ByteBuddy & The Self-Invocation Trap](lessons/0054-dynamic-proxies-and-bytecode-manipulation.html)** &bull; [Reference Sheet](reference/0054-proxies-and-aop.html)
- **[Lesson 0055: Runtime Generics, Super Type Tokens & Jackson Serialization Internals](lessons/0055-runtime-generics-type-tokens-json.html)** &bull; [Reference Sheet](reference/0055-type-tokens-and-json.html)
- **[Lesson 0056: JDBC Batching, PreparedStatement Plans & HikariCP Pool Sizing](lessons/0056-jdbc-and-connection-pooling-mechanics.html)** &bull; [Reference Sheet](reference/0056-jdbc-and-pools.html)
- **[Lesson 0057: JPA & Hibernate Entity Lifecycle, Dirty Checking, N+1 & Natural Keys](lessons/0057-jpa-hibernate-entity-lifecycle.html)** &bull; [Reference Sheet](reference/0057-jpa-and-hibernate.html)
- **[Lesson 0058: Database Concurrency: Isolation Levels, Optimistic/Pessimistic Locking, @Transactional Traps & Flyway](lessons/0058-transactions-locking-isolation-and-flyway.html)** &bull; [Reference Sheet](reference/0058-transactions-locking-and-flyway.html)
- **[Lesson 0059: Servlet Containers, Filter Chains, DispatcherServlet & Tomcat Virtual Threads](lessons/0059-servlet-spec-web-containers-virtual-threads.html)** &bull; [Reference Sheet](reference/0059-servlet-and-virtual-threads.html)

---

## Phase 4: Modern Spring Boot 3 Production Microservices (Lessons 0060–0068)
*Goal: Design, build, secure, cache, test, and observe resilient microservices with Spring Boot 3, RFC 9457 `ProblemDetail`, `RestClient`, Resilience4j, Caffeine/Redis, Testcontainers, OAuth2 JWT, and OpenTelemetry.*

- **[Lesson 0060: Inversion of Control (IoC), Bean Scopes & BeanPostProcessor Internals](lessons/0060-ioc-and-bean-lifecycle.html)** &bull; [Reference Sheet](reference/0060-spring-ioc-basics.html)
- **[Lesson 0061: Spring Boot 3 Auto-Configuration (@Conditional) & Record Properties](lessons/0061-spring-boot-auto-configuration-and-properties.html)** &bull; [Reference Sheet](reference/0061-auto-configuration-and-properties.html)
- **[Lesson 0062: Production REST APIs, Bean Validation & RFC 9457 ProblemDetail](lessons/0062-rest-apis-dtos-validation-problemdetail.html)** &bull; [Reference Sheet](reference/0062-rest-dtos-and-validation.html)
- **[Lesson 0063: Spring Data JPA: Projections, Keyset Pagination, OSIV Off & Read-Only Transactions](lessons/0063-spring-data-jpa-repositories-transactions.html)** &bull; [Reference Sheet](reference/0063-data-jpa-and-transactions.html)
- **[Lesson 0064: Inter-Service HTTP: Spring RestClient, Resilience4j (Circuit Breakers, Retries) & Idempotency Keys](lessons/0064-restclient-resilience4j-and-idempotency.html)** &bull; [Reference Sheet](reference/0064-restclient-and-resilience4j.html)
- **[Lesson 0065: Multi-Tier Caching: Caffeine (W-TinyLFU), Redis, Stampede Protection & After-Commit Eviction](lessons/0065-caching-architecture-caffeine-and-redis.html)** &bull; [Reference Sheet](reference/0065-caffeine-and-redis-caching.html)
- **[Lesson 0066: Production Testing Pyramid: @WebMvcTest, @DataJpaTest, WireMock & Testcontainers](lessons/0066-production-testing-mockmvc-testcontainers.html)** &bull; [Reference Sheet](reference/0066-testing-and-testcontainers.html)
- **[Lesson 0067: Spring Security 6 Filter Chains, OAuth2 Resource Server (JWT), @PreAuthorize & Layered Docker](lessons/0067-security-oauth2-jwt-and-containerization.html)** &bull; [Reference Sheet](reference/0067-security-oauth2-and-docker.html)
- **[Lesson 0068: Production Observability: Micrometer, OpenTelemetry Tracing, MDC Logging & Graceful Shutdown](lessons/0068-observability-opentelemetry-and-logging.html)** &bull; [Reference Sheet](reference/0068-opentelemetry-and-logging.html)

---

## Phase 5: Distributed Systems, Event Streaming & Big Data (Lessons 0069–0073)
*Goal: Architect fault-tolerant event-driven pipelines and stateful stream processors with Apache Kafka, Transactional Outbox, Kafka Streams, Apache Spark (`Dataset<Row>` & Tungsten), and Apache Flink.*

- **[Lesson 0069: Apache Kafka Internals: KRaft, ISR/acks=all, Idempotent Producers & Cooperative Rebalancing](lessons/0069-apache-kafka-architecture-and-clients.html)** &bull; [Reference Sheet](reference/0069-kafka-architecture.html)
- **[Lesson 0070: Spring Kafka Async CompletableFuture Publishing, Retry/DLT, Transactional Outbox & Kafka Streams](lessons/0070-spring-kafka-outbox-and-kafka-streams.html)** &bull; [Reference Sheet](reference/0070-spring-kafka-and-outbox.html)
- **[Lesson 0071: Apache Spark on the JVM: Catalyst DAG, Java Dataset<Row> API, Shuffle Skew & Tungsten Off-Heap](lessons/0071-apache-spark-dataset-jvm-memory.html)** &bull; [Reference Sheet](reference/0071-spark-dataset-and-memory.html)
- **[Lesson 0072: Apache Flink Stateful Streaming: Watermarks, KeyedProcessFunction, ValueState & RocksDB Checkpoints](lessons/0072-apache-flink-stateful-stream-processing.html)** &bull; [Reference Sheet](reference/0072-flink-stateful-processing.html)
- **[Lesson 0073: End-to-End Distributed Capstone: Idempotent Spring Boot Gateway, Outbox, Kafka & Flink Fraud Engine](lessons/0073-distributed-streaming-capstone.html)** &bull; [Reference Sheet](reference/0073-capstone-pipeline.html)

---
