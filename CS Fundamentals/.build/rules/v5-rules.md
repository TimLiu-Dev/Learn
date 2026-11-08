# Roadmap v5 shared rules (forks building L48–L57 and the L32 time part)

Workspace: `/Users/tl/Documents/Learns/CS Fundamentals/` (call it W). Scratchpad: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad` (call it S). macOS wipes S every few days, so keep anything you need later in W.

## Files (NN = your lesson number, slug given in your prompt)
- Lesson: `W/lessons/00NN-<slug>.html` with `<body data-lesson="lNN">`.
- Template: `W/.build/tpl/00NN.html`. Verified sources: `W/.build/lNN/`. Memviz specs (if any): `W/.build/viz/lNN.py` (see `W/.build/viz/README.txt`; names `\w+`, prefix `lNN_`). Every path must match `[\w/.]+` (no hyphens anywhere, including Gradle subfolders, so use `gr_kotlin`, not `gr-kotlin`).
- Build: `cd "W/.build/bin" && python3 build.py ../tpl/00NN.html ../../lessons/00NN-<slug>.html`.
  - `<!--CODE:lNN/path/File.kt-->` injects a verified file, with the language taken from its extension (.kt .kts .java .sql .sh .json).
  - `<!--VIZ:name-->` injects a memviz spec.
  - If you paste captured outputs with your own script, keep it as `W/.build/lNN/fill.py` and give the rebuild order in your report.
- Gradle projects (only if your lesson needs one): put a copy under `W/.build/lNN/gr_kotlin/` and `W/.build/lNN/gr_java/`.
  - Generate them with Gradle 9.7.1, Kotlin DSL, the same as L11's `W/.build/l11/tdd_kotlin`.
  - Work in a scratch copy under S, then copy the sources into W.
  - Before you finish, delete `build/`, `.gradle/` and `.kotlin/` from the copies in W.

## Don't edit shared files
- Off limits: `W/assets/*` (existing ones), `W/.build/bin/*`, `W/reference/*`, `NOTES.md`, `RESOURCES.md`, and other lessons and templates.
  - The only exception is the L32 fork, whose prompt lists exactly what it may edit.
- New styles go in a `<style>` block in your lesson's `<head>`; report them.
- You MAY create ONE new asset file, but only with the name assigned in your prompt, so siblings never collide.
  - It must inject its own CSS using the theme tokens in `assets/course.css` (`assets/gitgraph.js` and `assets/graphviz.js` are models).
  - It must work in light and dark mode and fit at phone width.
- I integrate links, nav, notes, the roadmap and the reference sheets afterwards.

## Tools
- **Kotlin compiler (durable copy, sha256-verified, v2.4.20):** `export KOTLINC="W/.build/tools/kotlinc/bin/kotlinc"`, then `W/.build/bin/run.sh File.kt File.java …`.
  - The serialization plugin is `W/.build/tools/kotlinc/lib/kotlinx-serialization-compiler-plugin.jar`.
  - `run.sh` compiles into a private `mktemp -d`, so parallel runs are safe. It takes no classpath; compile with `-cp` yourself when you need a library.
- **Libraries:** checksum-verified jars in `W/.build/libs/`. Never download other copies. In Gradle, use the same coordinates.
  - kotlinx-coroutines 1.11.0 (+ test)
  - kotlinx-serialization 1.12.0 (core + json)
  - Jackson 3.2.4 (`tools.jackson.*`) + annotations 2.22
  - H2 2.5.252
  - sqlite-jdbc 3.53.4.0 (run with `--enable-native-access=ALL-UNNAMED`)
- **System tools:**
  - JDK 26.0.1 (`java File.java`, `javap`, `jshell`); Gradle 9.7.1
  - `/usr/bin/sqlite3` 3.51.0: always call it by full path
  - `/usr/bin/jq` 1.7.1, BSD `sed`/`awk`, bash 3.2 (macOS default shell is zsh)
  - Apple clang 21, `ssh-keygen`
  - `shellcheck` is NOT installed; don't install anything
- **Servers:** bind to `127.0.0.1` and shut themselves down. Confirm with `lsof -iTCP -sTCP:LISTEN -n -P` before finishing. Use `perl -e 'alarm N; exec @ARGV'` for timeouts.
- **Durable copies of JetBrains/other downloads, if you need any:** put them under `W/.build/tools/`, and verify a checksum.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings:** show the real numbers and say so ("on my machine, an Apple M5 Max; yours will differ"). Use warm-up runs and never claim precision you don't have. L16 taught warm-up and sinks; L24 taught JMH. Link them.
- **Facts:** verify every factual claim against an official or canonical source with WebFetch/curl, and cite it inline. Check redirects and `<title>`.
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and the Kotlin API reference, the JLS/JVMS, JEPs, RFCs, man pages, and the primary sources listed per lesson below.
  - Books (CLRS, *CS:APP*, *DDIA*, Sedgewick & Wayne, *Effective Java*) may be cited by chapter or section, but only after checking the chapter title on the publisher's or author's site. If you can't verify a chapter number, cite the chapter by title.
  - Never quote from memory: a quote must be copied from a fetched page.
- **Theory claims** (proofs, theorems, complexity classes) must also be checked against a canonical text. Where a short proof is shown, it must be correct and complete for the stated claim; say so when you only sketch it.

## Match the existing lessons exactly in structure and style
First read `W/lessons/0021-graphs-and-dynamic-programming.html`, `W/lessons/0033-security-basics.html` and their templates in `W/.build/tpl/`.
- **Top:** kicker; h1; subtitle; `.lesson-meta` with `#resume`; `.win`; `.toc` with `data-part`.
- **Warm-up:** recalls earlier lessons (`.warmup` plus quizzes). Interleave several earlier lessons, not just the previous one.
- **3–4 parts**, each with:
  - Kotlin/Java `.pair` code and real `.out`/`.err` blocks
  - a "What Kotlin does for you" `.auto` box, or an honest note when both languages are the same
  - an `.exercise` with `details.solution`
  - a Check quiz and a `label.done` checkbox
- **Final exercise** with `.rating` rows (KOTLIN, JAVA, plus a third row where your prompt says so).
- **End matter:**
  - an exit quiz interleaving earlier lessons
  - the "Build my results" summary and a midweek recall card
  - a `.primary` source box and a reference-sheet link (sheet named in your prompt; I'll write or extend it)
  - `.ask`, `.lesson-nav` prev/next in STUDY order (given in your prompt), and a reset button
- **Scripts:** prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.
- **Optional deep dives:** if a section goes beyond the lesson's core, label its heading with `<span class="deep-tag">Optional deep dive</span>` (style already in `course.css`). Keep the core near 2 hours.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns: `Prints true` / `Prints false` / `Throws exception` / `Compile error`, or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains the answer, with a citation where relevant.

## Level and size
- **Learner:** a CS student with basic programming experience who mostly codes with AI and wants to code by hand. The mission is in `W/MISSION.md`. Destinations are Android and Spring Boot.
- **Code:** all of it in Kotlin and Java, always pointing out what Kotlin does automatically that Java makes explicit.
  - Shell, SQL and assembly appear where the topic needs them, alongside the Kotlin/Java versions.
- **Length:** the core is about 100–120 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- **Theory lessons (L48, L51):** keep them concrete. Every idea gets a runnable program that checks or demonstrates it. No proofs-for-their-own-sake.
- **Bridge:** add an Android/Spring "bridge" aside where it's natural, without teaching the framework.

## QA before you finish
- `python3 "W/.build/bin/qa.py" "W"` (pass the ABSOLUTE path): your lesson must print OK. Allowed missing links: your reference sheet if it's new, and sibling v5 lessons not yet built.
- `python3 "W/.build/bin/chrome_qa.py" "W"` (ABSOLUTE path; a relative one gives NO RESULT): your lesson must show `sw` ≤ 500 and `errs=[]`.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Durable reference-sheet parts
Write your 2–3 reference-sheet `<section>` blocks to `W/.build/refparts/lNN.html` (plain HTML, the same format as the sections in `W/reference/data-structures-and-algorithms.html`). Keep each `<code>` cell under ~35 characters, because sheet columns are narrow. The L53 fork writes 4–5 sections, because the shell gets its own sheet.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. The path of your refparts file.
4. Rebuild steps, if not just `build.py`.
5. Anything unverified.

## Study order and nav (prev → lesson → next)
L15 → **L48** → L16 … L21 → **L49** → **L50** → **L51** → **L52** → L22 → **L53** → L23 → **L54** → L24 … L33 → **L55** → L34 … L36 → **L56** → L37 … L43 → **L57** → L44.
In warm-ups and callbacks, a learner at lesson X has done every lesson before X in this order. For example, L52's learner has done L48–L51 but not L22.

## The v5 outline (shared, so warm-ups and callbacks reference the right material)
- **L48 Maths for Computer Science** (`0048-maths-for-computer-science.html`, after L15, before L16; sheet `maths-and-theory.html`, asset `mathviz.js`):
  - **Logic and sets:** propositions, implication and its contrapositive, De Morgan (callback: `if` conditions, L3), sets ↔ `Set` operations; quantifiers as `all`/`any`/`allMatch`.
  - **Induction:** proof by induction and strong induction, shown to be the same argument as "why a recursive function is correct" (a preview of L18). Loop invariants checked at runtime with Kotlin `check()` and Java `assert` (`-ea`).
  - **Counting and probability:** sum and product rules, permutations and combinations (overflow-safe binomial via `Long`/`BigInteger`, callback L2), pigeonhole (why hash collisions are certain, previewing L19). Probability and expected value, the birthday paradox computed exactly and by Monte Carlo simulation (seeded `Random`).
  - **Logs, sums and modular arithmetic:**
    - logs as "how many halvings" (previewing binary search and Big O)
    - arithmetic and geometric series (1+2+…+n; 1+2+4+…: why doubling arrays are amortised O(1), previewing L16)
    - `%` vs `Math.floorMod` with negative numbers (JLS §15.17.3), modular exponentiation, gcd (Euclid)
  - **Bits as tools:** `and`/`or`/`xor`/`inv`/`shl`/`shr`/`ushr` vs `& | ^ ~ << >> >>>`, masks and flags, `Integer.bitCount`/`highestOneBit`, power-of-two tests, `EnumSet` as a bit vector, the `h ^ (h >>> 16)` spread previewed for L19.
  - **Final:** prove a loop correct with an invariant (e.g. integer power or binary-search-like loop), then write tests and a runtime-checked invariant that confirm it.
  - **Primary source:** MIT 6.042J *Mathematics for Computer Science* (Lehman, Leighton, Meyer), free on OCW.
- **L49 Greedy, Shortest Paths & Union-Find** (`0049-greedy-shortest-paths-and-union-find.html`, after L21; sheet `data-structures-and-algorithms.html`, asset `pathviz.js`):
  - **Greedy:** interval scheduling (earliest finish), proved with an exchange argument; when greedy fails (L21's coin change); Huffman named.
  - **Dijkstra built for real:** with `PriorityQueue` and lazy deletion (callback L20's heap), path reconstruction, O(E log V), why negative edges break it (a real counterexample run), Bellman–Ford named; A* named.
  - **Union-find:** quick-find → weighted quick-union → path compression, timed. Kruskal's MST on it; Prim named.
  - **Final:** a route planner over a small real weighted graph (e.g. algs4 `tinyEWD.txt`/`mediumEWD.txt`, verified by download and checksum), test-first.
  - **Primary source:** Sedgewick & Wayne §1.5, §4.3, §4.4; Erickson *Algorithms* chapter on greedy algorithms.
- **L50 Pointers, Backtracking & Tries** (`0050-pointers-backtracking-and-tries.html`; sheet `data-structures-and-algorithms.html`, asset `windowviz.js`):
  - **Two pointers:** pair-sum on a sorted array, in-place dedupe.
  - **Sliding window:** fixed and variable windows, longest substring without repeats, and a monotonic deque for window maximum (callback L17's `ArrayDeque`); O(n²) → O(n) timed.
  - **Backtracking:** subsets, permutations, N-queens with pruning (counts verified against OEIS A000170); its exponential cost measured.
  - **Tries:** insert, contains, prefix search, autocomplete; memory vs a `HashMap` of all prefixes.
  - **A spaced practice track:** about 15 problems grouped by pattern across L16–L50, each with a link (algs4 exercises, Exercism Kotlin/Java tracks, and named LeetCode problems as optional extras), plus a schedule.
    - The mission says no interview grinding, so keep it modest and frame it as hand-coding practice.
  - **Final:** autocomplete ranked by word frequency from the L3 counter.
  - **Primary source:** Sedgewick & Wayne §5.2 (Tries); Erickson's chapter on backtracking.
- **L51 The Limits of Computation** (`0051-the-limits-of-computation.html`; sheet `maths-and-theory.html`, asset `automataviz.js`):
  - **Finite automata:** DFAs as state machines in code (sealed states, callback L7); NFA and Thompson's construction.
  - **What a regex is (callback L2):** catastrophic backtracking in `java.util.regex` measured (ReDoS, OWASP), and why an NFA simulation stays linear.
  - **Turing machines:** a tiny TM simulator; the halting problem as a code-shaped diagonal argument (`halts(p)` contradiction); Rice's theorem in one paragraph (why no linter can be perfect).
  - **Complexity classes:** P, NP, NP-complete, reductions; SAT, TSP and knapsack.
    - What to do in practice: exact for small n, DP (callback L21), heuristics and approximations.
    - Measure brute-force TSP vs nearest-neighbour on small n.
  - **Final:** a regex engine (literals, `.`, `*`, `+`, `?`, `|`, parentheses) via Thompson NFA, differential-tested against `java.util.regex`, linear on the pathological pattern where `java.util.regex` explodes.
  - **Primary source:** Russ Cox, "Regular Expression Matching Can Be Simple And Fast"; Erickson's chapter on NP-hardness; Turing 1936 by title.
- **L52 From Gates to Instructions** (`0052-from-gates-to-instructions.html`, before L22; sheet `runtime-and-os.html`, asset `cpuviz.js`):
  - **Gates to an adder:** gates as boolean functions; half and full adder; an 8-bit ripple-carry adder checked against `+` for all 65,536 pairs; subtraction via two's complement (callback L2, L48 bits).
  - **A toy CPU:** registers, ALU, program counter, fetch–decode–execute loop for a small ISA (e.g. a Hack-like or 8-instruction register machine), with programs assembled by hand.
  - **Assembly vs bytecode:** real ARM64 assembly from `clang -S -O1` for a tiny C function, labelled as C for illustration; register machine vs the JVM's stack machine (javap preview for L23); ART is register-based (previewed for L23).
  - **Pipelining and branch prediction:** the sorted-vs-unsorted branchy sum measured on the JVM, reporting honestly if the JIT removes the branch (e.g. a conditional select). Caches are previewed for L22.
  - **Final:** a toy CPU emulator running a hand-assembled multiply-by-repeated-addition or sum 1..n program, with tests.
  - **Primary source:** Nand2Tetris (free course and book chapters 1–5); *CS:APP* ch. 3–4 by title.
- **L53 The Shell & the Unix Toolkit** (`0053-the-shell-and-the-unix-toolkit.html`, after L22; NEW sheet `shell.html`, asset none unless needed, name `shellviz.js`):
  - **Pipes and text tools (callback L1):** `grep -E/-r/-c`, `sed` (BSD `-i ''` vs GNU), `awk` fields and sums, `sort | uniq -c | sort -rn`, `jq` on JSON, `xargs`; the same pipeline as a Kotlin/Java program and via `ProcessBuilder`.
  - **Permissions:** rwx bits as octal (callback L48 bits), `chmod`, `umask`, the executable bit and shebangs.
  - **Environment:** env vars and `PATH` lookup; `System.getenv` and `ProcessBuilder.environment()`.
  - **Processes and signals:** `ps`, `kill`, exit codes and `$?`, `SIGINT`/`SIGTERM`/`SIGKILL`. A real capture showing that a shutdown hook runs on SIGTERM but not on SIGKILL (callback L22 processes; previews L46's graceful shutdown).
  - **SSH and scripts:** SSH keys with `ssh-keygen -t ed25519` (callback L33 Ed25519), `~/.ssh` permissions, `known_hosts`, the agent. Safe scripts: `set -euo pipefail`, quoting, `"$@"`, traps.
  - **Final:** a script that answers questions about a JSON access log (top paths, error counts, slowest requests), cross-checked by a Kotlin/Java program giving the same answers.
  - **Primary source:** MIT Missing Semester (shell tools, data wrangling, command-line environment); Bash manual; jq manual; OpenSSH man pages.
- **L54 How Languages Work** (`0054-how-languages-work.html`, after L23; sheet `runtime-and-os.html`, asset `parseviz.js`):
  - **Scanning:** text → tokens (sealed `Token`), with errors that carry positions.
  - **Parsing:** a grammar in EBNF; recursive-descent parsing with precedence and associativity into a sealed AST. Kotlin `when` vs Java pattern-matching `switch` on sealed records (JEP 441).
  - **Interpreting:** a tree-walking interpreter with variables (`let x = 3; x * (x + 1)`) and runtime errors.
  - **Compiling:** a compiler to your own stack bytecode (PUSH/LOAD/STORE/ADD/MUL/…) plus a VM loop, compared with `javap` of the same Java expression (callback L23's operand stack).
  - **Final:** a calculator language with variables, both interpreted and compiled, with a differential test proving both agree on many generated programs.
  - **Primary source:** *Crafting Interpreters* (Scanning, Representing Code, Parsing Expressions, Evaluating Expressions, Chunks of Bytecode); JVMS ch. 6.
- **L32 new part: Time on the wire** (edit the existing L32; sheet `networks-and-web.html`):
  - **The types:** `Instant`, `LocalDate`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, `ZoneId`, and when to use which. Store instants for past events; store local time plus zone for future events, because tz rules change. Show the tzdb version.
  - **DST bugs, real runs:** a gap (2:30 on 2026-03-08 in America/New_York doesn't exist) and an overlap (1:30 on 2026-11-01 happens twice).
  - **The wire:** ISO-8601/RFC 3339 with offsets; kotlin.time `Instant` vs `java.time`.
  - **Clocks:** `Clock` injection (callback L38 if seen, else a preview); wall clock (`currentTimeMillis`, can jump) vs monotonic (`nanoTime`, `TimeSource.Monotonic`) for durations and timeouts.
  - **Primary source:** `java.time` package docs, RFC 3339, the IANA tz database, Kotlin time docs.
- **L55 Authentication & Authorization** (`0055-authentication-and-authorization.html`, after L33; sheet `networks-and-web.html`, asset `authviz.js`):
  - **Sessions and cookies:** server-side sessions; cookie attributes `HttpOnly`/`Secure`/`SameSite`/`Path`/`Max-Age` (RFC 6265); session fixation and rotating the session id on login.
  - **CSRF demo on localhost:** verify which host pairs count as cross-site; ports do NOT separate cookies. Defences: `SameSite`, synchronizer token, `Origin` check.
  - **JWT:** structure in base64url; HS256 by hand with HMAC (callback L33); verification pitfalls (`alg: none`, algorithm confusion, missing `exp`/`aud`/`iss` checks); revocation problems, hence short-lived access tokens plus rotating refresh tokens.
  - **OAuth 2.0 and OIDC:** the roles; the authorization-code flow with PKCE, with S256 implemented and checked against the RFC 7636 Appendix B test vector; a three-party simulation on localhost (authorization server, client, resource server); OIDC ID token vs access token; implicit flow deprecated (RFC 9700).
  - **Authorization:** 401 vs 403 (RFC 9110); RBAC vs ownership checks (IDOR, callback L33 broken access control); CORS preflight and why CORS is not an auth mechanism.
  - **Bridge (names only):** Spring Security's filter chain; Android Credential Manager / AppAuth.
  - **Final:** add login (PBKDF2 from L33), a session cookie, CSRF protection and per-user ownership checks to the L32 books server, with tests.
  - **Primary source:** OWASP Cheat Sheets (Session Management, Authentication, CSRF Prevention, JSON Web Token for Java); RFCs 6265, 7519, 6749, 7636, 9700; OIDC Core 1.0; MDN CORS.
- **L56 Storage Engines & Other Data Models** (`0056-storage-engines-and-other-data-models.html`, after L36; sheet `databases.html`, asset `logviz.js`):
  - **A Bitcask-style append-only log:** length-prefixed records with CRC32; an in-memory hash index (callback L19); recovery by scanning; tombstones; compaction/merge of segments.
  - **Durability:** `FileChannel.force` and the fsync cost measured, per write vs batched (group commit); a `kill -9` mid-write from a child process, with the torn tail detected by CRC (callback L53 signals).
  - **LSM-trees vs B-trees:** memtable (`TreeMap`/`ConcurrentSkipListMap`), sorted SSTables and merging, a Bloom filter built and its false-positive rate measured vs the formula (callback L48 probability); read/write/space amplification vs B-trees (callback L35).
  - **Data models:** document vs relational (joins, schema-on-read), key-value, wide-column, graph; friends-of-friends with `WITH RECURSIVE` in SQLite. Products named from official docs only.
  - **Final:** a key-value store that survives `kill -9` mid-write, with tests.
  - **Primary source:** *DDIA* 2nd ed. (Storage and Retrieval; Data Models and Query Languages); the Bitcask paper (Basho, 2010); SQLite "Atomic Commit"; CMU 15-445.
- **L57 Time, Order & Agreement** (`0057-time-order-and-agreement.html`, after L43; sheet `system-design.html`, asset `raftviz.js`):
  - **Why clocks lie:** skew and NTP (callback L32's time part); last-writer-wins with skewed clocks silently loses a write (callback L43's LWW).
  - **Logical clocks:** Lamport clocks and happens-before (callback L27, same name, different scope); vector clocks detecting concurrent writes.
  - **Consensus:** why it's needed (leader election, split brain). Raft simulated deterministically in one JVM with seeded message delays: terms, RequestVote, heartbeats, randomized timeouts, plus simplified log replication with majority commit.
    - Safety (at most one leader per term) checked over many seeds.
    - Partition and heal: the minority can't elect, the old leader steps down.
  - **Two-phase commit vs sagas:** 2PC's blocking on coordinator crash, simulated; sagas with compensating actions; the transactional outbox (callback L42 queues).
  - **Final:** a Raft election through a network partition, and watching it heal.
  - **Primary source:** the Raft paper (extended version, raft.github.io); Lamport 1978 "Time, Clocks, and the Ordering of Events in a Distributed System"; *DDIA* 2nd ed.
