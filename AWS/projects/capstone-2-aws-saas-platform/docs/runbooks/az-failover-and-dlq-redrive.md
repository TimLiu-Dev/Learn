# Runbook: Multi-AZ Failover & SQS Dead-Letter Queue (DLQ) Redrive

- **Service:** CloudPulse SaaS Platform (`cloudpulse-production`)
- **Owners:** Platform On-Call / SRE
- **SLOs:** Availability `99.9%` (43.2 min/mo error budget) | p99 Latency `< 500ms` | Zero silent message loss (`DLQ == 0`)

---

## Severity Classification

| Severity | Trigger Condition | Target Response |
| :--- | :--- | :--- |
| **SEV-1** | `cloudpulse-production-slo-5xx-rate` in `ALARM` (>1% 5xx) OR single-AZ outage impacting Aurora writer | Immediate (< 5 min) |
| **SEV-2** | `cloudpulse-production-slo-p99-latency` > 500ms OR `cloudpulse-production-dlq-nonempty` >= 1 | < 15 min |
| **SEV-3** | `cloudpulse-production-aurora-acu-high` > 85% without 5xx impact | Next business window |

---

## Part A: Availability Zone (AZ) Degradation or Failover

### 1. Triage (Read-Only Assessment)
Run these checks first to confirm whether degradation is isolated to one AZ (`us-east-1a` vs `us-east-1b`) or a bad code rollout:

```bash
# 1. Check active CloudWatch SLO alarms
aws cloudwatch describe-alarms \
  --alarm-name-prefix cloudpulse-production \
  --state-value ALARM \
  --query "MetricAlarms[].{Name:AlarmName,Reason:StateReason}" \
  --output table

# 2. Inspect ECS Fargate task distribution across AZs and stopped task reasons
aws ecs list-tasks \
  --cluster cloudpulse-production-cluster \
  --service-name cloudpulse-production-api \
  --query "taskArns" --output text | xargs -n 100 \
  aws ecs describe-tasks --cluster cloudpulse-production-cluster \
  --query "tasks[].{AZ:availabilityZone,LastStatus:lastStatus,Health:healthStatus}" \
  --output table

# 3. Inspect Aurora Serverless v2 writer/reader AZ placement & status
aws rds describe-db-clusters \
  --db-cluster-identifier cloudpulse-production-ledger \
  --query "DBClusters[0].DBClusterMembers[].{Instance:DBInstanceIdentifier,IsWriter:IsClusterWriter,Status:DBClusterParameterGroupStatus}" \
  --output table
```

### 2. Automated vs. Manual Failover Actions

1. **ALB & ECS Fargate Layer (Automatic, ~30–45s):**
   - ALB health checks (`/health` every 15s, `unhealthyThresholdCount: 3`) automatically stop routing to tasks in an impaired AZ.
   - ECS Fargate service scheduler launches replacement tasks in the healthy AZ.
   - **If an AZ is gray-failing** (packets dropping intermittently, passing TCP handshake but timing out DB calls), temporarily evacuate the bad AZ by verifying target health in the target group and adjusting task placement or forcing a fresh deployment:
     ```bash
     aws ecs update-service \
       --cluster cloudpulse-production-cluster \
       --service cloudpulse-production-api \
       --force-new-deployment
     ```

2. **Aurora PostgreSQL Serverless v2 + RDS Proxy Failover (~10–30s):**
   - If the Aurora writer instance is in the impaired AZ and RDS has not yet promoted the reader, force an immediate Multi-AZ failover to the healthy reader:
     ```bash
     aws rds failover-db-cluster \
       --db-cluster-identifier cloudpulse-production-ledger
     ```
   - **Why RDS Proxy protects the app:** ECS tasks and Saga Lambdas connect to `rdsProxy.endpoint` rather than the raw cluster endpoint. RDS Proxy detects the writer promotion, queues incoming queries briefly, and transparently routes new transactions to the promoted writer without requiring ECS task restarts or DNS TTL expiration.

3. **Verify Recovery:**
   - Confirm `cloudpulse-production-slo-5xx-rate` returns to `OK` for 5 consecutive minutes.
   - Confirm RDS Proxy `ClientConnections` and `DatabaseConnections` stabilize on the CloudWatch `CloudPulse-production-SLO` dashboard.

---

## Part B: SQS Dead-Letter Queue (DLQ) Inspection & Safe Redrive

A `cloudpulse-production-dlq-nonempty` alarm means a message failed processing `maxReceiveCount: 3` times. **Never blindly redrive a DLQ without inspecting the root cause**—poison-pill payloads will immediately fail 3 more times and re-trigger alerts.

### 1. Peek Messages Non-Destructively (Do Not Delete)

```bash
DLQ_URL=$(aws cloudformation describe-stacks \
  --stack-name CloudPulse-production \
  --query "Stacks[0].Outputs[?OutputKey=='IngestionDlqUrl'].OutputValue" \
  --output text)

# Receive up to 5 messages with a 30s visibility timeout (do NOT call delete-message)
aws sqs receive-message \
  --queue-url "$DLQ_URL" \
  --max-number-of-messages 5 \
  --attribute-names All \
  --message-attribute-names All \
  --visibility-timeout 30
```

### 2. Classify the Failure Mode

| Category | Symptoms in Logs / Payload | Action |
| :--- | :--- | :--- |
| **Transient Downstream Outage** | `503 ServiceUnavailable`, `RDS Proxy timeout`, `ThrottlingException` during a known incident window | **Safe to redrive** once downstream is healthy |
| **Application Bug (Fixed)** | `TypeError` / validation bug that has been patched and deployed | **Safe to redrive** after verifying fix in prod |
| **Poison Pill (Malformed Schema)** | Invalid JSON, missing `tenantId`, corrupted encoding | **Do NOT redrive.** Archive payload to incident S3 bucket, fix producer, then purge specific message receipt handle |

### 3. Execute Managed SQS DLQ Redrive

All CloudPulse consumers use `requestId` / DynamoDB `ConditionExpression` idempotency keys, so replaying transient-failure messages is safe:

```bash
DLQ_ARN=$(aws sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --query "Attributes.QueueArn" --output text)

MAIN_QUEUE_ARN="arn:aws:sqs:us-east-1:<ACCOUNT_ID>:cloudpulse-production-ingestion"

# Start AWS-managed DLQ redrive task back to the source queue
aws sqs start-message-move-task \
  --source-arn "$DLQ_ARN" \
  --destination-arn "$MAIN_QUEUE_ARN" \
  --max-number-of-messages-per-second 50

# Monitor redrive progress
aws sqs list-message-move-tasks --source-arn "$DLQ_ARN"
```

### 4. Post-Incident Verification
1. Confirm `ApproximateNumberOfMessagesVisible` and `ApproximateNumberOfMessagesNotVisible` on `cloudpulse-production-ingestion-dlq` both equal `0`.
2. Confirm `cloudpulse-production-dlq-nonempty` transitions back to `OK`.
3. Open a blameless postmortem within 24 hours for any SEV-1/SEV-2 incident documenting timeline, root cause, and missing automated guardrails.
