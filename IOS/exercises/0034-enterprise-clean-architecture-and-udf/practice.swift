import Foundation

struct CartItem: Equatable {
    let id: String
    let price: Double
}

struct CartState: Equatable {
    var items: [CartItem] = []
    var totalPrice: Double {
        items.reduce(0) { $0 + $1.price }
    }
}

enum CartAction {
    case addItem(CartItem)
    case clearCart
}

func cartReducer(state: inout CartState, action: CartAction) {
    // TODO: Handle CartAction
    // - .addItem: append item to state.items
    // - .clearCart: state.items.removeAll()
    fatalError("TODO: Implement cartReducer")
}

func runTests() {
    print("🧪 Running Lesson 5.1 Verification Tests...")
    var state = CartState()

    cartReducer(state: &state, action: .addItem(CartItem(id: "c1", price: 25.0)))
    cartReducer(state: &state, action: .addItem(CartItem(id: "c2", price: 15.0)))
    assert(state.items.count == 2, "Test 1 Failed")
    assert(state.totalPrice == 40.0, "Test 2 Failed: Expected 40.0, got \(state.totalPrice)")

    cartReducer(state: &state, action: .clearCart)
    assert(state.items.isEmpty, "Test 3 Failed: Cart not cleared")
    assert(state.totalPrice == 0.0, "Test 4 Failed")
    print("  ✅ Challenge Passed: UDF pure reducer state machine verified")
}
runTests()