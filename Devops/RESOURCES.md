# DevOps Resources

## Knowledge

### Foundations, Shell, Git & Networking
- [Manual: _GNU Bash Reference Manual_ — Quoting, Pipelines, and `set` Builtin](https://www.gnu.org/software/bash/manual/bash.html)
  Primary reference for shell word splitting, quoting rules, pipelines, and `set -euo pipefail`. Use for: Lesson 0001 and CI shell scripting.
- [Manual: `chmod(1)` & `signal(7)` — Linux Manual Pages (`man7.org`)](https://www.man7.org/linux/man-pages/man7/signal.7.html)
  Authoritative specification for POSIX file permissions (`chmod(1)`), `cgroups`, and process signals (`SIGTERM`, `SIGKILL`, `SIGHUP`). Use for: Lessons 0001, 0011, and 0012.
- [Docs: Git Reference Documentation (`git-scm.com`)](https://git-scm.com/docs)
  Official Git manual covering branching, rebasing, `git bisect`, `git worktree`, hooks, and tagging. Use for: Lesson 0002.
- [Spec: Conventional Commits & Semantic Versioning (`semver.org`)](https://www.conventionalcommits.org/)
  Primary specifications for automated changelog generation and `MAJOR.MINOR.PATCH` release tags. Use for: Lesson 0002.
- [Manual: `jq` Official Manual (`jqlang.org`)](https://jqlang.org/manual/)
  Primary reference for filtering and transforming structured JSON logs and cloud CLI outputs. Use for: Lessons 0001, 0009, 0014, 0019, and 0021.

### Containers, CI/CD, Mobile & App Delivery
- [Docs: Docker Multi-Stage Builds & Compose Spec (`docs.docker.com`)](https://docs.docker.com/build/building/multi-stage/)
  Official guides for lean multi-stage Dockerfiles, `.dockerignore`, BuildKit cache mounts, non-root containers, and Compose service definitions. Use for: Lessons 0004–0005.
- [Docs: GitHub Actions Security Hardening & OIDC (`docs.github.com`)](https://docs.github.com/en/actions/security-guides/security-hardening-your-deployments)
  Official reference for workflow triggers, caching, reusable `workflow_call` templates, least-privilege `GITHUB_TOKEN`, and keyless OIDC cloud federation. Use for: Lessons 0006–0008, 0013, 0022, and 0025.
- [Docs: Android App Signing & Release Publishing (`developer.android.com`)](https://developer.android.com/studio/publish/app-signing)
  Official guide to upload keys, Play App Signing, R8 mapping files, and release artifact security. Use for: Lesson 0007 and Capstone 1 (0011).
- [Docs: Spring Boot Graceful Shutdown (`docs.spring.io`)](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html)
  Official Spring Boot documentation on `server.shutdown=graceful`, `SIGTERM` request draining, and Actuator probes. Use for: Lessons 0001, 0008, 0015, and Capstone 1.
- [Docs: Testcontainers for Java (`java.testcontainers.org`)](https://java.testcontainers.org/)
  Primary reference for running real PostgreSQL/Redis containers in JUnit 5 CI pipelines. Use for: Lesson 0008 and Capstone 1.
- [Docs: Caddy Server (`caddyserver.com/docs`)](https://caddyserver.com/docs/)
  Official reverse proxy and automatic HTTPS/TLS documentation. Use for: Lesson 0010 and Capstone 1.

### Automation SDKs, Immutable Runners, IaC & Cloud Security
- [Docs: Python `boto3` & Kubernetes Python Client (`boto3.amazonaws.com` / `github.com/kubernetes-client/python`)](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
  Primary SDK documentation for replacing fragile Bash glue with typed, retry-safe cloud and Kubernetes automation. Use for: Lesson 0026.
- [Docs: HashiCorp Packer & Ansible Documentation (`developer.hashicorp.com/packer` / `docs.ansible.com`)](https://developer.hashicorp.com/packer/docs)
  Official guides for building immutable golden VM/runner images and idempotent OS configuration playbooks. Use for: Lesson 0027.
- [Docs: HashiCorp Terraform & OpenTofu (`developer.hashicorp.com/terraform` / `opentofu.org`)](https://developer.hashicorp.com/terraform/tutorials/kubernetes)
  Primary IaC reference for modular infrastructure, remote state locking, `terraform test`, `moved {}` refactoring, and provisioning managed Kubernetes. Use for: Lessons 0013–0014 and Capstone 2A.
- [Docs: AWS EKS Pod Identity & IAM Roles for Service Accounts (`docs.aws.amazon.com`)](https://docs.aws.amazon.com/eks/latest/userguide/pod-identities.html)
  Official reference for modern zero-static-secret workload identity in EKS clusters. Use for: Lessons 0014 and 0024.

### Kubernetes, GitOps, Progressive Delivery & Service Mesh
- [Docs: Kubernetes Workloads, Gateway API & Network Policies (`kubernetes.io`)](https://kubernetes.io/docs/concepts/services-networking/gateway/)
  Official Kubernetes networking, scheduling, probes, storage, RBAC, and `kubectl debug` documentation. Use for: Lessons 0015–0016, 0023, and 0029.
- [Docs: Helm, KEDA & cert-manager (`helm.sh/docs` / `keda.sh/docs` / `cert-manager.io/docs`)](https://helm.sh/docs/)
  Primary docs for authoring Helm charts, event-driven autoscaling (`ScaledObject`), and automated cluster TLS certificates. Use for: Lessons 0016–0017.
- [Docs: Argo CD & Argo Rollouts (`argo-cd.readthedocs.io` / `argo-rollouts.readthedocs.io`)](https://argo-cd.readthedocs.io)
  Primary docs for GitOps pull reconciliation, `ApplicationSet`, and progressive canary/blue-green rollouts with `AnalysisTemplate`. Use for: Lessons 0017–0018 and Capstone 2A.
- [Docs: Cilium eBPF Networking & Hubble (`docs.cilium.io`)](https://docs.cilium.io/)
  Authoritative reference for eBPF CNI, L7 NetworkPolicies, kube-proxy replacement, and Hubble flow visibility. Use for: Lesson 0018.

### Observability, SRE, Load Testing & Stateful Resilience
- [Docs: Prometheus & OpenTelemetry (`prometheus.io` / `opentelemetry.io`)](https://opentelemetry.io/)
  Primary documentation for metrics scraping, recording rules, PromQL histograms, logs, traces, and OTel Collector pipelines. Use for: Lessons 0009, 0019, and Capstones.
- [Book: _Site Reliability Engineering_ & _Workbook_ — Google SRE (`sre.google`)](https://sre.google/books/)
  Foundational primary texts on SLIs, SLOs, error budgets, multi-window burn-rate alerting, and blameless postmortems. Use for: Lesson 0020 and Capstone 2A.
- [Docs: Grafana k6 Load Testing & PgBouncer (`k6.io/docs` / `pgbouncer.org`)](https://grafana.com/docs/k6/latest/)
  Official references for code-driven load/soak/spike testing and Postgres connection pooling under autoscaling. Use for: Lesson 0028.

### DevSecOps, Platform Engineering, FinOps & AI-Infra
- [Docs: Sigstore Cosign, Trivy, Kyverno & External Secrets (`docs.sigstore.dev` / `trivy.dev` / `kyverno.io` / `external-secrets.io`)](https://docs.sigstore.dev)
  Primary references for image signing, SBOM/CVE gating, SLSA provenance, admission policy-as-code, and Vault/ESO secret synchronization. Use for: Lesson 0021 and Capstone 2B.
- [Docs: Karpenter, FinOps Foundation & DORA (`karpenter.sh` / `finops.org` / `dora.dev`)](https://karpenter.sh/docs/)
  Primary references for Kubernetes node consolidation, cloud cost attribution, and evidence-based delivery performance metrics. Use for: Lesson 0022 and Capstone 2B.
- [Docs: Kubernetes `kind` Local Clusters & LocalStack (`kind.sigs.k8s.io`)](https://kind.sigs.k8s.io/docs/user/quick-start/)
  Official guide for running multi-node Kubernetes clusters locally for real terminal practice and Capstone drills. Use for: Lesson 0029 and all Capstones.

## Wisdom (Communities)

- [roadmap.sh DevOps Roadmap & Community](https://roadmap.sh/devops)
  Community-curated DevOps skill tree and peer discussion for benchmarking breadth against industry expectations.
- [CNCF Slack (`slack.cncf.io`)](https://slack.cncf.io/)
  Real-world practitioner channels for Kubernetes, Argo, OpenTelemetry, Cilium, Kyverno, and Karpenter maintainers and users.
- [SRE Weekly Newsletter (`sreweekly.com`)](https://sreweekly.com/)
  Curated weekly real-world outage postmortems and reliability engineering writeups for developing incident judgment.
- [FinOps Foundation Community (`finops.org`)](https://www.finops.org/community/)
  Practitioner community for cloud cost engineering, unit economics, and Kubernetes showback/chargeback patterns.
