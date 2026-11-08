import Foundation

// TODO: Fix SecurityToken so it is safe under Swift 6 strict concurrency
// 1. Mark as final class
// 2. Conform to Sendable
// 3. Make all properties immutable 'let' constants
class SecurityToken {
    var rawToken: String
    var expiresAtEpoch: Int

    init(rawToken: String, expiresAtEpoch: Int) {
        self.rawToken = rawToken
        self.expiresAtEpoch = expiresAtEpoch
    }
}

func transferToken(_ token: SecurityToken) async -> String {
    // Spawn a child task sending the token across concurrency domain:
    let task = Task {
        return "validated_\(token.rawToken)"
    }
    return await task.value
}

func runTests() async {
    print("🧪 Running Lesson 2.6 Verification Tests...")
    let token = SecurityToken(rawToken: "sec_token_999", expiresAtEpoch: 5000)
    let result = await transferToken(token)

    assert(result == "validated_sec_token_999", "Test 1 Failed: Token transfer failed")
    print("  ✅ Challenge Passed: Sendable token transfer verified")
}

await runTests()