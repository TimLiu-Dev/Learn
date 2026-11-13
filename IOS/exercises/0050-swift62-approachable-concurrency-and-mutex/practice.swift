import Foundation
import Synchronization

final class ThreadSafeMetrics: Sendable {
    // TODO: Store a Mutex<[String: Int]> initialized to [:]
    
    func recordHit(for endpoint: String) {
        // TODO: Increment endpoint count atomically inside withLock
        fatalError("TODO: Implement recordHit")
    }

    func count(for endpoint: String) -> Int {
        // TODO: Read endpoint count inside withLock
        fatalError("TODO: Implement count")
    }
}

func runTests() async {
    print("🧪 Running Lesson 8.2 Verification Tests...")
    let metrics = ThreadSafeMetrics()
    await withTaskGroup(of: Void.self) { group in
        for _ in 0..<200 {
            group.addTask {
                metrics.recordHit(for: "/v1/feed")
            }
        }
    }
    assert(metrics.count(for: "/v1/feed") == 200, "Test 1 Failed: Expected 200 hits under contention")
    assert(metrics.count(for: "/v1/missing") == 0, "Test 2 Failed: Missing key should return 0")
    print("  ✅ Challenge Passed: Synchronization.Mutex contention test verified")
}
await runTests()