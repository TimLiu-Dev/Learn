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
