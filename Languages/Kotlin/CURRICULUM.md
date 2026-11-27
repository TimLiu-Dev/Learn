# Curriculum: Idiomatic Kotlin (2027 Edition)

Master roadmap prioritized across **1. Android (Jetpack Compose)**, **2. DSA Coding Interviews**, **3. Spring Boot Backend**, and **4. Kotlin Multiplatform (KMP · Optional)** (**76 lessons**: 74 Core + 2 Optional).
One tightly-scoped lesson per session; **R** = mixed interleaved review ending each section.

**Every lesson follows the same shape:**
1. **Warm-up**: 2–5 spaced-retrieval questions strictly from earlier lessons
2. **The concept**: what problem it solves, an **Interactive Concept Lab** (`<code class="kt-run">`), and a **Blank-Page Syntax Blueprint** showing how to type the syntax from memory
3. **Quick check**: 3–8 length-balanced multiple-choice quizzes with per-option instant feedback
4. **Practice**: In-browser Kotlin 2.4 editor with automatic `localStorage` draft saving and instant verification (`▶ Run & Check`). One **write-from-blank** task plus one **refactor** of AI/Java-flavoured code, each with progressive hints and a **Model Answer** tab.

---

## 0 · Diagnostic Calibration
- **0.1** Calibration Diagnostic (`0000`): one write-from-blank classifier, one AI-code refactor, and a fast-track placement rubric for Sections 1–2

## 1 · Foundations: thinking in expressions
- **1.1** `val` vs `var`, and type inference (`0001`): immutable by default
- **1.2** String templates, raw strings & slicing (`0002`): `"$x"`, `"${a.b}"`, `"""…""".trimIndent()`, `substringAfter`, `split`, and destructured `Regex`
- **1.3** Ranges and progressions (`0003`): `..`, `..<`, `downTo`, `step`, `in`, `coerceIn`
- **1.4** `if` and `try` as expressions (`0004`): why Kotlin has no ternary operator
- **1.5** `when` in depth (`0005`): with or without a subject, `is`, `in`, exhaustiveness
- **1.6** Equality (`==` vs `===`) and destructuring (`0006`): `val (a, b) = pair`
- **R1** Mixed review: Foundations (`0007`)

## 2 · Null safety & type narrowing
- **2.1** Nullable types and `?.` (`0008`): moving null checks from runtime to compile time
- **2.2** Elvis `?:` (`0009`): defaults plus `?: return` and `?: throw` (`Nothing`)
- **2.3** Smart casts, `is`, `as?` (`0010`): why `var` properties fail smart casting
- **2.4** `!!`, `lateinit`, and platform types (`String!`) (`0011`): guarding Android/Java APIs
- **R2** Mixed review: Null safety (`0012`)

## 3 · Functions & clean APIs
- **3.1** Default and named arguments (`0013`): replacing overloads and builders
- **3.2** Single-expression functions (`0014`): when to write explicit return types
- **3.3** Extension functions & properties (`0015`): retiring `*Utils` classes
- **3.4** Infix and operator functions (`0016`): reading them everywhere, defining them judiciously
- **R3** Mixed review: Functions (`0017`)

## 4 · Classes and domain modeling
- **4.1** Primary constructors and properties (`0018`): `val` in header, `init`, custom getters, backing `field`
- **4.2** Secondary constructors, `open`/`final`, and visibility (`0019`): `internal`, `private`
- **4.3** Data classes (`0020`): generated `equals`/`copy`/destructuring and body-property pitfalls
- **4.4** Enum classes (`0021`): properties, zero-allocation `entries`, exhaustive `when`
- **4.5** Sealed classes and interfaces (`0022`): modeling UI & domain states
- **4.6** `object`, `companion object`, and `data object` (`0023`)
- **4.7** Value classes (`@JvmInline value class`) (`0024`): type-safe IDs & JVM heap-boxing rules
- **R4** Mixed review: Classes & modeling (`0025`)

