import Foundation

nonisolated(unsafe) var closedDescriptors: [Int32] = []

struct FileDescriptorGuard: ~Copyable {
    let fd: Int32

    init(fd: Int32) {
        self.fd = fd
    }

    // TODO: Implement consuming func transferOwnership() -> Int32
    // Return fd and call discard self so deinit does NOT fire
    consuming func transferOwnership() -> Int32 {
        fatalError("TODO: Implement transferOwnership")
    }

    deinit {
        closedDescriptors.append(fd)
    }
}

// TODO: Implement computeChecksum(_ span: Span<Int>) -> Int without allocating an Array
func computeChecksum(_ span: Span<Int>) -> Int {
    fatalError("TODO: Implement computeChecksum")
}

func runTests() {
    print("🧪 Running Lesson 8.3 Verification Tests...")
    closedDescriptors.removeAll()
    var transferredFD: Int32 = -1
    do {
        let g1 = FileDescriptorGuard(fd: 101)
        transferredFD = g1.transferOwnership()
        let _ = FileDescriptorGuard(fd: 202) // Dropped at scope exit -> deinit closes it
    }
    assert(transferredFD == 101, "Test 1 Failed: Expected fd 101 returned")
    assert(closedDescriptors == [202], "Test 2 Failed: Only un-transferred fd 202 should close in deinit")

    let samples: InlineArray<4, Int> = [10, 20, 30, 40]
    assert(computeChecksum(samples.span) == 100, "Test 3 Failed: Expected Span sum of 100")
    print("  ✅ Challenge Passed: ~Copyable ownership and InlineArray/Span verified")
}
runTests()