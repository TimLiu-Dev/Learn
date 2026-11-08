import Foundation

actor ImagePipelineCache {
    private var cachedImages: [URL: String] = [:]
    private var inFlightTasks: [URL: Task<String, Error>] = [:]

    func fetchImage(from url: URL) async throws -> String {
        // 1. Check cachedImages[url] -> if found, return it
        if let existing = cachedImages[url] { return existing }

        // 2. Check inFlightTasks[url] -> if found, return try await existingTask.value
        if let runningTask = inFlightTasks[url] { return try await runningTask.value }

        // TODO: 3. Create a new Task { ... } that sleeps 10ms and returns "image_\(url.lastPathComponent)"
        // TODO: 4. Store in inFlightTasks[url] before awaiting
        // TODO: 5. Use defer { inFlightTasks.removeValue(forKey: url) }
        // TODO: 6. Await download, store in cachedImages[url], and return result
        fatalError("TODO: Implement deduplication")
    }

    var cacheCount: Int { cachedImages.count }
}

func runTests() async {
    print("🧪 Running Lesson 2.4 Verification Tests...")
    let cache = ImagePipelineCache()
    let url = URL(string: "https://cdn.example.com/avatars/user.png")!

    // Launch 5 concurrent calls simultaneously
    let results = await withTaskGroup(of: String?.self) { group in
        for _ in 0..<5 {
            group.addTask { try? await cache.fetchImage(from: url) }
        }
        var list: [String] = []
        for await res in group { if let res { list.append(res) } }
        return list
    }

    assert(results.count == 5, "Test 1 Failed: Not all callers received result")
    assert(results.allSatisfy { $0 == "image_user.png" }, "Test 2 Failed")
    let count = await cache.cacheCount
    assert(count == 1, "Test 3 Failed: Should only cache exactly 1 item")
    print("  ✅ Challenge Passed: Actor deduplicated in-flight tasks successfully")
}

await runTests()