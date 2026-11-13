# Curriculum Upgrade: Roadmap.sh Gap Closure, Track Reordering & Java 25 LTS / Spring Boot 4.0 Modernization

## Purpose
Documents the architectural curriculum upgrade aligning this workspace with `roadmap.sh/java` and the 2025–2027 production Java ecosystem, tailored for a junior Android developer fluent in reading Kotlin who is advancing to backend Java mastery.

## Learner State & Verified Baseline
- **Active position:** Ready to start main study at Lesson `0002` (following Kotlin bridge orientation in `0074` immediately afterward).
- **Local runtime verified (`2026-11-12`):** OpenJDK `25.0.4.1` LTS at `/opt/homebrew/opt/openjdk@25/bin/{java,javac,javadoc}` and Gradle `9.7.1`. All flagship Java 25 JEPs taught in the new curriculum (`JEP 512`, `JEP 511`, `JEP 513`, `JEP 485`, `JEP 510`, `JEP 467`) were compiled and executed directly against the local JDK 25 installation.

## 1. Roadmap Additions (`0074`–`0081` + Kotlin Bridge Callouts)
Eight full interactive lessons (`16` exercises, `24` quiz questions) and paired cheat-sheet reference files were added and integrated at natural prerequisite points along the main path:
- `0074` (`lessons/0074-kotlin-to-java-mental-model-bridge.html`): Kotlin-to-Java 25 mental-model bridge (`==` vs `.equals()`, checked exceptions, `static` vs `companion object`, `List.copyOf` vs Kotlin read-only interfaces, `record` vs `data class`, nullable refs vs `T?`/JSpecify, Virtual Threads vs Coroutines).
- `0075` (`lessons/0075-packages-access-inner-classes-and-initializers.html`): Packages, module imports (`JEP 511`), 4-tier access control (`package-private` vs Kotlin `internal`), static nested vs non-static `this$0` inner classes, initializers, flexible constructor bodies (`JEP 513`), and classic Builders.
- `0076` (`lessons/0076-java25-compact-sources-and-stream-gatherers.html`): Java 25 compact source files & `java.lang.IO` (`JEP 512`), module imports (`JEP 511`), and custom intermediate stream operations via Stream Gatherers (`JEP 485`).
- `0077` (`lessons/0077-regular-expressions-and-redos-safety.html`): Pre-compiled `Pattern`/`Matcher`, named capturing groups, possessive quantifiers/atomic groups, and ReDoS defense.
- `0078` (`lessons/0078-classic-java-io-and-tcp-sockets.html`): Classic `java.io` byte/character decorator chains, UTF-8 encoding safety, framed `BufferedReader`/`BufferedWriter` protocols, and virtual-thread-friendly `Socket`/`ServerSocket` timeouts.
- `0079` (`lessons/0079-java-cryptography-kdf-and-password-hashing.html`): JCA `SecureRandom`, `MessageDigest.isEqual`, authenticated `AES/GCM/NoPadding`, standard Key Derivation Functions (`JEP 510` `javax.crypto.KDF`), and memory-hard password hashing (`Argon2id`/`BCrypt`).
- `0080` (`lessons/0080-javadoc-and-production-logging-facades.html`): Markdown `///` Javadoc (`JEP 467`), `SLF4J`/`Logback` vs JDK `System.Logger`, parameterized logging, and structured production log discipline.
- `0081` (`lessons/0081-spring-boot-4-and-modern-web-ecosystem.html`): Spring Boot 4.0 / Spring Framework 7.0 flagship delta alongside backend ecosystem comparisons (Quarkus, Javalin).

Targeted Android/Kotlin Mental-Model Bridge callout boxes were also added directly inside core lessons `0006`, `0008`, `0012`, `0013`, `0014`, `0022`, `0032`, and `0033`.

## 2. Track Demotions (Main Track vs. Optional Specializations)
To keep momentum high toward production Java 25 and Spring Boot 4.0 backend delivery:
- **DSA Mastery (`0041`–`0053`)** was demoted to **`[Optional Track A]`** (placed after Core Java + Spring Boot 4.0 + Kafka).
- **Big Data Batch & Stream Processing (`0071`–`0073` — Spark & Flink)** was demoted to **`[Optional Track B]`** at the end of the curriculum.
- `Prev`/`Next` navigation links across all 81 lessons and reference sheets follow the unified `CURRICULUM_ORDER` graph ending at `0073` -> Master Plan (`0001`).

## 3. Baseline Upgrade (Java 25 LTS & Spring Boot 4.0 / 2027 Stack)
- Upgraded Core & Build curriculum from Java 21 / Gradle 8 defaults to **Java 25 LTS** (`--release 25`, classfile major version `69`, `JavaLanguageVersion.of(25)`, Gradle `9`), while preserving accurate historical distinctions where features originated in JDK 21 (`JEP 431`, `JEP 439`, `JEP 440`, `JEP 441`, `JEP 444`, JDK 21–23 `synchronized` pinning vs JDK 24/25 `JEP 491`, and JDK 21 preview `StructuredTaskScope` vs JDK 25 `JEP 505`).
- Upgraded Framework lessons (`0054`–`0070`) and portal docs from Spring Boot 3 / Framework 6 / Security 6 / Hibernate 6 / Servlet 6.0 / Jackson 2 to **Spring Boot 4.0 / Spring Framework 7.0 / Spring Security 7 / Hibernate 7 (JPA 3.2) / Jakarta Servlet 6.1 / Jackson 3 (`tools.jackson.*`) / JSpecify (`@NullMarked`/`@Nullable`) / `@MockitoBean` / `RestTestClient` & REST Assured / `org.springframework.boot.health.contributor.HealthIndicator`**.
