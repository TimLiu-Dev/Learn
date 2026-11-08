# Spring Boot (Java & Kotlin) Resources

## Knowledge

- [Spring Boot Official Reference Documentation](https://docs.spring.io/spring-boot/reference/)
  Official source of truth for configuration properties, auto-configuration mechanisms, starters, actuator endpoints, and runtime lifecycle. Use for verifying behavior and exact property keys.
- [Spring Framework Core Reference](https://docs.spring.io/spring-framework/reference/core.html)
  Primary reference for Inversion of Control (IoC), ApplicationContext, bean lifecycles, and Spring Expression Language (SpEL). Use for foundational architectural principles.
- [High-Performance Java Persistence — Vlad Mihalcea](https://vladmihalcea.com/)
  The industry gold standard for Hibernate and Spring Data JPA internals. Use for query optimization, entity relationship pitfalls (e.g. N+1 queries, `@ManyToOne` vs `@ManyToMany`), and transaction management.
- [Official Kotlin Documentation for Spring](https://kotlinlang.org/docs/jvm-spring-boot-restful.html)
  Authoritative guide on using Kotlin with Spring Boot. Use for Kotlin-specific idioms, data class entity considerations, null-safety mappings, and coroutines in Spring WebFlux / MVC.
- [Testcontainers Official Guides](https://testcontainers.com/guides/)
  Modern standard for integration testing with real disposable databases and message brokers in Docker. Use for integration test architecture.
- [Use The Index, Luke! — Markus Winand](https://use-the-index-luke.com/)
  The industry benchmark for relational database indexing, execution plan mechanics (`EXPLAIN`), and SQL performance for developers.
- [The Twelve-Factor App](https://12factor.net/)
  The foundational architectural principles for building cloud-native, containerized, stateless backend services.
- [Spring Boot Kubernetes Guide](https://docs.spring.io/spring-boot/reference/deployment/cloud.html#cloud-deployment.kubernetes)
  Official documentation on Kubernetes container lifecycle, liveness/readiness probes, and graceful shutdown.
- [RFC 7807 / RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html)
  Industry standard HTTP error specification natively adopted in Spring Boot 3. Use for REST API error modeling.

## Wisdom (Communities)

- [Spring Boot Discussions on GitHub](https://github.com/spring-projects/spring-boot/discussions)
  High-signal interactions with the Spring engineering team and ecosystem contributors. Use for design discussions, edge cases, and upcoming framework shifts.
- [r/java](https://www.reddit.com/r/java/)
  Moderated subreddit focused on modern Java (JDK 21/25, JVM internals, performance, ecosystem news). Use for language evolution insights.
- [r/springboot](https://www.reddit.com/r/springboot/)
  Focused community for Spring Boot backend developers. Use for architecture reviews, library comparisons, and production troubleshooting.
- [Kotlinlang Public Slack (#spring channel)](https://slack.kotlinlang.org/)
  Active practitioner community discussing Kotlin backend architecture and Spring Boot integration patterns. Use for idiomatic Kotlin advice.
