# Mission: Junior Android to Senior iOS Developer

## Why
Transition from a junior Android engineer to an autonomous, high-impact Senior iOS Developer and Lead-capable mobile engineer. Master low-level Swift compiler and runtime internals, advanced Swift 6 concurrency, resilient offline architectures, performance profiling with Instruments and MetricKit, and architect scalable, modular systems ready for enterprise production and technical leadership interviews.

## Success looks like
- Deep mastery of Swift language and runtime internals: static vs dynamic dispatch, Protocol Witness Tables (PWT), Value Witness Tables (VWT), existential containers, custom Copy-on-Write (CoW) implementation, and memory layouts (`size`, `stride`, `alignment`).
- Advanced Swift 6 concurrency engineering: complete data-race safety, strict region-based isolation, custom actor executors, reentrancy defense, and cooperative thread pool starvation avoidance.
- High-performance UI engineering: SwiftUI layout and render-tree performance, identity mechanics, eliminating invalidations with `Self._printChanges()`, and seamless UIKit/CoreGraphics integration.
- Enterprise-grade architecture: multi-package SPM modularization, Unidirectional Data Flow (UDF), compile-time dependency injection, and pre-main launch time optimization (dyld4, static vs dynamic linkage).
- Production observability & reliability: MetricKit telemetry (hang rate, thermal, dirty memory), Apple Unified Logging (`os.Logger`), crash symbolication (dSYM), and Privacy Manifests (`PrivacyInfo.xcprivacy`).
- Completion of a flagship, Senior-grade capstone app featuring modular architecture, offline sync with vector clocks / conflict resolution, WidgetKit, MetricKit telemetry, and CI/CD automation.
- 2027 Senior iOS frontier readiness: Swift 6.2/6.3 Approachable Concurrency (`nonisolated(nonsending)`, `Mutex`), zero-copy/ownership primitives (`~Copyable`, `InlineArray`, `Span<T>`), Objective-C legacy interoperability, TCA vs. `@Observable` UDF trade-offs, FileManager/Data Protection & direct SQLite/GRDB, XCFramework binary distribution, `swift-format`/SwiftLint/Test Plans, iOS 26 Liquid Glass & `.xcstrings` localization, `FoundationModels` & Core ML, `AppIntents`, `StoreKit 2`/Passkeys, and Xcode Cloud.

## Constraints
- Accelerate through Android/Kotlin parallels, but rigorously expose Apple platform internals (e.g., ARC retain trees vs JVM GC, Darwin kernel jetsam vs Android low-memory killer, dyld vs ART runtime).
- Code must meet Senior engineering standards: zero force-unwrapping, strict concurrency compliance, exhaustive error modeling, comprehensive documentation, and automated tests.

## Out of scope
- Cross-platform wrappers (React Native, Flutter, KMP) — pure native Apple platform mastery.
- Game development frameworks (SpriteKit, Metal shaders) outside standard UI rendering and CoreAnimation.
- Obsolete/deprecated `roadmap.sh/ios` tooling (Carthage, CocoaPods authoring, RxSwift, Storyboards/XIBs/IBOutlets, Alamofire, SwiftUI `NavigationView`, Tailor).
