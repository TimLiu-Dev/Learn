# Phase 5: Scalable Architecture, Modularization & Launch Time
# Lessons 0034 - 0037

PHASE5_LESSONS = [
    # 0034: Lesson 5.1
    {
        "id": "0034",
        "slug": "0034-enterprise-clean-architecture-and-udf",
        "phase_num": 5,
        "phase_title": "Phase 5: Architecture, Dependency Injection & Modularization",
        "lesson_num": "Lesson 5.1",
        "title": "Enterprise Clean Architecture & Unidirectional Data Flow (UDF)",
        "subtitle": "Separation of concerns: Views, ViewModels with @Observable, Use Cases/Interactors, and pure Reducer state machines.",
        "prev_url": "0033-offline-first-sync-architecture.html",
        "next_url": "0035-scalable-dependency-injection.html",
        "cheatsheet_url": "../reference/architecture-and-modularization-cheatsheet.html",
        "cheatsheet_title": "Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, standard architecture follows Google's Recommended Architecture: UI Layer (Compose + ViewModel + StateFlow) -> Domain Layer (UseCases) -> Data Layer (Repositories + DataSources).</p>
        <p>In iOS, Senior engineers build <strong>Clean Architecture with Unidirectional Data Flow (UDF)</strong>. Views emit Actions -> Reducer/ViewModel computes pure next State -> View renders new State. Use Cases isolate domain business rules, keeping ViewModels free of raw networking or database calls.</p>
        """,
        "core_sections_html": """
        <h3>1. The Unidirectional Data Flow Cycle</h3>
        <p>State flows down; actions flow up. ViewModels cannot mutate state arbitrarily outside action handlers:</p>
        <pre><code>struct AppState: Equatable {
    var count: Int = 0
    var isLoading: Bool = false
}

enum AppAction {
    case increment
    case decrement
    case setLoading(Bool)
}

func appReducer(state: inout AppState, action: AppAction) {
    switch action {
    case .increment:
        state.count += 1
    case .decrement:
        state.count -= 1
    case .setLoading(let loading):
        state.isLoading = loading
    }
}</code></pre>

        <h3>2. Use Cases / Interactors</h3>
        <p>A Use Case encapsulates a single business transaction (e.g. <code>AuthenticateUserUseCase</code>, <code>FetchMetricsUseCase</code>). ViewModels depend on Use Cases, never on raw database models or network endpoints.</p>
        """,
        "failure_mode_html": """
        <p><strong>Massive View Controller / Massive ViewModel:</strong> Cramming network requests, Core Data contexts, notification observers, and formatting logic into a single ViewModel makes unit testing impossible. Decompose responsibilities into Domain Use Cases.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary benefit of Unidirectional Data Flow (UDF) in large iOS applications?",
                "correct_idx": 1,
                "options": [
                    {"text": "Bypasses all Swift 6 concurrency warnings.", "reason": "Option A is wrong because UDF architecture is orthogonal to language concurrency safety."},
                    {"text": "Makes state mutations predictable and testable.", "reason": "Option B is correct: state transitions occur strictly through action reducers, eliminating race conditions and simplifying unit testing."},
                    {"text": "Directly compresses PNG image assets.", "reason": "Option C is wrong because architecture does not alter media files."},
                    {"text": "Forces all functions into static dispatch.", "reason": "Option D is wrong because methods inside ViewModels can use table or static dispatch."}
                ]
            },
            {
                "question": "What is the responsibility of a Use Case (Interactor) in Clean Architecture?",
                "correct_idx": 0,
                "options": [
                    {"text": "Encapsulates a single business logic operation.", "reason": "Option A is correct: Use Cases represent reusable domain actions (e.g. RefreshFeedUseCase) decoupled from UI frameworks."},
                    {"text": "Manages Auto Layout constraints in UIKit.", "reason": "Option B is wrong because Use Cases live in the domain layer with zero UI dependencies."},
                    {"text": "Handles push notification APNs tokens.", "reason": "Option C is wrong because notifications are managed in AppDelegate or notification managers."},
                    {"text": "Compiles Swift packages into Mach-O binaries.", "reason": "Option D is wrong because build compilation is handled by SPM."}
                ]
            }
        ],
        "exercise": {
            "title": "Pure Reducer State Machine",
            "instructions": "Implement <code>cartReducer</code>. Handle <code>.addItem(let item)</code> (appends item) and <code>.clearCart</code> (empties items). Run tests.",
            "starter_code": """import Foundation

struct CartItem: Equatable {
    let id: String
    let price: Double
}

struct CartState: Equatable {
    var items: [CartItem] = []
    var totalPrice: Double {
        items.reduce(0) { $0 + $1.price }
    }
}

enum CartAction {
    case addItem(CartItem)
    case clearCart
}

func cartReducer(state: inout CartState, action: CartAction) {
    // TODO: Handle CartAction
    // - .addItem: append item to state.items
    // - .clearCart: state.items.removeAll()
    fatalError("TODO: Implement cartReducer")
}

func runTests() {
    print("🧪 Running Lesson 5.1 Verification Tests...")
    var state = CartState()

    cartReducer(state: &state, action: .addItem(CartItem(id: "c1", price: 25.0)))
    cartReducer(state: &state, action: .addItem(CartItem(id: "c2", price: 15.0)))
    assert(state.items.count == 2, "Test 1 Failed")
    assert(state.totalPrice == 40.0, "Test 2 Failed: Expected 40.0, got \\(state.totalPrice)")

    cartReducer(state: &state, action: .clearCart)
    assert(state.items.isEmpty, "Test 3 Failed: Cart not cleared")
    assert(state.totalPrice == 0.0, "Test 4 Failed")
    print("  ✅ Challenge Passed: UDF pure reducer state machine verified")
}
runTests()""",
            "hints": [
                "switch action { case .addItem(let item): state.items.append(item); case .clearCart: state.items.removeAll() }"
            ],
            "solution_code": """import Foundation

struct CartItem: Equatable {
    let id: String
    let price: Double
}

struct CartState: Equatable {
    var items: [CartItem] = []
    var totalPrice: Double {
        items.reduce(0) { $0 + $1.price }
    }
}

enum CartAction {
    case addItem(CartItem)
    case clearCart
}

func cartReducer(state: inout CartState, action: CartAction) {
    switch action {
    case .addItem(let item):
        state.items.append(item)
    case .clearCart:
        state.items.removeAll()
    }
}

func runTests() {
    print("🧪 Running Lesson 5.1 Verification Tests...")
    var state = CartState()
    cartReducer(state: &state, action: .addItem(CartItem(id: "c1", price: 25.0)))
    cartReducer(state: &state, action: .addItem(CartItem(id: "c2", price: 15.0)))
    assert(state.items.count == 2)
    assert(state.totalPrice == 40.0)

    cartReducer(state: &state, action: .clearCart)
    assert(state.items.isEmpty)
    assert(state.totalPrice == 0.0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Pure reducers take inout state and an action, mutating state deterministically. Because reducers have no side-effects or asynchronous dependencies, they can be unit-tested instantaneously with 100% coverage."
        },
        "primary_sources": [
            {"title": "Point-Free: The Composable Architecture (TCA)", "url": "https://www.pointfree.co/collections/composable-architecture", "desc": "Foundational architectural exploration of UDF in modern Swift."},
            {"title": "Clean Architecture by Robert C. Martin", "url": "https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html", "desc": "Core principles of entity and use-case separation."}
        ],
        "coaching_prompt": "Notice how the reducer is a pure function. It makes zero network calls and touches no databases. That is what makes UDF architectures so reliable."
    },

    # 0035: Lesson 5.2
    {
        "id": "0035",
        "slug": "0035-scalable-dependency-injection",
        "phase_num": 5,
        "phase_title": "Phase 5: Architecture, Dependency Injection & Modularization",
        "lesson_num": "Lesson 5.2",
        "title": "Scalable Dependency Injection Architecture",
        "subtitle": "Protocol-based constructor injection, container factories, environment injection, and eliminating global singletons.",
        "prev_url": "0034-enterprise-clean-architecture-and-udf.html",
        "next_url": "0036-multi-package-spm-modularization.html",
        "cheatsheet_url": "../reference/architecture-and-modularization-cheatsheet.html",
        "cheatsheet_title": "Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, dependency injection is almost always handled by Dagger 2 or Hilt (using compiler annotation processors) or Koin (service locator).</p>
        <p>In iOS, commercial teams favor <strong>Protocol-Based Constructor Injection</strong> and <strong>Container Factories</strong> over heavy third-party DI reflection frameworks. Dependencies are passed explicitly via initializers, enabling hermetic unit test mocking with zero runtime reflection overhead.</p>
        """,
        "core_sections_html": """
        <h3>1. Protocol-Based Constructor Injection</h3>
        <pre><code>protocol NetworkServiceProtocol: Sendable {
    func fetchMetrics() async throws -> [Metric]
}

final class DashboardViewModel {
    private let networkService: any NetworkServiceProtocol

    // Dependencies injected via constructor:
    init(networkService: any NetworkServiceProtocol) {
        self.networkService = networkService
    }
}</code></pre>

        <h3>2. Container Factories</h3>
        <p>Centralize assembly in an AppContainer to avoid scattering concrete constructor calls across the codebase:</p>
        <pre><code>final class AppContainer {
    let networkService: any NetworkServiceProtocol
    let databaseService: any DatabaseServiceProtocol

    init(isTesting: Bool = false) {
        if isTesting {
            self.networkService = MockNetworkService()
            self.databaseService = MockDatabaseService()
        } else {
            self.networkService = LiveNetworkService()
            self.databaseService = LiveDatabaseService()
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Overusing Global Singletons (Service.shared):</strong> Scattering <code>NetworkService.shared</code> across classes couples code to concrete implementations, preventing parallel unit testing and introducing hidden global mutable state.</p>
        """,
        "quizzes": [
            {
                "question": "Why is Constructor Injection preferred over global singletons (Service.shared) in Senior iOS architecture?",
                "correct_idx": 1,
                "options": [
                    {"text": "Bypasses all Swift compiler type checking.", "reason": "Option A is wrong because constructor injection is 100% type-safe."},
                    {"text": "Enables hermetic mocking and parallel unit tests.", "reason": "Option B is correct: passing dependencies explicitly via protocols allows test suites to inject mock stubs easily without global state bleeding."},
                    {"text": "Reduces application binary size in App Store.", "reason": "Option C is wrong because injection patterns do not significantly alter Mach-O binary size."},
                    {"text": "Eliminates the need for any local storage.", "reason": "Option D is wrong because data storage is separate from DI."}
                ]
            },
            {
                "question": "What is an AppContainer factory in mobile engineering?",
                "correct_idx": 0,
                "options": [
                    {"text": "A centralized object assembling dependency graphs.", "reason": "Option A is correct: AppContainer acts as the root composition factory instantiating services and injecting them into view models."},
                    {"text": "A specialized Docker image running on iOS.", "reason": "Option B is wrong because iOS does not run Docker containers."},
                    {"text": "A Core Data SQLite persistent store container.", "reason": "Option C is wrong because that is NSPersistentContainer."},
                    {"text": "A Keychain access group entitlement token.", "reason": "Option D is wrong because Keychain groups govern security entitlements."}
                ]
            }
        ],
        "exercise": {
            "title": "Protocol-Based Service Factory Assembler",
            "instructions": "Implement <code>AppFactory.makeViewModel(isTest:)</code>. If <code>isTest</code> is true, inject <code>MockAuthService</code>; otherwise inject <code>LiveAuthService</code>. Run tests.",
            "starter_code": """import Foundation

protocol AuthService: Sendable {
    func authenticate() -> String
}

struct LiveAuthService: AuthService {
    func authenticate() -> String { "LIVE_AUTH_TOKEN" }
}

struct MockAuthService: AuthService {
    func authenticate() -> String { "MOCK_TEST_TOKEN" }
}

final class LoginViewModel {
    let authService: any AuthService
    init(authService: any AuthService) {
        self.authService = authService
    }
}

struct AppFactory {
    // TODO: Implement makeViewModel(isTest: Bool) -> LoginViewModel
    // If isTest is true, inject MockAuthService(), else LiveAuthService()
    static func makeViewModel(isTest: Bool) -> LoginViewModel {
        fatalError("TODO: Implement makeViewModel")
    }
}

func runTests() {
    print("🧪 Running Lesson 5.2 Verification Tests...")
    let testVM = AppFactory.makeViewModel(isTest: true)
    assert(testVM.authService.authenticate() == "MOCK_TEST_TOKEN", "Test 1 Failed")

    let liveVM = AppFactory.makeViewModel(isTest: false)
    assert(liveVM.authService.authenticate() == "LIVE_AUTH_TOKEN", "Test 2 Failed")
    print("  ✅ Challenge Passed: Protocol-based DI factory verified")
}
runTests()""",
            "hints": [
                "let service: any AuthService = isTest ? MockAuthService() : LiveAuthService()",
                "return LoginViewModel(authService: service)"
            ],
            "solution_code": """import Foundation

protocol AuthService: Sendable {
    func authenticate() -> String
}

struct LiveAuthService: AuthService {
    func authenticate() -> String { "LIVE_AUTH_TOKEN" }
}

struct MockAuthService: AuthService {
    func authenticate() -> String { "MOCK_TEST_TOKEN" }
}

final class LoginViewModel {
    let authService: any AuthService
    init(authService: any AuthService) {
        self.authService = authService
    }
}

struct AppFactory {
    static func makeViewModel(isTest: Bool) -> LoginViewModel {
        let service: any AuthService = isTest ? MockAuthService() : LiveAuthService()
        return LoginViewModel(authService: service)
    }
}

func runTests() {
    print("🧪 Running Lesson 5.2 Verification Tests...")
    let testVM = AppFactory.makeViewModel(isTest: true)
    assert(testVM.authService.authenticate() == "MOCK_TEST_TOKEN")

    let liveVM = AppFactory.makeViewModel(isTest: false)
    assert(liveVM.authService.authenticate() == "LIVE_AUTH_TOKEN")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Constructor injection decouples the consumer (LoginViewModel) from the concrete service implementation. Centralizing instantiation in AppFactory enables swapping live services with test doubles effortlessly."
        },
        "primary_sources": [
            {"title": "Apple Developer: Dependency Injection in SwiftUI", "url": "https://developer.apple.com/documentation/swiftui", "desc": "Idiomatic dependency management using constructor and environment injection."},
            {"title": "Point-Free: Designing Dependencies", "url": "https://www.pointfree.co/collections/dependencies", "desc": "Deep architectural analysis of controllable dependency injection."}
        ],
        "coaching_prompt": "Constructor injection gives you compiler-verified dependency graphs: if a dependency is missing, your app fails to compile rather than crashing at runtime!"
    },

    # 0036: Lesson 5.3
    {
        "id": "0036",
        "slug": "0036-multi-package-spm-modularization",
        "phase_num": 5,
        "phase_title": "Phase 5: Architecture, Dependency Injection & Modularization",
        "lesson_num": "Lesson 5.3",
        "title": "Multi-Package SPM Modularization Architecture",
        "subtitle": "Decomposing monolithic Xcode projects into local Swift Package Manager modules: CoreNetwork, CoreDatabase, Features, and DesignSystem.",
        "prev_url": "0035-scalable-dependency-injection.html",
        "next_url": "0037-schemes-xcconfig-and-launch-time.html",
        "cheatsheet_url": "../reference/architecture-and-modularization-cheatsheet.html",
        "cheatsheet_title": "Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, multi-module architectures decompose apps into Gradle subprojects (<code>:core:network</code>, <code>:feature:login</code>) defined in <code>settings.gradle.kts</code>.</p>
        <p>In iOS, modern enterprise apps use <strong>Swift Package Manager (SPM)</strong> to split code into decoupled local packages. SPM eliminates brittle Xcode project file conflicts (<code>.pbxproj</code> merge conflicts) and enables <strong>parallel incremental compilation</strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. The Enterprise SPM Module Graph</h3>
        <pre><code>App Target (Runner)
    ├── FeatureAuth ──> DesignSystem
    ├── FeatureDashboard ──> DesignSystem
    ├── CoreNetwork (URLSession, Interceptors)
    ├── CoreDatabase (SwiftData, SQLite)
    └── DomainModels (Pure Entities, Protocols)</code></pre>

        <h3>2. Package.swift Manifest Architecture</h3>
        <pre><code>// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "CoreModules",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "CoreNetwork", targets: ["CoreNetwork"]),
        .library(name: "DomainModels", targets: ["DomainModels"])
    ],
    targets: [
        .target(name: "DomainModels"),
        .target(name: "CoreNetwork", dependencies: ["DomainModels"]),
        .testTarget(name: "CoreNetworkTests", dependencies: ["CoreNetwork"])
    ]
)</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Circular Module Dependencies:</strong> If <code>CoreNetwork</code> imports <code>DomainModels</code> and <code>DomainModels</code> imports <code>CoreNetwork</code>, the build graph deadlocks and fails to compile. Strict dependency inversion (high-level modules depend on abstractions) prevents circularity.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary build-system advantage of multi-package SPM modularization?",
                "correct_idx": 1,
                "options": [
                    {"text": "Bypasses all App Store distribution signing.", "reason": "Option A is wrong because code signing is required for all apps."},
                    {"text": "Enables parallel concurrent compilation targets.", "reason": "Option B is correct: independent SPM targets can be compiled in parallel across CPU cores, dramatically slashing build times."},
                    {"text": "Disables memory retain cycles in classes.", "reason": "Option C is wrong because modularization does not alter ARC."},
                    {"text": "Converts all UIKit code into Jetpack Compose.", "reason": "Option D is wrong because Swift code stays on the Apple platform."}
                ]
            },
            {
                "question": "What happens if Module A depends on Module B, and Module B depends on Module A?",
                "correct_idx": 0,
                "options": [
                    {"text": "A circular dependency build error occurs.", "reason": "Option A is correct: build systems require a Directed Acyclic Graph (DAG); cycles cannot be resolved and trigger compilation failure."},
                    {"text": "The compiler merges them into one single file.", "reason": "Option B is wrong because SPM does not automatically merge targets."},
                    {"text": "The app runs twice as fast at launch.", "reason": "Option C is wrong because circular dependencies prevent compilation."},
                    {"text": "The OS reboots into recovery diagnostics.", "reason": "Option D is wrong because build errors are caught in Xcode."}
                ]
            }
        ],
        "exercise": {
            "title": "Module Dependency Graph Validator",
            "instructions": "Implement <code>hasCycle</code> in <code>DependencyGraph</code>. Detect whether a circular dependency exists in the package target graph using Depth-First Search (DFS). Run tests.",
            "starter_code": """import Foundation

final class DependencyGraph {
    private var adjList: [String: [String]] = [:]

    func addEdge(from: String, to: String) {
        adjList[from, default: []].append(to)
    }

    // TODO: Implement hasCycle() -> Bool
    // Return true if any circular dependency exists in the graph
    func hasCycle() -> Bool {
        var visited: Set<String> = []
        var recStack: Set<String> = []

        func dfs(_ node: String) -> Bool {
            visited.insert(node)
            recStack.insert(node)
            for neighbor in adjList[node, default: []] {
                if !visited.contains(neighbor) {
                    if dfs(neighbor) { return true }
                } else if recStack.contains(neighbor) {
                    return true // Cycle detected!
                }
            }
            recStack.remove(node)
            return false
        }

        for node in adjList.keys {
            if !visited.contains(node) {
                if dfs(node) { return true }
            }
        }
        return false
    }
}

func runTests() {
    print("🧪 Running Lesson 5.3 Verification Tests...")
    let acyclicGraph = DependencyGraph()
    acyclicGraph.addEdge(from: "App", to: "Features")
    acyclicGraph.addEdge(from: "Features", to: "CoreNetwork")
    acyclicGraph.addEdge(from: "CoreNetwork", to: "Domain")
    assert(acyclicGraph.hasCycle() == false, "Test 1 Failed: Acyclic graph flagged as cycle")

    let cyclicGraph = DependencyGraph()
    cyclicGraph.addEdge(from: "ModuleA", to: "ModuleB")
    cyclicGraph.addEdge(from: "ModuleB", to: "ModuleA")
    assert(cyclicGraph.hasCycle() == true, "Test 2 Failed: Circular dependency not detected")
    print("  ✅ Challenge Passed: Dependency graph cycle validation verified")
}
runTests()""",
            "hints": [
                "Implement DFS tracking visited and recStack sets",
                "If neighbor is in recStack, return true (cycle detected)"
            ],
            "solution_code": """import Foundation

final class DependencyGraph {
    private var adjList: [String: [String]] = [:]

    func addEdge(from: String, to: String) {
        adjList[from, default: []].append(to)
    }

    func hasCycle() -> Bool {
        var visited: Set<String> = []
        var recStack: Set<String> = []

        func dfs(_ node: String) -> Bool {
            visited.insert(node)
            recStack.insert(node)
            for neighbor in adjList[node, default: []] {
                if !visited.contains(neighbor) {
                    if dfs(neighbor) { return true }
                } else if recStack.contains(neighbor) {
                    return true
                }
            }
            recStack.remove(node)
            return false
        }

        for node in adjList.keys {
            if !visited.contains(node) {
                if dfs(node) { return true }
            }
        }
        return false
    }
}

func runTests() {
    print("🧪 Running Lesson 5.3 Verification Tests...")
    let acyclicGraph = DependencyGraph()
    acyclicGraph.addEdge(from: "App", to: "Features")
    acyclicGraph.addEdge(from: "Features", to: "CoreNetwork")
    acyclicGraph.addEdge(from: "CoreNetwork", to: "Domain")
    assert(acyclicGraph.hasCycle() == false)

    let cyclicGraph = DependencyGraph()
    cyclicGraph.addEdge(from: "ModuleA", to: "ModuleB")
    cyclicGraph.addEdge(from: "ModuleB", to: "ModuleA")
    assert(cyclicGraph.hasCycle() == true)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "SPM packages require a strict Directed Acyclic Graph (DAG). Modeling dependency validation with DFS cycle detection ensures modular architecture stays clean and buildable across large engineering teams."
        },
        "primary_sources": [
            {"title": "Apple Developer: Swift Package Manager", "url": "https://developer.apple.com/documentation/xcode/swift-packages", "desc": "Official Apple documentation on organizing code into SPM packages."},
            {"title": "WWDC Session: Create Swift packages", "url": "https://developer.apple.com/videos/play/wwdc2019/408/", "desc": "Apple engineers explain Package.swift dependency configuration."}
        ],
        "coaching_prompt": "In multi-module SPM projects, keeping DomainModels free of framework dependencies allows both CoreNetwork and CoreDatabase to import Domain without circularity."
    },

    # 0037: Lesson 5.4
    {
        "id": "0037",
        "slug": "0037-schemes-xcconfig-and-launch-time",
        "phase_num": 5,
        "phase_title": "Phase 5: Architecture, Dependency Injection & Modularization",
        "lesson_num": "Lesson 5.4",
        "title": "Build Schemes, xcconfig & dyld4 Pre-Main Launch Time",
        "subtitle": "Managing Debug/Staging/Production configurations safely, and optimizing pre-main launch time under 400ms.",
        "prev_url": "0036-multi-package-spm-modularization.html",
        "next_url": "../lessons/0038-modern-swift-testing-framework.html",
        "cheatsheet_url": "../reference/architecture-and-modularization-cheatsheet.html",
        "cheatsheet_title": "Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, Gradle <code>productFlavors</code> and <code>buildTypes</code> configure <code>BuildConfig</code> variables (<code>BASE_URL</code>, <code>API_KEY</code>).</p>
        <p>In iOS, commercial teams use <strong>Xcode Schemes</strong> coupled with plain-text <strong><code>.xcconfig</code> files</strong>. Furthermore, on iOS, the Mach-O dynamic linker (<strong>dyld4</strong>) parses all dynamic libraries before <code>main()</code> is even reached. Having 30+ dynamic frameworks slows app cold launch by hundreds of milliseconds. Senior engineers optimize launch time by using <strong>static SPM libraries</strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. Multi-Environment .xcconfig Architecture</h3>
        <pre><code>// Config-Release.xcconfig
API_BASE_URL = https:/$()/api.pulseflow.app/v1
BUNDLE_IDENTIFIER = com.pulseflow.enterprise
APP_NAME = PulseFlow</code></pre>

        <h3>2. The Pre-Main Launch Timeline (&lt; 400ms Goal)</h3>
        <ol>
          <li><strong>dylib loading:</strong> dyld4 parses Mach-O headers and maps dynamic libraries into memory. (Minimized by using static SPM libraries!).</li>
          <li><strong>Rebasing & Binding:</strong> Adjusting internal pointer addresses due to ASLR (Address Space Layout Randomization).</li>
          <li><strong>ObjC & Swift Runtime Setup:</strong> Class registration and category attachments.</li>
          <li><strong>Initializers:</strong> Running C++ static constructors and legacy <code>+load</code> methods. (Avoid <code>+load</code>; use <code>+initialize</code>).</li>
        </ol>
        """,
        "failure_mode_html": """
        <p><strong>Overusing Dynamic Frameworks:</strong> Dynamic frameworks (dylibs) require individual rebasing and binding by dyld4 during app cold start. Converting internal SPM modules to static libraries (.library(type: .static)) links symbols directly into the app binary, eliminating dyld binding overhead.</p>
        """,
        "quizzes": [
            {
                "question": "What diagnostic environment variable can you enable in Xcode Scheme arguments to profile pre-main launch time?",
                "correct_idx": 1,
                "options": [
                    {"text": "PRINT_ALL_STACK_FRAMES", "reason": "Option A is wrong because that is not an Apple dynamic linker flag."},
                    {"text": "DYLD_PRINT_STATISTICS", "reason": "Option B is correct: DYLD_PRINT_STATISTICS prints detailed millisecond breakdowns of pre-main dylib loading, rebasing, and initializers."},
                    {"text": "ENABLE_DEBUG_MEMORY_LOCK", "reason": "Option C is wrong because that is not a launch environment variable."},
                    {"text": "OS_LOG_PRINT_TIMINGS", "reason": "Option D is wrong because unified logging does not profile dyld linker time."}
                ]
            },
            {
                "question": "Why do static libraries launch faster than dynamic libraries (dylibs) on iOS?",
                "correct_idx": 0,
                "options": [
                    {"text": "Symbols are pre-bound at build time.", "reason": "Option A is correct: static libraries are linked directly into the Mach-O executable binary, eliminating runtime dyld dynamic binding."},
                    {"text": "Static libraries run on the GPU Metal cores.", "reason": "Option B is wrong because library linkage is CPU binary structure."},
                    {"text": "Static libraries bypass Darwin kernel Jetsam.", "reason": "Option C is wrong because memory limits apply to all processes."},
                    {"text": "Dynamic libraries require network downloads.", "reason": "Option D is wrong because embedded dylibs are stored locally inside the app bundle."}
                ]
            }
        ],
        "exercise": {
            "title": "Environment Configuration Variable Resolver",
            "instructions": "Implement <code>resolveBaseURL</code> in <code>EnvironmentResolver</code>. Given an environment flag ('DEBUG', 'STAGING', 'PRODUCTION'), return the corresponding API endpoint. If unknown, return fallback. Run tests.",
            "starter_code": """import Foundation

struct EnvironmentResolver {
    // TODO: Implement resolveBaseURL(for env: String) -> String
    // "DEBUG" -> "https://api-dev.pulseflow.app"
    // "STAGING" -> "https://api-stage.pulseflow.app"
    // "PRODUCTION" -> "https://api.pulseflow.app"
    // default -> "https://api.pulseflow.app"
    static func resolveBaseURL(for env: String) -> String {
        fatalError("TODO: Implement resolveBaseURL")
    }
}

func runTests() {
    print("🧪 Running Lesson 5.4 Verification Tests...")
    assert(EnvironmentResolver.resolveBaseURL(for: "DEBUG") == "https://api-dev.pulseflow.app", "Test 1 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "STAGING") == "https://api-stage.pulseflow.app", "Test 2 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "PRODUCTION") == "https://api.pulseflow.app", "Test 3 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "UNKNOWN") == "https://api.pulseflow.app", "Test 4 Failed")
    print("  ✅ Challenge Passed: Environment configuration resolver verified")
}
runTests()""",
            "hints": [
                "switch env.uppercased() { case \"DEBUG\": return \"https://api-dev.pulseflow.app\" ... }"
            ],
            "solution_code": """import Foundation

struct EnvironmentResolver {
    static func resolveBaseURL(for env: String) -> String {
        switch env.uppercased() {
        case "DEBUG":
            return "https://api-dev.pulseflow.app"
        case "STAGING":
            return "https://api-stage.pulseflow.app"
        case "PRODUCTION":
            return "https://api.pulseflow.app"
        default:
            return "https://api.pulseflow.app"
        }
    }
}

func runTests() {
    print("🧪 Running Lesson 5.4 Verification Tests...")
    assert(EnvironmentResolver.resolveBaseURL(for: "DEBUG") == "https://api-dev.pulseflow.app")
    assert(EnvironmentResolver.resolveBaseURL(for: "STAGING") == "https://api-stage.pulseflow.app")
    assert(EnvironmentResolver.resolveBaseURL(for: "PRODUCTION") == "https://api.pulseflow.app")
    assert(EnvironmentResolver.resolveBaseURL(for: "UNKNOWN") == "https://api.pulseflow.app")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Xcode Schemes and .xcconfig files inject build configuration values into Info.plist. Resolving endpoints by environment configuration ensures clean separation between development, staging, and production release binaries."
        },
        "primary_sources": [
            {"title": "Apple Developer: Optimizing App Launch Time", "url": "https://developer.apple.com/documentation/xcode/optimizing-app-launch-time", "desc": "Official Apple performance guide on pre-main launch time and dyld4."},
            {"title": "WWDC Session: App startup: Improving launch performance", "url": "https://developer.apple.com/videos/play/wwdc2019/423/", "desc": "Apple engineers explain dylib rebasing, binding, and static linking."}
        ],
        "coaching_prompt": "Congratulations! You have completed Phase 5: Scalable Architecture, Modularization & Launch Time, covering Clean Architecture, UDF reducers, protocol DI, multi-package SPM, and pre-main launch optimization. Next: Phase 6 Deep Diagnostics, Testing & Performance Profiling!"
    }
]

print("Phase 5 data loaded (Lessons 5.1 - 5.4).")
