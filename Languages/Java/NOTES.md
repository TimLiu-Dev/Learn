# Teaching Notes

- **Learner Profile & Target Bar:**
  - Transitioning from passive Kotlin reading to **Senior Java Software Engineer** mastery (Java 21 LTS, DSA Medium/Hard Coding Interviews, Spring Boot 3 Internals & Production Microservices, and Distributed Streaming with Kafka, Spark, and Flink).
  - Needs active recall, deliberate coding from scratch, and production debugging intuition rather than passive code reading.
- **Explicit Interactive Lesson Requirements (Mandatory in Every Lesson HTML):**
  1. **Production Code Examples:** Minimum 3 code blocks per lesson contrasting naive/buggy code, under-the-hood mechanics (bytecode, memory layout, SQL, wire protocol), and idiomatic Java 21 solutions.
  2. **Runnable & Checkable In-HTML Code Practices (`assets/workbench.js`):**
     - 2 interactive workbenches per coding lesson (`#ex-1` From-Scratch Implementation + `#ex-2` Senior Bug-Fix / Refactor Challenge).
     - **Never pre-solved**: starter code must require genuine edits to pass checks.
     - Must include **Run & Check Code** (3–5 checks + simulated JVM output), **Progressive Hints** (`2–3` steps), and **Show Final Answer** (displaying the complete reference solution, senior trade-off explanation, Copy button, and one-click *Load into Editor & Run*).
  3. **Multiple-Choice Code Quizzes with Full Option Breakdowns (`assets/quiz.js`):**
     - 3 questions per lesson (at least 2 code-snippet puzzles).
     - Balanced option word lengths and varied correct-answer positions (`A`–`D`).
     - Selecting any option must show **why the correct choice is right** AND **why each wrong choice fails** (including specific feedback on the selected distractor).
- **73-Lesson 5-Phase Curriculum:**
  - **Phase 1 (`0001`–`0040`):** Core Java 21 LTS, Pattern Matching, `java.time`/`BigDecimal`, Generics/PECS, Collections Internals, Streams, JMM, High-Contention Concurrency, Loom Virtual Threads, `StructuredTaskScope`/`ScopedValue`, GC (G1/ZGC), HotSpot JIT & JFR/Async-Profiler Diagnostics.
  - **Phase 2 (`0041`–`0053`):** Java Interview Memory/Sorting Traps & 13 Core Algorithmic Problem Patterns (including Prefix Sums & Rolling Hash/KMP).
  - **Phase 3 (`0054`–`0059`):** Dynamic Proxies & AOP, Super Type Tokens, JDBC/HikariCP, JPA Lifecycle & N+1, ACID Isolation, `@Version` Optimistic vs `FOR UPDATE` Pessimistic Locking, `@Transactional` Traps & Flyway.
  - **Phase 4 (`0060`–`0068`):** Spring Boot 3 IoC, Auto-Config, RFC 9457 `ProblemDetail`, Spring Data Keyset Pagination & OSIV Off, `RestClient` + Resilience4j + Idempotency Keys, Caffeine + Redis Caching, Testcontainers, OAuth2 JWT Resource Server & OpenTelemetry/MDC Observability.
  - **Phase 5 (`0069`–`0073`):** Apache Kafka Internals, Spring Kafka Async Futures + Retry/DLT + Outbox + Kafka Streams, Apache Spark `Dataset<Row>` & Tungsten, Apache Flink `KeyedProcessFunction` & RocksDB Checkpoints, and End-to-End Capstone.
