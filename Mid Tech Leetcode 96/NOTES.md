# NOTES & Architectural Decisions

## 1. File Naming Standard: Dual Indexing (126 Problems = 119 Core + 7 Optional)
All lessons follow chronological dual indexing:
`./lessons/{0001-0126}-{lcID|custom}-{slug}.html`
- **Original Set (`0001`–`0096`)**: Preserved intact so spaced-retrieval pairs (`#933→#362`, `#191→#338`, `#435→#452`, `#417→#130`, `#207→#210`) and existing URLs/localStorage keys never break. Low-ROI/redundant entries (`#0003`, `#0020`, `#0021`, `#0022`, `#0053`, `#0065`, `#0077`) are tagged `Optional / Low-ROI`.
  - `#0067` (`0067-custom-in-memory-kv-store-ttl.html`) is upgraded to a deterministic **4-Stage CodeSignal ICA** (`Entry(value, expiresAt)` + `O(T)` before-image UndoMap stack, deterministic integer timestamps).
  - `#0068` (`0068-lc380-insert-delete-getrandom-o1.html`) is classified under **Multi-Tier OOP & Timed State Systems** (`ArrayList + HashMap<Val, Index>` swap-with-last OOP design).
- **2027 Mid-Level Additions (`0097`–`0116`)**:
  - `0097`–`0110`: `#88`, `#359`, `#53`, `#48`, `#79`, `#399`, `#5`, `#271`, `#230`, `#105`, `#621`, `#55`, `#416`, `#91`
  - `0111`–`0116`: `#528`, `#34`, `#2`, `#143`, `#152`, `#1091`
- **2027 Gap & ICA Additions (`0117`–`0126`)**:
  - `0117-lc242-valid-anagram.html` (Pass 1 — Fixed 26-frequency counter bridge between `#217` and `#49`)
  - `0118-lc252-meeting-rooms.html` (Pass 1 — Interval sort & adjacent overlap invariant bridge to `#56`/`#253`)
  - `0119-lc733-flood-fill.html` (Pass 1 — 4-directional grid DFS/BFS bridge to `#200`/`#994`)
  - `0120-lc703-kth-largest-element-in-a-stream.html` (Pass 1 — Size-`k` Min-Heap OOP stream bridge to `#215`/`#295`)
  - `0121-lc40-combination-sum-ii.html` (Pass 2B — Sorted backtracking with same-depth sibling deduplication)
  - `0122-lc17-letter-combinations-of-a-phone-number.html` (Pass 2A — Multi-way digit-to-letters backtracking)
  - `0123-lc721-accounts-merge.html` (Pass 2B — Union-Find / bipartite DFS entity equivalence clustering)
  - `0124-lc36-valid-sudoku.html` (Pass 2A — GCA Q3 row/col/box constraint hashing & `(r/3)*3 + (c/3)` indexing)
  - `0125-lc692-top-k-frequent-words.html` (Pass 2B — Custom comparator Min-Heap with inverted lexicographical tie-breaker)
  - `0126-custom-cloud-storage-ica.html` (Pass 2B — 4-Stage CodeSignal ICA: CRUD/Copy → Prefix/Suffix Search → User Quotas & Eviction → Backup/Restore Snapshots)

## 2. Shared `/teach` Component Architecture (`./assets/` & `./reference/`)
- `./assets/lesson-theme.css`: Shared typography (`Plus Jakarta Sans` + `JetBrains Mono`), dark-mode Tufte-meets-Pocock surfaces, Study-Order navigation bar styling, code editor & quiz feedback states, and print rules.
- `./assets/lesson-runtime.js`: Classic `<script src="../assets/lesson-runtime.js">` (zero ES-module `file://` CORS issues) providing:
  1. **Study-Order Navigation Bar (`LessonRuntime.StudyNav`)**: Automatically renders Recommended Study-Order `Prev / Next` (`Pass 1 → 2A → 2B → 3 → Optional`), `Pass X • Y of Z` progress pill, `🎲 Interleave` random review button, and `📋 Copy` buttons on code blocks.
  2. **High-DPI Vector Whiteboard**: 6 colors, Pen/Eraser, 3 stroke widths, Undo, Clear, Minimize.
  3. **Runnable Tri-Language Practice Engine (`Kotlin → Python → Java`)**:
     - Starts from cold method/class stubs (with `ListNode` / `TreeNode` / `Node` definition headers where applicable).
     - Includes **⏱ Cold Solve Timer** (`15m` Easy, `25m` Medium, `35m` Hard), **🐛 Debug Broken PR** mode, **3 sequential Hints**, and **gated Final Answer**.
     - Distinguishes **`Cold ★`** (solved without revealing hints or solution) from **`Assisted ✓`** in `localStorage`.
     - Enforces deep-copy identity checks (`random_list`, `graph`), non-mutating return validation, outer-only `unordered` vs nested `unorderedInner` comparators, and `accountsMerge` equivalence checks.
  4. **4-Question Diagnostic Code Quiz Engine**: Strict word-count parity across `A–D`, balanced answer letter rotation, HTML generic escaping, `localStorage` persistence, and `Reset Quiz Answers`.
  5. **48-Hour Spaced-Repetition Cold Recall Tracker**.
- `./reference/`:
  - `reference/pattern-reference.html`: 16-pattern trigger radar, invariant cards across all 126 lessons, and tri-language stdlib syntax table.
  - `reference/glossary.html`: Searchable, printable HTML Glossary covering complexity, data-structure invariants, and 2027 ICA mechanics.
  - `reference/resources.html`: 2027 CodeSignal 4-Stage ICA, Karat & AI-Era Pair-Programming Playbook, Daily Study Protocol, and trusted primary references.

## 3. Authoring & Maintenance Rule
- **Do NOT run `scripts/batch_render_v2.py` over `0001`–`0110`**: `lessons/0001..0116.html` contain surgical repairs applied via `scripts/repair_all_116.py`. Use `render_lesson_v2` in `scripts/template_engine_v2.py` for `#0067` and `0117`–`0126`, or patch lesson HTML files directly.
