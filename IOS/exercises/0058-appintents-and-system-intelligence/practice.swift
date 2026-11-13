import Foundation

struct ProjectEntity: Identifiable, Equatable {
    let id: String
    let name: String
}

final class HeadlessProjectStore {
    private let projects: [ProjectEntity]
    init(projects: [ProjectEntity]) { self.projects = projects }

    func entities(for identifiers: [String]) -> [ProjectEntity] {
        // TODO: Return matching ProjectEntity items preserving identifiers order
        fatalError("TODO: Implement entities(for:)")
    }

    func suggestedEntities() -> [ProjectEntity] {
        // TODO: Return up to first 3 projects as Spotlight/Shortcuts suggestions
        fatalError("TODO: Implement suggestedEntities")
    }
}

struct SyncProjectIntent {
    let projectID: String
    let store: HeadlessProjectStore

    // TODO: Resolve project by ID; throw NSError if missing, else return "Synced:<name>"
    func perform() throws -> String {
        fatalError("TODO: Implement perform")
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.10 Verification Tests...")
    let store = HeadlessProjectStore(projects: [
        ProjectEntity(id: "p1", name: "Checkout"),
        ProjectEntity(id: "p2", name: "Search"),
        ProjectEntity(id: "p3", name: "Telemetry"),
        ProjectEntity(id: "p4", name: "Auth")
    ])
    assert(store.entities(for: ["p3", "p1"]).map(\.id) == ["p3", "p1"], "Test 1 Failed")
    assert(store.suggestedEntities().count == 3, "Test 2 Failed")
    let output = try SyncProjectIntent(projectID: "p2", store: store).perform()
    assert(output == "Synced:Search", "Test 3 Failed")
    print("  ✅ Challenge Passed: Headless AppIntent & EntityQuery resolution verified")
}
try runTests()