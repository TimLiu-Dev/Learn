# ADR 0003: Failure-Driven Golden Evals, Kappa Calibration & OWASP Red-Team CI Gates

- **Status:** Accepted
- **Context:** Prompt changes and chunking updates frequently cause silent regressions in exact-SKU recall or open indirect prompt-injection vulnerabilities.
- **Decision:** Block merges in `.github/workflows/aegisops-ci.yml` unless `evals/run_evals.py` passes `Recall@5 >= 0.85`, calibrated judge `Cohen's kappa >= 0.70`, and `100%` OWASP red-team block rate.
- **Consequences:** Prevents unverified "vibe-check" prompt changes from reaching customer VPCs and gives enterprise buyers auditable pre-release quality evidence.
