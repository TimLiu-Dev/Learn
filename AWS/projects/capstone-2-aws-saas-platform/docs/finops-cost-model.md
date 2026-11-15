# CloudPulse FinOps Cost Model (`us-east-1` Monthly Baseline)

This cost model quantifies the architectural FinOps decisions built into [saas-platform-stack.ts](../infra/cdk/lib/saas-platform-stack.ts) and [main.tf](../infra/terraform/main.tf) for a mid-scale B2B SaaS workload.

### Workload Assumptions
- **Region:** `us-east-1` (730 hours/month)
- **Traffic:** 50M API requests/month (~19 req/sec avg, ~120 req/sec peak)
- **Compute:** ECS Fargate ARM64 (`0.5 vCPU / 1 GB RAM`), average **4 tasks** running across 2 AZs
- **Internal AWS Service Data Transfer:** **10 TB/month** total across S3 backups/exports, DynamoDB streams/queries, ECR image pulls, CloudWatch Logs, and Secrets Manager
- **Databases:** Aurora PostgreSQL Serverless v2 (avg `1.5 ACU` writer + `0.5 ACU` reader, `100 GB` storage) + DynamoDB (`25M` writes, `50M` reads, `50 GB` storage)
- **Audit & Telemetry Archive in S3:** `10 TB` accumulated across hot + cold compliance tiers

---

## 1. NAT Gateway vs. VPC Endpoints (Gateway + PrivateLink)

In `us-east-1`, a NAT Gateway charges **\$0.045/hr** (\$32.85/mo per AZ) plus **\$0.045/GB** for data processing. PrivateLink Interface Endpoints charge **\$0.01/hr per ENI/AZ** (\$7.30/AZ/mo) plus **\$0.01/GB**—and **S3 & DynamoDB Gateway Endpoints are \$0.00/hr and \$0.00/GB**.

Assuming **6 TB/month** goes to S3/DynamoDB and **4 TB/month** goes to ECR/Logs/Secrets/STS:

| Architecture | Hourly Fixed Cost (2 AZs) | Data Processing Cost | Monthly Total |
| :--- | :--- | :--- | :--- |
| **All-NAT Baseline** (no VPC endpoints) | 2 NATs × \$32.85 = **\$65.70** | 10,240 GB × \$0.045 = **\$460.80** | **\$526.50** |
| **CloudPulse Hybrid** (2 NATs + 2 Gateway EPs + 5 Interface EPs × 2 AZs) | 2 NATs (\$65.70) + 10 ENIs (\$73.00) = **\$138.70** | 6 TB S3/DDB (**\$0.00**) + 4 TB Interface (4,096 GB × \$0.01 = **\$40.96**) | **\$179.66** |

- **Net Savings:** **\$346.84/month (\$4,162/year)** — a **65.9% reduction** in VPC data transfer spend, while keeping private traffic off the public internet.
- **FinOps Rule of Thumb (Crossover Math):** Each Interface Endpoint costs `2 AZs × $7.30 = $14.60/mo` fixed and saves `$0.045 - $0.010 = $0.035/GB`. The break-even point is `14.60 / 0.035 = 417 GB/month` per endpoint (or justified earlier for zero-trust security compliance). Gateway Endpoints (S3, DynamoDB) have **zero fixed cost** and should *always* be enabled.

---

## 2. Compute & Database: On-Demand vs. Graviton / Savings Plans / Provisioned

### A. ECS Fargate (4 Tasks Avg: 0.5 vCPU, 1 GB RAM = 2 vCPU, 4 GB total/hr)

| Option | vCPU Rate/hr | Mem Rate/hr | Monthly Total | Savings vs. x86 On-Demand |
| :--- | :--- | :--- | :--- | :--- |
| **x86_64 On-Demand** | \$0.04048 | \$0.004445 | 730 × (2×0.04048 + 4×0.004445) = **\$72.08** | Baseline |
| **ARM64 Graviton On-Demand** (implemented) | \$0.03238 | \$0.00356 | 730 × (2×0.03238 + 4×0.00356) = **\$57.67** | **-20.0%** |
| **ARM64 Graviton + 1-Yr Compute Savings Plan** (~28% off) | ~\$0.02331 | ~\$0.00256 | **~\$41.52** | **-42.4%** |

