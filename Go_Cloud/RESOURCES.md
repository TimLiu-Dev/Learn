# Senior Go with Cloud Resources

## Knowledge

### Go Internals, Performance & Architecture
- [The Go Programming Language Specification](https://go.dev/ref/spec)
  The definitive source of truth for language grammar, type semantics, channel behavior, and evaluation order. Use for: resolving ambiguities in syntax, type assertions, and semantics.
- [The Go Memory Model](https://go.dev/ref/mem)
  Official specification of happens-before relationships, synchronization guarantees, atomics, and data race semantics. Use for: verifying correctness of lock-free and concurrent algorithms.
- [Ardan Labs: Ultimate Go Series — Bill Kennedy](https://www.ardanlabs.com/blog/)
  In-depth architectural breakdowns of escape analysis, mechanical sympathy, garbage collector pacing, and value vs. pointer semantics. Use for: deep mechanical understanding and low-allocation design.
- [100 Go Mistakes and How to Avoid Them — Teiva Harsanyi (Manning)](https://www.manning.com/books/100-go-mistakes-and-how-to-avoid-them)
  Comprehensive catalog of subtle production bugs, concurrency hazards, memory leaks, and performance anti-patterns. Use for: defensive coding and code reviews.
- [Go Diagnostics & Profiling Guide](https://go.dev/doc/diagnostics)
  Official documentation on `pprof`, execution tracer (`go tool trace`), race detector (`-race`), and memory allocation profiling. Use for: performance bottleneck diagnosis and tuning.

### Cloud-Native Systems & Distributed Architecture
- [Cloud Native Go: Building Reliable Services in Unreliable Environments — Matthew Titmus (O'Reilly)](https://www.oreilly.com/library/view/cloud-native-go/9781492076322/)
  Pragmatic blueprint for fault-tolerance, circuit breakers, rate limiting, and observability in Go services. Use for: resilient service orchestration.
- [Designing Data-Intensive Applications — Martin Kleppmann (O'Reilly)](https://dataintensive.net/)
  The gold standard for distributed consensus, replication, partitioning, and transactional isolation. Use for: grounding distributed systems design in Go.
- [OpenTelemetry Go SDK Documentation](https://opentelemetry.io/docs/languages/go/)
  Standard guidelines for instrumenting traces, baggage propagation, and metrics with OpenTelemetry. Use for: distributed observability.
- [Kubernetes Best Practices: Up and Running — Brendan Burns et al.](https://www.oreilly.com/library/view/kubernetes-best-practices/9781492056461/)
  Foundational rules for container resource management, pod lifecycles, graceful shutdowns, and health checks. Use for: deploying Go binaries in Kubernetes.

## Wisdom (Communities)

- [Gophers Slack](https://gophers.slack.com/)
  The global community of professional Go developers; channels like `#performance`, `#internals`, and `#cloud` feature Go compiler contributors and cloud architects. Use for: peer review on complex architecture and concurrency dilemmas.
- [r/golang on Reddit](https://www.reddit.com/r/golang)
  Active community discussing releases, production war stories, open-source libraries, and job market trends. Use for: staying attuned to industry tooling shifts.
- [Go Forum](https://forum.golangbridge.org/)
  Constructive discussion board for nuanced language and library architecture queries. Use for: deep conceptual questions.
- [CNCF Slack](https://cloud-native.slack.com/)
  The hub of cloud-native engineering (#kubernetes-dev, #opentelemetry, #envoy). Use for: production cloud deployments and ecosystem updates.

## Gaps
- Interactive in-browser micro-benchmarking with real-time hardware counter visualization (addressed via our custom interactive simulation suite in `./assets/lesson-engine.js`).
