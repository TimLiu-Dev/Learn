# User Learning Preferences & Workspace Notes

- **Goal**: Master Senior System Design for 2027 interviews end-to-end (targeting L5/L6 offers across both Mid Tech and Big Tech).
- **Format Requirements**:
  - HTML-based interactive lessons with clean, readable Tufte-like typography.
  - Interactive, in-browser runnable code practice widgets with test suite verification, console output, progressive hints, and full solution reveals.
  - Interactive multiple-choice quizzes with equal-length options and detailed explanations for why answers are correct or incorrect.
  - Clear structural separation between Mid Tech (Mid & Senior levels) and Big Tech (Mid & Senior levels).
  - Master Plan HTML acting as a comprehensive legend and dashboard for all tracks and lessons.
  - Portfolio-ready Capstone project ("NexusPulse") that can be showcased on a resume.
- **Core Strategic Focus (2027 Senior Interview)**:
  - Deep-dive prioritized on the 4 foundational pillars of Distributed Backend Systems:
    1. Storage Engine Internals (LSM-Trees vs B-Trees, WAL, Compaction, Bloom Filters)
    2. Distributed Consensus (Raft, Paxos, Quorums, Leader Election, Split-Brain)
    3. Multi-Region Active-Active Replication (CRDTs, Vector Clocks, Latency Physics)
    4. High-Throughput Event Streams (Kafka Internals, Zero-Copy I/O, Exactly-Once Semantics)
  - Generic application CRUD is deprioritized in favor of deep mechanical sympathy and distributed trade-offs.
  - Focus on storage strength over mere fluency strength (active retrieval, desirable difficulty).
  - Emphasize trade-offs (CAP, PACELC, latency vs consistency, cost vs redundancy) rather than reciting boilerplate diagrams.
  - Modernized for 2027: cloud-native, distributed SQL, active-active multi-region, zero-trust, event mesh, AI-assisted workload patterns.


## 2026-11-12 — Learner background update
- User describes themselves as a **junior Android developer starting backend system design from scratch**, aiming to "master" it during 2027.
- Mission confirmed and rewritten (LR-0005): Part 1 = pass L3/L4 backend system design in 2027; Part 2 = pass senior in 2027. The "Goal" bullet at the top of this file is outdated — MISSION.md wins.
- Stack: **Kotlin + Spring Boot primary; Java + Spring Boot on the side.** Show Kotlin first, Java equivalent in a collapsed/secondary block.
- Real-world "do it on your machine" steps are allowed alongside in-browser JS sandboxes (Kotlin can't run in the browser sandbox).
- Leverage Android strengths: Kotlin (→ Ktor/Spring Boot), API consumption, pagination, push notifications, offline sync, BFF.
