# Learning Record 0008: Two-Part 80/20 Curriculum Split & Part 2 Pro Expansion

**Timestamp:** 2026-11-12  
**Goal:** Optimize time-to-seniority by splitting the curriculum into a high-ROI Core Senior Backend Track (Part 1) and an optional Professional Cloud, DevOps & Observability Track (Part 2), while keeping core lessons `0001`–`0039` untouched.

---

### 1. Structural Decisions
- **Part 1 — Senior Backend Core (47 Lessons, Phases 0–15):**
  - Preserved `0001`–`0040` and `0049` untouched.
  - Kept developer-facing production survival skills in Part 1: `0041` (Actuator probes/metrics), `0042` (Distributed tracing), `0043` (Multi-stage Docker/Distroless), `0044` (Container JVM memory ergonomics), `0046` (Graceful shutdown/`preStop`), and `0047` (AWS S3 presigned URLs).
  - Kept existing filenames `0001`–`0049` intact so existing `localStorage` keys and lesson URLs remain valid.
- **Part 2 — Professional Cloud, DevOps & Observability Track (11 Lessons, Phases 16–18):**
  - Moved `0045` (Kubernetes Pods/Deployments/Services) and `0048` (AWS RDS Multi-AZ, RDS Proxy, Secrets Manager & LocalStack) into Part 2, and fixed the unescaped `<service-name>.<namespace>` HTML rendering bug in `0045`.
  - Added 9 new lessons (`0050`–`0058`) across Track 2A (Pro Observability), Track 2B (Pro DevOps & K8s Operations), and Track 2C (Pro AWS Cloud & Terraform IaC).
  - Added a dedicated Part 2 Capstone (`reference/pro-cloud-capstone-spec.html`) to day-2 productionize NexusCommerce with Terraform, Helm/Kustomize, HPA/PDB, Canary/Expand-and-Contract rollouts, Grafana LGTM, and SLO burn-rate alerting.
- **2027 Currency Reference:**
  - Added `reference/spring-boot-4-java-25-delta.html` summarizing Spring Boot 4.0 / Spring Framework 7 / Java 25 LTS shifts (modular starters, `spring-boot-starter-opentelemetry`, JSpecify, Jackson 3, native API versioning, JEP 491 Virtual Thread unpinning).
