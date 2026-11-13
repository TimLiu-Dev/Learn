# Teaching Notes

- Learner relies on AI to write code and can't write it alone, so every lesson needs a **write-from-blank** exercise as well as refactors. Recall beats recognition.
- Little or no Java: don't use Java as the explanation. Show it only when it shows what the compiler generates (e.g. data class `equals`/`copy`, `companion` → static).
- Frame "Java written in Kotlin" as **AI/Java-flavoured Kotlin**: the patterns AI tools and Java-trained habits produce.
- Exercises go in Kotlin Playground (no local kotlinc installed). Learner pastes answers back for review.
- Never give solutions unless asked. Give hints, in steps, as questions where possible.
- Context: Android first, learning Spring Boot, KMP, and DSA coding interviews (LeetCode/HackerRank in Kotlin, standard library collections, time/space complexity).
- Exercises: Native in-browser code editor with Tab/Enter indentation + "Copy Code", "Playground ↗", and "Model Answer" tab. 100% offline and works opening HTML directly via file:// without any localhost server. Learner runs code in local IDE or Kotlin Playground.
- Format: one concept per lesson; each section ends with a mixed (interleaved) review.
- Open each lesson with 2–3 retrieval questions from earlier lessons (spacing).

## Workspace conventions (for the teacher)
- Components: `assets/style.css` (all pages) and `assets/lesson.js` (quiz widget + JetBrains Kotlin Playground embed). Markup contract is documented at the top of `lesson.js`. `lessons/0001-…` is the template for new lessons.
- `code.kt-run` = runnable/editable, `code.kt-show` = highlight-only. Escape `<` `>` `&` inside them.
- Native editor works completely offline directly from disk (`file://`). No localhost server required.
- Verify every runnable block before shipping: `python3 -I tools/check_kt.py lessons/<file>.html`.
- Glossary: Created `GLOSSARY.md` following canonical format with domain clustering across Core, Android, KMP, Spring Boot, and DSA.

## Progress
- 2026-10-18: Sections 1 and 2 built (lessons 0001–0012, reference/foundations.html, reference/null-safety.html).
- Master plan expanded to include Generics (Section 9), Coroutines & Flow (Section 10), and Capstones & KMP (Section 11) to provide complete coverage of the Kotlin language across Android, KMP, Spring Boot, and DSA Coding Interviews.
- 2026-11-06: Created master plan HTML page (`lessons/0000-master-plan.html`) serving as the first page of the lessons with a complete backlog legend, and updated `index.html` with synchronized backlog badges and navigation.
- 2026-11-06: Built Calibration Diagnostic 0.1 (`lessons/0000-diagnostic.html`), Section 3 (Functions: 0013–0017), Section 4 (Classes & Domain Modeling: 0018–0025), Section 5 (Lambdas & Functional Kotlin: 0026–0029), Section 6 (Scope Functions: 0030–0033), Section 7 (Collections & DSA Data Structures: 0034–0040), Section 8 (Delegation: 0041–0044), Section 9 (Generics & Type System: 0045–0048), Section 10 (Coroutines & Flow: 0049–0054), and Section 11 (Capstones & Smell Audits: 0055–0061).
- 2026-11-06 (Triple-Check & Pillars Expansion): Added 4 dedicated Production Pillar Playbooks (`reference/android.html`, `reference/spring-boot.html`, `reference/kmp.html`, `reference/dsa-patterns.html`), expanded Lesson 0037 with bitwise infix operators (`shl`/`shr`/`and`/`or`/`xor`/`inv`) and 2D matrix representations, enriched Lessons 0057, 0058, 0060 with modern framework mechanics (Compose state, `@field:` use-site targets, KMP library stack), added `GLOSSARY.md`, and enriched `RESOURCES.md` with official Spring, Android, KMP, and DSA sources.
- 2026-11-07 (Senior Developer Audit & Phase 1 Repairs): Fixed `assets/answers.js` (converted 125 entries from broken JS template literals with 29 `${...}` syntax/reference bombs to pure JSON encoding), repaired all 28 non-clickable `<ol type="A">` quizzes, escaped all vanished HTML generics (`<String>`, `<out T>`, `<reified T>`, etc.) across 67 HTML files, stripped pre-solved solutions from all 11 leaked exercises, added 12 new Senior quizzes to Capstones `0057–0060`, de-duplicated `0022`/`0053`/`0057` ViewModel exercises, added value-class boxing rules (`0024`) and `runCatching` + `CancellationException` warnings (`0055`), removed offline-blocking Google Fonts import and duplicate `0000-master-plan.html`, and queued Section-by-Section Senior expansion + Section 12 (Testing).

