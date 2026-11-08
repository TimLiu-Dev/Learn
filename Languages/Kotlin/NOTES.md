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
