# Mission: Idiomatic Kotlin

## Why
I want full independence in Kotlin across four practical targets:
1. **Android development** (UI state, clean ViewModel/Repository architecture)
2. **Kotlin Multiplatform (KMP)** (shared business logic, `expect`/`actual` patterns)
3. **Spring Boot backend** (concise services, DTOs, domain models)
4. **DSA coding interviews** (algorithms, collections, Big-O trade-offs, LeetCode/HackerRank in Kotlin)

Right now AI writes most of my Kotlin and I can't write it myself from scratch. I want to write idiomatic Kotlin from a blank file, judge and fix AI/Java-flavoured code, and crack technical coding interviews with fluent Kotlin idioms.

## Success looks like
- Write complete features and data models from a blank file with AI assistance turned off
- Solve DSA coding interview problems fluently using Kotlin's standard library (collections, two pointers, recursion, `tailrec`, trees, graphs)
- Read and audit AI-generated or PR Kotlin at a **Senior Developer** level—spotting subtle concurrency, boxing, binary-compatibility, and cancellation bugs
- Comfortably read, write, and **deterministically test** scope functions, delegates, sealed hierarchies, coroutines (`runTest`), and flows (`Turbine`, `StateFlow.update`, `callbackFlow`)
- Make senior architectural decisions (`data class` vs `class` vs `value class`, `sealed` vs `enum` vs `Result<T>`, `StateFlow` vs `SharedFlow` vs `Channel`, eager `Iterable` vs lazy `Sequence`, common `interface` fakes vs `expect`/`actual`)

## Constraints
- Under 6 months of Kotlin; little or no Java, so Java comparisons only when they illuminate JVM bytecode or compiler behaviour
- Exercises should be directly editable in the lesson page (plain typing with auto-indent, zero external compiler dependencies); code will be run in a separate local IDE/playground
- Every exercise has a self-check "Model Answer" tab with idiomatic explanation
- One concept per session, with mixed interleaved reviews at the end of each section
