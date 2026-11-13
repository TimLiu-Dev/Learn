# System Design & Distributed Systems Resources

## Knowledge

### Primary Texts & Books
- [Book: _Designing Data-Intensive Applications_ — Martin Kleppmann](https://dataintensive.net/)
  The gold standard bible for distributed data systems. Use for: storage engine internals, replication, partitioning, transactions, and consensus.
- [Book: _Database Internals_ — Alex Petrov](https://www.databass.dev/)
  Deep mechanical sympathy of storage engines and distributed consensus. Use for: B-Trees vs LSM-Trees, Raft, Paxos, and distributed storage engines.
- [Book: _Patterns of Distributed Systems_ — Unmesh Joshi](https://martinfowler.com/articles/patterns-of-distributed-systems/)
  Comprehensive catalog of recurring distributed patterns. Use for: Write-Ahead Log, Heartbeat, Leader & Followers, Two-Phase Commit, and Gossip.
- [Book: _System Design Interview Vol 1 & 2_ — Alex Xu](https://bytebytego.com/)
  Structured templates for standard interview questions. Use for: pacing, diagram templates, and back-of-the-envelope estimation benchmarks.

### Landmark Engineering Papers & Tech Blogs
- [Paper: _The Google File System (GFS)_ — Ghemawat et al.](https://research.google/pubs/pub51/)
  Foundational paper on commodity hardware clustering. Use for: chunk servers, master bottleneck mitigation, and append-only semantics.
- [Paper: _Dynamo: Amazon's Highly Available Key-value Store_ — DeCandia et al.](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf)
  The blueprint for modern NoSQL systems. Use for: consistent hashing, vector clocks, sloppy quorums, and anti-entropy with Merkle trees.
- [Paper: _Spanner: Google's Globally-Distributed Database_ — Corbett et al.](https://research.google/pubs/pub39966/)
  Externally consistent distributed transactions at global scale. Use for: TrueTime API, Paxos state machines, and multi-version concurrency control (MVCC).
- [Paper: _In Search of an Understandable Consensus Algorithm (Raft)_ — Ongaro & Ousterhout](https://raft.github.io/raft.pdf)
  The modern standard consensus protocol. Use for: leader election, log replication, and safety invariants.
- [Blog: _Stripe Engineering Blog_](https://stripe.com/blog/engineering)
  Best-in-class real-world fintech architecture. Use for: idempotency keys, payment ledgers, rate limiting, and zero-downtime database migrations.
- [Blog: _Netflix TechBlog_](https://netflixtechblog.com/)
  Pioneers of chaos engineering, edge routing, and multi-region resilience. Use for: Zuul gateway, Chaos Monkey, active-active failover, and caching.
- [Blog: _Uber Engineering_](https://www.uber.com/blog/engineering/)
  High-throughput geospatial dispatch and real-time streaming architectures. Use for: H3 indexing, Kafka at scale, and microservice orchestration.
- [Blog: _Cloudflare The Cloudflare Blog_](https://blog.cloudflare.com/)
  Edge compute, DDoS mitigation, and distributed rate limiting at 50M+ RPS. Use for: Anycast, edge caching, and eBPF network filtering.

### Part 1 Foundations (Junior → Mid, Kotlin + Spring Boot)
- [Roadmap: roadmap.sh System Design](https://roadmap.sh/system-design) ([source](https://github.com/kamranahmedse/developer-roadmap))
  Vocabulary checklist (132 nodes). Use for: coverage checks only — not as a learning order. Audited 2026-11-12, see LR-0005.
- [Guide: _The System Design Primer_ — Donne Martin](https://github.com/donnemartin/system-design-primer)
  Source of roadmap.sh's fundamentals section. Use for: scalability basics, CAP, caching, DB scaling, worked interview problems.
- [Docs: Azure Cloud Design Patterns](https://learn.microsoft.com/en-us/azure/architecture/patterns/) and [Performance Antipatterns](https://learn.microsoft.com/en-us/azure/architecture/antipatterns/)
  Source of roadmap.sh's patterns section. Use for: retry, circuit breaker, queue-based load leveling, chatty I/O, retry storm.
- [Tutorial: Building web applications with Spring Boot and Kotlin — spring.io](https://spring.io/guides/tutorials/spring-boot-kotlin)
  Official Kotlin-first Spring Boot walkthrough. Use for: Phase 0 project setup, controllers, Spring Data, testing.
- [Tool: Spring Initializr](https://start.spring.io/)
  Official project generator. Use for: bootstrapping Kotlin/Java Spring Boot projects.
- [Spec: RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110) and [MDN: HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
  Authoritative HTTP method and status semantics. Use for: choosing 200/201/204/400/404/409, Location header.

## Wisdom (Communities)

- [Reddit: r/ExperiencedDevs](https://reddit.com/r/ExperiencedDevs)
  High-signal discussions among senior and staff software engineers. Use for: interview post-mortems, leveling calibration, and architecture trade-offs.
- [Lobste.rs](https://lobste.rs)
  Anticipatory, technically rigorous link aggregator and discussion forum for systems engineering. Use for: emerging distributed systems research and post-mortems.
- [Hacker News (Y Combinator)](https://news.ycombinator.com)
  Real-time discussions and post-mortems from top industry practitioners. Use for: outage analyses and modern cloud tooling debates.
- [System Design Community Discord](https://discord.gg/systemdesign)
  Active mock interview community for Senior and Staff practice sessions. Use for: peer mock interviews and live feedback calibration.

## Gaps
- Mobile-to-backend bridge material (offline-first sync, FCM fan-out at scale) from a high-trust source: not yet found.
- Concrete 2027 AI-augmented backend design (LLM caching, semantic rate limiting, RAG latency budgets): rapidly evolving; must monitor emerging papers from Anthropic/OpenAI on inference infrastructure.
