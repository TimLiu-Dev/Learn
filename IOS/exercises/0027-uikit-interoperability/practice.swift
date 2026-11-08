import Foundation

final class MockCoordinator {
    var textBinding: String

    init(initialText: String) {
        self.textBinding = initialText
    }

    // TODO: Implement textDidChange(newText: String) -> mutate textBinding = newText
    func textDidChange(newText: String) {
        fatalError("TODO: Implement textDidChange")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.9 Verification Tests...")
    let coordinator = MockCoordinator(initialText: "initial")
    coordinator.textDidChange(newText: "searching swift 6")

    assert(coordinator.textBinding == "searching swift 6", "Test Failed: Binding was not updated")
    print("  ✅ Challenge Passed: Coordinator delegate bridge verified")
}
runTests()