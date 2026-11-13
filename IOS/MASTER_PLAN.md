# iOS Engineering Master Plan & Syllabus: Junior Android to Senior iOS Developer

> **Purpose**: This Master Plan serves as the foundational legend and operational roadmap for your transition from a Junior Android Developer to an autonomous, high-impact **Senior iOS Developer / Technical Lead Candidate**. Every lesson, architectural standard, exercise, and milestone traces directly to this syllabus.

---

## 1. The Senior Rosetta Stone: Android/Kotlin vs. iOS/Swift Systems Model

As an Android engineer, you already understand mobile lifecycle constraints, reactive state, declarative layout trees, and async data streams. Transitioning to a **Senior** iOS engineer requires understanding how the two operating systems and runtimes differ at the hardware, compiler, and kernel levels.

| Dimension | Android / Kotlin | iOS / Swift | Senior Engineering Distinction |
| :--- | :--- | :--- | :--- |
| **Language Paradigm** | Multiplatform OOP + Functional | Protocol-Oriented + Value Semantics | Default to `struct` (value type). Memory is copied on write and stack-allocated. Generics specialize at compile time without JVM type erasure. |
| **Memory Architecture** | JVM Generational Tracing GC | Automatic Reference Counting (ARC) | No GC pauses, but **retain cycles** cause permanent leaks. Memory layout (`size`, `stride`, `alignment`) and copy-in copy-out (`inout`) exclusivity are strictly enforced. |
| **Method Dispatch** | Virtual method table (vtable) by default | Static, Table (Witness/Vtable), and Message Send (`objc_msgSend`) | Senior engineers design interfaces around dispatch overhead: static dispatch for structs/final classes, Protocol Witness Tables (PWT) for protocols, and dynamic dispatch when required. |
| **Polymorphism Overhead** | Interface references (boxed on heap) | Generics (`some P`) vs. Existentials (`any P`) | `some` generates specialized inline code with zero boxing overhead; `any` allocates a 5-word existential container (3-word buffer + PWT + VWT) and allocates on heap if $>3$ words. |
| **OS Memory Pressure** | Low Memory Killer (LMK) via `oom_adj` | Darwin Kernel Jetsam | iOS aggressively terminates background apps exceeding dirty memory limits without page swapping. Senior engineers monitor dirty memory vs. clean memory. |
| **Mutability Semantics** | `val` vs `var` (reference immutability) | `let` vs `var` (deep transitive immutability) | `let` on a value type freezes its entire memory representation. Mutating methods on structs require explicit `mutating` modifiers. |
| **Declarative UI Engine** | Jetpack Compose (Slot Table / Snapshot state) | SwiftUI (AttributeGraph / View Identity) | SwiftUI uses structural vs. explicit identity. Senior engineers eliminate redundant view re-evaluations using `Self._printChanges()` and `.equatable()`. |
| **Concurrency & Safety** | Coroutines (`CoroutineScope`, `Dispatchers`) | Swift 6 Concurrency (`Task`, `Actor`, `Sendable`) | Swift 6 compiler guarantees **zero data races** at compile time via isolation domains and `Sendable`. Senior devs prevent actor reentrancy and thread starvation. |
| **Asynchronous Streams** | Kotlin `Flow` (Cold / Hot) | `AsyncStream` / `AsyncSequence` / Combine | Modern Swift favors `AsyncStream` with backpressure policies over heavy Combine pipelines. |
| **Runtime & Launch Time** | ART (Android Runtime) & Ahead-of-Time / JIT | dyld4 (Mach-O Dynamic Linker) & Native ARM64 | Senior engineers optimize pre-main launch time: minimizing dynamic framework linkage, dylib rebase/binding overhead, and using static SPM libraries. |
| **Persistence & Migrations** | Room ORM (SQLite) | `SwiftData` & `Core Data` | Enterprise persistence requires handling schema evolution via `VersionedSchema` and custom migration stages without data loss. |
| **Logging & Telemetry** | Timber, Logcat | Apple Unified Logging (`os.Logger`) & MetricKit | Production apps replace bare `print()` with structured OSLog and use MetricKit (`MXMetricManager`) to collect production battery, hang rates, and thermal metrics. |
| **Compliance & App Store** | Android Manifest permissions | Privacy Manifest (`PrivacyInfo.xcprivacy`) & App Attest | Apple strictly mandates Privacy Manifests for required reason APIs and signed third-party SDK dependencies for App Store approval. |
| **Profiling & Diagnostics** | Android Studio Profiler, LeakCanary | Xcode Instruments & LLDB | Kernel-level profiling: Allocations, Leaks, Time Profiler, Core Animation commit times, and dirty memory analysis. |

