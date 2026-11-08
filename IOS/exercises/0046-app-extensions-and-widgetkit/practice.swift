import Foundation

func generateTimelineIntervals(startHour: Int, count: Int) -> [Int] {
    // TODO: Return an array of Int representing consecutive hours from startHour to (startHour + count - 1)
    // If count <= 0 return []
    fatalError("TODO: Implement generateTimelineIntervals")
}

func runTests() {
    print("🧪 Running Lesson 7.3 Verification Tests...")
    let timeline = generateTimelineIntervals(startHour: 10, count: 4)
    assert(timeline == [10, 11, 12, 13], "Test 1 Failed: Got \(timeline)")

    let empty = generateTimelineIntervals(startHour: 5, count: 0)
    assert(empty.isEmpty, "Test 2 Failed")
    print("  ✅ Challenge Passed: Timeline interval generation verified")
}
runTests()