import Foundation

protocol DataStore<T> {
    associatedtype T
    func retrieve() -> T
}

struct InMemoryDataStore<T>: DataStore {
    let value: T
    func retrieve() -> T { value }
}

final class AnyDataStore<T>: DataStore {
    private let _retrieve: () -> T

    // TODO: Implement init<D: DataStore>(_ store: D) where D.T == T
    // Store the closure { store.retrieve() } into self._retrieve
    init<D: DataStore>(_ store: D) where D.T == T {
        fatalError("TODO: Implement AnyDataStore init")
    }

    func retrieve() -> T {
        _retrieve()
    }
}

func runTests() {
    print("🧪 Running Lesson 1.7 Verification Tests...")
    let concreteStore = InMemoryDataStore(value: "Senior iOS Engineer")
    let erasedStore = AnyDataStore(concreteStore)

    assert(erasedStore.retrieve() == "Senior iOS Engineer", "Test Failed: Erased store did not return expected value")
    print("  ✅ Challenge Passed: Type-erased wrapper verified")
}
runTests()