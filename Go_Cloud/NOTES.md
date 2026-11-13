# Learner Preferences & Workspace Notes

## Learner Profile & Objectives
- **Current Baseline**: Junior Android Developer (familiar with Kotlin/Java, basic OOP, Gradle, Retrofit/JSON clients, Room/SQLite, basic coroutines; new to Go, server-side Linux/cgroups, SQL pooling/migrations, and Kubernetes).
- **Target Role**: Senior Go Cloud Engineer / Distributed Systems Backend Engineer (2027 Hiring Standard).
- **Roadmap Calibration (`https://roadmap.sh/golang`)**:
  - **Added**: Module 00 bridge (Go toolchain & modules, Kotlin→Go mental model, slices/maps/structs/pointers, errors as values & generics, table-driven testing/`-race`/`testing/synctest`, Go 1.22+ `net/http` routing & graceful shutdown, Linux/cgroups + `sqlc`/migrations + Redis basics), plus 2025–2027 Go features (`weak.Pointer`, native container `GOMAXPROCS`, `new(expr)`, `errors.AsType`, generic methods).
  - **Removed / Trimmed**: Overlapping web framework churn (`Beego`/`Buffalo`/`Fiber`/`Echo`), heavy ORMs (`GORM`/`Ent`), deep CLI/TUI frameworks (`Bubble Tea`/`Lip Gloss`), `plugin`, `encoding/xml`, SSR `html/template`, deep `CGO`.
- **Verification Preference**:
  - Browser lessons provide interactive code editors with honest **static semantic pre-checks** (never faking `go test` output) plus balanced A/B/C/D quizzes with all-options rationales.
  - Every live lesson is backed by real runnable `.go` + `_test.go` packages in `./labs/` verified with real `go test -v -race -bench=. -benchmem` on macOS.
- **Rollout Preference**:
  - Module 00 (`0000-1`..`0000-6`), Module 01 (`0001`–`0005`), Module 02 (`0006`–`0010`), Reference sheets, and Capstone ready upfront for Day 1.
  - Modules 03–06 (`0011`–`0026+`) generated dynamically step-by-step as learning progresses per ZPD (`./learning-records/`).

## Architectural Conventions for Lessons & Assets
- Reusable components live strictly in `./assets/` (`style.css`, `lesson-engine.js`).
- Light and Dark themes must both remain high-contrast across editors, terminals, and callouts.
- Quizzes strictly distribute correct keys across A/B/C/D, include `.quiz-option-key` badges, balance option lengths, and provide itemized rationales for every option.
- `GLOSSARY.md` is built incrementally—only add terms once the learner demonstrates mastery of them (per `GLOSSARY-FORMAT.md`).
- Master plan & syllabus live in `index.html`; progress tracker tracks only actual lessons (never non-lesson pages like `capstone` or `ref-01`).