## Session 0006 (2026-11-12) · Full QA Pass & roadmap.sh/kotlin (2027) Audit
- Audited all 66 lessons (`0000`–`0065`), 16 reference sheets, `index.html`, `assets/lesson.js`, and all 133 `assets/answers.js` entries.
- Fixed 19 model-answer drifts, 5 leaked refactor starters (`0038-ex2`, `0062-ex2`, `0063-ex2`, `0064-ex2`, `0065-ex2`), 13 length-skewed quiz questions, 36+ mismatched hints across `0017`–`0058`, missing `.source` (28 lessons) and `.ask` (40 lessons) blocks, broken quotes in `0009`, stripped templates in `0000`, `lesson.js` `/reference/` path fallback, and stale `"coming soon"`/`"Claude"` links.
- Added Kotlin 2.2–2.4 callouts (`0027` non-local `break`/`continue`, `0053` explicit backing fields, `0060` + `reference/kmp.html` `kotlin.time.Clock`/`Instant` + `kotlin.uuid.Uuid`), marked `0058` Optional Backend Track, and expanded `reference/delegation.html`, `reference/generics.html`, and `reference/coroutines-flow.html`.
- Post-edit verification (`kotlinc-jvm 2.3.10`): `133/133` model answers compile + run + match `expectedOutput` (with `0051-ex2` verified deterministic over 5 runs); `0` broken HTML/links/skewed quizzes/missing `.source`/`.ask`/solution leaks.
- See `learning-records/0006-roadmap-audit-and-full-qa.md` for full verification stats and `roadmap.sh/kotlin` Add/Defer/Lighten recommendations.

## Session 0007 (2026-11-12) · Section 13 Expansion (`0066–0069` & Modern Tooling Reference)
- Built Section `13 · Modern Kotlin 2.2–2.4, Production Flow & Android Tooling (2027 Track)` in response to the `roadmap.sh/kotlin` 2027 audit:
  - `lessons/0066-modern-kotlin-dsls-recursion.html` (`13.1`: `when` guards, non-local `break`/`continue`, Context Parameters, Explicit Backing Fields, `@DslMarker`, `fun interface`, `tailrec`, `DeepRecursiveFunction`)
  - `lessons/0067-android-flow-concurrency-patterns.html` (`13.2`: `callbackFlow` + `awaitClose`, `stateIn(WhileSubscribed(5_000))`, `flatMapLatest`, `Mutex.withLock`)
  - `lessons/0068-gradle-kts-serialization-interop.html` (`13.3`: `build.gradle.kts`, `libs.versions.toml`, `kotlinx.serialization`, `@JvmOverloads`, `@JvmName`, `@JvmStatic`, `@Throws`)
  - `lessons/0069-review-2027-android-mastery.html` (`13.R`: cumulative quiz + offline-sync capstone lab + 4-bug PR hunt)
  - `reference/modern-kotlin-tooling.html` (17th reference cheat sheet)
- Wired `0065 -> 0066 -> 0067 -> 0068 -> 0069 -> ../index.html`, updated `index.html`, `CURRICULUM.md`, `RESOURCES.md`, and added 8 verified model answers (`0066-ex1`..`0069-ex2`) to `assets/answers.js` (141 total exercises).
- See `learning-records/0007-section13-modern-kotlin-modules.md` for details and post-edit verification counts.
