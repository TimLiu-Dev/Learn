import Foundation

final class SimulatedBackgroundTask {
    private(set) var wasCancelled: Bool = false
    var shouldCancelEarly: Bool = false

    func executeWork() -> Bool {
        // TODO: If shouldCancelEarly is true, set wasCancelled = true and return false
        // Otherwise return true
        fatalError("TODO: Implement executeWork")
    }
}

func runTests() {
    print("🧪 Running Lesson 7.2 Verification Tests...")
    let successTask = SimulatedBackgroundTask()
    assert(successTask.executeWork() == true, "Test 1 Failed")
    assert(successTask.wasCancelled == false, "Test 2 Failed")

    let cancelledTask = SimulatedBackgroundTask()
    cancelledTask.shouldCancelEarly = true
    assert(cancelledTask.executeWork() == false, "Test 3 Failed")
    assert(cancelledTask.wasCancelled == true, "Test 4 Failed: wasCancelled should be true")
    print("  ✅ Challenge Passed: Background task expiration handling verified")
}
runTests()