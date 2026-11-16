# Concurrency Engineering Resources

## Knowledge (Part 1 · Core, Kotlin, Android & Distributed)

- [Kotlin Coroutines Guide (Official JetBrains Docs)](https://kotlinlang.org/docs/coroutines-guide.html)
  Primary reference for structured concurrency, `CoroutineContext`, dispatchers, cancellation, channels, and Flow. Use for: Lessons 06–12.
- [Kotlin Coroutine Context and Dispatchers](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
  Canonical specification for `Dispatchers.Default`, `Dispatchers.IO`, `Main`, `withContext`, and `ThreadLocal.asContextElement`. Use for: Lessons 04, 09.
- [Kotlin Asynchronous Flow](https://kotlinlang.org/docs/flow.html)
  Official reference for cold flows, context preservation, `flowOn`, `buffer`, `conflate`, `StateFlow`, and `SharedFlow`. Use for: Lessons 11–12.
- [Android Developers: Best Practices for Coroutines in Android](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
  Official Android guidance on injecting dispatchers, main-safety, structured scopes, and avoiding `GlobalScope`. Use for: Lessons 07, 13–14.
- [Android Developers: Lifecycle-aware Coroutine Scopes & `repeatOnLifecycle`](https://developer.android.com/topic/libraries/architecture/coroutines)
  Authoritative guide on UI collection safety (`repeatOnLifecycle`, `collectAsStateWithLifecycle`) and `viewModelScope`. Use for: Lesson 14.
- [Android Developers: WorkManager Guide & `CoroutineWorker`](https://developer.android.com/topic/libraries/architecture/workmanager)
  Primary reference for deferrable, persistent background work surviving process death and Doze constraints. Use for: Lesson 16 & Capstone 01.
- [Android Developers: Room & SQLite WAL Mode](https://developer.android.com/training/data-storage/room)
  Official documentation on Room transactions, reactive DAO `Flow` queries, and SQLite Write-Ahead Logging concurrency. Use for: Lesson 15 & Capstone 01.
- [JSR-133: Java Memory Model and Thread Specification](https://jcp.org/en/jsr/detail?id=133)
  Formal specification of happens-before, `volatile`, `final` safe publication, and monitor locks on the JVM. Use for: Lessons 02–03.
- [JEP 444: Virtual Threads (OpenJDK)](https://openjdk.org/jeps/444)
  Primary specification for Java 21+ lightweight virtual threads and carrier thread mounting/unmounting. Use for: Lessons 17, 21.
- [Martin Kleppmann: "How to do distributed locking"](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html)
  Foundational critique explaining GC pauses, lease expiration races, and why fencing tokens at the storage layer are mandatory. Use for: Lesson 18 & Capstone 02.
- [CashApp Turbine & `kotlinx-coroutines-test`](https://github.com/cashapp/turbine)
  Industry-standard testing library and virtual-time patterns for deterministic Kotlin `Flow` verification. Use for: Lesson 13.

## Knowledge (Part 2 · Java 21–25+ & Spring Boot Senior Backend 2027)

- [JEP 491: Synchronize Virtual Threads without Pinning (OpenJDK)](https://openjdk.org/jeps/491)
  Java 24 specification moving monitor locks from carrier threads to virtual threads, eliminating `synchronized` pinning. Use for: Lesson 21.
- [Spring Boot Reference: Virtual Threads & Task Execution](https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html)
  Official Spring Boot documentation for `spring.threads.virtual.enabled=true`, `SimpleAsyncTaskExecutor`, `@Async`, and `TaskDecorator`. Use for: Lessons 21–22.
- [Spring Framework Reference: WebFlux & Reactive Core](https://docs.spring.io/spring-framework/reference/web/webflux.html)
  Official Reactor/WebFlux guide covering event-loop concurrency, `Schedulers.boundedElastic()`, and non-blocking backpressure. Use for: Lesson 23.
- [Spring Data JPA: Locking & Transactions](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html)
  Canonical guide for `@Version` optimistic locking, `@Lock(LockModeType.PESSIMISTIC_WRITE)`, and HikariCP connection boundaries. Use for: Lesson 24 & Capstone 03.
- [Spring for Apache Kafka Reference](https://docs.spring.io/spring-kafka/reference/)
  Official documentation on `ConcurrentMessageListenerContainer`, partition concurrency, manual acks, and `@TransactionalEventListener` outbox patterns. Use for: Lesson 25 & Capstone 03.
- [Resilience4j Official Documentation](https://resilience4j.readme.io/docs)
  Primary guide for Bulkheads, Rate Limiters, Circuit Breakers, and Spring Boot graceful shutdown integration. Use for: Lesson 26 & Capstone 03.

## Wisdom (Communities)

- [Kotlin Slack (`#coroutines` channel)](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
  Direct discussions with JetBrains coroutines maintainers and senior Android/Kotlin engineers.
- [r/androiddev](https://www.reddit.com/r/androiddev/) & [r/java](https://www.reddit.com/r/java/)
  Engineering communities for production war stories, Compose/Room patterns, and Java Virtual Threads / Spring Boot benchmarks.
