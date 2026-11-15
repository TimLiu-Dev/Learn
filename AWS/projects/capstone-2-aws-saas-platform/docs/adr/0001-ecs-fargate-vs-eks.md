# ADR-0001: Compute Platform — ECS Fargate (Graviton ARM64) vs. Amazon EKS

- **Status:** Accepted
- **Date:** 2026-11-15
- **Decision Owners:** CloudPulse Platform Architecture
- **Scope:** Core multi-tenant API (`cloudpulse-api`) and async ingestion services

---

## 1. Context

CloudPulse is a B2B SaaS telemetry and billing platform operated by a lean 4–6 engineer backend/platform team. We need a container orchestration platform that provides:
- Multi-AZ high availability and zero-downtime rolling/canary deployments
- Strong tenant network isolation inside private VPC subnets (`10.20.0.0/16`)
- Fine-grained IAM boundaries per service (Task Role vs. Execution Role)
- Low operational toil (no OS kernel patching, CNI IP exhaustion tuning, or Kubernetes control-plane upgrade emergencies)

## 2. Decision Drivers

1. **Team Cognitive Load & Toil:** Minimize undifferentiated heavy lifting (control plane upgrades, node AMI rotation, cluster autoscaler/Karpenter tuning).
2. **Security Boundary:** Isolate container runtime credentials cleanly from infrastructure bootstrapping credentials.
3. **Price-Performance:** Leverage AWS Graviton (ARM64) compute without managing EC2 instance families or bin-packing utilization.
4. **Deployment Safety:** Built-in rollback on unhealthy health checks without maintaining third-party GitOps/service-mesh controllers on day one.

## 3. Options Considered

| Criterion | **Option A: ECS on Fargate (ARM64)** | **Option B: Amazon EKS (Managed Node Groups / Fargate)** | **Option C: AWS Lambda + API Gateway Only** |
| :--- | :--- | :--- | :--- |
| **Control Plane Cost** | \$0/month | \$73/month per cluster (\$0.10/hr) | \$0/month |
| **OS & Node Patching** | Managed by AWS (serverless tasks) | Shared responsibility (AMI updates, Karpenter/CAS) | Managed by AWS |
| **IAM Granularity** | Native Task Role vs. Execution Role per TaskDef | IRSA / Pod Identity (extra OIDC/addon config) | Per-function IAM Role |
| **Long-Lived Connections / Streaming** | Full support (gRPC, WebSockets, connection pooling) | Full support | Constrained (15-min max, cold starts in VPC/Aurora) |
| **Deployment Rollback** | Native ECS Deployment Circuit Breaker + ALB health checks | Requires Argo Rollouts / Flagger or custom readiness logic | Lambda aliases + CodeDeploy |
| **Portability** | OCI containers (portable to EKS/Cloud Run later) | Highest (Kubernetes API) | Low (Lambda handler contract) |

## 4. Decision

**Adopt Amazon ECS on AWS Fargate running Linux/ARM64 (Graviton)** for the core CloudPulse API (`512 CPU / 1024 MiB` baseline, autoscaling `2–10` tasks across 2 AZs), paired with **AWS Lambda (ARM64)** strictly for event-driven Step Functions Saga steps.

Key architectural choices enforced in [saas-platform-stack.ts](../../infra/cdk/lib/saas-platform-stack.ts):
1. **Strict IAM Separation:** `executionRole` is scoped to ECR pull, CloudWatch Logs write, and Secrets Manager bootstrap; `taskRole` is scoped strictly to DynamoDB, SQS, EventBridge, and RDS Proxy IAM auth.
2. **Graviton ARM64:** ~20% lower vCPU/GB-hour cost vs. x86_64 Fargate with better p99 latency consistency.
3. **Deployment Circuit Breaker (`enable: true, rollback: true`):** Automatically halts and rolls back failed task launches before draining healthy tasks.

## 5. Consequences & Tradeoffs

### Positive
- **Zero node fleet management:** Eliminates EC2 AMI patching, kubelet/CNI version skew, and cluster upgrade sprints.
- **Instant cost savings:** Saves \$73/mo control plane fee plus idle EC2 buffer capacity (~25–35% slack typical in EKS node groups).
- **Clean FinOps attribution:** Every task's vCPU/memory cost maps directly to the service without shared-node chargeback heuristics.

### Negative / Mitigations
- **Startup latency (~20–35s per Fargate task):** Mitigated by keeping `minCapacity: 2` across 2 AZs and scaling out on CPU (60%) and `RequestCountPerTarget` (1,000 req/target) with a 60s scale-out cooldown.
- **No Kubernetes ecosystem CRDs (e.g., Istio, Kyverno):** Mitigated by using native AWS primitives (API Gateway HTTP API + WAFv2 + ALB + IAM + PrivateLink).

## 6. Revisit Triggers

Re-evaluate moving specific workloads to **Amazon EKS** only if:
1. Monthly Fargate spend exceeds **\$15,000/month** where dense EC2 Spot/Reserved bin-packing yields >30% net savings after platform headcount cost.
2. Product requirements demand custom kernel/eBPF networking, GPU daemonsets, or multi-cloud Kubernetes portability for enterprise on-prem deployments.
