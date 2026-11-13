# Java, Spring Boot, DSA & Big Data Glossary

The canonical vocabulary and precise technical terminology for this Senior Java teaching workspace. All lessons, coding exercises, quizzes, and reference sheets adhere to these definitions.

## Core JVM, Concurrency & Diagnostics

**JVM (Java Virtual Machine)**:
The abstract computing machine that executes compiled Java bytecode (`.class`) by managing runtime memory, thread scheduling, and JIT compilation.
_Avoid_: Java compiler, Java runtime environment.

**HotSpot JIT (Just-In-Time) Compiler**:
The JVM tiered compilation engine (C1 client + C2 server compilers) that profiles bytecode execution at runtime, inlines hot methods, performs Escape Analysis, and emits optimized native CPU instructions.
_Avoid_: AOT compiler, bytecode interpreter.

**Compact Source File & `java.lang.IO` (JEP 512)**:
A single-file Java 25 source program omitting an explicit enclosing `class` declaration and launching via an instance `void main()` method with `IO.println(...)` / `IO.readln(...)`.
_Avoid_: Untyped scripting language, reflective runner.

**Flexible Constructor Body (JEP 513)**:
Java 25 language rule allowing prologue statements (argument validation, local computations, field initialization) before `super(...)` or `this(...)` inside a constructor, provided they do not read `this` before superclass construction.
_Avoid_: Unbounded helper factory methods only.

**Stream Gatherer (`JEP 485`)**:
A composable intermediate operation (`Stream.gather(Gatherer)`) introduced cleanly in Java 24/25 via `java.util.stream.Gatherers` for windowing, batching, stateful scanning, and short-circuiting mid-pipeline.
_Avoid_: Terminal Collector misuse.

**Escape Analysis & Scalar Replacement**:
A C2 JIT optimization determining whether an object's reference escapes the current method or thread; non-escaping objects can have their fields decomposed onto CPU registers/stack slots instead of allocating on the heap.
_Avoid_: Stack allocation keyword, manual free.

**JDK Flight Recorder (JFR)**:
A low-overhead (`<1%` CPU) production profiling and diagnostics engine built directly into the OpenJDK runtime capturing allocation rates, GC pauses, lock contention, virtual thread pinning, and I/O events.
_Avoid_: Debug-mode tracer, external log scraper.

**Stack Memory**:
Thread-private memory regions storing active call frames, primitive local variables, and object references with strict LIFO allocation and zero garbage collection overhead.
_Avoid_: Local heap, static memory.

**Heap Memory**:
The JVM shared memory arena where object instances, arrays, and virtual-thread continuation stack chunks reside, managed by generational garbage collectors (G1GC, Generational ZGC).
_Avoid_: Global variables pool, system memory.

**Autoboxing**:
The automatic compile-time insertion of conversions between primitive types (e.g., 4-byte `int`) and heap-allocated wrapper objects (e.g., 24-byte `Integer` under compressed OOPs).
_Avoid_: Object casting, manual wrapping.

**Type Erasure**:
The compile-time process where `javac` translates generic type parameters into their upper bounds (typically `Object`), inserts synthetic casts, and generates bridge methods to preserve binary compatibility.
_Avoid_: Dynamic typing, runtime duck typing.

**Structured Concurrency (`StructuredTaskScope`)**:
A concurrency model tying the lifetimes of concurrent subtasks spawned on virtual threads to a lexical block, ensuring automatic cancellation on failure and zero leaked child threads (JDK 21 preview `ShutdownOnFailure`; JDK 25 JEP 505 `StructuredTaskScope.open()`).
_Avoid_: Unbounded fire-and-forget futures.

**ScopedValue**:
An immutable, lexically-scoped context carrier designed for Project Loom virtual threads, replacing mutable `ThreadLocal` variables without unbounded inheritance copying or forgotten `.remove()` memory leaks.
_Avoid_: Mutable global state, pooled ThreadLocal.

**KDF (`javax.crypto.KDF`, JEP 510)**:
The Java 25 standard Key Derivation Function API supporting HKDF-SHA256 extraction and expansion of high-entropy symmetric `SecretKey` material from shared secrets or contexts.
_Avoid_: Rolling raw ad-hoc XOR/SHA concatenation for key expansion.

**ReDoS (Regular Expression Denial of Service)**:
Catastrophic CPU stalling triggered when a backtracking NFA regex engine (`java.util.regex.Pattern`) encounters nested quantifiers (`(a+)+`) on crafted non-matching input.
_Avoid_: Unbounded regexes on untrusted user strings without possessive/atomic groups or length limits.

**JSpecify Nullness (`@NullMarked` / `@Nullable`)**:
The standardized JVM nullability annotation set adopted across Spring Boot 4, Spring Framework 7, and NullAway to distinguish non-null defaults from explicit nullable parameters and return values.
_Avoid_: Mixing conflicting JSR-305, JetBrains, and legacy Spring `@NonNull` annotations.

---

## Data Structures & Algorithmic Patterns

**Dual-Pivot Quicksort**:
An in-place partitioning sort used by `Arrays.sort()` on primitive arrays (`int[]`), offering fast cache locality and `O(N log N)` average complexity, with `O(N^2)` worst-case vulnerability on crafted adversarial inputs.
_Avoid_: TimSort, MergeSort.