---

## 2. Senior iOS Developer Competency Benchmark

To be evaluated and hired as a **Senior iOS Developer**, you must operate across the full software lifecycle and demonstrate the following six pillars:

```
┌────────────────────────────────────────────────────────────────────────┐
│                   SENIOR iOS COMPETENCY PILLARS                        │
├────────────────────────────────┬───────────────────────────────────────┤
│ 1. Compiler, Memory & Runtime  │ MemoryLayout (size/stride/alignment), │
│    Internals                   │ static vs dynamic dispatch, PWT/VWT,  │
│                                │ existential containers, custom CoW,   │
│                                │ Darwin Jetsam & dirty memory defense. │
├────────────────────────────────┼───────────────────────────────────────┤
│ 2. Swift 6 Concurrency & Scale │ Complete concurrency checking, actor  │
│                                │ reentrancy defense, custom executors, │
│                                │ AsyncStream backpressure, no deadlocks│
├────────────────────────────────┼───────────────────────────────────────┤
│ 3. UI Performance & Rendering  │ AttributeGraph optimization, identity │
│                                │ mechanics, SwiftUI _printChanges(),   │
│                                │ 120fps scrolling, UIKit & CoreAnim.   │
├────────────────────────────────┼───────────────────────────────────────┤
│ 4. Modular Enterprise Arch     │ Multi-package SPM dependency graphs,  │
│                                │ Unidirectional Data Flow (UDF), DI,   │
│                                │ dyld4 pre-main launch time < 400ms.   │
├────────────────────────────────┼───────────────────────────────────────┤
│ 5. Telemetry, Reliability & OS │ MetricKit telemetry (hang rate, CPU), │
│                                │ Unified Logging (os.Logger), crash    │
│                                │ symbolication (dSYM), PrivacyInfo.    │
├────────────────────────────────┼───────────────────────────────────────┤
│ 6. Testing, CI/CD & Leadership │ Swift Testing (@Test), snapshot tests,│
│                                │ Fastlane release trains, code review  │
│                                │ standards, technical mentorship.      │
└────────────────────────────────┴───────────────────────────────────────┘
```

---

## 3. Curriculum Syllabus & Lesson Sequence

The curriculum is structured into 9 progressive phases (60 lessons + flagship capstone) designed to take you from Android fundamentals to 2027 Senior iOS mastery.

### Phase 1: Swift Compiler & Language Internals
- **Lesson 1.1**: The Swift Type System, Mutability & Optionals (Memory layout of Optionals, Spare Bit Optimization, Transitive Value Immutability, mutating semantics, and the Law of Exclusivity.)
- **Lesson 1.2**: Value Types vs. Reference Types & Custom Copy-on-Write (CoW) (Deep dive into memory layouts (size, stride, alignment), stack vs heap allocation, and implementing production-grade Copy-on-Write using isKnownUniquelyReferenced.)
- **Lesson 1.3**: Automatic Reference Counting (ARC) & Memory Safety (Strong, Weak, Unowned references, closure capture lists [weak self], retain cycles, and autorelease pools.)
- **Lesson 1.4**: Enums with Associated Values & Algebraic State Modeling (Sum types, recursive enums with indirect, pattern matching with switch and case let, and modeling bulletproof UI state machines.)
- **Lesson 1.5**: Protocol-Oriented Programming (POP) & Method Dispatch (Witness Tables (PWT/VWT), Static vs Dynamic Dispatch, Protocol Extension Traps, and Opaque (some) vs Existential (any) Types.)
- **Lesson 1.6**: Closures, Capture Semantics & Defensive Error Handling (Escaping closures (@escaping), autoclosures (@autoclosure), typed throws (Swift 6), rethrows, and deterministic LIFO defer cleanup.)
- **Lesson 1.7**: Generics, Associated Types & Type Erasure (Generic specialization, associatedtype, primary associated types, and building type-erasing AnyRepository wrappers.)
- **Lesson 1.8**: Metaprogramming: Property Wrappers, KeyPaths & Swift Macros (@propertyWrapper mechanics, projected values ($), KeyPaths, and compile-time Swift Macros vs Kotlin KSP.)

