# Kotlin Glossary

Canonical definitions for the idiomatic Kotlin workspace covering language core, Android, Kotlin Multiplatform, Spring Boot, and DSA coding interviews.

## Core Language & Type System

**Expression**:
A syntactic construct that evaluates to a concrete value. In Kotlin, `if`, `when`, and `try` are expressions rather than statements.
_Avoid_: Statement, ternary operator

**Smart cast**:
A compiler optimization that automatically casts an object reference to a specific subtype after a runtime type or null check within that execution scope.
_Avoid_: Explicit cast, `as` casting

**Platform type (`T!`)**:
A type originating from Java or foreign code whose nullability is unknown to the Kotlin compiler, placing null-safety responsibility entirely on the developer.
_Avoid_: Unsafe type, dynamic type

**Backing property**:
A software design pattern where a private mutable property (`_state`) is exposed publicly via an immutable read-only property (`state`).
_Avoid_: Public mutable variable, getter method

**Value class (`@JvmInline value class`)**:
A zero-cost compile-time type wrapper that adds domain-specific type safety around an underlying primitive or reference without runtime heap allocation.
_Avoid_: Boxed wrapper, typealias (which lacks type safety)

**Reified type parameter**:
A generic type parameter available at runtime inside an `inline` function, overcoming JVM bytecode type erasure.
_Avoid_: Class token parameter, `Class<T>` argument

**Scope function**:
A standard library higher-order function (`let`, `apply`, `run`, `also`, `with`) that executes a block of code within the context of an object.
_Avoid_: Helper block, utility wrapper

## Android Development

**Unidirectional Data Flow (UDF)**:
An architectural pattern where state flows downward from state holders (ViewModels) to UI composables, and events flow upward from the UI to state holders.
_Avoid_: Two-way data binding, mutable activity state

**UI State**:
An immutable data model—typically a `sealed interface`—representing the complete, deterministic visual state of a screen at any single moment in time.
_Avoid_: Multi-boolean state flags (`isLoading`, `isError`)

**One-off UI Effect**:
A transient user action or alert (such as a snackbar or navigation event) that must be consumed exactly once and not re-emitted on screen recomposition or device rotation.
_Avoid_: Replayed state event, boolean alert flag

**StateFlow**:
A hot, state-holding observable flow that continuously emits the current state to collectors and conflates duplicate values.
_Avoid_: LiveData (legacy), plain Channel for state

**Lifecycle-aware collection (`collectAsStateWithLifecycle`)**:
A Compose flow collection API that automatically pauses and cancels collection when the Android Activity or Fragment is in the background.
_Avoid_: `collectAsState()` without lifecycle checks

## Spring Boot & Server-Side Kotlin

**Use-site target annotation**:
An explicit annotation prefix (`@field:`, `@get:`, `@param:`) specifying which underlying bytecode artifact an annotation attaches to when targeting a Kotlin property.
_Avoid_: Naked property annotation on DTOs

**Compiler open plugin (`kotlin-spring` / `all-open`)**:
A Kotlin compiler plugin that automatically adds the `open` modifier to classes and methods annotated with Spring components so CGLIB proxies can subclass them.
_Avoid_: Manual `open` keyword on every Spring service

**Compiler no-arg plugin (`kotlin-jpa` / `no-arg`)**:
A Kotlin compiler plugin that synthesizes a zero-argument bytecode constructor required by JPA Hibernate reflection without altering source code.
_Avoid_: Dummy default constructor values on domain models

## Kotlin Multiplatform (KMP)

**Source set hierarchy**:
A directed acyclic structure of code folders where target-specific code (`iosMain`, `androidMain`) inherits from intermediate or common source sets (`commonMain`).
_Avoid_: Monolithic common code, duplicate target copies

**Expect / Actual declaration**:
A Kotlin language mechanism where a common source set declares a signature using `expect` and each target platform source set supplies the implementation using `actual`.
_Avoid_: Platform reflection hooks, C preprocessor `#ifdef`

## DSA Coding Interviews

**Unboxed primitive array (`IntArray`)**:
A flat contiguous array of native 32-bit integers (`int[]`) compiled without Java `Integer` object wrapper allocations, minimizing garbage collection pressure.
_Avoid_: `Array<Int>`, boxed `List<Int>` in tight loops

