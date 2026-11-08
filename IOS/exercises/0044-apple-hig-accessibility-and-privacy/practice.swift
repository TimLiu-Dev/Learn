import Foundation

struct PrivacyManifestValidator {
    // TODO: Implement validate(category: String, reasonCode: String) -> Bool
    // For "NSPrivacyAccessedAPICategoryUserDefaults", valid codes are ["CA92.1", "1C8F.1"]
    // Return true if category and reason code are valid
    static func validate(category: String, reasonCode: String) -> Bool {
        fatalError("TODO: Implement validate")
    }
}

func runTests() {
    print("🧪 Running Lesson 7.1 Verification Tests...")
    let cat = "NSPrivacyAccessedAPICategoryUserDefaults"
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "CA92.1") == true, "Test 1 Failed")
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "1C8F.1") == true, "Test 2 Failed")
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "INVALID_CODE") == false, "Test 3 Failed")
    assert(PrivacyManifestValidator.validate(category: "UnknownCategory", reasonCode: "CA92.1") == false, "Test 4 Failed")
    print("  ✅ Challenge Passed: Privacy manifest required reason code validation verified")
}
runTests()