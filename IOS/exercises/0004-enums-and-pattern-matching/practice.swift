import Foundation

enum AuthState: Equatable {
    case unauthenticated
    case authenticating
    case authenticated(token: String)
    case error(message: String)
}

enum AuthAction {
    case startLogin
    case loginSuccess(token: String)
    case loginFailure(error: String)
    case logout
}

func reduce(state: AuthState, action: AuthAction) -> AuthState {
    switch (state, action) {
    case (.unauthenticated, .startLogin):
        return .authenticating
    // TODO: Handle (.authenticating, .loginSuccess(let token)) -> return .authenticated(token: token)
    // TODO: Handle (.authenticating, .loginFailure(let err)) -> return .error(message: err)
    // TODO: Handle (.authenticated, .logout) -> return .unauthenticated
    default:
        return state
    }
}

func runTests() {
    print("🧪 Running Lesson 1.4 Verification Tests...")
    let s1 = reduce(state: .unauthenticated, action: .startLogin)
    assert(s1 == .authenticating, "Test 1 Failed")

    let s2 = reduce(state: s1, action: .loginSuccess(token: "jwt_token_999"))
    assert(s2 == .authenticated(token: "jwt_token_999"), "Test 2 Failed")

    let s3 = reduce(state: s2, action: .logout)
    assert(s3 == .unauthenticated, "Test 3 Failed")
    print("  ✅ Challenge Passed: Algebraic state transitions verified")
}
runTests()