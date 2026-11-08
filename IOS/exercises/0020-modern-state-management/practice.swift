import Foundation

struct MockBinding<T> {
    let get: () -> T
    let set: (T) -> Void

    var wrappedValue: T {
        get { get() }
        nonmutating set { set(newValue) }
    }
}

final class StateContainer {
    var count: Int = 0
}

func makeBinding(container: StateContainer) -> MockBinding<Int> {
    // TODO: Return MockBinding<Int>(get: { container.count }, set: { container.count = $0 })
    fatalError("TODO: Implement makeBinding")
}

func runTests() {
    print("🧪 Running Lesson 3.2 Verification Tests...")
    let container = StateContainer()
    let binding = makeBinding(container: container)

    assert(binding.wrappedValue == 0, "Test 1 Failed")
    binding.wrappedValue = 42
    assert(container.count == 42, "Test 2 Failed: Container not updated via binding")
    assert(binding.wrappedValue == 42, "Test 3 Failed")
    print("  ✅ Challenge Passed: Two-way binding mechanics verified")
}
runTests()