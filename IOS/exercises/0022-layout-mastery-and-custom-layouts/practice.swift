import Foundation

func calculateFlowWidths(itemWidths: [Double], spacing: Double) -> Double {
    // TODO: Calculate the total horizontal width:
    // Sum of all item widths + (spacing * (count - 1)) if count > 0, otherwise 0.0
    fatalError("TODO: Implement calculateFlowWidths")
}

func runTests() {
    print("🧪 Running Lesson 3.4 Verification Tests...")
    let widths = [50.0, 75.0, 100.0]
    let total = calculateFlowWidths(itemWidths: widths, spacing: 10.0)
    // 50 + 75 + 100 = 225; 2 spaces of 10 = 20 -> 245.0
    assert(total == 245.0, "Test 1 Failed: Expected 245.0, got \(total)")

    let emptyTotal = calculateFlowWidths(itemWidths: [], spacing: 10.0)
    assert(emptyTotal == 0.0, "Test 2 Failed")
    print("  ✅ Challenge Passed: Layout coordinate calculations verified")
}
runTests()