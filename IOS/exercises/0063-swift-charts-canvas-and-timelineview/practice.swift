import Foundation

enum ChartMath {
    // TODO: Map value in [minValue, maxValue] to flipped Y coordinate in [height, 0]
    // Return height / 2 if maxValue <= minValue
    static func mapToCanvasY(value: Double, minValue: Double, maxValue: Double, height: Double) -> Double {
        fatalError("TODO: Implement mapToCanvasY")
    }

    // TODO: Average consecutive chunks of size `bucketSize` (last chunk may be smaller)
    static func downsample(_ samples: [Double], bucketSize: Int) -> [Double] {
        fatalError("TODO: Implement downsample")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.3 Verification Tests...")
    assert(ChartMath.mapToCanvasY(value: 0, minValue: 0, maxValue: 100, height: 200) == 200, "Test 1 Failed: Min value maps to bottom (height)")
    assert(ChartMath.mapToCanvasY(value: 100, minValue: 0, maxValue: 100, height: 200) == 0, "Test 2 Failed: Max value maps to top (0)")
    assert(ChartMath.mapToCanvasY(value: 50, minValue: 0, maxValue: 100, height: 200) == 100, "Test 3 Failed")

    let reduced = ChartMath.downsample([10, 20, 30, 40, 50], bucketSize: 2)
    assert(reduced == [15, 35, 50], "Test 4 Failed: Expected [15, 35, 50], got \(reduced)")
    print("  ✅ Challenge Passed: Chart coordinate mapping & downsampling verified")
}
runTests()