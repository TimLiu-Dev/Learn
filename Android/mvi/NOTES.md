# Notes

- Mission: interview prep for MVI, not a live project migration. No stated
  interview date — pacing one tightly-scoped concept per lesson until Tim
  says otherwise.
- Prior experience: shipped MVVM apps, comfortable Kotlin. Skip basics;
  teach MVI by contrast with MVVM throughout (see learning-records/0001).
- Practice stack: Jetpack Compose. No specific MVI library mandated yet —
  cover the plain pattern first, survey libraries (Orbit, Mavericks,
  MVIKotlin) later at a lighter depth unless a target job posting names one.
- GLOSSARY.md intentionally not created yet — per the format rules, terms
  should only be promoted to the glossary once Tim demonstrates he can use
  them correctly (not just after first exposure in Lesson 1). Revisit after
  he engages with Lesson 1's quiz/exercises.
- Course roadmap (revised 2026-10-18; Tim asked "do you have a lesson plan
  for me?"):
  1. ✓ What is MVI, by contrast with MVVM
  2. ✓ The reducer: Result type, pure reduce(), JUnit tests
  3. ✓ One-off events: state+acknowledge Intent vs. effect Channel
  4. ✓ Reducers with memory: (State, Result) -> State, update{} purity,
     plus racing loads (cancel Job) and process death (SavedStateHandle)
  5. ✓ Build a login screen from scratch (TDD reducer tests, double-tap guard)
  6. ✓ Library survey (Orbit 12 / Mavericks 3.1 / MVIKotlin 4.4) + overkill
  7. ✓ Mock interview: shuffled spoken-answer drill (assets/drill.js)
  All of 4–7 were written in one go on 2026-10-18 at Tim's request ("dont
  need to wait for me to reply"), so they were NOT adapted to his quiz
  results. Next session: ask which drill cards were shaky/missed and the
  quiz scores, write learning records from that evidence, then write
  targeted follow-ups (candidates: ViewModel testing with fakes/runTest,
  Orbit rewrite of the login screen, a live mock interview with pushback).
- No evidence yet on how Tim did with any quiz after Lesson 1. Ask
  next session before writing any learning record.
- Watch for: Tim pushes back when a claim feels like a difference-without-a-
  distinction rather than a concrete mechanism. Lead with the concrete
  failure case (a bug that actually ships) before the abstract principle.
