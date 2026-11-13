import Foundation

struct CounterState: Equatable {
    var count: Int = 0
    var isSyncing: Bool = false
}

enum CounterAction: Equatable {
    case increment
    case syncRequested
    case syncCompleted(Int)
}

enum MiniEffect {
    case none
    case emit(CounterAction)
}

func reduce(state: inout CounterState, action: CounterAction) -> MiniEffect {
    // TODO:
    // .increment -> state.count += 1, return .none
    // .syncRequested -> state.isSyncing = true, return .emit(.syncCompleted(state.count))
    // .syncCompleted(let serverVal) -> state.isSyncing = false, state.count = serverVal, return .none
    fatalError("TODO: Implement reduce")
}

final class MiniTestStore {
    private(set) var state: CounterState
    private var pendingActions: [CounterAction] = []

    init(initialState: CounterState) { self.state = initialState }

    func send(_ action: CounterAction, assertState: (inout CounterState) -> Void) {
        var expected = state
        assertState(&expected)
        let effect = reduce(state: &state, action: action)
        assert(state == expected, "State mismatch after send(\(action))")
        if case .emit(let next) = effect {
            pendingActions.append(next)
        }
    }

    func receive(_ expectedAction: CounterAction, assertState: (inout CounterState) -> Void) {
        // TODO: Pop first pending action, assert it equals expectedAction, reduce it, and assert state matches expected
        fatalError("TODO: Implement receive")
    }

    func finish() {
        assert(pendingActions.isEmpty, "Exhaustivity failure: unreceived actions \(pendingActions)")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.4 Verification Tests...")
    let store = MiniTestStore(initialState: CounterState())
    store.send(.increment) { $0.count = 1 }
    store.send(.syncRequested) { $0.isSyncing = true }
    store.receive(.syncCompleted(1)) { $0.isSyncing = false; $0.count = 1 }
    store.finish()
    print("  ✅ Challenge Passed: Exhaustive Reducer & TestStore loop verified")
}
runTests()