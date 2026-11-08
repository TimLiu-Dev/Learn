import Foundation

func detectGenerationLeak(genABytes: Int, genBBytes: Int) -> Int {
    // TODO: Return max(0, genBBytes - genABytes)
    fatalError("TODO: Implement detectGenerationLeak")
}

func runTests() {
    print("🧪 Running Lesson 6.5 Verification Tests...")
    let leaked = detectGenerationLeak(genABytes: 10_000, genBBytes: 15_500)
    assert(leaked == 5_500, "Test 1 Failed: Expected 5,500 leaked bytes, got \(leaked)")

    let clean = detectGenerationLeak(genABytes: 10_000, genBBytes: 9_500)
    assert(clean == 0, "Test 2 Failed: Expected 0 leaked bytes")
    print("  ✅ Challenge Passed: Generation leak calculation verified")
}
runTests()