# Learning Record 0016: 2027 End-to-End Upgrade, Pedagogical Reordering & Sequential 0001–0091 Curriculum

**Date:** 2026-11-26  
**Scope:** Full curriculum audit remediation, 2027 stack baseline upgrade, new local dev workflow lesson, conceptual expansion of 21 lessons, and atomic sequential renumbering (`0001`–`0091`).

## Summary of Upgrades Completed
1. **Interactive Course Engine (`assets/app.js`):**
   - Added `formatSafeInlineHtml()` so generic type parameters (`<String, T>`) in quiz explanations and lab failure messages are never swallowed by `innerHTML`.
   - Fixed `stripStarterComments()` to strip SQL `--` starter comments so untouched SQL labs never auto-pass assertions.
   - Added `localStorage` reload persistence for answered quizzes, global header `Java 25 | Kotlin 2.x` language toggle injection, clickable `[data-progress-badge]` completion toggles, and `SPRING BOOT 4.2` test runner banners.
2. **2027 Stack Baseline Alignment:**
   - Upgraded primary stack across lessons, starters, validators, and reference guides to **Spring Boot 4.2 + Spring Framework 7.1 + Spring Security 7 + Hibernate ORM 7 + Jackson 3 (`tools.jackson.databind.json.JsonMapper`) + Java 25 LTS (JEP 491, `ScopedValue`, Generational ZGC, Leyden AOT) + Kotlin 2.2+ + PostgreSQL 17**, while preserving concise Boot 3.x / Java 21 callouts and repurposing Lesson `0056` into an **Enterprise Migration Playbook (Boot 3 → 4.2)**.
3. **Pedagogical Reordering, New Lesson & Sequential Renumbering (`0001`–`0091`):**
   - Added **Lesson `0005`** (`0005-local-dev-workflow-docker-compose-and-debugging.html`) covering `start.spring.io`, `spring-boot-docker-compose` (`compose.yaml`), `.http` request files, and Virtual Thread `Suspend: Thread` / tracepoint debugging.
   - Moved Money (`BigDecimal`)/UTC (`Instant`)/PII safety (`0014`) and Production SQL/CTEs/Window Functions/`JdbcClient` (`0018`) into **Stage 1** before Project 1.
   - Split `AbstractRoutingDataSource` Read-Replica Routing out of Stage 1 (`0021` is pure Keyset Pagination) into Stage 2 Database Scaling (`0030`, after `@Transactional` is taught in `0025`).
   - Sequentially renumbered all **91 lessons (`0001`–`0091`)**: **75 Core (`27 + 30 + 18`) + 16 Optional**, aligned Project 1 & Project 2 specs strictly to taught skills, and added **Milestone 3 (`reference/stage3-production-milestone-spec.html`)**.
4. **Content Depth, HTML Hygiene & Quiz Rebalancing:**
   - Expanded all 21 bullet-only lessons with multi-section `<h3>` architectural prose, comparison matrices, and ASCII topology diagrams.
   - Upgraded single-assertion labs to multi-assertion verification, fixed all raw LaTeX/Markdown/unescaped `<Generic>`/backslash-quote defects, and rebalanced quiz options across all 273 quizzes to `68 / 70 / 68 / 67` (`24.9% / 25.6% / 24.9% / 24.5%`).
