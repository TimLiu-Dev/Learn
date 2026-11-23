# v7 senior-lens scope reviews (2026-11-21)

Read-only reviewers, one per block. Raw reports, condensed. roadmap.sh CS gap list in plan.md.

## L19 L20 L21 L49 L50 L51 L52 L22 L53
- L19: [ADD LOW] hash flooding para (why treeification exists).
- L20: [ADD MED 15m] "How balancing works": one rotation, AVL vs RB invariants, B-tree = disk balanced tree (→L35). No implementation. [FIX LOW] heaps bridge says Dijkstra in L21 → L49.
- L21: [ADD LOW 5m] 3-colour DFS cycle detection. [ADD LOW 3m] SequencedCollection/SequencedMap (Java 21) in collection table.
- L49: nothing; optional A* practice (LOW).
- L50: [ADD MED 15m] prefix sums + subarray-sum-k (count map). [ADD LOW-MED 15m optional] Rabin–Karp rolling hash. Skip KMP/BM/suffix arrays. [PROJECT HIGH] mini search engine after L50.
- L51: [TRIM LOW] mark TM tape sim + subset construction optional.
- L52: [ADD MED 20m] CALL/RET + stack pointer in toy CPU. [ADD MED 10m] interrupts + user/kernel mode.
- L22: [ADD HIGH 15m] timer interrupt, preemption, run queue, context switch cost, load avg. [ADD HIGH 20m] containers = processes: namespaces + cgroups, JVM container awareness, MaxRAMPercentage, exit 137. [ADD MED 10m] page cache (→L56 fsync).
- L53: [ADD HIGH 15m] fork/exec, inheritance of env/fds, zombies/orphans, PID 1, `exec java` in entrypoints. [ADD MED 15m] inodes, hard vs symlinks. [ADD MED 10m] lsof -i, top, df/du, tail -f, less. [TRIM LOW] .pub internals + SSH signing optional. [PROJECT MED] tiny shell.

## L1–L9
- L1: [ADD MED 10m] git undo (restore, restore --staged, commit --amend) + push to private remote as backup (Part 4).
- L2: [TRIM MED] make Unicode part two optional (keep Locale.ROOT + three lengths). [ADD MED 8m] BigDecimal.divide needs RoundingMode (HALF_EVEN), leftover cent when splitting.
- L3/L4: nothing. [PROJECT] after L3 possible CLI tool.
- L5: [ADD LOW 5m] NaN/-0.0 equality, Kotlin == on Any/Double?.
- L6: [ADD MED 12m] nested vs inner classes (Kotlin nested by default; Java inner by default) — later lessons rely on it; Android leak source.
- L7: [ADD MED 15m] @JvmInline value class; FIX naming clash "Value classes three ways" vs Kotlin value class keyword (L37 says "Cents value class"). [ADD LOW 8m] Comparable consistent with equals (BigDecimal HashSet vs TreeSet).
- L8: [ADD LOW] bridge: final-by-default → kotlin-spring/all-open plugin, inline mocks.
- L9: [ADD LOW-MED 2m] Spring 7/Boot 4 JSpecify annotations (UNVERIFIED — check).
- [PROJECT] test-first domain-model project after L12.

