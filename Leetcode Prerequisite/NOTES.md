# Learner Notes & Working Preferences

- **Current Learning State (CRITICAL FOR `/teach` ZPD):**
  - **Completed Lessons:** `0 / 16` — Learner has **not started Lesson 01 yet**; curriculum was audited and expanded end-to-end so the learner can begin cleanly at `lessons/0001-how-computers-execute-code.html`.
  - **Next Active Lesson:** **Lesson 01** (`lessons/0001-how-computers-execute-code.html`).
- **Learner Profile:** Self-taught software engineer with strong software fluency, treating this curriculum as a formal, rigorous Computer Science major education from ground zero.
- **Pedagogical Standard: "Deep CS Edition":**
  - **Zero Hand-Waving:** Never introduce an abstraction (Hash Maps, dynamic arrays, heaps, recursion, sorting) as a black box. Always explain the underlying machine mechanics: CPU registers, ALU operations, memory buses, 64-byte cache lines, Call Stack vs. Heap, hash functions, and bucket arrays.
  - **Comprehensive Explanations + ELI5 Analogies:** Pair formal CS derivations and state traces with vivid everyday ELI5 callouts.
  - **Active Verification & Desirable Difficulty:** Every lesson includes interactive simulators, Spot & Fix the Bug code drills, Fill-in-the-Code labs (with Hint & Show Solution), or active Trace Tables, plus word-balanced retrieval quizzes.
