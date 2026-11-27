# Mission: Master Modern Java 25 LTS & Spring Boot 4.0 for 2027 Backend Engineering

## Why
Transition from reading beginner Kotlin on Android to writing, debugging, and architecting production backend systems in Java 25 LTS and Spring Boot 4.0. Master the modern JVM language, standard libraries, concurrency primitives, and enterprise Spring mechanics needed to thrive as a Java backend engineer in 2027, with focused optional tracks for coding interviews, big-data streaming, and native/polyglot/AI extensions.

## Success looks like
- Bridge Android/Kotlin mental models (`val`, `data class`, `when`, nullability, coroutines) cleanly into idiomatic Java 25 (`final`, `record`, exhaustive `switch`, `Optional`/JSpecify, Virtual Threads, `StructuredTaskScope`).
- Write clean core Java from scratch across OOP, packages/`package-private`, JEP 511/512/513/485, `HashMap` treeification & Collections, Streams, Regex, I/O & Sockets, Cryptography (AES-GCM & JEP 510 KDF), JUnit 5/AssertJ/Mockito, and SLF4J/Markdown Javadoc.
- Understand deep runtime mechanics behind frameworks: JDK/ByteBuddy proxies, Jackson 3 type tokens, JDBC/HikariCP pools, JPA 3.2 / Hibernate 7 entity lifecycles, ACID isolation, `@Transactional` traps & Flyway, plus JVM memory, Generational ZGC, JIT, and JFR profiling.
- Build, test, and operate real multi-file services (starting in `projects/springboot4-order-service/`) with Spring Boot 4.0 & Framework 7.0 (`JdbcClient`, `@EnableResilientMethods`, `@Retryable`, `@ConcurrencyLimit`, native API versioning, `RestTestClient`, `MockMvcTester`), Jackson 3 (`tools.jackson`), JSpecify null safety, `Spring Modulith` + `ArchUnit`, SQL `EXPLAIN ANALYZE`/replicas, SSE/WebSockets/signed Webhooks, Spring Security 7 OAuth2/JWT + OWASP/CORS/ETag, OpenTelemetry + Graceful Shutdown, Edge Gateways/CI/CD, Apache Kafka + Outbox + `KStream`/`KTable`, and Distributed Sagas / `ShedLock`.
- Optionally master LeetCode DSA patterns (`[Optional Track A]`), Apache Spark/Flink (`[Optional Track B]`), and FFM / Leyden AOT Cache / gRPC / GraphQL / `Spring AI` (`[Optional Track C]`) after completing the core backend path.

## Constraints
- Starting baseline: Junior Android developer who can read some Kotlin syntax, starting fresh with zero prior Java writing practice at Lesson `0002`.
- Local toolchain: OpenJDK `25.0.4.1` LTS (`/opt/homebrew/opt/openjdk@25/bin/{java,javac}`), Gradle `9.7.1`, IntelliJ IDEA; install OrbStack or Docker Desktop before Phase 3 `compose.yaml` / Testcontainers labs.
- Every coding lesson stays short and focused with `localStorage`-persisted Java workbenches, progressive hints, balanced 4-option retrieval quizzes, and a paired printable cheat sheet.

## Out of scope
- Android UI / Jetpack Compose / APK lifecycle development.
- Legacy Java EE / XML Spring configs / obsolete Java 8 patterns replaced by Java 25 LTS & Spring Boot 4.0.
