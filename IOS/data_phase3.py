# Phase 3: High-Performance Declarative UI (SwiftUI & UIKit)
# Lessons 0019 - 0027

PHASE3_LESSONS = [
    # 0019: Lesson 3.1
    {
        "id": "0019",
        "slug": "0019-swiftui-render-tree",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.1",
        "title": "The SwiftUI Render Tree: Identity, AttributeGraph & Re-renders",
        "subtitle": "Structural vs Explicit identity, eliminating redundant layout passes, and debugging invalidations with Self._printChanges().",
        "prev_url": "0018-enterprise-network-layer.html",
        "next_url": "0020-modern-state-management.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, UI rendering is driven by recomposition of <code>@Composable</code> functions tracked via the Slot Table. Recomposition can be skipped if parameters are <code>@Stable</code> or <code>@Immutable</code>.</p>
        <p>In SwiftUI, a <code>View</code> is not a retained screen widget or class; it is an ephemeral, lightweight <strong>struct value description</strong>. SwiftUI constructs an internal directed dependency graph called the <strong>AttributeGraph</strong>. The lifetime of a view's state (<code>@State</code>) is strictly tied to its <strong>View Identity</strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. Structural vs Explicit Identity</h3>
        <ul>
          <li><strong>Structural Identity:</strong> Determined implicitly by the view's static position within the SwiftUI hierarchy type tree.</li>
          <li><strong>Explicit Identity:</strong> Assigned explicitly using <code>.id(...)</code> or <code>ForEach(items, id: \\.id)</code>. Changing an explicit ID causes SwiftUI to destroy the existing view, discard all attached <code>@State</code>, and construct a fresh view with reset state.</li>
        </ul>

        <h3>2. The Structural Identity Branching Trap</h3>
        <pre><code>// ❌ STRUCTURAL IDENTITY TRAP:
// SwiftUI treats these as TWO SEPARATE BRANCHES (_ConditionalContent):
// Toggling 'isEditing' destroys internal state and interrupts animations!
var body: some View {
    if isEditing {
        ProfileCard(name: name, editable: true)
    } else {
        ProfileCard(name: name, editable: false)
    }
}

// ✅ SENIOR PATTERN (Preserves single persistent structural identity):
var body: some View {
    ProfileCard(name: name, editable: isEditing)
}</code></pre>

        <h3>3. Debugging Invalidation with <code>Self._printChanges()</code></h3>
        <p>Call <code>let _ = Self._printChanges()</code> inside your view's <code>body</code>. During runtime debugging in Xcode, SwiftUI prints the exact state or environment property that triggered that view's evaluation (e.g. <code>@self changed</code>, <code>_counter changed</code>).</p>
        """,
        "failure_mode_html": """
        <p><strong>Applying Random UUIDs to .id():</strong> Writing <code>.id(UUID())</code> forces SwiftUI to treat the view as a completely new view on every layout pass, continuously allocating and deallocating memory, resetting animations, and generating frame drops.</p>
        """,
        "quizzes": [
            {
                "question": "What happens to a view's attached @State properties when its explicit .id(...) value changes?",
                "correct_idx": 1,
                "options": [
                    {"text": "State values persist across the change.", "reason": "Option A is wrong because changing explicit identity destroys existing state."},
                    {"text": "State resets to initial default values.", "reason": "Option B is correct: changing explicit identity tells SwiftUI to destroy the view node and recreate fresh state."},
                    {"text": "The Darwin operating system leaks memory.", "reason": "Option C is wrong because SwiftUI cleans up the old node gracefully."},
                    {"text": "The compiler issues a static warning.", "reason": "Option D is wrong because ID changes are dynamic runtime operations."}
                ]
            },
            {
                "question": "Why does using an 'if/else' block around two slightly different configurations of the same view hurt animation performance?",
                "correct_idx": 0,
                "options": [
                    {"text": "Creates two distinct structural identities.", "reason": "Option A is correct: if/else creates _ConditionalContent with 2 identities, causing removal and insertion transitions rather than in-place property animation."},
                    {"text": "Allocates existential containers on stack.", "reason": "Option B is wrong because view modifiers do not create existential boxes."},
                    {"text": "Disables the Metal GPU rendering shader.", "reason": "Option C is wrong because Metal rendering is unaffected."},
                    {"text": "Forces the thread into an infinite loop.", "reason": "Option D is wrong because structural branching does not deadlock threads."}
                ]
            }
        ],
        "exercise": {
            "title": "View Identity & State Preservation Engine",
            "instructions": "Demonstrate the difference between structural identity and explicit identity in <code>MockAttributeGraph</code>. When an explicit ID changes, purge prior state and initialize fresh state. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

final class MockAttributeGraph {
    private var nodeRegistry: [String: [String: Any]] = [:]

    func resolveState(explicitId: String?, path: String, defaultInitialState: [String: Any]) -> [String: Any] {
        let identityKey = explicitId ?? path
        if let existing = nodeRegistry[identityKey] {
            return existing
        } else {
            nodeRegistry[identityKey] = defaultInitialState
            return defaultInitialState
        }
    }

    func mutateState(explicitId: String?, path: String, key: String, value: Any) {
        let identityKey = explicitId ?? path
        nodeRegistry[identityKey]?[key] = value
    }

    // TODO: Implement purgeExplicitIdentity(_ id: String) to remove the id from nodeRegistry
    func purgeExplicitIdentity(_ id: String) {
        fatalError("TODO: Implement purgeExplicitIdentity")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.1 Verification Tests...")
    let graph = MockAttributeGraph()

    // 1. Structural Identity: state persists across re-render because path stays invariant
    let path = "RootView/Content/ProfileCard"
    var state = graph.resolveState(explicitId: nil, path: path, defaultInitialState: ["scrollOffset": 120])
    assert((state["scrollOffset"] as? Int) == 120, "Test 1A Failed")

    graph.mutateState(explicitId: nil, path: path, key: "scrollOffset", value: 350)
    let updatedState = graph.resolveState(explicitId: nil, path: path, defaultInitialState: ["scrollOffset": 0])
    assert((updatedState["scrollOffset"] as? Int) == 350, "Test 1B Failed: State lost on same structural path")

    // 2. Explicit Identity Reset: changing explicit ID wipes prior state
    let id1 = "user_42"
    _ = graph.resolveState(explicitId: id1, path: "any_path", defaultInitialState: ["draftText": "Hello"])
    graph.mutateState(explicitId: id1, path: "any_path", key: "draftText", value: "Updated Draft")

    graph.purgeExplicitIdentity(id1)
    let id2State = graph.resolveState(explicitId: "user_43", path: "any_path", defaultInitialState: ["draftText": ""])
    assert((id2State["draftText"] as? String) == "", "Test 2 Failed: Explicit ID change should reset state")
    print("  ✅ Challenge Passed: View identity state preservation verified")
}
runTests()""",
            "hints": [
                "In purgeExplicitIdentity, write: nodeRegistry.removeValue(forKey: id)"
            ],
            "solution_code": """import Foundation

final class MockAttributeGraph {
    private var nodeRegistry: [String: [String: Any]] = [:]

    func resolveState(explicitId: String?, path: String, defaultInitialState: [String: Any]) -> [String: Any] {
        let identityKey = explicitId ?? path
        if let existing = nodeRegistry[identityKey] {
            return existing
        } else {
            nodeRegistry[identityKey] = defaultInitialState
            return defaultInitialState
        }
    }

    func mutateState(explicitId: String?, path: String, key: String, value: Any) {
        let identityKey = explicitId ?? path
        nodeRegistry[identityKey]?[key] = value
    }

    func purgeExplicitIdentity(_ id: String) {
        nodeRegistry.removeValue(forKey: id)
    }
}

func runTests() {
    print("🧪 Running Lesson 3.1 Verification Tests...")
    let graph = MockAttributeGraph()
    let path = "RootView/Content/ProfileCard"
    var state = graph.resolveState(explicitId: nil, path: path, defaultInitialState: ["scrollOffset": 120])
    assert((state["scrollOffset"] as? Int) == 120)

    graph.mutateState(explicitId: nil, path: path, key: "scrollOffset", value: 350)
    let updatedState = graph.resolveState(explicitId: nil, path: path, defaultInitialState: ["scrollOffset": 0])
    assert((updatedState["scrollOffset"] as? Int) == 350)

    let id1 = "user_42"
    _ = graph.resolveState(explicitId: id1, path: "any_path", defaultInitialState: ["draftText": "Hello"])
    graph.mutateState(explicitId: id1, path: "any_path", key: "draftText", value: "Updated Draft")
    graph.purgeExplicitIdentity(id1)
    let id2State = graph.resolveState(explicitId: "user_43", path: "any_path", defaultInitialState: ["draftText": ""])
    assert((id2State["draftText"] as? String) == "")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "In SwiftUI, state is attached to the view's identity (structural hierarchy path or explicit id). When explicit identity changes, SwiftUI purges associated state from the AttributeGraph, resetting the view node cleanly."
        },
        "primary_sources": [
            {"title": "WWDC Session: Demystify SwiftUI", "url": "https://developer.apple.com/videos/play/wwdc2021/10022/", "desc": "Essential WWDC talk on Identity, Lifetime, and Dependencies in SwiftUI."},
            {"title": "Apple Developer: View.id(_:)", "url": "https://developer.apple.com/documentation/swiftui/view/id(_:)", "desc": "Official documentation on explicit view identity."}
        ],
        "coaching_prompt": "Always preserve structural identity across boolean state changes to get smooth spring animations for free!"
    }
]

print("Phase 3 loaded (Lesson 3.1).")
