import Foundation

func interpolatePosition(start: Double, end: Double, progress: Double) -> Double {
    // TODO: 1. Clamp progress between 0.0 and 1.0
    // TODO: 2. Return start + (end - start) * clampedProgress
    fatalError("TODO: Implement interpolatePosition")
}

func runTests() {
    print("🧪 Running Lesson 3.7 Verification Tests...")
    let mid = interpolatePosition(start: 100.0, end: 200.0, progress: 0.5)
    assert(mid == 150.0, "Test 1 Failed: Expected 150.0, got \(mid)")

    let clampedHigh = interpolatePosition(start: 0.0, end: 100.0, progress: 1.5)
    assert(clampedHigh == 100.0, "Test 2 Failed: Did not clamp upper bound")
    print("  ✅ Challenge Passed: Coordinate interpolation verified")
}
runTests()