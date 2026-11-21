# Teaching Notes

## User preferences (stated in the first session, 2026-10-18)
- Teach one pattern per session: explain the idea, walk through one example, then the user solves 2–3 problems alone.
- Primary target language updated to trilingual (Kotlin, Python, Java) in 2026-11-07; confirm user's preferred primary interview language at Session 0.
- Give hints step by step (nudge → direction → key insight → pseudocode). Give the full solution only if the user explicitly asks.
- After every solution, the user states its time and space complexity.
- Review each solution for correctness, edge cases, and cleaner or more idiomatic approaches.
- The user explains the approach out loud (in chat) before coding, as in a real interview.
- Bring back problems from earlier patterns (spaced review).
- Toward the end: timed mock interviews, with feedback on both the solution and the communication.

## Updated preferences (2026-11-07)
- Target: End-to-end Big Tech coding interview mastery for 2027.
- Language scope expanded to trilingual: Kotlin, Python, and Java comprehensively across lessons, code examples, quizzes, and exercises.
- Comprehensive Masterlist created at `reference/masterlist.html` serving as a full legend for all lessons and patterns.
- Curriculum expanded to include 2027 Big Tech essentials: Data Structure Design (LRU/LFU/Rate Limiter), Range Query Trees (Segment/Fenwick Trees), Bitwise Tries, 0-1 BFS, and Concurrency/Thread-Safe DSA in Kotlin and Java.
- All 38 lesson files (Sessions 0 to 37: `lessons/0001` through `lessons/0038`) are fully generated and linked, featuring synchronized language tabs, step-by-step walkthroughs, quizzes with character-balanced choices, and problem hints.
- Updated 2026-11-15 after benchmarking against `roadmap.sh/leetcode` & DSA tracks: added Sessions 31–35 (`0032` Matrix Traversal & In-Place Simulation, `0033` Math, Geometry & Randomized Algorithms, `0034` String Algorithms: Palindromes, KMP & Parsing, `0035` Advanced Graphs II: MST & Eulerian Paths, `0036` Divide & Conquer, Quickselect & Boyer-Moore), moved Checkpoints/Mocks to `0037`–`0038`, added Segment Tree (`0027`), Bellman-Ford (`0021`), Quickselect note (`0013`), Interval/Regex DP (`0025`), Product/Encode-Decode (`0002`), and removed duplicate problems in `0038`.
- Interactive in-browser runnable code practice (`assets/practice.js` powered by Skulpt) allows live code execution, test verification, standard output capture, 3-tier progressive hint reveals, and final solution drawers in Kotlin, Python, and Java.
- Quizzes updated with per-choice explanations (`data-why="..."`), visual indicators, and expandable `<details>` breakdown explaining why the chosen answer is right/wrong and why each distractor is flawed.

## Working notes
- The roadmap lives at `reference/roadmap.html`. On 2026-10-18 the user replied "build all of phase 0 and 1" without commenting on it, so treat it as accepted for now.
- The user hasn't yet answered the questions about timeline, prior level, target companies, or coding environment. Ask again at the start of the first teaching session.
- Lesson file numbers are offset by one from session numbers across all 38 files: `0001` is Session 0, `0002` is Session 1, …, `0038` is Session 37.
- Updated 2026-11-21: Fixed `0026` orphaned `canPartition` DOM block & wrong trace math, fixed `0018`/`0019`/`0021` templates & imports, fixed `0003` YouTube link, repaired spaced-review warm-ups across `0013`–`0036`, escaped unescaped `<` and markdown backticks across all HTMLs, upgraded `assets/practice.js` to unpack `class Solution` & type shims cleanly, activated `assets/trace.js` visualizers, expanded 1-test widgets, and added high-ROI FAANG additions (Vertical Order Traversal, Sliding Window Max, Difference Array/2D Prefix, Find Peak/First-Last Position, Copy Random Pointer, Max Path Sum, BST Iterator, Merge k Sorted Lists, Bitwise Trie & Bitmask DP templates, Randomized Quickselect template, Time-Based KV/Snapshot Array, Web Crawler Multithreaded, Rapid Diagnosis/Bug-Hunt drills, and 2-Medium Meta Mock Sprint).
- Spacing plan: start each session with a warm-up problem from a pattern learned about 1, 3, or 7 sessions earlier. Unlabeled mixed checkpoints follow Sessions 8, 15, and 25 (Checkpoints A, B, C) plus Session 36.
- Environment: `kotlinc` is not installed on this machine (Java 26 is). Ask whether the user wants to solve on leetcode.com, which supports Kotlin, or to install Kotlin locally for running tests.