## 5 · Lambdas and functional Kotlin
- **5.1** Function types, lambda syntax, `it`, trailing lambdas (`0026`)
- **5.2** Function references (`::`), `inline`, and qualified returns (`return@forEach`) (`0027`)
- **5.3** Lambdas with receiver (`T.() -> Unit`) (`0028`): how `apply`, `buildString`, Compose, and Gradle `.kts` work
- **R5** Mixed review: Functional Kotlin (`0029`)

## 6 · Scope functions
- **6.1** `let` and `also` (`it`-based) (`0030`): null handling and side effects
- **6.2** `apply`, `run`, and `with` (`this`-based) (`0031`): configuring and computing
- **6.3** Decision matrix, nested `?.let` anti-patterns, and `takeIf`/`takeUnless` (`0032`)
- **R6** Mixed review: Scope functions (`0033`)

## 7 · Collections & Complete DSA Interview Structures
- **7.1** Read-only vs mutable collections (`buildList`) & unboxed primitive arrays (`IntArray` vs `Array<Int>`) (`0034`)
- **7.2** Transforming: `map`, `mapNotNull`, `flatMap`, `filter`, `associateBy` (`0035`)
- **7.3** Grouping and aggregating: `groupBy`, `groupingBy().eachCount()`, `partition`, `fold`, `sumOf` (`0036`)
- **7.4** DSA search & ordering: `binarySearch()`, overflow-safe `compareBy`, `windowed`/`chunked`, infix bitwise ops, and 2D `Array<IntArray>` (`0037`)
- **7.5** DSA core structures: `ArrayDeque` (BFS/Stack), `PriorityQueue` (Min/Max Heap), `LinkedHashMap` (`0038`)
- **7.6** DSA strings, `Char` arithmetic & memoized DP: `ch - 'a'`, `digitToInt`, `buildString`, bottom-up `IntArray` DP, and `getOrPut` memoization (`0073`)
- **7.7** DSA trees, graphs & backtracking: null-safe `TreeNode?` DFS/BFS, indexed adjacency lists, Kahn's cycle detection, and snapshot-safe `path.toList()` backtracking (`0074`)
- **7.8** Sequences: lazy evaluation pipelines and big-O memory optimization (`0039`)
- **R7** Mixed review: Collections & DSA (`0040`)

## 8 · Delegation
- **8.1** `by lazy`, and `lazy` vs `lateinit` (`0041`)
- **8.2** Delegated properties (`getValue`/`setValue`, `Delegates.observable`, `by viewModels()`, `by remember`) (`0042`)
- **8.3** Class delegation (`class A(b: B) : I by b`) (`0043`)
- **R8** Mixed review: Delegation (`0044`)

## 9 · Generics and the advanced type system
- **9.1** Generic classes, functions, `<T : Any>`, and `where` constraints (`0045`)
- **9.2** Variance: covariance (`out`), contravariance (`in`), and call-site projections (`0046`)
- **9.3** Star projections (`*`) and `inline` with `reified` type parameters (`0047`)
- **R9** Mixed review: Generics (`0048`)

## 10 · Concurrency with Coroutines and Flow
- **10.1** Coroutines foundations: `suspend`, `launch` vs `async`/`await`, and type-safe `Duration` (`0049`)
- **10.2** Coroutine contexts, dispatchers (`IO`, `Default`, `Main`), and `withContext` (`0050`)
- **10.3** Structured concurrency: `CoroutineScope`, `supervisorScope`, `ensureActive()`, and cancellation (`0051`)
- **10.4** Cold streams: `Flow`, `flowOn`, `catch`, and search operators (`debounce`/`distinctUntilChanged`/`flatMapLatest`) (`0052`)
- **10.5** Hot streams: `StateFlow.update`, `SharedFlow`, and `stateIn(WhileSubscribed(5_000))` (`0053`)
- **R10** Mixed review: Coroutines & Flow (`0054`)

