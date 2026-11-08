# Kotlin Multiplatform (KMP) Master Plan & Course Legend

Welcome to the end-to-end Kotlin Multiplatform (KMP) Master Curriculum. This roadmap is engineered to take you from foundational multiplatform concepts to architectural leadership, equipped with the 2026/2027 industry-standard tech stack.

- ⚡ **Environment Preflight**: Before Lesson 01, review [PREFLIGHT.md](PREFLIGHT.md) or [`reference/00-environment-preflight.html`](reference/00-environment-preflight.html).
- 📊 **Progress Tracker**: Track your lesson completions and quiz scores in [`learning-records/PROGRESS_TRACKER.md`](learning-records/PROGRESS_TRACKER.md).
- 🏆 **Capstone Blueprint**: Explore the architectural plan for Phase 10 in [`reference/capstone-pulsesync-blueprint.html`](reference/capstone-pulsesync-blueprint.html).

---

## 🧭 Course Legend & Lesson Anatomy

Every lesson in this curriculum is authored as a standalone, clean HTML document in `./lessons/` (`0001-*.html`), linked to our shared `./assets/style.css`. 

Each lesson follows a strict pedagogical framework:
1. **The Why & Core Concept**: Real-world context and architecture mental model.
2. **Production Code Examples**: Idiomatic Kotlin 2.x snippets with inline line-by-line annotations.
3. **Common Pitfalls & Anti-Patterns**: What breaks in production and how senior engineers avoid it.
4. **Hands-On Coding Practice**: A concrete coding task or test you implement in this workspace.
5. **Retrieval Practice (Quiz)**: Quick-fire recall checks designed to build storage strength.
6. **Primary Source Citation**: Direct link to the highest-trust official documentation or engineering blog.
7. **Reference Artifact**: Corresponding cheatsheet or architecture decision in `./reference/`.

---

## 🗺️ Master Curriculum Roadmap

### Phase 0: Tooling & The Modern KMP Build Engine
*Goal: Demystify the multiplatform project layout, Gradle Kotlin DSL, and build mechanics.*

- **Lesson 01: Anatomy of a Modern KMP Project**
  - *Topic*: Multiplatform project layout, source set hierarchy (`commonMain`, `androidMain`, `iosMain`, `iosArm64`, `iosSimulatorArm64`), and Gradle Version Catalogs (`libs.versions.toml`).
  - *Practice*: Configure a clean multiplatform Gradle build with hierarchical source sets and version catalogs from scratch.
  - *Reference*: `reference/01-gradle-kmp-anatomy.html`

- **Lesson 02: Kotlin 2.x & The K2 Compiler Pipeline**
  - *Topic*: K2 compiler architecture, new frontend IR generation, Compose compiler integration directly into Kotlin Gradle Plugin, and compiler flags.
  - *Practice*: Enable and optimize K2 build options, diagnosing compile-time compatibility.
  - *Reference*: `reference/02-k2-compiler-flags.html`

- **Lesson 03: The Apple Toolchain & CocoaPods vs. SPM vs. Direct Frameworks**
  - *Topic*: How Kotlin Native compiles to Apple targets: static vs dynamic frameworks, XCFrameworks, `kdoctor`, Swift Package Manager (SPM) integration vs CocoaPods.
  - *Practice*: Set up an automated SPM configuration exporting an XCFramework to an Xcode project.
  - *Reference*: `reference/03-apple-xcframework-spm.html`

---

### Phase 1: Core Architecture & Platform Interop
*Goal: Master platform boundaries and learn when to share vs. when to specialize.*

- **Lesson 04: The Multiplatform Architecture Mindset**
  - *Topic*: Clean Architecture in KMP: Unidirectional Data Flow (UDF), Layer separation (Data, Domain, Presentation). What to share (business rules, state, networking, persistence) vs. what to keep platform-native.
  - *Practice*: Design an architectural boundary diagram and interface contracts for a real feature.
  - *Reference*: `reference/04-kmp-clean-architecture.html`

