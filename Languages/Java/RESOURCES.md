# Senior Java, JVM, DSA, Spring Boot 3 & Distributed Systems Resources

## Knowledge

### Core Java 21 LTS, Concurrency & JVM Profiling
- [Dev.java — The Official Home for Java Developers](https://dev.java)
  Oracle's official portal for Java 21 tutorials, Collections, Streams, NIO.2, Virtual Threads, and modern language guides.
- [The Java Language Specification (Java SE 21 Edition)](https://docs.oracle.com/javase/specs/jls/se21/html/index.html)
  The normative specification for Java type rules, pattern matching, generics erasure, and the Java Memory Model (Chapter 17).
- [OpenJDK JEPs (JDK Enhancement Proposals)](https://openjdk.org/jeps/0)
  Primary design documents for Records (JEP 395), Sealed Classes (JEP 409), Sequenced Collections (JEP 431), Record Patterns (JEP 440), Pattern Matching for `switch` (JEP 441), Generational ZGC (JEP 439), Virtual Threads (JEP 444), Structured Concurrency, and Scoped Values.
- [Book: _Effective Java (3rd Edition)_ — Joshua Bloch](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
  Definitive engineering guide to `equals`/`hashCode`, immutability, defensive copying, PECS generics, enums, and serialization traps.
- [Book: _Java Concurrency in Practice_ — Brian Goetz et al.](https://jcip.net/)
  Foundational reference on happens-before visibility, lock ordering, thread-pool sizing, and concurrent data structures.
- [Aleksey Shipilëv — JVM Anatomy Quarks](https://shipilev.net/jvm/anatomy-quarks/)
  Deep-dive articles by an OpenJDK engineer on Compressed OOPs, TLABs, Escape Analysis, Safepoints, Lock Inflation, and GC barriers.
- [Async-Profiler & JDK Flight Recorder (JFR) Guide](https://github.com/async-profiler/async-profiler)
  Industry-standard low-overhead CPU, allocation, lock, and flame-graph profiling on production HotSpot JVMs without safepoint bias.

### Data Structures & Algorithms (DSA) in Java
- [NeetCode 150 / Roadmap (Java)](https://neetcode.io/roadmap)
  Structured progression through the core interview problem patterns with Java solutions and complexity analyses.
- [Book: _Algorithms (4th Edition)_ — Robert Sedgewick & Kevin Wayne (Princeton)](https://algs4.cs.princeton.edu/home/)
  Rigorous textbook covering sorting (Quicksort/Mergesort), heaps, tries, balanced search trees, Union-Find, and graph algorithms in Java.
- [CP-Algorithms — Prefix Sums, String Hashing & Graphs](https://cp-algorithms.com/)
  Clear mathematical derivations and edge-case proofs for Rabin-Karp rolling hash, KMP prefix functions, Dijkstra, and DSU.

### Spring Boot 3, JPA/Database Concurrency & Resilience
- [Spring Boot 3 Official Reference Documentation](https://docs.spring.io/spring-boot/index.html)
  Canonical documentation for auto-configuration, `@ConfigurationProperties`, `RestClient`, Actuator, Testcontainers (`@ServiceConnection`), and layered Docker builds.
- [Spring Security 6 Reference — OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
  Official guide to stateless JWT/JWKS validation, `SecurityFilterChain`, and `@PreAuthorize` method security.
- [Vlad Mihalcea — High-Performance Java Persistence](https://vladmihalcea.com/tutorials/hibernate/)
  Authoritative deep dives on JDBC batching, HikariCP sizing, JPA entity states, N+1 prevention, `@Version` optimistic vs. `FOR UPDATE SKIP LOCKED` pessimistic locking, and ACID isolation anomalies.
- [Resilience4j Official Documentation](https://resilience4j.readme.io/)
  Reference manual for Circuit Breakers, Bulkheads, Rate Limiters, TimeLimiters, and jittered Retries in Spring Boot 3.
- [Caffeine Cache Wiki — Architecture & W-TinyLFU](https://github.com/ben-manes/caffeine/wiki)
  Design and configuration guide for high-hit-rate, stampede-safe in-memory caching on the JVM.
- [Flyway Documentation — Zero-Downtime Database Migrations](https://documentation.red-gate.com/fd)
  Best practices for versioned SQL migrations, checksums, and expand-and-contract schema evolution.
- [OpenTelemetry Java & Micrometer Tracing Docs](https://docs.micrometer.io/tracing/reference/)
  Production telemetry guide for W3C `traceparent` propagation, `ObservationRegistry`, and SLF4J `MDC` log correlation.

### Distributed Streaming & Big Data (Kafka, Spark, Flink)
- [Book: _Kafka: The Definitive Guide (2nd Edition)_ — Gwen Shapira et al.](https://www.confluent.io/resources/kafka-the-definitive-guide-2nd-edition/)
  Deep coverage of KRaft, ISR replication, idempotent producers, consumer group rebalancing, and delivery guarantees.
- [Spring for Apache Kafka Reference](https://docs.spring.io/spring-kafka/reference/)
  Official guide for `KafkaTemplate` async futures, `@RetryableTopic`, Dead Letter Topics, and consumer error handling.
- [Debezium — Transactional Outbox Pattern](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html)
  Production blueprint for atomic database + Kafka event publishing via Change Data Capture (CDC).
- [Apache Spark SQL & Dataset Programming Guide (Java)](https://spark.apache.org/docs/latest/sql-programming-guide.html)
  Official reference for `Dataset<Row>`, Catalyst optimization, broadcast joins, and Tungsten memory management.
- [Apache Flink Documentation — Stateful Stream Processing](https://nightlies.apache.org/flink/flink-docs-stable/docs/concepts/time/)
  Authoritative guide to Event Time, `WatermarkStrategy`, `KeyedProcessFunction`, `ValueState`, RocksDB, and Chandy-Lamport checkpoints.

---

## Wisdom (Communities)

- [r/java](https://www.reddit.com/r/java) — High-signal discussions with OpenJDK contributors, library authors, and senior JVM architects.
- [Together Java Discord](https://discord.gg/together-java) — Real-time Java engineering community with code-review and architecture channels.
- [Spring Community & GitHub Discussions](https://spring.io/community) — Direct Q&A and architectural discussions around the Spring ecosystem.
- [Confluent Community Slack](https://confluentcommunity.slack.com/) — Global community of Apache Kafka, Flink, and event-driven systems practitioners.
