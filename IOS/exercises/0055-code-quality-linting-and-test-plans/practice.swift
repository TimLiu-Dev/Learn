import Foundation

struct CIPolicy {
    let swiftFormatStrict: Bool
    let swiftLintRules: Set<String>
    let testPlanRandomized: Bool
    let sanitizers: Set<String>
}

enum QualityGate {
    // TODO: Return true iff swiftFormatStrict is true,
    // swiftLintRules contains "force_unwrapping",
    // testPlanRandomized is true, and sanitizers contains "TSan"
    static func evaluate(_ policy: CIPolicy) -> Bool {
        fatalError("TODO: Implement evaluate")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.7 Verification Tests...")
    let senior = CIPolicy(swiftFormatStrict: true, swiftLintRules: ["force_unwrapping", "weak_delegate"], testPlanRandomized: true, sanitizers: ["TSan"])
    let weak = CIPolicy(swiftFormatStrict: true, swiftLintRules: [], testPlanRandomized: false, sanitizers: [])
    assert(QualityGate.evaluate(senior) == true, "Test 1 Failed")
    assert(QualityGate.evaluate(weak) == false, "Test 2 Failed")
    print("  ✅ Challenge Passed: CI quality gates and TestPlan policy verified")
}
runTests()