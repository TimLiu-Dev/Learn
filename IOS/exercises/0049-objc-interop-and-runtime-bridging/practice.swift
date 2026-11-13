import Foundation

final class Payload {
    let tag: String
    var onDeinit: (() -> Void)?
    init(tag: String, onDeinit: (() -> Void)? = nil) {
        self.tag = tag
        self.onDeinit = onDeinit
    }
    deinit { onDeinit?() }
}

enum BridgeBox {
    // TODO: Convert Payload into an opaque UnsafeMutableRawPointer with +1 retain count
    static func wrapRetained(_ payload: Payload) -> UnsafeMutableRawPointer {
        fatalError("TODO: Implement wrapRetained")
    }

    // TODO: Recover Payload from opaque pointer and consume the +1 retain count
    static func unwrapAndRelease(_ pointer: UnsafeMutableRawPointer) -> Payload {
        fatalError("TODO: Implement unwrapAndRelease")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.1 Verification Tests...")
    var deinitialized = false
    do {
        let raw: UnsafeMutableRawPointer
        do {
            let p = Payload(tag: "legacy-sdk-token") { deinitialized = true }
            raw = BridgeBox.wrapRetained(p)
        }
        assert(deinitialized == false, "Test 1 Failed: Object freed prematurely while retained in opaque pointer")
        let recovered = BridgeBox.unwrapAndRelease(raw)
        assert(recovered.tag == "legacy-sdk-token", "Test 2 Failed: Tag mismatch")
    }
    assert(deinitialized == true, "Test 3 Failed: Object leaked due to unbalanced retain")
    print("  ✅ Challenge Passed: Unmanaged C-context retain/release balance verified")
}
runTests()