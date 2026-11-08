import Foundation

enum ViewState: Equatable {
    case idle
    case loading
    case loaded(String)
    case failed
}

protocol NameService: Sendable {
    func fetchName() async throws -> String
}

final class MockNameService: NameService {
    var stubbedResult: Result<String, Error> = .success("TestUser")
    private(set) var callCount = 0

    func fetchName() async throws -> String {
        callCount += 1
        return try stubbedResult.get()
    }
}

final class UserViewModel {
    let service: any NameService
    var state: ViewState = .idle

    init(service: any NameService) {
        self.service = service
    }

    func loadData() async {
        // TODO: 1. Set state = .loading
        // TODO: 2. Do: let name = try await service.fetchName(); state = .loaded(name)
        // TODO: 3. Catch: state = .failed
        fatalError("TODO: Implement loadData")
    }
}

func runTests() async {
    print("🧪 Running Lesson 6.2 Verification Tests...")
    let mock = MockNameService()
    let vm = UserViewModel(service: mock)

    // Test Success
    await vm.loadData()
    assert(vm.state == .loaded("TestUser"), "Test 1 Failed: Expected loaded(TestUser)")
    assert(mock.callCount == 1, "Test 2 Failed")

    // Test Failure
    mock.stubbedResult = .failure(NSError(domain: "test", code: -1))
    await vm.loadData()
    assert(vm.state == .failed, "Test 3 Failed: Expected failed state")
    assert(mock.callCount == 2, "Test 4 Failed")
    print("  ✅ Challenge Passed: Async ViewModel state transitions verified with test double")
}

await runTests()