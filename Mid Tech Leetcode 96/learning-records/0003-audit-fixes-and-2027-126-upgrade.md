# Learning Record 0003: Full 2027 Audit, Bug Fixes & 126-Lesson Upgrade

- **Date**: 2026-11-26
- **Scope**: End-to-end audit and upgrade of the Mid-Tech LeetCode teaching workspace (`0001`–`0126`, `assets/`, `reference/`, `master-plan.html`, and core docs) for 2027 Mid-Level (`SDE II / L4`) readiness.

## 1. Runtime & Lesson Bug Fixes (`0001`–`0116`)
1. **Practice Runner Strictness (`assets/lesson-runtime.js`)**:
   - Eliminated false-positive passes on empty stubs by restricting the `actual === undefined -> args[0]` fallback strictly to true in-place void methods (`moveZeroes`, `merge`, `rotate`, `solve`, `reorderList`, `reverseString`, `setZeroes`, `sortColors`) or `config.inPlace`.
   - Added `_oid` deep-copy verification on `random_list` (`#0035`) and `graph` (`#0082`) so returning the original head/node fails immediately.
   - Separated outer-only `unordered` (preserving `[r, c]` coordinate and permutation order) from nested `unorderedInner` (`#0023`, `#0048`, `#0060`, `#0078`, `#0121`), and added `accountsMerge` equivalence checking (`#0123`).
   - Replaced `expectedCount`-only test cases on `#0048`, `#0049`, `#0060`, `#0078`, `#0079`, and `#0080` with exact expected output suites.
   - Rebuilt all incomplete OOP and classless starter stubs and prepended `ListNode` / `TreeNode` / `Node` definition comments across all pointer/tree/graph lessons.
2. **Interactive Steppers, Pseudocode & HTML Escaping**:
   - Fixed all 24 blank-on-load visual steppers (`0037`–`0091`) by invoking `buildSteps(...)` with explicit initial preset state on `DOMContentLoaded`.
   - Re-rendered `#0067` as a deterministic 4-Stage CodeSignal ICA (`Entry(value, expiresAt)` + `O(T)` delta UndoMap stack, integer timestamps instead of wall-clock threads, working Stage 2–4 stepper presets).
   - Rebuilt Section 2.5 pseudocode with balanced-parenthesis indentation (eliminating `THENreturn` / `( DO)` tokens), replaced tautological variable descriptions, and escaped all raw `<Generic>` tags (`<Int>`, `<Pair<String, String>>`) across HTML, `<pre>`, hints, and quizzes.
3. **Diagnostic Quizzes (`504 / 504` Questions)**:
   - Enforced exact word-count parity across choices `A–D` on every question, rotated correct answer letters evenly across `A/B/C/D`, added `localStorage` persistence (`lc110_quiz_<pid>`), and added `Reset Quiz Answers`.

## 2. 10 New 2027 Mid-Tech Gap Lessons (`0117`–`0126`)
- **Pass 1 Bridges (`4`)**: `#0117` Valid Anagram (`#242`), `#0118` Meeting Rooms (`#252`), `#0119` Flood Fill (`#733`), `#0120` Kth Largest Element in a Stream (`#703`).
- **Pass 2A / 2B High-Frequency Archetypes (`5`)**: `#0121` Combination Sum II (`#40`), `#0122` Letter Combinations of a Phone Number (`#17`), `#0123` Accounts Merge (`#721`), `#0124` Valid Sudoku (`#36`), `#0125` Top K Frequent Words (`#692`).
- **Companion 4-Stage CodeSignal ICA (`1`)**: `#0126` Cloud Storage System (`CRUD/Copy -> Sorted Prefix/Suffix Search -> User Capacity Quotas & Eviction -> User Backup/Restore Snapshots`).

## 3. Master Plan, Study UX & Reference Suite
- Added `LessonRuntime.StudyNav` (`Pass 1 → 2A → 2B → 3 → Optional` Prev/Next bar + `🎲 Interleave` + `📋 Copy` buttons), Cold Solve Timer (`15m/25m/35m`), `🐛 Debug Broken PR` mode, and `Cold ★` vs `Assisted ✓` mastery badges.
- Created standalone `reference/glossary.html` and `reference/resources.html` (featuring the **2027 CodeSignal ICA, Karat & AI-Era Pair-Programming Playbook**), and updated `master-plan.html` and `reference/pattern-reference.html` for all `126` lessons (`119 Core + 7 Optional`).
