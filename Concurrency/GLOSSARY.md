# Concurrency Engineering Glossary

Canonical workspace terminology. Terms are promoted here once grounded in lessons and verified through practice; avoid listed aliases across all explainers and exercises.

## Terms

**Concurrency**:
Structuring a program to manage multiple tasks whose lifetimes overlap in time, even on a single CPU core.
_Avoid_: Multithreading, running in parallel

**Parallelism**:
Physically executing two or more computations at the exact same instant across multiple CPU cores.
_Avoid_: Async, concurrency

**OS Thread**:
A kernel-scheduled execution context within a process that shares the process heap and owns a dedicated call stack and register set.
_Avoid_: Lightweight thread, coroutine

**Coroutine**:
A user-space suspendable computation represented by a heap continuation object that switches cooperatively at suspension points on top of a dispatcher's thread pool.
_Avoid_: Green thread (when referring specifically to stackless Kotlin CPS coroutines), magic background task

**Preemptive Scheduling**:
Scheduler policy where the OS kernel interrupts a running thread at arbitrary CPU instructions via timer or hardware interrupts.
_Avoid_: Automatic yielding

**Cooperative Scheduling**:
Scheduling model where a running coroutine yields its underlying thread voluntarily only at explicit suspension or cancellation checkpoints.
_Avoid_: Thread preemption

**Race Condition**:
A correctness defect where program output depends on the timing or interleaving of concurrent operations accessing shared state without synchronization.
_Avoid_: Slow code, random bug

**Mutual Exclusion**:
An invariant guaranteeing that at most one execution unit holds a lock and executes a critical section at any instant.
_Avoid_: Thread-safety (too vague)
