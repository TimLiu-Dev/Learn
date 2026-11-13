import Foundation

enum ModelAvailability {
    case available
    case unavailable(reason: String)
}

enum TriageBackend: Equatable {
    case onDeviceFoundationModel
    case cloudFallback
    case deterministicRuleEngine
}

enum AITriageRouter {
    // TODO: Select appropriate backend based on availability & offline constraint
    static func selectBackend(availability: ModelAvailability, isOffline: Bool) -> TriageBackend {
        fatalError("TODO: Implement selectBackend")
    }

    // TODO: Clamp/validate @Generable severity into 1...5; return nil if summary is empty
    static func validateTriage(severity: Int, summary: String) -> (Int, String)? {
        fatalError("TODO: Implement validateTriage")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.9 Verification Tests...")
    assert(AITriageRouter.selectBackend(availability: .available, isOffline: true) == .onDeviceFoundationModel, "Test 1 Failed")
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Older chip"), isOffline: false) == .cloudFallback, "Test 2 Failed")
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Disabled"), isOffline: true) == .deterministicRuleEngine, "Test 3 Failed")
    assert(AITriageRouter.validateTriage(severity: 9, summary: "Memory spike")?.0 == 5, "Test 4 Failed")
    assert(AITriageRouter.validateTriage(severity: 3, summary: "   ") == nil, "Test 5 Failed")
    print("  ✅ Challenge Passed: FoundationModels availability routing & guide validation verified")
}
runTests()