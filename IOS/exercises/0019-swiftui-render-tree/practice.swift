import Foundation

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
runTests()