import Foundation

@MainActor
final class UIStateHolder {
    var text: String = "empty"
    var isMainThread: Bool = false

    func apply(value: String) {
        self.text = value
        self.isMainThread = Thread.isMainThread
    }
}

func deliverUpdate(to holder: UIStateHolder, rawValue: String) async {
    // 1. Perform background string manipulation:
    let formatted = "formatted_\(rawValue.uppercased())"

    // TODO: 2. Hop to MainActor using await MainActor.run { ... }
    // Call holder.apply(value: formatted)
    fatalError("TODO: Implement MainActor hop")
}

func runTests() async {
    print("🧪 Running Lesson 2.5 Verification Tests...")
    let holder = await UIStateHolder()

    // Execute from background detached task:
    await Task.detached {
        await deliverUpdate(to: holder, rawValue: "senior")
    }.value

    let finalText = await holder.text
    let wasOnMain = await holder.isMainThread

    assert(finalText == "formatted_SENIOR", "Test 1 Failed: Text not updated correctly")
    assert(wasOnMain == true, "Test 2 Failed: UI mutation did not execute on Main thread!")
    print("  ✅ Challenge Passed: MainActor thread-hopping verified")
}

await runTests()