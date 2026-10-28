# Phase 13 (Capstone) shared rules: L45 first, then L46 + L47

Workspace: `/Users/tl/Documents/Learns/CS Fundamentals/` (call it W). Scratchpad: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad` (call it S).

## Files (NN = your lesson number, slug given in your prompt)
- Lesson: `W/lessons/00NN-<slug>.html` with `<body data-lesson="lNN">`.
- Template: `W/.build/tpl/00NN.html`. Verified sources: `W/.build/lNN/`. Memviz specs (if any): `W/.build/viz/lNN.py` (see `W/.build/viz/README.txt`; names `\w+`, prefix `lNN_`). Every path must match `[\w/.]+` (no hyphens anywhere, including Gradle subfolders, so use `gr_kotlin`, not `gr-kotlin`).
- Build: `cd "W/.build/bin" && python3 build.py ../tpl/00NN.html ../../lessons/00NN-<slug>.html`. `<!--CODE:lNN/path/File.kt-->` injects a verified file (language from extension); `<!--VIZ:name-->` injects a memviz spec.
- Gradle projects (only if your lesson needs one): put a copy under `W/.build/lNN/gr_kotlin/` and `W/.build/lNN/gr_java/`, generated with Gradle 9.7.1 (`gradle init --type kotlin-application` / `java-application`, Kotlin DSL), the same as L11's `W/.build/l11/tdd_kotlin` (look at its `app/build.gradle.kts`; `~/.gradle` caches the JDK 21 toolchain, Kotlin plugin and JUnit). Work in a scratch copy under S, then copy the sources into W. Before you finish, delete `build/`, `.gradle/` and `.kotlin/` from the copies in W.

## Don't edit shared files
`W/assets/*` (existing ones), `W/.build/bin/*`, `W/reference/*`, `NOTES.md`, `RESOURCES.md`, other lessons and templates. New styles go in a `<style>` block in your lesson's `<head>`; report them. You MAY create ONE new asset file, but only with the name assigned in your prompt (so siblings never collide). It must inject its own CSS using the theme tokens in `assets/course.css` (look at `assets/gitgraph.js` and `assets/pipeviz.js` as models), work in light and dark mode, and fit at phone width. I integrate links, notes, the roadmap and the reference sheet afterwards.

## Tools
- `export KOTLINC=S/kotlinc-dl/kotlinc/bin/kotlinc (re-downloaded 2026-10-26; if missing, re-download kotlin-compiler-2.4.20.zip from the JetBrains GitHub release and verify its .sha256)`, then `W/.build/bin/run.sh File.kt File.java …`.
- **Libraries** are checksum-verified jars in `S/libs/`. Never download other copies. `run.sh` takes no classpath, so compile with `-cp` yourself; in Gradle use the same coordinates.
  - **kotlinx.coroutines** 1.11.0: `kotlinx-coroutines-core-jvm-1.11.0.jar`.
  - **kotlinx.serialization** 1.12.0: `kotlinx-serialization-core-jvm-1.12.0.jar` + `kotlinx-serialization-json-jvm-1.12.0.jar`.
    - It needs the compiler plugin: `"$KOTLINC" -Xplugin=S/kotlinc-dl/kotlinc/lib/kotlinx-serialization-compiler-plugin.jar -cp "$SCP" File.kt -include-runtime -d out.jar`, then `java -cp "out.jar:$SCP" FileKt`.
    - In Gradle use the `plugin.serialization` plugin, `kotlin("plugin.serialization")`, plus `org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0`. Verify the plugin id against the Kotlin docs.
  - **Jackson 3** (`tools.jackson.*` packages, the current major; check its status and that Spring Boot 4 uses it before claiming so): `jackson-databind-3.2.4.jar` + `jackson-core-3.2.4.jar` + `jackson-annotations-2.22.jar`. Run with `java -cp "$JCP" File.java`. Gradle: `tools.jackson.core:jackson-databind:3.2.4`. Mention Jackson 2 (`com.fasterxml`) in one line, since older tutorials use it.
  - **H2** 2.5.252: `h2-2.5.252.jar`, a pure-Java SQL database with real isolation levels and MVCC (`jdbc:h2:mem:…`). L36 uses it for isolation-level/anomaly demos.
  - **SQLite JDBC** 3.53.4.0: `sqlite-jdbc-3.53.4.0.jar`. Run with `java --enable-native-access=ALL-UNNAMED -cp "$SQ" File.java` (JDK 26 warns about native access otherwise; show or suppress the warning honestly). Gradle: `org.xerial:sqlite-jdbc:3.53.4.0`. The capstone (L45–L47) uses SQLite.
  - **sqlite3 CLI:** `/usr/bin/sqlite3` 3.51.0 (macOS). There's also an Android SDK copy earlier on PATH; always call `/usr/bin/sqlite3` explicitly and say so. Use it for interactive SQL transcripts and `EXPLAIN QUERY PLAN`, with `.mode box` or `.mode column` and `.headers on`.
  - Copies of all jars are also in `W/.build/libs/`.
- `.build/bin/run.sh` compiles into a private `mktemp -d` folder, so parallel runs are safe.
- JDK 26: `java File.java`, `javap`. Gradle 9.7.1 on PATH.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings and measurements:** show the real numbers from this machine and say so ("on my machine; yours will differ"). Use warm-up runs; never claim precision you don't have. JMH was taught in L24; link it (and use it only if a comparison really needs it).
- Verify every factual claim against an official or canonical source with WebFetch/curl and cite inline:
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and Kotlin API reference, JLS/JEPs, dev.java (check redirects and `<title>` with curl).
  - Fielding ch. 5, the RFCs, and the JDK/Spring reference docs: the phase's primary sources (see below).
  - OpenJDK source on GitHub (github.com/openjdk/jdk) for implementation details the API docs leave unspecified, labelled as "implementation detail, not a guarantee".
  - CLRS / *Effective Java* may be cited by chapter or item number.

## Match L13–L15 exactly in structure and style
Read `W/lessons/0013-generics.html` and `W/lessons/0015-lazy-pipelines-and-idiomatic-kotlin.html`, and their templates in `W/.build/tpl/`, first.
- Top: kicker; h1; subtitle; `.lesson-meta` with `#resume`; `.win`; `.toc` with `data-part`.
- Warm-up: recalls earlier lessons (`.warmup` plus quizzes).
- 3–4 parts, each with:
  - Kotlin/Java `.pair` code and real `.out`/`.err` blocks
  - a "What Kotlin does for you" `.auto` box
  - an `.exercise` with `details.solution`
  - a Check quiz and a `label.done` checkbox
- Final exercise with `.rating` rows (KOTLIN, JAVA, plus a third row where your prompt says so).
- End matter:
  - an exit quiz interleaving earlier lessons
  - the "Build my results" summary and a midweek recall card
  - a `.primary` source box and a reference-sheet link to `../reference/capstone.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Capstone specifics: one program, built by the learner across L45 and L46
**The project: "Shelf", a no-framework library REST API.** It reuses the course's library domain (L32 books API, L34–L36 schema, L38 DI, L41 cache, L43 rate limiter and observability). Everything uses the JDK `HttpServer`, SQLite via `sqlite-jdbc`, kotlinx.serialization (Kotlin) / Jackson 3 (Java), JUnit (kotlin.test / Jupiter), and Gradle 9.7.1. No frameworks.

**Fixed spec, so L45, L46 and L47 agree.** Deviate only with a stated reason.
- **Resources and endpoints** (JSON, Problem Details errors per RFC 9457):
  - `GET /health`
  - `GET /books?limit&offset&author`, `GET /books/{id}`, `POST /books` (201 + `Location`), `PUT /books/{id}`, `DELETE /books/{id}`
  - `POST /members`, `GET /members/{id}`
  - `POST /loans` (body `{bookId, memberId}`): 201, 409 if already lent, 404 if unknown
  - `POST /loans/{id}/return`
  - `GET /members/{id}/loans`
- **Layers:**
  - `http/`: the router and handlers. Hand-written routing with path params, method dispatch, 404/405 + `Allow`, and a JSON body helper.
  - `service/`: `BookService`, `LendingService`. The rules: one open loan per book, and max 3 loans per member.
  - `store/`: interfaces `BookStore`, `MemberStore`, `LoanStore`, with SQLite implementations plus in-memory fakes.
  - `App`: the composition root (L38).
  - Inject a `Clock`.
- **Schema:** `authors`/`books`/`members`/`loans` like L34, plus a partial unique index on open loans (L36). `PRAGMA foreign_keys = ON` on every connection; for concurrent writers, `?transaction_mode=IMMEDIATE` (the L36 finding).
- **Gradle projects (paths must match `[\w/.]+`):**
  - **L45** builds v1 in `W/.build/l45/cap_kotlin` and `W/.build/l45/cap_java`.
  - **L46** first copies L45's projects to `W/.build/l46/cap_kotlin`/`cap_java` and evolves the copies. **Never edit `.build/l45/`**, because L45's lesson injects code from there.
  - **L47** reads both and writes only to `W/.build/l47/`.
  - Remove `build/`, `.gradle/` and `.kotlin/` before finishing.
- **Tests:**
  - **Unit tests:** services with fakes and a fixed clock.
  - **Integration tests:** start `App` on port 0 with a temp-file SQLite DB, then drive it with `java.net.http.HttpClient`.
  - Contract tests run the same store suite on the fake and the real store (L38).
  - Real green `./gradlew test` with counts; at least one real red run per lesson where it teaches something.
- **Hand-coding is the point.** Each lesson guides the learner to build their own Shelf step by step, from a blank `gradle init` project. Reference code sits in `details.solution` blocks or after the exercise prompt. Kotlin and Java are side by side throughout.
- **All servers bind to 127.0.0.1 and shut themselves down.** Verify with `lsof -iTCP -sTCP:LISTEN -n -P` before finishing. Use `perl -e 'alarm N; exec @ARGV'` for timeouts on macOS.
- **Sources:**
  - Fielding's dissertation ch. 5 (REST), the primary source
  - RFC 9110/9457/6585
  - the JDK `HttpServer` and `HttpClient` API docs, the `java.sql` docs, the SQLite docs
  - the kotlinx.serialization and Jackson docs, the Gradle docs
  - for L47, the Spring Boot / Spring Framework reference docs (docs.spring.io), cited per mapping; names and short snippets from the docs only, with no Spring code run
- **Bridge:** this phase *is* the bridge to the learner's next missions (Spring Boot and Android). Keep the map honest: what Spring automates, what it doesn't, and what still matters.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. the capstone pulls every phase into one program.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/capstone.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Durable reference-sheet parts
Also write your 2 reference-sheet `<section>` blocks to `W/.build/refparts/lNN.html` (plain HTML, no escaping). The scratchpad gets wiped by macOS after a few days; `.build/refparts/` doesn't.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Capstone outline
- **L45 Build the Core** (`0045-build-the-core.html`): project setup from `gradle init` (L1/L25), package layout and the composition root (L38); routing on the JDK `HttpServer` (a hand-written router: path templates `/books/{id}`, method dispatch, 404/405/Allow, a virtual-thread executor from L28); JSON endpoints (DTOs vs domain, validation at the boundary, Problem Details, kotlinx.serialization / Jackson 3 from L32); JDBC with SQLite (stores, transactions for lending, the partial unique index, from L36); unit vs integration tests (what each catches, the test pyramid per Fowler's bliki, fakes vs real DB, port-0 servers, temp DBs), contract tests for stores. Final: the full v1 Shelf API green, plus a curl session.
- **L46 Make It Production-Shaped** (`0046-make-it-production-shaped.html`): thread safety under load (a concurrent load test against Shelf finds a real race or SQLITE_BUSY, then the fix; virtual threads; connection-per-request vs a tiny hand-rolled pool, measured); add the L41 cache (cache-aside on `GET /books/{id}` with invalidation on writes, request coalescing) and the L43 rate limiter (per-client token bucket, 429 + Retry-After), measuring the difference with a scripted load test (p50/p95/p99, DB query counts, 429 counts); structured logging (JSON log lines with request id, method, path, status, duration; `traceparent` passthrough from L43; a `/metrics` endpoint with golden signals); config via env vars (port, DB path; no secrets in code, L33); graceful shutdown (`HttpServer.stop(delay)`, draining); a runnable distribution via `installDist` (L25). Final: Shelf v2 under a load test with a before/after table.
- **L47 What Spring Boot Would Have Done** (`0047-what-spring-boot-would-have-done.html`): map each piece the learner built to what Spring automates: router → `@RestController`/`@GetMapping`; JSON → Jackson auto-config/`HttpMessageConverter`; Problem Details → `ProblemDetail`/`@ControllerAdvice`; composition root → the `ApplicationContext` and constructor injection; stores → `JdbcTemplate`/`JdbcClient`/Spring Data; transactions → `@Transactional`; cache → `@Cacheable`; rate limit → no built-in (filters/gateways); logging/metrics → Actuator + Micrometer; config → `application.properties` and env binding; tests → `@SpringBootTest`/`MockMvc`/test slices; distribution → the executable jar. Each mapping gives the learner's code next to a short Spring snippet from the official docs (not run; labelled), and "what Spring hides that you now understand", plus Android parallels (Hilt, Room, Retrofit, WorkManager). Then a look back at the whole course (a map of the 13 phases and the threads that ran through them, with links to every reference sheet) and choosing your next mission, linking the roadmap's "After the course: next missions" section (A Frameworks → B Containers/Cloud/DevOps → C Distributed systems; D Compilers optional) with concrete first steps and resources from RESOURCES.md. Course finale: no "next" lesson; the nav's next link points to the roadmap.