### Phase 2: Modern Swift 6 Concurrency & Enterprise Networking
- **Lesson 2.1**: Fundamentals of Swift Concurrency: async, await & Tasks (Task lifecycles, cooperative cancellation, thread hopping, and the Darwin cooperative thread pool.)
- **Lesson 2.2**: Structured Concurrency: async let & TaskGroup Hierarchies (Bounded child tasks, dynamic concurrency trees with withTaskGroup, and automatic cancellation propagation.)
- **Lesson 2.3**: AsyncStream & Bridging Legacy Asynchronous APIs (Bridging delegate callbacks, notification center observers, and WebSocket frames into AsyncSequence with backpressure policies.)
- **Lesson 2.4**: Thread Safety with Actors & The Reentrancy Hazard (Actor isolation boundaries, nonisolated declarations, and preventing silent state corruption across await suspension points.)
- **Lesson 2.5**: Global Actors, @MainActor & Custom Executors (Thread hopping, ensuring UI mutations on the main runloop, and custom serial actor executors.)
- **Lesson 2.6**: Swift 6 Strict Concurrency, Region-Based Isolation & Sendable (Compile-time data-race safety, Sendable protocols, non-Sendable transfer rules, and migrating legacy codebases.)
- **Lesson 2.7**: Combine Essentials & Bridging to Swift Concurrency (Publishers, AnyCancellable, sink retain cycle traps, and bridging Combine streams into async/await via .values.)
- **Lesson 2.8**: Production Networking with URLSession & HTTP Security (Async URLSession.shared.data(for:), status code validation, custom HTTP headers, timeout policies, and HTTP/3 support.)
- **Lesson 2.9**: Robust JSON Parsing with Codable Architecture (Key decoding strategies, date formatting, custom init(from:), polymorphic payloads, and resilient lossy array decoders.)
- **Lesson 2.10**: Enterprise Network Layer: Interceptors & Token Refresh (Request interceptors, actor-synchronized OAuth2 token refresh, exponential backoff retries, and network telemetry.)

### Phase 3: High-Performance Declarative UI (SwiftUI & UIKit)
- **Lesson 3.1**: The SwiftUI Render Tree: Identity, AttributeGraph & Re-renders (Structural vs Explicit identity, eliminating redundant layout passes, and debugging invalidations with Self._printChanges().)
- **Lesson 3.2**: Modern State Management: @State, @Binding & @Environment (Single source of truth, two-way bindings ($), custom EnvironmentKeys, and @FocusState mechanics.)
- **Lesson 3.3**: The Observation Framework (@Observable) in Production (Fine-grained property-level dependency tracking, @Bindable, and eliminating legacy ObservableObject coarse invalidations.)
- **Lesson 3.4**: Layout Mastery & The Custom Layout Protocol (The 3-step layout negotiation algorithm, avoiding GeometryReader performance traps, and building custom Layout containers.)
- **Lesson 3.5**: Type-Safe NavigationStack & Deep Linking (Decoupled programmatic routing with NavigationPath, .navigationDestination(for:), and serializing deep links.)
- **Lesson 3.6**: Enterprise Design Systems & Custom ViewModifiers (Design tokens, custom ViewModifier pipelines, ButtonStyles, typography scales, and dynamic dark mode palettes.)
- **Lesson 3.7**: Animations & Micro-Interactions: Springs & Matched Geometry (Physics-based springs, matchedGeometryEffect hero transitions, drag gestures, and phase-based animations.)
- **Lesson 3.8**: UIKit Core Architecture: Lifecycles, Responders & Auto Layout (UIViewController lifecycle, Responder Chain and hit-testing (hitTest:withEvent:), Auto Layout engine, and UICollectionView DiffableDataSource.)
- **Lesson 3.9**: UIKit Interoperability: Representables & The Coordinator Pattern (UIViewRepresentable, UIViewControllerRepresentable, UIHostingController, and two-way delegate bridges via Coordinators.)

