import Foundation

final class MockKeychainVault {
    private var vault: [String: Data] = [:]

    // TODO: Implement saveSecret(account: String, secret: String) -> Bool
    // Convert secret string to utf8 Data and store in vault[account]. Return true.
    func saveSecret(account: String, secret: String) -> Bool {
        fatalError("TODO: Implement saveSecret")
    }

    // TODO: Implement retrieveSecret(account: String) -> String?
    // Retrieve Data from vault[account], decode as UTF-8 string. Return nil if not found.
    func retrieveSecret(account: String) -> String? {
        fatalError("TODO: Implement retrieveSecret")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.1 Verification Tests...")
    let keychain = MockKeychainVault()

    let saved = keychain.saveSecret(account: "auth_token", secret: "jwt_secret_xyz")
    assert(saved == true, "Test 1 Failed")

    let retrieved = keychain.retrieveSecret(account: "auth_token")
    assert(retrieved == "jwt_secret_xyz", "Test 2 Failed: Secret mismatch")

    let missing = keychain.retrieveSecret(account: "unknown")
    assert(missing == nil, "Test 3 Failed: Missing secret should return nil")
    print("  ✅ Challenge Passed: Keychain storage mechanics verified")
}
runTests()