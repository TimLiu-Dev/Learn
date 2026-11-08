import Foundation

struct EnvironmentResolver {
    // TODO: Implement resolveBaseURL(for env: String) -> String
    // "DEBUG" -> "https://api-dev.pulseflow.app"
    // "STAGING" -> "https://api-stage.pulseflow.app"
    // "PRODUCTION" -> "https://api.pulseflow.app"
    // default -> "https://api.pulseflow.app"
    static func resolveBaseURL(for env: String) -> String {
        fatalError("TODO: Implement resolveBaseURL")
    }
}

func runTests() {
    print("🧪 Running Lesson 5.4 Verification Tests...")
    assert(EnvironmentResolver.resolveBaseURL(for: "DEBUG") == "https://api-dev.pulseflow.app", "Test 1 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "STAGING") == "https://api-stage.pulseflow.app", "Test 2 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "PRODUCTION") == "https://api.pulseflow.app", "Test 3 Failed")
    assert(EnvironmentResolver.resolveBaseURL(for: "UNKNOWN") == "https://api.pulseflow.app", "Test 4 Failed")
    print("  ✅ Challenge Passed: Environment configuration resolver verified")
}
runTests()