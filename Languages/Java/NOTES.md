# Teaching Notes

- **Learner Profile & Target Bar (2027 Roadmap):**
  - **Junior Android developer** who can read some Kotlin, transitioning to active writing, debugging, and production architecture in **Java 25 LTS** and **Spring Boot 4.0 / Spring Framework 7.0**.
  - Needs Kotlin-to-Java mental-model bridging early (`0074`), deliberate coding from scratch, and production debugging intuition rather than passive reading.
- **Explicit Interactive Lesson Requirements (Mandatory in Every Coding Lesson HTML):**
  1. **Production Code Examples:** Minimum 3 code blocks per lesson contrasting naive/buggy code (or Kotlin traps), under-the-hood mechanics, and idiomatic Java 25 solutions.
  2. **Runnable & Checkable In-HTML Code Practices (`assets/workbench.js`):**
     - 2 interactive workbenches per coding lesson (`#ex-1` Implementation Drill + `#ex-2` Bug-Fix / Refactor Challenge).
     - **Never pre-solved**: starter code must require genuine edits to pass checks and compile cleanly as realistic buggy code or clean partial stubs where appropriate.
     - Includes **Run & Check Code** (automated checks + simulated JVM output), **Progressive Hints**, and **Show Final Answer**.
  3. **Multiple-Choice Code Quizzes (`assets/quiz.js`):**
     - 3 questions per lesson with balanced option lengths (`wc_spread <= 3`, `cc_spread <= 35`), balanced correct positions (`A`–`D`), and full per-option explanations.
- **81-Lesson Curriculum Structure (Roadmap-Aligned Order):**
  - **Main Track — Phase 1 (`0001` Hub + 46 Lessons: `0002`–`0040`, `0074`–`0080`):** Core Java 25 LTS, Kotlin Bridge (`0074`), Packages/Inner Classes/JEP 513 (`0075`), Strings & Regex/ReDoS (`0077`), Collections/Streams & JEP 512/511/485 Gatherers (`0076`), Classic `java.io`/TCP Sockets (`0078`), NIO.2/HTTP, Cryptography/AES-GCM/JEP 510 KDF (`0079`), Markdown Javadoc JEP 467 & SLF4J/Logback (`0080`), JMM, Virtual Threads (JEP 491), `StructuredTaskScope`/`ScopedValue` (JEP 505/506), Generational ZGC, JFR/Async-Profiler, Gradle 9/Maven, JUnit 5/Mockito/REST Assured, JPMS.
  - **Main Track — Phase 2 (6 Lessons: `0054`–`0059`):** Enterprise Internals & Persistence (Proxies/AOP, Jackson 3 Type Tokens, JDBC/HikariCP, JPA/Hibernate 7, ACID Locking & Flyway, Servlet 6.1 & Tomcat Virtual Threads).
  - **Main Track — Phase 3 (12 Lessons: `0060`–`0070`, `0081`):** Spring Boot 4.0, Framework 7.0 (`@Retryable`, API Versioning, `RestTestClient`), JSpecify, Spring Security 7, OpenTelemetry Starter, Spring Boot 4 Ecosystem & Quarkus/Javalin (`0081`), plus Event-Driven Kafka (`0069`–`0070`).
  - **`[Optional Track A]` — Coding Interviews & DSA (`0041`–`0053`, 13 Lessons):** Demoted after Backend Mastery for interview prep without blocking core Spring Boot progress.
  - **`[Optional Track B]` — Distributed Data & Streaming Engines (`0071`–`0073`, 3 Lessons):** Demoted optional specialty track for Apache Spark & Apache Flink.