### Phase 4: Local Persistence, App Security & Offline-First
- **Lesson 4.1**: Lightweight Storage & Apple Keychain Services (UserDefaults (@AppStorage) for UI preferences vs Apple Keychain Services (SecItem) for biometric and hardware-encrypted tokens.)
- **Lesson 4.2**: App Security: CryptoKit & SSL Certificate Pinning (Apple CryptoKit (AES-GCM, SHA-256), Secure Enclave, URLSessionDelegate TLS pinning, and App Attest defenses.)
- **Lesson 4.3**: SwiftData Fundamentals: Schema, Container & Context (@Model macros, ModelContainer, ModelContext, @Query in SwiftUI, and CRUD operations on local SQLite stores.)
- **Lesson 4.4**: Advanced SwiftData: VersionedSchema & ModelActor (Schema evolution with VersionedSchema, custom SchemaMigrationPlan stages, and thread-safe background ingestion with ModelActor.)
- **Lesson 4.5**: Core Data Essentials for Enterprise Codebases (Understanding legacy NSPersistentContainer, NSManagedObjectContext threading rules, NSFetchedResultsController, and Core Data migrations.)
- **Lesson 4.6**: Offline-First Sync Architecture: Optimistic UI & Conflict Resolution (Local persistence as single source of truth, optimistic UI state updates, Vector Clocks / Last-Write-Wins (LWW), and sync queues.)

### Phase 5: Architecture, Dependency Injection & Modularization
- **Lesson 5.1**: Enterprise Clean Architecture & Unidirectional Data Flow (UDF) (Separation of concerns: Views, ViewModels with @Observable, Use Cases/Interactors, and pure Reducer state machines.)
- **Lesson 5.2**: Scalable Dependency Injection Architecture (Protocol-based constructor injection, container factories, environment injection, and eliminating global singletons.)
- **Lesson 5.3**: Multi-Package SPM Modularization Architecture (Decomposing monolithic Xcode projects into local Swift Package Manager modules: CoreNetwork, CoreDatabase, Features, and DesignSystem.)
- **Lesson 5.4**: Build Schemes, xcconfig & dyld4 Pre-Main Launch Time (Managing Debug/Staging/Production configurations safely, and optimizing pre-main launch time under 400ms.)

### Phase 6: Testing, Debugging & Performance Profiling
- **Lesson 6.1**: The Modern Swift Testing Framework: @Test & #expect (Macro-driven testing (@Test), compile-time expression inspection (#expect vs #require), parameterized testing, and parallel execution.)
- **Lesson 6.2**: Test Doubles, Stubs & Async ViewModel Testing (Protocol mocks, in-memory repository fakes, testing async state transitions, and deterministic error paths.)
- **Lesson 6.3**: UI Automation & Snapshot Regression Testing (XCUIApplication automation, accessibilityIdentifier query matching, and pixel-perfect snapshot testing.)
- **Lesson 6.4**: Advanced LLDB & View Hierarchy Debugging (Conditional breakpoints, po vs p vs v, expression command injection, Memory Graph Debugger, and layout constraint debugging.)
- **Lesson 6.5**: Performance Profiling with Xcode Instruments (Hunting retain cycles with Leaks & Allocations (Generation Analysis), eliminating CPU bottlenecks with Time Profiler, and 120fps hitch rates.)
- **Lesson 6.6**: Production Observability: MetricKit & Apple Unified Logging (Apple Unified Logging (os.Logger), structured subsystems, privacy redaction (<private>), and on-device MetricKit telemetry.)

### Phase 7: App Store Readiness, Ecosystem & DevOps
- **Lesson 7.1**: Apple HIG, Accessibility & Privacy Manifests (Human Interface Guidelines compliance, Dynamic Type scaling, VoiceOver accessibility traits, and PrivacyInfo.xcprivacy required reason declarations.)
- **Lesson 7.2**: Background Processing & Apple Push Notifications (APNs) (BGTaskScheduler (App Refresh & Processing), silent push notifications (content-available: 1), and device token registration.)
- **Lesson 7.3**: App Extensions, WidgetKit, Live Activities & App Groups (WidgetKit TimelineProvider, Dynamic Island Live Activities, sharing data via App Group containers, and the strict 30MB extension memory limit.)
- **Lesson 7.4**: Provisioning, Certificates, Code Signing & App Attest (Apple Developer Program, App IDs, Provisioning Profiles, Entitlements, codesign CLI, and Apple App Attest security.)
- **Lesson 7.5**: Enterprise CI/CD Pipelines & TestFlight Automation (Automated build and test workflows with Fastlane, GitHub Actions, match certificate synchronization, and TestFlight deployment trains.)

