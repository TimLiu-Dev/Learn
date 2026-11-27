# Workspace Notes & Pedagogical Strategy

## Learner Profile & Curriculum Structure
- **Learner Baseline**: Junior Android Developer targeting Senior Modern Android & KMP readiness (2026/2027 standards).
- **Curriculum Structure**: **76 Sequential Lessons** (`0001`–`0076`, zero numbering jumps):
  - **Core Pathway (`0001`–`0074`, Phases I–IX)**: Kotlin 2.x & Flow -> Compose, `TextFieldState`/i18n & UI/Screenshot Testing -> Navigation 3, MVI, Hilt & Brownfield Interop -> Networking, Room, DataStore, WorkManager & OS Boundaries -> Multi-Module, `build-logic`, Feature Flags & CI/CD -> Profiling, 16 KB, Security/Passkeys, Release & `ApplicationExitInfo` -> Media3/CameraX, Glance/Live Updates, KMP, CMP & Gemini Nano -> 9-Module "Aether" Capstone -> System Design & Career.
  - **Optional Specializations (`0075`–`0076`, Phase X)**: Google Play Billing 7+ Subscriptions (`0075`) and Schema-First APIs with Apollo Kotlin 4 GraphQL & gRPC/Protobuf (`0076`).
- **Desk References (`./reference/`)**: 5 printable HTML cheat sheets (`01` through `05`, where `05-glossary.html` holds the full curriculum dictionary while `GLOSSARY.md` tracks concepts personally mastered by the learner).

## Recommended Study & `/teach` Workflow
1. **Browser Lesson (`lessons/0001-*.html` .. `0076-*.html`)**: Read mental model + anti-pattern comparison, run the simulated snippet, solve the coding challenge *from memory* before revealing hints/answers, and complete both retrieval quizzes.
2. **Real Compiler Verification (`./playground/`)**: Paste or extend solutions inside `./playground/core-practice` and run `./gradlew test` (using Android Studio's JBR 21) so K2, Coroutines `runTest`, and Turbine verify real compilation and behavior.
3. **Stateful `/teach` Check-In**: After each lesson (or 2–3 lesson block), check in via `/teach` for spaced/interleaved recall questions, edge-case code review, promoting mastered terms into `GLOSSARY.md`, and recording decision-grade insights in `learning-records/`.
