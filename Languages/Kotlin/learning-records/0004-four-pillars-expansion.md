# Four Pillars Production Expansion & Audit

A comprehensive audit of the curriculum across Android Development, Kotlin Multiplatform (KMP), Spring Boot Backend, and DSA Coding Interviews established key domain-specific gaps, leading to the creation of 4 dedicated reference playbooks, the canonical Glossary, and enriched core lessons.

**Implications**:
- **DSA Coding Interviews**: Added explicit coverage of Kotlin-specific infix bitwise operators (`shl`, `shr`, `and`, `or`, `xor`, `inv`), 2D unboxed primitive matrices, and canonical pattern templates (Two Pointers, Monotonic Stack, Bisect Binary Search, Island BFS/DFS, Union-Find) in `reference/dsa-patterns.html` and Lesson 0037.
- **Android Development**: Added Jetpack Compose state mechanics (`remember`, `rememberSaveable`, `derivedStateOf`), Compose compiler stability contracts (`@Stable`/`@Immutable`), lifecycle-aware flow collection (`collectAsStateWithLifecycle`), and one-off effect channels (`Channel<UiEffect>`) in `reference/android.html` and Lesson 0057.
- **Spring Boot Backend**: Established mandatory coverage of Kotlin compiler plugins (`kotlin-spring`/`all-open`, `kotlin-jpa`/`no-arg`), annotation use-site targets (`@field:NotBlank`), JPA entity implementation patterns (avoiding `data class`), and WebFlux coroutines in `reference/spring-boot.html` and Lesson 0058.
- **Kotlin Multiplatform (KMP)**: Clarified Kotlin 2.0 source set hierarchy, best practices favoring interfaces + DI over `expect class`, and the multiplatform library stack (`kotlinx.serialization`, `Ktor Client`, `kotlinx-datetime`) in `reference/kmp.html` and Lesson 0060.
- Established canonical `GLOSSARY.md` and updated `RESOURCES.md` with official, primary documentation across all four target pillars.
