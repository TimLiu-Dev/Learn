# Learning Record 0009: roadmap.sh/backend Gap Audit & 6 High-ROI Lesson Additions

**Timestamp:** 2026-11-12  
**Goal:** Cross-check the curriculum against `roadmap.sh/backend`, skip low-ROI noise (Go/Python/Node, SOAP/HATEOAS, Git 101, niche NoSQL tour), and close the 5 real senior engineering gaps.

---

### Additions Placed in Part 1 (Phase 15B — Core Backend Extensions)
- `0059-edge-http-reverse-proxies-cors-and-etag-caching.html`: Reverse proxy `X-Forwarded-*` (`server.forward-headers-strategy`), strict CORS, conditional `ETag` + `304 Not Modified`.
- `0060-realtime-backends-sse-websockets-and-redis-fanout.html`: SSE (`SseEmitter`) vs. WebSockets, leak-free cleanup callbacks, Redis Pub/Sub multi-pod fan-out.
- `0061-database-scaling-cap-sharding-and-nosql-tradeoffs.html`: CAP/PACELC theorem, PostgreSQL range partitioning vs. horizontal sharding, consistent hashing, NoSQL selection matrix.
- `0062-security-hardening-argon2-bcrypt-and-owasp-api-top-10.html`: Memory-hard `Argon2id`/`BCrypt`, BOLA/IDOR, banning Mass Assignment, and SSRF defense.

### Additions Placed in Part 2 (Phase 19 — Track 2D: Advanced Distributed APIs & Search)
- `0063-grpc-protobuf-vs-rest-and-graphql-tradeoffs.html`: gRPC/Protobuf binary contracts & HTTP/2 load-balancing pitfalls vs. REST, plus GraphQL `@BatchMapping` N+1 defense.
- `0064-search-engines-postgres-fts-elasticsearch-and-cqrs.html`: Inverted indexes (`GIN(tsvector)` / `pgvector` vs. Elasticsearch/OpenSearch) and CQRS read-model sync via Kafka/Outbox.

### Result
- **Part 1:** 51 Lessons (`0001`–`0044`, `0046`, `0047`, `0049`, `0059`–`0062`) + Part 1 Capstone.
- **Part 2:** 13 Lessons (`0050`–`0052`, `0045`, `0053`–`0055`, `0048`, `0056`–`0058`, `0063`–`0064`) + Part 2 Capstone.
- **Total:** 64 Lessons across 19 Architectural Phases; core lessons `0001`–`0043` remained completely untouched.
