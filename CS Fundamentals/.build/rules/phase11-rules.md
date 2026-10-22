# Phase 11 shared rules (all four forks: L37–L40)

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
  - *A Philosophy of Software Design*, Fowler's *Refactoring* catalog, Refactoring.Guru, and Google's code-review guide: the phase's primary sources (see below).
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
  - a `.primary` source box and a reference-sheet link to `../reference/design-and-clean-code.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Design lessons specifically
- **Refactor real course code.** Callbacks to code the learner already wrote are the point of this phase:
  - L3's word counter, L6's BankAccount, L8's notifier/decorators, L10's config parser
  - L11's password validator, L19's MyHashMap, L25's CommandRouter
  - L30's KvServer, L32's books API, L36's LibraryRepository
  
  Read the relevant `.build/lNN/` sources and lessons before writing. Before/after code must both be real and verified (same output; tests green before and after).
- **Tests as the safety net:** show the test run before and after every refactoring step (real `./gradlew test` output, or `run.sh` output compared). Use Gradle projects under `W/.build/lNN/gr_kotlin` and `gr_java` where tests are needed.
- **Sources:**
  - Ousterhout, *A Philosophy of Software Design* 2nd ed., cited by chapter: deep modules ch. 4, information hiding ch. 5, comments ch. 12–13.
  - Fowler, *Refactoring* 2nd ed.: name the refactorings exactly as in refactoring.com/catalog (verify each name there).
  - *Effective Java* items.
  - Martin's SOLID papers, or the Wikipedia SOLID page as a last resort, clearly labelled. Prefer primary sources: Martin's "Design Principles and Design Patterns" (2000) and Liskov & Wing 1994, cited by title only if not openly accessible.
  - Refactoring.Guru for patterns (refactoring.guru/design-patterns), the GoF book cited by title.
  - Google's Engineering Practices "How to do a code review" (google.github.io/eng-practices).
  - Pro Git ch. 3.6 Rebasing and ch. 7.6 Rewriting History.
  - GitHub Actions docs (docs.github.com/actions) and Gradle's official `gradle/actions` setup docs.
  - Kotlin coding conventions (kotlinlang.org/docs/coding-conventions.html) and the Google Java Style Guide, briefly.
- **Kotlin vs Java:** keep the side-by-side discipline. Kotlin makes several patterns disappear or shrink:
  - `object` for Singleton
  - named/default args for Builder (callback L3/L6)
  - function types for Strategy (callback L14)
  - `by` delegation for Decorator (callback L15)
  - sealed types instead of the Visitor pattern
  - extension functions vs utility classes
  
  Show the Java pattern and the idiomatic Kotlin side by side, and say clearly where Kotlin genuinely removes ceremony and where it doesn't.
- **Git demos:** throwaway repos under S only. Set `user.name`/`user.email` locally with `git -c` or a local config; NEVER touch global git config; `init.defaultBranch=main` locally. Real transcripts for rebase, interactive-rebase equivalents (`git rebase -i` needs an editor, so use `GIT_SEQUENCE_EDITOR` with a sed script to make it non-interactive, and explain that the learner will see an editor), squash, fixup/autosquash, `git commit --amend`, `git reflog` recovery, and `git log --graph`.
- **CI:** GitHub-hosted runs can't happen here; label them "not run here". Write a real `.github/workflows/ci.yml` checked against the GitHub docs and run the exact same commands locally (`./gradlew build`), real output. Optionally validate the YAML with Python's `yaml` module if available. Never push anywhere.
- **Bridge:** Spring's DI container and Hilt/Dagger on Android are DI done for you; Spring's `@Component` strategies; observers ↔ `Flow`/`LiveData`. Names only; no framework code.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 11 explains the design ideas behind Spring's DI container and Hilt.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/design-and-clean-code.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 11 outline (shared, so warm-ups and callbacks reference the right material)
- **L37 Clean Code & Refactoring** (`0037-clean-code-and-refactoring.html`): naming (intention-revealing, consistent, Kotlin/Java conventions), small focused functions vs over-fragmentation (Ousterhout's critique), comments that explain why (not what), and when comments help (interface comments, Ousterhout ch. 12–13); code smells (long function, duplicated code, primitive obsession, feature envy, long parameter list, flag arguments); refactoring with tests as the safety net (Fowler's catalog: Extract Function, Inline Variable, Rename, Introduce Parameter Object, Replace Conditional with Polymorphism, Split Phase), small steps with green tests after each, IntelliJ refactoring shortcuts (docs only); deep vs shallow modules and information hiding (Ousterhout ch. 4–5), with a deep refactor of a course module.
- **L38 SOLID & Dependency Injection** (`0038-solid-and-dependency-injection.html`): each SOLID principle shown on course code (SRP on L32's books handler; OCP via sealed types/strategies; LSP with a real violation like Square/Rectangle or a read-only list that throws on add; ISP; DIP with repository interfaces), with honest caveats on over-applying them; dependency injection by hand (constructor injection, composition root, no container), why it beats `new` inside classes and singletons (testability, callback L36's repository); a hand-written tiny DI container exists from L25, so compare it; testing with fakes vs mocks (a fake `LibraryRepository` in memory, fake clock, fake HTTP client), test doubles taxonomy (Fowler's "Mocks Aren't Stubs"/TestDouble).
- **L39 Design Patterns** (`0039-design-patterns.html`): Strategy (vs function types) and Observer (vs `Flow`, listeners leak from L24); Factory (static factory methods, Effective Java Item 1) and Builder (Item 2; Kotlin named/default args make it mostly unnecessary, `apply`, DSL builders from L15); Adapter (wrapping a legacy API), Decorator (L8's notifier, Kotlin `by`, Java I/O streams as decorators from L22), Singleton (Kotlin `object`, enum singleton, why singletons hurt testing, callback L38 DI); each with a real "when not to use it".
- **L40 Code Review & Team Git** (`0040-code-review-and-team-git.html`): reading a diff critically (`git diff`, a review checklist, Google eng-practices), reviewing AI-generated diffs (plausible but wrong: off-by-one, swallowed exceptions, N+1 queries, missing tests, hallucinated APIs, security holes from L33), with a real planted-bug diff to review; pull requests (small, focused, good descriptions), rebase vs merge (Pro Git 3.6, the golden rule), interactive rebase to clean history (squash/fixup/reword), `--force-with-lease`, conventional commit messages; continuous integration (a GitHub Actions workflow running `./gradlew build` on every push, caching, a required status check), shown and run locally. Final (Phase 11 milestone): refactor your phase 9–10 code and submit it as a reviewed pull request (simulated locally: feature branch, clean commits, self-review checklist, CI commands green, rebase onto main, merge).
