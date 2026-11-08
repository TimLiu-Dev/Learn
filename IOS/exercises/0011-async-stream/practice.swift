import Foundation

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

await runTests()