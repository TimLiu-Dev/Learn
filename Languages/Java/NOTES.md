# Teaching Notes

- **Learner Profile & Target Bar (2027 Roadmap):**
  - **Junior Android developer** who can read some Kotlin (starting fresh from zero active Java writing at Lesson `0002`), transitioning to active writing, debugging, and production architecture in **Java 25 LTS** and **Spring Boot 4.0 / Spring Framework 7.0** on Linux production servers.
  - Uses Ground-Zero Kotlin Bridge (`0074` with zero premature syntax jumps) + 39+ Android/Kotlin bridge callouts across Core Java and Spring lessons, deliberate coding from scratch, and multi-file IDE practice in `projects/springboot4-order-service/`.
- **Interactive Lesson & UX Capabilities (`assets/workbench.js`, `assets/quiz.js`, `assets/style.css`):**
  1. **Production Code Examples:** Minimum 3 code blocks per lesson contrasting naive/buggy code (or Kotlin traps), under-the-hood mechanics, and idiomatic Java 25 / Spring Boot 4 solutions.
  2. **Runnable & Autosaved In-HTML Workbenches (`180` drills across `90` coding lessons):**
     - 2 interactive workbenches per coding lesson (`#ex-1` Implementation Drill + `#ex-2` Bug-Fix / Refactor Challenge), never pre-solved, with order-independent and variable-name-flexible checks.
     - Automatically saves draft code, passed drill/quiz state, and lesson completion in `localStorage` (`agy_java_progress_v1`), feeding the live Progress Bar, Track Filters, and **"Start / Resume Next Lesson"** button on Lesson `0001` Hub.
  3. **Balanced Retrieval Quizzes (`273` questions across `91` lessons):**
     - 3 questions per lesson (`wc_spread <= 3`, `cc_spread <= 35`) with full per-option explanations; options remain clickable on revisit to support spaced retrieval practice while showing a `✓ Mastered` badge.
- **91-Lesson Curriculum Structure:**
  - **Main Track — Phase 1 (`0001` Hub + `46` Core Lessons: `0002`–`0040`, `0074`–`0080`):** Core Java 25 LTS, Ground-Zero Kotlin Bridge (`0074`), OOP/Packages/JEP 513 (`0075`), `HashMap` Treeification & Collections (`0019`), Streams & JEP 511/512/485 Gatherers (`0076`), Regex (`0077`), Classic I/O & Sockets (`0078`), NIO.2/HTTP, JCA/AES-GCM/JEP 510 KDF (`0079`), Markdown Javadoc & SLF4J (`0080`), JMM, Virtual Threads (JEP 491), `StructuredTaskScope` (`--enable-preview` JEP 505) & `ScopedValue` (JEP 506), ZGC, JFR, Gradle 9, JUnit 5.
  - **Main Track — Phase 2 (`6` Lessons: `0054`–`0059`):** Pure runtime mechanics: JDK/ByteBuddy proxies, Jackson 3 type tokens, JDBC/HikariCP, JPA 3.2 / Hibernate 7 `EntityManager` states, ACID locking + `@Transactional` traps + Flyway, Servlet 6.1 + Tomcat Virtual Threads.
  - **Main Track — Phase 3 (`19` Lessons: `0060`, `0061`, `0091`, `0062`, `0081`, `0063`, `0087`, `0064`, `0065`, `0088`, `0066`, `0083`, `0067`, `0089`, `0068`, `0090`, `0069`, `0070`, `0085`):** Ordered critical path including Local Gradle + `compose.yaml` + `JdbcClient` Starter Lab (`0091` + `projects/springboot4-order-service/`).
  - **`[Optional Track A]` (`0041`–`0053`, `13` Lessons):** DSA Coding Interviews.
  - **`[Optional Track B]` (`0071`–`0073`, `3` Lessons):** Apache Spark & Apache Flink Stateful Streaming.
  - **`[Optional Track C]` (`0082`, `0086`, `0084`, `3` Lessons):** Off-Heap FFM & Leyden AOT Cache, gRPC/Protobuf & GraphQL `@BatchMapping`, and `Spring AI` RAG & `@Tool` Calling.