**Double-ended queue (`ArrayDeque`)**:
A resizable circular array data structure providing amortized $O(1)$ operations at both ends for FIFO queues and LIFO stacks.
_Avoid_: `java.util.Stack` (synchronized overhead), `LinkedList` (pointer cache misses)

**Infix bitwise operator**:
A Kotlin keyword function (`shl`, `shr`, `and`, `or`, `xor`, `inv`) used in place of traditional symbolic operators for low-level binary manipulation.
_Avoid_: Symbolic operators like `<<` or `&` (invalid in Kotlin)

## Modern Kotlin (2.2–2.4), DSLs & Tooling

**`@DslMarker`**:
A meta-annotation applied to builder DSL receiver classes that prevents inner lambda scopes from implicitly calling methods on outer receivers of the same DSL family.
_Avoid_: Unscoped nested `apply` chains where outer `this` leaks into inner blocks

**`callbackFlow` & `awaitClose`**:
A cold `Flow` builder that bridges callback/listener APIs into coroutines using thread-safe `trySend(...)` and suspends on a mandatory `awaitClose { ... }` block to unregister listeners upon cancellation.
_Avoid_: Calling `emit()` inside external listener threads or omitting `awaitClose`

**Kotlin Contract (`contract { ... }`)**:
A compile-time declaration inside an `inline` or validator function (`callsInPlace(block, EXACTLY_ONCE)` or `returns(true) implies (x != null)`) that informs the compiler's control-flow and smart-cast analyzer about runtime guarantees.
_Avoid_: Forcing callers to use `lateinit var` or `!!` after custom validation helpers

**`@PublishedApi internal`**:
A visibility modifier bridge that allows a `public inline` function (whose body is copied into external caller modules) to reference `internal` class members without exposing them in normal source autocomplete.
_Avoid_: Making internal backing maps `public` just to support `inline fun <reified T>`

**Gradle Version Catalog (`gradle/libs.versions.toml`)**:
A centralized TOML file declaring `[versions]`, `[libraries]`, `[bundles]`, and `[plugins]` that Gradle compiles into type-safe Kotlin DSL accessors (`libs.kotlinx.coroutines.android`).
_Avoid_: Hardcoding duplicate dependency version strings across multiple `build.gradle.kts` files

**R8 Shrinking & Obfuscation**:
Android's release build optimizer (`isMinifyEnabled = true`) that strips unreachable code (tree shaking), renames classes/fields to short identifiers (`a.b`), and optimizes bytecode; requires compile-time codegen (`@Serializable`, KSP) or explicit `@Keep` rules when using reflection.
_Avoid_: Unannotated reflection DTOs that work in `debug` but deserialize `null` in `release`

**Strong Skipping Mode (Compose Compiler)**:
The Kotlin 2.0.20+ Compose Compiler mode where stable parameters skip recomposition via structural equality (`old == new`) and unstable parameters skip via referential identity (`old === new`).
_Avoid_: Mutating `MutableList` properties in place inside UI state objects

**`rememberUpdatedState`**:
A Compose state-holder helper that updates an internal reference box on every recomposition so long-lived coroutines (`LaunchedEffect(Unit)`) always invoke the latest callback lambda without restarting the effect.
_Avoid_: Capturing raw callback parameters directly inside non-restarting `LaunchedEffect(Unit)` blocks

**`snapshotFlow`**:
A bridge function that observes Compose Snapshot `State<T>` reads inside its block and emits changes as a cold Kotlin `Flow<T>` with automatic `distinctUntilChanged()` deduplication.
_Avoid_: Triggering suspending network/analytics side effects directly inside synchronous `derivedStateOf` blocks

**Explicit API Mode (`kotlin { explicitApi() }`)**:
A Kotlin compiler mode for shared and multi-module libraries that requires explicit visibility modifiers (`public`/`internal`) and explicit return types on every public declaration.
_Avoid_: Accidental `public` exposure of internal implementation classes or silent ABI breaks from inferred return types

**Assisted Factory (`fun interface` DI Factory)**:
A single-abstract-method factory interface pre-bound to long-lived singletons in a Dependency Injection graph while accepting caller-supplied runtime arguments (such as screen navigation IDs).
_Avoid_: Passing runtime navigation IDs through global mutable singletons
