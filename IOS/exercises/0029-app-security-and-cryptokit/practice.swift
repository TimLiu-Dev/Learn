import Foundation
import CryptoKit

func verifyPayloadHash(data: Data, expectedHexHash: String) -> Bool {
    // TODO: 1. Compute digest = SHA256.hash(data: data)
    // TODO: 2. Convert digest to hex string: digest.map { String(format: "%02x", $0) }.joined()
    // TODO: 3. Return computedHex == expectedHexHash.lowercased()
    fatalError("TODO: Implement verifyPayloadHash")
}

func runTests() {
    print("🧪 Running Lesson 4.2 Verification Tests...")
    let sampleData = "Senior iOS Developer".data(using: .utf8)!
    // SHA256 of "Senior iOS Developer"
    let knownHash = "d1558b35e6abd18d9556727f15ebe319dcccccb597eb905f3ebb26154c546e82"

    let isValid = verifyPayloadHash(data: sampleData, expectedHexHash: knownHash)
    assert(isValid == true, "Test 1 Failed: Hash mismatch")

    let isInvalid = verifyPayloadHash(data: sampleData, expectedHexHash: "bad_hash_123")
    assert(isInvalid == false, "Test 2 Failed")
    print("  ✅ Challenge Passed: CryptoKit SHA-256 payload verification verified")
}
runTests()