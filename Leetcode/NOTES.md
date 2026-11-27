# Teaching Notes

## User preferences
- **Primary live-interview language:** **Kotlin** (confirmed 2026-11-26), with full trilingual side-by-side reference in **Kotlin, Python, and Java**.
- **Session structure (`/teach` loop):**
  1. **Spaced warm-up (5–10 min):** Recall from memory 2 concepts/templates from ~1 and ~3–7 sessions back.
  2. **Concept & Walkthrough (10–15 min):** Read the HTML lesson's *Idea*, *Pattern Templates*, and *Walkthrough*, step through the interactive visualizer, and answer the 3-question *Quick Check*.
  3. **Live Interview Practice (25–35 min):** Pick **2–3 Core problems** (keep *Stretch* problems for spaced review or company-specific loops). Explain clarifying questions, examples, and approach in chat *before* coding; state time and space complexity after coding.
- **Hint protocol:** Give hints step by step (nudge → direction → key insight → pseudocode). Reveal full solutions only when explicitly asked.
- **Code review focus:** Correctness, boundary edge cases, Kotlin idioms (`IntArray`, `ArrayDeque`, null-safety, overflow avoidance), and JVM/Python trade-offs.
- **Checkpoints & Mocks:** Unlabeled mixed diagnostic checkpoints run after Sessions 8 (Checkpoint A), 15 (Checkpoint B), 25 (Checkpoint C), and 35 (Checkpoint D) inside `lessons/0037-unlabeled-checkpoints.html`, followed by 2027 Progressive Specs & Bug-Hunt (`0038`) and 45-Minute Mock Rehearsals (`0039`).

## Curriculum Architecture (39 Lessons: Sessions 0–38)
- Lesson filenames are 1-indexed (`0001` = Session 0, `0002` = Session 1, …, `0039` = Session 38).
- **Core Track vs. Track B (Specialized):** Five specialized lower-frequency modules (`0016` Bitwise Trie section, `0021` Dijkstra/Bellman-Ford/Floyd-Warshall, `0026` Bitmask DP, `0027` Segment/Fenwick Trees, `0035` MST/Eulerian/Bridges) are badged **Track B · Specialized** so learners on a tight timeline can prioritize High/Medium-frequency rounds (including Phase 6 LRU/Rate Limiter/Concurrency and Phase 7 Matrix/Strings/Quickselect) first.
- **In-Browser Runner & Workspace Components (`assets/`):**
  - `assets/practice.js`: Lazy-loads **Pyodide (CPython 3.12 WebAssembly)** on first `▶ Run Tests` click with isolated `data-setup` harnesses, plus a **📋 Kotlin Stub** generator that copies clean Kotlin class/method signatures without revealing solutions.
  - `assets/progress.js`: Shared top navigation bar across `index.html`, `reference/*.html`, and `lessons/*.html` with `localStorage` session mastery tracking, automatic `1/3/7`-back spaced-review surfacing on `index.html`, interview countdown timer (`25m`/`35m`/`45m`), `Auto/Dark/Light` theme switcher, and 1-click `Copy` buttons on code blocks.
- **Local Environment Note:** `python3` (3.14), `javac` (JDK 26), and JetBrains `kotlin-compiler.jar` (`/Applications/IntelliJ IDEA.app/Contents/plugins/Kotlin/kotlinc/lib/kotlin-compiler.jar`) are all available locally.

## Curriculum Build History (Pre-Session 0)
- **2026-10-18 → 2026-11-21:** Built initial 38-session trilingual HTML curriculum aligned with NeetCode 150, Blind/Grind 75, and `roadmap.sh/leetcode`, with synchronized tabs (`assets/tabs.js`), per-choice retrieval quizzes (`assets/quiz.js`), hint ladders (`assets/hints.js`), and trace steppers (`assets/trace.js`).
- **2026-11-26 (2027 End-to-End Audit & Complete Upgrade):** Replaced dead Skulpt CDN/harness with lazy-loaded **Pyodide (Python 3.12 WASM)** + isolated `data-setup` drivers (`39/39` verified) + one-click Kotlin stub copier; built root hub (`index.html`), searchable HTML glossary (`reference/glossary.html`), and `assets/progress.js`; expanded `trace.js` visualizers to `39/39` lessons; enforced exact `0`-spread word-count parity across all `117/117` MCQs; verified `79/79` Kotlin classes under `kotlinc` and `83/83` Java classes under `javac`; fixed duplicate `.why` quiz CSS, explicit `[data-theme="dark"]` tokens, `0021` Kotlin `!in`, 5 dead YouTube links, raw LaTeX/escaped entities, `0018` true 0-1 BFS walkthrough (*Minimum Obstacle Removal*), `0004` trilingual templates, same-lesson duplicates (`0020`, `0022`), `1/3/7` spaced warm-ups, Core vs. Stretch splits, `[Premium]` inline specs + free mirror links, Checkpoints A–D (`0037`), Session 37 (`0038-practical-progressive-and-bug-hunt.html`), and modernized toolkits for JDK 21/25, Kotlin 2.x, and Python 3.12+.
