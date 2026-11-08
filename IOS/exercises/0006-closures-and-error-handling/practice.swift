import Foundation

enum TransactionError: Error, Equatable {
    case insufficientFunds
    case accountLocked
}

final class BankVault {
    var balance: Double = 500.0
    var isLocked: Bool = false
    var auditLog: [String] = []

    func acquireLock() {
        isLocked = true
        auditLog.append("LOCK_ACQUIRED")
    }

    func releaseLock() {
        isLocked = false
        auditLog.append("LOCK_RELEASED")
    }

    // TODO: Implement executeSecureTransaction(amount: Double) throws(TransactionError) -> Double
    // 1. acquireLock()
    // 2. Use defer { releaseLock() } to guarantee lock is ALWAYS released even on error!
    // 3. If amount > balance, throw TransactionError.insufficientFunds
    // 4. Otherwise subtract amount and return new balance
    func executeSecureTransaction(amount: Double) throws(TransactionError) -> Double {
        fatalError("TODO: Implement executeSecureTransaction")
    }
}

func runTests() {
    print("🧪 Running Lesson 1.6 Verification Tests...")
    let vault = BankVault()

    // Test 1: Successful Transaction
    do {
        let newBal = try vault.executeSecureTransaction(amount: 200.0)
        assert(newBal == 300.0, "Test 1A Failed")
        assert(vault.isLocked == false, "Test 1B Failed: Lock not released")
        assert(vault.auditLog == ["LOCK_ACQUIRED", "LOCK_RELEASED"], "Test 1C Failed")
        print("  ✅ Challenge 1 Passed: Successful transaction with defer cleanup")
    } catch {
        fatalError("Unexpected error: \(error)")
    }

    // Test 2: Error path with defer release
    vault.auditLog.removeAll()
    do {
        _ = try vault.executeSecureTransaction(amount: 999.0)
        fatalError("Test 2 Failed: Should have thrown insufficientFunds")
    } catch TransactionError.insufficientFunds {
        assert(vault.isLocked == false, "Test 2B Failed: Lock not released on error exit!")
        assert(vault.auditLog == ["LOCK_ACQUIRED", "LOCK_RELEASED"], "Test 2C Failed: Defer not executed on throw")
        print("  ✅ Challenge 2 Passed: Typed throws with guaranteed defer cleanup")
    } catch {
        fatalError("Unexpected error type: \(error)")
    }
}
runTests()