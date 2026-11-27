# Spring Boot (Java 25 LTS & Kotlin 2.2+) Official Resources

## Primary Documentation (2027 Stack Baseline)

- [Spring Boot 4 Official Reference Documentation](https://docs.spring.io/spring-boot/reference/)
  Authoritative source for Spring Boot 4.2 starters (`spring-boot-starter-webmvc`, `spring-boot-starter-opentelemetry`, `spring-boot-docker-compose`), structured logging, CDS/AOT packaging, and Actuator probes.
- [Spring Framework 7 Core & WebMvc Reference](https://docs.spring.io/spring-framework/reference/)
  Authoritative reference for IoC/DI, JSpecify `@NullMarked` nullability contracts, native API versioning (`ApiVersionConfigurer`), `@ImportHttpServices`, `@EnableResilientMethods`, and `RestTestClient`.
- [Spring Security 7 Reference](https://docs.spring.io/spring-security/reference/)
  Authoritative guide for `SecurityFilterChain`, OAuth2 Resource Servers, PKCE, native WebAuthn Passkeys (`http.webAuthn(...)`), mTLS, `@PreAuthorize` method security, and `Argon2PasswordEncoder`.
- [OpenJDK JDK 25 LTS Documentation](https://openjdk.org/projects/jdk/25/)
  Official specifications for Virtual Threads monitor unmounting (JEP 491), `ScopedValue` (JEP 506), Compact Object Headers (JEP 519), Project Leyden AOT Cache, and Generational ZGC.
- [High-Performance Java Persistence — Vlad Mihalcea](https://vladmihalcea.com/)
  Industry gold standard for Hibernate ORM, PostgreSQL MVCC/locking, N+1 prevention, and HikariCP sizing.
- [Official Kotlin Documentation for Spring](https://kotlinlang.org/docs/jvm-spring-boot-restful.html)
  Authoritative guide on Kotlin 2.2+ with Spring Boot, compiler plugins (`plugin.spring`, `plugin.jpa`), JSpecify null-safety, and Coroutines.
- [Testcontainers Official Guides](https://testcontainers.com/guides/)
  Modern standard for integration testing against real disposable PostgreSQL 17, Redis 7, Kafka, and LocalStack containers via `@ServiceConnection`.
- [Use The Index, Luke! — Markus Winand](https://use-the-index-luke.com/)
  Benchmark reference for PostgreSQL B-Tree indexes, `EXPLAIN (ANALYZE, BUFFERS)`, and keyset pagination (`SEEK` method).
- [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html)
  IETF standard HTTP error envelope natively supported via Spring's `ProblemDetail`.
- [Spring AI Reference Documentation](https://docs.spring.io/spring-ai/reference/)
  Official reference for `ChatClient`, Structured Output, `pgvector` RAG Advisors, `@Tool`, and Stateless Streamable-HTTP MCP servers (`@McpTool`).

## Practitioner Communities

- [Spring Boot Discussions & Release Notes](https://github.com/spring-projects/spring-boot/wiki)
  Official Spring Boot 4.x migration guides, release notes, and engineering discussions.
- [r/java](https://www.reddit.com/r/java/)
  High-signal discussions on JDK 25 LTS, Project Loom, Project Leyden, JSpecify, and JVM profiling.
- [r/springboot](https://www.reddit.com/r/springboot/)
  Practitioner community for Spring Boot architecture, testing, and production troubleshooting.
- [Kotlinlang Public Slack (#spring channel)](https://slack.kotlinlang.org/)
  Active community for idiomatic Kotlin + Spring Boot 4 patterns.
