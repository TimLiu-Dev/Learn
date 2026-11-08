# Distributed Backend System Design Glossary

Core architectural vocabulary for senior-level system design interviews across Mid Tech and Big Tech.

## Distributed Foundations

**CAP Theorem**:
A formal proof that in an asynchronous network subject to partitions (P), a distributed data store can guarantee at most either strong consistency (C) or high availability (A).
_Avoid_: Fast vs Consistent, Tri-factor balance

**PACELC Theorem**:
An extension to CAP stating that If there is a Partition (P), trade off Availability (A) and Consistency (C); Else (E), trade off Latency (L) and Consistency (C).
_Avoid_: Extended CAP

**Linearizability**:
A recency guarantee ensuring that every read returns the value of the most recent write in real global wall-clock time across all nodes.
_Avoid_: Instant sync, Global lock

**Eventual Consistency**:
A liveness property guaranteeing that in the absence of new updates, all replicas will eventually converge to identical state.
_Avoid_: Eventual correctness, Loose sync

**Write-Ahead Log (WAL)**:
An append-only disk log where state changes are flushed sequentially before applying them to in-memory tables or storage indexes, guaranteeing durability.
_Avoid_: Transaction journal, Audit trail

## Concurrency & Resiliency

**Idempotency**:
A mathematical property of an API operation where making the identical request multiple times produces the exact same side-effect as a single request.
_Avoid_: De-duplication, Safe retrying

**Circuit Breaker**:
A client-side stability pattern that tracks downstream failures and rapidly fails fast without sending requests once a threshold is breached, preventing cascade outages.
_Avoid_: Retry blocker, Error catcher

**Token Bucket**:
A rate-limiting algorithm that maintains a bucket filled with tokens at a constant rate, admitting requests immediately if tokens exist and dropping them when empty.
_Avoid_: Throttler, Burst limiter

**Sliding Window Counter**:
A low-memory hybrid rate-limiting algorithm that calculates request density by combining the count of the previous fixed window with the progress through the current window.
_Avoid_: Rolling window, Moving average

## Storage & Messaging

**Log-Structured Merge-Tree (LSM-Tree)**:
A storage engine architecture that buffers writes in memory (MemTable) before sequentially flushing to immutable sorted disk runs (SSTables), optimizing write throughput.
_Avoid_: Write tree, Append database

**B-Tree**:
A self-balancing search tree variant with high branching factor that stores sorted keys across fixed-size pages on disk, optimizing random read performance.
_Avoid_: Binary search tree, Balanced index

**Outbox Pattern**:
A reliability pattern where database updates and corresponding event messages are written to the same relational database in a single atomic ACID transaction, then published asynchronously.
_Avoid_: Dual-write bridge, Event queue sync
