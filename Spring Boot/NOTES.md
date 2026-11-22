# Teaching Notes & User Preferences

## Learner Profile & Elevated Target
- **Current Role:** Junior Android Developer (Kotlin).
- **Target Role:** **Senior Spring Boot Backend Developer (2027 Industry Standard)**.
- **Starting Point:** Strong in Kotlin/Android client concepts; starting from zero on server-side architecture.
- **Key Pedagogical Directives:**
  - **No abstract knowledge hubs:** Teach through sequential, highly practical HTML lessons with immediate active recall checks and live coding editors.
  - **Progressive 4-Stage Split (90 Lessons Total: 74 Core + 16 Optional):**
    - **Stage 1 (Mid-Level, 24 Lessons) → Project 1:** Single-service Java/Kotlin Spring Boot + Advanced DI (`0086`) + PostgreSQL + Testcontainers before distributed complexity.
    - **Stage 2 (Senior, 30 Lessons) → Project 2:** Distributed caching & Redis Internals (`0087`), Kafka & Kafka Ops (`0088`), Security, Virtual Threads, Modulith/Sagas, System Design Playbook (`0090`), Spring Boot 4 / Java 25, Kotlin Coroutines + MockK.
    - **Stage 3 (Production Engineering, 20 Lessons):** Production SQL & `JdbcClient` (`0078`), PostgreSQL MVCC/Locks/Deadlocks (`0089`), Linux Essentials (`0079`), Backend Networking (`0075`), Money/UTC/PII Correctness (`0080`), Grafana/PromQL/RED/OTel Telemetry (`0051` promoted + `0041, 0042, 0050, 0052`), `k6` Load Testing (`0076`), Bounded `@Async` + `TaskDecorator` (`0081`), Docker/K8s Literacy, Zero-Downtime Expand-and-Contract DB Migrations (`0054`), and Developer-Facing AWS (`0047`, `0048`).
    - **Stage 4 (Optional Deep-Dive Shelf, 16 Lessons):** Platform/SRE deep dives, Multi-Tenancy & OpenFeature (`0082`), Spring Data MongoDB/DynamoDB (`0083`), Debezium CDC & Event Sourcing (`0084`), Contract Testing & ArchUnit (`0085`), & specialized Spring modules (`0066–0070` incl. Spring AI MCP).
  - **Mandatory lesson format:**
    1. Plain-English conceptual mechanics & mobile analogies.
    2. Dual production code snippets (Java 21/25 primary + Kotlin 2.x migration).
    3. Multiple active recall quizzes (3 questions, balanced word count options, varied answer positions, per-option explanations).
    4. Interactive in-browser coding practice lab with automated multi-assertion validation.

---

## Complete 4-Stage Senior Curriculum (90 Lessons across 21 Phases: 0–20)

### STAGE 1: Mid-Level Java & Kotlin Spring Boot (24 Lessons • Phases 0–5) → Project 1
- **Phase 0:** `0001`, `0002`, `0071`
- **Phase 1:** `0072`, `0003`, `0086` (`@Qualifier`, Strategy Maps, `ObjectProvider`, Scopes, `BeanPostProcessor`), `0004`, `0005`, `0065`
- **Phase 2:** `0006`, `0007`, `0008`, `0009`, `0010`
- **Phase 3:** `0011`, `0012`, `0013`, `0014`
- **Phase 4:** `0015`, `0016`, `0073`, `0017`
- **Phase 5:** `0038`, `0039`
- **Project 1 Milestone:** `reference/mid-level-capstone-spec.html`

### STAGE 2: Senior Java & Kotlin Spring Boot (30 Lessons • Phases 6–13) → Project 2
- **Phase 6:** `0059`, `0060`, `0061`
- **Phase 7:** `0018`, `0019`, `0020`, `0021`, `0022`, `0087` (Redis Eviction, Serialization, Lua Scripts & Streams)
- **Phase 8:** `0023`
- **Phase 9:** `0024`, `0025`, `0026`, `0027`, `0088` (Kafka Manual Offsets, Cooperative Rebalancing, Lag & Compaction)
- **Phase 10:** `0028`, `0029`, `0030`, `0031`, `0062`
- **Phase 11:** `0032`, `0033`, `0034`, `0035`
- **Phase 12:** `0036`, `0037`, `0074`, `0090` (Senior System Design Playbook & Sizing)
- **Phase 13:** `0077`, `0049`
- **Project 2 Capstone:** `reference/capstone-project-spec.html`

### STAGE 3: Senior Production Engineering — SQL/PostgreSQL, Linux, Networking, Grafana/OTel & AWS (20 Lessons • Phases 14–17)
- **Phase 14:** `0078`, `0089` (PostgreSQL MVCC, `AUTOVACUUM`, `lock_timeout` & Deadlock Triage), `0079`, `0075`, `0080`
- **Phase 15:** `0041`, `0042`, `0050`, `0051` (Grafana RED Dashboards, PromQL & SLOs — promoted from Stage 4), `0052`, `0076`, `0081`
- **Phase 16:** `0040`, `0043`, `0044`, `0045`, `0046`, `0054`
- **Phase 17:** `0047`, `0048`

### STAGE 4: Optional Deep-Dive Shelf (16 Lessons • Phases 18–20)
- **Phase 18 (Optional Platform/SRE & AWS IaC):** `0053`, `0055`, `0056`, `0057`, `0058`
- **Phase 19 (Optional Distributed APIs, Search, Multi-Tenancy, NoSQL & CDC):** `0063`, `0064`, `0082`, `0083`, `0084`
- **Phase 20 (Optional Modern Spring Extensions & Contract/Arch Testing):** `0066`, `0067` (Spring AI + MCP), `0068`, `0069`, `0070`, `0085`
- **Optional Platform Capstone:** `reference/pro-cloud-capstone-spec.html`