## 11 · Testing Idiomatic Kotlin, Coroutines & Flows
- **11.1** Idiomatic unit testing: `kotlin.test`, backtick test names, and in-memory Fakes vs mocks (`0062`)
- **11.2** Testing Coroutines & virtual time: `runTest`, `StandardTestDispatcher` vs `UnconfinedTestDispatcher`, `advanceTimeBy` (`0063`)
- **11.3** Testing Flows & hot state: `StateFlow` conflation, `backgroundScope`, and `Turbine` (`0064`)
- **R11** Testing review & production bug hunt (`0065`)

## 12 · Error Handling, Smells & Four-Pillar Capstones
- **12.1** Error handling: `require`/`check`/`error`, `runCatching`/`Result`, and the `CancellationException` trap (`0055`)
- **12.2** Smell catalogue: 15 common non-idiomatic AI & Java patterns (`0056`)
- **12.3** **Pillar 1 Capstone (Android)**: UDF search & bookmark ViewModel (`StateFlow.update` + buffered `Channel` effects) (`0057`)
- **12.4** **Pillar 2 Capstone (DSA Interview)**: Two Pointers & `O(N)` Monotonic `ArrayDeque` (`0059`)
- **12.5** **Pillar 3 Capstone (Spring Boot)**: DTO `@field:` targets, compiler plugins, JPA entity identity, and service mapping (`0058`)
- **12.6** **[Optional] Pillar 4 Capstone (KMP)**: `commonMain` interfaces + Fakes vs `expect`/`actual`, `kotlin.time.Clock` & `kotlin.uuid.Uuid` (`0060`)
- **R12** Core & Four-Pillar synthesis review (`0061`)

## 13 · Modern Kotlin 2.2–2.4, Compose Runtime & Senior Architecture (2027 Track)
- **13.1** Modern Kotlin 2.2–2.4 (live runnable!): `when` guards, non-local `break`/`continue`, named Context Parameters (`context(logger: Logger)`), and Explicit Backing Fields (`field = ...`) (`0066`)
- **13.2** Type-safe builder DSLs (`@DslMarker`), `fun interface` SAM, `tailrec`, and `DeepRecursiveFunction` (`0075`)
- **13.3** Production Android Flow & Concurrency: `callbackFlow` + `awaitClose`, `stateIn(WhileSubscribed(5_000))`, `flatMapLatest`, `Mutex.withLock` (`0067`)
- **13.4** Gradle Kotlin DSL (`build.gradle.kts` & `libs.versions.toml`), resilient `kotlinx.serialization`, and `@JvmOverloads`/`@JvmName`/`@JvmStatic`/`@Throws` (`0068`)
- **13.5** Compose Compiler & Kotlin Runtime Mental Model: `@Immutable`/`@Stable`, Strong Skipping (`===` vs `==`), `rememberUpdatedState`, `snapshotFlow` (`0071`)
- **13.6** Multi-Module Architecture, `explicitApi()` & Pure-Kotlin DI (`0072`)
- **13.7** **[Optional · Staff SDK Internals]** Kotlin Contracts (`callsInPlace`, `returns(true) implies`), `@PublishedApi internal`, binary compatibility, and Android R8 (`0069`)
- **R13** 2027 Senior Kotlin & Android Mastery Review: Capstone offline-sync engine & 4-bug PR audit (`0070`)

---

## Reference Playbooks & Cheat Sheets (`reference/`)
- **Four Pillar Playbooks (Priority Order)**: `android.html` · `dsa-patterns.html` · `spring-boot.html` · `kmp.html` *(Optional)*
- **13 Section Reference Sheets**: `foundations.html` · `null-safety.html` · `functions.html` · `classes-modeling.html` · `lambdas.html` · `scope-functions.html` · `collections-dsa.html` · `delegation.html` · `generics.html` · `coroutines-flow.html` · `testing.html` · `smells-idioms.html` · `modern-kotlin-tooling.html`
