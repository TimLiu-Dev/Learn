# Learner Notes & Working Preferences

- **Learner Profile:** Self-taught software engineer with strong software fluency, treating this curriculum as a formal, rigorous Computer Science major education.
- **Pedagogical Standard: "Deep CS Edition":**
  - **Zero Hand-Waving:** Never introduce an abstraction (e.g. Hash Maps, dynamic arrays, recursion, stack frames) as a black box. Always explain the underlying machine mechanics: CPU registers, ALU operations, memory buses, cache lines, heap vs. stack, hash functions, and bucket arrays.
  - **Comprehensive & Verbose Explanations:** Deep explanatory prose, physical diagrams, line-by-line CPU state traces, and mathematical derivations without artificial truncation.
  - **ELI5 ("Explain Like I'm 5") Section for Every Concept:** Every single core concept and subtopic includes a dedicated ELI5 callout pairing deep CS rigor with a vivid, physical everyday analogy (e.g. mailboxes, cafeteria trays, odometer roll-overs).
  - **Interactive Verification & Active Practice:** Every lesson features interactive hardware/data structure simulators, active Fill-in-the-Code evaluators, Spot-the-Bug interactive code drills, or active Trace-Table evaluators, paired with strict retrieval practice quizzes with word-count-balanced options and detailed mechanical explanations.
- **Languages:**
  - Primary code: Kotlin
  - Additional read-along languages: Python and Java
- **Critical Conceptual Anchors:**
  1. **Physical Memory Reality:** RAM layout, contiguous blocks, pointer math, bus latency, and 64-byte cache hierarchy.
  2. **Data Structure Internals:** Hash functions, modulo compression, bucket arrays, separate chaining collision handling, dynamic array geometric doubling.
  3. **The "Hidden Big O" Trap in Standard Libraries:** Auditing built-in library methods (e.g., Kotlin `contains()`, `removeAt(0)`, string immutability).
  4. **Logarithmic Intuition (`log n`):** The "Halving Game" as the inverse of exponential doubling, decision tree depths.
  5. **Call Stack vs. Heap Memory:** Stack frames, recursion depth, and auxiliary space accounting.
- **Interview Dynamics Stance:**
  - Modern technical interviews require candidates to verbalize brute force upfront (20–40s) to establish comprehension and pinpoint the algorithmic bottleneck, then gain consensus and code only the optimal solution.
- **Curriculum Architecture (8 Core Lessons + 2 Prerequisites):**
  - **Prerequisites (Pre-01 & Pre-02):**
    - Pre-01: Code vs. Algorithms, Elementary steps, Problem size `n`, Single vs. Nested loop multiplication (`n * n = n²`), halving loops, Code Complexity Quiz, Fill-in-the-Code pair loop.
    - Pre-02: Bits & Bytes, 32-bit `Int` sizing (4 bytes), Hexadecimal `0x1000` prefix, RAM as a row of byte-lockers, Clock-face Modulo `%` arithmetic, ASCII offset indexing (`ch - 'a'`), negative modulo gotchas (`.mod()`), and negative modulo bug hunt.
  - **Core Lessons 01–06:**
    - Lesson 01: RAM Layout, 64-byte cache lines, separate-chaining collision simulator, array intersection polyglot code, disguised `ArrayList.contains` `O(n²)` bug drill.
    - Lesson 02: Asymptotics, Halving loops, multi-variable bounds (`O(n + m)`, `O(R · C)`), amortized sliding window loops, Code Complexity Quizzes.
    - Lesson 03: 10⁷ CPU operations ceiling, 32-bit signed overflow (`a.toLong() * b`), modulo `10^9 + 7`, safe-midpoint fill-in lab, integer overflow bug drill.
    - Lesson 04: 45-minute interview choreography, senior scripts, consensus checks, single-lookup null-safe Kotlin idioms, skipped-clarification bug drill.
    - Lesson 05: Tabular dry-running, invariant decision rules (`<` vs `<=`), active Fill-in-the-Trace-Table lab, off-by-one binary search singleton bug drill.
    - Lesson 06: Online Judge platform mechanics, verdicts (AC/WA/TLE/MLE/RE/CE), stateful class property traps, Fill-in-the-Code Two Sum lab, stateful-field bug drill.
  - **Core Lessons 07–08 (Pre-LeetCode Bridge Capstones):**
    - Lesson 07: `IntArray` vs `ArrayList`, geometric doubling simulator, `HashSet` deduplication, `IntArray(26)` frequency tables, `ArrayDeque` (LIFO/FIFO), LeetCode node backticks (`` curr.`val` ``), Guided Bridge Labs for LC #217 (Contains Duplicate) & #242 (Valid Anagram).
    - Lesson 08: Hardware Call Stack mechanics, activation frames, interactive frame push/unwind visualizer, chain `O(n)` vs tree `O(2ⁿ)` recursion, custom comparators, active stack unwind trace table, Guided Bridge Lab for LC #104 (Maximum Depth of Binary Tree).
- **Shared Interactive Engine:**
  - `assets/components.js`: Single shared JS engine handling polyglot tabs, retrieval quizzes, 2-part complexity evaluators, clickable bug line drills, fill-in-the-code verification, active trace tables, and hints.
- **Reference Library & Canonical Glossary:**
  - `reference/kotlin-leetcode-syntax.html`: Print-ready cheat sheet covering Kotlin (primary) with Python/Java equivalents for all arrays, loops, strings, ASCII math, collections, deques, heaps, sorting, and safe math.
  - `reference/big-o-and-complexity.html`: Hierarchy, 10⁷ rule, multi-variable bounds, amortized loops, standard library costs.
  - `reference/interview-protocol-scripts.html`: 45-min senior script, edge-case gauntlet, recovery lines.
  - `reference/data-structures-and-hashmaps.html`: RAM architecture, polyglot lookup cheat sheet, dynamic array doubling.
  - `reference/glossary.html` & `GLOSSARY.md`: Formal definitions and avoid-aliases for 20 core CS terms.
