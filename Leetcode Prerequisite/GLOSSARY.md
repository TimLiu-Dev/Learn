# Algorithmic Thinking & Technical Interview Foundations Glossary

Canonical terminology and mechanical definitions for algorithmic problem solving, physical hardware execution, and technical coding interviews.

## Hardware & Memory

**Contiguous Memory**:
A continuous, unbroken sequence of byte addresses allocated in physical RAM without gaps.
_Avoid_: Sequential storage, connected memory

**Pointer**:
A variable whose numerical value is the physical or virtual memory address of another location in RAM.
_Avoid_: Memory reference, link variable

**Stack Frame**:
A fixed, contiguous block of memory allocated on the call stack to store a single function invocation's local variables, arguments, and return address.
_Avoid_: Call record, activation block

**Heap**:
A dynamic, unstructured region of system RAM used for objects and arrays whose lifespan exceeds the stack frame that created them.
_Avoid_: Free pool, dynamic area

**Cache Line**:
The fixed-size chunk of physical memory (typically 64 bytes) transferred between main RAM and the CPU L1/L2/L3 caches in a single bus transaction.
_Avoid_: Memory packet, cache block

## Asymptotic Complexity

**Asymptotic Analysis**:
The mathematical evaluation of how an algorithm's resource consumption (time or memory) scales as the input size `n` approaches infinity.
_Avoid_: Performance testing, benchmark calculation

**Big O (`O(...)`)**:
The formal mathematical upper bound that guarantees an algorithm's worst-case rate of growth as `n -> infinity`.
_Avoid_: Average speed, maximum runtime

**Auxiliary Space**:
The extra temporary memory allocated by an algorithm during execution, strictly excluding the memory occupied by the input arguments and returned output.
_Avoid_: Working space, scratchpad memory

**Logarithmic Growth (`O(log n)`)**:
A growth rate proportional to the number of times the input size `n` can be divided by a constant factor (typically 2) before reaching 1.
_Avoid_: Sub-linear curve, exponential decay

**Amortized Complexity**:
The average resource cost per operation evaluated over a worst-case sequence of operations, where infrequent expensive steps are distributed across frequent inexpensive steps.
_Avoid_: Average-case runtime, smoothed complexity

**In-Place Algorithm**:
An algorithm that transforms its input directly within the existing data structure using `O(1)` bounded auxiliary memory (excluding input storage, though recursive call-stack frames must still be accounted for).
_Avoid_: Zero-memory algorithm, destructive algorithm

## Data Structures & Algorithmic Mechanics

**Algorithmic Bottleneck**:
The single most computationally expensive operation in an execution pipeline that dominates the total Big O runtime.
_Avoid_: Slow part, chokepoint

**Dynamic Array**:
A resizable wrapper around a fixed-size contiguous backing array (`ArrayList`, Python `list`) that allocates a `2x` larger contiguous block and copies existing elements upon overflow, achieving `O(1)` amortized append at the tail.
_Avoid_: Elastic array, auto-growing list

**Hash Set**:
A hash-table-backed collection storing unique keys without associated values, supporting `O(1)` average membership testing, insertion, and deletion via hash codes and bucket indexing.
_Avoid_: Unique list, deduplicated array

**Separate Chaining**:
A collision resolution technique where each hash table bucket stores a linked list or balanced tree of entries that hash to the same bucket index.
_Avoid_: Bucket chaining, collision list

**Modulo Compression**:
The mathematical operation `(hash & 0x7FFFFFFF) % bucket_count` (or `hash.mod(bucket_count)`) used to map an arbitrary 32-bit hash integer into a valid array index between `0` and `bucket_count - 1`.
_Avoid_: Array wrapping, index clamping

**ASCII Offset Indexing**:
Direct-address array indexing that subtracts a base character code (such as `ch - 'a'` for `0..25` or `ch - '0'` for `0..9`) to map bounded characters into a tiny contiguous frequency array in `O(1)` worst-case time with zero hash overhead.
_Avoid_: Letter numbering, alphabet hashing

**Ring Buffer (Deque)**:
A contiguous backing array paired with `head` and `tail` indices that wrap around via modulo arithmetic (`ArrayDeque`), enabling `O(1)` amortized insertion and removal at both the front (FIFO queue) and back (LIFO stack).
_Avoid_: Double list, two-way array

**Base Case**:
The explicit non-recursive termination condition in a recursive function that returns an immediate value without pushing any further stack frames, preventing `StackOverflowError`.
_Avoid_: Stop rule, exit line

**Call Stack Unwinding**:
The reverse lifo phase of recursion after hitting a base case, in which completed stack frames pop off the call stack one by one and return their computed results to their suspended callers.
_Avoid_: Back-stepping, reverse looping

**Loop Invariant**:
A formal logical condition that is guaranteed to be true before a loop begins, after each iteration, and immediately upon loop termination.
_Avoid_: Loop rule, consistency check

**Complement**:
In pair-matching problems, the exact mathematical difference between the target value and the current element (`complement = target - current`).
_Avoid_: Target difference, matching partner

## Interview Dynamics

**State Trace Table**:
A systematic columnar walkthrough recording the deterministic values of all active variables and pointers across sequential loop iterations on a sample input.
_Avoid_: Trace matrix, variable chart

**Edge Case**:
An extreme, degenerate, or boundary input (e.g. empty collection, single item, all duplicates, negative numbers, maximum integer bound) that exposes latent logical flaws.
_Avoid_: Corner case, weird input

**Consensus Check**:
A deliberate, explicit verbal confirmation requested by the candidate to align with the interviewer on an algorithmic approach before writing code.
_Avoid_: Green light, permission request
