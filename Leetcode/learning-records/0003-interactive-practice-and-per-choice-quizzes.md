# 0003. Interactive In-Browser Code Practice and Per-Choice Retrieval Quizzes

- **Date:** 2026-11-07
- **Status:** Accepted

## Context
Learners require interactive practice directly inside the lesson HTML files with immediate feedback, test case validation, progressive hint ladders, and verified trilingual solutions, as well as multiple-choice quizzes that explain why the correct choice is optimal and why each distractor is wrong.

## Decisions
1. **In-Browser Python Execution (`assets/practice.js`):** Embedded Skulpt (`skulpt.min.js` and `skulpt-stdlib.js`) to run test assertions client-side without requiring a backend server. Included standard output capture and error formatting.
2. **Tab Indentation & Reset:** Configured textarea code editors to intercept the `Tab` key (4-space indentation / dedentation) and provided a boilerplate reset button.
3. **Progressive Hint Ladder:** Structured 3-tier progressive hints (`data-hints`) unlocked on demand (Intuition -> Invariant -> Algorithm).
4. **Trilingual Solution Drawer:** Provided collapsible verified solutions in Kotlin, Python, and Java with full Big-O complexity analysis and interview commentary.
5. **Per-Choice Quiz Breakdown (`assets/quiz.js`):** Added `data-why="..."` to every multiple-choice option, offering instant color-coded feedback and a `<details>` breakdown explaining why the chosen answer is right/wrong and why each distractor is flawed.
6. **Curriculum Recompilation:** Upgraded all 33 lessons (Sessions 0 to 32) using the enhanced HTML rendering pipeline.

## Consequences
Every lesson across the 33-session curriculum is now an interactive coding workshop with live browser execution, progressive hints, trilingual answers, and deep retrieval quizzing.
