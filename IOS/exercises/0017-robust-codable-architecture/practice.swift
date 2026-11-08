import Foundation

struct User: Codable, Equatable {
    let id: Int
    let name: String
}

func decodeLossyList(jsonData: Data) -> [User] {
    // Helper struct for lossy element parsing
    struct LossyElement<T: Decodable>: Decodable {
        let value: T?
        init(from decoder: Decoder) throws {
            let container = try decoder.singleValueContainer()
            self.value = try? container.decode(T.self)
        }
    }

    // TODO: 1. Decode [LossyElement<User>] from jsonData
    // TODO: 2. Return compactMap { $0.value }
    // If decoding fails completely, return []
    fatalError("TODO: Implement decodeLossyList")
}

func runTests() {
    print("🧪 Running Lesson 2.9 Verification Tests...")
    let rawJSON = """
    [
        {"id": 1, "name": "Alice"},
        {"id": "invalid_id", "name": "Bob"},
        {"id": 3, "name": "Charlie"},
        {"broken_json": true}
    ]
    """.data(using: .utf8)!

    let validUsers = decodeLossyList(jsonData: rawJSON)
    assert(validUsers.count == 2, "Test 1 Failed: Expected 2 valid users, got \(validUsers.count)")
    assert(validUsers[0] == User(id: 1, name: "Alice"), "Test 2 Failed")
    assert(validUsers[1] == User(id: 3, name: "Charlie"), "Test 3 Failed")
    print("  ✅ Challenge Passed: Lossy array decoding rescued valid users")
}
runTests()