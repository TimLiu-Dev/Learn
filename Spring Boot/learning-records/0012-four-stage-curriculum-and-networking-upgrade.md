# Learning Record 0012: 4-Stage Progressive Curriculum, Networking Essentials & 2027 Production Readiness

**Timestamp:** 2026-11-21  
**Goal:** Restructure the 77-lesson curriculum into a realistic 4-stage progressive learning path for a Junior Android Kotlin Developer targeting 2027 Senior Spring Boot Engineer roles—with an early Mid-Level project milestone and "just-enough" networking/cloud/telemetry breadth without SRE bloat.

---

### 1. 4-Stage Curriculum Structure (77 Lessons Total: 64 Core + 13 Optional Shelf)
- **Stage 1 — Mid-Level Java & Kotlin Spring Boot (23 Lessons • Phases 0–5) → Project 1:**
  - `0001, 0002, 0071` | `0072, 0003, 0004, 0005, 0065` | `0006–0010` | `0011–0014` | `0015, 0016, 0073, 0017` | `0038, 0039`
  - **Milestone (`reference/mid-level-capstone-spec.html`):** Single-service Order & Catalog REST API with PostgreSQL, Flyway, JPA Specifications, ProblemDetails, and Testcontainers.
- **Stage 2 — Senior Java & Kotlin Spring Boot (27 Lessons • Phases 6–13) → Project 2:**
  - `0059, 0060, 0061` | `0018–0022` | `0023` | `0024–0027` | `0028–0031, 0062` | `0032–0035` | `0036, 0037, 0074` | `0077, 0049`
  - **Milestone (`reference/capstone-project-spec.html`):** NexusCommerce Distributed Event-Driven Order & Ledger Engine.
- **Stage 3 — Production Engineering ("You Build It, You Run It" — 14 Lessons • Phases 14–17):**
  - **Phase 14 (Networking Essentials):** `0075` (DNS/JVM cache TTL, TCP Keep-Alive pools, timeouts, TLS 1.3 & Spring SSL Bundles, L4 vs L7 load balancers, `curl -v`/`dig`/`nc`).
  - **Phase 15 (Telemetry & Performance Verification):** `0041, 0042, 0050, 0052, 0076` (Actuator, OTel, structured JSON logs & cardinality traps, Boot 4 `@Observed`, `k6` load testing & `StreamingResponseBody`).
  - **Phase 16 (CI/CD, Containers, Zero-Downtime & K8s Literacy):** `0040, 0043, 0044, 0045, 0046, 0054` (GitHub Actions CI, Distroless Docker, JVM container memory/ZGC, K8s Pods/Deployments/Services, graceful shutdown/`preStop`, Expand-and-Contract Flyway migrations).
  - **Phase 17 (Developer-Facing AWS):** `0047, 0048` (S3 Presigned URLs, RDS/Secrets Manager/LocalStack).
- **Stage 4 — Optional Deep-Dive Shelf (13 Lessons • Phases 18–20, collapsible in `index.html`):**
  - `0051, 0053, 0055, 0056, 0057, 0058` (SRE PromQL burn-rates, K8s CFS throttling/HPA/PDB, Helm/Cosign/SBOM, AWS VPC/IAM/SQS, Terraform HCL)
  - `0063, 0064` (gRPC/GraphQL, Postgres FTS/pgvector vs Elasticsearch & CQRS)
  - `0066–0070` (GraalVM Native vs AppCDS, Spring AI, Spring Data JDBC/jOOQ, Spring Cloud Gateway, Spring Batch)
  - **Optional Capstone (`reference/pro-cloud-capstone-spec.html`):** NexusCommerce Production Platform.

### 2. New Lessons Built
- `0075-backend-networking-essentials-dns-tcp-tls-and-timeouts.html`
- `0076-load-testing-k6-and-streaming-responses.html`
- `0077-spring-boot-4-framework-7-and-java-25-practice.html`
- `reference/mid-level-capstone-spec.html`
