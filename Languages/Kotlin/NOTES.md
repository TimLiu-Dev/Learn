# Teaching Notes

## Learner Profile & Teaching Rules
- Learner relies on AI to write code and wants full blank-page independence: every teaching lesson includes an **Interactive Concept Lab**, a **Blank-Page Syntax Blueprint**, a **write-from-blank** exercise (`ex1`), and an **AI/Java refactor** exercise (`ex2`). Recall beats recognition.
- Little or no Java: never use Java as the prerequisite explanation. Mention JVM bytecode only when explaining compiler behavior (e.g. `value class` boxing, `@JvmOverloads`, Spring `all-open`).
- Pillar Priority Order: **1. Android (Jetpack Compose)** → **2. DSA Coding Interviews** → **3. Spring Boot Backend** → **4. KMP (Optional)**.
- Never give exercise solutions unprompted in chat; give Socratic hints in small steps.
- Every lesson opens with spaced-retrieval prompts strictly from *prior* lessons (`.recall`).

## Workspace & Execution Conventions
- **Local Server (`./serve.sh` or `python3 serve.sh`)**: Serves `http://localhost:8000` and compiles/runs code 100% offline via the local IntelliJ `kotlinc-jvm 2.4.0` toolchain (with `25s` compile / `10s` run timeouts and accurate `File.kt` stack line numbers), falling back to JetBrains' `2.4.21+` compiler API if needed.
- **Persistence (`assets/lesson.js`)**: Automatically persists exercise code drafts, solved badges, quiz selections, checklist checkboxes, and lesson completions in `localStorage` (`kt_course_v1_*`), hydrating the live progress bar on `index.html`.
- **Verification**: Run `python3 -I tools/check_kt.py lessons/<file>.html` to verify runnable blocks before shipping changes.
- **`learning-records/` Hygiene**: Keep `learning-records/` strictly for learner-demonstrated insights and ZPD milestones (`0001`, `0002`, and future study milestones). Course construction/QA changelogs live below in `NOTES.md`.

## Course Build & QA Changelog (Archived from Pre-Study Setup)
- **2026-10-18 – 2026-11-21 (Pre-Launch Build Sessions)**: Authored Sections `0`–`13`, 4 Production Pillar Playbooks, 13 Language Reference Sheets, `GLOSSARY.md`, `RESOURCES.md`, Blank-Page Syntax Blueprints, Interactive Concept Labs, and length-balanced quizzes.
- **2026-11-24 (Final Pre-Study End-to-End Audit & Upgrade)**:
  - Upgraded `serve.sh` (process timeouts, exact `File.kt` runtime line numbers, Kotlin `2.4.21` endpoint priority) and `assets/lesson.js` (`localStorage` draft/quiz/completion persistence, `index.html` progress dashboard, `file://` helper banner, comment-stripped assertion detection).
  - Added `0073-dsa-strings-chars-dp.html` (`7.6`: `Char` arithmetic, `buildString`, memoized DP) and `0074-dsa-trees-graphs-backtracking.html` (`7.7`: `TreeNode?` DFS/BFS, adjacency lists, Kahn's cycle detection, backtracking snapshot rule).
  - Split `0066` into live runnable Kotlin 2.2–2.4 syntax (`13.1` `0066`: `when` guards, Context Parameters, Explicit Backing Fields) and `13.2` `0075-dsls-sam-deep-recursion.html` (`@DslMarker`, `fun interface` SAM, `tailrec`, `DeepRecursiveFunction`).
  - Sequenced **Section 11 (Testing)** before **Section 12 (Four-Pillar Capstones)**, ordered Capstones as **Android → DSA → Spring Boot → KMP [Optional]**, marked `0060` (KMP) and `0069` (Staff SDK Internals) as `[Optional]`, added the fast-track calibration rubric to `0000-diagnostic.html`, fixed all HTML generic escaping/backticks/recalls, and verified **76 lessons / 153 exercises / 262 quizzes** end-to-end on `kotlinc-jvm 2.4.0`.
