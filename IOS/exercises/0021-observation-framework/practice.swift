import Foundation

final class MockObservationRegistrar {
    private var observers: [String: Set<String>] = [:] // propertyName -> Set<viewId>

    func access(property: String, viewId: String) {
        observers[property, default: []].insert(viewId)
    }

    // TODO: Implement invalidations(for property: String) -> Set<String>
    // Return the set of viewIds observing that property (or empty set if none)
    func invalidations(for property: String) -> Set<String> {
        fatalError("TODO: Implement invalidations")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.3 Verification Tests...")
    let registrar = MockObservationRegistrar()

    // View 1 only reads "name"
    registrar.access(property: "name", viewId: "HeaderView")

    // View 2 only reads "age"
    registrar.access(property: "age", viewId: "AgeLabel")

    // When "name" mutates:
    let nameInv = registrar.invalidations(for: "name")
    assert(nameInv == ["HeaderView"], "Test 1 Failed: Expected only HeaderView to invalidate")

    // When "age" mutates:
    let ageInv = registrar.invalidations(for: "age")
    assert(ageInv == ["AgeLabel"], "Test 2 Failed: Expected only AgeLabel to invalidate")
    print("  ✅ Challenge Passed: Fine-grained property observation verified")
}
runTests()