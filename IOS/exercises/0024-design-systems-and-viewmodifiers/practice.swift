import Foundation

struct DesignTokens {
    // TODO: Implement formatBadge(text: String, category: String) -> String
    // Return: "[CATEGORY_UPPER] TEXT_UPPER"
    // Example: category "info", text "active" -> "[INFO] ACTIVE"
    static func formatBadge(text: String, category: String) -> String {
        fatalError("TODO: Implement formatBadge")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.6 Verification Tests...")
    let badge = DesignTokens.formatBadge(text: "verified account", category: "security")
    assert(badge == "[SECURITY] VERIFIED ACCOUNT", "Test 1 Failed: Got \(badge)")
    print("  ✅ Challenge Passed: Design token formatter verified")
}
runTests()