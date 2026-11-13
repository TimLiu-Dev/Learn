import Foundation

struct XCFrameworkManifest {
    let slices: Set<String>
    let hasSwiftInterface: Bool
    let sha256Checksum: String
}

enum BinaryPackageValidator {
    // TODO: Return true iff slices contains both "ios-arm64" and "ios-arm64-simulator",
    // hasSwiftInterface is true, and sha256Checksum matches expectedChecksum
    static func validate(_ manifest: XCFrameworkManifest, expectedChecksum: String) -> Bool {
        fatalError("TODO: Implement validate")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.6 Verification Tests...")
    let valid = XCFrameworkManifest(
        slices: ["ios-arm64", "ios-arm64-simulator"],
        hasSwiftInterface: true,
        sha256Checksum: "abc123"
    )
    let missingSim = XCFrameworkManifest(
        slices: ["ios-arm64"],
        hasSwiftInterface: true,
        sha256Checksum: "abc123"
    )
    let noEvolution = XCFrameworkManifest(
        slices: ["ios-arm64", "ios-arm64-simulator"],
        hasSwiftInterface: false,
        sha256Checksum: "abc123"
    )
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "abc123") == true, "Test 1 Failed")
    assert(BinaryPackageValidator.validate(missingSim, expectedChecksum: "abc123") == false, "Test 2 Failed")
    assert(BinaryPackageValidator.validate(noEvolution, expectedChecksum: "abc123") == false, "Test 3 Failed")
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "tampered") == false, "Test 4 Failed")
    print("  ✅ Challenge Passed: XCFramework module stability & slice integrity verified")
}
runTests()