# Phase 8 shared rules (all four forks: L26–L29)

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
- **kotlinx.coroutines 1.11.0** (checksum-verified) is at `S/libs/kotlinx-coroutines-core-jvm-1.11.0.jar`, with the test library `S/libs/kotlinx-coroutines-test-jvm-1.11.0.jar`. `run.sh` takes no classpath, so compile coroutine code with `"$KOTLINC" -cp "$J" File.kt -include-runtime -d out.jar` and run it with `java -cp "out.jar:$J" FileKt`. In Gradle use `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0`. Never download other copies.
- `.build/bin/run.sh` compiles into a private `mktemp -d` folder, so parallel runs are safe.
- JDK 26: `java File.java`, `javap`. Gradle 9.7.1 on PATH.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings and measurements:** show the real numbers from this machine and say so ("on my machine; yours will differ"). Use warm-up runs; never claim precision you don't have. JMH was taught in L24; link it (and use it only if a comparison really needs it).
- Verify every factual claim against an official or canonical source with WebFetch/curl and cite inline:
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and Kotlin API reference, JLS/JEPs, dev.java (check redirects and `<title>` with curl).
  - *Java Concurrency in Practice*, JLS ch. 17, the `java.util.concurrent` docs, the Kotlin Coroutines guide, and *OSTEP*'s Concurrency chapters (26–32, free): the phase's primary sources.
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
  - a `.primary` source box and a reference-sheet link to `../reference/concurrency.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Concurrency lessons specifically
- **Nondeterminism must be shown honestly.**
  - A race demo prints real results from several runs, e.g. 5 runs of a counter that should reach 2,000,000, and each number is the real one.
  - Say plainly that the learner's numbers will differ.
  - Never present a single lucky run as typical.
  - If a bug doesn't reproduce on this machine (Apple Silicon is weakly ordered, x86 is TSO, and the JIT matters), say so. Don't fake it. Use a technique that does reproduce, such as more iterations, `Thread.yield()` or a deliberately widened window, and explain the technique.
- **Never let a demo hang.**
  - Deadlock demos must detect and report the deadlock, e.g. with `ThreadMXBean.findDeadlockedThreads()` after a timeout or with a `jstack`/`jcmd Thread.print` capture, then exit. Use `timeout` or daemon threads.
  - Every program must terminate in under ~30 s.
- **Machine facts:** state core count (`Runtime.availableProcessors()`, `sysctl hw.ncpu`) and architecture in the lesson. Results depend on them.
- **Sources:**
  - *Java Concurrency in Practice* (Goetz et al.), cited by chapter/section number.
  - JLS ch. 17 "Threads and Locks", §17.4 Memory Model.
  - The `java.util.concurrent` package-summary "Memory Consistency Properties" section.
  - The JDK API docs.
  - JEP 444 Virtual Threads; JEP 505/525 Structured Concurrency (check its JDK 26 preview status); JEP 506 Scoped Values; check every one's current status.
  - The Kotlin Coroutines guide (kotlinlang.org/docs/coroutines-guide.html) and the kotlinx.coroutines API docs.
- **Kotlin vs Java:**
  - Kotlin has no `synchronized` keyword; it has the `@Synchronized` annotation, the `synchronized(lock) { }` stdlib function, `@Volatile`, and `kotlin.concurrent.thread { }`. Kotlin `object`s are thread-safe-initialised singletons. Verify each against Kotlin docs.
  - Kotlin also has its own `kotlin.concurrent.atomics` API; check its stability status in 2.4 and use `java.util.concurrent.atomic` if it's experimental.
  - Show the bytecode difference (`monitorenter`/`monitorexit` vs `ACC_SYNCHRONIZED`) where it helps (callback L23).
- **Bridge:** Android's main thread (never block it; `Dispatchers.Main`; ANR) and Spring MVC's thread-per-request model (servlet container thread pools, virtual threads option). Name them only and cite official docs; no framework code.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 8 underpins Android's main-thread rule and Spring's thread-per-request model; modern Android UI state is mostly `Flow`.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/concurrency.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 8 outline (shared, so warm-ups and callbacks reference the right material)
- **L26 Threads & Race Conditions** (`0026-threads-and-race-conditions.html`): starting threads (`Thread`, `Runnable`, `kotlin.concurrent.thread`), `join`, thread states, the OS scheduler and context switches (callback L22), daemon threads; a race condition on purpose (lost updates on `count++`, check-then-act, read-modify-write; `javap` shows `count++` is 3+ bytecodes, callback L23); fixes: `synchronized`/`@Synchronized`/`synchronized(lock){}`, `ReentrantLock` (+ `tryLock`, fairness), atomics (`AtomicInteger`, `LongAdder`, CAS), each measured. Final: prove a counter is broken with a test, then fix it three ways.
- **L27 The Memory Model & Deadlock** (`0027-the-memory-model-and-deadlock.html`): visibility (a stop-flag loop that never ends without `volatile`, if reproducible), `volatile`/`@Volatile`, happens-before rules (JLS §17.4.5), safe publication, double-checked locking (and why Kotlin `by lazy`/`object` handle it, callback L15); deadlock (two locks in opposite order, detected with `ThreadMXBean` and `jcmd Thread.print`), lock ordering, `tryLock` with timeout, livelock/starvation; thread-safe collections (`ConcurrentHashMap` and its atomic `compute`/`merge`, `CopyOnWriteArrayList`, `BlockingQueue` producer-consumer, `Collections.synchronizedList` pitfalls), immutability as the best defence (callback L5/L7).
- **L28 Executors & Virtual Threads** (`0028-executors-and-virtual-threads.html`): thread pools (`Executors.newFixedThreadPool`, `ThreadPoolExecutor` parameters, queue/rejection policies, shutdown/`awaitTermination`, try-with-resources on `ExecutorService` since JDK 19); `Future`, `Callable`, `invokeAll`; `CompletableFuture` composition (`supplyAsync`, `thenApply`, `thenCompose`, `thenCombine`, `allOf`, `exceptionally`/`handle`) and Kotlin interop; virtual threads (JEP 444): millions of blocking tasks, `Executors.newVirtualThreadPerTaskExecutor()`, pinning (and JEP 491's change), when not to use them (CPU-bound); structured concurrency preview status in JDK 26 (verify; run only if it doesn't need preview flags you can't explain).
- **L29 Coroutines** (`0029-coroutines.html`): `suspend` functions and what the compiler does (CPS/state machine, `Continuation` in javap, callback L23); `runBlocking`, `launch`, `async`/`await`, `coroutineScope`, structured concurrency, cancellation (cooperative, `isActive`, `CancellationException`), exceptions and `SupervisorJob`; dispatchers (`Default`, `IO`, `Main` as a concept, `withContext`) and the "main thread" idea (Android bridge); `Flow` (cold streams, operators, `collect`, `StateFlow`/`SharedFlow` briefly, callback L15 sequences); coroutines vs virtual threads comparison (both measured on the same 100k-sleep workload). Java has no coroutines: the Java side of each pair is the closest equivalent (`CompletableFuture`, virtual threads, `Flow` vs `java.util.concurrent.Flow`/reactive streams), clearly labelled.
