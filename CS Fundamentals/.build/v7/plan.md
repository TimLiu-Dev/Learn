# v7 plan: senior-by-2027 pass (proposed 2026-11-21, awaiting approval)

Inputs: roadmap.sh/computer-science (188 topic nodes, from its GitHub data), seven senior-lens scope reviews (`reviews.md`), mission.

## 0. Already done (user asked to "add all these")
All 12 CS2023 additions from v6 are present (verified by grep): L40 branching/releases, L25 licences + SemVer/BOMs/modules, L33 personal data + five principles, L47 ethics, L2 Unicode p2, L48 Bayes/variance, L28 Amdahl/Little, L45 coverage/property tests, L44 accessibility.

## 1. roadmap.sh check
Covered: almost everything (DS, sorting, graphs, Dijkstra/Bellman-Ford/Kruskal, Prim/A*/Huffman paragraphs, tries, Big O, P/NP, caching incl. LFU, CDN, CAP/PACELC, load balancing, sharding, replication, queues, OSI/TCP-IP, DNS, HTTP, TLS, sockets, REST, SQL incl. window fns/CTEs, ACID, indexes, normalisation, GRANT, OWASP 2025, hashing/encryption/encoding, public-key crypto, processes/threads, locks/semaphores, CPU cache, registers/RAM, endianness, Unicode, DI, GoF patterns, tail recursion).
Gaps → added below: real-time web (polling/SSE/WebSockets), architecture styles, UML-style diagrams, balanced-tree rotations, Θ/Ω, interrupts + scheduling, fork/exec, file systems/inodes, gRPC/GraphQL depth, views/stored procedures, null-object/state/command/template-method, rolling hash.
Deliberately skipped (research-level for a working engineer): max-flow, k-d trees, suffix arrays, KMP/Boyer-Moore, knight's tour, longest path, co-NP, skip-list implementation, "pick a language" nodes (C/C++/Go/Rust/Python).

## 2. Two new lessons
- **L58 Software Architecture** (after L40, end of phase 11). Parts: layers + dependency rule (layered vs hexagonal, package-by-feature, enforced by Gradle modules + an architecture test); DDD essentials (ubiquitous language, bounded contexts, value objects, aggregates as transaction boundary); modular monolith vs microservices (Conway, cost of distribution, strangler fig); events (domain events, event-driven integration; event sourcing/CQRS named); diagrams as text (C4 context/container, sequence, state, ER in Mermaid) + ADRs. Final: split the library into lending + catalogue modules talking through events, with 2 ADRs.
- **L59 Shipping Safely** (after L43, phase 12). Parts: schema migrations by hand (schema_version, numbered scripts, checksums, transaction; SQLite ALTER limits + 12-step rebuild = Room); expand/contract with two versions live + batched backfill; deploy strategies (rolling/blue-green/canary, /live vs /ready, readiness flip, feature-flag kill switch, rollback); incidents (mitigate first, roles, comms) + blameless postmortem + SLO burn-rate alert. Final: rename a column with zero downtime while two app versions serve traffic, then write the postmortem of a scripted bad deploy.
Both get practices, code quizzes, whyopts, recall cards, sheets, roadmap entries (numbers L58/L59 keep all existing links).