- **Lesson 05: The `expect`/`actual` Mechanism & Anti-Patterns**
  - *Topic*: Syntax, rules, expected declarations, and why overusing `expect`/`actual` is an anti-pattern. Preferring polymorphism and Dependency Injection over raw platform branching.
  - *Practice*: Refactor a fragile `expect`/`actual` implementation into a clean interface with platform-specific DI providers.
  - *Reference*: `reference/05-expect-actual-best-practices.html`

- **Lesson 06: Hardware & Native System Interop**
  - *Topic*: Accessing platform-native APIs: Secure key storage (Android Keystore vs iOS Keychain), connectivity observers, device info, and platform-specific Dispatchers.
  - *Practice*: Implement a secure token storage service using Keychain on iOS and EncryptedSharedPreferences/Keystore on Android.
  - *Reference*: `reference/06-secure-storage-bridges.html`

---

### Phase 2: Asynchronous Multiplatform Domain Layer
*Goal: Build a high-performance reactive domain engine using Coroutines and Flow across platforms.*

- **Lesson 07: Multiplatform Coroutines & Concurrency**
  - *Topic*: Coroutines across JVM and Kotlin/Native. Structured concurrency, lifecycle cancellation, avoiding memory leaks, and Dispatchers (`Dispatchers.Default`, `Dispatchers.Main`, `Dispatchers.IO`).
  - *Practice*: Implement a thread-safe, cancellable background sync worker in `commonMain`.
  - *Reference*: `reference/07-coroutines-native-concurrency.html`

- **Lesson 08: Reactive State with Flow & Channels**
  - *Topic*: `StateFlow`, `SharedFlow`, Channels, Cold vs. Hot streams, and building a multiplatform `BaseViewModel` with unidirectional state machines (`UiState`, `UiIntent`, `UiEffect`).
  - *Practice*: Build a robust multiplatform ViewModel state machine handling loading, pagination, and error states.
  - *Reference*: `reference/08-stateflow-mvi-patterns.html`

- **Lesson 09: Bridging Kotlin Coroutines to Swift with SKIE**
  - *Topic*: The Swift/Kotlin async boundary: Why default Kotlin/Native generates unwieldy completion blocks in Swift. How SKIE transforms `suspend` into Swift `async/await` and `StateFlow` into Swift `@Observable` / `AsyncSequence`.
  - *Practice*: Configure SKIE and write a Swift test consuming a Kotlin `StateFlow` seamlessly as an `AsyncSequence`.
  - *Reference*: `reference/09-skie-swift-concurrency.html`

---

### Phase 3: Networking & Type-Safe APIs
*Goal: Construct an enterprise-grade network client handling streaming, caching, and token refresh.*

- **Lesson 10: Ktor 3.x Client Architecture**
  - *Topic*: Ktor 3.x client setup, engine selection (OkHttp on Android, Darwin on iOS, CIO), plugins (`ContentNegotiation`, `HttpTimeout`, `Logging`).
  - *Practice*: Configure a multiplatform Ktor 3 client with automatic Darwin/OkHttp engine resolution and JSON serialization.
  - *Reference*: `reference/10-ktor3-client-setup.html`

- **Lesson 11: Kotlinx.Serialization & Advanced Data Parsing**
  - *Topic*: `@Serializable`, polymorphic serialization, custom serializers for dates and UUIDs, snake_case vs camelCase mapping, handling partial failures and missing fields.
  - *Practice*: Write custom serializers for ISO-8601 instant timestamps and polymorphic server event streams.
  - *Reference*: `reference/11-kotlinx-serialization-recipes.html`

