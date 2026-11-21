# Teaching Notes & User Preferences

## Learner Profile & Elevated Target
- **Current Role:** Junior Android Developer (Kotlin).
- **Target Role:** **Senior Spring Boot Backend Developer (2027 Industry Standard)**.
- **Starting Point:** Strong in Kotlin/Android client concepts; starting from zero on server-side architecture.
- **Key Pedagogical Directives:**
  - **No abstract knowledge hubs:** Teach through sequential, highly practical HTML lessons with immediate active recall checks and live coding editors.
  - **Progressive 4-Stage Split:**
    - **Stage 1 (Mid-Level, 23 Lessons) → Project 1:** Single-service Java/Kotlin Spring Boot + PostgreSQL + Testcontainers before distributed complexity.
    - **Stage 2 (Senior, 27 Lessons) → Project 2:** Distributed caching, Kafka, Security, Virtual Threads, Modulith/Sagas, Webhooks/WireMock, Spring Boot 4 / Java 25, Kotlin Coroutines + MockK.
    - **Stage 3 (Production Engineering, 18 Lessons):** Production SQL & `JdbcClient` (`0078`), Linux Essentials (`0079`), Backend Networking (`0075`), Money/UTC/PII Correctness (`0080`), Telemetry & `k6` Load Testing (`0076`), Bounded `@Async` + `TaskDecorator` (`0081`), Docker/K8s Literacy, Zero-Downtime Expand-and-Contract DB Migrations (`0054`), and Developer-Facing AWS (`0047`, `0048`).
    - **Stage 4 (Optional Deep-Dive Shelf, 17 Lessons):** Platform/SRE deep dives, Multi-Tenancy & OpenFeature (`0082`), Spring Data MongoDB/DynamoDB (`0083`), Debezium CDC & Event Sourcing (`0084`), Contract Testing & ArchUnit (`0085`), & specialized Spring modules.
  - **Mandatory lesson format:**
    1. Plain-English conceptual mechanics & mobile analogies.
    2. Dual production code snippets (Java 21/25 primary + Kotlin 2.x migration).
    3. Multiple active recall quizzes (3 questions, balanced word count options, varied answer positions, per-option explanations).
    4. Interactive in-browser coding practice lab with automated multi-assertion validation.

---

## Complete 4-Stage Senior Curriculum (85 Lessons across 21 Phases: 0–20)

### STAGE 1: Mid-Level Java & Kotlin Spring Boot (23 Lessons • Phases 0–5) → Project 1
- **Phase 0:** `0001`, `0002`, `0071` (Modern Java 21/25 for Kotlin Devs)
- **Phase 1:** `0072` (Gradle vs Maven & BOMs), `0003`, `0004`, `0005`, `0065` (Profiles & `@Around` AOP)
- **Phase 2:** `0006`, `0007`, `0008`, `0009`, `0010`
- **Phase 3:** `0011`, `0012`, `0013`, `0014`
- **Phase 4:** `0015`, `0016`, `0073` (JPA `Specification<T>`, Auditing & `@SoftDelete`), `0017`
- **Phase 5:** `0038` (`@WebMvcTest` & Unit), `0039` (Testcontainers PostgreSQL)
- **Project 1 Milestone:** `reference/mid-level-capstone-spec.html`

### STAGE 2: Senior Java & Kotlin Spring Boot (27 Lessons • Phases 6–13) → Project 2
- **Phase 6:** `0059` (Edge HTTP/CORS/ETag), `0060` (SSE/WebSockets + Redis Fan-Out), `0061` (CAP/PACELC, Partitioning, Sharding)
- **Phase 7:** `0018`, `0019`, `0020`, `0021`, `0022`
- **Phase 8:** `0023` (Resilience4j)
- **Phase 9:** `0024`, `0025`, `0026`, `0027`
- **Phase 10:** `0028`, `0029`, `0030`, `0031`, `0062` (Argon2id & OWASP API Top 10)
- **Phase 11:** `0032`, `0033`, `0034`, `0035`
- **Phase 12:** `0036`, `0037`, `0074` (Webhooks HMAC-SHA256 & WireMock)
- **Phase 13:** `0077` (Spring Boot 4.0 / Framework 7 & Java 25 Practice), `0049` (Kotlin Coroutines, `MDCContext` & `MockK`)
- **Project 2 Capstone:** `reference/capstone-project-spec.html`

### STAGE 3: Senior Production Engineering — SQL, Linux, Correctness, Networking, Telemetry, Containers & AWS (18 Lessons • Phases 14–17)
- **Phase 14 (Production SQL, Linux, Correctness & Networking Foundations):** `0078` (CTEs, Window Functions, Atomic `UPSERT`, `JSONB` & Spring `JdbcClient`), `0079` (Linux & Container Shell Essentials: `SIGTERM`, `ulimit -n`, `ss`/`lsof`, `jq`), `0075` (DNS, TCP Keep-Alive Pools, Timeouts, TLS 1.3 & SSL Bundles, L4 vs L7), `0080` (Money `BigDecimal`, UTC `Instant` & PII Log Masking)
- **Phase 15 (Telemetry, Async Pools & Performance):** `0041`, `0042`, `0050`, `0052` (Boot 4 OTel & `@Observed`), `0076` (`k6` Load Testing & `StreamingResponseBody`), `0081` (Bounded `@Async` & `TaskDecorator`)
- **Phase 16 (CI/CD, Containers, Zero-Downtime & K8s):** `0040`, `0043`, `0044`, `0045`, `0046`, `0054` (Expand-and-Contract Flyway Migrations)
- **Phase 17 (Developer-Facing AWS):** `0047`, `0048`

### STAGE 4: Optional Deep-Dive Shelf (17 Lessons • Phases 18–20)
- **Phase 18 (Optional Platform/SRE & AWS IaC):** `0051`, `0053`, `0055`, `0056`, `0057`, `0058`
- **Phase 19 (Optional Distributed APIs, Search, Multi-Tenancy, NoSQL & CDC):** `0063`, `0064`, `0082`, `0083`, `0084`
- **Phase 20 (Optional Modern Spring Extensions & Contract/Arch Testing):** `0066`, `0067`, `0068`, `0069`, `0070`, `0085`
- **Optional Platform Capstone:** `reference/pro-cloud-capstone-spec.html`
