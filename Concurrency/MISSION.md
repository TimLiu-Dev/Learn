# Mission: End-to-End Concurrency Engineering (Junior Android → Senior Engineer, 2027)

## Why
Transition from a junior Android developer with basic Kotlin Coroutines familiarity to a senior-level concurrency and systems engineer across **both** Mobile/Systems (Part 1: OS/JMM, Kotlin Coroutines & Flow, Android Production, Distributed Systems) **and** Enterprise JVM Backend (Part 2: Java 21–25+ Virtual Threads, Spring Boot, HikariCP/JPA Locking, WebFlux, Kafka, and Resilience4j) for senior 2027 roles.

## Success looks like
- Explain concurrency from hardware/OS primitives (cores, preemption, store buffers, JMM happens-before) up through Kotlin Coroutines CPS internals, Java Virtual Threads (JEP 444 / JEP 491), Spring Boot transactional boundaries, and distributed fencing tokens without treating any layer as magic.
- Diagnose and fix race conditions, deadlocks, HikariCP pool starvation, `@Async`/`@Transactional` proxy bugs, Reactor event-loop blocking, and cancellation leaks deterministically.
- Ship three benchmarked, resume-ready end-to-end projects:
  1. **SyncForge** (Part 1 · Android-First): Offline-first conflict-resolving sync & media pipeline SDK.
  2. **PulseQueue** (Part 1 · Full-Stack): Rate-limited JVM concurrent job scheduler + live Android client.
  3. **LedgerFlow** (Part 2 · Java/Spring Boot Senior Backend): High-throughput idempotent payment, outbox, & webhook engine using Virtual Threads, HikariCP tuning, optimistic/pessimistic locking, Kafka consumers, and Resilience4j.

## Constraints
- Starting baseline: Junior Android developer with minimal Kotlin Coroutines exposure.
- Practice format: Self-contained offline HTML lessons in `./lessons/` sharing `./assets/`, with interactive visualizers, runnable code labs (tests + 3 hints + final answer), side-by-side Kotlin/Java Spring Boot code, and balanced MCQs explaining right and wrong options.
- Reference sheets live in `./reference/`.

## Out of scope
- Non-JVM kernel driver development or exotic C++ `memory_order_consume` proofs unrelated to JVM/Android/Spring Boot/distributed systems.
