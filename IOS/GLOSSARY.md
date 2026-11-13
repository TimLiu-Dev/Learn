# iOS & Swift Systems Engineering Glossary

The canonical definitions for memory, compiler, concurrency, and UI systems in this workspace.

## Memory & Runtime

**Automatic Reference Counting (ARC)**:
Deterministic, compile-time reference counting that inserts retain and release calls; frees instances immediately when their strong count reaches zero without garbage collection pauses.
_Avoid_: GC, Tracing collector

**Retain Cycle**:
A condition where two or more class instances hold strong references to each other, permanently preventing reference counts from hitting zero and leaking memory.
_Avoid_: Memory loop, circular pointer

**Copy-on-Write (CoW)**:
Optimization where value types share an underlying heap buffer until a mutation occurs; clones storage just-in-time only if the buffer is shared.
_Avoid_: Lazy cloning, deep copy on assign

**Spare Bit Optimization**:
Compiler layout optimization that stores enum discriminators (such as Optional `.none`) inside unused bit patterns of pointers (like `0x0`) with zero memory overhead.
_Avoid_: Null compression, pointer packing

**Law of Exclusivity**:
Swift language rule (SE-0176) mandating that two accesses to the same memory location cannot overlap in time if at least one access is a write.
_Avoid_: Memory locking, write exclusion

**Existential Container**:
A 5-word memory structure (3-word value buffer + VWT + PWT) used to represent `any Protocol` values at runtime, boxing larger payloads onto the heap.
_Avoid_: Boxed interface, protocol wrapper

## Method Dispatch & Polymorphism

**Static Dispatch**:
Direct function invocation where the compiler emits the target memory address at compile time, enabling inlining with zero runtime lookup.
_Avoid_: Direct call, hardcoded jump

**Protocol Witness Table (PWT)**:
A lookup table of function pointers mapping a concrete type's implementations to a protocol's required methods.
_Avoid_: Interface vtable, witness list

**Value Witness Table (VWT)**:
A runtime table containing pointers to fundamental operations for a type: allocate, copy, destroy, and deallocate.
_Avoid_: Type lifecycle table

**Opaque Return Type (`some P`)**:
A fixed, single concrete type determined at compile time whose specific identity is hidden from the caller, preserving static dispatch and type identity.
_Avoid_: Abstract return, generic protocol

## Concurrency

**Actor**:
A reference type that protects its mutable state through automated serialization, guaranteeing that only one task accesses its state at any given time.
_Avoid_: Synchronized class, thread monitor

**Actor Reentrancy**:
The behavior where an actor yields its thread at an `await` suspension point, allowing other tasks to run methods and potentially alter state before resumption.
_Avoid_: Thread hopping bug, async race

**Sendable**:
A marker protocol indicating that a type's values can be safely transferred across concurrency domains and thread boundaries without data races.
_Avoid_: Thread-safe interface, concurrent value

**Cooperative Thread Pool**:
The Darwin runtime thread pool (bounded to the machine's CPU core count) executing Swift Tasks cooperatively without thread explosion.
_Avoid_: Grand Central Dispatch queue, thread executor

## UI & Architecture

**AttributeGraph**:
SwiftUI's internal directed acyclic dependency graph that tracks state properties and determines minimum invalidation passes during rendering.
_Avoid_: Virtual DOM, Compose slot table

**Structural Identity**:
View identity determined implicitly by the view's static position within the SwiftUI hierarchy type tree.
_Avoid_: Implicit ID, layout position

**Explicit Identity**:
View identity explicitly assigned using `.id(...)` or `Identifiable`, whose mutation completely destroys and recreates view state.
_Avoid_: Key identifier, element tag

**MetricKit**:
Apple's first-party telemetry framework delivering aggregated production diagnostics (hang rate, CPU time, thermal throttling, dirty memory) directly on-device.
_Avoid_: APM analytics, crash reporter

## 2027 Frontier & Platform Systems

**`nonisolated(nonsending)` (SE-0461)**:
Swift 6.2+ default behavior where nonisolated async functions execute in the caller's actor/isolation context rather than hopping to the global concurrent executor unless marked `@concurrent`.
_Avoid_: Implicit background thread, auto-dispatch

**Noncopyable Type (`~Copyable`)**:
A value type whose ownership cannot be implicitly duplicated; enforces unique ownership via `consuming` and `borrowing` parameters and deterministic `deinit` cleanup.
_Avoid_: Singleton struct, move-only class

**`InlineArray` & `Span<T>`**:
Fixed-size stack-allocated array (`InlineArray`) and non-escapable zero-copy contiguous buffer view (`Span<T>`) eliminating heap allocation and ARC overhead in hot loops.
_Avoid_: UnsafeBufferPointer wrapper, raw C array

**XCFramework**:
Apple's multi-architecture binary bundle format packaging device (`arm64`) and simulator (`arm64`/`x86_64`) slices with optional `.swiftinterface` files for module stability.
_Avoid_: Fat binary, universal lipo framework

**FoundationModels (`@Generable`)**:
Apple's Swift framework for invoking on-device and Private Cloud Compute LLMs with compile-time schema constraints (`@Generable`) and Swift tool calling.
_Avoid_: Cloud REST wrapper, local weights loader

**AppIntent & AppEntity**:
Declarative Swift types exposing app capabilities and domain objects to Siri, Spotlight, Shortcuts, Widgets, and Control Center.
_Avoid_: Deep link URL handler, SiriKit legacy intent

**StoreKit 2 `VerificationResult` (JWS)**:
Cryptographically signed JSON Web Signature payload verified locally by StoreKit 2 without requiring a custom receipt-validation server.
_Avoid_: Legacy receipt ASN.1 blob
