# Modern Android & KMP Engineering Resources

## Knowledge

- [Google Developers: Guide to App Architecture](https://developer.android.com/topic/architecture)
  The official architecture blueprint. Use for: UI layer, domain layer, offline-first SSOT data layer, and UDF principles.
- [Google: Now in Android (GitHub Repository)](https://github.com/android/nowinandroid)
  Production multi-module reference app. Use for: module boundaries, `build-logic` convention plugins, Version Catalogs, and testing.
- [Kotlin 2.x Documentation (Kotlinlang.org)](https://kotlinlang.org/docs/home.html)
  Official language and K2 compiler reference. Use for: Smart Cast 2.0, sealed interfaces, value classes, context parameters, and serialization.
- [Kotlin Coroutines & Flow Guide](https://kotlinlang.org/docs/coroutines-overview.html)
  Core concurrency specification. Use for: structured concurrency, `CancellationException` safety, cold Flows, `StateFlow`/`SharedFlow`, and Turbine testing.
- [Jetpack Compose Official Documentation](https://developer.android.com/jetpack/compose)
  Primary declarative UI guide. Use for: 3 frame phases, state hoisting, Strong Skipping, `Modifier.Node`, `WindowInsets`, Canvas, and accessibility.
- [Android Developers: Compose Text (`TextFieldState`) & Per-App Languages](https://developer.android.com/develop/ui/compose/text/user-input)
  Official guide to buffer-backed `TextFieldState`, `InputTransformation`/`OutputTransformation`, `WindowInsets.ime`, RTL, and `LocaleManager`. Use for: Lesson 0013 and all user input forms.
- [Android Developers: Jetpack Navigation 3 & Adaptive Apps](https://developer.android.com/guide/navigation/navigation-3)
  Compose-first user-owned back stack (`rememberNavBackStack`) and Material 3 Adaptive multi-pane layout guides. Use for: Phones, foldables, and Android 17 resizability.
- [Android Developers: Compose & View Interoperability](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis)
  Official brownfield migration guide. Use for: `ComposeView`, `ViewCompositionStrategy`, and pooled `AndroidView`.
- [Android Developers: 16 KB Page Sizes, Credential Manager & Play Integrity](https://developer.android.com/guide/practices/page-sizes)
  2027 Play Store compliance and trust guides. Use for: 16 KB ELF `.so` alignment, Passkeys, and payload-bound backend attestation.
- [Android Developers: Android Vitals, Perfetto & ApplicationExitInfo](https://developer.android.com/topic/performance/vitals)
  Production profiling and observability docs. Use for: 120Hz frame budgets, Baseline Profiles, `StrictMode`, and ANR/LMK exit forensics.
- [JetBrains & Google: Kotlin Multiplatform & Compose Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
  Official KMP and CMP guides. Use for: `commonMain` architecture, Ktor, Room KMP, Koin, SKIE Swift interop, `composeResources`, and `UIKitView`.
- [Android Developers: Android AICore & Gemini Nano](https://developer.android.com/ai/gemini-nano)
  Official ML Kit GenAI and AICore documentation. Use for: On-device NPU summarization, capability probing, and privacy-first hybrid routing.
- [Android Developers: Jetpack Media3, CameraX & Jetpack Glance](https://developer.android.com/media/media3)
  Rich media and system surface guides. Use for: Pooled `ExoPlayer`, `CameraX` UseCases, `GlanceAppWidget`, and Android 16/17 Promoted Live Updates.
- [Android Developers: Google Play Billing Library v7+](https://developer.android.com/google/play/billing/integrate)
  Official monetization and entitlement guide. Use for: Optional Lesson 0075 (`BillingClient`, backend `purchaseToken` verification, 3-day acknowledgment, RTDN).
- [Apollo Kotlin 4 & gRPC-Kotlin Documentation](https://www.apollographql.com/docs/kotlin/)
  Schema-first networking specifications. Use for: Optional Lesson 0076 (GraphQL normalized caching, fragments, and binary Protobuf/HTTP2 streaming).

## Wisdom (Communities)

- [Kotlinlang Public Slack (#android, #compose, #compose-ios, #multiplatform)](https://kotlinlang.slack.com)
  Direct access to JetBrains, Google, and OSS library maintainers. Use for: Compiler edge cases, KMP/CMP idioms, and architectural debates.
- [r/androiddev](https://reddit.com/r/androiddev)
  Practitioner forum for Android engineers. Use for: Production war stories, Play Console policy updates, and interview expectations.
- [Android Worldwide Community](https://androidworldwide.com)
  Global practitioner meetups and talks. Use for: Peer code reviews, study groups, and conference case studies.

## Gaps

- Real-world production benchmarks comparing Kotlin 2.2+ Context Parameters vs DI frameworks in 50+ module Android codebases (actively evolving ecosystem standard).