- **Lesson 12: Production Network Resilience & Authentication**
  - *Topic*: Auto-refreshing OAuth2 tokens using Ktor's `Auth` plugin, handling 401s, exponential backoff retries, and mapping HTTP exceptions into domain Result types (`Result<T, AppError>`).
  - *Practice*: Implement a dual-token (Access + Refresh) interceptor with thread-safe refresh mutex.
  - *Reference*: `reference/12-ktor-auth-token-refresh.html`

---

### Phase 4: Local Storage & Offline-First Persistence
*Goal: Architect an offline-first Single-Source-of-Truth database layer.*

- **Lesson 13: Room KMP vs. SQLDelight Comparison**
  - *Topic*: Architectural trade-offs between Google's official Room KMP (Jetpack) and Cash App's SQLDelight. When to choose annotation-driven Room vs. SQL-first SQLDelight.
  - *Practice*: Evaluate a feature specification and create a technical decision matrix justifying the storage engine.
  - *Reference*: `reference/13-persistence-engine-matrix.html`

- **Lesson 14: Jetpack Room KMP in Practice**
  - *Topic*: Configuring Room in `commonMain`: SQLite driver setup (`BundledSQLiteDriver`), `@Database`, `@Dao`, `@Entity`, Flow queries, schema exports, and migrations.
  - *Practice*: Build a Room KMP database with reactive Flow queries and schema versioning.
  - *Reference*: `reference/14-room-kmp-setup.html`

- **Lesson 15: Key-Value Storage & Multiplatform Settings**
  - *Topic*: Multiplatform Settings / DataStore Multiplatform for preferences, settings, and flags. Secure vs unencrypted storage.
  - *Practice*: Implement a type-safe AppSettings repository backed by Multiplatform Settings.
  - *Reference*: `reference/15-multiplatform-settings.html`

- **Lesson 16: The Offline-First Repository Pattern**
  - *Topic*: Single Source of Truth (SSOT), network-bound resources, optimistic UI updates, conflict resolution, and background sync queuing.
  - *Practice*: Build a complete `OfflineFirstRepository` that streams local cache immediately, fetches remote updates, and reconciles conflicts.
  - *Reference*: `reference/16-offline-first-sync-engine.html`

---

### Phase 5: Dependency Injection & Modular Architecture
*Goal: Wire complex multiplatform applications with zero runtime crashes.*

- **Lesson 17: Multiplatform DI with Koin**
  - *Topic*: Koin Core for KMP, `module`, `single`, `factory`, platform-specific module injection, and Koin Compose Multiplatform integration (`koinViewModel()`).
  - *Practice*: Wire a full stack (Network -> DB -> Repository -> ViewModel) across Android and iOS targets using Koin.
  - *Reference*: `reference/17-koin-kmp-guide.html`

- **Lesson 18: Compile-Time DI with Kotlin-Inject**
  - *Topic*: The Dagger/Anvil equivalent for KMP: Kotlin-Inject. `@Component`, `@Provides`, `@Inject`, compile-time validation, and avoiding runtime service locator overhead.
  - *Practice*: Build a component graph with compile-time dependency injection using Kotlin-Inject.
  - *Reference*: `reference/18-kotlin-inject-recipes.html`

---

### Phase 6: Shared UI with Compose Multiplatform
*Goal: Build gorgeous, fluid, cross-platform user interfaces rendered natively via Skia on iOS.*

- **Lesson 19: Compose Multiplatform Core & Rendering Pipeline**
  - *Topic*: Compose Multiplatform architecture: How Skia renders Compose on iOS (`UIKitView`, `ComposeUIViewController`). Managing 120Hz ProMotion displays and gestures.
  - *Practice*: Create a responsive, high-performance UI component that adapts flawlessly to Android and iOS screen dimensions.
  - *Reference*: `reference/19-compose-multiplatform-rendering.html`

- **Lesson 20: Navigation in Compose Multiplatform**
  - *Topic*: Jetpack Navigation Compose Multiplatform vs. Decompose. Type-safe navigation routes, backstack state preservation, and nested navigation graphs.
  - *Practice*: Implement type-safe navigation with deep linking and backstack restoration across screen rotations.
  - *Reference*: `reference/20-navigation-multiplatform.html`

