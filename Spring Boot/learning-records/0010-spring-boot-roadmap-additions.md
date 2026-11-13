# Learning Record 0010: roadmap.sh/spring-boot Gap Audit & 6 Spring Ecosystem Additions

**Timestamp:** 2026-11-12  
**Goal:** Cross-check the curriculum against `roadmap.sh/spring-boot`, skip obsolete/superseded items (JSP, OpenFeign, Eureka/Config Server, MongoDB standalone), and add the 6 high-ROI Spring Boot capabilities.

---

### Addition Placed in Part 1 (Phase 15B → 52 Core Lessons)
- `0065-spring-profiles-and-custom-aop-aspects.html`: Spring Profiles (`@Profile("!prod")`, `SPRING_PROFILES_ACTIVE` parity) and custom `@Aspect` / `@Around` ordered interceptors.

### Additions Placed in Part 2 (Phase 20 — Track 2E: Modern Spring Ecosystem Extensions → 18 Pro Lessons)
- `0066-graalvm-native-images-vs-appcds-aot.html`: GraalVM Native Images + `@RegisterReflectionForBinding` vs. HotSpot JVM + AppCDS / Leyden AOT tradeoffs.
- `0067-spring-ai-chatclient-rag-and-tool-calling.html`: Spring AI `ChatClient`, `.entity(...)` structured output, `QuestionAnswerAdvisor` (`pgvector` RAG), `@Tool`, and keeping LLM I/O outside `@Transactional`.
- `0068-spring-data-jdbc-and-jooq-alternatives.html`: Immutable Java Records / Kotlin data classes in Spring Data JDBC + compile-time type-safe SQL via jOOQ `DSLContext`.
- `0069-spring-cloud-gateway-routing-and-token-relay.html`: Spring Cloud Gateway Server WebMvc edge routing, untrusted header sanitization, `TokenRelay`, and CircuitBreaker fallbacks.
- `0070-spring-batch-chunk-processing-and-retries.html`: Chunk-oriented batch streaming, `JobRepository` checkpoint restartability, and `faultTolerant()` retry/skip rules.

### Total Curriculum State
- **Part 1:** 52 Lessons (`0001`–`0044`, `0046`, `0047`, `0049`, `0059`–`0062`, `0065`) + Part 1 Capstone.
- **Part 2:** 18 Lessons (`0050`–`0052`, `0045`, `0053`–`0055`, `0048`, `0056`–`0058`, `0063`–`0064`, `0066`–`0070`) + Part 2 Capstone.
- **Grand Total:** 70 Lessons across 20 Phases (210 active-recall quizzes).
