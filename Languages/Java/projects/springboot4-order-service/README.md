# Spring Boot 4.0 & Java 25 Local Starter Lab (`springboot4-order-service`)

Companion multi-file project for **Lesson 0091** and **Main Track Phase 3 (`0060`–`0090`)**. Open this folder directly in **IntelliJ IDEA** (or run from your terminal with Gradle `9.7.1` + OpenJDK `25`) to practice real multi-file backend engineering alongside the browser lessons.

## Prerequisites on macOS

1. **OpenJDK 25 LTS:** Installed at `/opt/homebrew/opt/openjdk@25` (Gradle's `JavaLanguageVersion.of(25)` toolchain resolves it automatically, or export `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home`).
2. **Docker / OrbStack (for `bootRun` Postgres & Testcontainers):** Install [OrbStack](https://orbstack.dev/) (`brew install --cask orbstack`) or Docker Desktop before running `bootRun` or `@Testcontainers` integration tests. (`OrderControllerSliceTest` runs in <1s *without* Docker!)

## Common Commands

```bash
cd /Users/tl/Documents/Learns/Languages/Java/projects/springboot4-order-service

# 1. Run fast @WebMvcTest + RestTestClient slice tests (no Docker needed!)
gradle test

# 2. Boot the service locally with PostgreSQL 17 auto-managed via compose.yaml
gradle bootRun --args='--spring.profiles.active=local'
```

## Sample `curl` Verification

```bash
# Create an order (201 Created + Location header)
curl -i -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "X-API-Version: 1.0" \
  -d '{"customer_id":"CUST-42","sku":"JAVA25-BOOK","amount_cents":4900}'

# Trigger Jakarta Validation -> RFC 9457 ProblemDetail (400 Bad Request + fieldErrors)
curl -i -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customer_id":"","sku":"","amount_cents":-10}'

# Check Kubernetes readiness probe
curl -s http://localhost:8080/actuator/health/readiness
```