- **Lesson 21: Design Systems, Theming & Platform Adaptivity**
  - *Topic*: Material 3 theming, dark mode, dynamic color, and creating adaptive UI components that adopt Cupertino aesthetics on iOS and Material on Android.
  - *Practice*: Build a unified design system with adaptive buttons, dialogs, and navigation bars.
  - *Reference*: `reference/21-adaptive-design-systems.html`

- **Lesson 22: Image Loading & Multiplatform Resources**
  - *Topic*: Coil 3 (Multiplatform Coil), asynchronous image caching, SVG/vector support, and Compose Multiplatform resource generation (strings, fonts, drawables).
  - *Practice*: Build an image gallery with shimmer placeholders, memory/disk caching, and localized multiplatform strings.
  - *Reference*: `reference/22-coil3-and-resources.html`

---

### Phase 7: Native iOS Interop & Dual-UI Mastery
*Goal: Master the hybrid paradigm: embedding Compose in iOS and embedding SwiftUI/UIKit in Compose.*

- **Lesson 23: Embedding Compose Multiplatform into Existing iOS Apps**
  - *Topic*: Exporting Compose screens as `UIViewController` via `ComposeUIViewController`, managing safe areas, iOS status bars, and keyboard insets.
  - *Practice*: Build an iOS host view that embeds a Compose Multiplatform screen and passes Swift state into it.
  - *Reference*: `reference/23-compose-in-swiftui.html`

- **Lesson 24: Hosting Native UIKit/SwiftUI Views inside Compose**
  - *Topic*: `UIKitView` interop: Embedding native iOS Apple Maps, camera previews, and Apple Pay buttons directly inside a Compose Multiplatform hierarchy.
  - *Practice*: Embed an interactive iOS native map and Android Google Map inside a single shared Compose composable.
  - *Reference*: `reference/24-uikitview-in-compose.html`

- **Lesson 25: The Dual-UI Architecture: Shared KMP Logic + Native SwiftUI**
  - *Topic*: When enterprise teams prefer native SwiftUI: Sharing only Data/Domain/ViewModels while maintaining 100% native SwiftUI and Jetpack Compose frontends.
  - *Practice*: Implement a shared KMP ViewModel and bind it directly to a SwiftUI `@StateObject` / `@Observable` view using SKIE.
  - *Reference*: `reference/25-shared-logic-native-swiftui.html`

---

### Phase 8: Comprehensive Testing & Quality Assurance
*Goal: Guarantee production stability with multiplatform unit, flow, and UI tests.*

- **Lesson 26: Multiplatform Unit Testing**
  - *Topic*: `kotlin.test` framework, test runners for JVM and Kotlin/Native, testing Repositories and UseCases.
  - *Practice*: Write comprehensive unit tests for business logic running on both Android and iOS test runners.
  - *Reference*: `reference/26-kmp-unit-testing.html`

- **Lesson 27: Testing Flows & Coroutines with Turbine**
  - *Topic*: Cash App's Turbine library: Deterministic Flow testing, testing state transitions, emissions, debounce, and error triggers.
  - *Practice*: Write a Turbine test suite verifying complex ViewModel StateFlow emissions under simulated network lag.
  - *Reference*: `reference/27-turbine-flow-testing.html`

- **Lesson 28: Mocking in Multiplatform: Fakes vs. Mockative**
  - *Topic*: Why Mockito/MockK don't work on Kotlin/Native. Building robust Test Fakes vs. using multiplatform mocking libraries (Mockative). Ktor `MockEngine`.
  - *Practice*: Construct a complete network-to-UI test pipeline using Ktor `MockEngine` and domain fakes.
  - *Reference*: `reference/28-mockative-and-fakes.html`

