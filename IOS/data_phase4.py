# Phase 4: Local Persistence, Caching & Offline-First
# Lessons 0028 - 0033

PHASE4_LESSONS = [
    # 0028: Lesson 4.1
    {
        "id": "0028",
        "slug": "0028-lightweight-storage-and-keychain",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.1",
        "title": "Lightweight Storage & Apple Keychain Services",
        "subtitle": "UserDefaults (@AppStorage) for UI preferences vs Apple Keychain Services (SecItem) for biometric and hardware-encrypted tokens.",
        "prev_url": "0027-uikit-interoperability.html",
        "next_url": "0029-app-security-and-cryptokit.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android, simple preferences use <code>SharedPreferences</code> or Jetpack <code>DataStore</code>, while secure secrets rely on the Android Keystore system (<code>EncryptedSharedPreferences</code>).</p>
        <p>In iOS, <code>UserDefaults</code> (and SwiftUI <code>@AppStorage</code>) stores unencrypted plist data—<strong>never store auth tokens or passwords in UserDefaults</strong>! Sensitive secrets must be stored in <strong>Apple Keychain Services</strong> (<code>SecItemAdd</code>, <code>SecItemCopyMatching</code>), which uses AES-256 hardware encryption managed by Darwin's <code>securityd</code> daemon.</p>
        """,
        "core_sections_html": """
        <h3>1. UserDefaults vs Keychain Boundaries</h3>
        <ul>
          <li><strong>UserDefaults (@AppStorage):</strong> Unencrypted. Backed by a plist file in the sandbox. Survives app updates; deleted on app uninstall. Use only for UI state (e.g. <code>hasSeenOnboarding</code>, <code>preferredTheme</code>).</li>
          <li><strong>Keychain Services:</strong> Encrypted with hardware keys. Backed by SQLite database in <code>securityd</code> outside the app sandbox. <em>Persists even after the app is uninstalled!</em> Use for JWT bearer tokens, refresh tokens, and passwords.</li>
        </ul>

        <h3>2. Idiomatic Keychain CRUD Operations</h3>
        <pre><code>import Security

func saveTokenToKeychain(account: String, tokenData: Data) -> OSStatus {
    let query: [String: Any] = [
        kSecClass as String: kSecClassGenericPassword,
        kSecAttrAccount as String: account,
        kSecValueData as String: tokenData,
        kSecAttrAccessible as String: kSecAttrAccessibleAfterFirstUnlock
    ]
    SecItemDelete(query as CFDictionary) // Clean old item
    return SecItemAdd(query as CFDictionary, nil)
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Storing Auth Tokens in UserDefaults:</strong> Plain-text plists inside UserDefaults are easily extracted from unencrypted device backups or jailbroken devices using basic file inspection tools. Always route sensitive credentials through Keychain Services.</p>
        """,
        "quizzes": [
            {
                "question": "What happens to credentials stored in Apple Keychain Services when a user uninstalls the app?",
                "correct_idx": 1,
                "options": [
                    {"text": "They are deleted immediately on uninstall.", "reason": "Option A is wrong: unlike UserDefaults, Keychain items persist across app uninstalls on iOS."},
                    {"text": "They persist securely in the system keychain.", "reason": "Option B is correct: Keychain items survive uninstalls, allowing automatic re-login if reinstalled unless explicitly deleted."},
                    {"text": "The Darwin kernel logs a security violation.", "reason": "Option C is wrong because Keychain persistence is standard iOS behavior."},
                    {"text": "They are emailed to the registered developer.", "reason": "Option D is wrong because credentials remain securely encrypted on-device."}
                ]
            },
            {
                "question": "Which accessibility attribute ensures keychain items are accessible only while device is unlocked?",
                "correct_idx": 0,
                "options": [
                    {"text": "kSecAttrAccessibleAfterFirstUnlock", "reason": "Option A is correct: it protects data until the user unlocks the device for the first time after boot."},
                    {"text": "kSecAttrAccessibleAlwaysThisDeviceOnly", "reason": "Option B is deprecated because accessible always leaves keys exposed before initial unlock."},
                    {"text": "kSecAttrAccessibleNeverDecrypted", "reason": "Option C is wrong because that is not a valid Apple security attribute."},
                    {"text": "kSecAttrAccessiblePublicCloudOnly", "reason": "Option D is wrong because local keychain items are not public cloud items."}
                ]
            }
        ],
        "exercise": {
            "title": "Secure In-Memory Keychain Vault Simulator",
            "instructions": "Implement <code>saveSecret</code> and <code>retrieveSecret</code> in <code>MockKeychainVault</code>. Overwrite existing entries on save. Return nil if account not found. Run tests.",
            "starter_code": """import Foundation

final class MockKeychainVault {
    private var vault: [String: Data] = [:]

    // TODO: Implement saveSecret(account: String, secret: String) -> Bool
    // Convert secret string to utf8 Data and store in vault[account]. Return true.
    func saveSecret(account: String, secret: String) -> Bool {
        fatalError("TODO: Implement saveSecret")
    }

    // TODO: Implement retrieveSecret(account: String) -> String?
    // Retrieve Data from vault[account], decode as UTF-8 string. Return nil if not found.
    func retrieveSecret(account: String) -> String? {
        fatalError("TODO: Implement retrieveSecret")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.1 Verification Tests...")
    let keychain = MockKeychainVault()

    let saved = keychain.saveSecret(account: "auth_token", secret: "jwt_secret_xyz")
    assert(saved == true, "Test 1 Failed")

    let retrieved = keychain.retrieveSecret(account: "auth_token")
    assert(retrieved == "jwt_secret_xyz", "Test 2 Failed: Secret mismatch")

    let missing = keychain.retrieveSecret(account: "unknown")
    assert(missing == nil, "Test 3 Failed: Missing secret should return nil")
    print("  ✅ Challenge Passed: Keychain storage mechanics verified")
}
runTests()""",
            "hints": [
                "In saveSecret: guard let data = secret.data(using: .utf8) else { return false }; vault[account] = data; return true",
                "In retrieveSecret: guard let data = vault[account] else { return nil }; return String(data: data, encoding: .utf8)"
            ],
            "solution_code": """import Foundation

final class MockKeychainVault {
    private var vault: [String: Data] = [:]

    func saveSecret(account: String, secret: String) -> Bool {
        guard let data = secret.data(using: .utf8) else { return false }
        vault[account] = data
        return true
    }

    func retrieveSecret(account: String) -> String? {
        guard let data = vault[account] else { return nil }
        return String(data: data, encoding: .utf8)
    }
}

func runTests() {
    print("🧪 Running Lesson 4.1 Verification Tests...")
    let keychain = MockKeychainVault()
    let saved = keychain.saveSecret(account: "auth_token", secret: "jwt_secret_xyz")
    assert(saved == true)

    let retrieved = keychain.retrieveSecret(account: "auth_token")
    assert(retrieved == "jwt_secret_xyz")

    let missing = keychain.retrieveSecret(account: "unknown")
    assert(missing == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Keychain storage operates on raw Data buffers. Converting secrets to utf8 Data and scoping access to specific account identifier keys mirrors Apple's SecItemAdd and SecItemCopyMatching APIs."
        },
        "primary_sources": [
            {"title": "Apple Developer: Keychain Services", "url": "https://developer.apple.com/documentation/security/keychain_services", "desc": "Official Apple guide on secure storage and item accessibility."},
            {"title": "Apple Developer: Searching for Keychain Items", "url": "https://developer.apple.com/documentation/security/keychain_services/keychain_items/searching_for_keychain_items", "desc": "SecItem query attributes and search dictionaries."}
        ],
        "coaching_prompt": "Remember: UserDefaults is for user settings (e.g. notificationsOn=true). Keychain is for identity secrets (e.g. oauth_token)!"
    },

    # 0029: Lesson 4.2
    {
        "id": "0029",
        "slug": "0029-app-security-and-cryptokit",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.2",
        "title": "App Security: CryptoKit & SSL Certificate Pinning",
        "subtitle": "Apple CryptoKit (AES-GCM, SHA-256), Secure Enclave, URLSessionDelegate TLS pinning, and App Attest defenses.",
        "prev_url": "0028-lightweight-storage-and-keychain.html",
        "next_url": "0030-swiftdata-fundamentals.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android, certificate pinning is configured via <code>NetworkSecurityConfig.xml</code> or OkHttp <code>CertificatePinner</code>.</p>
        <p>In iOS, <strong>Apple CryptoKit</strong> provides modern, hardware-accelerated cryptographic primitives (AES-GCM, Curve25519, SHA-256). Certificate and public key pinning is implemented via <code>URLSessionDelegate</code> (<code>didReceive challenge:</code>), inspecting the server's X.509 certificate trust chain to prevent Man-in-the-Middle (MITM) attacks.</p>
        """,
        "core_sections_html": """
        <h3>1. Modern Encryption with CryptoKit (AES-GCM)</h3>
        <pre><code>import CryptoKit

func encryptPayload(plainData: Data, symmetricKey: SymmetricKey) throws -> Data {
    let sealedBox = try AES.GCM.seal(plainData, using: symmetricKey)
    return sealedBox.combined ?? Data()
}

func decryptPayload(combinedData: Data, symmetricKey: SymmetricKey) throws -> Data {
    let sealedBox = try AES.GCM.SealedBox(combined: combinedData)
    return try AES.GCM.open(sealedBox, using: symmetricKey)
}</code></pre>

        <h3>2. TLS Public Key Pinning (URLSessionDelegate)</h3>
        <p>In <code>urlSession(_:didReceive:completionHandler:)</code>, validate the server's public key hash against your pinned SPKI (Subject Public Key Info) SHA-256 hash. If they do not match, abort connection with <code>.cancelAuthenticationChallenge</code>.</p>
        """,
        "failure_mode_html": """
        <p><strong>Pinning Expiring Root Certificates without Backup Keys:</strong> If your server certificates rotate and you pinned a leaf certificate with no backup pin, the app will fail all network requests until an emergency App Store update is approved. Always pin the intermediate CA or include backup public key pins.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary security advantage of Public Key (SPKI) pinning over leaf certificate pinning?",
                "correct_idx": 1,
                "options": [
                    {"text": "Bypasses all Darwin kernel sandbox restrictions.", "reason": "Option A is wrong because pinning does not alter OS process isolation."},
                    {"text": "Survives certificate renewals if key is unchanged.", "reason": "Option B is correct: the public key remains valid even when the certificate is re-signed upon annual expiration."},
                    {"text": "Eliminates all needs for HTTPS transport.", "reason": "Option C is wrong because pinning is an authentication check on top of HTTPS."},
                    {"text": "Runs network decryption on background threads.", "reason": "Option D is wrong because TLS negotiation is handled automatically by the OS."}
                ]
            },
            {
                "question": "Which Apple framework provides hardware-accelerated AES-GCM and SHA-256 encryption in modern Swift?",
                "correct_idx": 0,
                "options": [
                    {"text": "CryptoKit framework.", "reason": "Option A is correct: CryptoKit is Apple's high-level, safe, hardware-accelerated cryptography framework introduced in iOS 13."},
                    {"text": "CoreData framework.", "reason": "Option B is wrong because Core Data is an object graph persistence framework."},
                    {"text": "Combine framework.", "reason": "Option C is wrong because Combine is a reactive streams framework."},
                    {"text": "SwiftTesting framework.", "reason": "Option D is wrong because Swift Testing is for unit tests."}
                ]
            }
        ],
        "exercise": {
            "title": "SHA-256 Payload Integrity Verifier",
            "instructions": "Implement <code>verifyPayloadHash</code>. Compute the SHA-256 hash of <code>data</code> using <code>CryptoKit.SHA256.hash(data:)</code> and compare its hex representation to <code>expectedHexHash</code>. Run tests.",
            "starter_code": """import Foundation
import CryptoKit

func verifyPayloadHash(data: Data, expectedHexHash: String) -> Bool {
    // TODO: 1. Compute digest = SHA256.hash(data: data)
    // TODO: 2. Convert digest to hex string: digest.map { String(format: "%02x", $0) }.joined()
    // TODO: 3. Return computedHex == expectedHexHash.lowercased()
    fatalError("TODO: Implement verifyPayloadHash")
}

func runTests() {
    print("🧪 Running Lesson 4.2 Verification Tests...")
    let sampleData = "Senior iOS Developer".data(using: .utf8)!
    // SHA256 of "Senior iOS Developer"
    let knownHash = "d1558b35e6abd18d9556727f15ebe319dcccccb597eb905f3ebb26154c546e82"

    let isValid = verifyPayloadHash(data: sampleData, expectedHexHash: knownHash)
    assert(isValid == true, "Test 1 Failed: Hash mismatch")

    let isInvalid = verifyPayloadHash(data: sampleData, expectedHexHash: "bad_hash_123")
    assert(isInvalid == false, "Test 2 Failed")
    print("  ✅ Challenge Passed: CryptoKit SHA-256 payload verification verified")
}
runTests()""",
            "hints": [
                "let digest = SHA256.hash(data: data)",
                "let hex = digest.map { String(format: \"%02x\", $0) }.joined()",
                "return hex == expectedHexHash.lowercased()"
            ],
            "solution_code": """import Foundation
import CryptoKit

func verifyPayloadHash(data: Data, expectedHexHash: String) -> Bool {
    let digest = SHA256.hash(data: data)
    let hex = digest.map { String(format: "%02x", $0) }.joined()
    return hex == expectedHexHash.lowercased()
}

func runTests() {
    print("🧪 Running Lesson 4.2 Verification Tests...")
    let sampleData = "Senior iOS Developer".data(using: .utf8)!
    let knownHash = "d1558b35e6abd18d9556727f15ebe319dcccccb597eb905f3ebb26154c546e82"
    let isValid = verifyPayloadHash(data: sampleData, expectedHexHash: knownHash)
    assert(isValid == true)

    let isInvalid = verifyPayloadHash(data: sampleData, expectedHexHash: "bad_hash_123")
    assert(isInvalid == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "CryptoKit executes hashing using native Apple Silicon hardware acceleration. SHA-256 digests provide collision-resistant verification, essential for cache integrity and security handshake validation."
        },
        "primary_sources": [
            {"title": "Apple Developer: CryptoKit", "url": "https://developer.apple.com/documentation/cryptokit", "desc": "Official Apple documentation on CryptoKit and secure key management."},
            {"title": "Apple Developer: Identity and Trust", "url": "https://developer.apple.com/documentation/security/certificate_key_and_trust_services", "desc": "Official guide on TLS authentication challenges and trust evaluation."}
        ],
        "coaching_prompt": "In financial and enterprise apps, pairing CryptoKit encryption with Keychain-stored keys ensures user data remains secure even on compromised filesystems."
    },

    # 0030: Lesson 4.3
    {
        "id": "0030",
        "slug": "0030-swiftdata-fundamentals",
        "phase_num": 4,
        "phase_title": "Phase 4: Local Persistence, Caching & Offline-First",
        "lesson_num": "Lesson 4.3",
        "title": "SwiftData Fundamentals: Schema, Container & Context",
        "subtitle": "@Model macros, ModelContainer, ModelContext, @Query in SwiftUI, and CRUD operations on local SQLite stores.",
        "prev_url": "0029-app-security-and-cryptokit.html",
        "next_url": "0031-advanced-swiftdata-and-migrations.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence Cheatsheet",
        "android_anchor_html": """
        <p>In Android, local SQLite persistence is managed via Room ORM (<code>@Entity</code>, <code>@Dao</code>, <code>@Database</code>).</p>
        <p>In iOS, <strong>SwiftData</strong> (iOS 17+) is Apple's modern, macro-powered persistence framework replacing legacy Core Data. Models are declared with <strong><code>@Model</code></strong> on standard Swift classes. CRUD operations execute via <strong><code>ModelContext</code></strong>, and SwiftUI views query data reactively using <strong><code>@Query</code></strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. The SwiftData Architecture</h3>
        <ul>
          <li><strong><code>@Model</code>:</strong> Macro that transforms a Swift class into a managed persistence schema.</li>
          <li><strong><code>ModelContainer</code>:</strong> Manages the schema and physical SQLite database storage.</li>
          <li><strong><code>ModelContext</code>:</strong> Scratchpad tracking inserts, updates, and deletes before committing to disk.</li>
          <li><strong><code>@Query</code>:</strong> SwiftUI property wrapper that automatically fetches and animates records.</li>
        </ul>

        <h3>2. Basic Model Definition</h3>
        <pre><code>import SwiftData

@Model
final class DeviceReading {
    var id: String
    var timestamp: Date
    var metricValue: Double

    init(id: String, timestamp: Date = .now, metricValue: Double) {
        self.id = id
        self.timestamp = timestamp
        self.metricValue = metricValue
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Accessing ModelContext Across Concurrency Domains:</strong> ModelContext is NOT thread-safe. A ModelContext created on the main thread must only be touched on the main thread. Background insertions require <code>ModelActor</code> or a dedicated background context.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary role of ModelContext in SwiftData?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles Swift classes into C headers.", "reason": "Option A is wrong because ModelContext is runtime persistence."},
                    {"text": "Acts as the scratchpad tracking changes.", "reason": "Option B is correct: ModelContext tracks inserted, modified, and deleted models before persisting to disk."},
                    {"text": "Disables dirty memory limits in Darwin.", "reason": "Option C is wrong because Jetsam memory limits are OS-enforced."},
                    {"text": "Handles push notification tokens.", "reason": "Option D is wrong because notifications are managed by APNs."}
                ]
            },
            {
                "question": "Why does SwiftData require model classes to be reference types (classes) rather than structs?",
                "correct_idx": 0,
                "options": [
                    {"text": "To maintain persistent object graph identity.", "reason": "Option A is correct: reference identity allows the context to track relationships, faults, and in-place property mutations across the graph."},
                    {"text": "Because structs cannot store String types.", "reason": "Option B is wrong because structs store Strings freely."},
                    {"text": "To bypass the Swift 6 Sendable compiler.", "reason": "Option C is wrong because persistence schemas must adhere to concurrency rules."},
                    {"text": "To enforce static witness table dispatch.", "reason": "Option D is wrong because managed models use dynamic property getters/setters."}
                ]
            }
        ],
        "exercise": {
            "title": "ModelContext In-Memory Persistence Engine",
            "instructions": "Implement <code>insert</code> and <code>fetch</code> in <code>MockModelContext</code>. Track inserted models by their <code>id</code>. Return all matching models. Run tests.",
            "starter_code": """import Foundation

struct ManagedItem: Equatable {
    let id: String
    var title: String
}

final class MockModelContext {
    private var storage: [String: ManagedItem] = [:]

    // TODO: Implement insert(_ item: ManagedItem) -> store in storage[item.id]
    func insert(_ item: ManagedItem) {
        fatalError("TODO: Implement insert")
    }

    // TODO: Implement fetchAll() -> [ManagedItem]
    func fetchAll() -> [ManagedItem] {
        fatalError("TODO: Implement fetchAll")
    }
}

func runTests() {
    print("🧪 Running Lesson 4.3 Verification Tests...")
    let context = MockModelContext()
    context.insert(ManagedItem(id: "item_1", title: "Metrics"))
    context.insert(ManagedItem(id: "item_2", title: "Logs"))

    let items = context.fetchAll()
    assert(items.count == 2, "Test 1 Failed: Expected 2 items, got \\(items.count)")
    assert(items.contains(ManagedItem(id: "item_1", title: "Metrics")), "Test 2 Failed")
    print("  ✅ Challenge Passed: In-memory ModelContext CRUD verified")
}
runTests()""",
            "hints": [
                "In insert: storage[item.id] = item",
                "In fetchAll: return Array(storage.values)"
            ],
            "solution_code": """import Foundation

struct ManagedItem: Equatable {
    let id: String
    var title: String
}

final class MockModelContext {
    private var storage: [String: ManagedItem] = [:]

    func insert(_ item: ManagedItem) {
        storage[item.id] = item
    }

    func fetchAll() -> [ManagedItem] {
        Array(storage.values)
    }
}

func runTests() {
    print("🧪 Running Lesson 4.3 Verification Tests...")
    let context = MockModelContext()
    context.insert(ManagedItem(id: "item_1", title: "Metrics"))
    context.insert(ManagedItem(id: "item_2", title: "Logs"))

    let items = context.fetchAll()
    assert(items.count == 2)
    assert(items.contains(ManagedItem(id: "item_1", title: "Metrics")))
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "ModelContext tracks object mutations in memory before flushing transactions to SQLite. Using persistent identifiers ensures relationships and object graphs remain deterministic across saves."
        },
        "primary_sources": [
            {"title": "Apple Developer: Meet SwiftData", "url": "https://developer.apple.com/videos/play/wwdc2023/10187/", "desc": "WWDC introduction to @Model, ModelContainer, and ModelContext."},
            {"title": "Apple Developer: SwiftData Documentation", "url": "https://developer.apple.com/documentation/swiftdata", "desc": "Official Apple framework reference for SwiftData."}
        ],
        "coaching_prompt": "Notice how SwiftData eliminates the verbose .xcdatamodeld XML files from legacy Core Data. Models are defined directly in Swift code using the @Model macro!"
    }
]

print("Phase 4 partial loaded (Lessons 4.1 - 4.3).")
