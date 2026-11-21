# Learner Notes & Working Preferences

- **Learner Profile:** Self-taught software engineer with strong software fluency, treating this curriculum as a formal, rigorous Computer Science major education.
- **Pedagogical Standard: "Deep CS Edition":**
  - **Zero Hand-Waving:** Never introduce an abstraction (e.g. Hash Maps, dynamic arrays, recursion, stack frames) as a black box. Always explain the underlying machine mechanics: CPU registers, ALU operations, memory buses, cache lines, heap vs. stack, hash functions, and bucket arrays.
  - **Comprehensive & Verbose Explanations:** Deep explanatory prose, physical diagrams, line-by-line CPU state traces, and mathematical derivations without artificial truncation.
  - **ELI5 ("Explain Like I'm 5") Section for Every Concept:** Every single core concept and subtopic includes a dedicated ELI5 callout pairing deep CS rigor with a vivid, physical everyday analogy (e.g. mailboxes, cafeteria trays, odometer roll-overs).
  - **Interactive Verification & Active Practice:** Every lesson features interactive hardware/data structure simulators, active Fill-in-the-Code evaluators (with Hints and Show Solution), Spot-the-Bug interactive code drills, or active Trace-Table evaluators, paired with strict retrieval practice quizzes with word-count-balanced options and detailed mechanical explanations.
- **Languages:**
  - Primary code: Kotlin
  - Additional read-along languages: Python and Java
- **Critical Conceptual Anchors:**
  1. **Physical Memory Reality:** RAM layout, contiguous blocks, pointer math, bus latency, and 64-byte cache hierarchy.
  2. **Data Structure Internals:** Hash functions, modulo compression, bucket arrays, separate chaining collision handling, dynamic array geometric doubling, binary heap array indexing.
  3. **The "Hidden Big O" Trap in Standard Libraries:** Auditing built-in library methods (e.g., Kotlin `contains()`, `removeAt(0)`, string immutability, `IntArray` identity hashing).
  4. **Logarithmic Intuition (`log n`):** The "Halving Game" as the inverse of exponential doubling, decision tree depths.
  5. **Call Stack vs. Heap Memory:** Stack frames, recursion depth (`StackOverflowError` limits), and auxiliary space accounting.
- **Interview Dynamics Stance:**
  - Modern 2027 technical interviews require candidates to clarify bounds, verbalize brute force upfront (20–40s) to establish comprehension and pinpoint the algorithmic bottleneck, defend invariants aloud, gain consensus, and code only the optimal solution.
