# AegisOps Customer UAT Sign-Off & Incident Runbook

## 1. Pre-Go-Live UAT Checklist
- [ ] Zero P0/P1 blockers open across real sample tenant exports (`configs/tenants/<slug>.json`)
- [ ] Cross-tenant & role-based ACL verification passed (`group:eng` cannot retrieve `group:exec` chunks)
- [ ] DLQ replay verified on malformed rows with zero raw PII in logs/alerts
- [ ] Terraform (`publicly_accessible = false`, KMS enabled) + K8s (`startupProbe`, `NetworkPolicy`) verified
- [ ] Eval CI Gate passed (`Recall@5 >= 0.85`, `Red-Team = 1.00`, `Kappa >= 0.70`)

## 2. P0 / P1 Incident Triage Playbook
| Symptom | Likely Root Cause | First 3 Commands / Checks | Immediate Mitigation |
| --- | --- | --- | --- |
| Webhook 401 spikes | Middleware altered raw body or clock skew > 300s | Inspect `x-webhook-timestamp` vs server UTC; verify secret rotation env | Confirm raw bytes reach `hmac_guard.py`; align NTP/window |
| Pod `CrashLoopBackOff` (`Exit 137`) | Memory limit too low for PDF/tokenizer init | `kubectl describe pod`; check `OOMKilled` | Raise K8s `limits.memory` to `2Gi` and verify `startupProbe` |
| Retrieval misses exact SKUs | Missing `tsvector` GIN match or over-aggressive dense weight | Inspect `prefilter_hybrid_rrf` BM25 input & chunk metadata | Verify `doc_chunks.tsv` index and contextual prefix |
| Suspected ACL leak | Stale JWT groups used instead of SCIM principals | Audit `session.principals` vs `doc_chunks.allowed_principals` | Revoke user in SCIM; rotate session cache immediately |
