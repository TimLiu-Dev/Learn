# CS Fundamentals Resources

## Knowledge

### Kotlin Language
- [Docs: _Kotlin Documentation_ by JetBrains](https://kotlinlang.org/docs/home.html)
  The official language guide and reference: basics, types, null safety, classes, collections, and the standard library. Use for: the authoritative answer to "how does this Kotlin feature work?"
- [Exercises: _Kotlin Koans_ by JetBrains](https://kotlinlang.org/docs/koans.html)
  A series of small failing unit tests that you make pass. Each one teaches one piece of syntax and is aimed at Java developers. You can do them in the browser or in IntelliJ with the JetBrains Academy plugin. Use for: building finger memory for Kotlin syntax by typing it yourself. Try each one before you click "Show answer".

### Java Language
- [Tutorials: _Learn Java_ by Oracle (dev.java)](https://dev.java/learn/)
  Oracle's current, modern Java tutorials: language basics, OOP, generics, lambdas, pattern matching, collections and streams, virtual threads, and the JVM tools. Use for: learning Java as it is written today. The old docs.oracle.com "Java Tutorials" are frozen at JDK 8.

### Kotlin and Java Interop
- [Docs: _Calling Java from Kotlin_ by JetBrains](https://kotlinlang.org/docs/java-interop.html)
  Covers platform types (`T!`), nullability annotations, getters and setters becoming properties, type mappings, SAM conversions, and checked exceptions. It links to the reverse guide, "Calling Kotlin from Java". Use for: understanding what happens when your Kotlin code meets Java libraries, which happens all the time in Android and Spring.
- [Docs: _Comparison to Java_ by JetBrains](https://kotlinlang.org/docs/comparison-to-java.html)
  Lists what Kotlin fixes, what Java has that Kotlin lacks (checked exceptions, primitives, statics, ternary), and what Kotlin adds. Use for: a quick side-by-side when you're writing the same thing in both languages.

### Generics and Functional Style
- [Docs: _Generics: in, out, where_ by JetBrains](https://kotlinlang.org/docs/generics.html)
  Kotlin's generics page explains declaration-site variance (`out`/`in`), use-site projections and star projections by contrasting them with Java's wildcards. Use for: the one page that teaches variance in both languages at once (L13).
- [Tutorials: _Generics_ and _Lambda Expressions_ by Oracle (dev.java)](https://dev.java/learn/language/fp/generics/intro/)
  The Java side: wildcards, type erasure, generics restrictions, and the [lambda tutorial](https://dev.java/learn/language/fp/lambdas/) (functional interfaces, method references). Use for: Java's version of L13–L14.
- [Docs: _Sequences_, _Scope functions_ and _Delegation_ by JetBrains](https://kotlinlang.org/docs/sequences.html)
  Short, precise pages on lazy sequences vs eager collections, the five scope functions (with a selection table), and [delegated properties](https://kotlinlang.org/docs/delegated-properties.html) and [class delegation](https://kotlinlang.org/docs/delegation.html). Use for: writing idiomatic Kotlin pipelines and choosing a scope function (L15).
- [API: _java.util.stream_ package summary by Oracle](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/package-summary.html)
  The authoritative explanation of stream laziness, intermediate vs terminal and stateless vs stateful operations, and side-effect rules. Use for: understanding exactly what a Java stream will and won't do.

### Data Representation
- [Book: _Computer Systems: A Programmer's Perspective, 3rd ed._ by Randal E. Bryant & David R. O'Hallaron](https://csapp.cs.cmu.edu/)
  The CMU textbook. Chapter 2, "Representing and Manipulating Information", is the canonical treatment of binary, hex, two's complement, overflow, and IEEE 754 floating point. Paid, and the examples are in C. Use for: really understanding why `Int.MAX_VALUE + 1` is negative and how bits become numbers.
- [Guide: _The Floating-Point Guide_ (floating-point-gui.de)](https://floating-point-gui.de/)
  A short, free explanation of why `0.1 + 0.2 != 0.3`: binary fractions, rounding, and how to compare floats. Includes a Java cheat sheet. Use for: the moment you're tempted to store money in a `Double`.
- [Article: _The Absolute Minimum Every Software Developer Must Know About Unicode and Character Sets_ by Joel Spolsky](https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/)
  A classic 2003 essay covering ASCII, code pages, Unicode code points, and UTF-8. Use for: the mental model behind `String`, `Char` (a UTF-16 unit on the JVM), and encoding bugs.

### Memory and the JVM
- [Spec: _JVM Specification, Ch. 2: The Structure of the Java Virtual Machine_ by Oracle](https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-2.html)
  §2.5 covers the run-time data areas: per-thread JVM stacks, the shared heap, the method area, and the constant pool. §2.6 covers frames (local variables and the operand stack). Use for: the precise, official answer to "what lives on the stack vs the heap?"
- [Guide: _HotSpot GC Tuning Guide, Ch. 3: Garbage Collector Implementation_ by Oracle](https://docs.oracle.com/en/java/javase/25/gctuning/garbage-collector-implementation.html)
  Covers the weak generational hypothesis, young and old generations, eden and survivor spaces, minor vs major collections, and how objects age. Use for: understanding how garbage collection actually reclaims your objects.
- [Reference: _The javap Command_ by Oracle (JDK 25)](https://docs.oracle.com/en/java/javase/25/docs/specs/man/javap.html)
  The class-file disassembler. `javap -c` prints the bytecode and `-v` prints the constant pool and everything else. Use for: compiling a tiny Kotlin or Java file and seeing what it really became, e.g. what a `data class` or a `when` generates.
- [Tool: _JDK Flight Recorder_ (`jfr` command reference) by Oracle](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jfr.html)
  The built-in, low-overhead profiler: record with `-XX:StartFlightRecording`, then `jfr summary`, `jfr print` and `jfr view hot-methods`. On macOS `jfr` may not be on PATH; use `$(/usr/libexec/java_home)/bin/jfr`. Use for: finding where time and allocations really go, without installing anything (L24).
- [Tool: _JMH (Java Microbenchmark Harness)_ by OpenJDK](https://github.com/openjdk/jmh)
  The standard microbenchmark harness. It handles warm-up, forking, dead-code elimination and constant folding, which naive `nanoTime` loops get wrong. The `samples` folder is the best tutorial. Use for: any "is A faster than B?" question at the method level (L24).
- [Docs: _Android Runtime (ART) and Dalvik_ by Google (AOSP)](https://source.android.com/docs/core/runtime)
  Android does not run JVM bytecode. Your `.class` files are converted to DEX, and ART runs them with ahead-of-time compilation (dex2oat) plus a mostly concurrent, compacting garbage collector. Use for: knowing which JVM facts carry over to Android and which don't.

- [Docs: _Annotations_ by JetBrains](https://kotlinlang.org/docs/annotations.html)
  How to declare, apply, target and retain annotations in Kotlin, and how they map to Java annotations. Use for: understanding what `@Something` actually is before Spring and Android throw dozens at you.
- [Docs: _Reflection_ by JetBrains](https://kotlinlang.org/docs/reflection.html)
  `KClass`, `KProperty`, `KFunction`, callable references and `kotlin-reflect`. Use for: writing a tiny annotation-driven "framework" yourself, to see how Spring finds your `@GetMapping` methods.

### Concurrency
- [Tutorial: _Lesson: Concurrency (The Java Tutorials)_ by Oracle](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
  Covers threads, `synchronized`, memory consistency errors, deadlock, immutability, executors, locks, and atomics. Oracle marks it as written for JDK 8, so it doesn't cover virtual threads. Use for: a gentle, hands-on first pass at threads before the heavier book.
- [Book: _Java Concurrency in Practice_ by Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes & Doug Lea](https://jcip.net/)
  The canonical book on thread safety, visibility, publication, and the `java.util.concurrent` toolkit (Addison-Wesley, 2006). Paid. It predates lambdas and virtual threads, but its principles haven't changed. Use for: learning to reason about whether code is thread-safe.
- [Spec: _JLS Ch. 17: Threads and Locks (§17.4 Memory Model)_ by Oracle](https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html)
  The official Java Memory Model: happens-before, volatile, monitors, data races, and final-field semantics. Use for: the final word once JCIP Ch. 16 has given you the intuition.
- [Spec: _JLS Chapter 17: Threads and Locks_ by Oracle](https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html)
  The official memory model: §17.4.5 happens-before, §17.5 `final` field semantics, and the definition of a data race. Pair it with the `java.util.concurrent` package summary's "Memory Consistency Properties". Use for: the authoritative answer to "will the other thread see this write?" (L27).
- [JEP: _JEP 444: Virtual Threads_ by OpenJDK](https://openjdk.org/jeps/444)
  Why virtual threads exist, how they mount and unmount on carrier threads, and the guidance (don't pool them; limit with semaphores). Read it alongside [JEP 491](https://openjdk.org/jeps/491), which stopped `synchronized` from pinning in JDK 24. Use for: deciding between pools, virtual threads and coroutines (L28).
- [Guide: _Coroutines Guide_ by JetBrains](https://kotlinlang.org/docs/coroutines-guide.html)
  The official kotlinx.coroutines guide: basics, cancellation and timeouts, composing suspend functions, dispatchers, Flow, channels, exception handling, and shared mutable state. Use for: Kotlin's structured concurrency, which is how Android does async work.

### Data Structures and Algorithms
- [Book: _Algorithms, 4th ed._ by Robert Sedgewick & Kevin Wayne](https://algs4.cs.princeton.edu/home/)
  The Princeton textbook, with all code in Java. The booksite is free and covers analysis of algorithms (Big O, §1.4), sorting, searching, hash tables, balanced trees, graphs, and strings. The print book is paid. Use for: your primary DSA text, implementing each structure by hand.
- [Course: _Algorithms, Part I_ by Kevin Wayne & Robert Sedgewick (Princeton, Coursera)](https://www.coursera.org/learn/algorithms-part1)
  A free MOOC that follows the book: union-find, analysis, stacks and queues, sorts, priority queues, and symbol tables. It has autograded Java programming assignments, and Part II continues with graphs and strings. There's no certificate. Use for: lectures plus graded hand-written assignments that keep you honest.
- [Practice: _LeetCode_](https://leetcode.com/)
  A large problem archive that accepts both Java (25) and Kotlin (2.1). Use for: drilling DSA patterns once you've implemented the structures yourself. Type the solutions in the editor without AI help.

### Testing and Debugging
- [Docs: _JUnit User Guide_ by the JUnit team](https://docs.junit.org/current/user-guide/)
  The official guide to JUnit Jupiter: `@Test`, assertions, lifecycle, parameterized tests, and nested tests. The current release is JUnit 6 (6.1.x, Java 17+); the old junit.org/junit5 URL now redirects here. Use for: writing tests for your Kotlin and Java exercises. Kotlin's `kotlin.test` assertions run on top of it.
- [Docs: _Debug code_ by JetBrains (IntelliJ IDEA Help)](https://www.jetbrains.com/help/idea/debugging-code.html)
  The official debugger overview, with linked pages on breakpoints, stepping, and evaluating expressions. Use for: replacing `println` with breakpoints and watching state change line by line.

### Design
- [Article: _Solid Relevance_ by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html)
  A short summary of the five SOLID principles, with the reasoning behind each, by the person who coined the set. Use for: a quick refresher on SRP, OCP, LSP, ISP, and DIP in their originator's words.
- [Site: _Design Patterns_ by Refactoring.Guru](https://refactoring.guru/design-patterns)
  Free, illustrated explanations of the 22 classic GoF patterns, each with a Java example (there are no Kotlin examples). The site also sells a paid ebook and course, which you don't need. Use for: looking up a pattern when you meet it in Android or Spring code.
- [Book: _Effective Java, 3rd ed._ by Joshua Bloch](https://www.informit.com/store/effective-java-9780134685991)
  90 items of best practice on object creation, equals and hashCode, generics, enums, lambdas, exceptions, and concurrency. Paid, and it covers up to Java 9. Use for: the canonical "how to write Java well" reference. Much of it also explains why Kotlin made the choices it did.
- [Book: _Effective Kotlin_ by Marcin Moskała](https://kt.academy/book/effectivekotlin)
  Rules for idiomatic Kotlin covering safety, readability, reuse, abstraction design, class design, and efficiency. Paid, and still updated on Leanpub. Use for: the Kotlin counterpart to Effective Java once the syntax feels natural.
- [Book: _A Philosophy of Software Design, 2nd ed._ by John Ousterhout](https://web.stanford.edu/~ouster/cgi-bin/book.php)
  A Stanford professor on managing complexity: deep vs shallow modules, information hiding, and comments. The 2nd edition (2021) contrasts it with Clean Code. Paid. Use for: deciding how to split code into classes and functions.
- [Book: _Refactoring, 2nd ed._ by Martin Fowler (with Kent Beck)](https://martinfowler.com/books/refactoring.html)
  The catalogue of behaviour-preserving code transformations, with the test-first discipline behind them (2018). Paid. The examples are in JavaScript, not Java. Use for: improving code you (or an AI) already wrote, safely and in small steps.

### Tools
- [Book: _Pro Git, 2nd ed._ by Scott Chacon & Ben Straub](https://git-scm.com/book/en/v2)
  The official Git book, free under CC BY-NC-SA. It covers the basics, branching, remotes, rewriting history, and Git internals. Use for: understanding what Git is doing instead of memorising commands.
- [Course: _The Missing Semester of Your CS Education_ by MIT](https://missing.csail.mit.edu/)
  Free MIT lectures (2026 edition) on the shell, editors, Git, debugging and profiling, build and packaging, and AI-assisted coding. Use for: becoming fluent on the command line you'll drive Gradle and Git from.
- [Docs: _Gradle User Manual_ by Gradle Inc.](https://docs.gradle.org/current/userguide/userguide.html)
  The official build tool docs (9.x), including a beginner tutorial that initialises a basic Java app. Use for: understanding the build system both Android and Spring Boot projects use.

### Operating Systems
- [Book: _Operating Systems: Three Easy Pieces_ by Remzi H. Arpaci-Dusseau & Andrea C. Arpaci-Dusseau](https://pages.cs.wisc.edu/~remzi/OSTEP/)
  A free University of Wisconsin textbook (v1.10) in three parts: virtualization (processes, scheduling, virtual memory), concurrency (threads, locks, condition variables), and persistence (file systems). Use for: what the OS does beneath the JVM. Read its concurrency part alongside JCIP.

### Networking, HTTP, REST and JSON
- [Docs: _HTTP_ by MDN Web Docs (Mozilla)](https://developer.mozilla.org/en-US/docs/Web/HTTP)
  The best free reference for HTTP: overview, messages, methods, status codes, headers, caching, and CORS. Use for: understanding every request your Android app sends and every response your Spring Boot API returns.
- [Guide: _HTTP caching_ by MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching)
  Private vs shared caches, freshness (`max-age`), validation (`ETag`, `Last-Modified`), `Vary`, and `no-cache` vs `no-store`. Use for: the HTTP-caching lesson, and for debugging "why is my app showing stale data?"
- [Guide: _Beej's Guide to Network Programming_ by Brian "Beej Jorgensen" Hall](https://beej.us/guide/bgnet/)
  A free, readable tour of sockets, TCP vs UDP, addresses, and client/server loops. The examples are in C. Use for: seeing what sits underneath HTTP. Port a small echo server to Kotlin with `java.net.Socket` as an exercise.
- [Spec: _Introducing JSON_ by Douglas Crockford (json.org)](https://www.json.org/json-en.html)
  The whole JSON grammar on one page, with railroad diagrams. It links to the formal standard, ECMA-404. Use for: knowing exactly what is and isn't valid JSON, e.g. no trailing commas, no comments, and no NaN.
- [Dissertation: _Ch. 5, Representational State Transfer (REST)_ by Roy T. Fielding](https://ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)
  The 2000 dissertation chapter that defined REST: client-server, statelessness, cacheability, uniform interface, and resources and representations. Use for: what REST actually means, before you design Spring Boot endpoints. Most "REST APIs" only partly follow it.

### Databases
- [Interactive: _SQLBolt_](https://sqlbolt.com/)
  Interactive SQL lessons in the browser: SELECT, filtering, joins, aggregates, and then INSERT, UPDATE, and CREATE TABLE. Use for: writing your first few hundred queries by hand.
- [Docs: _SQLite Documentation_ (Query Planner, EXPLAIN QUERY PLAN, Foreign Keys, Datatypes, Quirks)](https://www.sqlite.org/docs.html)
  Short, precise pages on how SQLite really behaves: type affinity, foreign keys being off by default, the rowid B-tree, and how the planner picks indexes. Use for: the dialect-specific truth behind L34–L36 and the capstone's SQLite store.
- [Docs: _PostgreSQL: Transaction Isolation_](https://www.postgresql.org/docs/current/transaction-iso.html)
  The clearest official table of isolation levels vs anomalies, plus what PostgreSQL actually does: snapshot isolation for REPEATABLE READ, and SSI for SERIALIZABLE with retryable failures. Use for: reasoning about isolation in a production database (L36).
- [Book: _Use The Index, Luke_ by Markus Winand](https://use-the-index-luke.com/)
  The free web edition of "SQL Performance Explained": B-tree indexes, WHERE clauses, joins (including the N+1 problem), sorting, and pagination, across PostgreSQL, MySQL, Oracle, and others. Use for: understanding why a query is slow, which you'll need for Spring Data and JPA.
- [Video lectures: _CMU 15-445/645 Intro to Database Systems (Fall 2024)_ by Andy Pavlo (Carnegie Mellon)](https://www.youtube.com/playlist?list=PLSE8ODhjZXjYDBpQnSymaectKjxCy6BYq)
  The full recorded university course: storage, buffer pools, hash and B+tree indexes, query execution, concurrency control, and recovery. Slides are on the course site, 15445.courses.cs.cmu.edu. Use for: how a database works on the inside.

### System Design and Caching
- [Guide: _The System Design Primer_ by Donne Martin (GitHub)](https://github.com/donnemartin/system-design-primer)
  A free, widely used overview of scalability, load balancing, caching strategies (cache-aside, write-through, write-behind), replication, sharding, queues and CAP, with latency numbers in its appendix. It's interview-flavoured and community-written, so check deep claims against DDIA. Use for: the map of the territory and quick reference.
- [Book: _Designing Data-Intensive Applications, 2nd ed._ by Martin Kleppmann & Chris Riccomini](https://martin.kleppmann.com/2026/03/24/designing-data-intensive-applications-2e.html)
  The canonical book on reliable, scalable data systems (O'Reilly, 2026): storage engines, replication, partitioning, transactions, consistency and stream processing. Paid. Use for: the deep, trustworthy explanation behind every system-design trade-off.

### Security Basics
- [Standard: _OWASP Top 10:2025_ by OWASP](https://top10.owasp.org/2025)
  The ten most critical web application risks in the 2025 edition: broken access control, misconfiguration, supply chain failures, cryptographic failures, injection, and more. Use for: a checklist of mistakes to avoid before you build anything with Spring Boot.

### Future missions (after the course)
- [Book: _Crafting Interpreters_ by Robert Nystrom](https://craftinginterpreters.com/)
  Free online. You build two interpreters for a small language: jlox in Java (a tree-walker) and clox in C (a bytecode VM with a GC). Use for: mission D (compilers), any time after phase 6.
- [Course: _MIT 6.5840 Distributed Systems_ (formerly 6.824)](https://pdos.csail.mit.edu/6.824/)
  MIT's graduate distributed-systems course. Its labs include implementing Raft. Use for: mission C (distributed systems internals), alongside DDIA.

## Wisdom (Communities)
- [Exercism: Kotlin track](https://exercism.org/tracks/kotlin)
  88 free exercises with automated analysis plus free human mentoring from volunteers (100% free; expect waits of hours to days). Use for: getting a human to review your idiomatic Kotlin, iteration by iteration.
- [Exercism: Java track](https://exercism.org/tracks/java)
  158 exercises across 26 concepts, with a large volunteer mentor pool (about 3,000 listed). Use for: the same mentored loop in Java. Do an exercise in both languages and compare the feedback.
- [Code Review Stack Exchange](https://codereview.stackexchange.com/)
  A Q&A site for open-ended peer review of working code you wrote yourself. Broken code goes to Stack Overflow instead. Use for: a detailed critique of a finished, hand-written solution.
- [r/learnprogramming](https://www.reddit.com/r/learnprogramming/)
  A large (about 4M members), beginner-friendly subreddit for questions in any language. Read the FAQ before posting. Use for: study-path questions and "why doesn't this work?" posts.
- [r/javahelp](https://www.reddit.com/r/javahelp/)
  A Java help subreddit. The rules require you to show what you've tried, and complete solutions aren't given. Use for: specific Java questions where you've already written code.
- [Kotlin Slack (sign-up)](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
  The official Kotlin community Slack. Sign-ups go through JetBrains' form, linked from kotlinlang.org/community. Use for: asking Kotlin questions directly to experienced Kotlin developers and JetBrains staff.
- [r/androiddev](https://www.reddit.com/r/androiddev/)
  The main Android developer subreddit (about 270k members), focused on building apps. Use for: later, when you move to Android, for what the ecosystem looks like today.

## Gaps
- **Design patterns in Kotlin:** I found no high-trust, Kotlin-first patterns reference. Refactoring.Guru and the GoF book use Java and other languages, so translating the examples to Kotlin yourself is a useful exercise.
- **Data representation in JVM terms:** CS:APP is the canonical source, but it is paid and C-based. I found no free, high-trust primer on binary, two's complement and bitwise operations written for Java or Kotlin. The JLS (Ch. 4, 15) has the facts but isn't a tutorial.
- **ART vs JVM comparison:** The official AOSP page explains ART and DEX but doesn't directly compare ART with the HotSpot JVM. There is no single authoritative side-by-side.
- **Modern Java concurrency tutorial:** Oracle's threads tutorial is frozen at JDK 8, and JCIP is from 2006. I found no current, free, official tutorial that teaches threads, executors and virtual threads together; JEP 444 (openjdk.org/jeps/444) is the spec for virtual threads.
- **Sockets in Java and Kotlin:** Beej's guide is in C, and Oracle's "Custom Networking" tutorial is in the JDK 8-era Java Tutorials. I found no current official sockets tutorial.
- **Big O as a standalone topic:** I found no free, high-trust, language-agnostic primer worth adding beyond Sedgewick §1.4. Popular cheat-sheet sites aren't authoritative.
