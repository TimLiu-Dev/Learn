# NOTES & Architectural Decisions

## 1. File Naming Standard: Dual Indexing (116 Problems)
All lessons follow chronological dual indexing:
`./lessons/{0001-0116}-lc{ID#}-{slug}.html`
- **Core Set (`0001`–`0096`)**: Preserved intact so spaced-retrieval pairs (`#933→#362`, `#191→#338`, `#435→#452`, `#417→#130`, `#207→#210`) and existing URLs/localStorage keys never break. Low-ROI/redundant entries (`#0003`, `#0020`, `#0021`, `#0022`, `#0053`, `#0065`, `#0077`) are tagged `Optional / Low-ROI` in `master-plan.html`.
- **2027 Mid-Level Additions (`0097`–`0116`)**:
  - `0097`–`0110`: `#88`, `#359`, `#53`, `#48`, `#79`, `#399`, `#5`, `#271`, `#230`, `#105`, `#621`, `#55`, `#416`, `#91`
  - `0111-lc528-random-pick-with-weight.html` (Pass 2B — Prefix Sum + lower-bound binary search)
  - `0112-lc34-find-first-and-last-position-of-element-in-sorted-array.html` (Pass 2A — Dual biased binary search)
  - `0113-lc2-add-two-numbers.html` (Pass 2A — Dummy-head digit carry addition)
  - `0114-lc143-reorder-list.html` (Pass 2B — Mid-split + reverse + in-place zip)
  - `0115-lc152-maximum-product-subarray.html` (Pass 2B — Dual-extremum curMax/curMin DP)
  - `0116-lc1091-shortest-path-in-binary-matrix.html` (Pass 2B — 8-directional BFS shortest path)

## 2. Shared `/teach` Component Architecture (`./assets/`)
- `./assets/lesson-theme.css`: Shared typography (`Plus Jakarta Sans` + `JetBrains Mono`), dark-mode Tufte-meets-Pocock surfaces, custom scrollbars, code editor & quiz feedback states, and print rules.
- `./assets/lesson-runtime.js`: Classic `<script src="../assets/lesson-runtime.js">` (zero ES-module `file://` CORS issues) providing:
  1. High-DPI vector whiteboard (6 colors, Pen/Eraser, 3 stroke widths, Undo, Clear, Minimize).
  2. Multi-approach (`1. Brute Force` → `2. Better` → `★ 3. Optimal`) and tri-language (**Kotlin Default → Python → Java**) code viewer with clipboard Copy.
  3. **Runnable Tri-Language Practice Engine**: Starts from cold method stubs (persisted in `localStorage` with `Reset to Stub`), supports `Tab` 4-space indentation and `Cmd/Ctrl + Enter` execution, runs real Python (Pyodide CPython when online + offline Python transpiler fallback) and Kotlin/Java (in-browser JVM-subset transpiler + stdlib runtime shim + infinite-loop guard), supports custom user test inputs and custom output comparators, and provides **3 sequential Hints** + **gated Final Answer reveal/load**.
  4. **4-Question Diagnostic Code Quiz Engine**: 2 code-snippet/bug-hunt MCQs + 2 complexity/edge-case MCQs, randomized `A–D` correct answers, matched word counts across all 4 choices, and per-option explanations detailing *why the right choice works* and *why each wrong distractor fails*.
  5. 48-hour `localStorage` spaced-repetition cold recall tracker.

## 3. Comprehensive Sections per Golden Master v2 Lesson
- **Top Sticky Header + Quick-Jump Bar**: Breadcrumb to `master-plan.html` and `reference/pattern-reference.html`, Prev/Next navigation, and section anchors.
- **Section 0**: Verbatim Problem Statement, Plain-English Translation, Official Examples, Constraints (using clean HTML/Unicode math — never raw unrendered `$...$` strings), and 3 Canonical Input Shapes.
- **Whiteboard**: Collapsible vector sketchpad.
- **Section 1**: Mental Model ("Explain Like I'm 12"), "A-Ha!" Switch, Pattern Trigger, and Sister Problems.
- **Section 2**: Naive Trap (Red) vs Optimal Breakthrough (Green) + Asymptotic Trade-off Matrix.
- **Section 2.5**: Non-Code Solution Explanation & Language-Agnostic Whiteboard Pseudocode (State Setup, Step-by-Step Plain-English Walkthrough, and Clean Syntax-Free Pseudocode).
- **Section 3**: Concrete Variable Trace Table.
- **Section 4**: Interactive Visual Stepper (Presets, Custom Input, Prev/Next/Play/Reset, Memory Inspector, Synced Pseudocode).
- **Section 5**: Multi-Approach Code Examples (`Kotlin → Python → Java`) + **Runnable Code Practice Arena** (Cold Stub, Test Suite + Custom Case, 3 Progressive Hints, Final Answer).
- **Section 6**: 4-Phase Mid-Level Spoken Interview Script, Edge Cases Checklist, and Practical Mid-Tech Follow-Ups.
- **Section 7**: **4 Diagnostic Code Quizzes** (with per-option right/wrong explanations), 48-Hour Cold Recall Timer, Primary Trusted Sources (`RESOURCES.md`), and Teacher Agent Follow-Up Prompt Card.
