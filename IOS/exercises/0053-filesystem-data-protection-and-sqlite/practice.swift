import Foundation

enum StorageKind {
    case userDocument
    case database
    case ephemeralCache
}

enum SandboxRouter {
    // TODO: Return base/<Directory>/<filename>
    // .userDocument -> "Documents", .database -> "ApplicationSupport", .ephemeralCache -> "Caches"
    static func destinationURL(base: URL, kind: StorageKind, filename: String) -> URL {
        fatalError("TODO: Implement destinationURL")
    }

    // TODO: Write data atomically with .completeFileProtectionUnlessOpen after creating parent directories
    static func writeProtected(_ data: Data, to url: URL) throws {
        fatalError("TODO: Implement writeProtected")
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.5 Verification Tests...")
    let tempBase = FileManager.default.temporaryDirectory.appendingPathComponent("pulse_sandbox_\(UUID().uuidString)")
    defer { try? FileManager.default.removeItem(at: tempBase) }

    let dbURL = SandboxRouter.destinationURL(base: tempBase, kind: .database, filename: "sync.sqlite")
    assert(dbURL.path.contains("ApplicationSupport/sync.sqlite"), "Test 1 Failed: Wrong database path")

    let docURL = SandboxRouter.destinationURL(base: tempBase, kind: .userDocument, filename: "export.pdf")
    assert(docURL.path.contains("Documents/export.pdf"), "Test 2 Failed: Wrong documents path")

    let payload = Data("WAL_HEADER_V1".utf8)
    try SandboxRouter.writeProtected(payload, to: dbURL)
    let readBack = try Data(contentsOf: dbURL)
    assert(readBack == payload, "Test 3 Failed: Written data mismatch")
    print("  ✅ Challenge Passed: Sandbox routing and atomic protected write verified")
}
try runTests()