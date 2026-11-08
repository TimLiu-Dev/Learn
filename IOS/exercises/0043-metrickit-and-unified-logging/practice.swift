import Foundation

enum PrivacyLevel {
    case `public`
    case `private`
}

func logMessage(category: String, message: String, secret: String, privacy: PrivacyLevel) -> String {
    // TODO: If privacy is .private, format as: "[CATEGORY] MESSAGE [<private>]"
    // If privacy is .public, format as: "[CATEGORY] MESSAGE [SECRET]"
    fatalError("TODO: Implement logMessage")
}

func runTests() {
    print("🧪 Running Lesson 6.6 Verification Tests...")
    let priv = logMessage(category: "auth", message: "Login attempt", secret: "user_email@test.com", privacy: .private)
    assert(priv == "[AUTH] Login attempt [<private>]", "Test 1 Failed: Got \(priv)")

    let pub = logMessage(category: "net", message: "GET /status", secret: "200 OK", privacy: .public)
    assert(pub == "[NET] GET /status [200 OK]", "Test 2 Failed: Got \(pub)")
    print("  ✅ Challenge Passed: Structured logging with PII redaction verified")
}
runTests()