- **Curriculum Architecture (11 Core Modules + 3 Prerequisites = 14 Lessons):**
  - **Prerequisites (Pre-01, Pre-02 & Pre-03):**
    - Pre-01 (`0000a`): Code vs. Algorithms, compilation/JVM bytecode to CPU Fetch-Decode-Execute, compiled vs. Python constant factors, best vs. worst case, halving loops, nested loop multiplication, 2 Code Complexity quizzes, and Fill-in-the-Code pair loop.
    - Pre-02 (`0000b`): Bits & Bytes, Two's Complement signed bounds (`0x7FFFFFFF`), int vs. float division & `Double` precision, value vs. reference passing, Hex `0x1000`, ASCII offsets (`ch - 'a'`), negative modulo `.mod()` bug hunt, and ASCII fill-in lab.
    - Pre-03 (`0000c`): Bitwise ALU operators (`and`, `or`, `xor`, `shl`, `shr`, `ushr`), interactive 8-bit ALU simulator, parity & power-of-2 bitmasks, ceiling division `(a + b - 1) / b`, arithmetic/geometric series, `2^10`/`2^20` bounds, Euclidean GCD, `O(sqrt(n))` primality, negative parity bug hunt, and bit/math fill-in lab.
  - **Core Lessons 01–09 (with 05b & 07b Bridges):**
    - Lesson 01 (`0001`): RAM Layout, 64-byte cache lines, separate-chaining simulator, hashability & `IntArray` key trap, load factor rehashing, disguised `ArrayList.contains` `O(n²)` bug drill.
    - Lesson 02 (`0002`): Asymptotics (`O`/`Ω`/`Θ`), Worst vs. Average vs. Amortized, `O(√n)`, `O(n·2ⁿ)` leaf copies, log base invariance, multi-variable bounds, amortized sliding window, Code Complexity Quizzes.
    - Lesson 03 (`0003`): 10⁷ safe / 10⁸ hard CPU ceiling simulator, MLE memory math, language constant factors, 32-bit overflow (`a.toLong() * b`), `abs(Int.MIN_VALUE)`, modulo `10^9 + 7`, safe-midpoint lab, overflow bug drill.
    - Lesson 04 (`0004`): 45-minute interview choreography, senior scripts, 2027 AI-era invariant defense & follow-up pivots, `firstDuplicatePair` hygiene showcase (no Two Sum spoiler), single-lookup null-safe Kotlin lab, skipped-clarification bug drill.
    - Lesson 05 (`0005`): Tabular dry-running (pointers + HashMap states), invariant decision rules (`<` vs `<=`), `left = mid` infinite-loop trap, active trace table lab, off-by-one binary search bug drill.
    - Lesson 05b (`0005b`): Subarray vs. Subsequence vs. Subset, prompt-to-pattern signals, `String`/`StringBuilder` internals, interactive Prefix Sum `O(1)` range calculator, string concat bug hunt, Prefix Sum lab, and 2027 Study Playbook.
    - Lesson 06 (`0006`): Online Judge platform mechanics, Run vs. Submit, `Unit` in-place problems, `println` TLE trap, default imports, all 6 verdicts, Two Sum lab, stateful-field bug drill.
    - Lesson 07 (`0007`): `IntArray` vs `ArrayList`, geometric doubling simulator, 2D matrix & `contentEquals` traps, `HashSet`, `IntArray(26)`, `ArrayDeque`, `ListNode?`/`TreeNode?` backticks, Guided Bridge Labs for LC #217 & #242.
    - Lesson 07b (`0007b`): Binary Min/Max Heap array indexing (`2i+1`, `2i+2`), interactive heap simulator, why Top-K uses Min-Heap, Tree/BST vocabulary, Adjacency List vs. Matrix, 2D Grid 4-dir offsets, cyclic BFS `visited` MLE bug hunt, Top-K Heap lab.
    - Lesson 08 (`0008`): Hardware Call Stack mechanics, interactive frame push/unwind visualizer, chain `O(n)` vs tree `O(2ⁿ)` recursion, `fib(n)` `O(n)` stack depth, memoization preview, JVM `n = 10^5` `StackOverflowError` limit, unwind trace table, Guided Bridge Lab for LC #104.
    - Lesson 09 (`0009`): Sorting internals (Merge Sort, Dual-Pivot Quicksort, TimSort), interactive two-half merge stepper, `Ω(n log n)` decision-tree proof, Sort Stability, `O(n + k)` Counting Sort, overflow-safe `compareBy<IntArray>` bug hunt & lab.
- **Shared Interactive Engine:**
  - `assets/components.js`: Single shared JS engine handling polyglot tabs, retrieval quizzes, 2-part complexity evaluators, clickable bug line drills, fill-in-the-code verification (with Hint & Show Solution), active trace tables, and hints.
- **Reference Library & Canonical Glossary:**
  - `reference/kotlin-leetcode-syntax.html`: Print-ready cheat sheet covering Kotlin (primary) with Python/Java equivalents for arrays, loops, strings, ASCII math, collections, deques, heaps, sorting, bitwise, prefix sums, 2D grid dirs, and safe math.
  - `reference/big-o-and-complexity.html`: Hierarchy, 10⁷ rule, multi-variable bounds, amortized loops, standard library costs.
  - `reference/interview-protocol-scripts.html`: 45-min senior script, edge-case gauntlet, recovery lines.
  - `reference/data-structures-and-hashmaps.html`: RAM architecture, polyglot null-safe lookup cheat sheet, dynamic array doubling, Binary Heap & Graph Adjacency List rows.
  - `reference/glossary.html` & `GLOSSARY.md`: Formal definitions and avoid-aliases for core CS terms.
