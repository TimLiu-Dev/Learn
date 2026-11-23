# Project 2 Capstone Blueprint — ShopPulse Full-Stack E2E (Part 2 Capstone)

Companion starter scaffold for [Lesson 0026: Capstone — ShopPulse E2E](../../lessons/0026-project-2-sdet-e2e-capstone.html).

## Included Starter Templates

* [`maestro/checkout_smoke.yaml`](maestro/checkout_smoke.yaml) — Declarative PR smoke flow for mobile checkout.
* [`perf-k6/checkout-load.js`](perf-k6/checkout-load.js) — Grafana k6 ramping load test with `p(95) < 300ms` and `< 1%` error thresholds.
* [`.github/workflows/ci.yml`](.github/workflows/ci.yml) — 3-tier sharded GitHub Actions quality pipeline (PR Tier 1, Merge Tier 2, Nightly Tier 3).

## Monorepo Architecture

```text
shoppulse-quality-platform/
  android-app/          # Kotlin + Compose + Roborazzi + Macrobenchmark
  backend-api/          # Spring Boot + Testcontainers + Pact Provider + BOLA AuthZ
  e2e-mobile/
    maestro/            # YAML PR smoke flows
    appium/             # Appium 2/3 + WebdriverIO Page Objects (Android & iOS)
  e2e-web-api/          # Playwright TypeScript + storageState + axe-core + Trace Viewer
  perf-k6/              # Grafana k6 thresholds
  ai-evals/             # Deterministic schema, grounding & safety evals
  .github/workflows/ci.yml
```
