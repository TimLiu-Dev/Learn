# Teaching Notes

- Learner relies on AI to write code and can't write it alone, so every lesson needs a **write-from-blank** exercise as well as refactors. Recall beats recognition.
- Little or no Java: don't use Java as the explanation. Show it only when it shows what the compiler generates (e.g. data class `equals`/`copy`, `companion` → static).
- Frame "Java written in Kotlin" as **AI/Java-flavoured Kotlin**: the patterns AI tools and Java-trained habits produce.
- Exercises go in Kotlin Playground (no local kotlinc installed). Learner pastes answers back for review.
- Never give solutions unless asked. Give hints, in steps, as questions where possible.
- Context: Android first, learning Spring Boot, KMP later. Use Android-flavoured examples (UI state, ViewModel-ish code, repositories); Spring examples sometimes.
- Format: one concept per lesson; each section ends with a mixed (interleaved) review.
- Open each lesson with 2–3 retrieval questions from earlier lessons (spacing).

## Workspace conventions (for the teacher)
- Components: `assets/style.css` (all pages) and `assets/lesson.js` (quiz widget + JetBrains Kotlin Playground embed). Markup contract is documented at the top of `lesson.js`. `lessons/0001-…` is the template for new lessons.
- `code.kt-run` = runnable/editable, `code.kt-show` = highlight-only. Escape `<` `>` `&` inside them.
- Runnable blocks need http (not file://): `./serve.sh` → http://localhost:8000. The compiler API refuses `Origin: null`.
- Verify every runnable block before shipping: `python3 -I tools/check_kt.py lessons/<file>.html`.
- Glossary not started yet: per the format, add terms only once the learner has shown they can use them.

## Progress
- 2026-10-18: Sections 1 and 2 built (lessons 0001–0012, reference/foundations.html, reference/null-safety.html), at the learner's request ("generate all of 1 and 2"). Diagnostic 0.1 was skipped. Nothing reviewed from the learner yet, so no learning records beyond 0001.
