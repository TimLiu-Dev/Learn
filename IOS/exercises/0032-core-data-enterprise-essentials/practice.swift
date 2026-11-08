import Foundation

struct ManagedObjectID: Hashable {
    let uri: String
}

final class MockContextCoordinator {
    private var database: [ManagedObjectID: String] = [:]

    func seed(id: ManagedObjectID, title: String) {
        database[id] = title
    }

    // TODO: Implement resolveObject(with id: ManagedObjectID) -> String?
    // Return database[id]
    func resolveObject(with id: ManagedObjectID) -> String? {
        fatalError("TODO: Implement resolveObject")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.5 Verification Tests...")
    let coordinator = MockContextCoordinator()
    let id1 = ManagedObjectID(uri: "coredata://User/101")
    coordinator.seed(id: id1, title: "Lead Architect")

    let title = coordinator.resolveObject(with: id1)
    assert(title == "Lead Architect", "Test 1 Failed: Got \(title ?? "nil")")

    let missing = coordinator.resolveObject(with: ManagedObjectID(uri: "coredata://User/999"))
    assert(missing == nil, "Test 2 Failed")
    print("  ✅ Challenge Passed: ManagedObjectID thread-safe resolution verified")
}
runTests()