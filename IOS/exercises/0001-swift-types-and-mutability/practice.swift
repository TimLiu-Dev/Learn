import Foundation

struct UserSession: Equatable {
    let sessionId: String
    var userId: String
    var expiresAtEpoch: Int

    // TODO: Add a mutating func extendSession(seconds: Int) that increments expiresAtEpoch
}

// TODO: Implement renewSession(session: UserSession) -> UserSession
// Create a mutable copy of session, extend it by 3600 seconds, and return it.
func renewSession(session: UserSession) -> UserSession {
    fatalError("TODO: Implement renewSession")
}

// TODO: Implement normalizeUsername(_ username: inout String)
// - Trim leading/trailing whitespace and newlines
// - Lowercase the string
// - If empty after trimming, set username to "guest_user"
func normalizeUsername(_ username: inout String) {
    fatalError("TODO: Implement normalizeUsername")
}

// MARK: - Verification Test Suite
func runTests() {
    print("🧪 Running Lesson 1.1 Verification Tests...")

    // Test 1: Struct Mutability
    let initialSession = UserSession(sessionId: "sess_001", userId: "u123", expiresAtEpoch: 1000)
    let renewed = renewSession(session: initialSession)
    assert(initialSession.expiresAtEpoch == 1000, "Test 1A Failed: Original struct was mutated unexpectedly")
    assert(renewed.expiresAtEpoch == 4600, "Test 1B Failed: Renewed session not extended")
    print("  ✅ Challenge 1 Passed: Struct value immutability & mutating func")

    // Test 2: inout Normalization
    var name1 = "   AlexDeveloper\n  "
    normalizeUsername(&name1)
    assert(name1 == "alexdeveloper", "Test 2A Failed: whitespace/lowercase not normalized")

    var name2 = "   "
    normalizeUsername(&name2)
    assert(name2 == "guest_user", "Test 2B Failed: empty string not reset to guest_user")
    print("  ✅ Challenge 2 Passed: inout parameter copy-in copy-out")

    print("\n🎉 All Lesson 1.1 tests passed successfully!")
}

runTests()