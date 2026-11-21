# DevOps Glossary

Canonical vocabulary for this workspace. Every lesson adheres to these exact definitions.

## Core Foundations & Release Engineering
- **Exit Code (`$?`)**: Integer (`0`–`255`) returned by a Linux process on termination; `0` means success and non-zero means failure. CI runners and container runtimes gate builds strictly on exit codes.
- **`SIGTERM` (`15`) vs `SIGKILL` (`9`)**: `SIGTERM` is a catchable termination signal allowing graceful HTTP request draining and connection cleanup; `SIGKILL` terminates immediately at the kernel level without cleanup.
- **Conventional Commits**: Structured commit message convention (`type(scope): description`, with `!` or `BREAKING CHANGE:` for breaking updates) used to automate Semantic Versioning bumps and changelogs.
- **Semantic Versioning (SemVer)**: `MAJOR.MINOR.PATCH` version scheme communicating breaking changes (`MAJOR`), backward-compatible features (`MINOR`), and bug fixes (`PATCH`).
- **Multi-Stage Dockerfile**: Dockerfile using separate builder and minimal runtime stages so compilers, SDKs, and build caches never ship into the production image.
- **Immutable Tag / Digest**: Tagging container images by commit SHA (`:sha-<commit>`) and deploying by cryptographic `@sha256:...` digest rather than mutable tags like `:latest`.

## CI/CD, Cloud & Infrastructure as Code
- **Keyless OIDC Federation**: Authentication pattern where CI runners (e.g., GitHub Actions) exchange a short-lived signed JWT (`id-token: write`) for temporary cloud or signing credentials, eliminating long-lived static secrets.
- **Infrastructure as Code (IaC)**: Managing cloud networks, IAM, clusters, and databases declaratively in versioned code (Terraform / OpenTofu) rather than manual console ClickOps.
- **Remote State & Locking**: Storing IaC `.tfstate` encrypted in object storage (e.g., S3) with concurrency locking so teams share state safely without committing secrets to Git.
- **Golden Image (Packer)**: Pre-baked, CIS-hardened immutable VM or CI runner image built from code so instances boot quickly and consistently without configuration drift.
- **Idempotency (Ansible / IaC)**: Property ensuring running automation repeatedly produces the exact same target system state without duplicate side effects.

## Kubernetes, GitOps & Observability
- **`cgroups v2` & Namespaces**: Linux kernel primitives underlying containers; namespaces isolate visibility (PID, network, mount) while `cgroups` enforce CPU/memory resource limits.
- **QoS Class (`Guaranteed`, `Burstable`, `BestEffort`)**: Kubernetes Pod scheduling/eviction priority derived from `requests` and `limits`; `Guaranteed` (`requests == limits`) protects critical JVM workloads last during node memory pressure.
- **Gateway API (`Gateway` / `HTTPRoute`)**: Modern Kubernetes L7 traffic routing standard replacing legacy `Ingress` annotations for TLS, header matching, and weighted canary splits.
- **GitOps (Pull Reconciliation)**: Continuous delivery model where an in-cluster controller (Argo CD) continuously reconciles cluster state against declarative manifests stored in Git.
- **Progressive Delivery (Canary / Blue-Green)**: Releasing new versions gradually (`10% -> 50% -> 100%`) or via preview slots, automatically rolling back when live Prometheus SLI gates fail.
- **High Cardinality**: Metric label anti-pattern caused by unbounded values (`user_id`, `order_id`, raw paths with IDs) that explodes Prometheus time-series memory; keep unbounded IDs in logs and traces.

## SRE, DevSecOps, Platform & Resilience
- **SLI / SLO / Error Budget**: An SLI measures user-facing reliability (e.g., good requests / valid requests); an SLO sets the reliability target (e.g., `99.9%`); the Error Budget (`1 - SLO`) quantifies allowable unreliability before freezing risky releases.
- **Multi-Window Burn-Rate Alert**: Alerting strategy checking both a long and short window (e.g., `1h` and `5m` at `14.4x` burn) to page fast on real incidents while ignoring brief blips.
- **SBOM (Software Bill of Materials)**: Machine-readable dependency inventory (CycloneDX/SPDX via Syft) enabling instant CVE impact audits across built artifacts.
- **Admission Control (`verifyImages`)**: Cluster gate (Kyverno/OPA) rejecting unsigned, root-privileged, or policy-violating Pods before they schedule.
- **Internal Developer Platform (IDP) & Golden Path**: Self-service reusable workflows, ephemeral PR environments, and paved templates that make the secure, observable path the easiest path for product teams.
- **RPO vs RTO**: **RPO** (Recovery Point Objective) is max acceptable data loss measured backward in time; **RTO** (Recovery Time Objective) is max acceptable downtime to restore service.
- **Expand-Contract Migration**: Zero-downtime database schema evolution pattern (add nullable column -> dual-write -> backfill -> drop old column) so old and new application replicas run safely together.
