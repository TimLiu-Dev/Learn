import Foundation
import CoreGraphics

final class SimulatedView {
    let name: String
    var isHidden: Bool = false
    var isUserInteractionEnabled: Bool = true
    var frame: CGRect
    var subviews: [SimulatedView] = []

    init(name: String, frame: CGRect) {
        self.name = name
        self.frame = frame
    }

    func hitTest(point: CGPoint) -> SimulatedView? {
        guard !isHidden, isUserInteractionEnabled, frame.contains(point) else {
            return nil
        }
        // TODO: Iterate subviews in REVERSE order (subviews.reversed())
        // If a child hitTest returns non-nil, return that child view!
        // If no child catches the touch, return self
        fatalError("TODO: Implement hitTest")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.8 Verification Tests...")
    let root = SimulatedView(name: "RootView", frame: CGRect(x: 0, y: 0, width: 400, height: 800))
    let button = SimulatedView(name: "SubmitButton", frame: CGRect(x: 50, y: 100, width: 200, height: 50))
    root.subviews.append(button)

    // Touch directly inside button:
    let target1 = root.hitTest(point: CGPoint(x: 100, y: 120))
    assert(target1?.name == "SubmitButton", "Test 1 Failed: Expected SubmitButton, got \(target1?.name ?? "nil")")

    // Touch outside button but inside root:
    let target2 = root.hitTest(point: CGPoint(x: 350, y: 700))
    assert(target2?.name == "RootView", "Test 2 Failed")

    // Touch completely outside root:
    let target3 = root.hitTest(point: CGPoint(x: 500, y: 900))
    assert(target3 == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: Responder chain hit-testing verified")
}
runTests()