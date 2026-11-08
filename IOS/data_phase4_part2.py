# Phase 4 Continued: Lessons 0031 - 0033

PHASE4_PART2 = [
    # 0031: Lesson 4.4
    {
        "id": "0031",
        "slug": "0031-advanced-swiftdata-and-migrations",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.4",
        "title": "Advanced SwiftData: VersionedSchema & ModelActor",
        "subtitle": "Schema evolution with VersionedSchema, custom SchemaMigrationPlan stages, and thread-safe background ingestion with ModelActor.",
        "prev_url": "0030-swiftdata-fundamentals.html",
        "next_url": "0032-core-data-enterprise-essentials.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Room), database migrations require <code>Migration(from, to)</code> objects executing manual SQL <code>ALTER TABLE</code> statements, with fallback options like <code>fallbackToDestructiveMigration()</code>.</p>
        <p>In SwiftData, schema evolution is managed via <strong><code>VersionedSchema</code></strong> and <strong><code>SchemaMigrationPlan</code></strong>. You define explicit schema version enums (<code>SchemaV1</code>, <code>SchemaV2</code>) and declare lightweight or custom migration stages. Furthermore, background data ingestion without main-thread blocking uses the <strong><code>@ModelActor</code></strong> macro.</p>
        """,
        "core_sections_html": """
        <h3>1. The VersionedSchema Architecture</h3>
        <pre><code>enum AppSchemaV1: VersionedSchema {
    static var versionIdentifier = Schema.Version(1, 0, 0)
    static var models: [any PersistentModel.Type] { [User.self] }

    @Model
    final class User {
        var id: String
        var fullName: String
    }
}

enum AppSchemaV2: VersionedSchema {
    static var versionIdentifier = Schema.Version(2, 0, 0)
    static var models: [any PersistentModel.Type] { [User.self] }

    @Model
    final class User {
        var id: String
        var firstName: String
        var lastName: String // Split name in V2
    }
}</code></pre>

        <h3>2. Background Ingestion with @ModelActor</h3>
        <pre><code>@ModelActor
actor DataIngestionService {
    func ingest(remoteRecords: [RecordDTO]) {
        for dto in remoteRecords {
            let item = Item(from: dto)
            modelContext.insert(item)
        }
        try? modelContext.save()
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Destructive Schema Migrations in Production:</strong> Deploying an app update with a changed <code>@Model</code> schema without providing a <code>VersionedSchema</code> and migration plan will crash on launch or wipe the user's entire local database! Always version production schemas.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary role of the @ModelActor macro in SwiftData?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles Swift code for watchOS devices.", "reason": "Option A is wrong because ModelActor is a persistence concurrency macro."},
                    {"text": "Isolates a ModelContext to a background actor.", "reason": "Option B is correct: @ModelActor creates an actor with its own dedicated private ModelContext for background ingestion."},
                    {"text": "Encrypts SQLite database files with AES.", "reason": "Option C is wrong because encryption is handled by filesystem Protection Classes."},
                    {"text": "Automatically logs all SQL queries to Console.", "reason": "Option D is wrong because SQL logging uses launch arguments."}
                ]
            },
            {
                "question": "Why should enterprise apps avoid destructive migrations in production?",
                "correct_idx": 0,
                "options": [
                    {"text": "Causes catastrophic offline user data loss.", "reason": "Option A is correct: destructive migrations delete the existing SQLite store file, wiping un-synced user drafts and cached history."},
                    {"text": "Increases app download size in App Store.", "reason": "Option B is wrong because schema migration rules do not increase binary size significantly."},
                    {"text": "Violates Apple Silicon CPU architecture.", "reason": "Option C is wrong because SQLite runs on all Apple platforms."},
                    {"text": "Disables SwiftUI animation spring models.", "reason": "Option D is wrong because persistence is separate from UI animations."}
                ]
            }
        ],
        "exercise": {
            "title": "Versioned Schema Migration Plan Resolver",
            "instructions": "Implement <code>resolveMigrationStage</code>. Given a source schema version and target schema version, return the stage identifier (e.g. 'MIGRATE_V1_TO_V2'). Return 'NO_OP' if versions match. Run tests.",
            "starter_code": """import Foundation

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
    assert(stage == "MIGRATE_V1_TO_V2", "Test 1 Failed: Expected MIGRATE_V1_TO_V2, got \\(stage)")

    let noop = resolveMigrationStage(current: v2, target: v2)
    assert(noop == "NO_OP", "Test 2 Failed")
    print("  ✅ Challenge Passed: Schema migration plan resolver verified")
}
runTests()""",
            "hints": [
                "if current == target { return \"NO_OP\" }",
                "if current == SchemaVersion(major: 1, minor: 0) && target == SchemaVersion(major: 2, minor: 0) { return \"MIGRATE_V1_TO_V2\" }",
                "return \"UNKNOWN_STAGE\""
            ],
            "solution_code": """import Foundation

struct SchemaVersion: Equatable, Comparable {
    let major: Int
    let minor: Int

    static func < (lhs: SchemaVersion, rhs: SchemaVersion) -> Bool {
        if lhs.major != rhs.major { return lhs.major < rhs.major }
        return lhs.minor < rhs.minor
    }
}

func resolveMigrationStage(current: SchemaVersion, target: SchemaVersion) -> String {
    if current == target { return "NO_OP" }
    if current == SchemaVersion(major: 1, minor: 0) && target == SchemaVersion(major: 2, minor: 0) {
        return "MIGRATE_V1_TO_V2"
    }
    return "UNKNOWN_STAGE"
}

func runTests() {
    print("🧪 Running Lesson 4.4 Verification Tests...")
    let v1 = SchemaVersion(major: 1, minor: 0)
    let v2 = SchemaVersion(major: 2, minor: 0)
    let stage = resolveMigrationStage(current: v1, target: v2)
    assert(stage == "MIGRATE_V1_TO_V2")
    let noop = resolveMigrationStage(current: v2, target: v2)
    assert(noop == "NO_OP")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "SchemaMigrationPlan defines a pipeline of MigrationStages (lightweight or custom) connecting VersionedSchema milestones. When opening a SQLite store, SwiftData evaluates the schema version and executes registered migration stages in order."
        },
        "primary_sources": [
            {"title": "Apple Developer: SchemaMigrationPlan Documentation", "url": "https://developer.apple.com/documentation/swiftdata/schemamigrationplan", "desc": "Official Apple reference on evolving SwiftData schemas."},
            {"title": "WWDC Session: Model your schema with SwiftData", "url": "https://developer.apple.com/videos/play/wwdc2023/10195/", "desc": "Apple engineers explain VersionedSchema and relationships."}
        ],
        "coaching_prompt": "Always wrap schema modifications inside VersionedSchema definitions once an app has shipped to real users in the App Store!"
    },

    # 0032: Lesson 4.5
    {
        "id": "0032",
        "slug": "0032-core-data-enterprise-essentials",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.5",
        "title": "Core Data Essentials for Enterprise Codebases",
        "subtitle": "Understanding legacy NSPersistentContainer, NSManagedObjectContext threading rules, NSFetchedResultsController, and Core Data migrations.",
        "prev_url": "0031-advanced-swiftdata-and-migrations.html",
        "next_url": "0033-offline-first-sync-architecture.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android, legacy projects might have used GreenDAO or raw SQLiteOpenHelper before migrating to Room.</p>
        <p>In iOS, <strong>Core Data</strong> has been the standard Apple object-relational framework for over 18 years. Most large-scale commercial codebases (Uber, Airbnb, Banking apps) still rely on Core Data. Senior iOS engineers must understand <strong><code>NSManagedObjectContext</code> concurrency rules</strong> (<code>perform</code> vs <code>performAndWait</code>), faulting, and <strong><code>NSFetchedResultsController</code></strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. The Core Data Concurrency Rule (Senior Interview Classic)</h3>
        <p>An <code>NSManagedObjectContext</code> (and any <code>NSManagedObject</code> fetched from it) is strictly bound to the queue/thread where it was created. <strong>Accessing a managed object on the wrong thread corrupts memory and causes instant crashes!</strong></p>
        <pre><code>// Safe background processing:
persistentContainer.performBackgroundTask { context in
    // context runs on a private background queue:
    let request: NSFetchRequest&lt;ItemEntity&gt; = ItemEntity.fetchRequest()
    let items = try? context.fetch(request)
    // Perform mutations...
    try? context.save()
}</code></pre>

        <h3>2. Faulting & Memory Optimization</h3>
        <p>Core Data does not load entire tables into memory. Fetched objects are initially lightweight <strong>faults</strong> (hollow shells containing only the object ID). When a property is read, Core Data 'fires the fault', fetching values from disk on-demand.</p>
        """,
        "failure_mode_html": """
        <p><strong>Passing NSManagedObjects Across Threads:</strong> Passing a managed object reference from a background context to the main thread causes silent data races and crashes. Pass only the thread-safe <code>NSManagedObjectID</code> and re-fetch the object in the destination context via <code>context.object(with: objectID)</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What is the only safe way to pass an entity reference between two different NSManagedObjectContext instances?",
                "correct_idx": 1,
                "options": [
                    {"text": "Cast the object to an unsafe pointer.", "reason": "Option A is wrong because raw pointers violate memory safety."},
                    {"text": "Pass the thread-safe NSManagedObjectID.", "reason": "Option B is correct: NSManagedObjectID is immutable and thread-safe; the target context re-fetches the instance via context.object(with: id)."},
                    {"text": "Wrap the entity in an @State variable.", "reason": "Option C is wrong because @State is a SwiftUI UI wrapper."},
                    {"text": "Dispatch to DispatchQueue.global().sync.", "reason": "Option D is wrong because sync dispatching the object directly still accesses cross-thread memory."}
                ]
            },
            {
                "question": "What does 'firing a fault' mean in Core Data?",
                "correct_idx": 0,
                "options": [
                    {"text": "Loading an object's attribute data from disk.", "reason": "Option A is correct: Core Data initially fetches hollow faults; reading a property fires the fault and retrieves attributes from SQLite."},
                    {"text": "Throwing a fatal runtime memory crash.", "reason": "Option B is wrong because faulting is an intentional memory optimization, not an error."},
                    {"text": "Deleting unreferenced rows from database.", "reason": "Option C is wrong because deletion is handled by context.delete()."},
                    {"text": "Executing an automatic schema migration plan.", "reason": "Option D is wrong because migrations occur at store load time."}
                ]
            }
        ],
        "exercise": {
            "title": "Thread-Safe Object ID Coordinator Simulator",
            "instructions": "Implement <code>resolveObject</code> in <code>MockContextCoordinator</code>. Given an <code>objectID</code>, look up the record in the thread-local store and return its title. Run tests.",
            "starter_code": """import Foundation

struct ManagedObjectID: Hashable {
    let uri: String
}

final class MockContextCoordinator {
    private var database: [ManagedObjectID: String] = [:]

    func seed(id: ManagedObjectID, title: String) {
        database[id] = title
    }

    // TODO: Implement resolveObject(with id: ManagedObjectID) -> String?
    // Return database[id]
    func resolveObject(with id: ManagedObjectID) -> String? {
        fatalError("TODO: Implement resolveObject")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.5 Verification Tests...")
    let coordinator = MockContextCoordinator()
    let id1 = ManagedObjectID(uri: "coredata://User/101")
    coordinator.seed(id: id1, title: "Lead Architect")

    let title = coordinator.resolveObject(with: id1)
    assert(title == "Lead Architect", "Test 1 Failed: Got \\(title ?? \"nil\")")

    let missing = coordinator.resolveObject(with: ManagedObjectID(uri: "coredata://User/999"))
    assert(missing == nil, "Test 2 Failed")
    print("  ✅ Challenge Passed: ManagedObjectID thread-safe resolution verified")
}
runTests()""",
            "hints": [
                "In resolveObject, return database[id]"
            ],
            "solution_code": """import Foundation

struct ManagedObjectID: Hashable {
    let uri: String
}

final class MockContextCoordinator {
    private var database: [ManagedObjectID: String] = [:]

    func seed(id: ManagedObjectID, title: String) {
        database[id] = title
    }

    func resolveObject(with id: ManagedObjectID) -> String? {
        database[id]
    }
}

func runTests() {
    print("🧪 Running Lesson 4.5 Verification Tests...")
    let coordinator = MockContextCoordinator()
    let id1 = ManagedObjectID(uri: "coredata://User/101")
    coordinator.seed(id: id1, title: "Lead Architect")

    let title = coordinator.resolveObject(with: id1)
    assert(title == "Lead Architect")

    let missing = coordinator.resolveObject(with: ManagedObjectID(uri: "coredata://User/999"))
    assert(missing == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "In Core Data, NSManagedObjectID is the universal thread-safe token. Passing the ID across threads and re-fetching in the destination context eliminates concurrency data races."
        },
        "primary_sources": [
            {"title": "Apple Developer: Core Data Concurrency", "url": "https://developer.apple.com/documentation/coredata/concurrency_with_core_data", "desc": "Official Apple guide on multi-context threading and performBackgroundTask."},
            {"title": "Apple Developer: Faulting and Uniquing", "url": "https://developer.apple.com/documentation/coredata/faulting_and_uniquing", "desc": "How Core Data minimizes memory consumption via faults."}
        ],
        "coaching_prompt": "Always remember this interview golden rule: Never pass NSManagedObject across threads. Always pass NSManagedObjectID!"
    },

    # 0033: Lesson 4.6
    {
        "id": "0033",
        "slug": "0033-offline-first-sync-architecture",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.6",
        "title": "Offline-First Sync Architecture: Optimistic UI & Conflict Resolution",
        "subtitle": "Local persistence as single source of truth, optimistic UI state updates, Vector Clocks / Last-Write-Wins (LWW), and sync queues.",
        "prev_url": "0032-core-data-enterprise-essentials.html",
        "next_url": "../lessons/0034-enterprise-clean-architecture-and-udf.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android, offline-first architectures use Room as the single source of truth: network requests write directly to Room, and the UI observes Room Flow streams.</p>
        <p>In iOS, <strong>Offline-First Architecture</strong> follows the same principle: SwiftUI views observe the local store (<code>@Query</code> or SwiftData). When the user performs an action (e.g. liking a post or sending a message), the app commits an <strong>optimistic update</strong> to the local SQLite database immediately. A background sync queue uploads changes to the server, resolving conflicts via <strong>Vector Clocks</strong> or <strong>Last-Write-Wins (LWW)</strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. The Offline-First Flow</h3>
        <ol>
          <li><strong>User action:</strong> User creates or mutates a record.</li>
          <li><strong>Local commit:</strong> App writes mutation to local database with status <code>.pendingSync</code>. UI updates instantly (0ms latency!).</li>
          <li><strong>Background sync:</strong> When network is available, SyncWorker uploads pending mutations.</li>
          <li><strong>Server ack:</strong> Server confirms; local status updates to <code>.synced</code>. If server rejects, app rolls back or prompts user.</li>
        </ol>

        <h3>2. Last-Write-Wins (LWW) Conflict Resolution</h3>
        <pre><code>struct SyncRecord {
    let id: String
    var content: String
    var timestampEpoch: Int64

    func resolveConflict(with serverRecord: SyncRecord) -> SyncRecord {
        // Last-Write-Wins: newer timestamp wins
        if self.timestampEpoch >= serverRecord.timestampEpoch {
            return self
        } else {
            return serverRecord
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Waiting for Network Before Updating Local UI:</strong> Forcing a spinning indicator for 2 seconds while waiting for network responses ruins the app experience on poor connections. Always mutate local storage optimistically and sync in the background.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary benefit of an Optimistic UI update in mobile architecture?",
                "correct_idx": 0,
                "options": [
                    {"text": "Eliminates perceived user interface latency.", "reason": "Option A is correct: updating local storage immediately makes interactions feel instantaneous (0ms), syncing changes in the background."},
                    {"text": "Bypasses backend authorization checks.", "reason": "Option B is wrong because the server still validates permissions when receiving the sync."},
                    {"text": "Disables device battery consumption.", "reason": "Option C is wrong because optimistic UI does not alter hardware battery circuits."},
                    {"text": "Prevents any merge conflicts on server.", "reason": "Option D is wrong because conflicts can still occur and require resolution rules."}
                ]
            },
            {
                "question": "In Last-Write-Wins (LWW) conflict resolution, which mutation is accepted?",
                "correct_idx": 2,
                "options": [
                    {"text": "The mutation with the shortest string length.", "reason": "Option A is wrong because string length is irrelevant to conflict order."},
                    {"text": "The mutation originating from Android devices.", "reason": "Option B is wrong because device platform does not govern conflict resolution."},
                    {"text": "The mutation with the latest timestamp.", "reason": "Option C is correct: LWW compares timestamps and accepts the most recent mutation."},
                    {"text": "All mutations are rejected and discarded.", "reason": "Option D is wrong because LWW resolves deterministically."}
                ]
            }
        ],
        "exercise": {
            "title": "Last-Write-Wins Conflict Resolution Engine",
            "instructions": "Implement <code>resolveLWW</code>. Given a local record and server record with identical IDs, return the record with the higher timestamp. If timestamps are equal, favor the server record. Run tests.",
            "starter_code": """import Foundation

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
runTests()""",
            "hints": [
                "if local.updatedAt > server.updatedAt { return local } else { return server }"
            ],
            "solution_code": """import Foundation

struct NoteRecord: Equatable {
    let id: String
    let text: String
    let updatedAt: Int64
}

func resolveLWW(local: NoteRecord, server: NoteRecord) -> NoteRecord {
    if local.updatedAt > server.updatedAt {
        return local
    } else {
        return server
    }
}

func runTests() {
    print("🧪 Running Lesson 4.6 Verification Tests...")
    let localNewer = NoteRecord(id: "n1", text: "Local edit", updatedAt: 200)
    let serverOlder = NoteRecord(id: "n1", text: "Server edit", updatedAt: 100)
    let r1 = resolveLWW(local: localNewer, server: serverOlder)
    assert(r1 == localNewer)

    let localOlder = NoteRecord(id: "n2", text: "Local old", updatedAt: 100)
    let serverNewer = NoteRecord(id: "n2", text: "Server new", updatedAt: 300)
    let r2 = resolveLWW(local: localOlder, server: serverNewer)
    assert(r2 == serverNewer)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Last-Write-Wins (LWW) is a simple, deterministic conflict resolution strategy for offline data synchronization. When reconnecting to the network, comparing timestamps allows automated convergence with zero user interruption."
        },
        "primary_sources": [
            {"title": "Point-Free: Modern Swift Architecture & Caching", "url": "https://www.pointfree.co", "desc": "Design principles for decoupled caching and offline mutation queues."},
            {"title": "Apple Developer: Synchronizing Data with CloudKit", "url": "https://developer.apple.com/documentation/cloudkit", "desc": "Apple's first-party cloud synchronization and conflict resolution guide."}
        ],
        "coaching_prompt": "Congratulations! You have finished Phase 4: Local Persistence, Caching & Offline-First, covering Keychain security, CryptoKit, SwiftData, schema migrations, Core Data concurrency, and offline sync engines. Next up: Phase 5 Modular Enterprise Architecture!"
    }
]

print("Phase 4 complete data loaded.")
