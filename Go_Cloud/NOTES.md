# Learner Preferences & Workspace Notes

## Learner Profile & Objectives
- **Target Role**: Senior / Staff Go Cloud Engineer or Distributed Systems Backend Engineer (2026/2027 Hiring Standard).
- **Background**: Experienced Senior Software Developer transitioning to Go and Cloud-native architectures.
- **Pacing**: Rapid, dense, high mechanical sympathy; skip junior introductory explanations (e.g., "what is a loop").
- **Key Pedagogical Priorities**:
  - Deep mechanics: Memory layout, stack/heap escape analysis, Go scheduler (GMP), GC tricolor concurrent sweep, zero-allocation patterns, CSP concurrency, atomics.
  - 2027 Modern Additions:
    - Profile-Guided Optimization (PGO) in CI/CD pipelines.
    - Go 1.22–1.24+ runtime innovations: `iter.Seq` range-over-func, Swiss Tables map architecture, `weak.Pointer` for zero-leak caches, `testing/synctest` for deterministic virtual-time concurrency testing.
    - Kubernetes production tuning: `go.uber.org/automaxprocs` to prevent CFS quota throttling, `GOMEMLIMIT` to eliminate Exit Code 137 OOMKills.
    - Cloud Identity: SPIFFE/SPIRE & Workload Identity Federation replacing static secrets.
    - Continuous profiling with eBPF (Pyroscope/Parca).
    - Streaming & LLM Gateways: Server-Sent Events (SSE) backpressure and chunked transfer.
  - Interactive verification: In-browser code runner & automated checks, progressive multi-level hints, final solution code with diff, and multiple-choice quizzes with comprehensive rationales for BOTH correct and incorrect answers.
  - Resume & Portfolio: End-to-end Capstone project ("CloudScale Distributed Financial Ledger & Event Stream") with production architecture, CI/CD, benchmarks, and deployment manifests.

## Architectural Conventions for Lessons
- Reusable components stored strictly in `./assets/` (`style.css`, `lesson-engine.js`).
- Lessons stored in `./lessons/0001-<name>.html` with sequential numbering and bidirectional navigation (`← Previous` and `Next →`).
- Master plan and curriculum directory in `index.html` and `master-plan.html`.
- Compressed reference sheets in `./reference/`.
- Quizzes adhere to the teach-skill rule: answer choices have balanced length/word-count to prevent unintentional formatting bias.
- Detailed rationale explanations provided for all options (A, B, C, D) detailing why correct and why wrong.
- Primary source citations included in every lesson.
- Capstone Project housed in `./capstone/` with interactive portal at `capstone/index.html` and runnable Go codebase, tests, benchmarks, Dockerfile, and K8s manifests.
