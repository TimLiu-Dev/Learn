import Foundation

enum DocCValidator {
    // TODO: Extract all substrings enclosed in ``...`` (e.g., "Calls ``SyncEngine/run()`` and ``AuthToken``" -> ["SyncEngine/run()", "AuthToken"])
    static func extractSymbolLinks(from docComment: String) -> [String] {
        fatalError("TODO: Implement extractSymbolLinks")
    }
}

enum PreviewAuditor {
    // TODO: Return true iff usesInMemoryStore == true && usesLiveNetwork == false
    static func isPreviewSafe(usesInMemoryStore: Bool, usesLiveNetwork: Bool) -> Bool {
        fatalError("TODO: Implement isPreviewSafe")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.6 Verification Tests...")
    let doc = "Coordinates ``PulseEngine/sync()`` using ``MockStore`` before rendering."
    assert(DocCValidator.extractSymbolLinks(from: doc) == ["PulseEngine/sync()", "MockStore"], "Test 1 Failed")
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: false) == true, "Test 2 Failed")
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: true) == false, "Test 3 Failed")
    print("  ✅ Challenge Passed: DocC symbol extraction & Preview isolation verified")
}
runTests()