- **Languages & Runtime (2027 Verified):**
  - Primary implementation language: **Kotlin 2.x** (matching LeetCode's Kotlin 2.x / OpenJDK 25 runtime, including `mod()`, `digitToInt()`, `lowercaseChar()`, `0..<n`, `ArrayDeque`, `compareBy`).
  - Secondary read-along languages: **Python** and **Java**.
- **2027 Interview Dynamics Stance:**
  - Modern 2027 technical loops test two complementary modes:
    1. **Zero-AI / Supervised Problem Solving & Multi-Part Mutations:** Clarify constraints, verbalize brute force in 30s, isolate the bottleneck, secure consensus, write clean optimal code, manually dry-run via trace tables, and pivot smoothly on Part 2 constraint twists (streaming, `O(1)` memory, thread safety).
    2. **Code-Critique & AI-Assisted Debugging Rounds:** Audit unfamiliar or AI-drafted code for disguised `O(n²)` library calls, 32-bit overflow, off-by-one invariants, and cyclic MLEs, backed by adversarial edge-case test design.
- **Unified 16-Lesson Curriculum Architecture (`0001` – `0016`):**
  - **Stage A — Hardware, Memory & Complexity Foundations (`01` – `05`):**
    - `0001`: Code vs. Algorithms, JVM/CPU Fetch-Decode-Execute, constant factors, elementary steps, `0..<n` vs `until`, loop scaling, 2 Complexity Quizzes, pair-loop lab.
    - `0002`: Bits & Bytes, Two's Complement (`0x7FFFFFFF`, `abs(Int.MIN_VALUE)`), truncated vs. float division, value vs. heap reference, Hex `0x1000`, ASCII `ch - 'a'`, `.mod()` bug hunt & ASCII lab.
    - `0003`: Von Neumann hierarchy, 64-byte cache lines, `O(1)` index math vs. `O(n)` scan, Gauss `n(n-1)/2`, Hash Table modulo compression & separate chaining simulator, `IntArray` hash-key trap, disguised `ArrayList.contains` bug hunt.
    - `0004`: Asymptotics (`O`/`Ω`/`Θ`), worst/average/amortized, `O(√n)` & `O(n·2ⁿ)` classes, `log₂ n` Halving Game simulator, hidden stdlib Big O, multi-variable & sliding-window amortization, Stack vs. Heap space.
    - `0005`: `10^7` safe / `10^8` hard CPU ceiling simulator, MLE memory math, 32-bit multiplication overflow (`a.toLong() * b`), modulo `10^9 + 7`, safe-midpoint lab & overflow bug drill.
  - **Stage B — 2027 Interview Protocol, Dry-Running & Prompt Literacy (`06` – `08`):**
    - `0006`: 45-minute 6-stage choreography, clarifying matrix, 3-sentence bottleneck pivot, 2027 Dual-Mode Playbook (Multi-Part Mutations + AI/Code-Critique Auditing), single-lookup null-safe Kotlin lab.
    - `0007`: Deterministic state-machine dry-running, `<` vs `<=` loop invariants, binary search `left = mid` infinite-loop trap, 6-point edge-case gauntlet, active trace table lab, LC #704 & #167 bridges.
    - `0008`: Subarray vs. Subsequence vs. Subset, prompt-to-pattern signals, UTF-16 `String` vs. `StringBuilder`, interactive `O(1)` Prefix Sum range calculator, string concat bug hunt, 2027 spaced-repetition playbook, LC #303 bridge.
  - **Stage C — First LeetCode Milestone & Discrete Math / Bitwise (`09` – `10`):**
    - `0009`: Online Judge harness (`class Solution`, no `main()`), Kotlin 2.x / OpenJDK 25 notes, Run vs. Submit, all 6 verdicts, stateful-field bug drill, and conquering **LeetCode #1 Two Sum** from scratch.
    - `0010`: CPU bitwise ALU simulator (`and`, `or`, `xor`, `shl`, `shr`, `ushr`), negative-safe parity `(x and 1) == 1`, power-of-2 `n and (n - 1)` mask, ceiling division `(a + b - 1) / b`, arithmetic/geometric series, Euclidean GCD, `O(√n)` primality, LC #136 & #231 bridges.
  - **Stage D — Complete Data Structures, Traversals & Optimization Toolkit (`11` – `16`):**
    - `0011`: `IntArray` vs `ArrayList` geometric doubling simulator, Kotlin swap & hashable `Pair(r, c)`, `HashSet` vs `TreeMap` (`O(log n)` ordered) vs `IntArray(26)`, `ArrayDeque` ring buffer (`removeLastOrNull` #20 guard), `ListNode?` backticks, **Dummy Sentinel Head (`ListNode(0)`)** & `nextTemp` reversal (#206), guided labs for **LC #217 & #242**.
    - `0012`: Binary Min/Max Heap array indexing (`2i+1`, `2i+2`), Top-K Min-Heap gatekeeper pattern, Binary Tree vs. BST invariants, Adjacency List (`O(V+E)`) vs. Matrix (`O(V²)`), 2D grid 4-dir offsets, cyclic BFS `visited` MLE bug hunt, **LC #215** lab.
    - `0013`: Hardware Call Stack push/unwind visualizer, chain `O(n)` vs. tree `O(2ⁿ)` recursion, `fib(n)` `O(n)` stack space & memoization preview, JVM `n = 10^5` `StackOverflowError` limit, **Backtracking Shared-Heap State (`choose → explore → unchoose` + `ArrayList(path)` leaf copy)**, unwind trace table, **LC #104** lab.
    - `0014`: Merge Sort, Dual-Pivot Quicksort & TimSort internals, two-half merge stepper, `Ω(n log n)` decision-tree proof, Sort Stability, `O(n + k)` Counting Sort, overflow-safe `compareBy<IntArray>` comparators, **LC #88 & #56** bridges.
    - `0015`: Monotonic `firstTrue` boundary binary search (`while (left < right)`, `right = mid`), Pre/In/Post-Order Tree DFS (sorted BST law), Level-Order BFS `val levelSize = q.size` wave snapshot, `nr in grid.indices` bounds, **LC #278, #35 & #102** bridges.
    - `0016`: Same-direction Read/Write in-place pointers (#283 Move Zeroes), Greedy running-min (#121) vs. Coin Change counterexample test, Top-Down Memoization (`IntArray(n + 1) { -1 }` collapsing `O(2ⁿ) -> O(n)`), **LC #70 Climbing Stairs** lab.
