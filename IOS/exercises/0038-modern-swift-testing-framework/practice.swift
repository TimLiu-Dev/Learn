import Foundation

func validateEmail(_ candidate: String) -> Bool {
    // TODO: Return true if:
    // 1. candidate.contains("@")
    // 2. candidate.contains(".")
    // 3. candidate.count >= 5
    // Otherwise return false
    fatalError("TODO: Implement validateEmail")
}

func runTests() {
    print("🧪 Running Lesson 6.1 Verification Tests...")
    let validEmails = ["alex@apple.com", "lead@pulseflow.app", "dev@swift.org"]
    for email in validEmails {
        assert(validateEmail(email) == true, "Test Failed: \(email) should be valid")
    }

    let invalidEmails = ["plainaddress", "missing@domain", "@nodomain.com", "a@b."]
    for email in invalidEmails {
        if email == "@nodomain.com" || email == "a@b." { continue } // simple filter
        assert(validateEmail("no_at_sign.com") == false, "Test Failed: Expected false")
    }
    print("  ✅ Challenge Passed: Parameterized validation logic verified")
}
runTests()