### Phase 8: 2027 Senior iOS Frontier & Platform Expansion
- **Lesson 8.1**: Objective-C Interoperability & Legacy Runtime Bridging (Bridging headers, @objc / dynamic dispatch, NSError** bridging, Unmanaged memory ownership, and wrapping legacy Obj-C SDKs for Swift 6.)
- **Lesson 8.2**: Swift 6.2/6.3 Approachable Concurrency & Mutex (SE-0461 nonisolated(nonsending), @concurrent opt-in, default @MainActor isolation, and Synchronization.Mutex for synchronous critical sections.)
- **Lesson 8.3**: Zero-Copy & Noncopyable Swift: ~Copyable, InlineArray & Span (Unique ownership with ~Copyable (consuming/borrowing/deinit), fixed-size stack InlineArray, and lifetime-safe Span<T> views.)
- **Lesson 8.4**: The Composable Architecture (TCA) vs. @Observable UDF (Point-Free's @Reducer, @ObservableState, Effect cancellation, DependencyKey, TestStore exhaustive assertions, and senior architecture trade-offs.)
- **Lesson 8.5**: File System Sandboxing, Data Protection & Direct SQLite / GRDB (Documents vs Application Support vs Caches, iCloud backup exclusion, NSFileProtectionCompleteUnlessOpen, and SQLite/GRDB vs SwiftData.)
- **Lesson 8.6**: XCFrameworks, Library Evolution & Mergeable Libraries (Static vs Dynamic vs Mergeable binaries, BUILD_LIBRARY_FOR_DISTRIBUTION (.swiftinterface), and xcodebuild -create-xcframework.)
- **Lesson 8.7**: Code Quality Gates: swift-format, SwiftLint & Xcode Test Plans (Toolchain swift-format, SwiftLint SPM Build Tool Plugins, and .xctestplan matrices (TSan, randomization, and localization).)
- **Lesson 8.8**: iOS 26 Liquid Glass, Modern Animators & String Catalogs (Apple's Liquid Glass material rules, PhaseAnimator & KeyframeAnimator, @Animatable, and .xcstrings String Catalogs at scale.)
- **Lesson 8.9**: On-Device AI: FoundationModels Framework & Core ML (Apple Intelligence FoundationModels (@Generable guided generation, Tool calling, multimodal prompts) and Core ML Neural Engine profiling.)
- **Lesson 8.10**: AppIntents, Siri, Spotlight & System Intelligence (Exposing domain actions & AppEntities to Siri, Apple Intelligence on-screen awareness, Spotlight, Control Center, and interactive Widgets.)
- **Lesson 8.11**: Monetization & Passwordless Auth: StoreKit 2 & Passkeys (Async JWS-verified StoreKit 2 subscriptions, Transaction.updates listeners, local .storekit testing, and AuthenticationServices Passkeys.)
- **Lesson 8.12**: Xcode Cloud & Multi-Channel Release Engineering (Apple's native Xcode Cloud (ci_post_clone.sh), headless xcodebuild + xcresulttool, phased App Store rollouts, and Fastlane vs. Xcode Cloud.)

### Phase 9: Capstone Portfolio Project ('PulseFlow Enterprise')
- **Milestone 9.1**: Capstone Showcase: PulseFlow Enterprise Architecture (The definitive, interview-winning 2027 portfolio project: multi-package SPM & XCFramework, SwiftData/SQLite, Swift 6.3 concurrency, AppIntents, FoundationModels, StoreKit 2, and MetricKit telemetry.)
---



## 4. The Senior Capstone Project Specification ("PulseFlow Enterprise")

### Architecture & Key Senior Deliverables
1. **Multi-Module SPM + Binary SDK Architecture**: Completely decoupled modules (`FeatureAuth`, `FeatureDashboard`, `CoreNetwork`, `CoreDatabase`, `DesignSystem`, `Telemetry`) with zero circular dependencies, static linkage for sub-400ms launch times, and a distributable `XCFramework` boundary (`PulseAnalyticsSDK`).
2. **Swift 6.3 Strict & Approachable Concurrency**: Compiled under strict data-race safety with zero warnings, `Mutex` for fast synchronous critical sections, `~Copyable` resource guards, and actor-isolated background data ingestion.
3. **Resilient Offline-First Engine**: Protected sandbox storage (`FileProtectionType`) paired with SwiftData (`VersionedSchema`) / SQLite/GRDB sync queues and deterministic conflict resolution.
4. **Custom Copy-on-Write & Zero-Copy Storage**: Custom CoW data structures utilizing `isKnownUniquelyReferenced` alongside `InlineArray` and `Span<T>` on tight telemetry loops.
5. **System Intelligence, On-Device AI & Liquid Glass UI**: WidgetKit + `AppIntents` (`AppEntity`), `FoundationModels` (`@Generable` triage), Passkeys auth, `StoreKit 2` entitlements, and `.xcstrings` localization.
6. **Telemetry & Production Observability**: MetricKit (`MXMetricManager`) reporting hang rates, CPU consumption, and dirty memory metrics, paired with `os.Logger` structured subsystems.
7. **Comprehensive Test Suite & CI/CD**: >85% unit test coverage via Swift Testing (`@Test`/`#expect`), `.xctestplan` randomized + TSan matrices, `swift-format` + SwiftLint gates, and automated Xcode Cloud / GitHub Actions + Fastlane pipelines.

---

## 5. Execution Legend & Lesson Workflow

Every lesson follows this rigorous Senior pedagogical feedback loop:

```
┌────────────────────────────────────────────────────────┐
│            SENIOR LESSON EXECUTION LOOP                │
├────────────────────────────────────────────────────────┤
│ 1. Android Anchor: Bridge from Kotlin/Android runtime │
│ 2. Deep Internals: Swift compiler & Darwin mechanics   │
│ 3. Production Code: Senior-standard runnable example   │
│ 4. Failure Modes: Anti-patterns, crashes, retain traps │
│ 5. Hands-on Challenge: Script with automated tests     │
│ 6. Verification: Learning record & curriculum update   │
└────────────────────────────────────────────────────────┘
```


---

## Progress Tracker

- [ ] **Phase 1: Swift Compiler & Language Internals**
  - [ ] Lesson 1.1: The Swift Type System, Mutability & Optionals
  - [ ] Lesson 1.2: Value Types vs. Reference Types & Custom Copy-on-Write (CoW)
  - [ ] Lesson 1.3: Automatic Reference Counting (ARC) & Memory Safety
  - [ ] Lesson 1.4: Enums with Associated Values & Algebraic State Modeling
  - [ ] Lesson 1.5: Protocol-Oriented Programming (POP) & Method Dispatch
  - [ ] Lesson 1.6: Closures, Capture Semantics & Defensive Error Handling
  - [ ] Lesson 1.7: Generics, Associated Types & Type Erasure
  - [ ] Lesson 1.8: Metaprogramming: Property Wrappers, KeyPaths & Swift Macros
- [ ] **Phase 2: Modern Swift 6 Concurrency & Enterprise Networking**
  - [ ] Lesson 2.1: Fundamentals of Swift Concurrency: async, await & Tasks
  - [ ] Lesson 2.2: Structured Concurrency: async let & TaskGroup Hierarchies
  - [ ] Lesson 2.3: AsyncStream & Bridging Legacy Asynchronous APIs
  - [ ] Lesson 2.4: Thread Safety with Actors & The Reentrancy Hazard
  - [ ] Lesson 2.5: Global Actors, @MainActor & Custom Executors
  - [ ] Lesson 2.6: Swift 6 Strict Concurrency, Region-Based Isolation & Sendable
  - [ ] Lesson 2.7: Combine Essentials & Bridging to Swift Concurrency
  - [ ] Lesson 2.8: Production Networking with URLSession & HTTP Security
  - [ ] Lesson 2.9: Robust JSON Parsing with Codable Architecture
  - [ ] Lesson 2.10: Enterprise Network Layer: Interceptors & Token Refresh
- [ ] **Phase 3: High-Performance Declarative UI (SwiftUI & UIKit)**
  - [ ] Lesson 3.1: The SwiftUI Render Tree: Identity, AttributeGraph & Re-renders
  - [ ] Lesson 3.2: Modern State Management: @State, @Binding & @Environment
  - [ ] Lesson 3.3: The Observation Framework (@Observable) in Production
  - [ ] Lesson 3.4: Layout Mastery & The Custom Layout Protocol
  - [ ] Lesson 3.5: Type-Safe NavigationStack & Deep Linking
  - [ ] Lesson 3.6: Enterprise Design Systems & Custom ViewModifiers
  - [ ] Lesson 3.7: Animations & Micro-Interactions: Springs & Matched Geometry
  - [ ] Lesson 3.8: UIKit Core Architecture: Lifecycles, Responders & Auto Layout
  - [ ] Lesson 3.9: UIKit Interoperability: Representables & The Coordinator Pattern
- [ ] **Phase 4: Local Persistence, App Security & Offline-First**
  - [ ] Lesson 4.1: Lightweight Storage & Apple Keychain Services
  - [ ] Lesson 4.2: App Security: CryptoKit & SSL Certificate Pinning
  - [ ] Lesson 4.3: SwiftData Fundamentals: Schema, Container & Context
  - [ ] Lesson 4.4: Advanced SwiftData: VersionedSchema & ModelActor
  - [ ] Lesson 4.5: Core Data Essentials for Enterprise Codebases
  - [ ] Lesson 4.6: Offline-First Sync Architecture: Optimistic UI & Conflict Resolution
- [ ] **Phase 5: Architecture, Dependency Injection & Modularization**
  - [ ] Lesson 5.1: Enterprise Clean Architecture & Unidirectional Data Flow (UDF)
  - [ ] Lesson 5.2: Scalable Dependency Injection Architecture
  - [ ] Lesson 5.3: Multi-Package SPM Modularization Architecture
  - [ ] Lesson 5.4: Build Schemes, xcconfig & dyld4 Pre-Main Launch Time
- [ ] **Phase 6: Testing, Debugging & Performance Profiling**
  - [ ] Lesson 6.1: The Modern Swift Testing Framework: @Test & #expect
  - [ ] Lesson 6.2: Test Doubles, Stubs & Async ViewModel Testing
  - [ ] Lesson 6.3: UI Automation & Snapshot Regression Testing
  - [ ] Lesson 6.4: Advanced LLDB & View Hierarchy Debugging
  - [ ] Lesson 6.5: Performance Profiling with Xcode Instruments
  - [ ] Lesson 6.6: Production Observability: MetricKit & Apple Unified Logging
- [ ] **Phase 7: App Store Readiness, Ecosystem & DevOps**
  - [ ] Lesson 7.1: Apple HIG, Accessibility & Privacy Manifests
  - [ ] Lesson 7.2: Background Processing & Apple Push Notifications (APNs)
  - [ ] Lesson 7.3: App Extensions, WidgetKit, Live Activities & App Groups
  - [ ] Lesson 7.4: Provisioning, Certificates, Code Signing & App Attest
  - [ ] Lesson 7.5: Enterprise CI/CD Pipelines & TestFlight Automation
- [ ] **Phase 8: 2027 Senior iOS Frontier & Platform Expansion**
  - [ ] Lesson 8.1: Objective-C Interoperability & Legacy Runtime Bridging
  - [ ] Lesson 8.2: Swift 6.2/6.3 Approachable Concurrency & Mutex
  - [ ] Lesson 8.3: Zero-Copy & Noncopyable Swift: ~Copyable, InlineArray & Span
  - [ ] Lesson 8.4: The Composable Architecture (TCA) vs. @Observable UDF
  - [ ] Lesson 8.5: File System Sandboxing, Data Protection & Direct SQLite / GRDB
  - [ ] Lesson 8.6: XCFrameworks, Library Evolution & Mergeable Libraries
  - [ ] Lesson 8.7: Code Quality Gates: swift-format, SwiftLint & Xcode Test Plans
  - [ ] Lesson 8.8: iOS 26 Liquid Glass, Modern Animators & String Catalogs
  - [ ] Lesson 8.9: On-Device AI: FoundationModels Framework & Core ML
  - [ ] Lesson 8.10: AppIntents, Siri, Spotlight & System Intelligence
  - [ ] Lesson 8.11: Monetization & Passwordless Auth: StoreKit 2 & Passkeys
  - [ ] Lesson 8.12: Xcode Cloud & Multi-Channel Release Engineering
- [ ] **Phase 9: Capstone Portfolio Project ('PulseFlow Enterprise')**
  - [ ] Milestone 9.1: Capstone Showcase: PulseFlow Enterprise Architecture
