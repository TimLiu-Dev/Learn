# Teaching Notes

## User preferences (stated in the first session, 2026-10-18)
- Teach one pattern per session: explain the idea, walk through one example, then the user solves 2–3 problems alone.
- All solutions in Kotlin.
- Give hints step by step (nudge → direction → key insight → pseudocode). Give the full solution only if the user explicitly asks.
- After every solution, the user states its time and space complexity.
- Review each solution for correctness, edge cases, and cleaner or more idiomatic approaches.
- The user explains the approach out loud (in chat) before coding, as in a real interview.
- Bring back problems from earlier patterns (spaced review).
- Toward the end: timed mock interviews, with feedback on both the solution and the communication.

## Working notes
- The roadmap lives at `reference/roadmap.html`. On 2026-10-18 the user replied "build all of phase 0 and 1" without commenting on it, so treat it as accepted for now.
- The user hasn't yet answered the questions about timeline, prior level, target companies, or coding environment. Ask again at the start of the first teaching session.
- Lesson file numbers are offset by one from session numbers: `0001` is Session 0, `0002` is Session 1, …, `0009` is Session 8.
- Fixed warm-ups for Sessions 2–8, which can be swapped based on performance:
  - Session 2: Group Anagrams
  - Session 3: 3Sum
  - Session 4: Top K Frequent Elements
  - Session 5: Longest Substring Without Repeating Characters
  - Session 6: Subarray Sum Equals K
  - Session 7: Container With Most Water
  - Session 8: Koko Eating Bananas
- Assets:
  - `style.css`: tokens, light/dark themes, and print styles.
  - `quiz.js`: `.quiz > .q[data-answer]`.
  - `hints.js`: `.hints > ol > li`, revealed one at a time.
  - `trace.js`: `figure.trace[data-trace]`, an array/pointer stepper.
  - Code highlighting uses highlight.js 11.9.0 plus the Kotlin language file from cdnjs.
- `reference/patterns.html` is assembled from one fragment per lesson. When adding a pattern, add a `<section class="pattern" id=…>` for it and keep the same format.
- No GLOSSARY.md yet. Terms get added only once the user shows they understand them.
- Spacing plan: start each session with a warm-up problem from a pattern learned about 1, 3, or 7 sessions earlier. Add unlabeled mixed checkpoints after sessions 8, 15, and 19.
- Environment: `kotlinc` is not installed on this machine (Java 26 is). Ask whether the user wants to solve on leetcode.com, which supports Kotlin, or to install Kotlin locally for running tests.
