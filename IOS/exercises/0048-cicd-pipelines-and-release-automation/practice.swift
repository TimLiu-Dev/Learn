import Foundation

func bumpBuildNumber(_ current: String) -> String {
    // TODO: Convert current to Int. If valid and >= 0, return String(val + 1)
    // Otherwise return "1"
    fatalError("TODO: Implement bumpBuildNumber")
}

func runTests() {
    print("🧪 Running Lesson 7.5 Verification Tests...")
    assert(bumpBuildNumber("105") == "106", "Test 1 Failed")
    assert(bumpBuildNumber("0") == "1", "Test 2 Failed")
    assert(bumpBuildNumber("invalid") == "1", "Test 3 Failed")
    print("  ✅ Challenge Passed: CI build version increment verified")
}
runTests()