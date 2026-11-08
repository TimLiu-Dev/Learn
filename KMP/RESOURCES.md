# Kotlin Multiplatform (KMP) Resources

## Knowledge

- [JetBrains Official Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
  Authoritative reference for Kotlin Multiplatform, Gradle configurations, source sets hierarchy, and Kotlin 2.x compiler updates.
- [Google Android Jetpack Multiplatform Documentation](https://developer.android.com/kotlin/multiplatform)
  Official guidance on Google Jetpack libraries ported to KMP (Room KMP, DataStore, Lifecycle, ViewModel, Navigation).
- [JetBrains Compose Multiplatform Documentation](https://www.jetbrains.com/lp/compose-multiplatform/)
  Primary source for declarative UI sharing across Android and iOS, rendering pipeline (Skia on iOS), and platform integration.
- [Touchlab Engineering Blog & Repositories](https://touchlab.co/blog)
  Premier industry authority on production KMP architecture, Swift interop, SKIE, memory diagnostics, and enterprise migration strategies.
- [Cash App Open Source (Square)](https://cashapp.github.io/)
  Architectural standard-bearers for KMP ecosystem: SQLDelight, Turbine (Flow testing), Molecule, and Zipline.
- [Ktor 3.x Official Documentation](https://ktor.io/)
  Authoritative guide for multiplatform HTTP client networking, streaming, serialization plugins, and platform engines (Darwin, OkHttp, CIO).

## Wisdom (Communities)

- [Kotlinlang Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
  The global epicentre for KMP developers. Primary channels: `#multiplatform`, `#compose-multiplatform`, `#skie`, `#touchlab-tools`.
- [r/Kotlin Subreddit](https://reddit.com/r/Kotlin)
  Active community for architecture discussions, library showcases, RFC discussions, and hiring trends.
- [Kotlin YouTube Channel (JetBrains)](https://www.youtube.com/@Kotlin)
  Keynotes, deep-dives into K2 compiler internals, and KotlinConf session archives.

## Gaps

- Swift Export (Direct Kotlin-to-Swift compiler plugin): Currently in experimental stabilization; we will track its evolution while anchoring production Swift interop to SKIE.
