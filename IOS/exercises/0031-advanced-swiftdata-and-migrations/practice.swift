import Foundation

struct SchemaVersion: Equatable, Comparable {
    let major: Int
    let minor: Int

    static func < (lhs: SchemaVersion, rhs: SchemaVersion) -> Bool {
        if lhs.major != rhs.major { return lhs.major < rhs.major }
        return lhs.minor < rhs.minor
    }
}

func resolveMigrationStage(current: SchemaVersion, target: SchemaVersion) -> String {
    // TODO: 1. If current == target return "NO_OP"
    // TODO: 2. If current == SchemaVersion(major: 1, minor: 0) && target == SchemaVersion(major: 2, minor: 0)
    //          return "MIGRATE_V1_TO_V2"
    // TODO: 3. Otherwise return "UNKNOWN_STAGE"
    fatalError("TODO: Implement resolveMigrationStage")
}

func runTests() {
    print("🧪 Running Lesson 4.4 Verification Tests...")
    let v1 = SchemaVersion(major: 1, minor: 0)
    let v2 = SchemaVersion(major: 2, minor: 0)

    let stage = resolveMigrationStage(current: v1, target: v2)
    assert(stage == "MIGRATE_V1_TO_V2", "Test 1 Failed: Expected MIGRATE_V1_TO_V2, got \(stage)")

    let noop = resolveMigrationStage(current: v2, target: v2)
    assert(noop == "NO_OP", "Test 2 Failed")
    print("  ✅ Challenge Passed: Schema migration plan resolver verified")
}
runTests()