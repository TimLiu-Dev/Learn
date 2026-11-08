import Foundation

protocol PaymentProcessor {
    func process(amount: Double) -> String
    // TODO: Declare func refund(amount: Double) -> String in the protocol header
    // so that it uses dynamic PWT dispatch rather than static extension dispatch!
}

extension PaymentProcessor {
    func refund(amount: Double) -> String {
        return "Default refund of $\(amount)"
    }
}

struct StripeProcessor: PaymentProcessor {
    func process(amount: Double) -> String {
        return "Stripe charged $\(amount)"
    }

    func refund(amount: Double) -> String {
        return "Stripe refunded $\(amount)"
    }
}

func testDispatch(processor: any PaymentProcessor) -> String {
    return processor.refund(amount: 50.0)
}

func runTests() {
    print("🧪 Running Lesson 1.5 Verification Tests...")
    let stripe = StripeProcessor()
    let result = testDispatch(processor: stripe)

    // With refund declared in protocol header, dynamic PWT dispatch will invoke StripeProcessor's implementation!
    assert(result == "Stripe refunded $50.0", "Test Failed: Expected dynamic PWT dispatch, but received static fallback!")
    print("  ✅ Challenge Passed: Protocol Witness Table dynamic dispatch verified")
}
runTests()