# Mission: Backend System Design — Junior Android Dev → Senior (2027)

## Why
I'm a junior Android developer starting backend from scratch. In 2027 I want to (1) pass junior-to-mid (L3/L4) backend system design interviews, then (2) pass senior backend system design interviews — turning my client-side experience into backend credibility.

## Success looks like
**Part 1 — Junior-to-Mid (L3/L4)**
- Build, containerize, and deploy a Kotlin + Spring Boot + Postgres service I can explain line by line.
- Deliver the Part 1 mid-level capstone (**PulseOrder**: mobile order-ahead backend + Android client, `reference/0004-capstone-part1-pulseorder-spec.html`).
- Run a 45-minute design round for classic problems (URL shortener, notification system, chat, news feed): requirements → estimation → API → data model → high-level design → bottlenecks.
- Defend the fundamentals: HTTP, SQL & indexes, transactions, caching, queues, load balancing, replication, sharding, consistent hashing, rate limiting, idempotency, auth, real-time delivery, observability.

**Part 2 — Senior**
- Lead with trade-offs and failure modes: outbox/CDC, sagas, zero-downtime migrations, observability & SLOs, multi-region, consensus, storage-engine internals, Kafka internals.
- Upgrade PulseOrder into the Part 2 senior capstone (**NexusPulse**: distributed double-entry payment & event ledger mesh, `reference/0002-capstone-nexus-pulse-spec.html`) as a resume showcase.

## Constraints
- Primary stack: **Kotlin + Spring Boot**. **Java + Spring Boot on the side** (read/translate snippets) since most Spring docs and job posts use Java.
- Every lesson: runnable in-browser practice with tests, equal-word-count quizzes with rationales, primary-source citations, and a teacher prompt.
- Part 1 before Part 2. Senior topics (Raft, LSM, Spanner, CRDTs) wait until Part 1 is solid.

## Out of scope
- Frontend web UI / CSS.
- Pure ML model training.
- Niche Azure-specific cloud patterns (Geodes, Deployment Stamps, Sequential Convoy, etc.) — recognize names only.
