import Foundation

actor AuthTokenActor {
    private var currentToken: String = "expired_token"
    private var activeRefreshTask: Task<String, Error>?
    var refreshExecutionCount = 0

    func fetchToken() async throws -> String {
        // TODO: 1. If activeRefreshTask exists, return try await activeRefreshTask.value
        // TODO: 2. Otherwise create a new Task<String, Error> { ... }
        //          Inside Task: sleep 10ms, increment refreshExecutionCount, return "fresh_token_123"
        // TODO: 3. Set activeRefreshTask = task
        // TODO: 4. Use defer { activeRefreshTask = nil }
        // TODO: 5. Await task value, update currentToken, and return it
        fatalError("TODO: Implement fetchToken")
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.10 Verification Tests...")
    let auth = AuthTokenActor()

    // Simulate 5 simultaneous requests trying to fetch a fresh token
    let tokens = await withTaskGroup(of: String?.self) { group in
        for _ in 0..<5 {
            group.addTask { try? await auth.fetchToken() }
        }
        var res: [String] = []
        for await t in group { if let t { res.append(t) } }
        return res
    }

    assert(tokens.count == 5, "Test 1 Failed")
    assert(tokens.allSatisfy { $0 == "fresh_token_123" }, "Test 2 Failed")
    let count = await auth.refreshExecutionCount
    assert(count == 1, "Test 3 Failed: Token refreshed multiple times! (Expected exactly 1, got \(count))")
    print("  ✅ Challenge Passed: Actor-synchronized token refresh deduplicated successfully")
}

await runTests()