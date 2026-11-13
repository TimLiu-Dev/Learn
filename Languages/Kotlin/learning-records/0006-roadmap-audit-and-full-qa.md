# Session 0006 · Curriculum QA Audit & `roadmap.sh/kotlin` (2027) Gap Analysis

**Date:** 2026-11-12  
**Scope:** End-to-end verification of all 66 lessons (`0000`–`0065`), 16 reference sheets, `index.html`, `assets/answers.js` (`133` exercises), and `assets/lesson.js`; plus gap comparison against [roadmap.sh/kotlin](https://roadmap.sh/kotlin) and modern Kotlin (`2.2`–`2.4`) for a junior Android developer aiming for Kotlin mastery by 2027.

---

## 1. Bugs Found & Fixed Across the Workspace

### Runtime, Engine & Answer Key Drift
- [`assets/lesson.js`](../assets/lesson.js): Fixed `/reference/` relative `answers.js` fallback path and selector handling (`section.querySelector("code.kt-show")`).
- [`assets/answers.js`](../assets/answers.js): Aligned 19 drifted/buggy model answers with lesson starters and checklists (`0002-ex1`, `0004-ex1`, `0006-ex1`, `0006-ex2`, `0007-ex2`, `0008-ex1`, `0008-ex2`, `0009-ex1`, `0009-ex2`, `0010-ex1`, `0010-ex2`, `0011-ex1`, `0011-ex2`, `0012-ex1`, `0012-ex2`, `0012-ex3`, `0016-ex2` literal `$$` bug, `0051-ex2` CPU-timing race condition replaced with deterministic `onFirstStep()` cancellation hook, and `0063-ex1` `FakeNetworkClient` naming).
- [`lessons/0000-diagnostic.html`](../lessons/0000-diagnostic.html): Restored `$name`, `$bpm`, `$zone`, `$pct` inside starter output string.
- [`lessons/0009-elvis-operator.html`](../lessons/0009-elvis-operator.html): Fixed unescaped inner double quotes inside `data-why` attribute (`'45min'`).
- [`lessons/0008-nullable-types-safe-calls.html`](../lessons/0008-nullable-types-safe-calls.html) & [`lessons/0012-review-null-safety.html`](../lessons/0012-review-null-safety.html): Removed stale `"coming soon"` links and `"Claude"` reference, linking directly to live lessons (`0030` and `0013`).
- [`lessons/0063-testing-coroutines-runtest.html`](../lessons/0063-testing-coroutines-runtest.html): Fixed `1 shl` bitshift literal to `1L shl` and aligned `FakeNetworkClient`.

### Starter Code Solution Leaks Fixed (Refactor Exercises)
- [`lessons/0038-collections-dsa-structures.html`](../lessons/0038-collections-dsa-structures.html) (`0038-ex2`): Removed leaked `PriorityQueue` solution; starter delegates to naive sort.
- [`lessons/0062-unit-testing-kotlin-test.html`](../lessons/0062-unit-testing-kotlin-test.html) (`0062-ex2`): Replaced pre-solved starter with brittle `SpyDiscountRule` interaction test to refactor.
- [`lessons/0063-testing-coroutines-runtest.html`](../lessons/0063-testing-coroutines-runtest.html) (`0063-ex2`): Replaced pre-solved starter with real-time `Thread.sleep` test to refactor.
- [`lessons/0064-testing-flows-turbine.html`](../lessons/0064-testing-flows-turbine.html) (`0064-ex2`): Replaced pre-solved starter with unchecked draining loop and aligned hint.
- [`lessons/0065-review-testing-bug-hunt.html`](../lessons/0065-review-testing-bug-hunt.html) (`0065-ex2`): Replaced pre-solved `CleanWallet` implementation with `TODO(...)` stub.

### Pedagogical Polish: Quizzes, Hints, Sources & Prompts
- Rebalanced **13** length-skewed quiz questions (`0007 Q7`, `0019 Q1`, `0030 Q1`, `0040 Q1`, `0041 Q1`, `0042 Q1`, `0043 Q1`, `0046 Q1`, `0047 Q1`, `0051 Q1`, `0052 Q1`, `0056 Q1`, `0063 Q1`) so correct answers can no longer be spotted by word length.
- Reconciled **36+** stale/mismatched `<details class="hint">` blocks across lessons `0017`–`0058` and `0061`/`0064` (including swapped hints in `0045-ex1`/`0045-ex2`, Two Sum vs Running Sum in `0034-ex1`, invalid `IntArray.windowed` in `0037-ex1`, and wrong symbol names in `0018`–`0054`).
- Added tailored `<div class="source">` blocks to **28** lessons (`0017`, `0025`, `0029`–`0052`, `0054`, `0056`) and `<div class="ask">` blocks to **40** lessons (`0013`–`0023`, `0025`–`0052`, `0054`, `0056`) so all 65 teaching lessons (`0001`–`0065`) match the `/teach` template.
- Added Kotlin `2.2`–`2.4` & Android currency updates in [`0027`](../lessons/0027-references-inline-returns.html) (non-local `break`/`continue`), [`0053`](../lessons/0053-stateflow-sharedflow-hot-streams.html) (explicit backing fields), [`0060`](../lessons/0060-kmp-essentials.html) + [`reference/kmp.html`](../reference/kmp.html) (`kotlin.time.Clock`/`Instant` and `kotlin.uuid.Uuid`), labeled [`0058`](../lessons/0058-capstone-backend-spring.html) / [`index.html`](../index.html) as an Optional Backend Track, and expanded [`reference/delegation.html`](../reference/delegation.html), [`reference/generics.html`](../reference/generics.html), and [`reference/coroutines-flow.html`](../reference/coroutines-flow.html).

---

## 2. Final Verification Results (`kotlinc-jvm 2.3.10`)

- **Structural & HTML Audit (`83` files: `index.html`, `66` lessons, `16` reference sheets):**
  - `broken_html = []`, `stale_text = []`, `missing_src = []`, `missing_ask = []`
  - `quiz_bugs = []`, `skewed_quizzes = []`, `leaked_starters = []`
  - `133 / 133` HTML exercises paired 1-to-1 with `assets/answers.js` keys.
- **Model Answers (`133 / 133`):** `133` compiled (`compile_fail = 0`), `133` ran cleanly (`runtime_fail = 0`), `133` matched `expectedOutput` (`mismatch = 0`).
- **Determinism Check (`0051-ex2` x5 consecutive runs):** `5 / 5` identical pass (`Step 1` -> `Cancelling job...` -> `Job cancelled cleanly!`).
- **Standalone Runnable Examples (`45` blocks outside exercises):** `37` passed, `5` intentional `// ❌` compile-error illustrations (`0001#run3`, `0004#run1`, `0005#run5`, `0008#run1`, `0010#run5`), `3` intentional runtime crash demos (`0009#run3`, `0011#run1`, `0011#run3`), `0` unexpected failures.
- **Exercise Starters (`133` blocks with `main`):** `90` compiled & ran cleanly; `31` write-from-blank starters fail to compile on calls to not-yet-written solutions; `4` intentional pre-refactor compile failures (`0046-ex2`, `0047-ex2`, `0048-ex2`, `0061-ex2`); `5` write-from-blank `TODO`/failing `check()` runtime stubs (`0004-ex1`, `0009-ex1`, `0011-ex1`, `0055-ex1`, `0059-ex1`); `3` intentional pre-refactor runtime stubs/crashes (`0011-ex2`, `0055-ex2`, `0065-ex2`); `0` unintended starter failures.

---

## 3. Roadmap Comparison (`roadmap.sh/kotlin` -> 2027 Android Mastery)

- **Recommended Additions (High ROI for Junior Android -> 2027 Mastery):**
  1. **Modern Kotlin 2.2–2.4 Syntax Lab**: Context Parameters (`context(logger: Logger)`), Explicit Backing Fields (`val state: StateFlow<S> field = MutableStateFlow(...)`), hands-on `when` guard condition refactoring, `@DslMarker`, `fun interface` SAM, `crossinline`/`noinline`, and `tailrec` / `DeepRecursiveFunction`.
  2. **Production Android Concurrency & State Lesson (`10.6` or `11.7`)**: Writing `callbackFlow` + `awaitClose`, `stateIn(viewModelScope, WhileSubscribed(5_000), initial)`, `flatMapLatest` for typeahead search, and `Mutex.withLock` from blank.
  3. **Android Build & Serialization Essentials**: `build.gradle.kts` + Version Catalogs (`libs.versions.toml`), `kotlinx.serialization` (`@Serializable`, `Json { ignoreUnknownKeys = true }`), and Java/Android interop annotations (`@JvmOverloads`, `@JvmName`, `@JvmStatic`, `@Throws`).
- **Recommended Deferrals / Lightening:**
  - **Spring Boot (`0058`)**: Keep as an explicitly marked Optional Backend Track; defer while focusing on Android & KMP (Ktor aligns better with KMP later).
  - Keep custom property delegate authoring (`ReadWriteProperty`) and heavy operator overloading in *reading-first* scope rather than deep authoring drills.
