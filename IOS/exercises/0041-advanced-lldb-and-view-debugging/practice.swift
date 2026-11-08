import Foundation

struct FrameVariableInspector {
    // TODO: Implement inspect(frame: [String: Any], key: String) -> String
    // If key exists in frame, return "\(val)"
    // If key does not exist, return "nil"
    static func inspect(frame: [String: Any], key: String) -> String {
        fatalError("TODO: Implement inspect")
    }
}

func runTests() {
    print("🧪 Running Lesson 6.4 Verification Tests...")
    let frame: [String: Any] = ["userId": 101, "role": "Senior", "isActive": true]

    assert(FrameVariableInspector.inspect(frame: frame, key: "role") == "Senior", "Test 1 Failed")
    assert(FrameVariableInspector.inspect(frame: frame, key: "userId") == "101", "Test 2 Failed")
    assert(FrameVariableInspector.inspect(frame: frame, key: "missing") == "nil", "Test 3 Failed")
    print("  ✅ Challenge Passed: Frame variable inspection verified")
}
runTests()