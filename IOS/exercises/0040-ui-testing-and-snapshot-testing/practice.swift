import Foundation

func compareSnapshots(reference: [UInt8], candidate: [UInt8]) -> Double {
    // TODO: 1. If reference.count != candidate.count || reference.isEmpty return 0.0
    // TODO: 2. Count matching bytes where reference[i] == candidate[i]
    // TODO: 3. Return (Double(matches) / Double(reference.count)) * 100.0
    fatalError("TODO: Implement compareSnapshots")
}

func runTests() {
    print("🧪 Running Lesson 6.3 Verification Tests...")
    let ref: [UInt8] = [255, 255, 0, 0]
    let cand1: [UInt8] = [255, 255, 0, 0]
    assert(compareSnapshots(reference: ref, candidate: cand1) == 100.0, "Test 1 Failed: Exact match should be 100%")

    let cand2: [UInt8] = [255, 255, 255, 255] // 2 of 4 match
    assert(compareSnapshots(reference: ref, candidate: cand2) == 50.0, "Test 2 Failed: Expected 50%")

    let diffSize: [UInt8] = [255]
    assert(compareSnapshots(reference: ref, candidate: diffSize) == 0.0, "Test 3 Failed")
    print("  ✅ Challenge Passed: Snapshot pixel diffing engine verified")
}
runTests()