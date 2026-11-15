# 0005. `roadmap.sh/leetcode` Alignment & Curriculum Gap-Fill

- **Date:** 2026-11-15
- **Status:** Accepted

## Context
Prior to beginning Session 0, the 33-session curriculum was audited against `https://roadmap.sh/leetcode` (18 core pattern tracks) and the broader `roadmap.sh` DSA track. While the existing curriculum exceeded `roadmap.sh/leetcode` in 2027 Big Tech areas (O(1) system design, concurrency, bitwise tries, 0-1 BFS, unlabeled checkpoints, and mock rehearsals), the audit identified specific omissions:
1. Missing `roadmap.sh/leetcode` **Math & Geometry** category (*Rotate Image*, *Spiral Matrix*, *Set Matrix Zeroes*, *Valid Sudoku*, *Happy Number*, *Detect Squares*, *Multiply Strings*).
2. Incomplete **Advanced Graphs** coverage: `0021` omitted Bellman-Ford code templates, and Prim's/Kruskal's MST (*Min Cost to Connect All Points*) and Hierholzer's Eulerian Path (*Reconstruct Itinerary*) were missing.
3. Missing **Segment Tree** implementation in `0027` (which previously only implemented Fenwick Tree despite testing Segment Tree in the quiz).
4. Missing **String Algorithms** (Expand-Around-Center palindromes, KMP prefix table, *Basic Calculator II* expression parsing) and **Divide & Conquer / Quickselect / Boyer-Moore** (*Sort Colors* 3-way partition, *Majority Element*, *Sort List*, *Reverse Pairs*, *Random Pick with Weight* / Reservoir Sampling).
5. Missing high-frequency problems in `0002` (*Product of Array Except Self*, *Encode and Decode Strings*) and `0025` (*Burst Balloons*, *Regular Expression Matching*), plus two duplicate problems in the final mock rehearsal lesson.

## Decisions
1. **Added 5 New Lessons (`0032`–`0036`, Sessions 31–35):**
   - `0032-matrix-simulation-geometry.html`: Matrix Traversal & In-Place Simulation
   - `0033-math-geometry-randomized.html`: Math, Geometry & Randomized Algorithms
   - `0034-string-algorithms-kmp-palindromes.html`: String Algorithms: Palindromes, KMP & Parsing
   - `0035-advanced-graphs-mst-eulerian.html`: Advanced Graphs II: MST (Prim's & Kruskal's) & Eulerian Paths
   - `0036-divide-conquer-quickselect.html`: Divide & Conquer, Quickselect & Boyer-Moore
2. **Moved Phase 8 Mock Finale to `0037`–`0038` (Sessions 36–37):**
   - Moved `Unlabeled Mixed Checkpoints` to `0037` and `Full 45-Min Big Tech Mock Rehearsals` to `0038`, replacing duplicate *Word Break* and *Merge k Sorted Lists* with unseen capstones *Task Scheduler* and *Longest Increasing Path in a Matrix*.
3. **Upgraded Existing Lessons In-Place:**
   - `0002`: Added *Product of Array Except Self* and *Encode and Decode Strings*.
   - `0013`: Added Quickselect `O(n)` average / `O(1)` space interview follow-up callout linking Session 35.
   - `0021`: Added trilingual Bellman-Ford templates alongside Dijkstra.
   - `0025`: Added *Burst Balloons* (Interval DP) and *Regular Expression Matching*.
   - `0027`: Added full trilingual Segment Tree (`build`, `update`, `query`) templates alongside Fenwick Tree.
4. **Synchronized References:** Updated `reference/roadmap.html`, `reference/masterlist.html`, `reference/patterns.html`, and `NOTES.md` to cover all 38 sessions (Sessions 0–37).
5. **Execution Bug Fixes & `/teach` Quiz Hardening:**
   - Fixed `assets/practice.js` so empty linked-list/tree returns (`None`) compare cleanly when `expected == []` (`0009`, `0012`).
   - Fixed expected assertion arrays in `0030` (`testBucket()` -> `[true, true, true]`) and `0031` (`testQueue()` -> `[2, 1, 2]`).
   - Replaced infinite `while ...: pass` loops in 6 starter stubs (`0003`, `0008`, `0009`, `0011`, `0012`, `0021`) with `# TODO` comments and safe fallback returns.
   - Balanced quiz answer choice lengths (eliminating length/formatting clues) across `0001`–`0011` and `0030` Q2, and fixed minor text typos (`0002`, `0007`).
6. **`roadmap.sh/datastructures-and-algorithms` Alignment Pass:**
   - `0001`: Added `Big-O`, `Θ`, and `Ω` asymptotic notation + runtime ladder callout (including `Ω(n log n)` comparison sorting lower bound).
   - `0012`: Added conceptual literacy callout on self-balancing BSTs (`AVL` vs `Red-Black`), database/storage `B/B+ Trees`, and `Skip Lists`.
   - `0034`: Added Rabin-Karp polynomial rolling hash overview alongside KMP.
   - `0036`: Added Sorting Algorithms & Non-Comparison Sorting (Insertion, Merge, 3-way Quicksort, Heapsort, Counting/Bucket/Radix) tradeoffs table.

## Consequences
The curriculum now achieves 18/18 coverage of `roadmap.sh/leetcode` plus practical `roadmap.sh/datastructures-and-algorithms` fundamentals and 2027 Big Tech systems/concurrency patterns, with verified in-browser tests, balanced quiz choices, and zero duplicate final mock problems.
