# 🎓 Kotlin Multiplatform (KMP) Master Learning Log & Progress Tracker

Use this document to track your progress through all 31 curriculum lessons and the Phase 10 Capstone Project. Check off boxes as you complete exercises, log your quiz scores, and record key insights.

---

## 📊 High-Level Roadmap Progress

- [ ] **Phase 0: Tooling & Modern Build Engine** (Lessons 01 - 03)
- [ ] **Phase 1: Core Architecture & Platform Interop** (Lessons 04 - 06)
- [ ] **Phase 2: Asynchronous Multiplatform Domain Layer** (Lessons 07 - 09)
- [ ] **Phase 3: Networking & Type-Safe APIs** (Lessons 10 - 12)
- [ ] **Phase 4: Local Storage & Offline-First Persistence** (Lessons 13 - 16)
- [ ] **Phase 5: Dependency Injection & Modular Architecture** (Lessons 17 - 18)
- [ ] **Phase 6: Shared UI with Compose Multiplatform** (Lessons 19 - 22)
- [ ] **Phase 7: Native iOS Interop & Dual-UI Mastery** (Lessons 23 - 25)
- [ ] **Phase 8: Comprehensive Testing & Quality Assurance** (Lessons 26 - 29)
- [ ] **Phase 9: Production CI/CD & Build Optimization** (Lessons 30 - 31)
- [ ] **Phase 10: Capstone Portfolio Project: "PulseSync"**

---

## 📝 Detailed Lesson Tracking Sheet

| Lesson | Title | Interactive Code | Quiz Score | Date Done | Key Takeaway / Notes |
| :---: | :--- | :---: | :---: | :---: | :--- |
| **01** | Anatomy of Modern KMP & Gradle | [ ] | / 2 | | Hierarchical source sets, Version Catalog, static XCFrameworks |
| **02** | Kotlin 2.x & K2 Compiler Pipeline | [ ] | / 2 | | FIR frontend, integrated Compose compiler plugin |
| **03** | Apple Toolchain & SPM vs CocoaPods | [ ] | / 2 | | Static vs dynamic linkage, SPM local package target |
| **04** | Multiplatform Architecture Mindset | [ ] | / 2 | | 80/20 rule, pure Kotlin domain layer in commonMain |
| **05** | expect/actual Mechanisms & Anti-Patterns | [ ] | / 2 | | Prefer interfaces + DI over raw expect/actual |
| **06** | Hardware & Native System Interop | [ ] | / 2 | | Android Keystore vs iOS Keychain security bridges |
| **07** | Multiplatform Coroutines & Concurrency | [ ] | / 2 | | New memory model, cooperative CancellationException |
| **08** | Reactive State with Flow & Channels | [ ] | / 2 | | StateFlow for UI state, Channels for one-off side effects |
| **09** | Bridging Coroutines to Swift with SKIE | [ ] | / 2 | | async/await, AsyncSequence, sealed interface to Swift enum |
| **10** | Ktor 3.x Client Architecture | [ ] | / 2 | | Darwin / OkHttp engine auto-resolution, ContentNegotiation |
| **11** | Kotlinx.Serialization & Advanced Parsing | [ ] | / 2 | | Polymorphic serialization, custom serializers for Instant |
| **12** | Production Network Resilience & Auth | [ ] | / 2 | | Ktor Auth plugin, dual-token refresh mutex, Result types |
| **13** | Room KMP vs SQLDelight Matrix | [ ] | / 2 | | Trade-offs: annotation-driven Room vs SQL-first SQLDelight |
| **14** | Jetpack Room KMP in Practice | [ ] | / 2 | | BundledSQLiteDriver, DAOs, schema export with KSP |
| **15** | Key-Value Storage & Multiplatform Settings | [ ] | / 2 | | Secure vs unencrypted preference storage |
| **16** | Offline-First Repository Pattern | [ ] | / 2 | | Single Source of Truth, optimistic UI, conflict resolution |
| **17** | Multiplatform DI with Koin | [ ] | / 2 | | koinViewModel, module wiring across Android and iOS |
| **18** | Compile-Time DI with Kotlin-Inject | [ ] | / 2 | | @Component, @Provides, zero-reflection validation |
| **19** | Compose Multiplatform Rendering Pipeline | [ ] | / 2 | | Skia on iOS, 120Hz ProMotion, UIKitView layer |
| **20** | Navigation in Compose Multiplatform | [ ] | / 2 | | Type-safe @Serializable routes, backstack handling |
| **21** | Design Systems, Theming & Adaptivity | [ ] | / 2 | | Material 3 Dynamic theming, adaptive Cupertino widgets |
| **22** | Image Loading & Multiplatform Resources | [ ] | / 2 | | Coil 3 async caching, Compose Resources (Res.string/drawable) |
| **23** | Embedding Compose into Existing iOS Apps | [ ] | / 2 | | ComposeUIViewController, safe areas, keyboard insets |
| **24** | Hosting Native UIKit/SwiftUI in Compose | [ ] | / 2 | | UIKitView interop for MapKit, AVPlayer, and Apple Pay |
| **25** | Dual-UI: Shared Logic + Native SwiftUI | [ ] | / 2 | | Shared KMP ViewModel bound to SwiftUI via SKIE |
| **26** | Multiplatform Unit Testing | [ ] | / 2 | | kotlin.test running on JVM and native iOS test runners |
| **27** | Testing Flows & Coroutines with Turbine | [ ] | / 2 | | Deterministic flow testing with awaitItem() & runTest |
| **28** | Mocking: Fakes vs. Mockative | [ ] | / 2 | | Ktor MockEngine, domain fakes on Kotlin/Native |
| **29** | Compose Multiplatform UI Testing | [ ] | / 2 | | Semantic tree node matching, user click simulations |
| **30** | Gradle Build Optimization & Caching | [ ] | / 2 | | Configuration cache, Kotlin daemon, native link time tuning |
| **31** | GitHub Actions Multiplatform Matrix CI | [ ] | / 2 | | macOS & Ubuntu matrix, Fastlane signing, AAB & XCFramework |

---

## 🏆 Capstone Project ("PulseSync") Milestones

- [ ] **Milestone 1**: Multiplatform Monorepo Architecture & Version Catalog setup (`build-logic`, `:core:*`, `:feature:*`)
- [ ] **Milestone 2**: Ktor 3 Network Client + Jetpack Room KMP Offline-First Data Layer
- [ ] **Milestone 3**: Domain UseCases & MVI ViewModels with Turbine test suites
- [ ] **Milestone 4**: Compose Multiplatform UI (Material 3 Adaptive) + Native iOS SwiftUI Widget via SKIE
- [ ] **Milestone 5**: GitHub Actions Matrix CI/CD pipeline building Android AAB and iOS test framework

---

## 💡 Tricky Concepts & Aha! Moments Log

Use this space to jot down non-obvious concepts you encounter during lessons:

### Example:
- **Date**: 2026-11-07
- **Lesson**: 01 (Anatomy of KMP)
- **Concept**: *Dynamic vs Static iOS Frameworks*
- **Aha! Insight**: Dynamic frameworks require `dyld` lookup on iOS cold start; static frameworks (`isStatic = true`) are embedded directly into the binary, avoiding launch delays and code-signing nesting bugs.
