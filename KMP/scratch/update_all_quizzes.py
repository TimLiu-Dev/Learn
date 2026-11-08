import os
import sys

sys.path.append(os.path.dirname(__file__))

def update_all_quizzes_complete():
    import gen_phase0_to_3
    import gen_phase1_to_3_remaining
    import gen_phase4_to_6
    import gen_phase7_to_9
    from generator_base import generate_lesson_file

    # ============================================================
    # LESSON 02: Kotlin 2.x & K2 Compiler Pipeline
    # ============================================================
    l2 = gen_phase0_to_3.lessons[0]
    l2['q1'] = {
        "question": "Review the build script below. In a modern Kotlin 2.1 project, what error will occur with this Compose compiler setup?",
        "code_snippet": """// shared/build.gradle.kts (Kotlin 2.1)
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("androidx.compose.compiler") version "1.5.8" // Legacy plugin
}""",
        "options": [
            "The build fails because Kotlin 2.0+ bundles the Compose compiler directly into the Kotlin repo under org.jetbrains.kotlin.plugin.compose.",
            "The Gradle daemon crashes because AndroidX plugins cannot be combined with Kotlin Multiplatform version catalogs.",
            "The build succeeds, but Compose UI buttons will fail to respond to touch gestures on Android 14+ devices.",
            "The K2 compiler automatically converts the AndroidX compiler into an LLVM static archive at compile time."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Starting in Kotlin 2.0, JetBrains integrated the Compose compiler directly into the Kotlin compiler repository. The legacy androidx.compose.compiler plugin is incompatible with Kotlin 2.x; you must apply the official Kotlin Compose compiler plugin.",
            "whyWrong": [
                {"letter": "B", "reason": "AndroidX plugins work fine with version catalogs; the issue is that this specific compiler plugin coordinate was retired after Kotlin 1.9."},
                {"letter": "C", "reason": "The failure happens at build time during plugin application, before runtime execution."},
                {"letter": "D", "reason": "K2 does not convert Gradle plugins into LLVM archives."}
            ]
        }
    }
    l2['hints'] = [
        "In the kotlin block, access compilerOptions: <code>kotlin { compilerOptions { ... } }</code>.",
        "Set languageVersion: <code>languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)</code>.",
        "Enable warnings as errors: <code>allWarningsAsErrors.set(true)</code>."
    ]

    # ============================================================
    # LESSON 03: Apple Toolchain & SPM
    # ============================================================
    l3 = gen_phase0_to_3.lessons[1]
    l3['q1'] = {
        "question": "In the Package.swift manifest below, why does modern KMP packaging use .binaryTarget with an XCFramework rather than legacy fat universal frameworks?",
        "code_snippet": """// Package.swift
let package = Package(
    name: "SharedKit",
    platforms: [.iOS(.v16)],
    products: [.library(name: "SharedKit", targets: ["SharedKit"])],
    targets: [
        .binaryTarget(name: "SharedKit", path: "./SharedKit.xcframework")
    ]
)""",
        "options": [
            "XCFrameworks allow bundling distinct arm64 slices for both physical devices and Apple Silicon simulators without slice collision.",
            "Binary targets in SPM bypass Xcode code signing and provisioning profiles entirely for App Store uploads.",
            "SPM binary targets convert compiled Kotlin machine code back into pure Swift source files on the fly.",
            "Legacy fat universal frameworks only support 32-bit iOS architectures and cannot run on 64-bit devices."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Legacy universal fat frameworks could not contain two slices with the same architecture name (arm64 for device vs arm64 for simulator). XCFrameworks solved this by organizing slices into separate platform directories.",
            "whyWrong": [
                {"letter": "B", "reason": "All binaries bundled in iOS apps must still be signed with valid Apple developer certificates."},
                {"letter": "C", "reason": "XCFrameworks contain compiled Mach-O machine binaries, not generated Swift source code."},
                {"letter": "D", "reason": "Fat frameworks supported 64-bit; their limitation was architecture collision on modern Apple Silicon Macs."}
            ]
        }
    }
    l3['hints'] = [
        "Instantiate the XCFramework task: <code>val xcf = XCFramework(\"SharedKit\")</code>.",
        "Iterate over iOS targets: <code>listOf(iosArm64(), iosSimulatorArm64()).forEach { it.binaries.framework { xcf.add(this) } }</code>.",
        "Remember that xcframework aggregates both device (arm64) and simulator slices into one bundle."
    ]

    # ============================================================
    # LESSON 04: Multiplatform Architecture Mindset
    # ============================================================
    l4 = gen_phase0_to_3.lessons[2]
    l4['q1'] = {
        "question": "Review the commonMain repository declaration below. Why does importing android.content.Context violate clean multiplatform architecture?",
        "code_snippet": """// commonMain/src/domain/IncidentRepository.kt
package com.pulsesync.domain

import android.content.Context // Architecture smell!

class IncidentRepository(private val context: Context) {
    fun getLogPath(): String = context.filesDir.absolutePath
}""",
        "options": [
            "commonMain compiles for both Android and iOS targets; importing Android SDK classes causes iOS compilation to fail immediately.",
            "Android Context can only be passed into Composable functions, not standard Kotlin repository classes.",
            "The K2 compiler disallows any class constructor that accepts parameters in the domain layer.",
            "Importing Context forces the entire application to execute synchronously on the background I/O thread."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "commonMain has no dependency on the Android SDK. Referencing Android classes like Context means the iOS compiler (Kotlin/Native) has no symbol resolution and halts with an unresolved reference error.",
            "whyWrong": [
                {"letter": "B", "reason": "Context can be used in Android classes; the issue is placing it in shared commonMain code."},
                {"letter": "C", "reason": "Constructors in the domain layer can accept parameters as long as they are multiplatform-compatible."},
                {"letter": "D", "reason": "Imports have zero effect on thread scheduling or synchronous vs asynchronous dispatch."}
            ]
        }
    }
    l4['hints'] = [
        "Declare enum class Severity with constants LOW, HIGH.",
        "Declare interface IncidentRepository with suspend fun getActiveIncidents(): List<String>.",
        "Ensure no platform SDKs (Android Context or iOS Foundation) are imported in commonMain."
    ]

    # ============================================================
    # LESSON 05: expect/actual Mechanisms & Anti-Patterns
    # ============================================================
    l5 = gen_phase0_to_3.lessons[3]
    l5['q1'] = {
        "question": "In the snippet below, an expect class is used for battery management. Why does modern KMP architecture recommend interfaces over expect class?",
        "code_snippet": """// commonMain
expect class DeviceBatteryManager() {
    fun getBatteryLevel(): Int
}

// androidMain
actual class DeviceBatteryManager {
    actual fun getBatteryLevel(): Int = 85
}""",
        "options": [
            "Interfaces decoupled with Dependency Injection can be easily substituted with test fakes in unit tests without platform mocking.",
            "expect classes are forbidden by the Kotlin compiler and will be removed in Kotlin 2.2.",
            "Classes declared with expect cannot contain member functions or properties on iOS targets.",
            "Interfaces automatically transpile Kotlin business logic directly into Apple Objective-C headers."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Using interfaces + DI allows you to swap in FakeBatteryManager in commonTest. Overusing expect/actual tightly couples shared code to platform implementations and makes pure unit testing very difficult.",
            "whyWrong": [
                {"letter": "B", "reason": "expect classes are not forbidden; they are simply considered in maintenance mode for classes while still valid for top-level functions."},
                {"letter": "C", "reason": "expect classes can contain members on all targets."},
                {"letter": "D", "reason": "Interfaces do not transpile code into headers; headers are generated by the Kotlin/Native framework compiler."}
            ]
        }
    }
    l5['hints'] = [
        "Use the expect keyword: <code>expect fun getCurrentTimeMillis(): Long</code>.",
        "Remember: expected functions declare signatures without a body in commonMain.",
        "In actual implementations, the platform supplies the concrete body (System.currentTimeMillis() or gettimeofday)."
    ]

    # ============================================================
    # LESSON 06: Hardware & Native System Interop
    # ============================================================
    l6 = gen_phase1_to_3_remaining.lessons[0]
    l6['q1'] = {
        "question": "When implementing the SecureVault interface below on iOS in iosMain, which native Apple C-API/framework provides hardware-backed credential encryption?",
        "code_snippet": """// commonMain
interface SecureVault {
    suspend fun saveToken(key: String, token: String)
    suspend fun getToken(key: String): String?
}""",
        "options": [
            "The Security framework via SecItemAdd and SecItemCopyMatching to access the iOS Keychain Services.",
            "The UserDefaults standard plist storage file located in the iOS app sandbox container.",
            "The CoreData SQLite database framework using default in-memory persistent stores.",
            "The StoreKit framework which verifies in-app purchase receipts against Apple App Store servers."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "On iOS, hardware-encrypted credentials are saved in Keychain Services via Apple's Security framework (SecItemAdd/SecItemCopyMatching). In Android, the equivalent is Android Keystore / EncryptedSharedPreferences.",
            "whyWrong": [
                {"letter": "B", "reason": "UserDefaults is unencrypted plain XML text and completely insecure for secrets."},
                {"letter": "C", "reason": "CoreData is an object-relational framework, not a secure credential vault."},
                {"letter": "D", "reason": "StoreKit handles payments and subscriptions, not local credential storage."}
            ]
        }
    }
    l6['hints'] = [
        "Define interface SecureStorage with suspend fun store(key: String, value: String).",
        "Add suspend fun get(key: String): String? returning nullable string.",
        "On iOS, back this with Keychain Services; on Android, use EncryptedSharedPreferences."
    ]

    # ============================================================
    # LESSON 07: Multiplatform Coroutines & Concurrency
    # ============================================================
    l7 = gen_phase1_to_3_remaining.lessons[1]
    l7['q1'] = {
        "question": "In the startHeartbeat() worker function below, what critical bug occurs when the parent CoroutineScope is cancelled?",
        "code_snippet": """fun CoroutineScope.startHeartbeat() = launch {
    while (isActive) {
        try {
            sendPing()
            delay(5000)
        } catch (e: Exception) {
            // Swallowing CancellationException!
            println("Ping error: ${e.message}")
        }
    }
}""",
        "options": [
            "CancellationException is swallowed by catch (e: Exception), breaking cooperative cancellation and leaking the background loop.",
            "The JVM immediately crashes with an OutOfMemoryError due to uncollected delay timers.",
            "delay(5000) permanently locks the main UI thread and prevents user touch gestures.",
            "The Kotlin compiler refuses to compile catch blocks inside while (isActive) loops."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Cancellation in coroutines is cooperative and works by throwing CancellationException. Catching Exception without re-throwing CancellationException swallows the cancellation signal, keeping the worker running forever as a zombie job.",
            "whyWrong": [
                {"letter": "B", "reason": "Swallowing cancellation causes a leaked background task, not an immediate OOM crash."},
                {"letter": "C", "reason": "delay is non-blocking and suspends cooperatively without blocking thread locks."},
                {"letter": "D", "reason": "The compiler allows standard try/catch blocks; it cannot statically determine if cancellation was handled correctly."}
            ]
        }
    }
    l7['hints'] = [
        "Launch on the supervisor scope: <code>return scope.launch { ... }</code>.",
        "Wrap the block() invocation in a try/catch block.",
        "In the catch block: <code>catch (e: CancellationException) { throw e }</code> to re-throw cooperative cancellation."
    ]

    # ============================================================
    # LESSON 08: Reactive State Flow & Channels
    # ============================================================
    l8 = gen_phase1_to_3_remaining.lessons[2]
    l8['q1'] = {
        "question": "In the ViewModel below, navigation is stored in MutableStateFlow<String?>. What bug occurs when a user rotates their device after navigating?",
        "code_snippet": """class IncidentViewModel : ViewModel() {
    // Storing navigation one-off event inside StateFlow!
    val navigateToDetail = MutableStateFlow<String?>(null)

    fun onSelect(id: String) {
        navigateToDetail.value = id
    }
}""",
        "options": [
            "StateFlow replays its latest non-null value to new subscribers, re-triggering navigation unexpectedly on screen rotation.",
            "The StateFlow value gets garbage-collected on rotation and triggers an unexpected NullPointerException.",
            "The Android operating system destroys the ViewModel instance permanently during configuration changes.",
            "StateFlow throws a ConflationException when accessed from an Activity with altered orientation."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "StateFlow has a replay cache of 1. When an Activity rotates or an iPad window resizes, the UI re-subscribes and receives the latest value again, causing duplicate navigation. One-off events should be sent via Channel.",
            "whyWrong": [
                {"letter": "B", "reason": "StateFlow retains strong references in ViewModel; it is not garbage collected on orientation change."},
                {"letter": "C", "reason": "Jetpack ViewModels survive configuration changes like screen rotations."},
                {"letter": "D", "reason": "There is no such thing as ConflationException in Kotlin Flow."}
            ]
        }
    }
    l8['hints'] = [
        "Declare MutableStateFlow with initial value: <code>private val _state = MutableStateFlow(\"INITIAL\")</code>.",
        "Expose read-only StateFlow: <code>val state: StateFlow<String> = _state.asStateFlow()</code>.",
        "Declare Channel for one-off side effects: <code>val effectChannel = Channel<String>(Channel.BUFFERED)</code>."
    ]

    # ============================================================
    # LESSON 09: Bridging Coroutines to Swift with SKIE
    # ============================================================
    l9 = gen_phase1_to_3_remaining.lessons[3]
    l9['q1'] = {
        "question": "In the SwiftUI view below, how is vm.uiState consumed without legacy completion handlers?",
        "code_snippet": """// SwiftUI View using SKIE
struct IncidentListView: View {
    @StateObject var vm = ObservableIncidentViewModel()
    var body: some View {
        List(vm.state.items, id: \\.id) { item in Text(item.title) }
            .task {
                for await state in vm.uiState {
                    vm.state = state
                }
            }
    }
}""",
        "options": [
            "SKIE generates code bridging Kotlin StateFlow into a Swift AsyncSequence, allowing native for await loops in SwiftUI.",
            "SwiftUI natively compiles Kotlin bytecode into Swift protocols without third-party plugins.",
            "The for await loop runs a blocking background thread that queries the Kotlin ViewModel every 100ms.",
            "The Kotlin/Native compiler replaces all StateFlow instances with Apple Combine PassthroughSubject."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Without SKIE, Kotlin/Native exports Flow as an unwieldy block callback. SKIE automatically generates Swift extensions conforming Kotlin StateFlow to Swift AsyncSequence.",
            "whyWrong": [
                {"letter": "B", "reason": "SwiftUI cannot natively parse Kotlin bytecode; it requires compiler bridges like SKIE."},
                {"letter": "C", "reason": "AsyncSequence is reactive and push-based; it does not poll on a timer."},
                {"letter": "D", "reason": "Kotlin/Native does not generate Combine subjects by default; SKIE provides modern Swift Concurrency interop."}
            ]
        }
    }
    l9['hints'] = [
        "In plugins block, add: <code>alias(libs.plugins.skie)</code>.",
        "SKIE automatically enhances Kotlin/Native Objective-C exports for Swift.",
        "It transforms StateFlow into Swift AsyncSequence and sealed interfaces into Swift enums."
    ]

    # ============================================================
    # LESSON 10: Ktor 3 Client Architecture
    # ============================================================
    l10 = gen_phase1_to_3_remaining.lessons[4]
    l10['q1'] = {
        "question": "Review the Ktor client code below. What exception will Ktor throw when calling client.get(...).body<AlertDto>()?",
        "code_snippet": """val client = HttpClient() // No ContentNegotiation installed!

suspend fun fetchAlert(): AlertDto {
    // Throws exception at runtime!
    return client.get("https://api.incident.com/alert").body()
}""",
        "options": [
            "NoTransformationFoundException because ContentNegotiation with a JSON serializer is not installed.",
            "MalformedURLException because HttpClient requires an explicit port number in the URL string.",
            "SSLHandshakeException because Ktor prohibits HTTPS connections unless certificates are pinned.",
            "UnsupportedTargetException because HttpClient without an explicit engine cannot resolve on mobile."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "By default, HttpClient can only deserialize raw String, ByteArray, or HttpResponse. To deserialize JSON into custom data classes like AlertDto, you must install ContentNegotiation with json().",
            "whyWrong": [
                {"letter": "B", "reason": "Standard HTTPS URLs on port 443 do not require explicit port numbers."},
                {"letter": "C", "reason": "Ktor supports standard HTTPS without requiring custom certificate pinning."},
                {"letter": "D", "reason": "Ktor auto-resolves the default engine on the classpath if none is explicitly specified."}
            ]
        }
    }
    l10['hints'] = [
        "Call HttpClient { ... } builder.",
        "Install ContentNegotiation: <code>install(ContentNegotiation) { json() }</code>.",
        "This attaches the kotlinx.serialization JSON serializer to the HTTP pipeline."
    ]

    # ============================================================
    # LESSON 11: Kotlinx Serialization Advanced
    # ============================================================
    l11 = gen_phase1_to_3_remaining.lessons[5]
    l11['q1'] = {
        "question": "What happens when json.decodeFromString<AlertPayload>(response) parses the JSON payload below with ignoreUnknownKeys = false?",
        "code_snippet": """@Serializable
data class AlertPayload(val id: String, val level: String)

// Server payload: {"id": "1", "level": "HIGH", "extraField": 123}
val json = Json { ignoreUnknownKeys = false }""",
        "options": [
            "It throws an UnknownFieldException at runtime because extraField is not declared in AlertPayload.",
            "It successfully parses the object and silently drops extraField into an internal fallback map.",
            "The compiler flags AlertPayload as invalid during Gradle build verification.",
            "It replaces all properties in AlertPayload with null values."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "By default, kotlinx.serialization is strict. If the server sends a field not declared in the @Serializable data class and ignoreUnknownKeys is false, an UnknownFieldException is thrown.",
            "whyWrong": [
                {"letter": "B", "reason": "Dropping unknown keys only happens when ignoreUnknownKeys = true."},
                {"letter": "C", "reason": "This is a runtime parsing error, not a compile-time failure."},
                {"letter": "D", "reason": "Properties are never silently set to null if types are non-nullable."}
            ]
        }
    }
    l11['hints'] = [
        "Annotate data class UserSession with <code>@Serializable</code>.",
        "Configure Json: <code>val appJson = Json { ignoreUnknownKeys = true }</code>.",
        "Setting ignoreUnknownKeys = true prevents client crashes when backend adds new fields."
    ]

    # ============================================================
    # LESSON 12: Network Resilience & Auth
    # ============================================================
    l12 = gen_phase1_to_3_remaining.lessons[6]
    l12['q1'] = {
        "question": "In the Ktor Bearer Auth configuration below, how does Ktor prevent 10 concurrent HTTP requests from triggering 10 redundant refresh API calls?",
        "code_snippet": """val client = HttpClient {
    install(Auth) {
        bearer {
            refreshTokens {
                // Refresh token logic
                val newTokens = authApi.refresh(oldTokens?.refreshToken ?: "")
                BearerTokens(newTokens.accessToken, newTokens.refreshToken)
            }
        }
    }
}""",
        "options": [
            "The Bearer Auth plugin acquires an internal mutex so only one coroutine refreshes the token while others wait.",
            "The client terminates 9 of the 10 requests immediately with a CancellationException.",
            "The Apple Darwin network engine merges all 10 sockets into a single unified TCP stream at the kernel layer.",
            "The Kotlin compiler wraps the refreshTokens block inside an atomic hardware spinlock."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Ktor's Auth plugin manages a thread-safe mutex for token refresh. When multiple concurrent calls hit 401s, only the first call triggers the refreshTokens lambda; the other 9 wait and retry using the newly refreshed token.",
            "whyWrong": [
                {"letter": "B", "reason": "Requests are not canceled; they are queued and retried with the new token."},
                {"letter": "C", "reason": "Darwin does not merge distinct HTTP request sockets at the kernel level."},
                {"letter": "D", "reason": "Concurrency synchronization is handled via Kotlin coroutines Mutex, not compiler spinlocks."}
            ]
        }
    }
    l12['hints'] = [
        "Declare sealed interface NetworkError.",
        "Add data class Http(val code: Int) : NetworkError.",
        "Add data object Connectivity : NetworkError.",
        "Add data object Unauthorized : NetworkError."
    ]

    # ============================================================
    # LESSON 13: Room KMP vs. SQLDelight
    # ============================================================
    l13 = gen_phase4_to_6.lessons[0]
    l13['q1'] = {
        "question": "Review the SQLDelight file below. How does its architectural code-generation approach fundamentally differ from Jetpack Room KMP?",
        "code_snippet": """-- SQLDelight file: Incident.sq
CREATE TABLE incidentEntity (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL
);

selectAll:
SELECT * FROM incidentEntity;""",
        "options": [
            "SQLDelight is SQL-first: you write pure SQL queries and it generates Kotlin types, whereas Room is annotation-driven on Kotlin data classes.",
            "SQLDelight compiles SQL directly into C headers, whereas Room requires an active PostgreSQL server.",
            "SQLDelight only runs on Android devices, whereas Room runs exclusively on Apple iOS.",
            "SQLDelight prohibits primary keys and unique indexes on SQLite tables."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "SQLDelight takes a schema-first approach: you write standard SQL schema and queries, and its Gradle plugin generates immutable Kotlin models and type-safe query executors. Room works in reverse: it inspects annotated Kotlin classes and generates SQLite bytecode.",
            "whyWrong": [
                {"letter": "B", "reason": "Both Room KMP and SQLDelight operate on local SQLite database files, not PostgreSQL."},
                {"letter": "C", "reason": "Both Room KMP and SQLDelight support Android and iOS multiplatform targets."},
                {"letter": "D", "reason": "SQLDelight supports full standard SQLite features including primary keys, indexes, and foreign keys."}
            ]
        }
    }
    l13['hints'] = [
        "Define enum class StorageEngine { ROOM, SQLDELIGHT }.",
        "Room is annotation-driven with full Jetpack ecosystem parity.",
        "SQLDelight is schema-first and generates type-safe Kotlin from pure SQL queries."
    ]

    # ============================================================
    # LESSON 14: Jetpack Room KMP in Practice
    # ============================================================
    l14 = gen_phase4_to_6.lessons[1]
    l14['q1'] = {
        "question": "In the Room KMP DAO below, why does getAllAlerts() return Flow<List<AlertEntity>> rather than a plain List<AlertEntity>?",
        "code_snippet": """@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>
}""",
        "options": [
            "Room sets up SQLite table invalidation trackers that automatically re-query and emit fresh snapshots whenever alerts data changes.",
            "Returning Flow forces SQLite to bypass local device disk caching and fetch records from the cloud server.",
            "The Apple iOS operating system prohibits returning standard Kotlin List collections from native libraries.",
            "Room DAOs cannot execute SQL queries synchronously without throwing a MainThreadException."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Returning a Flow from a Room DAO establishes an active table observer. Whenever an insert, update, or delete occurs on the observed table, Room automatically queries SQLite on a background dispatcher and emits the latest list to collectors.",
            "whyWrong": [
                {"letter": "B", "reason": "Room is purely a local SQLite database and has no concept of cloud server fetching."},
                {"letter": "C", "reason": "Kotlin/Native supports standard List collections across all Apple targets."},
                {"letter": "D", "reason": "While database work shouldn't block the main thread, suspending functions returning List are valid; Flow provides continuous reactivity."}
            ]
        }
    }
    l14['hints'] = [
        "Annotate data class with <code>@Entity(tableName = \"alerts\")</code>.",
        "Mark the id property with <code>@PrimaryKey val id: String</code>.",
        "Annotate interface AlertDao with <code>@Dao</code> and write <code>@Query(\"SELECT * FROM alerts\") fun getAlerts(): Flow<List<AlertEntity>></code>."
    ]

    # ============================================================
    # LESSON 15: Key-Value Storage & Multiplatform Settings
    # ============================================================
    l15 = gen_phase4_to_6.lessons[2]
    l15['q1'] = {
        "question": "In the code below, sensitive tokens are stored using default Settings(). Why is this a security violation on mobile?",
        "code_snippet": """val settings = Settings()
// Storing sensitive authentication tokens!
settings.putString("jwt_token", secretJwt)""",
        "options": [
            "Default Settings uses unencrypted NSUserDefaults on iOS and plain SharedPreferences on Android; tokens must use Keychain or Keystore.",
            "Settings can only store integer and boolean values, so string values are corrupted at runtime.",
            "The Multiplatform Settings library exports all key-value pairs to a publicly accessible web socket.",
            "Apple App Store review rejects any application that references the string key jwt_token."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Default Settings() delegates to standard platform preferences (NSUserDefaults on iOS and unencrypted SharedPreferences on Android). Anyone with physical device access or backup tools can read plaintext XML/plist tokens. Secrets must use Keychain or EncryptedSharedPreferences.",
            "whyWrong": [
                {"letter": "B", "reason": "Multiplatform Settings supports String, Int, Long, Boolean, Float, and Double."},
                {"letter": "C", "reason": "Settings does not open any network sockets; it is purely local disk storage."},
                {"letter": "D", "reason": "Apple does not reject apps based on key names; the issue is lack of encryption for credentials."}
            ]
        }
    }
    l15['hints'] = [
        "Wrap Settings instance in a repository: <code>class PreferencesRepository(private val settings: Settings)</code>.",
        "Implement storeUserId: <code>settings.putString(\"user_id\", id)</code>.",
        "Implement getUserId: <code>return settings.getStringOrNull(\"user_id\")</code>."
    ]

    # ============================================================
    # LESSON 16: Offline-First Repository Pattern
    # ============================================================
    l16 = gen_phase4_to_6.lessons[3]
    l16['q1'] = {
        "question": "In the repository implementation below, how is the Single Source of Truth (SSOT) pattern maintained between local database and remote API?",
        "code_snippet": """class IncidentRepository(private val dao: AlertDao, private val api: KtorApi) {
    fun streamIncidents(): Flow<List<Incident>> = dao.observeAll().map { ... }

    suspend fun sync() {
        val remote = api.fetch()
        dao.upsertAll(remote) // Updating local DB pushes fresh data to UI!
    }
}""",
        "options": [
            "The UI only observes the local database Flow; network updates are written to the database, which automatically pushes fresh data to observers.",
            "The repository fetches data from both sources simultaneously and averages their timestamps together.",
            "The local database is erased on every network request to ensure zero stale data remains in storage.",
            "The UI communicates directly with the Ktor API, and the database only stores crash stack traces."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "In an offline-first Single Source of Truth architecture, the local database is the only source the UI reads from. Network requests never update the UI directly; they update the database, which automatically emits the latest state to subscribers.",
            "whyWrong": [
                {"letter": "B", "reason": "Data layers do not average timestamps; they resolve conflicts via revision tokens or last-write-wins."},
                {"letter": "C", "reason": "Erasing the database breaks offline capability and causes jarring empty screens."},
                {"letter": "D", "reason": "The UI should never talk directly to raw network APIs in Clean Architecture."}
            ]
        }
    }
    l16['hints'] = [
        "Call flow.map: <code>return source.map { list -> list.map { it.uppercase() } }</code>.",
        "In offline-first, local database acts as Single Source of Truth (SSOT).",
        "Network operations update the database; the UI only observes database flows."
    ]

    # ============================================================
    # LESSON 17: Multiplatform DI with Koin
    # ============================================================
    l17 = gen_phase4_to_6.lessons[4]
    l17['q1'] = {
        "question": "In the Koin module below, what is the behavioral difference between single for HttpClient and factory for IncidentPresenter?",
        "code_snippet": """val appModule = module {
    single { HttpClient() }
    factory { IncidentPresenter(get()) }
}""",
        "options": [
            "single creates a singleton instance retained across the entire app, while factory creates a new instance on every injection request.",
            "single runs on the main thread, while factory offloads dependency resolution to background worker threads.",
            "factory dependencies can only be injected into Android Activities, while single is restricted to iOS Swift views.",
            "Koin validates factory dependencies at compile time and single dependencies at runtime."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "In Koin, single creates a thread-safe singleton that persists for the lifetime of the Koin application context. factory creates a brand new instance every single time get() or inject() is evaluated.",
            "whyWrong": [
                {"letter": "B", "reason": "Koin DSL declarations do not control thread dispatching."},
                {"letter": "C", "reason": "Both single and factory work identically on Android, iOS, and all other KMP targets."},
                {"letter": "D", "reason": "Koin is a runtime dependency injection / service locator library; neither is checked at compile-time by default."}
            ]
        }
    }
    l17['hints'] = [
        "Use module { ... } DSL from org.koin.dsl.module.",
        "Register singleton: <code>single { \"KoinService\" }</code>.",
        "In Compose Multiplatform, inject ViewModels using <code>koinViewModel()</code>."
    ]

    # ============================================================
    # LESSON 18: Compile-Time DI with Kotlin-Inject
    # ============================================================
    l18 = gen_phase4_to_6.lessons[5]
    l18['q1'] = {
        "question": "In the Kotlin-Inject component below, what happens at compile-time if provideClient() is missing while repository requires HttpClient?",
        "code_snippet": """@Component
abstract class ApplicationComponent {
    abstract val repository: IncidentRepository // Requires HttpClient in constructor!

    // provideClient() is missing!
}""",
        "options": [
            "The KSP Kotlin-Inject compiler generates a compilation error alerting you that HttpClient has no provider in the component.",
            "The application compiles successfully and only throws a RuntimeException when the user opens the repository screen.",
            "Kotlin-Inject automatically instantiates an empty mock instance of HttpClient with null properties.",
            "The Gradle build silently skips building the iOS target and only compiles the Android APK."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Kotlin-Inject is a compile-time dependency injection library powered by KSP. If a required dependency cannot be provided in the component graph, compilation halts with an explicit error message, preventing runtime crashes.",
            "whyWrong": [
                {"letter": "B", "reason": "Runtime missing-dependency crashes happen with runtime service locators like Koin, not Kotlin-Inject."},
                {"letter": "C", "reason": "Kotlin-Inject never generates dummy or mock objects automatically."},
                {"letter": "D", "reason": "KSP runs on all declared targets and halts the entire build upon missing dependencies."}
            ]
        }
    }
    l18['hints'] = [
        "Annotate class with <code>@Component</code>.",
        "Declare abstract property: <code>abstract val serviceName: String</code>.",
        "Provide implementation with <code>@Provides fun provideName(): String = \"KotlinInject\"</code>."
    ]

    # ============================================================
    # LESSON 19: Compose Multiplatform Pipeline
    # ============================================================
    l19 = gen_phase4_to_6.lessons[6]
    l19['q1'] = {
        "question": "In the iosMain Compose entry point below, how does Compose Multiplatform draw UI pixels on iOS inside ComposeUIViewController?",
        "code_snippet": """// iosMain
fun MainViewController() = ComposeUIViewController {
    MaterialTheme {
        IncidentApp()
    }
}""",
        "options": [
            "It uses the Skia 2D graphics engine to render UI components directly into a Metal-accelerated CAEAGLLayer on the screen.",
            "It converts Compose composables into native Apple UIKit controls (like UIButton and UILabel) at runtime.",
            "It runs a headless Safari WebKit browser instance that renders the UI using HTML5 and CSS3 styles.",
            "It executes Java Swing AWT drawing routines on top of an internal iOS X11 display server."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Unlike React Native or Flutter, Compose Multiplatform on iOS hosts a Skia rendering canvas inside a UIViewController. It renders buttons, text, and layouts directly to the Apple Metal graphics pipeline at 60/120Hz.",
            "whyWrong": [
                {"letter": "B", "reason": "Compose Multiplatform renders its own pixels via Skia; it does not map Composables to UIKit widgets."},
                {"letter": "C", "reason": "Compose Multiplatform is not a hybrid webview; it compiles to native ARM64 machine code."},
                {"letter": "D", "reason": "iOS does not support Java Swing or X11."}
            ]
        }
    }
    l19['hints'] = [
        "Declare <code>@Composable fun IncidentBadge(status: String)</code>.",
        "Use Box or Column modifier: <code>Box(modifier = Modifier.padding(16.dp))</code>.",
        "Add Text(text = status) inside the layout."
    ]

    # ============================================================
    # LESSON 20: Navigation in Compose Multiplatform
    # ============================================================
    l20 = gen_phase4_to_6.lessons[7]
    l20['q1'] = {
        "question": "In the Compose Multiplatform navigation host below, how does backStackEntry.toRoute<DetailRoute>() prevent runtime navigation bugs?",
        "code_snippet": """@Serializable
data class DetailRoute(val incidentId: String)

NavHost(navController, startDestination = HomeRoute) {
    composable<DetailRoute> { backStackEntry ->
        val route: DetailRoute = backStackEntry.toRoute()
        DetailScreen(id = route.incidentId)
    }
}""",
        "options": [
            "It uses kotlinx.serialization to deserialize strongly-typed arguments, eliminating URL string concatenation and key mismatches.",
            "It downloads the destination screen layout dynamically from a remote cloud server via HTTP.",
            "It disables the Android system back button to prevent the user from navigating backwards.",
            "It converts all screen arguments into Objective-C NSString pointers on iOS targets."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Jetpack Navigation 2.8+ supports type-safe navigation. Routes are defined as @Serializable classes or objects, and parameters are type-checked at compile time, eliminating raw URL string parsing and missing bundle key crashes.",
            "whyWrong": [
                {"letter": "B", "reason": "Navigation is local and does not download layouts over HTTP."},
                {"letter": "C", "reason": "Navigation integrates with native backstack handling on both Android and iOS."},
                {"letter": "D", "reason": "Data classes in commonMain remain pure Kotlin and do not convert to Objective-C NSStrings."}
            ]
        }
    }
    l20['hints'] = [
        "Declare sealed interface AppRoute.",
        "Annotate data object Home : AppRoute with <code>@Serializable</code>.",
        "Annotate data class Profile(val userId: String) : AppRoute with <code>@Serializable</code>."
    ]

    # ============================================================
    # LESSON 21: Design Systems, Theming & Adaptivity
    # ============================================================
    l21 = gen_phase4_to_6.lessons[8]
    l21['q1'] = {
        "question": "In the theming configuration below, how does isSystemInDarkTheme() behave across Android and iOS?",
        "code_snippet": """val isDark = isSystemInDarkTheme()
val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

MaterialTheme(colorScheme = colorScheme) {
    App()
}""",
        "options": [
            "It observes the OS appearance mode (Android UiModeManager / iOS UITraitCollection) and triggers recomposition on mode changes.",
            "It checks the device battery level and forces dark mode whenever battery drops below 20 percent.",
            "It permanently locks the application in dark mode on iOS devices to save OLED screen power.",
            "It queries a remote Firebase Remote Config flag to determine which theme colors should be displayed."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "isSystemInDarkTheme() is multiplatform in Compose. On Android, it hooks into system night mode; on iOS, it listens to UITraitCollection userInterfaceStyle changes and triggers a Compose recomposition automatically.",
            "whyWrong": [
                {"letter": "B", "reason": "Dark theme detection is based on OS appearance settings, not battery level."},
                {"letter": "C", "reason": "It respects the user's system toggle and transitions between light and dark seamlessly."},
                {"letter": "D", "reason": "It is a local platform API and does not query remote server flags."}
            ]
        }
    }
    l21['hints'] = [
        "Define primary Color: <code>val primaryBlue = Color(0xFF1E88E5)</code>.",
        "Call lightColorScheme: <code>val appColors = lightColorScheme(primary = primaryBlue)</code>.",
        "Pass appColors into MaterialTheme(colorScheme = appColors) { ... }."
    ]

    # ============================================================
    # LESSON 22: Image Loading & Multiplatform Resources
    # ============================================================
    l22 = gen_phase4_to_6.lessons[9]
    l22['q1'] = {
        "question": "In the Compose Multiplatform snippet below, how does Coil 3 load the remote network image in commonMain?",
        "code_snippet": """// Coil 3 Multiplatform AsyncImage in commonMain
AsyncImage(
    model = "https://images.example.com/incident.png",
    contentDescription = stringResource(Res.string.incident_photo),
    modifier = Modifier.size(120.dp)
)""",
        "options": [
            "Coil 3 uses Ktor client engines to fetch network bytes and Skia decoders to render images across Android and iOS.",
            "Coil 3 delegates image loading to Apple's native SDWebImage library on both mobile platforms.",
            "Compose Multiplatform converts the remote image URL into a local Base64 SVG vector during Gradle compilation.",
            "AsyncImage launches a background Java ThreadPoolExecutor that executes Android BitmapFactory on iOS."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Coil 3 was re-engineered for KMP. On Android and iOS, it utilizes Ktor HTTP engines to stream image bytes asynchronously, caches them in memory and disk, and decodes them via Skia into Compose Painters.",
            "whyWrong": [
                {"letter": "B", "reason": "Coil 3 does not depend on SDWebImage; it has its own multiplatform cache and decoding pipeline."},
                {"letter": "C", "reason": "Remote URLs are fetched at runtime over HTTP, not pre-baked at compile time."},
                {"letter": "D", "reason": "Android BitmapFactory does not exist on iOS; Skia handles decoding on Apple targets."}
            ]
        }
    }
    l22['hints'] = [
        "Define @Composable fun IncidentIcon(url: String).",
        "Use AsyncImage with model parameter: <code>AsyncImage(model = url, contentDescription = null)</code>.",
        "In Compose Multiplatform, resources are accessed via Res.drawable and Res.string."
    ]

    # ============================================================
    # LESSON 23: Embedding Compose in Existing iOS Apps
    # ============================================================
    l23 = gen_phase7_to_9.lessons[0]
    l23['q1'] = {
        "question": "In the SwiftUI host code below, how does UIViewControllerRepresentable bridge the Compose screen into SwiftUI?",
        "code_snippet": """// SwiftUI host app
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}""",
        "options": [
            "It embeds the UIViewController returned by ComposeUIViewController directly into the native SwiftUI view hierarchy.",
            "It converts the SwiftUI view hierarchy into an Android Activity running inside an iOS container.",
            "It generates a static screenshot of the Compose screen and renders it as an immutable SwiftUI Image.",
            "It forces the iOS app to launch an external Safari window displaying the Compose multiplatform screen."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "UIViewControllerRepresentable is Apple's official protocol for hosting UIKit view controllers in SwiftUI. Because Compose on iOS outputs a standard UIViewController via ComposeUIViewController, it can be embedded into any SwiftUI screen seamlessly.",
            "whyWrong": [
                {"letter": "B", "reason": "Android Activities cannot run on iOS; only native iOS view controllers execute."},
                {"letter": "C", "reason": "It renders a dynamic, interactive view with full touch and animation support, not a screenshot."},
                {"letter": "D", "reason": "It runs in-process inside the app view hierarchy, not in Safari."}
            ]
        }
    }
    l23['hints'] = [
        "In iosMain, write: <code>fun MainViewController() = ComposeUIViewController { Text(\"Shared iOS View\") }</code>.",
        "ComposeUIViewController bridges Compose layout tree onto Apple Skia layer.",
        "In Swift, embed this via UIViewControllerRepresentable."
    ]

    # ============================================================
    # LESSON 24: Hosting Native UIKit in Compose
    # ============================================================
    l24 = gen_phase7_to_9.lessons[1]
    l24['q1'] = {
        "question": "In the iosMain implementation below, what is the role of UIKitView in Compose Multiplatform?",
        "code_snippet": """// iosMain
@Composable
actual fun NativeMapView(modifier: Modifier) {
    UIKitView(
        factory = { MKMapView() },
        modifier = modifier,
        update = { view -> /* Configure MapKit */ }
    )
}""",
        "options": [
            "It embeds and positions a native Apple UIKit view (such as MKMapView or WKWebView) inside the Compose layout on iOS.",
            "It converts native iOS Objective-C views into pure Kotlin WebAssembly bytecode at runtime.",
            "It takes a snapshot of the Apple Map and displays it as a static PNG drawable inside Compose.",
            "It bypasses Apple UIKit completely and renders MapKit vector tiles directly through OpenGL."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "UIKitView is the Compose Multiplatform equivalent of AndroidView on Android. It allows shared Compose screens on iOS to host native UIKit views (MapKit, AVPlayer, ARKit, Apple Pay buttons) with proper sizing and gesture coordination.",
            "whyWrong": [
                {"letter": "B", "reason": "UIKitView does not use WebAssembly; it links directly against the native Apple UIKit runtime."},
                {"letter": "C", "reason": "It embeds the real, live, interactive UIView into the layer hierarchy."},
                {"letter": "D", "reason": "It uses standard UIKit view hierarchy mechanisms, not custom OpenGL rendering."}
            ]
        }
    }
    l24['hints'] = [
        "Declare expect function in commonMain: <code>@Composable expect fun NativeMapView()</code>.",
        "In iosMain, implement using UIKitView factory: <code>UIKitView(factory = { MKMapView() })</code>.",
        "On Android, implement actual using AndroidView factory: <code>AndroidView(factory = { MapView(it) })</code>."
    ]

    # ============================================================
    # LESSON 25: Dual-UI: Shared Logic + Native SwiftUI
    # ============================================================
    l25 = gen_phase7_to_9.lessons[2]
    l25['q1'] = {
        "question": "In the Dual-UI architectural pattern shown below, what is shared via KMP and what is specialized natively?",
        "code_snippet": """// SwiftUI View in a Dual-UI KMP app
struct IncidentListView: View {
    @ObservedObject var viewModel: ObservableIncidentViewModel
    var body: some View {
        List(viewModel.state.incidents, id: \\.id) { incident in
            Text(incident.title)
        }
    }
}""",
        "options": [
            "The Data, Domain, and ViewModel business state machines are shared in KMP; the UI presentation is written 100% natively in SwiftUI and Jetpack Compose.",
            "The UI design system is shared in KMP, while networking and SQLite database persistence are written natively in Swift.",
            "The Swift compiler converts the SwiftUI view into an Android XML layout file during Gradle builds.",
            "Dual-UI means the app runs simultaneously on two connected mobile devices over a local Bluetooth bridge."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Dual-UI (or Native UI) is widely chosen by enterprise teams with large existing iOS SwiftUI and Android Compose codebases. All networking, caching, domain rules, and ViewModels are shared in KMP, while each platform maintains its own 100% native declarative UI.",
            "whyWrong": [
                {"letter": "B", "reason": "In Dual-UI, data and networking are shared, while UI is native (the reverse of this option)."},
                {"letter": "C", "reason": "SwiftUI is never converted into Android XML layouts."},
                {"letter": "D", "reason": "Dual-UI refers to architectural separation between shared logic and native UI platforms, not Bluetooth device clustering."}
            ]
        }
    }
    l25['hints'] = [
        "Declare data class PassiveUiState(val title: String, val count: Int).",
        "Passive state structures contain zero platform dependencies.",
        "SwiftUI binds to this via SKIE @Observable / AsyncSequence."
    ]

    # ============================================================
    # LESSON 26: Multiplatform Unit Testing
    # ============================================================
    l26 = gen_phase7_to_9.lessons[3]
    l26['q1'] = {
        "question": "In commonTest, why does the test below use a hand-crafted FakeIncidentRepository rather than mockk<IncidentRepository>()?",
        "code_snippet": """// commonTest
class IncidentTest {
    @Test
    fun testBusinessLogic() {
        val fakeRepo = FakeIncidentRepository() // Hand-crafted test fake
        val result = fakeRepo.getIncidents()
        assertEquals(2, result.size)
    }
}""",
        "options": [
            "MockK and Mockito rely on JVM bytecode manipulation and reflection, which cannot compile or run on Kotlin/Native iOS test targets.",
            "Hand-crafted fakes are required by Apple App Store guidelines before publishing apps.",
            "kotlin.test prohibits using interfaces in any test method declared inside commonTest.",
            "The K2 compiler disallows mocking frameworks to prevent intellectual property leaks in test binaries."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Mockito and MockK dynamically generate JVM bytecode using cglib/dexmaker, which cannot run on native iOS machine code. Writing simple test fakes in pure Kotlin works across JVM, iOS Simulator, and WebAssembly targets without issues.",
            "whyWrong": [
                {"letter": "B", "reason": "Apple App Store guidelines inspect production app binaries, not internal unit test methodologies."},
                {"letter": "C", "reason": "kotlin.test fully supports interfaces, abstract classes, and polymorphism."},
                {"letter": "D", "reason": "K2 has no security restrictions on test mocks; the issue is platform runtime bytecode support."}
            ]
        }
    }
    l26['hints'] = [
        "Annotate test method with <code>@Test</code> from kotlin.test.",
        "Use <code>assertEquals(4, 2 + 2)</code>.",
        "commonTest runs identically on JVM (Gradle testDebugUnitTest) and iOS (iosSimulatorArm64Test)."
    ]

    # ============================================================
    # LESSON 27: Testing Flows & Coroutines with Turbine
    # ============================================================
    l27 = gen_phase7_to_9.lessons[4]
    l27['q1'] = {
        "question": "In the Turbine test below, what happens when the test block exits without asserting \"Alert 2\"?",
        "code_snippet": """@Test
fun testIncidentStream() = runTest {
    val flow = flowOf("Alert 1", "Alert 2")
    flow.test {
        assertEquals("Alert 1", awaitItem())
        // Block ends without consuming "Alert 2"
    }
}""",
        "options": [
            "Turbine throws an AssertionError because unconsumed items or unhandled events remaining in the stream violate strict verification.",
            "The test passes silently and logs a warning message to the Gradle console output.",
            "The coroutine engine hangs indefinitely waiting for the flow collector to complete.",
            "The JVM automatically executes the remaining assertions without developer intervention."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Turbine enforces strict determinism. If any emissions, errors, or completion signals occur that are not explicitly asserted with awaitItem(), awaitError(), or awaitComplete(), Turbine fails the test immediately.",
            "whyWrong": [
                {"letter": "B", "reason": "Turbine never silently passes tests with unconsumed items; it fails deterministically."},
                {"letter": "C", "reason": "Turbine terminates the collection scope when exiting the block; it does not hang indefinitely."},
                {"letter": "D", "reason": "The runtime cannot guess developer intent or auto-generate assertion checks."}
            ]
        }
    }
    l27['hints'] = [
        "Wrap test in runTest: <code>@Test fun testFlow() = runTest { ... }</code>.",
        "Call flow.test { ... } from app.cash.turbine.test.",
        "Consume items: <code>assertEquals(10, awaitItem())</code>, then assert completion with <code>awaitComplete()</code>."
    ]

    # ============================================================
    # LESSON 28: Mocking in Multiplatform: Fakes vs. Mockative
    # ============================================================
    l28 = gen_phase7_to_9.lessons[5]
    l28['q1'] = {
        "question": "Why is Ktor's MockEngine in the snippet below superior to mocking repository interfaces in KMP?",
        "code_snippet": """val mockEngine = MockEngine { request ->
    if (request.url.encodedPath.contains("incidents")) {
        respond(
            content = ByteReadChannel(\"\"\"[{"id":"1"}]\"\"\"),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    } else {
        respondError(HttpStatusCode.NotFound)
    }
}""",
        "options": [
            "MockEngine tests the entire real HTTP pipeline (JSON serialization, interceptors, headers) across both JVM and Native targets.",
            "MockEngine sends real HTTP requests over the public internet to JetBrains test servers.",
            "MockEngine converts all HTTP responses into native iOS CoreData SQLite entities automatically.",
            "MockEngine is required because Kotlin/Native cannot compile standard if/else conditional expressions in tests."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "MockEngine sits at the network transport layer. Your repositories, serialization plugins, and auth interceptors run exactly as they would in production, but without making actual socket network calls. It is 100% multiplatform.",
            "whyWrong": [
                {"letter": "B", "reason": "MockEngine is entirely in-memory and executes offline without network calls."},
                {"letter": "C", "reason": "MockEngine returns raw HTTP response data; it has no relationship to Apple CoreData."},
                {"letter": "D", "reason": "Kotlin/Native compiles all standard Kotlin control flow syntax including if/else."}
            ]
        }
    }
    l28['hints'] = [
        "Instantiate MockEngine: <code>val engine = MockEngine { request -> ... }</code>.",
        "Return mock response: <code>respond(content = \"OK\", status = HttpStatusCode.OK)</code>.",
        "MockEngine works across JVM and Kotlin/Native, avoiding reflection-based Mockito crashes."
    ]

    # ============================================================
    # LESSON 29: Compose Multiplatform UI Testing
    # ============================================================
    l29 = gen_phase7_to_9.lessons[6]
    l29['q1'] = {
        "question": "In the Compose Multiplatform UI test below, what tree does onNodeWithText(...) search through to find the element?",
        "code_snippet": """@Test
fun testIncidentCardDisplaysTitle() = runComposeUiTest {
    setContent { IncidentCard(title = "Network Outage") }
    onNodeWithText("Network Outage").assertIsDisplayed()
}""",
        "options": [
            "It traverses the Compose Semantics Tree, which exposes accessibility properties, content descriptions, and user-facing text.",
            "It parses the raw Android View hierarchy XML structure generated by the Android Gradle Plugin.",
            "It uses an optical character recognition (OCR) neural network to read pixels off the simulated screen.",
            "It reads the iOS UIKit UIView view hierarchy through Apple's native XCUITest test runner."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Compose UI tests do not search pixel buffers or view IDs. Instead, they query the Semantics Tree—a companion accessibility structure that records text, clickable states, and test tags on composables.",
            "whyWrong": [
                {"letter": "B", "reason": "Compose components do not generate legacy Android View XML hierarchies."},
                {"letter": "C", "reason": "Compose tests query the semantic model directly; they do not use OCR screen recognition."},
                {"letter": "D", "reason": "Compose Multiplatform UI tests run in commonTest using the multiplatform Compose testing library, not XCUITest."}
            ]
        }
    }
    l29['hints'] = [
        "In test scope, find element: <code>onNodeWithText(\"Submit\")</code>.",
        "Trigger user interaction: <code>.performClick()</code>.",
        "Assert presence on semantics tree: <code>.assertExists()</code>."
    ]

    # ============================================================
    # LESSON 30: Gradle Build Optimization & Caching
    # ============================================================
    l30 = gen_phase7_to_9.lessons[7]
    l30['q1'] = {
        "question": "In the gradle.properties file below, what performance optimization does org.gradle.configuration-cache=true provide?",
        "code_snippet": """# gradle.properties
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true
kotlin.daemon.jvmargs=-Xmx4096m""",
        "options": [
            "It caches the result of the Gradle configuration phase, allowing incremental builds to skip evaluating build scripts entirely when unchanged.",
            "It compresses all compiled Kotlin/Native XCFrameworks into encrypted ZIP archives on disk.",
            "It forces the Kotlin compiler to run in single-threaded mode to prevent high CPU fan noise on developer laptops.",
            "It uploads all project source files to Gradle Enterprise servers for remote distributed compilation."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "The Configuration Cache records the task execution graph during the configuration phase. On subsequent builds where build scripts haven't changed, Gradle skips the configuration phase completely and starts executing tasks immediately.",
            "whyWrong": [
                {"letter": "B", "reason": "Build caching stores task outputs, but does not create encrypted ZIP framework archives."},
                {"letter": "C", "reason": "Configuration cache works in tandem with org.gradle.parallel to maximize multi-core execution."},
                {"letter": "D", "reason": "Local configuration cache operates purely on the local machine without remote uploads."}
            ]
        }
    }
    l30['hints'] = [
        "Enable build cache: <code>org.gradle.caching=true</code>.",
        "Enable configuration cache: <code>org.gradle.configuration-cache=true</code>.",
        "Enable parallel project execution: <code>org.gradle.parallel=true</code>."
    ]

    # ============================================================
    # LESSON 31: GitHub Actions Multiplatform Matrix CI/CD
    # ============================================================
    l31 = gen_phase7_to_9.lessons[8]
    l31['q1'] = {
        "question": "In the GitHub Actions matrix workflow below, why must the iOS compilation job run on macos-latest rather than ubuntu-latest?",
        "code_snippet": """jobs:
  build:
    strategy:
      matrix:
        os: [ubuntu-latest, macos-latest]
    runs-on: ${{ matrix.os }}""",
        "options": [
            "Compiling Kotlin/Native for iOS and packaging XCFrameworks requires Apple Xcode and the iOS SDK, which are only available on macOS runners.",
            "Ubuntu Linux runners prohibit executing any shell scripts that contain the word 'kotlin' in their command line.",
            "GitHub Actions charges double for Linux runners compared to macOS runners for multiplatform repositories.",
            "The Kotlin 2.x compiler cannot generate JVM bytecode when running on Ubuntu operating systems."
        ],
        "correct_index": 0,
        "explanation": {
            "whyCorrect": "Kotlin/Native compiles to Apple targets using the Apple LLVM Clang toolchain and links against Apple system frameworks (Foundation, UIKit, Security). These frameworks and the Xcode simulator SDKs only exist on macOS runners.",
            "whyWrong": [
                {"letter": "B", "reason": "Linux has no restrictions on Kotlin scripts or commands."},
                {"letter": "C", "reason": "macOS runners are actually more expensive than Linux runners; teams use Ubuntu for Android/JVM tasks to save cost."},
                {"letter": "D", "reason": "The Kotlin compiler runs on any platform that has a Java Virtual Machine (JDK)."}
            ]
        }
    }
    l31['starter_code'] = """# TODO: Configure GitHub Actions matrix strategy
# with two OS runners: ubuntu-latest and macos-latest
strategy:
  matrix:
    # Add os list here
"""
    l31['final_solution'] = """strategy:
  matrix:
    os: [ubuntu-latest, macos-latest]"""
    l31['hints'] = [
        "Under strategy:, declare matrix:.",
        "Specify the os runners array: <code>os: [ubuntu-latest, macos-latest]</code>.",
        "Linux runs Android lint and JVM tests; macOS compiles iOS arm64 frameworks."
    ]

    # ============================================================
    # Re-generate all lessons cleanly!
    # ============================================================
    for l in gen_phase0_to_3.lessons:
        generate_lesson_file(l)
    for l in gen_phase1_to_3_remaining.lessons:
        generate_lesson_file(l)
    for l in gen_phase4_to_6.lessons:
        generate_lesson_file(l)
    for l in gen_phase7_to_9.lessons:
        generate_lesson_file(l)

    print("ALL 30 generator lessons (02-31) have been 100% harmonized and re-generated!")

if __name__ == "__main__":
    update_all_quizzes_complete()