### B. DynamoDB (25M Writes + 50M Reads/mo, 1 KB items)

| Capacity Mode | Monthly Read/Write Cost | Storage (50 GB) | Monthly Total | When to Choose |
| :--- | :--- | :--- | :--- | :--- |
| **On-Demand (`PAY_PER_REQUEST`)** (implemented) | 25M × \$0.625/M + 50M × \$0.125/M = **\$21.88** | \$12.50 | **\$34.38** | Spiky B2B SaaS / early-to-mid scale; zero throttling surprises |
| **Provisioned + Auto Scaling** (steady 70% util) | ~14 WCU + ~27 RCU avg ≈ **\$7.20** | \$12.50 | **\$19.70** | Predictable steady-state (>300 WCU/RCU sustained) |

*(Note: Using `us-east-1` On-Demand pricing post-Nov 2024 50% reduction: ~\$0.625/M WRU, ~\$0.125/M RRU.)*

### C. Aurora PostgreSQL Serverless v2 vs. Provisioned

| Deployment Model | Compute Cost/mo | Storage + I/O (100 GB) | Monthly Total | Tradeoff |
| :--- | :--- | :--- | :--- | :--- |
| **Serverless v2 (`0.5–8 ACU`, avg 2.0 ACU total)** (implemented) | 2.0 ACU × 730h × \$0.12 = **\$175.20** | ~\$12.00 | **\$187.20** | Scales sub-second for bursty onboarding/billing without over-provisioning |
| **Provisioned 2 × `db.r6g.large` (Multi-AZ)** | 2 × 730h × \$0.26 = **\$379.60** | ~\$12.00 | **\$391.60** | Cheaper only if sustained utilization > 3.2 ACU 24/7 with 1-Yr RI |

---

## 3. S3 Lifecycle Tiers (10 TB Telemetry & Compliance Archive)

For immutable telemetry exports and VPC Flow/audit logs (`10 TB` = `10,240 GB` total):

| Tiering Strategy | Storage Distribution | Monthly Storage Cost | Savings vs. All-Standard |
| :--- | :--- | :--- | :--- |
| **All S3 Standard** (\$0.023/GB) | 10 TB Standard | **\$235.52** | Baseline |
| **Lifecycle Policy** (0–30d Standard → 30–90d Standard-IA → 90–365d Glacier Instant → 1–7yr Deep Archive) | 1 TB Standard (\$23.55) + 1.5 TB Std-IA (\$19.20) + 2.5 TB Glacier IR (\$10.24) + 5 TB Deep Archive (\$5.07) | **\$58.06** | **-75.3% (\$177.46/mo saved)** |

- **S3 Intelligent-Tiering** (\$0.0025 per 1,000 objects monitoring fee) is preferred *only* when access patterns are unpredictable; for deterministic time-series/audit retention, explicit Lifecycle transitions avoid the per-object monitoring fee (especially critical with millions of small telemetry JSON/Parquet files).

---

## 4. Total Monthly Platform Summary (CloudPulse Optimized Baseline)

| Layer | Monthly Estimate |
| :--- | :--- |
| VPC Networking (2 NATs + Gateway & PrivateLink Endpoints) | \$179.66 |
| Compute (ECS Fargate ARM64 + Lambda Saga) | \$59.50 |
| Ingress & Security (ALB + API Gateway HTTP API + WAFv2 + KMS) | \$54.00 |
| Data Tier (Aurora Serverless v2 + RDS Proxy + DynamoDB + SQS) | \$244.00 |
| Storage & Observability (S3 Lifecycle + CloudWatch Logs/Alarms) | \$88.00 |
| **Estimated Production Baseline** | **~\$625 / month** |
