import Foundation

protocol SimplePreferenceKey {
    associatedtype Value
    static var defaultValue: Value { get }
    static func reduce(value: inout Value, nextValue: () -> Value)
}

enum MaxWidthPreferenceKey: SimplePreferenceKey {
    static let defaultValue: Double = 0.0

    // TODO: Update value to max(value, nextValue())
    static func reduce(value: inout Double, nextValue: () -> Double) {
        fatalError("TODO: Implement reduce")
    }
}

enum VisibleWindowCalculator {
    // TODO: Return the closed-open Range<Int> of visible row indices given scroll offsetY >= 0
    static func visibleRange(totalCount: Int, offsetY: Double, rowHeight: Double, viewportHeight: Double) -> Range<Int> {
        fatalError("TODO: Implement visibleRange")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.2 Verification Tests...")
    var acc = MaxWidthPreferenceKey.defaultValue
    MaxWidthPreferenceKey.reduce(value: &acc) { 84.0 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 142.5 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 96.0 }
    assert(acc == 142.5, "Test 1 Failed: Expected max width 142.5")

    let r1 = VisibleWindowCalculator.visibleRange(totalCount: 1000, offsetY: 250, rowHeight: 50, viewportHeight: 200)
    assert(r1 == 5..<9, "Test 2 Failed: Expected rows 5..<9, got \(r1)")

    let r2 = VisibleWindowCalculator.visibleRange(totalCount: 10, offsetY: 420, rowHeight: 50, viewportHeight: 200)
    assert(r2 == 8..<10, "Test 3 Failed: Expected clamped rows 8..<10, got \(r2)")
    print("  ✅ Challenge Passed: PreferenceKey reduction & scroll windowing verified")
}
runTests()