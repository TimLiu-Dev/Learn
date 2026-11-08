# Curriculum: Idiomatic Kotlin

Master roadmap targeting **Android**, **Kotlin Multiplatform (KMP)**, **Spring Boot**, and **DSA Coding Interviews**.
One lesson per session; **R** = mixed review that ends each section.
Items marked ➕ were added to expand the language scope.

**Every lesson follows the same shape:**
1. Warm-up: 2–3 recall questions from earlier lessons (spaced practice)
2. The concept: what problem it solves, why the syntax looks the way it does, and its implications across Android, backend, and algorithmic efficiency
3. Quick check: a short quiz with instant feedback
4. Practice: Native in-browser code editor with instant typing. One **write-from-blank** task plus one or two **refactors** of AI/Java-flavoured code. Every exercise includes a **Model Answer** tab with self-check rationale. Run code in your separate local IDE or playground.

---

## 0 · Diagnostic
- **0.1** ➕ Calibration: one short refactor and one write-from-blank task, so we find where recall actually breaks down and can skip or speed through what you already have

## 1 · Foundations: thinking in expressions
- **1.1** `val` vs `var`, and type inference: immutable by default
- **1.2** String templates and raw strings (`"$x"`, `"${a.b}"`, `"""…""".trimIndent()`)
- **1.3** Ranges and progressions (`..`, `..<`, `downTo`, `step`, `in`)
- **1.4** `if` and `try` as expressions: why Kotlin has no ternary operator
- **1.5** `when` in depth: with or without a subject, `is`, `in`, exhaustiveness
- **1.6** ➕ Equality (`==` vs `===`) and destructuring (`val (a, b) = pair`)
- **R1** Mixed review

## 2 · Null safety
- **2.1** Nullable types and `?.`: moving null checks from runtime to compile time
- **2.2** Elvis `?:`, including `?: return` and `?: throw`
- **2.3** Smart casts, `is`, `as?`, and why some smart casts fail (`var` properties)
- **2.4** `!!`, `lateinit` ➕, and ➕ platform types (`String!`) from Android/Java APIs
- **R2** Mixed review

## 3 · Functions
- **3.1** Default and named arguments: why Kotlin rarely needs overloads or builders
- **3.2** Single-expression functions, and when to write the return type
- **3.3** Extension functions and properties, and ➕ top-level functions instead of `Utils` classes
- **3.4** Infix and ➕ operator functions: reading them everywhere, defining them rarely
- **R3** Mixed review

## 4 · Classes and modelling
- **4.1** Primary constructors and properties (`val` in the constructor, `init`, custom getters, backing fields)
- **4.2** Secondary constructors, ➕ `open`/`final` by default, ➕ visibility (`internal`, `private`)
- **4.3** Data classes: what you get for free (`equals`, `copy`, destructuring) and when *not* to use one
- **4.4** Enum classes: properties, `entries`, exhaustive `when`
- **4.5** Sealed classes and interfaces: modelling UI state (Loading / Success / Error)
- **4.6** `object`, `companion object`, ➕ `data object`
- **4.7** ➕ Value classes (`@JvmInline value class UserId(val raw: String)`): type-safe IDs
- **R4** Mixed review

## 5 · Lambdas and higher-order functions
- **5.1** Function types, lambda syntax, `it`, trailing-lambda syntax
- **5.2** ➕ Function references (`::`), ➕ `inline`, and ➕ `return` inside lambdas (`return@forEach`)
- **5.3** ➕ Lambdas with receiver: how `apply`, `buildString`, Compose and Gradle `.kts` work
- **R5** Mixed review

## 6 · Scope functions
- **6.1** `let` and `also` (`it`-based): null handling and side effects
- **6.2** `apply`, `run` and `with` (`this`-based): configuring and computing
- **6.3** Choosing one: a decision table, overuse (nested `?.let` chains AI loves), ➕ `takeIf`/`takeUnless`
- **R6** Mixed review

## 7 · Collections & DSA Data Structures
- **7.1** Read-only vs mutable collections (`listOf`, `mutableListOf`, `buildList`), primitive unboxed arrays (`IntArray`) vs boxed `Array<Int>`
- **7.2** Transforming: `map`, `mapNotNull`, `flatMap`, `filter`, `associateBy`, and index mapping
- **7.3** Grouping and aggregating: `groupBy`, `partition`, `fold`/`reduce`, `sumOf`, `count`
- **7.4** DSA search & ordering: `binarySearch()`, custom `Comparator`, `sortedBy`, sliding window (`windowed`, `chunked`), ➕ infix bitwise ops (`shl`, `shr`, `and`, `or`, `xor`, `inv`), and ➕ 2D primitive matrix allocation
- **7.5** DSA core structures: double-ended queue (`ArrayDeque` for BFS/Stack), `PriorityQueue` (Min/Max Heap for Top-K), `LinkedHashMap`
- **7.6** ➕ Sequences: lazy evaluation pipelines and big-O space complexity
- **R7** Mixed review: collections & algorithmic puzzles

## 8 · Delegation
- **8.1** `by lazy`, and `lazy` vs `lateinit`
- **8.2** Delegated properties: how `by` works (`getValue`), `observable`, and Android delegates (`by viewModels()`, `by remember`)
- **8.3** Class delegation: composition instead of inheritance
- **R8** Mixed review

## 9 · Generics and the advanced type system
- **9.1** Generic classes and functions: type parameters, constraints (`T : Comparable<T>`)
- **9.2** Variance: covariance (`out`), contravariance (`in`), and call-site variance (`out`/`in` projection)
- **9.3** Star projections (`*`) and type erasure vs `inline` with `reified` type parameters
- **R9** Mixed review

