import Foundation

final class WorkerSession {
    let id: String
    var completionHandler: (() -> Void)?
    static var deinitCount = 0

    init(id: String) {
        self.id = id
        // TODO: Fix retain cycle by using [weak self] in closure capture list
        self.completionHandler = {
            self.doWork()
        }
    }

    func doWork() {
        // Work simulation
    }

    deinit {
        WorkerSession.deinitCount += 1
    }
}

func runTests() {
    print("🧪 Running Lesson 1.3 Verification Tests...")
    WorkerSession.deinitCount = 0

    do {
        let session = WorkerSession(id: "sess_42")
        session.completionHandler?()
    }
    // Outside scope, session must deallocate immediately!
    assert(WorkerSession.deinitCount == 1, "Test Failed: WorkerSession leaked due to retain cycle!")
    print("  ✅ Challenge Passed: Retain cycle broken; deinit invoked successfully")
}
runTests()