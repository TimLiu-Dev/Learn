import Foundation

struct ModelSchemaField {
    let name: String
    let isUnique: Bool
    let hasDefaultOrOptional: Bool
    let isRelationship: Bool
    let isOptionalRelationship: Bool
}

struct CloudRecord: Equatable {
    let id: String
    let changeTag: String
    let title: String
    let modifiedAt: TimeInterval
}

enum CloudKitSchemaAuditor {
    // TODO: Return true iff NO field is unique, every field hasDefaultOrOptional,
    // and every relationship field has isOptionalRelationship == true
    static func isSyncCompatible(_ fields: [ModelSchemaField]) -> Bool {
        fatalError("TODO: Implement isSyncCompatible")
    }
}

enum RecordConflictMerger {
    // TODO: Merge local & server records:
    // Always preserve server.changeTag! Choose title & modifiedAt from local ONLY if local.modifiedAt > server.modifiedAt.
    static func merge(local: CloudRecord, server: CloudRecord) -> CloudRecord {
        fatalError("TODO: Implement merge")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.5 Verification Tests...")
    let validSchema = [
        ModelSchemaField(name: "title", isUnique: false, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false),
        ModelSchemaField(name: "tags", isUnique: false, hasDefaultOrOptional: true, isRelationship: true, isOptionalRelationship: true)
    ]
    let badSchema = [
        ModelSchemaField(name: "email", isUnique: true, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false)
    ]
    assert(CloudKitSchemaAuditor.isSyncCompatible(validSchema) == true, "Test 1 Failed")
    assert(CloudKitSchemaAuditor.isSyncCompatible(badSchema) == false, "Test 2 Failed")

    let localNewer = CloudRecord(id: "r1", changeTag: "tag-old", title: "Local Edit", modifiedAt: 200)
    let serverOlder = CloudRecord(id: "r1", changeTag: "tag-v5", title: "Server Edit", modifiedAt: 150)
    let merged = RecordConflictMerger.merge(local: localNewer, server: serverOlder)
    assert(merged == CloudRecord(id: "r1", changeTag: "tag-v5", title: "Local Edit", modifiedAt: 200), "Test 3 Failed")
    print("  ✅ Challenge Passed: CloudKit schema audit & conflict merge verified")
}
runTests()