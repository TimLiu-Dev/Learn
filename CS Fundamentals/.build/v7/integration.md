# Integration TODO (from builder reports)

## B11 (L41 L43 L57) DONE
- Sheets: system-design.html — fencing tokens + leases; load shedding 503 + Retry-After; CDN purge + cache key; LWW vs version vectors vs CRDTs for offline sync.
- Glossary: fencing token, lease, load shedding, purge, cache key, CRDT, offline-first.
- Manifest re-run (l57/cq/StringToken.kt).
- L41 uncited: "version/content hash in file names" common practice — add a cite (e.g. web.dev / MDN caching) at review.
- Times: L57 ~185–200; L43 ~155–170; L41 unchanged.
- Roadmap L57 parts: add "Fencing tokens: leases that can't corrupt storage"; final now "run the simulator, write the safety argument, break isUpToDate".
- Leftover Gradle daemons (28255, 28536) — kill at end. 59709 is foreign (8765), leave.

## B3 (L16 L18 L19 L20 L21) DONE
- Sheets data-structures-and-algorithms.html: Θ/Ω vs cases; comparison-sort lower bound; counting sort O(n+k); rotation + AVL/RB invariants; three-colour DFS; SequencedCollection.
- Glossary: Big-Θ, Big-Ω, decision tree, counting sort, radix sort, rotation, AVL/red-black invariant, hash flooding, sequenced collection.
- Unfetched CLRS facts (≤2 rotations per RB insert; white/gray/black DFS) — verify at review.
- Times: L16 165–180, L18 165–180, L19 155–170, L20 160–175, L21 170–185.
- Roadmap parts: L16 +Θ/Ω; L18 +lower bound, counting sort; L20 +how balancing works; L21 +cycle detection, sequenced collections.
- Manifest re-run: cq Cases.java, Rotate4.kt, Diamond.kt + new pairs.

## B4 (L50 L51 L52) DONE
- Sheets: DSA — prefix sums + count map; runtime-and-os — CALL/RET/SP, BL/LR x30, interrupts, user/kernel mode, trap table, syscalls, preemption.
- Glossary: prefix sum, rolling hash, stack pointer, return address, link register, interrupt, trap, system call, preemption, calling convention.
- L52 diff NOT run (backup wiped) → review must read L52 carefully end to end. Arm site 403: used AAPCS64.
- Times: L50 175–190, L51 150–165, L52 180–195.
- Roadmap: L50 +prefix sums (+optional Rabin–Karp); L51 TM sim + subset construction optional; L52 new part "Calls, the stack and interrupts".
- Manifest: PrefixRange (java cq), StackOrder.kt.

## B2 (L10–L14) DONE
- Glossary: flaky test, JDWP, definitely non-nullable type.
- Sheets: errors-testing-debugging — Clock.fixed, -agentlib:jdwp…127.0.0.1:5005, --debug-jvm, log-or-rethrow; generics-and-functions — T & Any, UncheckedIOException.
- Unverified: Effective Java item numbers 69, 73 (pre-existing).
- Stray file lessons/_qa_0016-big-o-and-dynamic-arrays.html — delete at integration (chrome_qa leftover).
- Times: L10 145–160, L11 150–165, L12 160–175, L13 145–160, L14 170–185.
- Roadmap parts: L10 log-or-rethrow; L11 flaky tests; L12 attach debugger; L14 checked exceptions in lambdas.
- Manifest: LogCount.kt, Decimal.kt, Unwrap.java.

## B1 (L1 L2 L5–L9) DONE
- Sheets: classes-and-types — rename "Value classes" → "Data carriers", add value class row, Comparable consistent with equals, nested vs inner; kotlin-java-basics — mark Normalize/Graphemes rows deep dive, add divide(x,2,HALF_EVEN), NaN/-0.0; cli-git-cheatsheet — restore, restore --staged, commit --amend, gh repo create --private --push.
- Roadmap L2 parts: Unicode part two now optional; add dividing money. L1 + undo & private remote. L5 + NaN. L6 + nested vs inner. L7 + value classes, Comparable.
- Glossary: grapheme cluster, inline value class, consistent with equals, inner class.
- L7 and L1 diff NOT run (backup wiped) → review read carefully. L7 Part 1 = 61 min (long part; reviewer judge).
- Times: L1 135–150, L2 160–175, L5 125–140, L6 160–175, L7 155–170.

## B10 (L37–L40) DONE
- Sheet design-and-clean-code.html: remove IntelliJ shortcuts line (~line 36); add seams/sprout/wrap, tech-debt quadrant, strangler fig, ports & adapters, State/Command/Template Method/Null Object, AI never-delegate list; cli-git: SemVer recap points to L25.
- Glossary: seam, sprout/wrap method, technical debt, strangler fig, ports and adapters, State/Command/Template Method/Null Object, slopsquatting.
- L39 MVI sentence uncited — reviewer check.
- L39 now 180–195 (Part 4 added); L40 180–195; L37 165–180; L38 160–175.
- Roadmap: L37 +seams, tech debt; L38 +ports & adapters; L39 +state machines, Command, Template Method; L40 +working with AI as a senior, SemVer recap.
- Orphan headless Chromes 50072 50198 — kill at end.

## B6 (L23 L54 L24 L25) DONE
- Sheets: runtime-and-os — Native Image vs AOT cache; container RSS budget, exit 137, MaxRAMPercentage; compact headers (JEP 519 opt-in 25, JEP 534 default 27); K2 FIR phases. L25 sheet (generics-and-functions? check which) — reflection vs KSP vs compiler plugin; configuration cache.
- Glossary: closed-world assumption, Native Image, compiler plugin, convention plugin, configuration cache, compact object headers, name resolution, type checking, FIR.
- L25 still 190–205 (> target): at review make Part 3 fat-jar or Part 2 kotlin-reflect optional.
- L26 tpl line 46 "three kinds of reader (L25)" → four — told B7.
- Oracle blog 403: GraalVM detach from inside.java.
- Times: L23 160–175, L24 160–175, L25 190–205.
- Roadmap: L23 +Native Image vs Leyden; L24 +JVM in containers; L25 +compiler plugins, config cache, licences shorter; L54 optional type checking + K2.
- Manifest: Fields.kt

## B8 (L31 L32 L33) DONE
- Sheets networks-and-web: SSE format; WebSocket handshake + Accept; polling vs SSE vs WS; REST vs gRPC vs GraphQL; webhook dedupe + signature; SSRF; deserialization; cert lifetimes 200/100/47.
- Glossary: SSE, long polling, WebSocket, over-fetching, under-fetching, SSRF, webhook.
- Times: L31 180–195, L32 180–195, L33 185–200 (unchanged; over target, check at review).
- Roadmap: L31 +Part 4 Beyond request/response; L32 +REST vs gRPC vs GraphQL, webhooks; L33 +SSRF, deserialization, webhook signatures.
- Manifest: SseSplit.kt, Varint.
