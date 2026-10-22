# Phase 10 shared rules (all three forks: L34–L36)

Workspace: `/Users/tl/Documents/Learns/CS Fundamentals/` (call it W). Scratchpad: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad` (call it S).

## Files (NN = your lesson number, slug given in your prompt)
- Lesson: `W/lessons/00NN-<slug>.html` with `<body data-lesson="lNN">`.
- Template: `W/.build/tpl/00NN.html`. Verified sources: `W/.build/lNN/`. Memviz specs (if any): `W/.build/viz/lNN.py` (see `W/.build/viz/README.txt`; names `\w+`, prefix `lNN_`). Every path must match `[\w/.]+` (no hyphens anywhere, including Gradle subfolders, so use `gr_kotlin`, not `gr-kotlin`).
- Build: `cd "W/.build/bin" && python3 build.py ../tpl/00NN.html ../../lessons/00NN-<slug>.html`. `<!--CODE:lNN/path/File.kt-->` injects a verified file (language from extension); `<!--VIZ:name-->` injects a memviz spec.
- Gradle projects (only if your lesson needs one): put a copy under `W/.build/lNN/gr_kotlin/` and `W/.build/lNN/gr_java/`, generated with Gradle 9.7.1 (`gradle init --type kotlin-application` / `java-application`, Kotlin DSL), the same as L11's `W/.build/l11/tdd_kotlin` (look at its `app/build.gradle.kts`; `~/.gradle` caches the JDK 21 toolchain, Kotlin plugin and JUnit). Work in a scratch copy under S, then copy the sources into W. Before you finish, delete `build/`, `.gradle/` and `.kotlin/` from the copies in W.

## Don't edit shared files
`W/assets/*` (existing ones), `W/.build/bin/*`, `W/reference/*`, `NOTES.md`, `RESOURCES.md`, other lessons and templates. New styles go in a `<style>` block in your lesson's `<head>`; report them. You MAY create ONE new asset file, but only with the name assigned in your prompt (so siblings never collide). It must inject its own CSS using the theme tokens in `assets/course.css` (look at `assets/gitgraph.js` and `assets/pipeviz.js` as models), work in light and dark mode, and fit at phone width. I integrate links, notes, the roadmap and the reference sheet afterwards.

## Tools
- `export KOTLINC=S/kotlinc-dl/kotlinc/bin/kotlinc`, then `W/.build/bin/run.sh File.kt File.java …`.
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
  - SQLBolt, *Use The Index, Luke*, and the SQLite/H2/PostgreSQL docs: the phase's primary sources (see below).
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
  - a `.primary` source box and a reference-sheet link to `../reference/databases.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Database lessons specifically
- **One shared sample database across the phase,** so lessons build on each other. Each lesson keeps its own copy of the schema script under `W/.build/lNN/`. Use L32's books/library theme: `authors`, `books`, `members`, `loans`, with a few dozen rows of seed data (deterministic, hand-written or generated with a fixed seed).
  - L34 creates it.
  - L35 extends it with indexes and a larger generated table, e.g. 200k `loans` rows, so `EXPLAIN` and timing differences are real.
  - L36 uses it for transactions.
- **Every SQL statement shown is run for real,** with real output captured: `sqlite3` transcripts, or JDBC program output. SQL shows up in three ways:
  - as `.sql` files injected via `<!--CODE:lNN/file.sql-->`, (build.py maps `.sql` to `language-sql`, `.sh` to bash, `.kts` to kotlin, `.json` to json). Load `components/prism-sql.min.js` from prism 1.29.0;
  - as terminal transcripts in `<pre class="term">`;
  - inside Kotlin/Java JDBC programs.
- **Dialect honesty:** standard SQL vs SQLite vs H2 vs PostgreSQL differences, e.g. SQLite's flexible typing/type affinity, `INTEGER PRIMARY KEY` = rowid, foreign keys off by default (`PRAGMA foreign_keys = ON`), and isolation in SQLite (serializable by default; WAL). Cite the SQLite docs (sqlite.org/lang.html, datatype3.html, foreignkeys.html, eqp.html, isolation.html, queryplanner.html), the H2 docs (h2database.com/html/advanced.html#transaction_isolation), and the PostgreSQL docs (postgresql.org/docs/current/transaction-iso.html) as the reference for isolation anomalies. Postgres isn't installed; describe it from docs only and say so.
- **Sources:** SQLBolt (interactive practice; link it for drills), *Use The Index, Luke* (B-tree indexes, `EXPLAIN`), the SQLite/H2/PostgreSQL docs, the JDBC API docs (`java.sql`), the OWASP SQL Injection Prevention Cheat Sheet, and *DDIA* ch. 7 (Transactions), cited by chapter.
- **Kotlin vs Java:** JDBC is the same API in both. Show Kotlin's `use {}` vs try-with-resources, extension functions to tidy `ResultSet` mapping (e.g. `fun <T> ResultSet.map(f: (ResultSet) -> T): List<T>`), data classes vs records as row types, and `Sequence` for streaming rows (callback L15). Name JetBrains Exposed / jOOQ / JPA/Hibernate / Spring Data / Room (Android) as bridges only, with no framework code.
- **Bridge:** Android's Room is SQLite underneath; Spring Data JPA generates SQL like this. N+1 queries as the classic ORM trap (show it in plain JDBC).

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 10 is what Spring Data/JPA and Android's Room stand on.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/databases.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 10 outline (shared, so warm-ups and callbacks reference the right material)
- **L34 The Relational Model & SQL** (`0034-the-relational-model-and-sql.html`): relations/tables, rows, columns, types; primary keys (natural vs surrogate), foreign keys and referential integrity (`PRAGMA foreign_keys`), NOT NULL/UNIQUE/CHECK; CREATE/INSERT/UPDATE/DELETE; `SELECT`, `WHERE` (comparison, `AND`/`OR`, `IN`, `LIKE`, `BETWEEN`, `IS NULL`, three-valued logic with NULL), `ORDER BY`, `LIMIT`/`OFFSET` (callback L32 pagination), `DISTINCT`; `JOIN` (inner, left, the "missing rows" puzzle, self-join), `GROUP BY` + aggregates + `HAVING`, subqueries and CTEs (`WITH`), with logical query evaluation order explained. Mostly `sqlite3` transcripts, plus a first tiny JDBC read in both languages.
- **L35 Schema Design & Indexes** (`0035-schema-design-and-indexes.html`): modelling (entities, relationships 1:1/1:N/M:N with join tables), normalisation (1NF/2NF/3NF with anomalies shown: update/insert/delete), when to denormalise; indexes as B-trees (callback L20 BSTs and balanced trees; *Use The Index, Luke*), leaf/node structure, why `WHERE` on an indexed column is O(log n), composite indexes and column order (leftmost prefix), covering indexes, index costs on writes, functions on columns defeating indexes, `LIKE 'x%'` vs `'%x'`; `EXPLAIN QUERY PLAN` in SQLite (SCAN vs SEARCH USING INDEX) and timing on 200k rows; H2/Postgres `EXPLAIN` mentioned from docs.
- **L36 Transactions & JDBC** (`0036-transactions-and-jdbc.html`): ACID; transactions (`BEGIN`/`COMMIT`/`ROLLBACK`, autocommit in JDBC, `setAutoCommit(false)`); isolation levels and anomalies (dirty read, non-repeatable read, phantom, lost update, write skew), demonstrated for real with two JDBC connections on H2 in different isolation levels (callback L26/L27 races); optimistic locking with a version column vs `SELECT … FOR UPDATE` (check H2 support); JDBC by hand (`DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`, batching, generated keys, resource closing, connection pools named only, e.g. HikariCP); SQL injection and prepared statements (callback L33). Final (Phase 10 milestone): design a schema and query it from Kotlin and Java (a small repository layer over SQLite with tests).
