# Learning Record 0011: Pre-Course Master Plan & 74-Lesson Comprehensive Audit & Upgrade

**Timestamp:** 2026-11-21  
**Goal:** Audit, fix, reorganize, and expand the entire curriculum and interactive engine before starting Lesson 0001 so every lesson and phase meets the 2027 Senior Spring Boot Engineer (Java 21/25 & Kotlin 2.x) bar.

---

### 1. Master Plan Architectural Additions & Reorganization (74 Lessons Total: 56 Part 1 Core + 18 Part 2 Pro)
- **4 High-ROI New Lessons Added to Part 1:**
  - `0071-modern-java-21-25-for-kotlin-developers.html` (Phase 0): Modern Java 21/25 Records, Sealed Interfaces, guarded pattern-matching `switch` with record deconstruction, and Spring Boot 4 `JSpecify` (`@NullMarked` / `@Nullable`).
  - `0072-spring-boot-build-systems-gradle-maven-and-boms.html` (Phase 1): Gradle Kotlin DSL (`build.gradle.kts`) vs. Maven (`pom.xml`), `spring-boot-dependencies` BOM management, executable `bootJar` layout, and `BOOT-INF/layers.idx` container layer extraction.
  - `0073-dynamic-queries-jpa-specifications-and-auditing.html` (Phase 4): Composable dynamic filtering with `JpaSpecificationExecutor` (`Specification<T>`), `@EnableJpaAuditing` (`@CreatedDate`, `@LastModifiedDate`), and Hibernate 6.4+ `@SoftDelete`.
  - `0074-webhooks-hmac-verification-and-wiremock-testing.html` (Phase 11): Inbound webhook `HMAC-SHA256` verification on raw payload bytes with constant-time `MessageDigest.isEqual` and 300s replay windows, plus outbound `RestClient` contract testing with WireMock.
- **Upgraded Lesson `0049` (Phase 15 Finale):**
  - Deduplicated the JPA `data class` material already covered in `0015` and upgraded `0049` to cover structured concurrency fan-out with Kotlin Coroutines (`suspend fun`, `coroutineScope`, `async`, `MDCContext()`) vs. Java 21/25 Virtual Threads, plus idiomatic Kotlin testing with `MockK` & `SpringMockK` (`coEvery`, `@MockkBean`).
- **Eliminated the Disjointed "Phase 15B" Grab-Bag:**
  - Moved `0065` (Spring Profiles & Custom AOP `@Aspect`) into **Phase 1**.
  - Moved `0059` (Edge HTTP: Reverse Proxies, CORS & ETag) and `0060` (Real-Time Backends: SSE vs. WebSockets & Redis Fan-Out) into **Phase 2**.
  - Moved `0061` (Database Scaling: CAP/PACELC, Partitioning, Sharding & NoSQL) into **Phase 3**.
  - Moved `0062` (Application Security Hardening: Argon2id/BCrypt & OWASP API Top 10) into **Phase 8**.
  - Re-chained all 74 lessons' `← Previous` and `Next →` navigation buttons and phase badges to match the natural Phase 0 → Phase 20 progression.

### 2. Engine & Individual Lesson Fixes Across All 74 Lessons
- **Progress Sync (`index.html` ↔ `assets/app.js`):** Unified `localStorage` keys (`spring_mastery_progress` and `sb_mastery_done_<trackId>`) and fixed the `.lesson-card` double-brace CSS typo in `index.html`.
- **Prevented Starter Template False-Pass Bug (`assets/app.js`):** Added `stripStarterComments(code, starterCode)` in `assets/app.js` so instruction comments and `TODO` lines in untouched starter templates never trigger false-positive regex passes across 23 lessons.
- **Fixed All 7 Broken Lab Validators (`0004, 0007, 0011, 0028, 0030, 0031, 0034`):** Fixed the JS syntax error in `0030`, Kotlin `@field:` validation annotations in `0004`/`0007`, formula parentheses in `0011`, multi-line `.build()` in `0028`/`0031`, and multi-line `@SchedulerLock` matching in `0034`.
- **Escaped Swallowed HTML Pseudo-Tags (`0015, 0016, 0028, 0029, 0035`):** Escaped `List<OrderEntity>`, `List<OrderSummary>`, `Bearer <token>`, and `jcmd <pid>`.
- **Balanced All 222 Active-Recall Quizzes & Varied Answer Positions:** Permuted `[1, 1, 1]` option ordering in `0001`–`0031` (and `[2, 2, 2]` in `0036`/`0047`), balanced option word counts across `0004`–`0048`, replaced generic `"Follows production patterns..."` solution breakdowns in `0032`–`0048` with tailored architectural explanations, and fixed duplicate hints and doubled teacher prompts.
