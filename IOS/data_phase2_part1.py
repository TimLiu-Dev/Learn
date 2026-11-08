# Phase 2 Complete: Modern Swift 6 Concurrency & Networking (Lessons 0009 - 0018)

LESSON_0009 = {
    "id": "0009",
    "slug": "0009-concurrency-fundamentals",
    "phase_num": 2,
    "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
    "lesson_num": "Lesson 2.1",
    "title": "Fundamentals of Swift Concurrency: async, await & Tasks",
    "subtitle": "Task lifecycles, cooperative cancellation, thread hopping, and the Darwin cooperative thread pool.",
    "prev_url": "0008-property-wrappers-and-macros.html",
    "next_url": "0010-structured-concurrency.html",
    "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
    "cheatsheet_title": "Swift Concurrency Cheatsheet",
    "android_anchor_html": """
    <p>In Android (Kotlin), coroutines run inside a <code>CoroutineScope</code> with explicit <code>Dispatchers</code>. Dispatchers are backed by unbounded or flexible thread pools.</p>
    <p>In Swift, concurrency is built on a <strong>cooperative thread pool</strong> bounded strictly to CPU core count. There is <strong>no thread explosion</strong>. When a function hits <code>await</code>, it yields the physical thread, and Darwin schedules other tasks. Tasks carry priority and cooperative cancellation tokens.</p>
    """,
    "core_sections_html": """
    <h3>1. The Cooperative Thread Pool & Suspension Points</h3>
    <p>In Grand Central Dispatch (GCD), dispatching 1,000 blocks spawned 1,000 threads, blowing through kernel memory. Swift Concurrency solves this: exactly 1 thread per CPU core. A thread suspension point (<code>await</code>) yields the thread to other work.</p>

    <h3>2. Cooperative Cancellation (Task.isCancelled & Task.checkCancellation)</h3>
    <pre><code>func computeMetrics(batches: [DataBatch]) async throws -> [Result] {
    var results: [Result] = []
    for batch in batches {
        try Task.checkCancellation() // Throws CancellationError immediately if cancelled
        results.append(batch.process())
    }
    return results
}</code></pre>
    """,
    "failure_mode_html": """
    <p><strong>Blocking the Cooperative Thread Pool with Synchronous Locks:</strong> Calling synchronous blocking APIs (Thread.sleep, DispatchSemaphore.wait) inside an async context starves the cooperative pool. If all core threads block, the entire app freezes. Always use Task.sleep.</p>
    """,
    "quizzes": [
        {
            "question": "What is the maximum thread limit of Swift's cooperative thread pool?",
            "correct_idx": 1,
            "options": [
                {"text": "Unbounded based on active queue count.", "reason": "Option A is wrong because unbounded pools caused GCD thread explosion."},
                {"text": "Equal to available machine CPU cores.", "reason": "Option B is correct: Swift limits concurrent worker threads to CPU core count to eliminate context-switching thrash."},
                {"text": "Fixed permanently at 64 system threads.", "reason": "Option C is wrong because GCD had a 64-thread limit, not Swift Concurrency."},
                {"text": "Exactly one single main runloop thread.", "reason": "Option D is wrong because background tasks execute on background core threads."}
            ]
        },
        {
            "question": "What does calling Task.cancel() actually do to a running Swift Task?",
            "correct_idx": 2,
            "options": [
                {"text": "Terminates the thread execution stack immediately.", "reason": "Option A is wrong because preemptive killing would corrupt memory and leave resources locked."},
                {"text": "Rolls back all completed state mutations.", "reason": "Option B is wrong because Swift does not provide automatic database-style transaction rollbacks."},
                {"text": "Sets isCancelled flag for cooperative checks.", "reason": "Option C is correct: Swift Concurrency is cooperative; tasks check Task.isCancelled or Task.checkCancellation()."},
                {"text": "Deallocates all captured memory to nil.", "reason": "Option D is wrong because deallocation occurs via ARC when task references fall out of scope."}
            ]
        }
    ],
    "exercise": {
        "title": "Cooperative Work Processor with Cancellation Checks",
        "instructions": "Implement processWorkItems. Check for cancellation on each iteration using Task.checkCancellation(). Sleep cooperatively for 1ms using Task.sleep(nanoseconds: 1_000_000). Replace TODO and run tests.",
        "starter_code": """import Foundation

struct WorkItem: Equatable {
    let id: Int
    let value: String
}

func processWorkItems(_ items: [WorkItem]) async throws -> [String] {
    var processed: [String] = []
    for item in items {
        // TODO: 1. Call try Task.checkCancellation()
        // TODO: 2. Call try await Task.sleep(nanoseconds: 1_000_000)
        // TODO: 3. Append "processed_\\(item.id)" to processed
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
        fatalError("Unexpected error: \\(error)")
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
        fatalError("Unexpected error type: \\(error)")
    }
}

await runTests()""",
        "hints": [
            "Inside the loop: try Task.checkCancellation()",
            "Followed by: try await Task.sleep(nanoseconds: 1_000_000)",
            "Then append: processed.append(\"processed_\\(item.id)\")"
        ],
        "solution_code": """import Foundation

struct WorkItem: Equatable {
    let id: Int
    let value: String
}

func processWorkItems(_ items: [WorkItem]) async throws -> [String] {
    var processed: [String] = []
    for item in items {
        try Task.checkCancellation()
        try await Task.sleep(nanoseconds: 1_000_000)
        processed.append("processed_\\(item.id)")
    }
    return processed
}

func runTests() async {
    print("🧪 Running Lesson 2.1 Verification Tests...")
    let items = (1...5).map { WorkItem(id: $0, value: "val") }
    do {
        let results = try await processWorkItems(items)
        assert(results.count == 5)
        assert(results[0] == "processed_1")
    } catch { fatalError() }

    let task = Task {
        try await processWorkItems((1...100).map { WorkItem(id: $0, value: "val") })
    }
    task.cancel()
    do {
        _ = try await task.value
        fatalError()
    } catch is CancellationError {
        print("  ✅ All tests passed!")
    } catch { fatalError() }
}

await runTests()""",
        "solution_explanation": "Task.checkCancellation() checks the Task's cancellation flag cooperatively and throws CancellationError to unwind stack safely without data corruption."
    },
    "primary_sources": [
        {"title": "The Swift Programming Language: Concurrency", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/", "desc": "Official Apple documentation on Tasks and cooperative thread pool."},
        {"title": "Swift Evolution SE-0304: Structured Concurrency", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0304-structured-concurrency.md", "desc": "Canonical specification of Task hierarchies."}
    ],
    "coaching_prompt": "Always check Task.isCancelled or Task.checkCancellation() inside loops so long computations yield promptly."
}

LESSON_0010 = {
    "id": "0010",
    "slug": "0010-structured-concurrency",
    "phase_num": 2,
    "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
    "lesson_num": "Lesson 2.2",
    "title": "Structured Concurrency: async let & TaskGroup Hierarchies",
    "subtitle": "Bounded child tasks, dynamic concurrency trees with withTaskGroup, and automatic cancellation propagation.",
    "prev_url": "0009-concurrency-fundamentals.html",
    "next_url": "0011-async-stream.html",
    "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
    "cheatsheet_title": "Swift Concurrency Cheatsheet",
    "android_anchor_html": """
    <p>In Kotlin Coroutines, structured concurrency is managed via <code>coroutineScope { async { ... } }</code> where child coroutines inherit the parent Job.</p>
    <p>In Swift, structured concurrency is guaranteed via <code>async let</code> (fixed number of child tasks) and <code>withTaskGroup</code> / <code>withThrowingTaskGroup</code> (dynamic parallel tasks). When the parent scope exits or throws, all child tasks are cancelled automatically!</p>
    """,
    "core_sections_html": """
    <h3>1. Fixed Parallelism with async let</h3>
    <pre><code>func loadDashboard() async throws -> Dashboard {
    async let profile = fetchProfile()
    async let notifications = fetchNotifications()
    async let settings = fetchSettings()
    return try await Dashboard(profile: profile, notifications: notifications, settings: settings)
}</code></pre>

    <h3>2. Dynamic Parallelism with withTaskGroup</h3>
    <pre><code>func fetchAllAvatars(ids: [String]) async throws -> [String: Data] {
    try await withThrowingTaskGroup(of: (String, Data).self) { group in
        for id in ids {
            group.addTask { (id, try await downloadImage(id: id)) }
        }
        var results: [String: Data] = [:]
        for try await (id, data) in group { results[id] = data }
        return results
    }
}</code></pre>
    """,
    "failure_mode_html": """
    <p><strong>Unstructured Concurrency Leaks via Task { ... }:</strong> Unparented tasks created with Task { ... } do not cancel when screens dismiss, creating background memory and network leaks.</p>
    """,
    "quizzes": [
        {
            "question": "What happens to child tasks spawned in a withTaskGroup if one child throws an error and the group exits?",
            "correct_idx": 0,
            "options": [
                {"text": "All remaining child tasks cancel automatically.", "reason": "Option A is correct: structured concurrency guarantees child cancellation when the enclosing scope exits."},
                {"text": "Child tasks continue running indefinitely.", "reason": "Option B is wrong because that would violate structured concurrency boundaries."},
                {"text": "The Darwin operating system reboots app.", "reason": "Option C is wrong because error handling in Swift does not trigger system reboots."},
                {"text": "Parent task waits until all complete.", "reason": "Option D is wrong because cancellation is immediately propagated."}
            ]
        },
        {
            "question": "When should you use async let instead of withTaskGroup?",
            "correct_idx": 1,
            "options": [
                {"text": "When executing unbounded stream arrays.", "reason": "Option A is wrong because dynamic arrays require withTaskGroup."},
                {"text": "When the number of parallel tasks is fixed.", "reason": "Option B is correct: async let is ideal for a fixed, known set of concurrent operations."},
                {"text": "When updating UIKit buttons on MainActor.", "reason": "Option C is wrong because UI dispatch uses @MainActor, not async let."},
                {"text": "When writing unit tests in XCTestCase.", "reason": "Option D is wrong because Swift Testing supports all concurrency constructs."}
            ]
        }
    ],
    "exercise": {
        "title": "Parallel Batch Price Ingestion Engine",
        "instructions": "Implement fetchBatchPrices using withTaskGroup. Spawn a child task for each symbol that awaits fetchPrice(symbol:) and return the aggregated dictionary [String: Double]. Replace TODO and run tests.",
        "starter_code": """import Foundation

func fetchPrice(symbol: String) async -> (String, Double) {
    try? await Task.sleep(nanoseconds: 5_000_000)
    let mockPrices: [String: Double] = ["AAPL": 220.5, "GOOG": 175.0, "NVDA": 125.0]
    return (symbol, mockPrices[symbol] ?? 100.0)
}

func fetchBatchPrices(symbols: [String]) async -> [String: Double] {
    // TODO: Use withTaskGroup(of: (String, Double).self)
    // 1. For each symbol in symbols, group.addTask { await fetchPrice(symbol: symbol) }
    // 2. Iterate for await (sym, price) in group and aggregate into dictionary
    // 3. Return dictionary
    fatalError("TODO: Implement fetchBatchPrices")
}

func runTests() async {
    print("🧪 Running Lesson 2.2 Verification Tests...")
    let symbols = ["AAPL", "GOOG", "NVDA"]
    let prices = await fetchBatchPrices(symbols: symbols)
    assert(prices.count == 3, "Test 1A Failed: Incomplete prices")
    assert(prices["AAPL"] == 220.5, "Test 1B Failed")
    print("  ✅ Challenge Passed: Structured concurrency TaskGroup aggregation verified")
}

await runTests()""",
        "hints": [
            "Wrap in: await withTaskGroup(of: (String, Double).self) { group in ... }",
            "group.addTask { await fetchPrice(symbol: symbol) }",
            "var dict: [String: Double] = [:]; for await (sym, p) in group { dict[sym] = p }; return dict"
        ],
        "solution_code": """import Foundation

func fetchPrice(symbol: String) async -> (String, Double) {
    try? await Task.sleep(nanoseconds: 5_000_000)
    let mockPrices: [String: Double] = ["AAPL": 220.5, "GOOG": 175.0, "NVDA": 125.0]
    return (symbol, mockPrices[symbol] ?? 100.0)
}

func fetchBatchPrices(symbols: [String]) async -> [String: Double] {
    await withTaskGroup(of: (String, Double).self) { group in
        for symbol in symbols {
            group.addTask {
                await fetchPrice(symbol: symbol)
            }
        }
        var results: [String: Double] = [:]
        for await (sym, price) in group {
            results[sym] = price
        }
        return results
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.2 Verification Tests...")
    let symbols = ["AAPL", "GOOG", "NVDA"]
    let prices = await fetchBatchPrices(symbols: symbols)
    assert(prices.count == 3)
    assert(prices["AAPL"] == 220.5)
    print("  ✅ All tests passed!")
}

await runTests()""",
        "solution_explanation": "withTaskGroup creates a structured concurrency scope where child tasks run in parallel on the cooperative thread pool. The parent task awaits results asynchronously using for await, maintaining complete lifetime bounding."
    },
    "primary_sources": [
        {"title": "Apple Developer: TaskGroup Documentation", "url": "https://developer.apple.com/documentation/swift/taskgroup", "desc": "Official documentation for withTaskGroup and withThrowingTaskGroup."},
        {"title": "WWDC Session: Explore structured concurrency in Swift", "url": "https://developer.apple.com/videos/play/wwdc2021/10134/", "desc": "Apple engineers explain TaskGroup trees and async let."}
    ],
    "coaching_prompt": "withTaskGroup guarantees that all child tasks finish or cancel before the group block returns."
}
