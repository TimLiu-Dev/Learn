import Foundation

struct WorkItem: Equatable {
    let id: Int
    let value: String
}

func processWorkItems(_ items: [WorkItem]) async throws -> [String] {
    var processed: [String] = []
    for item in items {
        // TODO: 1. Call try Task.checkCancellation()
        // TODO: 2. Call try await Task.sleep(nanoseconds: 1_000_000)
        // TODO: 3. Append "processed_\(item.id)" to processed
        fatalError("TODO: Implement processWorkItems")
    }
    return processed
}

func runTests() async {
    print("🧪 Running Lesson 2.1 Verification Tests...")
    let items = (1...5).map { WorkItem(id: $0, value: "val") }
    do {
        let results = try await processWorkItems(items)
        assert(results.count == 5, "Test 1A Failed")
        assert(results[0] == "processed_1", "Test 1B Failed")
        print("  ✅ Challenge 1 Passed: Cooperative processing complete")
    } catch {
        fatalError("Unexpected error: \(error)")
    }

    let task = Task {
        try await processWorkItems((1...100).map { WorkItem(id: $0, value: "val") })
    }
    task.cancel()
    do {
        _ = try await task.value
        fatalError("Test 2 Failed: Task should have thrown CancellationError")
    } catch is CancellationError {
        print("  ✅ Challenge 2 Passed: CancellationError cooperatively intercepted")
    } catch {
        fatalError("Unexpected error type: \(error)")
    }
}

await runTests()