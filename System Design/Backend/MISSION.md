# Mission: Senior Distributed Backend Systems (2027 Mastery)

## Why
Pass Senior Software Engineer (L5/L6) distributed systems design interviews in 2027 by deeply mastering the four core pillars of distributed backend architecture: Distributed Consensus, Multi-Region Replication, Storage Engine Internals, and Event Streaming.

## Success looks like
- Fluently defend the 4 core distributed pillars in 45-minute interviews:
  1. **Storage Engine Internals:** LSM-Trees vs B-Trees, Write-Ahead Logs (WAL), MemTables, SSTables, Bloom filters, write amplification, compaction strategies (Leveled vs Size-Tiered).
  2. **Distributed Consensus:** Raft & Paxos mechanics, leader election, log replication, joint consensus, split-brain mitigation, quorum math ($W + R > N$).
  3. **Multi-Region Replication:** Active-Active topologies, cross-datacenter latency physics, Conflict-Free Replicated Data Types (CRDTs), Vector Clocks, Last-Write-Wins hazards.
  4. **High-Throughput Event Streams:** Kafka internals, OS page cache & `sendfile` zero-copy I/O, partition architecture, consumer group rebalancing, and exactly-once processing (EOS).
- Write and verify runnable implementations of distributed primitives directly in-browser.
- Deliver the "NexusPulse" capstone architecture integrating these four pillars for resume showcasing.

## Constraints
- Focus exclusively on the 4 distributed backend pillars; defer generic API crud questions.
- Every lesson must include runnable in-browser code with test suites, equal-word-count quizzes with full rationales, primary source citations, and direct teacher prompts.

## Out of scope
- Frontend UI rendering, CSS, or mobile client state.
- Basic monolithic CRUD APIs without distributed coordination requirements.
- Pure ML model training and weight optimization algorithms.
