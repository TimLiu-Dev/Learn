# Mission: MVI Architecture for Android

## Why
Tim is preparing for Android job interviews where MVI (Model-View-Intent) architecture knowledge is expected. He already knows Kotlin and has shipped MVVM apps, so the goal is to convert that existing architectural fluency into MVI fluency he can both explain under interview pressure and demonstrate with working code.

## Success looks like
- Can explain MVI (unidirectional data flow, single state holder, Intent/Action → Reducer → State) clearly, and articulate how it differs from MVVM without hand-waving.
- Can implement a small Jetpack Compose screen using an MVI pattern from scratch: sealed `Intent`, immutable `State`, a single state-holder exposing `StateFlow`.
- Can speak to trade-offs: when MVI earns its complexity over MVVM, how testing benefits from a single reducer, and what popular libraries (Orbit, Mavericks, MVIKotlin) add on top of the plain pattern.

## Constraints
- Prior knowledge: comfortable Kotlin, has built and shipped MVVM Android apps — lessons should teach MVI *by contrast* to MVVM rather than from zero.
- Practice stack: Jetpack Compose.
- No specific MVI library mandated — focus on the underlying pattern first (most interview questions probe the pattern, not a specific library's API), with library surveys layered in once fundamentals are solid.
- No stated deadline for the interview — pacing one tightly-scoped concept per lesson, interleaving explanation with small hands-on retrieval.

## Out of scope
- Deep API-level mastery of a specific third-party library (Orbit/Mavericks/MVIKotlin) — covered at survey depth only, unless Tim names a target library later (e.g. a specific job posting requires one).
- Non-Android MVI (e.g. Redux/React-style MVI) except as a brief contrast if it clarifies the Android pattern.
