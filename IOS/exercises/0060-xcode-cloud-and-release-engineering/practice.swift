import Foundation

enum RolloutDecision: Equatable {
    case advance(to: Int)
    case completed
    case paused(at: Int)
}

enum RolloutController {
    static let schedule = [1, 2, 5, 10, 20, 50, 100]

    // TODO: If crashFreeRate < 0.995 -> .paused(at: currentPercent)
    // If currentPercent == 100 -> .completed
    // Otherwise advance to the next percent in schedule
    static func nextStage(currentPercent: Int, crashFreeRate: Double) -> RolloutDecision {
        fatalError("TODO: Implement nextStage")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.12 Verification Tests...")
    assert(RolloutController.nextStage(currentPercent: 1, crashFreeRate: 0.999) == .advance(to: 2), "Test 1 Failed")
    assert(RolloutController.nextStage(currentPercent: 5, crashFreeRate: 0.980) == .paused(at: 5), "Test 2 Failed")
    assert(RolloutController.nextStage(currentPercent: 100, crashFreeRate: 0.999) == .completed, "Test 3 Failed")
    print("  ✅ Challenge Passed: Phased App Store rollout safety gate verified")
}
runTests()