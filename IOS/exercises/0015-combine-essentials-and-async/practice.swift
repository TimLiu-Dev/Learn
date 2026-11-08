import Foundation
import Combine

func consumeThreeValues(subject: CurrentValueSubject<Int, Never>) async -> [Int] {
    var output: [Int] = []
    // TODO: Iterate over subject.values using for await val in subject.values
    // Append val to output. When output.count == 3, break and return output.
    fatalError("TODO: Implement consumeThreeValues")
}

func runTests() async {
    print("🧪 Running Lesson 2.7 Verification Tests...")
    let subject = CurrentValueSubject<Int, Never>(10)

    Task {
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(20)
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(30)
    }

    let results = await consumeThreeValues(subject: subject)
    assert(results == [10, 20, 30], "Test Failed: Expected [10, 20, 30], got \(results)")
    print("  ✅ Challenge Passed: Combine to AsyncSequence bridging verified")
}

await runTests()