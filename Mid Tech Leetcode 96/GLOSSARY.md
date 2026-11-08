# Universal Mid-Tech LeetCode 103 Glossary

Canonical terminology, precise definitions, and shared vocabulary for algorithmic patterns, complexity boundaries, and runtime mechanics across all lessons.

> **Rule:** Terms are added progressively as concepts are anchored in lessons. Definitions stay tight (1–2 sentences) capturing what the concept *is* and what to avoid confusing it with.

## Terms

- **Amortized O(1) Time**: Constant average cost per operation over a sequence of operations, even though occasional table resizing/rehashing costs `O(N)`. *Avoid saying "guaranteed worst-case O(1)" for standard hash tables.*
- **Auxiliary Space**: Extra memory allocated temporarily by an algorithm beyond the input itself (excluding output storage unless specified). *Avoid confusing with total memory footprint, which includes the input.*
- **Boxing & Unboxing**: Wrapping a JVM primitive (`int` / Kotlin `Int`) inside a heap-allocated `java.lang.Integer` object (~16–24 bytes) when storing it in generic collections like `HashSet<Int>` or `HashMap<Integer, V>`. *Avoid claiming JVM generic sets store raw 4-byte primitives.*
- **Canonical Signature (Equivalence Key)**: A deterministic representation (such as sorted characters `"aet"` or a delimited 26-count key `"#1#0#0...#1"`) to which every member of an equivalence class maps identically. *Avoid undelimited multi-digit string concatenation like `"110"`, which collides `[1, 10]` with `[11, 0]`.*
- **Early Exit (Short-Circuit Termination)**: Returning immediately once a decisive condition is met (such as `!seen.add(num)` on the first duplicate) rather than processing the remaining input. *Avoid one-liners like `nums.toSet().size != nums.size` when streaming or early duplicate detection matters.*
- **Hash Collision**: When two distinct keys map to the same hash bucket index (`hash(key) & (capacity - 1)`), requiring bucket probing or chaining/treeification + `.equals()` verification. *Avoid saying equal hash codes imply equal keys.*
- **In-Place Algorithm**: An algorithm that transforms data directly within the input structure using at most `O(1)` (or `O(log N)` recursion stack) auxiliary space, such as `Arrays.sort(int[])`. *Avoid calling an algorithm side-effect-free when it mutates the caller's input array.*
- **Input Mutation**: Modifying the caller-owned array or object passed into a function (for example, sorting `nums` in place), which can break caller assumptions or cause shared-state bugs. *Always ask the interviewer whether mutating the input array is acceptable before sorting in place.*
- **Loop Invariant**: A property that holds true before and after every iteration of a loop (for example, *"every element in `nums[0..i-1]` is unique and stored in `seen`"*), proving correctness at termination.
- **Space-Time Trade-Off**: Deliberately spending extra auxiliary memory (such as an `O(N)` `HashSet` or `HashMap`) to eliminate repeated linear scans and drop time complexity (for example, from `O(N²)` down to `O(N)`).