## L57 L44 L45 L46 L47 (+ project proposals)
- L57: [TRIM HIGH] final = full Raft log replication labelled 25 min (really days) → run provided sim, write safety argument, break up-to-date check; full build optional stretch. [TRIM MED] ElectionTiming sweep optional. [ADD HIGH 20m] fencing tokens (Kleppmann). [ADD LOW 10m] LWW/version vectors → offline-first Android, name CRDTs.
- L44: [ADD HIGH 20m] ADRs (Nygard), 3 ADRs from shortener alternatives. [ADD MED 15m] diagrams as text: Mermaid sequence + ER. [ADD MED] Little's law → servers/threads/pool, one line monthly cost. [ADD LOW] SLO + error budget, rollback. [TRIM MED] final "30 min" is 3–4 h → relabel / code optional. [TRIM LOW] reason-phrase quirks para.
- L45: [TRIM HIGH] full build in both languages (~620 kt + 945 java lines) → full Kotlin, port loans slice to Java; relabel final honestly (8–15 h). [ADD MED 25m] API evolution: tolerant reader, additive changes, Deprecation (RFC 9745)/Sunset (RFC 8594). [ADD MED 10m] consumer-driven contract tests distinct from shared abstract suite.
- L46: [ADD HIGH 40m] schema migrations by hand: schema_version, numbered files, checksums; expand/contract rename + batched backfill. [ADD HIGH 20m] deploy strategies rolling/blue-green/canary with two Shelf instances behind L42 LB; readiness flip. [ADD MED 10m] /live vs /ready. [ADD MED 20m] blameless postmortem on Race 2 + SLO burn-rate alert. [ADD LOW] coordinated omission. [TRIM MED] DbDemo pool measurements optional.
- L47: [TRIM HIGH] Leitner scheduler coding (~35m) + security.txt practice. [ADD HIGH 35m] "From here to senior". [OUTDATED MED] Spring Boot "4.1.2 default" → "current 4.x" (4.2 GA ~Nov 2026). [OUTDATED LOW-MED] JDK 21 toolchain → 25 LTS.
- Projects proposed: Workout Log CLI (after P4/5); Mini Search Engine (after P6); Parallel Log Pipeline (after P7/8); TCP Chat own protocol (after P9); Schema Evolution kata (after P10); Legacy Rescue (after P11); Offline-first sync client (after Shelf).

## L23 L54 L24 L25 L26 L27 L28 L29
- L23: [ADD MED 8m] GraalVM Native Image closed-world vs Leyden AOT cache; Native Image off Java SE train Sept 2025.
- L54: [ADD MED 15m optional] name resolution/type checking walk; K2 FIR frontend + IR backend; compiler plugins hook.
- L24: [ADD MED 10m] JVM in containers: RSS = heap+metaspace+stacks+code cache+direct; default max heap 25% of limit; MaxRAMPercentage; OOME vs OOM-kill. (overlaps L22 containers item — put concept in L22, JVM sizing in L24) [ADD LOW] compact object headers JEP 519.
- L25: [ADD MED 10m] Kotlin compiler plugins row (Compose, kotlinx.serialization, all-open) vs KSP; reflection→build-time codegen trend. [ADD LOW] Gradle configuration cache, convention plugins. [TRIM MED] licences shorter/optional (lesson 185–200).
- L26: [ADD LOW-MED 10m] ReentrantReadWriteLock row + StampedLock mention; why not for LRU.
- L27: [ADD LOW] wait/notifyAll in while loop as monitor Condition.
- L28: [ADD HIGH 20m] Fork/Join + parallel streams (common pool, splitting, side effects; AI-diff smell). [ADD LOW] ScopedValue example (final JDK 25, JEP 506).
- L29: [ADD HIGH 20m] suspendCancellableCoroutine + callbackFlow/awaitClose. [ADD MED 10m] Channels producer-consumer. [ADD LOW] stateIn/shareIn, flatMapLatest one-liners. [PROJECT HIGH] concurrent crawler/job runner after L29 (executors+VT+Semaphore vs coroutines).

## L30 L31 L32 L33 L55 L34 L35 L36 L56
Already covered (my grep missed): window fns + CTEs (L34), optimistic locking (L36), offset vs cursor paging (L32), OSI + HTTP/2/3 (L30/31), GRANT (L33), informal ER (L35), OWASP 2025.
Time totals: L30 144, L31 144, L32 172, L33 185, L34 152, L35 159, L36 154, L55 162, L56 162.
- L30: nothing.
- L31: [ADD HIGH 30m] new part "Beyond request/response": short/long polling, SSE (HttpServer chunked + ofLines; LLM streaming), WebSockets (hand handshake on raw sockets; java.net.http.WebSocket client). [OUTDATED LOW] cert "about three months" → CA/B SC-081: 200 d since 2026-03-15, 100 d Mar 2027, 47 d 2029. [ADD LOW] PQ key exchange X25519MLKEM768 default in JDK 27 (JEP 527).
- L32: [ADD MED 15m] REST vs gRPC vs GraphQL (replace paragraph); optional protobuf varint. [ADD LOW 5m] webhooks (retries → dedupe).
- L33: [ADD MED 5m] SSRF + ObjectInputStream deserialization bullets. [ADD MED 0m] reframe HMAC practice as webhook signature verification (timestamp, constant time, replay).
- L55: [ADD HIGH 25m optional] passkeys/WebAuthn via Ed25519 challenge+origin. [ADD MED 15m] asymmetric JWT + JWKS + kid rotation. [ADD LOW] TOTP RFC 6238; client-credentials line. [OUTDATED LOW] OAuth 2.1 still draft (-17, 2026-10-31).
- L34: [ADD LOW 5m] CREATE VIEW + stored procs line.
- L35: [ADD HIGH 20m] schema migrations (versioned scripts, schema_version, expand/contract, SQLite ALTER limits + 12-step rebuild = Room). [ADD MED 15m] keyset pagination EXPLAIN. [ADD LOW] crow's foot.
- L36: [ADD LOW] ON CONFLICT upsert, FOR UPDATE SKIP LOCKED job queue.
- L56: [ADD MED 20m] full-text inverted index / FTS5. [ADD LOW] vector search paragraph.
- [PROJECT] after L55 multi-user API with SSE; after L36 data layer w/ migration runner.

