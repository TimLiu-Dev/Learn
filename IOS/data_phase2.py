# Phase 2: Modern Swift 6 Concurrency & Enterprise Networking (Lessons 0009 - 0013)

import sys

REMAINING_PHASE2 = [
    # 0011: Lesson 2.3
    {
        "id": "0011",
        "slug": "0011-async-stream",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.3",
        "title": "AsyncStream & Bridging Legacy Asynchronous APIs",
        "subtitle": "Bridging delegate callbacks, notification center observers, and WebSocket frames into AsyncSequence with backpressure policies.",
        "prev_url": "0010-structured-concurrency.html",
        "next_url": "0012-actors-and-thread-safety.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin, bridging callback APIs into reactive streams uses <code>callbackFlow { ... }</code> with <code>trySend()</code> and <code>awaitClose { ... }</code>.</p>
        <p>In Swift, the equivalent is <strong><code>AsyncStream</code></strong> (and <code>AsyncThrowingStream</code>). It adapts delegate protocols, NotificationCenter, and streaming data into modern <code>AsyncSequence</code> pipelines that support <code>for await item in stream</code> with explicit backpressure buffering.</p>
        """,
        "core_sections_html": """
        <h3>1. The AsyncStream Continuation Anatomy</h3>
        <pre><code>func trackLocationUpdates() -> AsyncStream&lt;CLLocation&gt; {
    AsyncStream(bufferingPolicy: .bufferingNewest(10)) { continuation in
        let listener = LocationListener { location in
            continuation.yield(location) // Emit new item
        }
        continuation.onTermination = { @Sendable termination in
            listener.stopListening() // Cleanup on cancel or break
        }
        listener.startListening()
    }
}</code></pre>

        <h3>2. Backpressure Buffering Strategies</h3>
        <ul>
          <li><strong><code>.unbounded</code>:</strong> Unbounded buffer. Memory risk if consumer is slow.</li>
          <li><strong><code>.bufferingNewest(limit)</code>:</strong> Drops oldest items when buffer is full. Ideal for real-time sensor and location streams.</li>
          <li><strong><code>.bufferingOldest(limit)</code>:</strong> Discards incoming items until buffer drains.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Omitting <code>continuation.onTermination</code>:</strong> If the consumer breaks from a <code>for await</code> loop or the task cancels, failure to clean up resources in <code>onTermination</code> causes permanent delegate and memory leaks.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary role of continuation.onTermination in an AsyncStream?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles Swift closures ahead of time.", "reason": "Option A is wrong because onTermination is a runtime callback."},
                    {"text": "Releases listeners when stream terminates.", "reason": "Option B is correct: onTermination handles teardown when consumer breaks from the loop or task cancels."},
                    {"text": "Allocates existential containers on heap.", "reason": "Option C is wrong because streams do not use existential boxing."},
                    {"text": "Bypasses strict concurrency data safety.", "reason": "Option D is wrong because onTermination closure must be @Sendable."}
                ]
            },
            {
                "question": "Which AsyncStream buffering policy drops the oldest item when capacity is exceeded?",
                "correct_idx": 2,
                "options": [
                    {"text": "The unbounded buffering policy.", "reason": "Option A is wrong because unbounded never drops items."},
                    {"text": "The bufferingOldest policy.", "reason": "Option B is wrong because bufferingOldest drops new incoming items."},
                    {"text": "The bufferingNewest policy.", "reason": "Option C is correct: bufferingNewest preserves the latest updates and purges older entries."},
                    {"text": "The zeroAllocation policy.", "reason": "Option D is wrong because that is not a valid buffering policy enum."}
                ]
            }
        ],
        "exercise": {
            "title": "Bridging Callback Sensor into AsyncStream",
            "instructions": "Implement <code>makeEventStream</code>. Wrap <code>LegacySensorEmitter</code> in an <code>AsyncStream&lt;Int&gt;</code>. On listener callback, yield the reading. In <code>onTermination</code>, call <code>emitter.stop()</code>. Run tests.",
            "starter_code": """import Foundation

final class LegacySensorEmitter {
    private var callback: ((Int) -> Void)?
    private(set) var isRunning: Bool = false

    func start(onData: @escaping (Int) -> Void) {
        self.callback = onData
        self.isRunning = true
    }

    func emit(value: Int) {
        callback?(value)
    }

    func stop() {
        self.isRunning = false
        self.callback = nil
    }
}

func makeEventStream(emitter: LegacySensorEmitter) -> AsyncStream<Int> {
    // TODO: Create AsyncStream<Int>(bufferingPolicy: .bufferingNewest(5)) { continuation in ... }
    // 1. emitter.start { val in continuation.yield(val) }
    // 2. continuation.onTermination = { @Sendable _ in emitter.stop() }
    fatalError("TODO: Implement makeEventStream")
}

func runTests() async {
    print("🧪 Running Lesson 2.3 Verification Tests...")
    let emitter = LegacySensorEmitter()
    var collected: [Int] = []

    let task = Task {
        let stream = makeEventStream(emitter: emitter)
        for await val in stream {
            collected.append(val)
            if collected.count == 2 { break }
        }
    }

    try? await Task.sleep(nanoseconds: 5_000_000)
    emitter.emit(value: 101)
    emitter.emit(value: 102)

    _ = await task.value
    task.cancel()
    try? await Task.sleep(nanoseconds: 10_000_000)

    assert(collected == [101, 102], "Test 1 Failed: Collected incorrect values")
    assert(emitter.isRunning == false, "Test 2 Failed: Emitter should stop after stream cancellation")
    print("  ✅ Challenge Passed: AsyncStream bridging and onTermination cleanup verified")
}

await runTests()""",
            "hints": [
                "Return AsyncStream(bufferingPolicy: .bufferingNewest(5)) { continuation in ... }",
                "Inside, call emitter.start { val in continuation.yield(val) }",
                "Set continuation.onTermination = { @Sendable _ in emitter.stop() }"
            ],
            "solution_code": """import Foundation

final class LegacySensorEmitter {
    private var callback: ((Int) -> Void)?
    private(set) var isRunning: Bool = false

    func start(onData: @escaping (Int) -> Void) {
        self.callback = onData
        self.isRunning = true
    }

    func emit(value: Int) {
        callback?(value)
    }

    func stop() {
        self.isRunning = false
        self.callback = nil
    }
}

func makeEventStream(emitter: LegacySensorEmitter) -> AsyncStream<Int> {
    AsyncStream(bufferingPolicy: .bufferingNewest(5)) { continuation in
        emitter.start { value in
            continuation.yield(value)
        }
        continuation.onTermination = { @Sendable _ in
            emitter.stop()
        }
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.3 Verification Tests...")
    let emitter = LegacySensorEmitter()
    var collected: [Int] = []

    let task = Task {
        let stream = makeEventStream(emitter: emitter)
        for await val in stream {
            collected.append(val)
            if collected.count == 2 { break }
        }
    }

    try? await Task.sleep(nanoseconds: 5_000_000)
    emitter.emit(value: 101)
    emitter.emit(value: 102)

    _ = await task.value
    task.cancel()
    try? await Task.sleep(nanoseconds: 10_000_000)

    assert(collected == [101, 102])
    assert(emitter.isRunning == false)
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "AsyncStream acts as an adapter between callback-driven APIs and modern async/await loops. Using continuation.yield emits data onto the consumer task, while onTermination ensures tear-down when the consuming loop breaks."
        },
        "primary_sources": [
            {"title": "Apple Developer: AsyncStream Documentation", "url": "https://developer.apple.com/documentation/swift/asyncstream", "desc": "Official Apple reference for AsyncStream and buffering policies."},
            {"title": "Swift Evolution SE-0314: AsyncStream", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0314-async-stream.md", "desc": "Rationale and design of Swift AsyncStream."}
        ],
        "coaching_prompt": "Notice how AsyncStream converts any delegate or notification observer into a clean for-await loop."
    },

    # 0012: Lesson 2.4
    {
        "id": "0012",
        "slug": "0012-actors-and-thread-safety",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.4",
        "title": "Thread Safety with Actors & The Reentrancy Hazard",
        "subtitle": "Actor isolation boundaries, nonisolated declarations, and preventing silent state corruption across await suspension points.",
        "prev_url": "0011-async-stream.html",
        "next_url": "0013-global-actors-and-mainactor.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), thread safety relies on explicit locks (<code>ReentrantLock</code>, <code>synchronized</code>, <code>Mutex.withLock { ... }</code>) or atomic primitives. Traditional locks block the thread, risking deadlocks and priority inversions.</p>
        <p>In Swift, thread safety is achieved natively via <strong><code>actor</code></strong>. An actor is a reference type that serializes access to its internal mutable state automatically. However, <strong>actors are reentrant</strong>: when an actor method reaches an <code>await</code>, it yields its thread to other callers!</p>
        """,
        "core_sections_html": """
        <h3>1. Actor Isolation & nonisolated Declarations</h3>
        <pre><code>actor UserStore {
    var profile: Profile // Isolated state
    let storeId: String  // Immutable constant

    init(storeId: String, profile: Profile) {
        self.storeId = storeId
        self.profile = profile
    }

    // Can be accessed synchronously without 'await':
    nonisolated var id: String { storeId }
}</code></pre>

        <h3>2. The Senior Reentrancy Hazard</h3>
        <p>Unlike mutex locks, actors do not freeze when suspended. Other tasks can execute methods on the same actor during an <code>await</code>:</p>
        <pre><code>actor AccountManager {
    var balance: Double = 100.0

    // ❌ REENTRANCY BUG:
    func withdraw(amount: Double) async throws {
        guard balance >= amount else { throw Error.insufficientFunds }
        // Suspension point: Thread yields! Another caller can withdraw!
        try await NetworkGateway.charge()
        balance -= amount // 💥 CORRUPTION: balance may now be negative!
    }

    // ✅ DEFENSIVE SENIOR PATTERN:
    func withdrawSafely(amount: Double) async throws {
        guard balance >= amount else { throw Error.insufficientFunds }
        try await NetworkGateway.charge()
        // Re-check invariants immediately after resumption:
        guard balance >= amount else { throw Error.insufficientFunds }
        balance -= amount
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Assuming Actor Invariants Across <code>await</code>:</strong> Any state checked before an <code>await</code> must be re-verified after the <code>await</code> resumes, because other callers may have mutated the actor during the suspension interval.</p>
        """,
        "quizzes": [
            {
                "question": "What happens to an actor's isolated state while it is suspended at an await expression?",
                "correct_idx": 1,
                "options": [
                    {"text": "The entire actor freezes and locks.", "reason": "Option A is wrong because actors are reentrant and do not lock."},
                    {"text": "Other tasks can execute actor methods.", "reason": "Option B is correct: when an actor method suspends, it yields the thread, allowing other callers to run actor methods and alter state."},
                    {"text": "The runtime rolls back all variables.", "reason": "Option C is wrong because Swift does not roll back memory on suspension."},
                    {"text": "All captured memory is moved to heap.", "reason": "Option D is wrong because actor memory is already allocated on the heap."}
                ]
            },
            {
                "question": "When can an actor member be marked nonisolated?",
                "correct_idx": 0,
                "options": [
                    {"text": "When accessing only immutable constants.", "reason": "Option A is correct: nonisolated allows synchronous read access to immutable properties (let) or pure functions without await."},
                    {"text": "When mutating internal mutable dictionaries.", "reason": "Option B is wrong because mutating mutable state requires actor isolation."},
                    {"text": "When calling into UIKit from worker threads.", "reason": "Option C is wrong because UIKit calls require @MainActor."},
                    {"text": "When disabling compile-time type safety.", "reason": "Option D is wrong because nonisolated is fully type-safe."}
                ]
            }
        ],
        "exercise": {
            "title": "Actor-Isolated Deduplicated Image Cache",
            "instructions": "Implement <code>fetchImage</code> in <code>ImagePipelineCache</code>. Check <code>cachedImages</code>; if missing, check <code>inFlightTasks</code> so identical concurrent requests share the same download <code>Task</code>. Remove from <code>inFlightTasks</code> in <code>defer</code>. Run tests.",
            "starter_code": """import Foundation

actor ImagePipelineCache {
    private var cachedImages: [URL: String] = [:]
    private var inFlightTasks: [URL: Task<String, Error>] = [:]

    func fetchImage(from url: URL) async throws -> String {
        // 1. Check cachedImages[url] -> if found, return it
        if let existing = cachedImages[url] { return existing }

        // 2. Check inFlightTasks[url] -> if found, return try await existingTask.value
        if let runningTask = inFlightTasks[url] { return try await runningTask.value }

        // TODO: 3. Create a new Task { ... } that sleeps 10ms and returns "image_\\(url.lastPathComponent)"
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

await runTests()""",
            "hints": [
                "Create task: let task = Task<String, Error> { try await Task.sleep(nanoseconds: 10_000_000); return \"image_\\(url.lastPathComponent)\" }",
                "inFlightTasks[url] = task",
                "defer { inFlightTasks.removeValue(forKey: url) }",
                "let result = try await task.value; cachedImages[url] = result; return result"
            ],
            "solution_code": """import Foundation

actor ImagePipelineCache {
    private var cachedImages: [URL: String] = [:]
    private var inFlightTasks: [URL: Task<String, Error>] = [:]

    func fetchImage(from url: URL) async throws -> String {
        if let existing = cachedImages[url] { return existing }
        if let runningTask = inFlightTasks[url] { return try await runningTask.value }

        let downloadTask = Task<String, Error> {
            try await Task.sleep(nanoseconds: 10_000_000)
            return "image_\\(url.lastPathComponent)"
        }
        inFlightTasks[url] = downloadTask
        defer {
            inFlightTasks.removeValue(forKey: url)
        }

        let downloaded = try await downloadTask.value
        cachedImages[url] = downloaded
        return downloaded
    }

    var cacheCount: Int { cachedImages.count }
}

func runTests() async {
    print("🧪 Running Lesson 2.4 Verification Tests...")
    let cache = ImagePipelineCache()
    let url = URL(string: "https://cdn.example.com/avatars/user.png")!

    let results = await withTaskGroup(of: String?.self) { group in
        for _ in 0..<5 {
            group.addTask { try? await cache.fetchImage(from: url) }
        }
        var list: [String] = []
        for await res in group { if let res { list.append(res) } }
        return list
    }

    assert(results.count == 5)
    assert(results.allSatisfy { $0 == "image_user.png" })
    let count = await cache.cacheCount
    assert(count == 1)
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "In an actor, checking and storing the running Task in inFlightTasks happens synchronously before the await suspension point. Subsequent concurrent callers find the existing Task and await its result, deduplicating duplicate network requests."
        },
        "primary_sources": [
            {"title": "The Swift Programming Language: Actors", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/#Actors", "desc": "Official Apple documentation on actor isolation and synchronization."},
            {"title": "Swift Evolution SE-0306: Actors", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0306-actors.md", "desc": "Design proposal explaining actor reentrancy."}
        ],
        "coaching_prompt": "Always store the Task in your tracking dictionary BEFORE you await it! That way, concurrent callers can share the in-flight work."
    },

    # 0013: Lesson 2.5
    {
        "id": "0013",
        "slug": "0013-global-actors-and-mainactor",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.5",
        "title": "Global Actors, @MainActor & Custom Executors",
        "subtitle": "Thread hopping, ensuring UI mutations on the main runloop, and custom serial actor executors.",
        "prev_url": "0012-actors-and-thread-safety.html",
        "next_url": "0014-swift6-strict-concurrency-and-sendable.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), switching to the UI thread requires <code>withContext(Dispatchers.Main) { ... }</code>. Calling UI mutations from worker threads causes runtime exceptions.</p>
        <p>In Swift, the main thread is encapsulated as a global actor: <strong><code>@MainActor</code></strong>. Tagging a class, function, or property with <code>@MainActor</code> instructs the Swift compiler to guarantee execution on the main runloop at compile time. Swift 6 eliminates manual <code>DispatchQueue.main.async</code> calls.</p>
        """,
        "core_sections_html": """
        <h3>1. The @MainActor Isolation Domain</h3>
        <pre><code>@MainActor
final class ProfileViewModel: ObservableObject {
    @Published var userName: String = ""

    func updateName(to newName: String) {
        // Guaranteed to run on main thread:
        self.userName = newName
    }
}</code></pre>

        <h3>2. Safe Thread Hopping with MainActor.run</h3>
        <p>When executing inside a background worker actor or nonisolated utility function, use <code>await MainActor.run { ... }</code> to hop over to the main thread:</p>
        <pre><code>func processBackgroundPayload() async {
    let result = parseBigData()
    // Thread hop to main actor:
    await MainActor.run {
        updateUI(result)
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Over-decorating Services with @MainActor:</strong> Tagging network clients, parsers, or database repositories with <code>@MainActor</code> forces background work onto the main thread, introducing UI hitches (frame drops). Keep background services on default actors or detached tasks.</p>
        """,
        "quizzes": [
            {
                "question": "What does annotating a class with @MainActor guarantee?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles the class to Objective-C headers.", "reason": "Option A is wrong because @MainActor is a Swift Concurrency isolation attribute."},
                    {"text": "All methods run on the main runloop thread.", "reason": "Option B is correct: the compiler enforces that all property accesses and methods execute on the main thread."},
                    {"text": "Allocates instance memory on the stack.", "reason": "Option C is wrong because classes are always allocated on the heap."},
                    {"text": "Disables memory retain cycles automatically.", "reason": "Option D is wrong because @MainActor does not alter ARC retain counting."}
                ]
            },
            {
                "question": "How should a background function update @MainActor state?",
                "correct_idx": 0,
                "options": [
                    {"text": "Call await MainActor.run { ... } closure.", "reason": "Option A is correct: MainActor.run hops execution safely to the main thread with compile-time isolation checking."},
                    {"text": "Dispatch synchronously via dispatch_sync.", "reason": "Option B is wrong because dispatch_sync from background can cause deadlocks if main thread waits."},
                    {"text": "Force cast pointer to UnsafeMutablePointer.", "reason": "Option C is wrong because raw pointer casting violates Swift 6 memory safety."},
                    {"text": "Disable compiler strict concurrency check.", "reason": "Option D is wrong because strict concurrency should never be bypassed."}
                ]
            }
        ],
        "exercise": {
            "title": "Thread Hopping UI State Dispatcher",
            "instructions": "Implement <code>deliverUpdate</code>. From a background thread, calculate the formatted string and use <code>await MainActor.run</code> to update <code>UIStateHolder.text</code> on the main actor. Run tests.",
            "starter_code": """import Foundation

@MainActor
final class UIStateHolder {
    var text: String = "empty"
    var isMainThread: Bool = false

    func apply(value: String) {
        self.text = value
        self.isMainThread = Thread.isMainThread
    }
}

func deliverUpdate(to holder: UIStateHolder, rawValue: String) async {
    // 1. Perform background string manipulation:
    let formatted = "formatted_\\(rawValue.uppercased())"

    // TODO: 2. Hop to MainActor using await MainActor.run { ... }
    // Call holder.apply(value: formatted)
    fatalError("TODO: Implement MainActor hop")
}

func runTests() async {
    print("🧪 Running Lesson 2.5 Verification Tests...")
    let holder = await UIStateHolder()

    // Execute from background detached task:
    await Task.detached {
        await deliverUpdate(to: holder, rawValue: "senior")
    }.value

    let finalText = await holder.text
    let wasOnMain = await holder.isMainThread

    assert(finalText == "formatted_SENIOR", "Test 1 Failed: Text not updated correctly")
    assert(wasOnMain == true, "Test 2 Failed: UI mutation did not execute on Main thread!")
    print("  ✅ Challenge Passed: MainActor thread-hopping verified")
}

await runTests()""",
            "hints": [
                "Write: await MainActor.run { holder.apply(value: formatted) }"
            ],
            "solution_code": """import Foundation

@MainActor
final class UIStateHolder {
    var text: String = "empty"
    var isMainThread: Bool = false

    func apply(value: String) {
        self.text = value
        self.isMainThread = Thread.isMainThread
    }
}

func deliverUpdate(to holder: UIStateHolder, rawValue: String) async {
    let formatted = "formatted_\\(rawValue.uppercased())"
    await MainActor.run {
        holder.apply(value: formatted)
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.5 Verification Tests...")
    let holder = await UIStateHolder()
    await Task.detached {
        await deliverUpdate(to: holder, rawValue: "senior")
    }.value

    let finalText = await holder.text
    let wasOnMain = await holder.isMainThread
    assert(finalText == "formatted_SENIOR")
    assert(wasOnMain == true)
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "MainActor.run coordinates with the Darwin main runloop, hopping execution from the background cooperative thread to the main thread safely. This ensures UI mutations are 100% thread-safe under Swift 6 data-race immunity."
        },
        "primary_sources": [
            {"title": "Apple Developer: MainActor Documentation", "url": "https://developer.apple.com/documentation/swift/mainactor", "desc": "Official guide on the MainActor singleton and run method."},
            {"title": "Swift Evolution SE-0316: Global Actors", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0316-global-actors.md", "desc": "Design specification for global actors and @MainActor."}
        ],
        "coaching_prompt": "In Swift 6, data races to UI components are caught at compile time. No more runtime crashes from background thread layout updates!"
    }
]

PHASE2_LESSONS = list(REMAINING_PHASE2)
print(f"Phase 2 data now has {len(PHASE2_LESSONS)} lessons loaded.")