## 10 · Concurrency with Coroutines and Flow
- **10.1** Coroutines foundations: `suspend`, suspending vs blocking, `launch` vs `async`/`await`
- **10.2** Coroutine contexts, dispatchers (`Dispatchers.IO`, `Default`, `Main`), and `withContext`
- **10.3** Structured concurrency: `CoroutineScope`, `Job`, supervisor jobs, cancellation and timeouts
- **10.4** Channels and cold streams: `Flow`, operators (`map`, `filter`, `transform`), exception handling
- **10.5** Hot streams in practice: `StateFlow` and `SharedFlow` (UI state and event pipelines)
- **R10** Mixed review

## 11 · Idiomatic vs AI/Java-flavoured Kotlin (capstones)
- **11.1** ➕ Error handling: no checked exceptions, `require`/`check`/`error`, `runCatching`/`Result`, and the `CancellationException` coroutine trap
- **11.2** Smell catalogue: spot and name 15 common non-idiomatic patterns produced by AI & Java habits
- **11.3** Capstone A (Android): UDF search & bookmark ViewModel (`StateFlow.update` + buffered `Channel` one-off effects)
- **11.4** Capstone B (Backend/Spring Boot): DTO invariants, `@field:` targets, JPA entity caveats, and service mapping
- **11.5** Capstone C (DSA Interview): Two Pointers, Monotonic Deque (`ArrayDeque`), overflow-safe comparators & bitwise ops
- **11.6** ➕ Kotlin Multiplatform (KMP) essentials: `expect`/`actual` vs common interfaces & in-memory fakes
- **R11** Cumulative review across the entire language

## 12 · Testing Idiomatic Kotlin, Coroutines & Flows (Senior Track — Built & Live)
- **12.1** ➕ Idiomatic unit testing: `kotlin.test` & JUnit 5, backtick test names, parameterized tests, and Fakes vs `MockK` (`commonTest`)
- **12.2** ➕ Testing Coroutines & virtual time: `runTest`, `StandardTestDispatcher` vs `UnconfinedTestDispatcher`, `advanceTimeBy`, and injected dispatchers
- **12.3** ➕ Testing Flows & hot state: `StateFlow` conflation, `Channel` effects, `backgroundScope`, and `Turbine`
- **R12** ➕ Mixed review & Senior Production Bug Hunt: race conditions, unmockable singletons, and dispatcher leaks

---

## Senior Deep-Dive Expansion Queue (Section-by-Section)
1. **Section 3 (Functions)**: Expand to 3–4 quizzes/lesson, progressive hints, `check()` harnesses, and JVM interop annotations (`@JvmOverloads`, `@JvmName`, `@JvmStatic`).
2. **Section 4 (Classes & Modeling)**: Expand quizzes/hints + `data class` binary compatibility, array equality traps, and `copy()` encapsulation caveats.
3. **Section 5 (Lambdas & DSLs)**: Add `fun interface` (SAM), `@DslMarker`, `crossinline` / `noinline` / `@PublishedApi`, and Kotlin Contracts (`callsInPlace`).
4. **Section 6 (Scope Functions)**: Expand to 3–4 quizzes/lesson, hints, `check()` harnesses, and Production Bug Hunt exercises.
5. **Section 7 (Collections & DSA)**: Add `tailrec` & `DeepRecursiveFunction`, `List<T>` vs `ImmutableList<T>`, and `Sequence` small-list overhead crossover.
6. **Section 8 & 9 (Delegation & Generics)**: Expand thin reference sheets (`delegation.html`, `generics.html`), add 3–4 quizzes/lesson, hints, and JVM signature clash resolution.
7. **Section 10 (Coroutines & Flow)**: Add `Mutex.withLock` / `Semaphore`, `callbackFlow` + `awaitClose`, `stateIn` / `shareIn` (`WhileSubscribed(5_000)`), `collectLatest` / `flatMapLatest`, and `CoroutineExceptionHandler` traps.
8. **Section 12 (Testing)**: Build Lessons `0062`–`0065` + `reference/testing.html`.

---

## Reference sheets built along the way (`reference/`)
- **Language Reference Sheets**: Foundations (1.x) · Null-safety (2.x) · Functions (3.x) · Classes & Modeling (4.x) · Lambdas (5.x) · Scope functions (6.x) · Collections & DSA (7.x) · Delegation (8.x) · Generics (9.x) · Coroutines & Flow (10.x) · Smell catalogue (11.x).
- **The Four Production Pillar Playbooks**:
  1. **Android & Jetpack Compose Playbook** (`reference/android.html`): UDF state, `collectAsStateWithLifecycle`, one-off effect channels, Compose stability contracts.
  2. **Spring Boot & Server-Side Kotlin Playbook** (`reference/spring-boot.html`): Compiler plugins (`kotlin-spring`/`all-open`, `kotlin-jpa`/`no-arg`), use-site target annotations (`@field:NotBlank`), JPA entity patterns, and non-blocking WebFlux coroutines.
  3. **Kotlin Multiplatform (KMP) Architecture Playbook** (`reference/kmp.html`): Source set hierarchies, `expect`/`actual` vs common interfaces, Ktor client, and `kotlinx.serialization`.
  4. **DSA Coding Interview Playbook** (`reference/dsa-patterns.html`): Unboxed `IntArray`, infix bitwise ops (`shl`/`shr`/`and`/`or`/`xor`/`inv`), 2D matrices, and canonical LeetCode templates (Two Pointers, Sliding Window, Monotonic Stack, Bisect Binary Search, BFS/DFS).
- **Glossary**: Canonical terminology and definitions (`GLOSSARY.md`).