- **Lesson 29: Compose Multiplatform UI Testing**
  - *Topic*: Writing cross-platform Compose UI tests, semantic tree matching, verifying user interactions, accessibility, and screenshot testing.
  - *Practice*: Write an automated Compose Multiplatform test that simulates user text entry, clicks a button, and verifies the UI update.
  - *Reference*: `reference/29-compose-ui-testing.html`

---

### Phase 9: Production CI/CD, Build Optimization & Release
*Goal: Automate multiplatform builds, code signing, and App Store / Play Store deployment.*

- **Lesson 30: Gradle Build Optimization & Caching**
  - *Topic*: Configuration cache, Gradle build cache, Kotlin daemon optimization, memory sizing, and minimizing Kotlin/Native link times.
  - *Practice*: Profile a slow KMP build with Gradle Build Scan and configure caching to cut compilation time by 50%.
  - *Reference*: `reference/30-build-optimization-caching.html`

- **Lesson 31: GitHub Actions Multiplatform Matrix CI/CD**
  - *Topic*: Cross-compiling on macOS and Linux runners, caching Gradle dependencies, building Android AABs, compiling iOS XCFrameworks and IPAs, code signing with Fastlane.
  - *Practice*: Write a complete production GitHub Actions workflow file that builds, tests, and packages both Android and iOS artifacts.
  - *Reference*: `reference/31-github-actions-matrix-ci.html`

---

### Phase 10: The Capstone Portfolio Project: "PulseSync"
*Goal: Build an end-to-end, portfolio-grade multiplatform application that demonstrates mastery to engineering hiring managers.*

#### 📱 Project Overview: PulseSync (Decentralized Team Incident & Operations Hub)
A real-time, offline-first incident and operations command center app for engineering teams. Runs natively on Android and iOS from a single KMP codebase.

#### 🛠️ Tech Stack:
- **Language**: Kotlin 2.x (K2 compiler enabled)
- **UI**: Compose Multiplatform (Material 3 Adaptive) + Native iOS SwiftUI companion widget
- **Networking**: Ktor 3.x with WebSockets for real-time incident event streaming + Darwin/OkHttp engines
- **Persistence**: Jetpack Room KMP with SQLite driver + encrypted storage for tokens
- **Architecture**: Clean Architecture + MVI (Model-View-Intent) with Unidirectional Data Flow
- **State Management**: Kotlin Coroutines & StateFlow bridged to Swift via SKIE
- **Dependency Injection**: Koin / Kotlin-Inject
- **Images & Resources**: Coil 3 + Compose Multiplatform Resources
- **Testing**: 85%+ coverage with `kotlin.test`, Turbine, and Ktor `MockEngine`
- **DevOps**: GitHub Actions matrix workflow generating unsigned Android AAB and iOS test framework

#### 💼 Resume Impact & Interview Talking Points:
- *"Architected and shipped an offline-first KMP application targeting Android and iOS with 85% shared code across UI, domain, and data layers."*
- *"Implemented seamless Swift-Kotlin interop utilizing SKIE to expose reactive StateFlow pipelines as Swift async sequences to SwiftUI."*
- *"Engineered an offline-first sync engine using Room KMP and Ktor 3 WebSockets with conflict resolution and optimistic UI updates."*
- *"Configured multi-platform CI/CD matrix pipelines on GitHub Actions reducing dual-platform build and test turnaround time."*

#### 📖 Full Technical Blueprint:
- *Reference*: [`reference/capstone-pulsesync-blueprint.html`](reference/capstone-pulsesync-blueprint.html)

---

## 🚀 How We Proceed

Whenever you are ready to begin, we take the roadmap one lesson at a time:
1. We author the self-contained HTML lesson with rich code examples, visual callouts, and practice exercises.
2. We write the hands-on code and verify it together in this workspace.
3. We record non-obvious lessons in `learning-records/` to guide your zone of proximal development.
4. Once all phases are mastered, we build and package the Capstone Project.
