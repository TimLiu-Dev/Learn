# Curriculum: Idiomatic Kotlin

Draft roadmap. One lesson per session; **R** = mixed review that ends each section.
Items marked ➕ were added to your original scope.

**Every lesson follows the same shape:**
1. Warm-up: 2–3 recall questions from earlier lessons (spaced practice)
2. The concept: what problem it solves, why the syntax looks the way it does
3. Quick check: a short quiz with instant feedback
4. Practice in Kotlin Playground: one **write-from-blank** task plus one or two **refactors** of AI/Java-flavoured Kotlin. You paste your answers back to me for review.

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

## 7 · Collections
- **7.1** Read-only vs mutable (read-only isn't immutable), `listOf`, ➕ `buildList`
- **7.2** Transforming: `map`, `mapNotNull`, `flatMap`, `filter`, `associate`/`associateBy`
- **7.3** Grouping and aggregating: `groupBy`, `partition`, `fold`/`reduce`, `sumOf`, `count`
- **7.4** Finding and ordering: `firstOrNull`, `any`/`all`/`none`, `sortedBy`, `maxByOrNull`, `chunked`/`windowed`
- **7.5** ➕ Sequences: when lazy evaluation matters
- **R7** Mixed review

## 8 · Delegation
- **8.1** `by lazy`, and `lazy` vs `lateinit`
- **8.2** Delegated properties: how `by` works (`getValue`), `observable`, and the ones you already use (`by viewModels()`, `by remember`)
- **8.3** Class delegation: composition instead of inheritance
- **R8** Mixed review

## 9 · Idiomatic vs AI/Java-flavoured Kotlin (capstone)
- **9.1** ➕ Error handling: no checked exceptions, `require`/`check`/`error`, `runCatching`/`Result`
- **9.2** Smell catalogue: spot and name 15 common non-idiomatic patterns
- **9.3** Capstone A (Android): write a small screen's state + ViewModel-ish logic from blank, with AI turned off
- **9.4** Capstone B (Spring-flavoured): refactor a service/DTO layer
- **R9** Cumulative review across everything

---

## Reference sheets built along the way (`reference/`)
Syntax cheat sheet · Null-safety operators · Scope function decision table · Collections operations by intent · Smell catalogue (AI/Java-flavoured → idiomatic) · Glossary

## Later tracks (out of scope for now)
➕ Generics (`in`/`out`, `reified`) · Coroutines and Flow · Compose idioms · Spring Boot with Kotlin · KMP (`expect`/`actual`)
