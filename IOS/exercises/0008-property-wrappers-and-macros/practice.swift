import Foundation

@propertyWrapper
struct Trimmed {
    private var text: String
    private(set) var wasTrimmed: Bool = false

    init(wrappedValue: String) {
        // TODO: Clean whitespace and set wasTrimmed to true if cleaned != wrappedValue
        fatalError("TODO: Implement Trimmed init")
    }

    var wrappedValue: String {
        get { text }
        set {
            // TODO: Clean whitespace on newValue and update wasTrimmed
            fatalError("TODO: Implement Trimmed setter")
        }
    }

    var projectedValue: Bool {
        wasTrimmed
    }
}

struct UserInput {
    @Trimmed var username: String
}

func runTests() {
    print("🧪 Running Lesson 1.8 Verification Tests...")
    var input = UserInput(username: "  AlexiOS  ")
    assert(input.username == "AlexiOS", "Test 1 Failed: Whitespace not trimmed")
    assert(input.$username == true, "Test 2 Failed: Projected value wasTrimmed should be true")

    input.username = "CleanName"
    assert(input.username == "CleanName", "Test 3 Failed")
    assert(input.$username == false, "Test 4 Failed: Projected value should be false for clean name")
    print("  ✅ Challenge Passed: Custom property wrapper and projectedValue verified")
}
runTests()