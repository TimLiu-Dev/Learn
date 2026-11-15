# Lesson Author Brief (shared by all lesson authors)

Workspace: `/Users/tl/Documents/Learns/System Design/Backend` (a teaching workspace).
Learner: junior Android developer (Kotlin, Retrofit/OkHttp, Paging, FCM) learning backend from scratch.
Mission: Part 1 = pass L3/L4 backend system design interviews in 2027; Part 2 = senior. Stack: **Kotlin + Spring Boot primary, Java + Spring Boot on the side.**

## Read first (mandatory)
1. `MISSION.md`, `NOTES.md`, `GLOSSARY.md` (adhere to its terms).
2. `lessons/0019-first-endpoint-server-side-of-retrofit.html` — **the template. Copy its structure, CSS classes, nav, header badges, `details.java-side` block, sandbox + 3 quizzes, "Do it for real" steps, primary-source section, and footer teacher prompt.**
3. `assets/style.css`, `assets/quiz.js`, `assets/code-runner.js` (CodeRunner accepts `successMessage`). Reuse; do NOT edit assets.

## Fixed filenames (use for cross-links)
- lessons/0019-first-endpoint-server-side-of-retrofit.html (exists)
- lessons/0020-request-lifecycle-dns-tcp-tls-load-balancer.html
- lessons/0021-postgres-spring-data-stateless-service.html
- lessons/0022-transactions-and-isolation-levels.html
- lessons/0023-server-side-auth-sessions-jwt-oauth.html
- lessons/0024-docker-and-deploy-first-service.html
- lessons/0025-real-time-delivery-polling-sse-websockets-push.html
- lessons/0026-worked-designs-url-shortener-notification-system.html
- lessons/0027-observability-metrics-logs-traces-slos.html
- reference/0003-backend-foundations-cheatsheet.html (exists)
- reference/0004-capstone-part1-pulseorder-spec.html (Part 1 capstone, being written by coordinator)
- Existing older lessons you may link: 0014 interview/estimation, 0010 indexes, 0015 pagination, 0009 caching, 0008 idempotency, 0001 rate limiting, 0012 resilience, 0005 kafka, 0017 jobs, 0011 outbox, 0018 object storage (see `lessons/`).

Nav links in each lesson: Master Plan (`../master-plan.html`), Foundations Cheat Sheet (`../reference/0003-backend-foundations-cheatsheet.html`), Part 1 Capstone (`../reference/0004-capstone-part1-pulseorder-spec.html`).

## Capstone context ("PulseOrder")
Part 1 capstone = **PulseOrder**, a mobile order-ahead backend (think coffee-shop app): menu, orders, order status, the learner's own Android app as client. Kotlin + Spring Boot + Postgres + Redis, deployed. Each lesson ends with a short **"Capstone milestone"** callout (`callout-tip`) saying exactly what to add to PulseOrder (given in your lesson spec).

## Lesson rules
- **Short (~20–25 min), one tangible skill**, in the learner's zone: they just finished the previous lesson(s). Knowledge first (minimal), then practice with feedback.
- Bridge from Android where it genuinely helps (don't force it).
- **Kotlin code first**, Java equivalent in a `<details class="java-side">` block where code is shown.
- **Never trust parametric knowledge for facts.** Verify every factual claim, API name, annotation, config key, default value, and URL with `search_web` (official docs, RFCs, specs, DDIA, Google SRE book, OWASP preferred). Cite inline with links. If unsure, leave the claim out. Do not state prices or version numbers unless verified; prefer "latest stable".
- **Sandbox**: one `CodeRunner` with a pure-JS simulation of the skill, 5–7 tests, 2–3 progressive hints, a solution, and a lesson-specific `successMessage`. Starter must fail ≥1 test (ideally all); solution must pass all. Starter/solution are JS **template literals**: escape backslashes (`\\d`) and avoid `${...}` inside them (use string concatenation). Tests define helper `eq` like 0019. Keep it synchronous (no async/WebCrypto).
- **3 quizzes** via `QuizEngine`, 4 options each, **every option exactly the same number of words** (and similar character length), single correct answer, a rationale for every option, a `summary`. Vary the position of the correct answer across quizzes (not always index 0).
- **"Do it for real"** numbered steps on their machine (Kotlin + Spring Boot), with a clear win condition. Java-on-the-side bonus step.
- **Section "Primary source & where this sits"**: one recommended primary source (best, high-trust), roadmap.sh nodes covered, links to cheat sheet, previous/next lesson.
- Footer `agent-help-box` reminding them to ask the teacher, with 2–3 example follow-up questions.

## Verify before reporting (mandatory)
Run: `/System/Library/Frameworks/JavaScriptCore.framework/Versions/Current/Helpers/jsc tools/verify-lesson.jsc.js -- lessons/<file>.html` from the workspace root. It must print `RESULT=PASS`. Fix and re-run until it does.

## Do NOT edit
master-plan.html, index.html, assets/*, tools/*, GLOSSARY.md, RESOURCES.md, MISSION.md, NOTES.md, reference/*, or other authors' lessons. Write only your assigned lesson files.

## Report back (in your final message)
1. Files written + the verifier output.
2. **Cheat-sheet rows** to add to reference 0003 (compact HTML table rows or bullets, with source links).
3. **Glossary entries** in the GLOSSARY.md format (`**Term**:` definition, `_Avoid_:` line), max 4 per lesson.
4. **RESOURCES.md entries** (annotated, verified URLs) for sources you relied on.
5. A one-line master-plan card description per lesson (≤ 30 words).
6. Any claims you could not verify or doubts.
