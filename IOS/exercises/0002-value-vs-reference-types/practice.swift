import Foundation

final class Storage<T> {
    var payload: T
    init(payload: T) { self.payload = payload }
}

struct CoWStore<T> {
    private var storage: Storage<T>

    init(payload: T) {
        self.storage = Storage(payload: payload)
    }

    var value: T {
        get { storage.payload }
        set {
            // TODO: Use isKnownUniquelyReferenced(&storage)
            // If NOT uniquely referenced, clone storage with newValue.
            // If uniquely referenced, mutate storage.payload directly.
            fatalError("TODO: Implement CoW setter")
        }
    }

    var pointerAddress: UnsafeMutableRawPointer {
        Unmanaged.passUnretained(storage).toOpaque()
    }
}

// MARK: - Verification
func runTests() {
    print("🧪 Running Lesson 1.2 Verification Tests...")
    let store1 = CoWStore(payload: "initial_data")
    var store2 = store1

    assert(store1.pointerAddress == store2.pointerAddress, "Test 1A Failed: Storage should be shared before write")
    store2.value = "modified_data"
    assert(store1.pointerAddress != store2.pointerAddress, "Test 1B Failed: Storage must diverge after write")
    assert(store1.value == "initial_data", "Test 1C Failed: store1 corrupted")
    assert(store2.value == "modified_data", "Test 1D Failed: store2 update failed")
    print("  ✅ Challenge Passed: Custom CoW storage isolation verified")
}
runTests()