import Foundation

func generateAppID(teamId: String, bundleId: String) -> String? {
    // TODO: If teamId.isEmpty || bundleId.isEmpty return nil
    // Return "\(teamId).\(bundleId)"
    fatalError("TODO: Implement generateAppID")
}

func runTests() {
    print("🧪 Running Lesson 7.4 Verification Tests...")
    let appId = generateAppID(teamId: "A1B2C3D4E5", bundleId: "com.pulseflow.app")
    assert(appId == "A1B2C3D4E5.com.pulseflow.app", "Test 1 Failed: Got \(appId ?? "nil")")

    assert(generateAppID(teamId: "", bundleId: "com.test") == nil, "Test 2 Failed")
    assert(generateAppID(teamId: "TEAM", bundleId: "") == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: App ID formatting verified")
}
runTests()