# 2027 Senior Java Expansion (`0082`–`0086`) & Full Curriculum Polish

## Summary of Changes
1. **Added 5 Senior-Level 2027 Lessons (`0082`–`0086`) + Paired Reference Sheets (`reference/0082`–`0086`):**
   - `0082` (`lessons/0082-ffm-panama-and-leyden-aot-cache.html`): Java 25 Foreign Function & Memory API (`java.lang.foreign` JEP 454 `Arena`, `MemorySegment`, `ValueLayout`) and Project Leyden AOT Cache (`-XX:AOTCache`, JEP 483/514/515) vs. GraalVM Native Image. Placed after `0036`.
   - `0083` (`lessons/0083-modular-monoliths-hexagonal-and-archunit.html`): Modular Monoliths (`Spring Modulith` `@ApplicationModule`, `@ApplicationModuleListener` Event Publication Registry), Hexagonal Architecture (Ports & Adapters), and automated JUnit boundary gates via `ArchUnit`. Placed after `0066`.
   - `0084` (`lessons/0084-spring-ai-llm-rag-and-tool-calling.html`): Production AI Engineering in Java with `Spring AI` (`ChatClient`, `BeanOutputConverter` structured Java `record` outputs, tenant-isolated `PgVectorStore` RAG, and safe `@Tool` calling). Placed after `0081`.
   - `0085` (`lessons/0085-sagas-rate-limiting-bulkheads-and-incidents.html`): Main Track Phase 3 Senior Backend Capstone covering Virtual-Thread vs. HikariCP connection-pool starvation (`@ConcurrencyLimit` / `Semaphore` bulkheads + split `TransactionTemplate`), Distributed Sagas with idempotent compensation, multi-pod cron locks (`ShedLock`), token-bucket rate limiting, and JDK 25 `jcmd Thread.dump_to_file -format=json` incident diagnostics. Placed after `0070`.
   - `0086` (`lessons/0086-grpc-protobuf-openapi-and-graphql.html`): Modern API protocols beyond REST — gRPC (HTTP/2 & Protobuf wire tag evolution), OpenAPI 3.1 contracts, and `Spring for GraphQL` `@BatchMapping` (`DataLoader` N+1 prevention). Placed after `0062`.

2. **Polished Existing Lessons (`0002`, `0067`, `0074`–`0081`) & Reference Sheets (`0075`–`0081`):**
   - Upgraded `lessons/0002` terminal simulator initial output to OpenJDK `25.0.4` LTS and JVMS SE 25 label.
   - Upgraded `lessons/0067` and `reference/0067` layered Dockerfile commands to Spring Boot 4 `-Djarmode=tools -jar app.jar extract --layers --launcher`.
   - Expanded `lessons/0074`, `0075`, `0077`, `0078`, `0079`, `0080`, and `0081` so all 85 coding lessons contain $\ge 3$ `<pre><code>` blocks.
   - Upgraded `reference/0075`–`0081` so all 85 reference sheets include a Canonical Production Snippet (`<pre><code>`) and a Senior Production Pitfalls Checklist.
