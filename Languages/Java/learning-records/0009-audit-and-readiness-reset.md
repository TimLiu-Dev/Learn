# Curriculum Audit, Objective Defect Repairs & Readiness Reset

## Purpose
This record supersedes the premature "Mastered" status claims in historical generation logs `0002` through `0006` and accurately resets the workspace state before the learner begins active study.

## True Starting Baseline
- **Prior background:** Junior Android developer who can read some Kotlin, with minimal prior Java fluency.
- **Active Java mastery:** Starting fresh at **Lesson 0002** (`0001` Master Plan reviewed as curriculum hub).
- **Local environment verified (2026-11-12):**
  - OpenJDK **25.0.4.1** LTS is installed via Homebrew (`/opt/homebrew/opt/openjdk@25/bin/{java,javac}`), though macOS `/usr/bin/java` currently reports missing runtime until `PATH` / `/Library/Java/JavaVirtualMachines` symlink is configured.
  - **Gradle 9.7.1** (with Kotlin 2.4.0) is installed at `/opt/homebrew/bin/gradle`; Maven and Docker are not currently installed.

## Workspace Audit & Objective Fixes Completed
1. Escaped raw generic angle brackets (`<T>`, `<T, R>`, `<Address>`, `<Row>`, `<? super T>`, `<? extends T>`) across lessons `0018`, `0021`, `0022`, `0071` and references `0022`, `0071`.
2. Fixed broken `ArrayList` -> `HashSet` inheritance double-counting bug in Lesson `0009`, fixed unclosed thread pool JVM hangs in Lesson `0031`, updated `StructuredTaskScope` / `ScopedValue` in Lesson `0033` & Reference `0033` for JDK 25 (`StructuredTaskScope.open()`, finalized `ScopedValue`), updated `0032`/`0035`/`0013`/`0040`/`0002` for JDK 25 accuracy (`JEP 491`, `JEP 490`, `JEP 456`, `JEP 403`), and aligned all mismatched `simulatedOutput` logs (`0029`, `0043`, `0047`, `0054`, `0071`).
3. Upgraded 27 stub Bug-Fix Challenges into genuine buggy-code diagnostics, replaced copy-pasted `"stream laziness"` quiz intros across 38+ lessons, fixed the `0048` quiz explanation typo, and replaced cartoonish quiz distractors with realistic Java engineering misconceptions and balanced option lengths.