## 3. Additions to existing lessons (HIGH + MED; cheap LOWs marked *)
- L1: git undo (restore, restore --staged, amend) + private remote backup.
- L2: BigDecimal division needs a RoundingMode; splitting a bill.
- L5*: NaN / -0.0 equality.
- L6: nested vs inner classes (Kotlin nested by default), leak link to L24.
- L7: `@JvmInline value class`; rename "Value classes three ways" to avoid the keyword clash. *Comparable consistent with equals.
- L8*: bridge line final-by-default → kotlin-spring/all-open, inline mocks.
- L9*: Spring 7 JSpecify bridge (verify first).
- L10*: log-or-rethrow; no exceptions for control flow.
- L11: flaky tests and their fixes (inject Clock, seeds, time zone/locale).
- L12: attach a debugger to a running JVM (JDWP); *debug a failing test.
- L14*: checked exceptions in lambdas, UncheckedIOException.
- L16: Big-Θ / Big-Ω, separate from best/worst/average; fix the "O(n) means worst case" sentence; amortised is not probabilistic.
- L18: Ω(n log n) comparison-sort lower bound; counting/radix get past it.
- L19*: hash flooding.
- L20: how balancing works (one rotation, AVL vs red-black invariants, B-tree link). Fix Dijkstra pointer L21→L49.
- L21*: 3-colour DFS cycle detection; SequencedCollection/SequencedMap.
- L50: prefix sums (+ subarray-sum-k); optional Rabin–Karp rolling hash.
- L52: CALL/RET + stack pointer in the toy CPU; interrupts + user/kernel mode.
- L22: timer interrupt, preemption, run queue, context-switch cost, load average; containers = namespaces + cgroups; page cache.
- L53: fork/exec, inherited env/fds, zombies/orphans, PID 1, `exec java`; inodes + hard vs symbolic links; lsof/top/df/du/tail -f/less.
- L23: GraalVM Native Image (closed world) vs Leyden AOT cache.
- L54: (optional part) name resolution + type checking walk; K2 FIR/IR.
- L24: JVM memory in containers (RSS breakdown, 25% default, MaxRAMPercentage, OOME vs OOM-kill).
- L25: Kotlin compiler plugins vs KSP vs reflection; *configuration cache, convention plugins.
- L26: ReadWriteLock (+ StampedLock mention).
- L28: Fork/Join + parallel streams. *ScopedValue example.
- L29: suspendCancellableCoroutine + callbackFlow; Channels producer–consumer. *stateIn/shareIn mention.
- L31: new part "Beyond request/response": polling, long polling, SSE (HttpServer), WebSocket handshake by hand + JDK client.
- L32: REST vs gRPC vs GraphQL (replaces one paragraph), optional protobuf varint; *webhooks.
- L33: SSRF + Java deserialization; HMAC practice reframed as webhook signature verification.
- L55: passkeys/WebAuthn (optional part, Ed25519 challenge + origin); asymmetric JWT + JWKS + kid rotation; *TOTP; *client credentials.
- L34*: CREATE VIEW; stored procedures, when and why not.
- L35: keyset pagination with EXPLAIN; pointer to L59 for migrations; *crow's foot.
- L36*: ON CONFLICT upsert; FOR UPDATE SKIP LOCKED.
- L56: full-text inverted index (why LIKE '%x%' can't use a B-tree); *vector search paragraph.
- L37: seams + sprout/wrap; technical debt as a decision, strangler fig.
- L38: name the final as ports-and-adapters + composition root.
- L39: State pattern / sealed state machine; Command; *Template Method, Null Object.
- L40: "Working with AI as a senior: delegate, verify, own" (tests first, weakened tests, slopsquatting, never-delegate list).
- L41*: CDN as outermost tier.
- L43: incident basics moved to L59; *load shedding.
- L57: fencing tokens; *LWW/version vectors → offline-first, CRDTs named.
- L44: ADRs in the design doc (taught in L58), Little's law → server/pool counts, monthly cost line, SLO + rollback.
- L45: API evolution (tolerant reader, additive change, Deprecation RFC 9745 / Sunset RFC 8594); consumer-driven contract tests vs shared suite.
- L46: apply L59 migrations to Shelf; /live vs /ready; *coordinated omission.
- L47: new part "From here to senior" (scope, writing, estimating, slicing, saying no, mentoring, evidence portfolio, directing AI agents, honest note about production ownership).

## 4. Trims (funds the projects)
- L2: Unicode part two → optional (keep Locale.ROOT + three lengths).
- L25: licences section shorter.
- L37: IntelliJ shortcut table + quiz.
- L40: SemVer → 3-min recap (L25 owns it); shorten changelog/semantic-release; autosquash practice optional.
- L44: final relabelled honestly (3–4 h) or code optional.
- L45: capstone built fully in Kotlin; one slice (loans) ported to Java; final relabelled honestly.
- L46: DbDemo pool measurements optional.
- L47: Leitner scheduler coding + security.txt practice cut (schedule stays as a table).
- L51: Turing-machine tape simulator + subset construction → optional.
- L53: .pub internals + SSH signing → optional.
- L57: final → run provided Raft sim, write the safety argument, break the up-to-date check; full log replication becomes optional stretch. ElectionTiming sweep optional.

## 5. Outdated fixes
- L11: JUnit 6.0.1 → 6.1.x; Gradle 9.7.1 → 9.9.x (re-check at build time).
- L31: certificate lifetime (CA/B SC-081: 200 days since 2026-03-15, 100 in 2027, 47 in 2029); PQ key exchange default in JDK 27 (JEP 527).
- L47: "Spring Boot 4.1.2 default" → current 4.x; Java 25 LTS on start.spring.io; capstone toolchain 21 → 25.
- L55: OAuth 2.1 still a draft.

## 6. Mini projects (between phases; 5–8 h each; Kotlin OR Java, learner's choice)
Each: a project page in `projects/`, milestones with acceptance criteria, a language-agnostic black-box checker (`java Check.java …` drives the learner's CLI/socket/HTTP program), Kotlin + Java Gradle starters, hints per milestone, a senior self-review rubric, stretch goals, reference solutions in both languages (checker must pass both and fail seeded bugs).
- **M1 Budget CLI** — after L15 (phases 1–5). Import a bank-statement CSV, categorise by rules, monthly reports; sealed commands/results, generics, pipelines, test-first on branches. First 500-line program from a blank file.
- **M2 Mini Search Engine** — after L51 (phase 6). Tokeniser, inverted index, trie autocomplete, top-k heap ranking, doubling benchmarks.
- **M3 Performance Clinic** — after L25 (phase 7). A provided slow, leaky log indexer: make it ≥10× faster and fix the leak, with profiler evidence and a one-page write-up.
- **M4 Parallel Job Runner** — after L29 (phase 8). Bounded queue, backpressure, retries with backoff, timeouts, cancellation, drains on SIGTERM; executors + virtual threads, then coroutines.
- **M5 Chat Server** — after L55 (phase 9). Own length-prefixed TCP protocol, many clients, slow-client handling, token login, SSE/WebSocket bridge for a browser.
- **M6 The Ledger That Never Loses a Cent** — after L56 (phase 10). SQLite + JDBC, transfers under concurrency, isolation, optimistic locking, idempotency keys, migrations; checker hammers it and audits the invariant.
- **M7 Legacy Rescue** — after L58 (phase 11). A messy ~1,500-line program: characterisation tests, seams, refactor into hexagonal modules, ADRs, land as reviewed PRs.
(Considered, not proposed: offline-first sync client (Android mission), tiny shell (novelty), order pipeline w/ outbox (overlaps L42/L59).)

## 7. Size
~150 h now. + 2 lessons (~5 h) + additions (~10 h) + 7 projects (~45 h) − trims (~15–20 h, mostly L45/L57/L47) ≈ 195 h, ~9–12 months at 4–7 h/week.

## 8. Process (same discipline as v6)
Builders per lesson/project (no forks; ≤10 agents at a time), then independent review of every touched lesson, then automated check (pg_check, qa --strict, cq_answers, manifest, chrome_qa, sheet_fit, nav walk) + project checkers against both reference solutions and seeded bugs. Tracking in `.build/v7/`.

## Approved 2026-11-21
User chose: full plan; all 7 projects; capstone Kotlin fully + Java slice; electives: all four groups "and more … networking, database, operating systems, ai, algorithms and data structures etc".

## 9. Electives track (E01–E28): optional, ~60–90 min each, 2–3 parts, same components as core lessons
File: `lessons/eNN-slug.html`, `data-lesson="eNN"`, build dir `.build/eNN/`, template `.build/tpl/eNN.html`. Not in the core study chain; each names the lesson to take it after.

Algorithms & data structures
- E01 `e01-balanced-trees` Balanced Trees by Hand: AVL implemented (rotations, height invariant), red-black as 2-3 trees (left-leaning RB insert), treap. After L20.
- E02 `e02-string-algorithms` String Algorithms: KMP (failure function), Rabin–Karp (rolling hash, multi-pattern), Boyer–Moore–Horspool, suffix arrays (naive build + LCP idea, binary search). After L50.
- E03 `e03-advanced-graphs` Advanced Graphs: strongly connected components (Tarjan or Kosaraju), Floyd–Warshall, A* implemented, max-flow (Edmonds–Karp) and bipartite matching. After L49.
- E04 `e04-range-and-spatial` Range & Spatial Structures: 2-D prefix sums, Fenwick tree, segment tree (lazy idea), k-d tree nearest neighbour. After L50.
- E05 `e05-randomised-structures` Randomised & Probabilistic Structures: quickselect, reservoir sampling, skip list implemented, count-min sketch, HyperLogLog. After L50.
- E06 `e06-compression` Compression & Information: entropy, Huffman coding implemented (encode/decode, bit I/O), LZ77, how DEFLATE/gzip combine them. After L49.
- E07 `e07-lock-free` Lock-Free Data Structures: CAS loops, ABA, Treiber stack, Michael–Scott queue, VarHandle memory ordering, false sharing. After L28.
- E08 `e08-functional-data-structures` Functional Data Structures: persistent lists and trees, structural sharing, HAMT (how persistent maps work), Result/Either composition. After L20.
AI & machine learning
- E09 `e09-machine-learning` Machine Learning by Hand: linear regression with gradient descent, logistic classification, train/test split and overfitting, precision/recall (links L48 Bayes). After L48.
- E10 `e10-neural-nets-and-llms` Neural Networks & How LLMs Work: a tiny MLP with backprop, tokenisation (BPE implemented), embeddings, attention at a high level, sampling (temperature, top-p), context windows. After E09.
- E11 `e11-building-with-llms` Building with LLMs: calling an LLM HTTP API from the JDK HttpClient (JSON, SSE streaming), tool calling loop, RAG over your own index, evals for nondeterministic output, prompt injection and data exfiltration defences, cost/latency. After L55. (Practice must not need an API key: use a local fake server.)
- E12 `e12-vector-search` Vector Search: cosine/dot product, exact kNN, LSH, HNSW intuition (small graph search), quantisation, recall vs latency. After E10.
Networking & security
- E13 `e13-tcp-in-depth` TCP & the Network in Depth: flow control, congestion control (slow start, AIMD), Nagle/delayed ACK, head-of-line blocking, HTTP/2 multiplexing, QUIC/HTTP/3, reading tcpdump. After L31.
- E14 `e14-rpc-beyond-rest` RPC Beyond REST: protobuf wire format by hand (varints, tags), gRPC semantics (deadlines, streaming, status codes), GraphQL execution, resolver N+1 and dataloader batching. After L32.
- E15 `e15-applied-cryptography` Applied Cryptography: TLS 1.3 handshake walk-through, certificate chain validation, AEAD (AES-GCM) and nonce reuse, envelope encryption/KMS, post-quantum key exchange. After L55.
Operating systems & runtime
- E16 `e16-os-internals` OS Internals, Simulated: scheduling algorithms (FIFO, SJF, RR, MLFQ, CFS idea), paging and page replacement (FIFO, LRU, clock), file systems (inodes, journaling, crash consistency). After L22.
- E17 `e17-containers-and-orchestration` Containers & Orchestration Concepts: namespaces and cgroups, images as layers, a reconciliation loop built by hand (desired vs actual state), health checks, service discovery. After L42. (No Docker/cloud needed.)
- E18 `e18-jvm-at-the-edge` The JVM at the Edge: FFM API (off-heap memory, calling a C function), Vector API (SIMD, incubator), GraalVM Native Image vs Leyden AOT cache. After L24.
- E19 `e19-kotlin-multiplatform-and-wasm` Kotlin Multiplatform & WebAssembly: expect/actual, shared modules, what KMP shares on Android/iOS/server, Kotlin/Wasm. After L25.
Databases & data
- E20 `e20-query-engine` Inside a Query Engine: parse → logical plan → physical plan, iterator (Volcano) model, join algorithms (nested loop, hash, sort-merge) implemented, cost-based choices. After L35.
- E21 `e21-mvcc` MVCC & Concurrency Control by Hand: two-phase locking, MVCC snapshot isolation implemented, write skew, serializable snapshot isolation idea. After L36.
- E22 `e22-search-engines` Search Engines: tokenisation, inverted index, BM25 ranking implemented, phrase queries with positions. After L56.
- E23 `e23-columnar-and-analytics` Columnar Storage & Analytics: row vs column layout, encodings (dictionary, RLE, bit-packing), OLTP vs OLAP, Parquet ideas, warehouse vs lake. After L56.
Distributed systems
- E24 `e24-raft-log-replication` Raft Log Replication, Built: the full build moved out of L57's final (nextIndex/matchIndex, commit rule, chaos tests). After L57.
- E25 `e25-crdts-and-offline-first` CRDTs & Offline-First Sync: G-counter, PN-counter, LWW-register, OR-set, sync protocol, Android offline-first link. After L57.
- E26 `e26-stream-processing` Stream Processing: partitioned logs, consumer groups and offsets, exactly-once via idempotence + transactions, windows and watermarks implemented. After L57.
Engineering practice
- E27 `e27-fuzzing-and-formal-methods` Fuzzing & Formal Methods: a fuzzer for L54's parser, differential testing, model checking a small protocol by exhaustive state search, a taste of TLA+. After L57.
- E28 `e28-observability-in-practice` Observability in Practice: OpenTelemetry model (traces, spans, metrics, logs), W3C traceparent propagation implemented, histograms and percentiles (HDR idea), sampling. After L43.