**TimSort**:
A hybrid stable sorting algorithm combining MergeSort and InsertionSort used by `Arrays.sort(Object[])` and `List.sort()`, guaranteeing `O(N log N)` worst-case time complexity.
_Avoid_: Primitive quicksort, bubble sort.

**Prefix Sum**:
A precomputed cumulative array `P[i] = P[i-1] + nums[i-1]` (or frequency `HashMap` of running sums) enabling `O(1)` range-sum queries and `O(N)` subarray-sum target counting even in the presence of negative integers.
_Avoid_: Sliding window on negative numbers.

**Monotonic Stack / Deque**:
A stack or `ArrayDeque` maintaining elements in strictly increasing or decreasing order, enabling `O(N)` amortized resolution of next-greater-element and sliding-window-maximum problems.
_Avoid_: Sorted list, priority stack.

**Disjoint Set Union (DSU / Union-Find)**:
A forest structure tracking dynamic connected components in near `O(1)` (`O(alpha(N))`) amortized time via path compression and union by rank/size.
_Avoid_: Repeated BFS per edge.

---

## Spring Framework, Database & Microservice Resilience

**Inversion of Control (IoC) & Dependency Injection (DI)**:
Delegating bean instantiation, constructor dependency wiring, and lifecycle hooks (`BeanPostProcessor`) to Spring's `ApplicationContext` rather than hardcoding `new` collaborators.
_Avoid_: Service Locator anti-pattern, `@Autowired` field injection.

**Self-Invocation Trap**:
The silent bypass of Spring AOP interceptors (`@Transactional`, `@Async`, `@Cacheable`, `@Retryable`) when a bean invokes one of its own methods via `this.method()` instead of routing through the enclosing JDK or CGLIB proxy.
_Avoid_: Proxy leak, inner method bug.

**Persistence Context (First-Level Cache)**:
The transactional identity map inside JPA's `EntityManager` that tracks `Managed` entities, retains initial state snapshots for automatic dirty checking on flush, and guarantees repeatable object identity within one transaction.
_Avoid_: Hibernate second-level cache, Redis cache.

**N+1 Select Problem**:
Executing `1` parent query returning `N` rows followed by `N` lazy-loading child queries (`N+1` round-trips); solved via `JOIN FETCH`, `@EntityGraph`, `@BatchSize`, or DTO projections.
_Avoid_: Lazy load leak.

**Optimistic vs. Pessimistic Locking**:
Optimistic locking (`@Version`) detects concurrent Lost Updates at `UPDATE` commit time without holding DB row locks (`WHERE id = ? AND version = ?`); Pessimistic locking (`SELECT ... FOR UPDATE`) acquires exclusive row locks upfront under high contention.
_Avoid_: JVM `synchronized` lock across multi-pod services.

**Circuit Breaker (Resilience4j)**:
A state machine (`CLOSED -> OPEN -> HALF_OPEN`) wrapping downstream network calls (`RestClient`) to fail fast when error or slow-call rates breach thresholds, preventing cascading thread/connection exhaustion.
_Avoid_: Infinite retries without backoff/jitter.

**W-TinyLFU (`Caffeine`)**:
A modern cache eviction and admission algorithm combining recency (Window LRU) with frequency sketching (Count-Min Sketch) to achieve near-optimal hit rates in bounded JVM heap memory.
_Avoid_: Unbounded `ConcurrentHashMap` cache.

**Jackson 3 (`tools.jackson`)**:
Major release of the Jackson JSON/data-binding library used by default in Spring Boot 4 / Framework 7, relocating core packages to `tools.jackson.*` and emphasizing immutable `JsonMapper` builders.
_Avoid_: Importing legacy `com.fasterxml.jackson.databind.ObjectMapper` on Boot 4 greenfield code unless maintaining compatibility bridges.

---

## Distributed Systems & Event Streaming

**Partition**:
The unit of parallelism, replication, and strict offset ordering within an Apache Kafka topic—an immutable, append-only log assigned to at most one consumer within a single Consumer Group.
_Avoid_: Global topic FIFO queue.

**Idempotent Producer & Consumer**:
Producer idempotence (`enable.idempotence=true`, PID + sequence number) prevents duplicate broker appends on network retries; Consumer idempotency (unique `event_id` deduplication / natural upsert) safely absorbs at-least-once redeliveries.
_Avoid_: Fire-and-forget publishing, assuming exactly-once network delivery.

**Transactional Outbox Pattern**:
Atomically persisting both domain state changes and an `outbox_events` row inside a single local RDBMS transaction, then streaming committed WAL/outbox records into Kafka via CDC (Debezium) or a relay to eliminate Dual-Write inconsistency.
_Avoid_: Uncoordinated DB commit + `kafkaTemplate.send()`.

**Watermark**:
A monotonically advancing event-time progress marker in Apache Flink signaling that no further events with timestamp `<= T` are expected, triggering event-time window and timer evaluations despite network out-of-orderness.
_Avoid_: Worker wall-clock processing time.

**Tungsten Engine**:
Apache Spark's execution engine encoding rows into compact binary layouts (`UnsafeRow`) in off-heap/page memory with whole-stage code generation, bypassing 24-byte JVM object headers and GC pause storms.
_Avoid_: Naive boxed Java object RDDs.
