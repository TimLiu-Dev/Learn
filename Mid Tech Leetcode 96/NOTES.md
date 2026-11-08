# NOTES & Architectural Decisions

## 1. File Naming Standard: Dual Indexing (103 Problems)
All lessons follow chronological dual indexing:
`./lessons/{0001-0103}-lc{LeetCode#}-{slug}.html`
- **Core Set (`0001`–`0096`)**: Preserved intact so spaced-retrieval pairs (`#933→#362`, `#191→#338`, `#435→#452`, `#417→#130`, `#207→#210`) and existing URLs/localStorage keys never break.
- **2027 Mid-Level Additions (`0097`–`0103`)**:
  - `0097-lc88-merge-sorted-array.html` (Pass 1 — Reverse 3-pointer in-place merge)
  - `0098-lc359-logger-rate-limiter.html` (Pass 1 — Timestamp HashMap deduplication)
  - `0099-lc53-maximum-subarray.html` (Pass 2A — Kadane's 1D DP/greedy reset)
  - `0100-lc48-rotate-image.html` (Pass 2A — Transpose + reflect in-place matrix geometry)
  - `0101-lc79-word-search.html` (Pass 2B — Karat #1 grid DFS + visited backtracking)
  - `0102-lc399-evaluate-division.html` (Pass 2B — Fintech weighted ratio graph BFS/DFS)
  - `0103-lc5-longest-palindromic-substring.html` (Pass 2B — Outward two-pointer center expansion)

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
- **Section 3**: Concrete Variable Trace Table.
- **Section 4**: Interactive Visual Stepper (Presets, Custom Input, Prev/Next/Play/Reset, Memory Inspector, Synced Pseudocode).
- **Section 5**: Multi-Approach Code Examples (`Kotlin → Python → Java`) + **Runnable Code Practice Arena** (Cold Stub, Test Suite + Custom Case, 3 Progressive Hints, Final Answer).
- **Section 6**: 4-Phase Mid-Level Spoken Interview Script, Edge Cases Checklist, and Practical Mid-Tech Follow-Ups.
- **Section 7**: **4 Diagnostic Code Quizzes** (with per-option right/wrong explanations), 48-Hour Cold Recall Timer, Primary Trusted Sources (`RESOURCES.md`), and Teacher Agent Follow-Up Prompt Card.