## L37–L43
Already covered: characterisation tests (L37), feature flags (L40), consistent hashing, idempotency keys, backpressure, DLQ (L42), SLO/SLI/error budget brief (L43), CDN (L31/L42).
- L37: [ADD MED 10m] seams + sprout/wrap (Feathers). [ADD MED 10m] tech debt quadrant, when not to refactor, strangler fig. [TRIM LOW] IntelliJ shortcut table + quiz.
- L38: [ADD MED 5m] name final as ports-and-adapters, composition root.
- L39: [ADD HIGH 20m] State pattern / sealed state machine (illegal transitions unrepresentable). [ADD MED 10m] Command. [ADD LOW 5m] Template Method vs Strategy; Null Object via data object None.
- L40: [ADD HIGH 15m] "Working with AI as a senior: delegate, verify, own" (tests first, narrow scope, weakened tests, slopsquatting, never-delegate list). [TRIM MED 20–25m] SemVer → 3-min recap (L25 owns it), shorten changelog/semantic-release, autosquash practice optional.
- L41: [ADD LOW 3m] CDN as outermost tier.
- L42: [ADD MED 15m] expand–contract + versioned migrations (dup with L35/L46 items).
- L43: [ADD HIGH 20m] operating: progressive delivery, incident response, blameless postmortem; final ends with postmortem. [ADD LOW 5m] load shedding.
- NEW LESSON (HIGH): Software Architecture after L40: layers + dependency rule (hexagonal, package-by-feature, Gradle-module boundaries, architecture test); DDD essentials (ubiquitous language, bounded contexts, value objects, aggregates); modular monolith vs microservices (Conway, strangler fig); events (domain events, ES/CQRS named), ADRs + C4/sequence diagrams. Final: split library into lending + catalogue modules via events.
- [PROJECT] legacy rescue after L40; order pipeline w/ outbox + postmortem after L43.

## L10–L15 L48 L16–L18
Already covered: tailrec (L18), amortised (L16), mocks vs fakes (L38), mutation aside (L11), heap sort (L20).
- L10: [ADD LOW 5m] log-or-rethrow; no exceptions for control flow.
- L11: [OUTDATED LOW] JUnit 6.0.1 → 6.1.x (6.1.2 2026-07-12); Gradle 9.7.1 → 9.9.1 (2026-11-10). [ADD MED 10m] flaky tests + fixes (inject Clock, seed, @DefaultTimeZone/@DefaultLocale in 6.1).
- L12: [ADD MED 10m] remote JVM debug (-agentlib:jdwp, Attach). [ADD LOW] debug a failing test.
- L13: [ADD LOW 2m] T & Any.
- L14: [ADD LOW 8m] checked exceptions vs functional interfaces, UncheckedIOException.
- L15: nothing. [PROJECT HIGH] test-first CLI after L15.
- L48: [TRIM LOW] permutations/combinations shorter if needed.
- L16: [ADD HIGH 12m] Θ/Ω vs best/worst/average; fix "O(n) means worst case" sentence; amortised ≠ probabilistic.
- L17: nothing.
- L18: [ADD MED 12m] Ω(n log n) comparison-sort lower bound (decision tree), counting/radix beat it.
