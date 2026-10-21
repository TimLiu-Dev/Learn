# Phase 7 shared rules (all four forks: L22–L25)

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
- JDK 26: `java File.java`, `javap`. Gradle 9.7.1 on PATH.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings and measurements:** show the real numbers from this machine and say so ("on my machine; yours will differ"). Use warm-up runs; never claim precision you don't have. JMH is taught in L24 (the L24 fork runs it for real); other lessons just link it.
- Verify every factual claim against an official or canonical source with WebFetch/curl and cite inline:
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and Kotlin API reference, JLS/JEPs, dev.java (check redirects and `<title>` with curl).
  - *OSTEP* (pages.cs.wisc.edu/~remzi/OSTEP/, free chapters), the JVM spec (docs.oracle.com/javase/specs/jvms/se25/), the HotSpot GC tuning guide, Gradle docs (docs.gradle.org), Kotlin annotations/reflection docs: the phase's primary sources.
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
  - a `.primary` source box and a reference-sheet link to `../reference/runtime-and-os.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Systems lessons specifically
- **This machine is macOS (Apple Silicon or Intel: check with `uname -m`/`sysctl`).**
  - Every shell command shown must be run here, with real output: `ps`, `top -l 1`, `vmmap`, `sysctl hw.*`, `lsof`, `ulimit`, `file`, `xxd`, `jcmd`, `jstat`, `jfr`, `jps`, `javap`, `jdeps`, `java -Xlog:…`, `-XX:+PrintCompilation`, `-verbose:class`.
  - Linux-only tools (`strace`, `/proc`, `perf`) and anything needing sudo/SIP changes (`dtruss`) must be clearly labelled "Linux" or "not run here", cited from official docs, and **never faked**.
  - Never run anything destructive, anything that needs sudo, or anything that changes system settings.
- **JVM flags and tools:** verify each against the official JDK docs (the `java` command reference, the `jcmd`/`jfr`/`jstat` tool pages, the HotSpot GC tuning guide for JDK 25 at docs.oracle.com/en/java/javase/25/…) and JEPs. Show real output from JDK 26. The default GC on this machine is whatever `java -Xlog:gc -version` / `-XX:+PrintCommandLineFlags` reports; don't assume.
- **GUI tools** (IntelliJ profiler, JDK Mission Control, Android Studio) can't be run headless. Describe them only from official docs (JetBrains, Oracle/OpenJDK JMC, developer.android.com), cite every menu name, and give a CLI equivalent that you did run (`jfr print`/`jfr summary`, `jcmd`).
- **Android ART claims:** cite source.android.com / developer.android.com only. No Android code is run; say so.
- **Programs that demonstrate effects** (cache misses, GC pauses, leaks, JIT tiers) must print real measurements. Keep each one under ~30 s of runtime and under ~1 GB of heap (use `-Xmx` explicitly). Label numbers "on my machine".
- **Kotlin vs Java:** keep the side-by-side discipline. Where a topic is JVM-level and identical for both (GC, class loading), show the program in both languages anyway, and point out Kotlin-specific runtime differences: `kotlin-stdlib` on the classpath, `@JvmStatic`/companion bytecode, the `Intrinsics.checkNotNullParameter` calls kotlinc inserts, data-class bytecode, `inline` erasing lambdas, `kotlin-reflect` being a separate jar.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 7 is where Spring's annotations and reflection, Android's ART runtime and memory leaks (leaked Activity/Context), and Gradle builds stop being magic.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/runtime-and-os.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 7 outline (shared, so warm-ups and callbacks reference the right material)
- **L22 The OS & the Memory Hierarchy** (`0022-the-os-and-the-memory-hierarchy.html`): processes vs threads, virtual memory, system calls and user/kernel mode (a JVM process seen through `ps`/`vmmap`/`lsof`; `ProcessHandle`, `ProcessBuilder`); CPU caches and cache lines, latency numbers, measured row-major vs column-major and array vs linked-list traversal (callback L17); files and I/O: byte vs character streams, buffering (measured), encodings (UTF-8, callback L2), `java.nio.file.Files`, Kotlin `File`/`useLines`/`readText`, try-with-resources/`use` (L10).
- **L23 Inside the JVM** (`0023-inside-the-jvm.html`): the class file and bytecode with `javap -c -v` (a Java record vs a Kotlin data class decompiled; Kotlin's inserted null checks; `when` → `tableswitch`; operand stack); class loading (loaders, delegation, `-verbose:class`, static init order/lazy loading), runtime data areas (callback L4); interpreter + JIT tiers (`-XX:+PrintCompilation`, warm-up measured, inlining), the AOT/Leyden direction only if verified for JDK 26; Android's ART vs the JVM (DEX, d8/R8, AOT+JIT profile-guided; docs only).
- **L24 Garbage Collection & Profiling** (`0024-garbage-collection-and-profiling.html`): reachability, GC roots, mark-sweep-compact, generational hypothesis, the HotSpot collectors (G1 default, ZGC, Serial, Parallel — verify), reading `-Xlog:gc` output; memory leaks in a GC'd language (static collections, listeners, caches, inner-class references, ThreadLocal; `WeakReference`/`WeakHashMap`; Android Activity leak as a bridge), heap dumps (`jcmd GC.heap_dump`, histogram `GC.class_histogram`); profiling: JFR (`-XX:StartFlightRecording`, `jfr print --events`/`jfr summary`), hot methods, allocation profiling, JMH for microbenchmarks (run for real via Gradle), the IntelliJ profiler (docs).
- **L25 Annotations, Reflection & Builds** (`0025-annotations-reflection-and-builds.html`): annotations (declaring, retention/targets, Kotlin use-site targets `@field:`/`@get:`/`@param:`, built-ins like `@Deprecated`/`@JvmStatic`/`@Override`, annotation processing vs KSP briefly); reflection (`Class`/`KClass`, `::class.java`, members, invoking methods, reading annotations at runtime, `kotlin-reflect` dependency, cost and encapsulation/`setAccessible`, modules); Gradle and dependencies (tasks, the task graph, configurations `implementation`/`api`/`testImplementation`, transitive deps and `./gradlew dependencies`, version catalogs from L1's `gradle init`, building a jar, `java -jar`, a fat/runnable jar or `installDist`, Maven Central coordinates). Final: a tiny annotation-driven "framework" of your own.
