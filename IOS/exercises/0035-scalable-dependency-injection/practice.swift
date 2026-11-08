import Foundation

protocol AuthService: Sendable {
    func authenticate() -> String
}

struct LiveAuthService: AuthService {
    func authenticate() -> String { "LIVE_AUTH_TOKEN" }
}

struct MockAuthService: AuthService {
    func authenticate() -> String { "MOCK_TEST_TOKEN" }
}

final class LoginViewModel {
    let authService: any AuthService
    init(authService: any AuthService) {
        self.authService = authService
    }
}

struct AppFactory {
    // TODO: Implement makeViewModel(isTest: Bool) -> LoginViewModel
    // If isTest is true, inject MockAuthService(), else LiveAuthService()
    static func makeViewModel(isTest: Bool) -> LoginViewModel {
        fatalError("TODO: Implement makeViewModel")
    }
}

func runTests() {
    print("🧪 Running Lesson 5.2 Verification Tests...")
    let testVM = AppFactory.makeViewModel(isTest: true)
    assert(testVM.authService.authenticate() == "MOCK_TEST_TOKEN", "Test 1 Failed")

    let liveVM = AppFactory.makeViewModel(isTest: false)
    assert(liveVM.authService.authenticate() == "LIVE_AUTH_TOKEN", "Test 2 Failed")
    print("  ✅ Challenge Passed: Protocol-based DI factory verified")
}
runTests()