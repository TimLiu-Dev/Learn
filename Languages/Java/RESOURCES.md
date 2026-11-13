# Senior Java 25 LTS, Spring Boot 4.0 & Distributed Systems Resources

## Knowledge

### Core Java 25 LTS, Kotlin Bridge & Modern Language JEPs
- [Dev.java — The Official Home for Java Developers](https://dev.java)
  Oracle's official portal for modern Java tutorials, Collections, Streams, Virtual Threads, cryptography, and JDK tooling.
- [The Java Language Specification (Java SE 25 Edition)](https://docs.oracle.com/javase/specs/jls/se25/html/index.html)
  Normative specification for Java 25 type rules, pattern matching, flexible constructor bodies, generic erasure, and Chapter 17 JMM.
- [JDK 25 Release Notes & OpenJDK JEPs](https://openjdk.org/projects/jdk/25/)
  Primary specs for Compact Source Files & `java.lang.IO` (JEP 512), Flexible Constructor Bodies (JEP 513), Module Imports (JEP 511), Stream Gatherers (JEP 485), Key Derivation Function API (JEP 510), Markdown Javadoc (JEP 467), Structured Concurrency (JEP 505), Scoped Values (JEP 506), and Generational ZGC (JEP 490).
- [Kotlin for Java Developers & Interop Guide (Kotlin Docs)](https://kotlinlang.org/docs/java-to-kotlin-interop.html)
  Official mapping of Kotlin nullability, JSpecify interop, `data class` vs `record`, checked exceptions, companion statics, and read-only collection views.
- [Book: _Effective Java (3rd Edition)_ — Joshua Bloch](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
  Definitive engineering guide to `equals`/`hashCode`, immutability, defensive copying, PECS generics, enums, and API design.
- [Book: _Java Concurrency in Practice_ — Brian Goetz et al.](https://jcip.net/)
  Foundational reference on happens-before visibility, lock ordering, thread-pool sizing, and concurrent collections.
- [Aleksey Shipilëv — JVM Anatomy Quarks](https://shipilev.net/jvm/anatomy-quarks/)
  Deep-dive OpenJDK articles on Compressed OOPs, TLABs, Escape Analysis, Safepoints, Lock Inflation, and GC barriers.
- [Async-Profiler & JDK Flight Recorder (JFR) Guide](https://github.com/async-profiler/async-profiler)
  Low-overhead CPU, allocation, lock, and flame-graph profiling on production HotSpot JVMs.

### Spring Boot 4.0, Framework 7.0, Persistence & Testing
- [Spring Boot 4.0 Official Reference Documentation](https://docs.spring.io/spring-boot/index.html)
  Canonical guide to modular starters, `@ConfigurationProperties`, Jackson 3 (`tools.jackson`), JSpecify null safety, OpenTelemetry starter, and Testcontainers.
- [Spring Framework 7.0 Reference — Resilience, API Versioning & `RestTestClient`](https://docs.spring.io/spring-framework/reference/)
  Official docs for core `@Retryable`/`@ConcurrencyLimit`, native MVC/WebFlux API versioning, and fluent blocking `RestTestClient`.
- [Spring Security 7 Reference — OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
  Official guide to stateless JWT/JWKS validation, `SecurityFilterChain`, Servlet 6.1 alignment, and `@PreAuthorize`.
- [JSpecify User Guide (`@NullMarked` / `@Nullable`)](https://jspecify.dev/docs/user-guide/)
  Standardized JVM static nullness annotations adopted across Spring Boot 4, Spring Framework 7, and NullAway.
- [Vlad Mihalcea — High-Performance Java Persistence](https://vladmihalcea.com/tutorials/hibernate/)
  Deep dives on JDBC batching, HikariCP sizing, Hibernate 7 / JPA 3.2 states, N+1 fixes, and optimistic vs pessimistic locking.
- [REST Assured & Quarkus / Javalin Docs](https://rest-assured.io/)
  Ecosystem alternatives and fluent REST contract testing highlighted alongside Spring Boot on `roadmap.sh/java` ([Quarkus](https://quarkus.io/), [Javalin](https://javalin.io/)).
- [Flyway Documentation — Zero-Downtime Migrations](https://documentation.red-gate.com/fd)
  Best practices for versioned SQL migrations, checksums, and expand-and-contract schema evolution.
- [OpenTelemetry Java & Micrometer Tracing Docs](https://docs.micrometer.io/tracing/reference/)
  W3C `traceparent` propagation, `ObservationRegistry`, SLF4J 2 + Logback, and MDC log correlation.

### Optional Tracks: DSA Coding Interviews & Distributed Streaming (Kafka, Spark, Flink)
- [NeetCode 150 / Roadmap (Java)](https://neetcode.io/roadmap)
  Structured interview problem progression for Optional Track A (`0041`–`0053`).
- [Book: _Kafka: The Definitive Guide (2nd Edition)_ — Gwen Shapira et al.](https://www.confluent.io/resources/kafka-the-definitive-guide-2nd-edition/)
  Deep coverage of KRaft, ISR replication, idempotent producers, and consumer rebalancing (`0069`–`0070`).
- [Spring for Apache Kafka Reference](https://docs.spring.io/spring-kafka/reference/)
  Official guide for `KafkaTemplate` async futures, `@RetryableTopic`, Dead Letter Topics, and Outbox (`0070`).
- [Apache Spark SQL & Apache Flink Documentation](https://spark.apache.org/docs/latest/sql-programming-guide.html)
  Reference guides for Optional Track B (`0071`–`0073`): Catalyst `Dataset<Row>` / Tungsten and [Flink stateful event-time processing](https://nightlies.apache.org/flink/flink-docs-stable/docs/concepts/time/).

---

## Wisdom (Communities)

- [r/java](https://www.reddit.com/r/java) — High-signal discussions with OpenJDK contributors, library authors, and senior JVM architects.
- [Together Java Discord](https://discord.gg/together-java) — Real-time Java engineering community with code-review and architecture channels.
- [Spring Community & GitHub Discussions](https://spring.io/community) — Direct Q&A and architectural discussions around Spring Boot 4 and Spring Framework 7.
- [Confluent Community Slack](https://confluentcommunity.slack.com/) — Practitioners building Apache Kafka and Flink streaming pipelines.
