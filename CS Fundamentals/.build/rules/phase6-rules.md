# Phase 6 shared rules (all six forks: L16–L21)

Workspace: `/Users/tl/Documents/Learns/CS Fundamentals/` (call it W). Scratchpad: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad` (call it S).

## Files (NN = your lesson number, slug given in your prompt)
- Lesson: `W/lessons/00NN-<slug>.html` with `<body data-lesson="lNN">`.
- Template: `W/.build/tpl/00NN.html`. Verified sources: `W/.build/lNN/`. Memviz specs (if any): `W/.build/viz/lNN.py` (see `W/.build/viz/README.txt`; names `\w+`, prefix `lNN_`). Every path must match `[\w/.]+` (no hyphens anywhere, including Gradle subfolders, so use `ds_kotlin`, not `ds-kotlin`).
- Build: `cd "W/.build/bin" && python3 build.py ../tpl/00NN.html ../../lessons/00NN-<slug>.html`. `<!--CODE:lNN/path/File.kt-->` injects a verified file (language from extension); `<!--VIZ:name-->` injects a memviz spec.
- Gradle projects (for the tests that ship with each structure): put a copy under `W/.build/lNN/ds_kotlin/` and `W/.build/lNN/ds_java/`, generated with Gradle 9.7.1 (`gradle init --type kotlin-application` / `java-application`, Kotlin DSL), the same as L11's `W/.build/l11/tdd_kotlin` (look at its `app/build.gradle.kts`; `~/.gradle` caches the JDK 21 toolchain, Kotlin plugin and JUnit). Work in a scratch copy under S, then copy the sources into W. Before you finish, delete `build/`, `.gradle/` and `.kotlin/` from the copies in W.

## Don't edit shared files
`W/assets/*` (existing ones), `W/.build/bin/*`, `W/reference/*`, `NOTES.md`, `RESOURCES.md`, other lessons and templates. New styles go in a `<style>` block in your lesson's `<head>`; report them. You MAY create ONE new asset file, but only with the name assigned in your prompt (so siblings never collide). It must inject its own CSS using the theme tokens in `assets/course.css` (look at `assets/gitgraph.js` and `assets/pipeviz.js` as models), work in light and dark mode, and fit at phone width. I integrate links, notes, the roadmap and the reference sheet afterwards.

## Tools
- `export KOTLINC=S/kotlinc-dl/kotlinc/bin/kotlinc`, then `W/.build/bin/run.sh File.kt File.java …`.
- JDK 26: `java File.java`, `javap`. Gradle 9.7.1 on PATH.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings:** show the real numbers from this machine and say so ("on my machine; yours will differ"). Use warm-up runs and a doubling experiment (time for N, 2N, 4N…, and the ratio). Never claim precision you don't have. JMH is the professional tool; name it with a link and say it comes in L24.
- Verify every factual claim against an official or canonical source with WebFetch/curl and cite inline:
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and Kotlin API reference, JLS/JEPs, dev.java (check redirects and `<title>` with curl).
  - Sedgewick & Wayne *Algorithms* 4th ed. site (algs4.cs.princeton.edu): the phase's primary source.
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
- Final exercise with `.rating` rows (KOTLIN, JAVA, plus TESTS where the final ships tests).
- End matter:
  - an exit quiz interleaving earlier lessons
  - the "Build my results" summary and a midweek recall card
  - a `.primary` source box and a reference-sheet link to `../reference/data-structures-and-algorithms.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Data-structure lessons specifically
- **Hand-coding is the point.** The learner builds each structure from a blank file in both languages. Show the reference implementation only inside `details.solution` blocks or after an exercise prompt, never as the first thing they see.
- **Each `My…` structure ships with a JUnit test file in both languages,** verified green with real `./gradlew test` output, for the learner to paste into their `~/katas` Gradle project. Use kotlin.test in Kotlin and Jupiter in Java, as in L11. Recommend writing the structure test-first (L11's red-green loop).
- **State Big O for every operation,** in a table, with the reason, e.g. "amortised O(1): a resize copies n elements but happens every ~n adds".
- **Compare with the JDK class** you're mimicking, and say which one to use in real code (always the JDK's).
- **Kotlin vs Java differences to call out where they arise:**
  - generic arrays: Kotlin `arrayOfNulls<Any?>(n)` + `as T` vs Java `(T[]) new Object[n]` with `@SuppressWarnings("unchecked")` (callback to L13)
  - `operator fun get/set` → `list[i]`
  - `Iterable`/`iterator()` so `for` works
  - `?.` on nullable node links vs explicit null checks
  - `tailrec`
  - data classes for nodes or edges
  - `require` vs `IndexOutOfBoundsException` (callback to L10)
- **Use memviz** for pointer-heavy structures; it's ideal for linked nodes and recursion frames.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework, e.g. RecyclerView lists, caching maps, or a JPA entity's hashCode.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/data-structures-and-algorithms.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 6 outline (shared, so warm-ups and callbacks reference the right material)
- **L16 Big O & Dynamic Arrays** (`0016-big-o-and-dynamic-arrays.html`): Big O reasoning (O(1), log n, n, n log n, n², 2ⁿ; drop constants; best/worst/average; amortised); measuring with `System.nanoTime` doubling experiments and JIT warm-up caveats; build `MyArrayList<T>` (growth, get/set/add/insert/removeAt, bounds, iterator) vs `ArrayList`.
- **L17 Linked Structures** (`0017-linked-structures.html`): singly and doubly linked lists (`MyLinkedList`), memviz of node pointers, reversal; stacks (array vs linked, balanced brackets); queues (circular buffer `MyQueue`), `ArrayDeque` vs `java.util.Stack`/`LinkedList`, Kotlin `ArrayDeque`; linked vs array cache locality (preview of L22).
- **L18 Recursion, Searching & Sorting** (`0018-recursion-searching-and-sorting.html`): recursion and the call stack (memviz; L4 frames), base cases, StackOverflowError, `tailrec`; binary search (iterative and recursive, the `(lo+hi)/2` overflow bug, JDK `binarySearch` return values); insertion, merge and quick sort, stability, what `Arrays.sort`/`sortedWith` actually use; timings.
- **L19 Hash Tables** (`0019-hash-tables.html`): hash functions, buckets, collisions (chaining vs open addressing), load factor and resize; build `MyHashMap<K,V>`; the equals/hashCode contract from L7 in action (mutable-key loss, constant hashCode → O(n)); `HashMap` internals as implementation detail; `HashSet`, `LinkedHashMap`, Kotlin `mutableMapOf` ordering.
- **L20 Trees & Heaps** (`0020-trees-and-heaps.html`): binary search trees, build `MyBST` (insert/search/delete in three cases, height, degenerate sorted input); traversals (pre/in/post/level-order; recursive and iterative); `TreeMap` as a balanced tree; heaps as arrays (sift up/down), build `MyMinHeap`, `PriorityQueue`, top-k.
- **L21 Graphs & Dynamic Programming** (`0021-graphs-and-dynamic-programming.html`): adjacency list vs matrix; BFS (shortest path in an unweighted graph) and DFS (recursive and iterative), visited sets, topological sort; memoisation and bottom-up DP; choosing the right JDK collection (a decision table with Big O). Phase 6 milestone: your `MyArrayList`, `MyHashMap` and `MyBST` all tested and timed against the JDK versions.
