# Phase 3 Continued: Lessons 0020 - 0027

REMAINING_PHASE3 = [
    # 0020: Lesson 3.2
    {
        "id": "0020",
        "slug": "0020-modern-state-management",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.2",
        "title": "Modern State Management: @State, @Binding & @Environment",
        "subtitle": "Single source of truth, two-way bindings ($), custom EnvironmentKeys, and @FocusState mechanics.",
        "prev_url": "0019-swiftui-render-tree.html",
        "next_url": "0021-observation-framework.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, state is managed via <code>remember { mutableStateOf(...) }</code> and hoisted via function parameters <code>(value: T, onValueChange: (T) -> Unit)</code>. CompositionLocal provides ambient state (e.g. <code>LocalContext.current</code>).</p>
        <p>In SwiftUI, <strong><code>@State</code></strong> manages local private state allocated in external storage managed by SwiftUI's runtime. <strong><code>@Binding</code></strong> creates a two-way reference to upstream state without duplicating it. <strong><code>@Environment</code></strong> injects values down the view hierarchy type-safely.</p>
        """,
        "core_sections_html": """
        <h3>1. The Three State Pillars</h3>
        <ul>
          <li><strong><code>@State</code>:</strong> Source of truth. Always declared <code>private</code>. Lives across view redraws.</li>
          <li><strong><code>@Binding</code>:</strong> Two-way reference. Does not own the memory. Accessed via the <code>$</code> operator on <code>@State</code>.</li>
          <li><strong><code>@Environment</code>:</strong> Dependency injection. Propagates values implicitly down the entire sub-tree.</li>
        </ul>

        <h3>2. Defining Custom EnvironmentKeys</h3>
        <pre><code>private struct NetworkStatusKey: EnvironmentKey {
    static let defaultValue: Bool = true
}

extension EnvironmentValues {
    var isOnline: Bool {
        get { self[NetworkStatusKey.self] }
        set { self[NetworkStatusKey.self] = newValue }
    }
}

// In any child view:
struct NetworkIndicatorView: View {
    @Environment(\\.isOnline) private var isOnline
    var body: some View {
        Circle().fill(isOnline ? .green : .red)
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Initializing @State with View Parameters:</strong> Writing <code>@State private var text: String = initialText</code> inside <code>init(initialText:)</code> does NOT update when <code>initialText</code> changes in subsequent parent renders! <code>@State</code> allocates storage once during initial view node creation. Use <code>@Binding</code> or <code>.onChange(of:)</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary architectural purpose of @Binding in SwiftUI?",
                "correct_idx": 1,
                "options": [
                    {"text": "Persists state values into Apple Keychain.", "reason": "Option A is wrong because Keychain manages secure credential storage."},
                    {"text": "Provides a two-way reference to upstream state.", "reason": "Option B is correct: @Binding connects a child view to a parent's source of truth without owning or copying the state."},
                    {"text": "Allocates dedicated memory on the heap.", "reason": "Option C is wrong because @Binding stores a getter and setter closure, not a duplicated heap payload."},
                    {"text": "Disables view redraws on user interaction.", "reason": "Option D is wrong because mutating a binding triggers parent view evaluation."}
                ]
            },
            {
                "question": "Why should @State properties always be marked private?",
                "correct_idx": 0,
                "options": [
                    {"text": "State is owned strictly by that view's node.", "reason": "Option A is correct: marking private enforces the Single Source of Truth rule, preventing external callers from corrupting internal view state."},
                    {"text": "Private variables run faster on CPU cores.", "reason": "Option B is wrong because access modifiers do not alter CPU instruction speed."},
                    {"text": "Required for Objective-C runtime bridging.", "reason": "Option C is wrong because @State is pure Swift with zero ObjC exposure."},
                    {"text": "Bypasses strict Swift 6 Sendable checks.", "reason": "Option D is wrong because @State is tied to the main actor view hierarchy."}
                ]
            }
        ],
        "exercise": {
            "title": "Two-Way Binding & Environment Key Simulation",
            "instructions": "Implement <code>MockBinding</code>. It wraps a getter closure and setter closure. Complete <code>makeBinding</code> to return a two-way binding that reads and writes to a target reference. Run tests.",
            "starter_code": """import Foundation

struct MockBinding<T> {
    let get: () -> T
    let set: (T) -> Void

    var wrappedValue: T {
        get { get() }
        nonmutating set { set(newValue) }
    }
}

final class StateContainer {
    var count: Int = 0
}

func makeBinding(container: StateContainer) -> MockBinding<Int> {
    // TODO: Return MockBinding<Int>(get: { container.count }, set: { container.count = $0 })
    fatalError("TODO: Implement makeBinding")
}

func runTests() {
    print("🧪 Running Lesson 3.2 Verification Tests...")
    let container = StateContainer()
    let binding = makeBinding(container: container)

    assert(binding.wrappedValue == 0, "Test 1 Failed")
    binding.wrappedValue = 42
    assert(container.count == 42, "Test 2 Failed: Container not updated via binding")
    assert(binding.wrappedValue == 42, "Test 3 Failed")
    print("  ✅ Challenge Passed: Two-way binding mechanics verified")
}
runTests()""",
            "hints": [
                "Return MockBinding<Int>(get: { container.count }, set: { container.count = $0 })"
            ],
            "solution_code": """import Foundation

struct MockBinding<T> {
    let get: () -> T
    let set: (T) -> Void

    var wrappedValue: T {
        get { get() }
        nonmutating set { set(newValue) }
    }
}

final class StateContainer {
    var count: Int = 0
}

func makeBinding(container: StateContainer) -> MockBinding<Int> {
    MockBinding<Int>(
        get: { container.count },
        set: { container.count = $0 }
    )
}

func runTests() {
    print("🧪 Running Lesson 3.2 Verification Tests...")
    let container = StateContainer()
    let binding = makeBinding(container: container)
    assert(binding.wrappedValue == 0)
    binding.wrappedValue = 42
    assert(container.count == 42)
    assert(binding.wrappedValue == 42)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "In SwiftUI, @Binding is conceptually a struct containing a get closure and a set closure. When a child view updates the binding's wrappedValue, it calls the setter closure, which updates the upstream source of truth without duplicating state."
        },
        "primary_sources": [
            {"title": "Apple Developer: State and Data Flow", "url": "https://developer.apple.com/documentation/swiftui/state-and-data-flow", "desc": "Official Apple guide on @State, @Binding, and @Environment."},
            {"title": "Apple Developer: EnvironmentKey", "url": "https://developer.apple.com/documentation/swiftui/environmentkey", "desc": "Official documentation on custom environment values."}
        ],
        "coaching_prompt": "Always ask: does this view OWN this state, or does it merely NEED TO READ/WRITE it? If it doesn't own it, use @Binding!"
    },

    # 0021: Lesson 3.3
    {
        "id": "0021",
        "slug": "0021-observation-framework",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.3",
        "title": "The Observation Framework (@Observable) in Production",
        "subtitle": "Fine-grained property-level dependency tracking, @Bindable, and eliminating legacy ObservableObject coarse invalidations.",
        "prev_url": "0020-modern-state-management.html",
        "next_url": "0022-layout-mastery-and-custom-layouts.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Compose), reads of <code>State&lt;T&gt;</code> inside a composable function register that function in the snapshot observer. Only the composable reading that specific field recomposes when the field changes.</p>
        <p>In SwiftUI (iOS 17+), Apple introduced the <strong>Observation Framework (<code>@Observable</code> macro)</strong>. Legacy <code>ObservableObject</code> used Combine's <code>objectWillChange</code>, which re-rendered EVERY view observing the class whenever ANY property changed! <code>@Observable</code> tracks dependencies at the <strong>individual property read level</strong>: views only re-evaluate when properties they actually access change!</p>
        """,
        "core_sections_html": """
        <h3>1. Migrating to @Observable (iOS 17+)</h3>
        <pre><code>// ❌ Legacy ObservableObject (Coarse invalidation):
// class UserViewModel: ObservableObject {
//     @Published var name: String = ""
//     @Published var age: Int = 0
// }

// ✅ Modern Observation Framework (Fine-grained):
@Observable
final class UserViewModel {
    var name: String = ""
    var age: Int = 0
}</code></pre>

        <h3>2. Two-Way Bindings with <code>@Bindable</code></h3>
        <p>When you need to pass a property of an <code>@Observable</code> object to a control requiring a <code>Binding</code> (e.g. <code>TextField</code>), wrap it with <code>@Bindable</code>:</p>
        <pre><code>struct UserProfileView: View {
    @Bindable var viewModel: UserViewModel

    var body: some View {
        TextField("Name", text: $viewModel.name)
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Mixing <code>@ObservedObject</code> with <code>@Observable</code>:</strong> Using legacy property wrappers (<code>@ObservedObject</code>, <code>@StateObject</code>) on a type marked with <code>@Observable</code> causes compile errors or breaks dependency tracking. Use simple bare <code>var viewModel: ViewModel</code> or <code>@Bindable</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary performance advantage of @Observable over legacy ObservableObject?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles Swift code directly into C++.", "reason": "Option A is wrong because Observation does not compile into C++."},
                    {"text": "Tracks dependencies at individual property level.", "reason": "Option B is correct: views only re-render if they read the specific property that changed, eliminating unnecessary re-renders."},
                    {"text": "Allocates class memory on CPU registers.", "reason": "Option C is wrong because class instances remain on the heap."},
                    {"text": "Bypasses all @MainActor thread rules.", "reason": "Option D is wrong because UI classes still require @MainActor."}
                ]
            },
            {
                "question": "When is the @Bindable property wrapper used with @Observable classes?",
                "correct_idx": 0,
                "options": [
                    {"text": "When creating two-way $ bindings to properties.", "reason": "Option A is correct: @Bindable generates projected bindings ($viewModel.name) for controls like TextField."},
                    {"text": "When making an actor conform to Sendable.", "reason": "Option B is wrong because @Bindable is a SwiftUI UI construct."},
                    {"text": "When decoding JSON payloads from server.", "reason": "Option C is wrong because decoding uses Decodable."},
                    {"text": "When executing unit tests in Swift Testing.", "reason": "Option D is wrong because testing uses @Test."}
                ]
            }
        ],
        "exercise": {
            "title": "Field-Level Property Tracking Engine",
            "instructions": "Demonstrate fine-grained dependency tracking. In <code>MockObservationRegistrar</code>, record property access. Verify that views reading property A do NOT invalidate when property B changes. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

final class MockObservationRegistrar {
    private var observers: [String: Set<String>] = [:] // propertyName -> Set<viewId>

    func access(property: String, viewId: String) {
        observers[property, default: []].insert(viewId)
    }

    // TODO: Implement invalidations(for property: String) -> Set<String>
    // Return the set of viewIds observing that property (or empty set if none)
    func invalidations(for property: String) -> Set<String> {
        fatalError("TODO: Implement invalidations")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.3 Verification Tests...")
    let registrar = MockObservationRegistrar()

    // View 1 only reads "name"
    registrar.access(property: "name", viewId: "HeaderView")

    // View 2 only reads "age"
    registrar.access(property: "age", viewId: "AgeLabel")

    // When "name" mutates:
    let nameInv = registrar.invalidations(for: "name")
    assert(nameInv == ["HeaderView"], "Test 1 Failed: Expected only HeaderView to invalidate")

    // When "age" mutates:
    let ageInv = registrar.invalidations(for: "age")
    assert(ageInv == ["AgeLabel"], "Test 2 Failed: Expected only AgeLabel to invalidate")
    print("  ✅ Challenge Passed: Fine-grained property observation verified")
}
runTests()""",
            "hints": [
                "In invalidations(for:), return observers[property] ?? []"
            ],
            "solution_code": """import Foundation

final class MockObservationRegistrar {
    private var observers: [String: Set<String>] = [:]

    func access(property: String, viewId: String) {
        observers[property, default: []].insert(viewId)
    }

    func invalidations(for property: String) -> Set<String> {
        observers[property] ?? []
    }
}

func runTests() {
    print("🧪 Running Lesson 3.3 Verification Tests...")
    let registrar = MockObservationRegistrar()
    registrar.access(property: "name", viewId: "HeaderView")
    registrar.access(property: "age", viewId: "AgeLabel")

    let nameInv = registrar.invalidations(for: "name")
    assert(nameInv == ["HeaderView"])

    let ageInv = registrar.invalidations(for: "age")
    assert(ageInv == ["AgeLabel"])
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Under the hood, the @Observable macro injects an ObservationRegistrar. When a view accesses a property in its body, access(keyPath:) registers that view. When the setter runs, withMutation(keyPath:) invalidates ONLY views registered to that specific property."
        },
        "primary_sources": [
            {"title": "Apple Developer: Observation Framework", "url": "https://developer.apple.com/documentation/observation", "desc": "Official Apple reference for @Observable and ObservationRegistrar."},
            {"title": "WWDC Session: Discover Observation in SwiftUI", "url": "https://developer.apple.com/videos/play/wwdc2023/10149/", "desc": "Apple engineers explain how Observation revolutionizes SwiftUI performance."}
        ],
        "coaching_prompt": "Always use @Observable for any new iOS 17+ code. It completely eliminates the notorious 'entire screen re-renders' bug from legacy Combine ObservableObject!"
    }
]

print("Phase 3 part loaded (Lessons 3.2 - 3.3).")
