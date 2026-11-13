import Foundation

struct StoreTransaction {
    let id: UInt64
    let productID: String
    let revocationDate: Date?
}

enum VerificationOutcome {
    case verified(StoreTransaction)
    case unverified(StoreTransaction)
}

final class EntitlementEngine {
    private(set) var activeProducts: Set<String> = []
    private(set) var finishedIDs: [UInt64] = []

    // TODO:
    // - Ignore .unverified transactions (do not grant, do not finish)
    // - For .verified(tx): if tx.revocationDate != nil, remove tx.productID from activeProducts;
    //   otherwise insert tx.productID into activeProducts. Always append tx.id to finishedIDs.
    func process(_ outcome: VerificationOutcome) {
        fatalError("TODO: Implement process")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.11 Verification Tests...")
    let engine = EntitlementEngine()
    engine.process(.unverified(StoreTransaction(id: 1, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs.isEmpty, "Test 1 Failed: Unverified tx must not unlock or finish")

    engine.process(.verified(StoreTransaction(id: 2, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts == ["pro.yearly"] && engine.finishedIDs == [2], "Test 2 Failed")

    engine.process(.verified(StoreTransaction(id: 3, productID: "pro.yearly", revocationDate: Date())))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs == [2, 3], "Test 3 Failed: Revoked tx must remove entitlement")
    print("  ✅ Challenge Passed: StoreKit 2 verification & revocation handling verified")
}
runTests()