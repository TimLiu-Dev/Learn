# Mission: Idiomatic Kotlin

## Why
I want full independence in Kotlin across four practical targets, prioritized in this order:
1. **Android development** (Jetpack Compose UI state, clean ViewModel/Repository UDF architecture, multi-module DI)
2. **DSA coding interviews** (algorithms, collections, strings/chars, trees, graphs, DP, Big-O trade-offs, LeetCode/HackerRank in Kotlin)
3. **Spring Boot backend** (concise services, DTO validation, JPA entity contracts)
4. **Kotlin Multiplatform (KMP)** *(Optional / Secondary track)* (shared `commonMain` business logic, common interfaces vs `expect`/`actual`)

Right now AI writes most of my Kotlin and I can't write it myself from scratch. I want to write idiomatic Kotlin from a blank file, judge and fix AI/Java-flavoured code, and crack technical coding interviews with fluent Kotlin idioms.

## Success looks like
- Write complete features, tests, and data models from a blank file with AI assistance turned off
- Solve DSA coding interview problems fluently using Kotlin's standard library (primitive arrays, strings/chars, two pointers, heaps/deques, recursion, `tailrec`, `DeepRecursiveFunction`, trees, graphs, and memoized DP)
- Read and audit AI-generated or PR Kotlin at a **Senior Developer** level—spotting subtle concurrency, Compose stability, boxing, cancellation, and state-mutation bugs
- Comfortably read, write, and **deterministically test** scope functions, delegates, sealed hierarchies, coroutines (`runTest`), and flows (`Turbine`, `StateFlow.update`, `callbackFlow`)
- Make senior architectural decisions (`data class` vs `class` vs `value class`, `sealed` vs `enum` vs `Result<T>`, `StateFlow` vs `SharedFlow` vs `Channel`, eager `Iterable` vs lazy `Sequence`, common `interface` fakes vs `expect`/`actual`)

## Constraints
- Under 6 months of Kotlin; little or no Java, so Java comparisons only when they illuminate JVM bytecode or compiler behaviour
- Exercises are directly editable and runnable in-browser via local `./serve.sh` (`kotlinc 2.4`) with automatic `localStorage` progress & draft saving
- Every exercise has progressive hints and a self-check "Model Answer" tab with idiomatic explanation
- One tightly-scoped concept per session, with mixed interleaved reviews at the end of each section

## Out of scope
- Legacy Android XML / `Fragment` / `LiveData` / `AsyncTask` patterns (except brief `@JvmOverloads` interop context)
- Deep compiler-plugin / KSP code-generator authoring or obscure SDK bytecode tricks on the main path (Staff SDK internals like Contracts/`@PublishedApi` and KMP are marked `[Optional]`)
- Heavy functional programming frameworks (Arrow-kt monads) beyond standard library idioms
