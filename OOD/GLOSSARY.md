# Object-Oriented Design & Low-Level Design Glossary

The canonical language and technical definitions for this teaching workspace, calibrated for 2027 Mid-Tech and Big-Tech technical interviews.

## Domain Modeling

**Rich Domain Model**:
An object-oriented design where domain entities encapsulate both state and behavioral business logic, enforcing their own invariants.
_Avoid_: Anemic model, data bag, DTO with logic elsewhere.

**Value Object**:
An immutable object defined solely by the combination of its attributes rather than a persistent conceptual identity, compared via structural equality.
_Avoid_: Record bag, primitive type wrapper.

**Entity**:
A domain object defined by an explicit persistent identity that endures across state transitions and lifecycles.
_Avoid_: Database row, POJO.

**Aggregate Root**:
The top-level entity within an aggregate boundary that serves as the single external gateway for state mutation and invariant enforcement.
_Avoid_: God class, master controller.

**Primitive Obsession**:
The anti-pattern of using basic language primitives (e.g. `number`, `string`) to represent domain concepts with rules (e.g. currency, email, zip code).
_Avoid_: String typing, raw numeric balances.

## Concurrency & State

**Check-Then-Act Race Condition**:
A concurrency bug where a thread inspects state, assumes it remains valid, and mutates it while another thread concurrently alters that same state.
_Avoid_: Double spend, timing glitch.

**Reader-Writer Lock (RWLock)**:
A synchronization primitive that allows concurrent read operations while requiring exclusive isolation for write mutations.
_Avoid_: Global synchronized lock, giant mutex.

**Compare-And-Swap (CAS)**:
An atomic hardware-assisted instruction that updates a memory location only if its current value matches an expected value, enabling lock-free algorithms.
_Avoid_: Optimistic retry loop, thread yield.

**Finite State Machine (FSM)**:
A behavioral model consisting of a finite set of states, transitions between them, and actions triggered by events or guards.
_Avoid_: Flag-based state, nested boolean logic.

**Transition Guard**:
A deterministic boolean condition that must evaluate to true before a state machine is permitted to transition to a target state.
_Avoid_: If-check inside caller.

## Distributed-Aware OOD

**Transactional Outbox Pattern**:
A reliability pattern where state changes and their corresponding domain events are committed atomically within the same unit of work before dispatch.
_Avoid_: Dual-write, post-commit publish.

**Saga Pattern**:
An architectural pattern for coordinating transactions across distributed boundaries using a sequence of local transactions and compensating rollbacks.
_Avoid_: Two-Phase Commit (2PC), distributed lock.

**Compensating Transaction**:
An idempotent undo operation executed during a Saga rollback to semantically reverse the effects of a previously committed step.
_Avoid_: Hard delete, rollback error.

**Idempotency Key**:
A unique client-provided identifier that guarantees a given operation produces the exact same outcome even if received multiple times.
_Avoid_: Deduplication token, nonce.

**Token Bucket**:
A rate-limiting algorithm where tokens accrue at a sustained rate up to a maximum burst capacity, refilled lazily upon incoming request timestamps.
_Avoid_: Leaky bucket timer, periodic refill thread.
