# Design Patterns, Senior Android & Java Spring Boot Resources

## Knowledge

- [Guide: Android App Architecture & Domain Layer — Android Developers](https://developer.android.com/topic/architecture)
  Official guidance on UI/Domain/Data layering, Use Cases, Dependency Inversion with Hilt, and Offline-First SSOT. Use for: Part 1 (L0001, L0005, L0019–L0023).
- [Catalog: Design Patterns & Refactoring — Refactoring.Guru](https://refactoring.guru/design-patterns)
  Canonical visual catalog of the 22 Gang of Four patterns, code smells, and "Replace Inheritance with Delegation" refactorings. Use for: L0003–L0018.
- [Docs: Kotlin Delegation (`by`)](https://kotlinlang.org/docs/delegation.html) & [Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html) & [Flow](https://kotlinlang.org/docs/coroutines-flow.html)
  Official Kotlin references for delegation, algebraic hierarchies, and reactive `StateFlow`/`SharedFlow`. Use for: Part 1.
- [Docs: Oracle Java 21+ Documentation & Pattern Matching](https://docs.oracle.com/en/java/javase/21/language/pattern-matching.html) & [Sealed Classes](https://docs.oracle.com/javase/tutorial/java/IandI/sealed.html)
  Official Java language specs for Records, Sealed hierarchies, Pattern Matching `switch`, and Virtual Threads (Project Loom). Use for: Part 2 (L0024, L0027).
- [Docs: Spring Boot Reference & Spring Modulith](https://docs.spring.io/spring-boot/reference/)
  Official Spring Boot documentation covering IoC/DI, Virtual Threads (`spring.threads.virtual.enabled=true`), `@Transactional` proxies, and modular event publication. Use for: Part 2 (L0024–L0033).
- [Docs: Resilience4j User Guide](https://resilience4j.readme.io/docs/getting-started)
  Official reference on CircuitBreaker, Retry, RateLimiter, Bulkhead, and Spring AOP aspect ordering. Use for: Part 2 (L0031, L0033).

## Wisdom (Communities)

- [Kotlin Official Slack (`kotlinlang`)](https://slack.kotlinlang.org/) — [Community Portal](https://kotlinlang.org/community/)
  Active `#android`, `#coroutines`, and `#multiplatform` channels for real-world architecture critiques.
- `r/androiddev`, `r/Kotlin`, & `r/java` / `r/SpringBoot` (Reddit)
  Practitioner discussions on Jetpack Compose, Spring Boot Virtual Threads, transaction pitfalls, and senior system-design prep.
