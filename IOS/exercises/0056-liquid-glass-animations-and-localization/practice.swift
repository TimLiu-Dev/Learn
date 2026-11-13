import Foundation

enum UILayer {
    case scrollingCell
    case floatingToolbar
    case navigationChrome
}

enum UIHygieneChecker {
    // TODO: Return true only for .floatingToolbar and .navigationChrome
    static func canApplyGlass(layer: UILayer) -> Bool {
        fatalError("TODO: Implement canApplyGlass")
    }

    // TODO: Return "zero" for 0, "one" for 1, "other" for all other non-negative counts
    static func pluralKey(for count: Int) -> String {
        fatalError("TODO: Implement pluralKey")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.8 Verification Tests...")
    assert(UIHygieneChecker.canApplyGlass(layer: .floatingToolbar) == true, "Test 1 Failed")
    assert(UIHygieneChecker.canApplyGlass(layer: .navigationChrome) == true, "Test 2 Failed")
    assert(UIHygieneChecker.canApplyGlass(layer: .scrollingCell) == false, "Test 3 Failed")
    assert(UIHygieneChecker.pluralKey(for: 0) == "zero", "Test 4 Failed")
    assert(UIHygieneChecker.pluralKey(for: 1) == "one", "Test 5 Failed")
    assert(UIHygieneChecker.pluralKey(for: 42) == "other", "Test 6 Failed")
    print("  ✅ Challenge Passed: Liquid Glass layering & String Catalog pluralization verified")
}
runTests()