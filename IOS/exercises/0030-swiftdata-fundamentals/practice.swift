import Foundation

struct ManagedItem: Equatable {
    let id: String
    var title: String
}

final class MockModelContext {
    private var storage: [String: ManagedItem] = [:]

    // TODO: Implement insert(_ item: ManagedItem) -> store in storage[item.id]
    func insert(_ item: ManagedItem) {
        fatalError("TODO: Implement insert")
    }

    // TODO: Implement fetchAll() -> [ManagedItem]
    func fetchAll() -> [ManagedItem] {
        fatalError("TODO: Implement fetchAll")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.3 Verification Tests...")
    let context = MockModelContext()
    context.insert(ManagedItem(id: "item_1", title: "Metrics"))
    context.insert(ManagedItem(id: "item_2", title: "Logs"))

    let items = context.fetchAll()
    assert(items.count == 2, "Test 1 Failed: Expected 2 items, got \(items.count)")
    assert(items.contains(ManagedItem(id: "item_1", title: "Metrics")), "Test 2 Failed")
    print("  ✅ Challenge Passed: In-memory ModelContext CRUD verified")
}
runTests()