import Foundation

struct NoteRecord: Equatable {
    let id: String
    let text: String
    let updatedAt: Int64
}

func resolveLWW(local: NoteRecord, server: NoteRecord) -> NoteRecord {
    // TODO: Compare local.updatedAt and server.updatedAt
    // If local.updatedAt > server.updatedAt return local
    // Otherwise return server
    fatalError("TODO: Implement resolveLWW")
}

func runTests() {
    print("🧪 Running Lesson 4.6 Verification Tests...")
    let localNewer = NoteRecord(id: "n1", text: "Local edit", updatedAt: 200)
    let serverOlder = NoteRecord(id: "n1", text: "Server edit", updatedAt: 100)
    let r1 = resolveLWW(local: localNewer, server: serverOlder)
    assert(r1 == localNewer, "Test 1 Failed: Local newer should win")

    let localOlder = NoteRecord(id: "n2", text: "Local old", updatedAt: 100)
    let serverNewer = NoteRecord(id: "n2", text: "Server new", updatedAt: 300)
    let r2 = resolveLWW(local: localOlder, server: serverNewer)
    assert(r2 == serverNewer, "Test 2 Failed: Server newer should win")
    print("  ✅ Challenge Passed: Last-Write-Wins conflict resolution verified")
}
runTests()