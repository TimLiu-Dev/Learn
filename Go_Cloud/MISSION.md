# Mission: Junior Android Developer to Senior Go Cloud Engineer (2027)

## Why
Transition from a Junior Android Developer background (Kotlin/Java, mobile REST clients, Room/SQLite) into a 2027-ready Senior Go Cloud & Distributed Systems Engineer. Build the missing bridge—idiomatic Go semantics, automated testing, `net/http` servers, SQL modeling, and Linux container mechanics—then master Go runtime internals, resilient microservices, Kubernetes production operations, and an interview-defensible distributed capstone.

## Success looks like
- Write idiomatic, race-free, tested Go services (`go test -race`, table-driven tests, `testing/synctest`, `net/http`, `pgx` + `sqlc`) without importing JVM/Android OOP anti-patterns.
- Profile and tune Go services for cloud production using escape analysis, memory layout, `sync.Pool`, Go 1.25+ container-aware `GOMAXPROCS`, `GOMEMLIMIT`, and PGO.
- Build and operate resilient cloud microservices (ConnectRPC/gRPC, SSE streaming, circuit breakers, transactional outbox, OpenTelemetry, eBPF profiling, Kubernetes, and Terraform).
- Complete, test (`go test -race -bench=.`), and defend the **CloudScale Ledger** capstone project for Senior/Staff Go Cloud roles.

## Constraints
- **Bridge first, no fluff**: Leverage Kotlin/Java programming intuition (skip "what is an `if` statement"), while thoroughly teaching Go-specific semantics (value/pointer semantics, slices, error values, CSP concurrency) and Linux/cloud fundamentals Android devs haven't operated.
- **2027 Standards**: Calibrated to Go 1.23–1.27 (`iter.Seq`, Swiss Tables, `weak.Pointer`, `testing/synctest`, native container-aware `GOMAXPROCS`, `new(expr)`, `errors.AsType`) and CNCF open standards.
- **Real verification**: Every lesson pairs an interactive browser lesson (honest static pre-checks + balanced A/B/C/D rationale quizzes) with real runnable Go labs and `_test.go` suites in `./labs/` verified via `go test -race`.
- **Adaptive rollout**: Module 00 (bridge) + Modules 01–02 + Capstone are ready immediately; Modules 03–06 are generated step-by-step based on verified mastery in `./learning-records/`.

## Out of scope
- Generic CS 101 syntax tutorials (basic loops, conditionals, boolean logic).
- Legacy Go web-framework churn (`Beego`, `Buffalo`, `Fiber`, `Echo`), heavy reflection ORMs (`GORM`), `go plugin`, `encoding/xml`, SSR HTML templates, and deep `CGO`.
- Mobile/Android UI topics or proprietary cloud vendor lock-in.
