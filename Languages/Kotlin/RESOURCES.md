# Kotlin Resources

Curated, high-trust sources grounding the idiomatic Kotlin curriculum across Android Development, Kotlin Multiplatform (KMP), Spring Boot Backend, and DSA Coding Interviews.

## Knowledge (Official & Primary Sources)

### Language Foundations & Core Idioms
- [Kotlin docs: Idioms](https://kotlinlang.org/docs/idioms.html)
  JetBrains' official list of idiomatic one-liners. Use for: the baseline for all refactoring exercises.
- [Kotlin docs: Coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
  Official style and idiom guidance (expression bodies, immutability, formatting).
- [Kotlin docs: Language Reference](https://kotlinlang.org/docs/basic-syntax.html)
  Definitive specification per feature: [null safety](https://kotlinlang.org/docs/null-safety.html), [functions](https://kotlinlang.org/docs/functions.html), [extensions](https://kotlinlang.org/docs/extensions.html), [classes](https://kotlinlang.org/docs/classes.html), [sealed](https://kotlinlang.org/docs/sealed-classes.html), [lambdas](https://kotlinlang.org/docs/lambdas.html), [scope functions](https://kotlinlang.org/docs/scope-functions.html), [collections](https://kotlinlang.org/docs/collections-overview.html), [delegation](https://kotlinlang.org/docs/delegation.html), [generics](https://kotlinlang.org/docs/generics.html), [coroutines](https://kotlinlang.org/docs/coroutines-overview.html).
- [Book: _Kotlin in Action_, 2nd ed. (Aigner, Elizarov, Isakova, Jemerov), Manning](https://www.manning.com/books/kotlin-in-action-second-edition)
  Written by Kotlin compiler and language designers. Deep explanations of compiler lowering and runtime trade-offs.
- [Book: _Effective Kotlin_ (Marcin Moskała), Kt. Academy](https://kt.academy/book/effectivekotlin)
  Best-practice engineering items ("prefer read-only", "use sealed hierarchies", "consider inline value classes").
- [Kotlin Koans](https://kotlinlang.org/docs/koans.html)
  Interactive official syntax challenges.
- [Kotlin Playground](https://play.kotlinlang.org)
  Official web execution engine.

### Pillar 1: Android Development & Jetpack Compose
- [Android Developers: Guide to App Architecture](https://developer.android.com/topic/architecture)
  Google's definitive modern guide: Unidirectional Data Flow, UI State, UI Events, and ViewModel layers.
- [Android Developers: State and Jetpack Compose](https://developer.android.com/develop/ui/compose/state)
  State hoisting, `remember`, `rememberSaveable`, `derivedStateOf`, and Compose stability rules.
- [Android Developers: Consuming Flows Safely in Jetpack Compose](https://developer.android.com/topic/libraries/architecture/coroutines)
  Why `collectAsStateWithLifecycle()` is mandatory to prevent background battery drain.
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
  Google's rules for Android codebases and library APIs.
- [Kotlin 2.2+ What's New & Context Parameters](https://kotlinlang.org/docs/whatsnew22.html)
  `when` guard conditions, non-local `break`/`continue`, and modern DSL / context APIs.
- [Gradle Version Catalogs (`libs.versions.toml`)](https://docs.gradle.org/current/userguide/platforms.html)
  Official reference for dependency aliases, bundles, and Kotlin DSL plugin blocks.
- [kotlinx.serialization JSON Configuration Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md)
  Production JSON tolerance (`ignoreUnknownKeys`, `coerceInputValues`, `@SerialName`).

### Pillar 2: Spring Boot Backend & Server-Side Kotlin
- [Spring Framework Reference: Kotlin Support](https://docs.spring.io/spring-framework/reference/languages/kotlin.html)
  Comprehensive official guide to Kotlin compiler plugins (`all-open`, `no-arg`), WebFlux Coroutines, and extension functions.
- [Spring Guides: Building Web Applications with Spring Boot and Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin)
  Canonical tutorial for DTOs, controllers, repository layers, and JUnit 5 testing in Kotlin.
- [Jackson Module Kotlin Documentation](https://github.com/FasterXML/jackson-module-kotlin)
  Essential details on constructor reflection, missing parameter exceptions, and null safety enforcement during JSON deserialization.

### Pillar 3: Kotlin Multiplatform (KMP)
- [Kotlin Multiplatform Official Documentation](https://kotlinlang.org/docs/multiplatform.html)
  Modern source set hierarchy, `expect`/`actual` mechanisms, and cross-platform compilation targets (JVM, iOS, Native, Wasm).
- [Ktor Client Documentation](https://ktor.io/docs/client-create-multiplatform-application.html)
  Multiplatform asynchronous HTTP engine configuration (`OkHttp` for Android, `Darwin` for iOS).
- [kotlinx.serialization Guide](https://github.com/Kotlin/kotlinx.serialization)
  Compile-time reflectionless JSON serialization for cross-platform data transfer.
- [kotlinx-datetime Guide](https://github.com/Kotlin/kotlinx-datetime)
  Multiplatform date, time, and clock manipulation.

### Pillar 4: DSA Coding Interviews
- [Kotlin Collections Standard Library Reference](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/)
  Complete documentation of inlined operators, `ArrayDeque`, `binarySearch()`, and `Sequence`.
- [LeetCode Kotlin Environment Reference](https://leetcode.com)
  Solving coding interview problems with zero-overhead `IntArray`, custom `compareBy` comparators, and bitwise infix operations.

## Wisdom (Communities & Code Review)

- [Kotlin Slack](https://kotlinlang.org/community/)
  Official JetBrains Slack featuring `#getting-started`, `#android`, `#spring`, and `#multiplatform` channels.
- [Kotlin Discussions Forum](https://discuss.kotlinlang.org)
  Searchable architectural discussions on language design and compiler behavior.
- [Reddit: r/Kotlin & r/androiddev](https://www.reddit.com/r/Kotlin/)
  Active practitioner communities for code reviews, idioms, and industry patterns.
- [Spring Community & GitHub Discussions](https://github.com/spring-projects/spring-framework/discussions)
  Expert backend discussions on reactive Spring, coroutines, and JPA integration.
