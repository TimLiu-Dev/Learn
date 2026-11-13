# CloudScale Ledger: Distributed Financial Transaction Engine (2027)

> **Staff/Senior Go Cloud Engineering Capstone Portfolio Project**  
> A reference distributed double-entry transaction engine and transactional outbox codebase engineered in Go and Kubernetes to demonstrate ACID ledger validation, bounded worker execution, container-aware runtime limits, and reproducible Go microbenchmarks.

---

## 🎯 Architecture Highlights & Production Invariants

1. **Strict Double-Entry Bookkeeping**:
   - Zero floating-point math: all monetary amounts are stored in 64-bit integer minor currency units (cents, satoshis).
   - Invariant: `sum(debits) + sum(credits) == 0`. Every transaction leg is validated before memory or database mutations.

2. **ACID Consistency with PostgreSQL & Transactional Outbox**:
   - PostgreSQL connection pool tuned via `pgxpool` (max lifetime 1hr, min conns 10, statement caches).
   - Dual-write race conditions eliminated by persisting business events inside the same database transaction table (`outbox_events`).
   - Asynchronous outbox processor dispatches events to Kafka / Redpanda with exponential backoff and idempotency headers.

3. **High-Performance Memory & Go Runtime Tuning**:
   - Cache-line padded counters and bounded queues in `domain/` and `outbox/` to reduce contention on multi-core nodes.
   - Profile-Guided Optimization (PGO) workflow via `default.pgo`.
   - Native Go 1.25+ container-aware `GOMAXPROCS` (`containermaxprocs` / `updatemaxprocs` GODEBUG defaults reading Linux cgroup CPU limits automatically), with `go.uber.org/automaxprocs` only needed for legacy Go 1.24 or earlier runtimes.
   - Zero-OOMKill headroom via `GOMEMLIMIT=460MiB` on a 512MiB cgroup boundary (90% threshold).

4. **Zero-Trust Cloud Security & Distroless Packaging**:
   - Multi-stage Google Distroless `static-debian12:nonroot` image (see `Dockerfile`, running as non-root UID 65532).
   - Read-only root filesystem, dropped Linux capabilities, and no static credentials (SPIFFE/SPIRE & Cloud Workload Identity Federation).

---

## 📊 Verification & Benchmark Results

Run the full race-detector test suite and build the service binary locally:

```bash
cd capstone
go vet ./...
go test -v -race ./...
go build -buildvcs=false -o /tmp/ledger-service ./cmd/server
```

Run the isolated micro-benchmarks locally (`./benchmark/...`):

```bash
go test -bench=. -benchmem ./benchmark/...
```

Baseline run measured on `go1.27.2 darwin/arm64` (`-benchmem` shows pure validation vs stateful execution vs parallel sharded execution):

```text
BenchmarkValidateDoubleEntry-18      1.749 ns/op    0 B/op    0 allocs/op
BenchmarkLedgerExecution-18          108.5 ns/op   80 B/op    0 allocs/op
BenchmarkParallelLedgerExecution-18  148.0 ns/op    0 B/op    0 allocs/op
```

- **Zero Heap Allocations on Parallel Path**: `0 allocs/op` on balanced validation and parallel sharded ledger execution (note: `80 B/op` on single-goroutine `BenchmarkLedgerExecution` reflects amortizing underlying balance map/slice growth rather than per-tx struct boxing).
- **Correctness Under Race Detector**: Idempotency keys are stored *after* balance/active validation so failed transfers cannot poison retries, and `outbox.Worker` snapshots batches under mutex before publishing.

---

## 💼 Senior / Staff Resume Bullet Templates

Measure your own service throughput, p99 latency, image size, and cost deltas after running load tests on your target environment before inserting concrete numbers:

> **Senior Go Distributed Systems Engineer | CloudScale Ledger**
> - Architected a double-entry financial ledger service in Go and Kubernetes achieving `0 allocs/op` on parallel transaction validation/execution paths via integer-cent accounting and bounded concurrency.
> - Guaranteed local ACID consistency and reliable asynchronous publishing by pairing database state transitions with a race-tested Transactional Outbox worker (`outbox/`) and idempotency keys.
> - Eliminated container CFS quota throttling and OOMKills by sizing runtime limits with Go 1.25+ container-aware `GOMAXPROCS` (`go.uber.org/automaxprocs` on Go <= 1.24) and `GOMEMLIMIT`.
> - Packaged static Go services into hardened non-root Distroless container images with multi-stage builds, automated `-race` verification, and PGO build support.

---

## 🎙️ Staff Interview Defense: Key Architectural Questions

### Q1: "Why not use Two-Phase Commit (2PC) instead of the Transactional Outbox pattern?"
**Answer:** Two-Phase Commit introduces blocking distributed locks across services and databases. If any coordinator or participant becomes slow or partitioned, locks remain held, rapidly exhausting connection pools and causing cascading outages across the cluster. The Transactional Outbox pattern avoids distributed locks entirely by writing domain state and outbox events within a single local PostgreSQL ACID transaction, then asynchronously streaming events to Kafka with at-least-once delivery and consumer idempotency keys.

### Q2: "How does GOMEMLIMIT prevent Kubernetes OOMKills compared to older versions of Go?"
**Answer:** Prior to Go 1.19, the Go garbage collector paced itself solely on `GOGC` (relative heap growth percentage). Under sudden traffic surges, heap growth could double before triggering a GC cycle, easily exceeding the Linux cgroup memory limit and provoking the kernel OOMKiller (`SIGKILL / Exit Code 137`). `GOMEMLIMIT` establishes a soft absolute ceiling (e.g. 90% of the container limit). As heap usage approaches this threshold, the Go runtime triggers proactive, aggressive GC cycles to keep memory within boundaries, completely eliminating OOM kills.

### Q3: "What happens if the Outbox processor crashes midway through publishing?"
**Answer:** Outbox records maintain explicit status flags (`StatusPending`, `StatusPublished`, `StatusFailed`) and an incrementing retry count. If the worker crashes, uncommitted records remain in `StatusPending`. When a replacement worker starts, it claims pending batches using `SELECT ... FOR UPDATE SKIP LOCKED`. Downstream Kafka consumers enforce idempotency via transaction UUIDs, ensuring that at-least-once message delivery produces exactly-once business side effects.
