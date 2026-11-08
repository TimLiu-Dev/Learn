# Stages 5 & 6: I/O, Networking & Concurrency Mastered

The learner completed Stages 5 and 6 (Lessons 0023 through 0029):
1. **NIO.2 & Modern File Systems (Lesson 0023):** `Path.of()`, `Files` API, atomic replacement via `StandardCopyOption.ATOMIC_MOVE`, and safe stream closing inside `try-with-resources`.
2. **Modern HTTP Client (Lesson 0024):** HTTP/2 negotiation, immutable `HttpRequest`, asynchronous non-blocking dispatch with `sendAsync()`, timeouts, and standard `BodyHandlers`.
3. **Threads & The Java Memory Model (Lesson 0025):** The 6 thread states, CPU cache visibility, instruction reordering, the `volatile` modifier (visibility without atomicity), and lock-free CAS atomics with `AtomicInteger`.
4. **Synchronization & Explicit Locks (Lesson 0026):** Intrinsic monitors, reentrancy, `ReentrantLock` with guaranteed `try-finally` unlock, `tryLock` timeouts, and strict lock ordering for deadlock prevention.
5. **Concurrency Utilities & Executors (Lesson 0027):** `ThreadPoolExecutor` architecture, pool sizing rules (CPU vs I/O bound), `Callable`/`Future`, graceful shutdown lifecycle, and `ConcurrentHashMap`.
6. **Asynchronous Composition with CompletableFuture (Lesson 0028):** Non-blocking promise composition (`thenApply`, `thenCompose`, `thenCombine`), parallel joining with `allOf`, and resilient exception recovery with `exceptionally`.
7. **Java 21 Project Loom & Virtual Threads (Lesson 0029):** Lightweight M:N threads, carrier thread unmounting on blocking I/O, `Executors.newVirtualThreadPerTaskExecutor()`, the "never pool virtual threads" rule, and thread pinning avoidance.

Progress: 29 / 35 Lessons Completed (83%).
Unlocks Stage 7 (JVM Internals, Memory Management & Performance: Lessons 0030–0032).
