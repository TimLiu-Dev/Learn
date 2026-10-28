# Phase 12 shared rules (all four forks: L41–L44)

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
  - The System Design Primer, *DDIA* 2nd ed., and Google's SRE book: the phase's primary sources (see below).
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
  - a `.primary` source box and a reference-sheet link to `../reference/system-design.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## System-design lessons specifically
- **No Docker, Redis, Kafka, nginx or Postgres are installed, and don't install any.**
  - Every "distributed system" in this phase is built for real, small, on localhost, from course pieces:
    - JDK `HttpServer` backends (L32), several processes or several servers in one JVM on different ports
    - a hand-written round-robin load balancer/proxy (L31's proxy)
    - SQLite files as primary and replica (L34–L36)
    - a `BlockingQueue` (L27) or a tiny TCP queue broker (L30) as a message queue
    - virtual threads for concurrency (L28)
  - Redis, Memcached, Kafka/RabbitMQ, nginx/HAProxy and cloud load balancers are named and cited from their official docs only.
  - Say clearly what a production system would use instead.
- **Everything runs on `127.0.0.1` and shuts itself down.** Before finishing, confirm with `lsof -iTCP -sTCP:LISTEN -n -P` that none of your processes are left. Use `perl -e 'alarm N; exec @ARGV'` for timeouts on macOS.
- **Numbers:**
  - Real measured numbers, labelled "on my machine, on localhost". Say plainly that localhost hides real network latency, and inject artificial latency with `Thread.sleep` where the point needs it, saying so.
  - Back-of-envelope estimates (QPS, storage, bandwidth) are shown as worked arithmetic with stated assumptions.
- **Sources** (verify each URL):
  - The System Design Primer (github.com/donnemartin/system-design-primer)
  - *DDIA* 2nd ed. (Kleppmann & Riccomini), cited by chapter (replication, partitioning/sharding, transactions, consistency and consensus, stream processing)
  - Google's SRE book, free at sre.google/sre-book: "Monitoring Distributed Systems" (four golden signals), "Handling Overload", "Addressing Cascading Failures"
  - the AWS Builders' Library article "Timeouts, retries, and backoff with jitter"
  - Fowler's bliki "CircuitBreaker"
  - Brewer's CAP and Gilbert & Lynch 2002, by title; Kleppmann's "Please stop calling databases CP or AP" (2015 blog), if verifiable
  - the OpenTelemetry docs (opentelemetry.io/docs/concepts)
  - Jeff Dean's latency numbers via Norvig (already used in L22)
  - MDN caching (L31)
  - the Caffeine wiki (Java cache library), named only
  - RFC 9110/9111 where HTTP is involved
- **Kotlin vs Java:** keep the side-by-side discipline. Coroutines vs virtual threads (L29), `Mutex` vs `ReentrantLock`, data classes vs records for messages, and Kotlin `Result`/sealed results for failures (L10). Say clearly when the design is language-neutral.
- **Diagrams:** system-architecture diagrams as inline SVG or one new asset per lesson (assigned in your prompt), using theme tokens; no external diagram tools.
- **Bridge:** Spring's `@Cacheable`, Spring Cloud/Resilience4j circuit breakers, Micrometer metrics, Android's image caches (Coil/Glide), offline-first with Room + WorkManager. Names only; cite official docs.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 12 shapes systems made of many programs.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/system-design.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Durable reference-sheet parts
Also write your 2 reference-sheet `<section>` blocks to `W/.build/refparts/lNN.html` (plain HTML, no escaping). The scratchpad gets wiped by macOS after a few days; `.build/refparts/` doesn't.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 12 outline (shared, so warm-ups and callbacks reference the right material)
- **L41 Latency & Caching** (`0041-latency-and-caching.html`): latency numbers worth memorising (callback L22's ladder; network round trips, disk, datacenter vs cross-continent), percentiles (p50/p95/p99) and tail latency measured on a local service; where caches live (CPU, OS page cache, in-process, shared like Redis, CDN, browser via HTTP from L31); patterns: cache-aside, read-through, write-through, write-behind, refresh-ahead; TTLs, eviction (LRU/LFU), cache stampede/thundering herd and request coalescing, negative caching, invalidation and stale reads (with real demos against a SQLite-backed service); build a thread-safe LRU cache from your hash map and linked list (L19 + L17), with TTL, made thread-safe (L26/L27), tested under concurrency and timed vs `LinkedHashMap`+lock.
- **L42 Scaling Out** (`0042-scaling-out.html`): vertical vs horizontal scaling, stateless servers (session state moved out), load balancers (round-robin, least-connections, health checks) built as a local proxy in front of 3 backends, with one killed mid-run; replication (leader/follower, read replicas, replication lag shown with a delayed SQLite copy, read-your-writes), partitioning/sharding (hash vs range; consistent hashing implemented and measured when adding a node); message queues and async work (producer/consumer, at-least-once delivery, idempotent consumers callback L32, dead-letter queue, backpressure), with a local queue.
- **L43 Trade-offs & Resilience** (`0043-trade-offs-and-resilience.html`): consistency models in plain terms (strong vs eventual, read-your-writes), CAP explained carefully (partition → choose consistency or availability) plus PACELC, demonstrated with two local replicas and a simulated partition; timeouts (callback L30/L31), retries with exponential backoff and jitter (AWS article), idempotency, circuit breaker (built by hand, state machine), bulkheads; build a rate limiter (token bucket and sliding window; per-client; thread-safe; HTTP 429 + `Retry-After` per RFC 6585/9110); observability: structured logs, metrics (counters, gauges, histograms, the four golden signals), traces (trace/span IDs propagated through a 3-hop local call chain via a header, W3C Trace Context named), a tiny dashboard printout; JFR as the JVM's own (callback L24).
- **L44 A Worked Design** (`0044-a-worked-design.html`): a URL shortener end to end: functional and non-functional requirements, back-of-envelope estimates (writes/reads per second, storage over 5 years), API design (callback L32), ID generation (counter + base62 vs hash vs random, collisions, callback L19), data model and SQLite schema (L34–L35), read path with cache (L41) and redirect semantics (301 vs 302 vs 307/308, callback L31), scaling and sharding (L42), failure modes and rate limiting (L43), analytics via a queue; a working mini implementation on `HttpServer` + SQLite + LRU, load-tested locally; then writing a one-page design doc (template: context, goals/non-goals, design, alternatives, risks). Phase 12 milestone.
