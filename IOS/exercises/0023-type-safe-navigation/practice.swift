import Foundation

enum AppRoute: Hashable, Equatable {
    case userProfile(id: String)
    case transactionDetail(txId: String)
}

func parseDeepLinkURL(_ url: URL) -> AppRoute? {
    // Expected host: "user" -> pathComponents[1] is id
    // Expected host: "tx" -> pathComponents[1] is txId
    // TODO: Inspect url.host and url.pathComponents to return the matching AppRoute
    fatalError("TODO: Implement parseDeepLinkURL")
}

func runTests() {
    print("🧪 Running Lesson 3.5 Verification Tests...")
    let u1 = URL(string: "pulseflow://user/usr_99")!
    let r1 = parseDeepLinkURL(u1)
    assert(r1 == .userProfile(id: "usr_99"), "Test 1 Failed")

    let u2 = URL(string: "pulseflow://tx/tx_888")!
    let r2 = parseDeepLinkURL(u2)
    assert(r2 == .transactionDetail(txId: "tx_888"), "Test 2 Failed")

    let u3 = URL(string: "pulseflow://unknown/123")!
    assert(parseDeepLinkURL(u3) == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: Deep link URL parsing verified")
}
runTests()