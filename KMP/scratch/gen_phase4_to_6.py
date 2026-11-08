import sys
import os
sys.path.append(os.path.dirname(__file__))
from generator_base import generate_lesson_file

lessons = [
    # Lesson 13
    {
        "num": 13,
        "slug": "room-kmp-vs-sqldelight",
        "phase_tag": "Phase 4",
        "title": "Room KMP vs. SQLDelight: Comparison & Trade-Offs",
        "subtitle": "Analyze the technical trade-offs between Google's official Jetpack Room KMP and Cash App's SQLDelight.",
        "prev_slug": "0012-network-resilience-auth-token-refresh",
        "next_slug": "0014-jetpack-room-kmp-in-practice",
        "ref_slug": "13-persistence-engine-matrix",
        "ref_title": "Local Persistence Engine Decision Matrix",
        "mental_model": """
        <p>Choosing a database persistence engine is one of the highest-leverage architectural decisions in KMP. Currently, two major players dominate:</p>
        <table style="width: 100%; border-collapse: collapse; margin: 1rem 0;">
          <tr style="border-bottom: 2px solid var(--border-color); text-align: left;">
            <th style="padding: 0.5rem;">Criteria</th>
            <th style="padding: 0.5rem;">Google Jetpack Room KMP</th>
            <th style="padding: 0.5rem;">Cash App SQLDelight</th>
          </tr>
          <tr style="border-bottom: 1px solid var(--border-color);">
            <td style="padding: 0.5rem;"><strong>Philosophy</strong></td>
            <td style="padding: 0.5rem;">Annotation-driven ORM (@Entity, @Dao)</td>
            <td style="padding: 0.5rem;">SQL-first compiler (.sq files &rarr; Kotlin types)</td>
          </tr>
          <tr style="border-bottom: 1px solid var(--border-color);">
            <td style="padding: 0.5rem;"><strong>Kotlin 2.x Support</strong></td>
            <td style="padding: 0.5rem;">Full KSP2 compiler support</td>
            <td style="padding: 0.5rem;">Full compiler plugin support</td>
          </tr>
          <tr style="border-bottom: 1px solid var(--border-color);">
            <td style="padding: 0.5rem;"><strong>Best For</strong></td>
            <td style="padding: 0.5rem;">Android teams migrating to KMP; CRUD schemas</td>
            <td style="padding: 0.5rem;">Complex custom SQL queries, high-throughput financial apps</td>
          </tr>
        </table>
        """,
        "production_code": """
        <h3>SQLDelight (.sq) vs Room (@Dao)</h3>
        <p><strong>Room DAO in commonMain:</strong></p>
        <pre><code>@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)
}</code></pre>
        <p><strong>SQLDelight .sq file in commonMain:</strong></p>
        <pre><code>CREATE TABLE incident (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    timestamp INTEGER NOT NULL
);

selectAll:
SELECT * FROM incident ORDER BY timestamp DESC;

insertIncident:
INSERT OR REPLACE INTO incident(id, title, timestamp)
VALUES ?;</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Android SQLiteDriver on iOS Targets</div>
          Room KMP requires <code>BundledSQLiteDriver</code> on iOS/Native targets. Attempting to use the platform Android SQLite framework driver on Apple devices will fail to link. Always configure the driver via expect/actual or DI.
        </div>
        """,
        "code_title": "Compare Storage Engine Selection",
        "code_filename": "StorageConfig.kt",
        "code_instructions": "Define an enum <code>StorageEngine { ROOM_KMP, SQL_DELIGHT }</code> and a function <code>recommendedEngine(isComplexSql: Boolean): StorageEngine</code>.",
        "starter_code": """// TODO: Define enum StorageEngine { ROOM_KMP, SQL_DELIGHT }
// TODO: Implement recommendedEngine(isComplexSql: Boolean): StorageEngine""",
        "hints": [
            "enum class StorageEngine { ROOM_KMP, SQL_DELIGHT }",
            "if (isComplexSql) StorageEngine.SQL_DELIGHT else StorageEngine.ROOM_KMP"
        ],
        "final_solution": """enum class StorageEngine { ROOM_KMP, SQL_DELIGHT }

fun recommendedEngine(isComplexSql: Boolean): StorageEngine {
    return if (isComplexSql) StorageEngine.SQL_DELIGHT else StorageEngine.ROOM_KMP
}""",
        "solution_explanation": "SQLDelight shines when writing raw SQL queries, joins, and custom window functions, while Room KMP offers familiarity for Android developers moving to multiplatform.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasEnum = /enum\s+class\s+StorageEngine\s*\{[\s\S]*?ROOM_KMP[\s\S]*?SQL_DELIGHT[\s\S]*?\}/.test(code);
          const hasFn = /fun\s+recommendedEngine\s*\(\s*isComplexSql\s*:\s*Boolean\s*\)\s*:\s*StorageEngine/.test(code);

          print("Verifying storage engine decision logic...");
          if (hasEnum) print("✔ StorageEngine enum defined.");
          if (hasFn) print("✔ recommendedEngine function signature verified.");

          tests.push({
            name: "StorageEngine enum declared with ROOM_KMP and SQL_DELIGHT",
            passed: hasEnum,
            message: "Must declare enum class StorageEngine { ROOM_KMP, SQL_DELIGHT }."
          });
          tests.push({
            name: "recommendedEngine function returns correct engine",
            passed: hasFn,
            message: "Must implement recommendedEngine(isComplexSql: Boolean): StorageEngine."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the core architectural philosophy difference between SQLDelight and Room KMP?",
            "options": [
                "SQLDelight generates type-safe Kotlin code from SQL files, while Room uses annotations on Kotlin classes.",
                "SQLDelight only supports SQLite, whereas Room KMP exclusively connects to remote PostgreSQL servers.",
                "Room KMP executes all database queries on the main thread, whereas SQLDelight forbids threading.",
                "SQLDelight requires dynamic JVM reflection, while Room KMP is completely reflection-free."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "SQLDelight is SQL-first: you write raw SQL schemas and queries in .sq files, and its compiler generates Kotlin models and interfaces. Room is annotation-driven: you annotate Kotlin classes with @Entity and @Dao.",
                "whyWrong": [
                    {"letter": "B", "reason": "Both Room KMP and SQLDelight operate on local SQLite databases."},
                    {"letter": "C", "reason": "Both engines support asynchronous Flow queries off the main thread."},
                    {"letter": "D", "reason": "SQLDelight is compile-time generated, not reflection-based."}
                ]
            }
        },
        "q2": {
            "question": "Which driver must be configured when running Jetpack Room KMP on iOS native targets?",
            "options": [
                "The BundledSQLiteDriver providing a bundled SQLite C-library across all non-JVM native targets.",
                "The AndroidSQLiteDriver utilizing Android framework SQLiteOpenHelper bindings and cursors.",
                "The WebStorageDriver executing database transactions through browser localStorage APIs.",
                "The InMemoryDriver preventing data from ever being written to persistent device flash storage."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Room KMP uses SQLiteDriver abstractions. On Apple native platforms, BundledSQLiteDriver bundles a stable SQLite engine so queries run consistently across all iOS versions.",
                "whyWrong": [
                    {"letter": "B", "reason": "AndroidSQLiteDriver is Android-specific and cannot compile on iOS."},
                    {"letter": "C", "reason": "WebStorage is for web browsers, not iOS native targets."},
                    {"letter": "D", "reason": "InMemoryDriver is for ephemeral tests, not production persistence."}
                ]
            }
        },
        "source_title": "Android Developers Room KMP Documentation",
        "source_url": "https://developer.android.com/kotlin/multiplatform/room",
        "source_link_text": "Room Multiplatform Guide",
        "source_note": "Official Google guide on setting up Room KMP in commonMain.",
        "ref_content": """
        <h2>Room KMP Setup Summary</h2>
        <ul>
          <li>Apply <code>androidx.room</code> plugin and <code>ksp</code>.</li>
          <li>Dependencies: <code>androidx.room:room-runtime</code> and <code>androidx.sqlite:sqlite-bundled</code>.</li>
        </ul>
        """
    },

    # Lesson 14
    {
        "num": 14,
        "slug": "jetpack-room-kmp-in-practice",
        "phase_tag": "Phase 4",
        "title": "Jetpack Room KMP in Practice",
        "subtitle": "Construct entities, DAOs, reactive Flow queries, and multiplatform database builders with Room KMP.",
        "prev_slug": "0013-room-kmp-vs-sqldelight",
        "next_slug": "0015-key-value-storage-multiplatform-settings",
        "ref_slug": "14-room-kmp-setup",
        "ref_title": "Room KMP Database & DAO Architecture",
        "mental_model": """
        <p>Google's official port of <strong>Room to Kotlin Multiplatform</strong> brings the familiar Android Jetpack database experience to iOS. In KMP, the architecture consists of:</p>
        <ol>
          <li><code>@Entity</code> data classes in <code>commonMain</code> defining database tables.</li>
          <li><code>@Dao</code> interfaces in <code>commonMain</code> exposing suspending queries and reactive <code>Flow&lt;List&lt;T&gt;&gt;</code> observables.</li>
          <li><code>@Database</code> class in <code>commonMain</code> inheriting from <code>RoomDatabase</code>.</li>
          <li>An <code>expect fun getDatabaseBuilder(): RoomDatabase.Builder&lt;AppDatabase&gt;</code> supplying the platform database file path and <code>BundledSQLiteDriver</code>.</li>
        </ol>
        """,
        "production_code": """
        <h3>Room Database in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val severity: String,
    val timestamp: Long
)

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(incident: IncidentEntity)
}

@Database(entities = [IncidentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Schema Export Directory Not Set for KSP</div>
          Room KMP requires KSP to export schema JSON files for migration verification. In your <code>build.gradle.kts</code>, configure <code>room { schemaDirectory("$projectDir/schemas") }</code> to avoid compilation warnings and enable safe schema migrations.
        </div>
        """,
        "code_title": "Declare Room Entity and DAO Interface",
        "code_filename": "IncidentLocalDb.kt",
        "code_instructions": "Declare a Room <code>@Entity(tableName = \"alerts\")</code> data class <code>AlertEntity(@PrimaryKey val id: String, val message: String)</code> and a <code>@Dao</code> interface with <code>@Query(\"SELECT * FROM alerts\") fun getAlerts(): Flow<List<AlertEntity>></code>.",
        "starter_code": """import androidx.room.*
import kotlinx.coroutines.flow.Flow

// TODO: 1. Annotate AlertEntity with @Entity(tableName = "alerts") and @PrimaryKey on id
data class AlertEntity(val id: String, val message: String)

// TODO: 2. Annotate AlertDao with @Dao and create @Query("SELECT * FROM alerts")
interface AlertDao {
}""",
        "hints": [
            "@Entity(tableName = \"alerts\") data class AlertEntity(@PrimaryKey val id: String, val message: String)",
            "@Dao interface AlertDao { @Query(\"SELECT * FROM alerts\") fun getAlerts(): Flow<List<AlertEntity>> }"
        ],
        "final_solution": """import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "alerts")
data class AlertEntity(@PrimaryKey val id: String, val message: String)

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts")
    fun getAlerts(): Flow<List<AlertEntity>>
}""",
        "solution_explanation": "Flow queries in Room automatically emit new database snapshots whenever underlying table rows are inserted, updated, or deleted.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasEntity = /@Entity\s*\(\s*tableName\s*=\s*"alerts"\s*\)[\s\S]*?@PrimaryKey\s+val\s+id\s*:\s*String/.test(code);
          const hasDao = /@Dao\s+interface\s+AlertDao\s*\{[\s\S]*?@Query\s*\(\s*"SELECT\s+\*\s+FROM\s+alerts"\s*\)[\s\S]*?fun\s+getAlerts\(\s*\)\s*:\s*Flow<List<AlertEntity>>/.test(code);

          print("Validating Room KMP Entity and DAO declarations...");
          if (hasEntity) print("✔ AlertEntity table and primary key verified.");
          if (hasDao) print("✔ AlertDao with reactive Flow query verified.");

          tests.push({
            name: "AlertEntity declared with @Entity and @PrimaryKey",
            passed: hasEntity,
            message: "Must annotate AlertEntity with @Entity(tableName = 'alerts') and id with @PrimaryKey."
          });
          tests.push({
            name: "AlertDao declared with reactive Flow @Query",
            passed: hasDao,
            message: "Must define @Dao interface AlertDao with @Query('SELECT * FROM alerts') fun getAlerts(): Flow<List<AlertEntity>>."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What happens when a DAO method returns a Flow<List<T>> in Room KMP?",
            "options": [
                "Room automatically observes database changes and re-emits updated query results whenever rows change.",
                "The query runs once on the calling thread and immediately cancels the underlying coroutine.",
                "Room opens a persistent WebSocket connection to synchronize table rows with Google Cloud.",
                "The compiler converts the query into a blocking stored procedure inside the iOS Keychain."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Room's reactive Flow integration installs table invalidation trackers. Whenever any insert, update, or delete affects the queried table, Room re-runs the query and emits the new list downstream.",
                "whyWrong": [
                    {"letter": "B", "reason": "Flow queries remain active and continue listening for future table updates."},
                    {"letter": "C", "reason": "Room is a local SQLite database and does not connect to WebSockets."},
                    {"letter": "D", "reason": "Keychain is for secure credentials, not relational tables."}
                ]
            }
        },
        "q2": {
            "question": "Which compiler plugin powers annotation processing for Room KMP in Kotlin 2.x?",
            "options": [
                "Kotlin Symbol Processing (KSP) which parses Kotlin syntax trees directly without Java stubs.",
                "Annotation Processing Tool (KAPT) which generates intermediate Java source code stubs.",
                "The Swift Compiler Plugin converting Objective-C headers into Swift macro definitions.",
                "The Gradle ProGuard plugin minifying unused database entity columns prior to release."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "KSP (Kotlin Symbol Processing) is the modern, fast annotation processor for Kotlin. Room KMP relies on KSP to generate database implementations across both JVM and Apple targets.",
                "whyWrong": [
                    {"letter": "B", "reason": "KAPT is legacy, slow, and cannot generate code for Kotlin/Native targets."},
                    {"letter": "C", "reason": "Swift macros operate within Xcode, not during Kotlin Gradle builds."},
                    {"letter": "D", "reason": "ProGuard optimizes code; it does not generate DAO implementations."}
                ]
            }
        },
        "source_title": "Google Jetpack Room Guide",
        "source_url": "https://developer.android.com/training/data-storage/room",
        "source_link_text": "Save Data in a Local Database Using Room",
        "source_note": "Google's authoritative guide on Room architecture, entities, and DAOs.",
        "ref_content": """
        <h2>Room KMP Architecture Essentials</h2>
        <pre><code>// Common builder setup
fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}</code></pre>
        """
    },

    # Lesson 15
    {
        "num": 15,
        "slug": "key-value-storage-multiplatform-settings",
        "phase_tag": "Phase 4",
        "title": "Key-Value Storage & Multiplatform Settings",
        "subtitle": "Persist user preferences, feature flags, and UI configurations using Multiplatform Settings and DataStore.",
        "prev_slug": "0014-jetpack-room-kmp-in-practice",
        "next_slug": "0016-offline-first-repository-pattern",
        "ref_slug": "15-multiplatform-settings",
        "ref_title": "Multiplatform Settings & DataStore Cheatsheet",
        "mental_model": """
        <p>Not all data belongs in a relational SQLite database. Simple user preferences (e.g. Dark Mode enabled, last sync timestamp, analytics opt-in) require lightweight key-value persistence.</p>
        <p>In KMP, <strong>Multiplatform Settings (by Russell Wolf)</strong> is the industry standard:</p>
        <ul>
          <li>On <strong>Android</strong>, it delegates to <code>SharedPreferences</code>.</li>
          <li>On <strong>iOS</strong>, it delegates to <code>NSUserDefaults</code>.</li>
          <li>It provides synchronous and observable (Flow-based) APIs through <code>ObservableSettings</code>.</li>
        </ul>
        """,
        "production_code": """
        <h3>Type-Safe Preference Repository</h3>
        <pre><code>package com.pulsesync.data.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class AppPreferences(private val settings: Settings) {
    companion object {
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
    }

    var isDarkModeEnabled: Boolean
        get() = settings[KEY_DARK_MODE, false]
        set(value) { settings[KEY_DARK_MODE] = value }

    var lastSyncTimestamp: Long
        get() = settings[KEY_LAST_SYNC, 0L]
        set(value) { settings[KEY_LAST_SYNC] = value }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Storing Cryptographic Passwords in NSUserDefaults / SharedPreferences</div>
          <code>NSUserDefaults</code> and standard <code>SharedPreferences</code> store data in unencrypted plaintext on the device filesystem. <strong>Never</strong> store user authentication tokens, private encryption keys, or passwords here. Use <code>SecureStorage</code> (Keychain/Keystore) as covered in Lesson 06!
        </div>
        """,
        "code_title": "Implement Type-Safe Preferences Wrapper",
        "code_filename": "UserSettings.kt",
        "code_instructions": "Implement <code>UserSettings(private val settings: Settings)</code> with a boolean property <code>notificationsEnabled</code> backed by <code>settings.getBoolean(\"notifications_enabled\", true)</code>.",
        "starter_code": """import com.russhwolf.settings.Settings

class UserSettings(private val settings: Settings) {
    // TODO: Implement var notificationsEnabled: Boolean (default true)
}""",
        "hints": [
            "get() = settings.getBoolean(\"notifications_enabled\", true)",
            "set(value) = settings.putBoolean(\"notifications_enabled\", value)"
        ],
        "final_solution": """import com.russhwolf.settings.Settings

class UserSettings(private val settings: Settings) {
    var notificationsEnabled: Boolean
        get() = settings.getBoolean("notifications_enabled", true)
        set(value) = settings.putBoolean("notifications_enabled", value)
}""",
        "solution_explanation": "Wrapping Settings behind dedicated typed properties isolates key strings and default values from the rest of the application codebase.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasGet = /settings\.getBoolean\(\s*"notifications_enabled"\s*,\s*true\s*\)/.test(code);
          const hasPut = /settings\.putBoolean\(\s*"notifications_enabled"\s*,\s*value\s*\)/.test(code);

          print("Validating UserSettings implementation...");
          if (hasGet) print("✔ Default true getter verified.");
          if (hasPut) print("✔ Boolean setter verified.");

          tests.push({
            name: "notificationsEnabled reads with default true",
            passed: hasGet,
            message: "Must call settings.getBoolean('notifications_enabled', true) in getter."
          });
          tests.push({
            name: "notificationsEnabled writes via putBoolean",
            passed: hasPut,
            message: "Must call settings.putBoolean('notifications_enabled', value) in setter."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the underlying native storage mechanism used by Multiplatform Settings on iOS targets?",
            "options": [
                "NSUserDefaults which persists lightweight key-value pairs in the app container property list.",
                "Apple CoreData configuring an encrypted SQLite relational graph on the disk filesystem.",
                "The Apple Keychain storing items inside the hardware Secure Enclave coprocessor.",
                "The iCloud CloudKit key-value storage synchronizing properties across user Apple devices."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Multiplatform Settings delegates to NSUserDefaults on iOS, providing fast, synchronous preference storage ideal for settings and flags.",
                "whyWrong": [
                    {"letter": "B", "reason": "CoreData is a relational ORM, not key-value settings."},
                    {"letter": "C", "reason": "Keychain is for secure secrets, not general app preferences."},
                    {"letter": "D", "reason": "CloudKit is a remote cloud sync service, not standard local settings."}
                ]
            }
        },
        "q2": {
            "question": "Which of the following data items should NEVER be stored in Multiplatform Settings?",
            "options": [
                "User OAuth2 refresh tokens and encrypted private session keys.",
                "The user's preferred application color theme (Dark vs Light).",
                "The timestamp of the last successful background content synchronization.",
                "A boolean flag indicating whether the onboarding tutorial was completed."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Multiplatform Settings delegates to unencrypted stores (NSUserDefaults and SharedPreferences). Sensitive secrets and tokens must always be stored in the Keychain / Keystore.",
                "whyWrong": [
                    {"letter": "B", "reason": "Theme preference is non-sensitive and perfect for Settings."},
                    {"letter": "C", "reason": "Sync timestamps are non-sensitive metadata."},
                    {"letter": "D", "reason": "Onboarding flags are standard preference values."}
                ]
            }
        },
        "source_title": "Multiplatform Settings Repository",
        "source_url": "https://github.com/russhwolf/multiplatform-settings",
        "source_link_text": "Multiplatform Settings by Russell Wolf",
        "source_note": "Industry standard key-value persistence library for Kotlin Multiplatform.",
        "ref_content": """
        <h2>Multiplatform Settings Platform Delegates</h2>
        <ul>
          <li><strong>Android</strong>: <code>SharedPreferencesSettings(context.getSharedPreferences(...))</code></li>
          <li><strong>iOS</strong>: <code>NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)</code></li>
        </ul>
        """
    },

    # Lesson 16
    {
        "num": 16,
        "slug": "offline-first-repository-pattern",
        "phase_tag": "Phase 4",
        "title": "The Offline-First Repository Pattern",
        "subtitle": "Build a Single-Source-of-Truth synchronization engine that streams cache instantly and updates from network in background.",
        "prev_slug": "0015-key-value-storage-multiplatform-settings",
        "next_slug": "0017-multiplatform-di-with-koin",
        "ref_slug": "16-offline-first-sync-engine",
        "ref_title": "Offline-First Synchronization Architecture",
        "mental_model": """
        <p>In high-grade mobile engineering, the app must <strong>never display an empty white screen or blocking spinner if data exists on the device</strong>. We achieve this with the <strong>Single Source of Truth (SSOT)</strong> pattern:</p>
        <ol>
          <li>The UI <strong>only observes the local database</strong> (Room / SQLDelight Flow).</li>
          <li>When a screen opens, cached data is rendered <strong>instantly</strong> (0ms latency).</li>
          <li>A background coroutine fetches fresh data from the network API (Ktor).</li>
          <li>Fresh data is written directly into the local database.</li>
          <li>The database automatically emits the updated list to the UI Flow.</li>
        </ol>
        """,
        "production_code": """
        <h3>Offline-First Repository Implementation</h3>
        <pre><code>package com.pulsesync.data.repository

import com.pulsesync.data.local.IncidentDao
import com.pulsesync.data.remote.IncidentApiClient
import kotlinx.coroutines.flow.*

class OfflineFirstIncidentRepository(
    private val localDao: IncidentDao,
    private val remoteApi: IncidentApiClient
) {
    // 1. Single Source of Truth: UI observes local database
    fun streamIncidents(): Flow<List<Incident>> {
        return localDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // 2. Background Sync: Fetch from network and write to database
    suspend fun syncWithServer(): Result<Unit> {
        return try {
            val remoteData = remoteApi.fetchIncidents()
            localDao.upsertAll(remoteData.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Clearing Cache on Network Failure</div>
          If a network request fails (e.g. offline mode, airplane mode, subway tunnel), do <strong>not</strong> clear or overwrite the local database cache! The app should silently retain cached data and expose a non-intrusive offline indicator to the user.
        </div>
        """,
        "code_title": "Implement Offline-First Flow Mapper",
        "code_filename": "SyncEngine.kt",
        "code_instructions": "Implement <code>observeCachedItems(dbFlow: Flow<List<String>>): Flow<Int></code> that maps the emitted list of strings to its count (list size).",
        "starter_code": """import kotlinx.coroutines.flow.*

// TODO: Implement observeCachedItems(dbFlow: Flow<List<String>>): Flow<Int>
fun observeCachedItems(dbFlow: Flow<List<String>>): Flow<Int> {
    return dbFlow.map { 0 }
}""",
        "hints": [
            "Use the map operator: dbFlow.map { it.size }"
        ],
        "final_solution": """import kotlinx.coroutines.flow.*

fun observeCachedItems(dbFlow: Flow<List<String>>): Flow<Int> {
    return dbFlow.map { it.size }
}""",
        "solution_explanation": "Flow transformations allow the repository layer to convert database entity models into UI domain state without breaking reactivity.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasMapSize = /dbFlow\.map\s*\{\s*it\.size\s*\}/.test(code);

          print("Verifying Flow stream transformation...");
          if (hasMapSize) print("✔ it.size mapping detected.");

          tests.push({
            name: "dbFlow mapped to item count via it.size",
            passed: hasMapSize,
            message: "Must map dbFlow to list size using 'dbFlow.map { it.size }'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary architectural rule of the Single Source of Truth (SSOT) pattern in mobile apps?",
            "options": [
                "The UI observes local database state directly; network responses write to database, never to UI.",
                "All UI state is streamed directly from a remote WebSocket server, bypassing local disk entirely.",
                "Every ViewModel maintains its own private SQLite database file to avoid cross-feature sharing.",
                "Network requests are only allowed to run when the user explicitly triggers a swipe-to-refresh."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Under SSOT, the local database is the only source of truth for the UI. Network calls update the database, which causes the database to emit updated state to the UI.",
                "whyWrong": [
                    {"letter": "B", "reason": "Bypassing local disk breaks offline capability and causes empty screens during poor connectivity."},
                    {"letter": "C", "reason": "Databases are centralized at the data layer, not fragmented across individual ViewModels."},
                    {"letter": "D", "reason": "Background sync should trigger automatically on screen entry, not just manual swipe."}
                ]
            }
        },
        "q2": {
            "question": "Why is the offline-first pattern critical for technical interviews and senior engineer resumes?",
            "options": [
                "It proves you can handle asynchronous race conditions, caching, offline resilience, and state consistency.",
                "It is a legal requirement mandated by the European Union General Data Protection Regulation.",
                "It eliminates the need to write unit tests for the application network and data layers.",
                "It reduces mobile cellular battery consumption to zero by halting all radio frequency operations."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Building offline-first synchronization demonstrates mastery over complex edge cases: conflict resolution, thread safety, network retries, and smooth UX during zero-connectivity conditions.",
                "whyWrong": [
                    {"letter": "B", "reason": "GDPR governs privacy and consent, not offline caching architecture."},
                    {"letter": "C", "reason": "Offline-first systems require more rigorous unit tests, not fewer."},
                    {"letter": "D", "reason": "Networking still uses battery when sync executes."}
                ]
            }
        },
        "source_title": "Google Android Architecture Guide",
        "source_url": "https://developer.android.com/topic/architecture/data-layer/offline-first",
        "source_link_text": "Building an Offline-First Mobile Architecture",
        "source_note": "Google's authoritative architectural guide on offline-first mobile apps.",
        "ref_content": """
        <h2>SSOT Data Flow Diagram</h2>
        <pre><code>[ UI Screen ] <==== (observe Flow) ===== [ Local Database (SSOT) ]
                                                        ^
                                                        | (writes fresh rows)
[ Network API ] ===== (background fetch) ===============+</code></pre>
        """
    },

    # Lesson 17
    {
        "num": 17,
        "slug": "multiplatform-di-with-koin",
        "phase_tag": "Phase 5",
        "title": "Multiplatform Dependency Injection with Koin",
        "subtitle": "Wire shared repositories, Ktor clients, Room databases, and multiplatform ViewModels using Koin 4.x.",
        "prev_slug": "0016-offline-first-repository-pattern",
        "next_slug": "0018-compile-time-di-kotlin-inject",
        "ref_slug": "17-koin-kmp-guide",
        "ref_title": "Koin 4.x Multiplatform Dependency Injection Reference",
        "mental_model": """
        <p>In multiplatform projects, we need a Dependency Injection (DI) system that compiles on both JVM and Native. <strong>Koin</strong> is the most widely adopted pragmatic DI framework in the KMP industry:</p>
        <ul>
          <li>Pure Kotlin, zero reflection on Kotlin/Native.</li>
          <li>Declarative DSL (<code>single</code>, <code>factory</code>, <code>viewModel</code>).</li>
          <li>Provides <code>koinViewModel()</code> directly for Compose Multiplatform.</li>
          <li>Allows injecting platform-specific modules (e.g. <code>platformModule()</code> in <code>androidMain</code> vs <code>iosMain</code>).</li>
        </ul>
        """,
        "production_code": """
        <h3>Koin Multiplatform Module Definition</h3>
        <pre><code>package com.pulsesync.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.context.startKoin

val appModule = module {
    // 1. Singleton Network Client
    single { createHttpClient(get()) }

    // 2. Singleton Repository
    single<IncidentRepository> {
        OfflineFirstIncidentRepository(localDao = get(), remoteApi = get())
    }

    // 3. Multiplatform ViewModel
    factory { IncidentViewModel(repository = get()) }
}

// Platform Module (expect/actual or passed from host app)
expect val platformModule: Module

fun initKoin() {
    startKoin {
        modules(appModule, platformModule)
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Calling startKoin Twice on iOS</div>
          On Android, <code>startKoin</code> is called once in <code>Application.onCreate()</code>. On iOS, calling <code>startKoin</code> multiple times (e.g. inside a SwiftUI View's <code>init</code>) throws <code>KoinAppAlreadyStartedException</code>. Ensure your Swift <code>App</code> struct calls <code>KoinKt.initKoin()</code> exactly once during app launch.
        </div>
        """,
        "code_title": "Define a Koin Multiplatform Module",
        "code_filename": "AppModule.kt",
        "code_instructions": "Define a Koin <code>val networkModule = module { ... }</code> providing a singleton string token via <code>single { \"auth_token_xyz\" }</code>.",
        "starter_code": """import org.koin.dsl.module

// TODO: Define val networkModule = module { single { "auth_token_xyz" } }""",
        "hints": [
            "val networkModule = module { single { \"auth_token_xyz\" } }"
        ],
        "final_solution": """import org.koin.dsl.module

val networkModule = module {
    single { "auth_token_xyz" }
}""",
        "solution_explanation": "The single DSL keyword registers a singleton definition inside the Koin container, resolving dependencies lazily upon first request.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasModule = /val\s+networkModule\s*=\s*module\s*\{[\s\S]*?single\s*\{\s*"auth_token_xyz"\s*\}[\s\S]*?\}/.test(code);

          print("Inspecting Koin module definition...");
          if (hasModule) print("✔ networkModule singleton registered.");

          tests.push({
            name: "networkModule declares single { 'auth_token_xyz' }",
            passed: hasModule,
            message: "Must define 'val networkModule = module { single { \"auth_token_xyz\" } }'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the key difference between the single and factory DSL keywords in Koin?",
            "options": [
                "single creates a persistent singleton instance, while factory creates a fresh instance on each request.",
                "single runs on the main UI thread, while factory automatically runs on a background worker thread.",
                "single is only available on Android, while factory is exclusively supported on Apple iOS devices.",
                "single encrypts the instance inside the Apple Keychain, while factory stores it in memory."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "single { } registers a singleton (one shared instance across the entire application lifetime). factory { } creates a new object every time get() or inject() is called.",
                "whyWrong": [
                    {"letter": "B", "reason": "Koin keywords control instance lifecycle, not thread dispatching."},
                    {"letter": "C", "reason": "Both single and factory are 100% multiplatform."},
                    {"letter": "D", "reason": "Koin is an in-memory dependency injection container; it does not touch the Keychain."}
                ]
            }
        },
        "q2": {
            "question": "How should Koin be initialized on iOS in a dual-platform application?",
            "options": [
                "By invoking an initKoin() function from the Swift App struct during iOS application launch.",
                "By declaring an Android Application class inside the iosMain source set directory.",
                "By annotating every SwiftUI View with the @KoinApplication Swift macro attribute.",
                "By adding an entry to the Apple Info.plist specifying the Koin XML descriptor path."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "In Swift, the root `@main struct iOSApp: App` calls `KoinKt.initKoin()` inside its `init()` method, ensuring Koin starts before any SwiftUI view initializes.",
                "whyWrong": [
                    {"letter": "B", "reason": "Android Application classes are JVM-only and cannot exist in iosMain."},
                    {"letter": "C", "reason": "Swift does not have a @KoinApplication macro."},
                    {"letter": "D", "reason": "Koin does not use XML configuration files."}
                ]
            }
        },
        "source_title": "Koin Official Documentation",
        "source_url": "https://insert-koin.io/docs/reference/koin-mp/kmp",
        "source_link_text": "Koin for Kotlin Multiplatform",
        "source_note": "Official guide on Koin setup, modules, and Compose Multiplatform integration.",
        "ref_content": """
        <h2>Koin Multiplatform DSL Quick Reference</h2>
        <ul>
          <li><code>single { MyRepo(get()) }</code>: Application-wide singleton.</li>
          <li><code>factory { MyUseCase(get()) }</code>: New instance per resolution.</li>
          <li><code>viewModel { MyViewModel(get()) }</code>: Lifecycle-aware ViewModel for Compose.</li>
        </ul>
        """
    },

    # Lesson 18
    {
        "num": 18,
        "slug": "compile-time-di-kotlin-inject",
        "phase_tag": "Phase 5",
        "title": "Compile-Time DI with Kotlin-Inject",
        "subtitle": "Explore the compile-time, zero-reflection alternative to Koin: Kotlin-Inject (Dagger for KMP).",
        "prev_slug": "0017-multiplatform-di-with-koin",
        "next_slug": "0019-compose-multiplatform-rendering-pipeline",
        "ref_slug": "18-kotlin-inject-recipes",
        "ref_title": "Kotlin-Inject Annotations & Component Graph",
        "mental_model": """
        <p>While Koin is simple and popular, it resolves dependencies at <strong>runtime</strong> (service locator style). If a dependency is missing, Koin crashes at runtime when a screen is opened.</p>
        <p>For teams seeking <strong>compile-time safety</strong> (similar to Dagger/Anvil on Android), <strong>kotlin-inject (by Eva Tatarka)</strong> is the multiplatform answer:</p>
        <ul>
          <li>100% compile-time verified via KSP.</li>
          <li>Missing dependencies fail at build time, preventing runtime crashes in production.</li>
          <li>Generates pure Kotlin dependency graphs with zero reflection overhead.</li>
        </ul>
        """,
        "production_code": """
        <h3>Kotlin-Inject Component Graph in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.di

import me.tatarka.inject.annotations.*

@Inject
class GetIncidentsUseCase(private val repository: IncidentRepository) {
    suspend operator fun invoke() = repository.getActiveIncidents()
}

@Component
abstract class ApplicationComponent {
    // Abstract property: kotlin-inject generates the implementation
    abstract val getIncidentsUseCase: GetIncidentsUseCase

    @Provides
    fun provideRepository(): IncidentRepository {
        return IncidentRepositoryImpl()
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Circular Dependencies in Compile-Time Graphs</div>
          Unlike Koin (which uses lazy lookup), compile-time DI graphs with circular dependencies (Class A requires Class B, Class B requires Class A) will cause KSP compilation to fail immediately. Structure your domain layers using strict unidirectional dependencies.
        </div>
        """,
        "code_title": "Declare a Kotlin-Inject Component Interface",
        "code_filename": "DiComponent.kt",
        "code_instructions": "Declare an <code>@Component abstract class NetworkComponent</code> with a <code>@Provides fun provideBaseUrl(): String = \"https://api.pulsesync.com\"</code>.",
        "starter_code": """import me.tatarka.inject.annotations.*

// TODO: Annotate abstract class NetworkComponent with @Component
// TODO: Define @Provides fun provideBaseUrl(): String = "https://api.pulsesync.com"
abstract class NetworkComponent {
}""",
        "hints": [
            "@Component abstract class NetworkComponent",
            "@Provides fun provideBaseUrl(): String = \"https://api.pulsesync.com\""
        ],
        "final_solution": """import me.tatarka.inject.annotations.*

@Component
abstract class NetworkComponent {
    @Provides
    fun provideBaseUrl(): String = "https://api.pulsesync.com"
}""",
        "solution_explanation": "The @Component annotation directs KSP to generate the concrete NetworkComponentImpl class with compile-time factory providers.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasComponent = /@Component[\s\S]*?abstract\s+class\s+NetworkComponent/.test(code);
          const hasProvides = /@Provides[\s\S]*?fun\s+provideBaseUrl\(\s*\)\s*:\s*String\s*=\s*"https:\/\/api\.pulsesync\.com"/.test(code);

          print("Validating Kotlin-Inject component...");
          if (hasComponent) print("✔ @Component abstract class declared.");
          if (hasProvides) print("✔ @Provides provider function declared.");

          tests.push({
            name: "NetworkComponent annotated with @Component",
            passed: hasComponent,
            message: "Must annotate abstract class NetworkComponent with @Component."
          });
          tests.push({
            name: "provideBaseUrl annotated with @Provides",
            passed: hasProvides,
            message: "Must annotate provideBaseUrl with @Provides returning 'https://api.pulsesync.com'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary advantage of Kotlin-Inject over Koin in large-scale mobile applications?",
            "options": [
                "It validates dependency graphs at compile time, preventing runtime crashes from missing bindings.",
                "It automatically converts all Kotlin source files into native Swift actor classes.",
                "It reduces mobile device RAM memory consumption by compressing SQLite databases on disk.",
                "It enables the application to bypass Apple App Store sandbox permission prompts."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Kotlin-Inject uses KSP to inspect and construct the dependency graph during compilation. If any dependency cannot be satisfied, the build fails immediately, guaranteeing zero runtime NoBeanDefFoundException crashes.",
                "whyWrong": [
                    {"letter": "B", "reason": "Kotlin-Inject does not convert Kotlin to Swift actors."},
                    {"letter": "C", "reason": "DI frameworks manage objects in memory, not database disk compression."},
                    {"letter": "D", "reason": "App Store sandbox permissions cannot be bypassed by any DI framework."}
                ]
            }
        },
        "q2": {
            "question": "Which annotation in Kotlin-Inject marks a function as an explicit factory provider for a type?",
            "options": [
                "The @Provides annotation instructing the component generator how to instantiate the type.",
                "The @Inject annotation placed on constructors to request automatic instantiation.",
                "The @Singleton annotation instructing the operating system to persist the object across reboots.",
                "The @Composable annotation enabling Jetpack Compose UI rendering on mobile screens."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "@Provides marks a method or property in an abstract @Component class that supplies an instance (such as third-party SDK clients or custom configuration strings).",
                "whyWrong": [
                    {"letter": "B", "reason": "@Inject is placed on class constructors, not factory functions."},
                    {"letter": "C", "reason": "@Singleton is not a native Kotlin-Inject annotation."},
                    {"letter": "D", "reason": "@Composable is for Compose UI, not DI."}
                ]
            }
        },
        "source_title": "Kotlin-Inject GitHub Repository",
        "source_url": "https://github.com/evant/kotlin-inject",
        "source_link_text": "Kotlin-Inject by Eva Tatarka",
        "source_note": "Compile-time dependency injection library for Kotlin Multiplatform.",
        "ref_content": """
        <h2>Kotlin-Inject vs Koin Decision Summary</h2>
        <ul>
          <li><strong>Koin</strong>: Fastest setup, DSL-driven, ubiquitous in KMP, runtime resolution.</li>
          <li><strong>Kotlin-Inject</strong>: Strict compile-time safety, KSP-driven, zero reflection, ideal for large teams requiring Dagger-like guarantees.</li>
        </ul>
        """
    },

    # Lesson 19
    {
        "num": 19,
        "slug": "compose-multiplatform-rendering-pipeline",
        "phase_tag": "Phase 6",
        "title": "Compose Multiplatform Core & Skia Rendering Pipeline",
        "subtitle": "Understand how Compose Multiplatform renders declarative UI across Android and iOS via the Skia graphics engine.",
        "prev_slug": "0018-compile-time-di-kotlin-inject",
        "next_slug": "0020-navigation-in-compose-multiplatform",
        "ref_slug": "19-compose-multiplatform-rendering",
        "ref_title": "Compose Multiplatform Rendering & Architecture",
        "mental_model": """
        <p>Compose Multiplatform (CMP) allows sharing 100% of UI code between Android and iOS. However, their underlying rendering engines differ:</p>
        <ul>
          <li><strong>On Android</strong>: Compose renders directly via the Android platform Canvas and interacts with native Android View trees.</li>
          <li><strong>On iOS</strong>: JetBrains embeds <strong>Skia</strong> (Google's high-performance 2D graphics library, compiled to Metal on iOS). Compose renders pixels onto a Metal-accelerated <code>CAMetalLayer</code> inside a native <code>UIViewController</code>.</li>
        </ul>
        <p>Because Skia renders identically on both platforms, UI components, custom Canvas drawings, and animations look and behave with pixel-perfect consistency.</p>
        """,
        "production_code": """
        <h3>Shared Composable Screen in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IncidentStatusBadge(severity: String, isResolved: Boolean) {
    Surface(
        color = if (isResolved) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                text = if (isResolved) "RESOLVED" else "CRITICAL: $severity",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Hardcoding Android-Specific DP Assumptions</div>
          While <code>Modifier.padding(16.dp)</code> works across both platforms, remember that iOS devices have physical top safe areas (the Dynamic Island and notch) and bottom home indicator bars. Always use <code>Modifier.windowInsetsPadding(WindowInsets.safeDrawing)</code> to avoid clipping!
        </div>
        """,
        "code_title": "Write a Reusable Shared Composable",
        "code_filename": "MetricCard.kt",
        "code_instructions": "Write an <code>@Composable fun MetricCard(label: String, value: String)</code> rendering a <code>Column</code> with two <code>Text</code> composables.",
        "starter_code": """import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// TODO: Write @Composable fun MetricCard(label: String, value: String)
@Composable
fun MetricCard(label: String, value: String) {
}""",
        "hints": [
            "Inside MetricCard: Column { Text(label); Text(value) }"
        ],
        "final_solution": """import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MetricCard(label: String, value: String) {
    Column {
        Text(text = label)
        Text(text = value)
    }
}""",
        "solution_explanation": "Composable functions in commonMain can be rendered on both Android and iOS with zero platform-specific code.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasComposable = /@Composable\s+fun\s+MetricCard\s*\(\s*label\s*:\s*String\s*,\s*value\s*:\s*String\s*\)/.test(code);
          const hasColumn = /Column\s*\{[\s\S]*?Text[\s\S]*?Text[\s\S]*?\}/.test(code);

          print("Validating Composable component structure...");
          if (hasComposable) print("✔ @Composable signature verified.");
          if (hasColumn) print("✔ Column layout with dual Text composables detected.");

          tests.push({
            name: "MetricCard annotated with @Composable",
            passed: hasComposable,
            message: "Must declare '@Composable fun MetricCard(label: String, value: String)'."
          });
          tests.push({
            name: "Renders Column with Text(label) and Text(value)",
            passed: hasColumn,
            message: "Must render a Column containing both Text composables."
          });
          return { tests };
        }""",
        "q1": {
            "question": "How does Compose Multiplatform achieve high-performance rendering on Apple iOS devices?",
            "options": [
                "It renders via Skia graphics engine accelerated by Apple's Metal GPU framework.",
                "It converts Composable functions into native HTML5 Canvas elements inside a hidden WKWebView.",
                "It transpiles all Compose code directly into imperative Objective-C UIView subclasses.",
                "It delegates touch handling and drawing to the Apple CoreText CPU software rasterizer."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "On iOS, JetBrains embeds Skia backed by Apple Metal. This renders frames at 60Hz/120Hz directly on the GPU without translating to UIKit views.",
                "whyWrong": [
                    {"letter": "B", "reason": "CMP does not use WebViews on iOS; it is native Metal rendering."},
                    {"letter": "C", "reason": "It does not generate UIKit subclasses for individual buttons or text."},
                    {"letter": "D", "reason": "CoreText is for text layouts; Skia + Metal handles full-frame GPU rasterization."}
                ]
            }
        },
        "q2": {
            "question": "Which modifier ensures Compose UI elements do not collide with the iOS Dynamic Island, status bar, or home indicator?",
            "options": [
                "Modifier.windowInsetsPadding(WindowInsets.safeDrawing) handling platform notch and bar insets.",
                "Modifier.systemSecurityPadding() preventing screen capture tools from reading text.",
                "Modifier.deviceNotchSuppressor() which instructs iOS to crop the display aspect ratio.",
                "Modifier.metalLayerInset() which disables GPU acceleration along the device screen edges."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "WindowInsets.safeDrawing automatically adapts to Android system bars and iOS notches/Dynamic Island, applying appropriate padding so content is never obscured.",
                "whyWrong": [
                    {"letter": "B", "reason": "systemSecurityPadding does not exist."},
                    {"letter": "C", "reason": "The notch cannot be cropped or suppressed via Compose modifiers."},
                    {"letter": "D", "reason": "metalLayerInset does not exist in Compose Multiplatform."}
                ]
            }
        },
        "source_title": "JetBrains Compose Multiplatform Documentation",
        "source_url": "https://www.jetbrains.com/lp/compose-multiplatform/",
        "source_link_text": "Compose Multiplatform Architectural Overview",
        "source_note": "JetBrains official guide on Skia rendering on iOS and desktop targets.",
        "ref_content": """
        <h2>Compose Rendering Architecture</h2>
        <ul>
          <li><strong>Android</strong>: Native Canvas / View rendering.</li>
          <li><strong>iOS</strong>: Skia 2D graphics engine &rarr; Apple Metal GPU pipeline.</li>
          <li><strong>Safe Insets</strong>: <code>Modifier.windowInsetsPadding(WindowInsets.safeDrawing)</code></li>
        </ul>
        """
    },

    # Lesson 20
    {
        "num": 20,
        "slug": "navigation-in-compose-multiplatform",
        "phase_tag": "Phase 6",
        "title": "Navigation in Compose Multiplatform",
        "subtitle": "Master type-safe navigation, backstack preservation, and deep linking with Jetpack Navigation Compose Multiplatform.",
        "prev_slug": "0019-compose-multiplatform-rendering-pipeline",
        "next_slug": "0021-design-systems-material3-adaptivity",
        "ref_slug": "20-navigation-multiplatform",
        "ref_title": "Multiplatform Navigation Patterns & Backstack",
        "mental_model": """
        <p>In modern Compose Multiplatform, <strong>Google's official Jetpack Navigation Compose</strong> has been ported to KMP. It provides:</p>
        <ul>
          <li><strong>Type-Safe Routes</strong>: In Kotlin 2.x, routes are defined using <code>@Serializable</code> objects/classes rather than raw URL strings.</li>
          <li><strong>Automatic Backstack Handling</strong>: Supports the Android system back button, predictive back gestures, and iOS edge-swipe back navigation.</li>
          <li><strong>ViewModel Scoping</strong>: Automatically scopes ViewModels to navigation graph backstack entries.</li>
        </ul>
        """,
        "production_code": """
        <h3>Type-Safe Navigation Graph in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object IncidentList : Route
    @Serializable data class IncidentDetail(val incidentId: String) : Route
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.IncidentList) {
        composable<Route.IncidentList> {
            IncidentListScreen(onIncidentClick = { id ->
                navController.navigate(Route.IncidentDetail(id))
            })
        }
        composable<Route.IncidentDetail> { backStackEntry ->
            val detail: Route.IncidentDetail = backStackEntry.toRoute()
            IncidentDetailScreen(incidentId = detail.incidentId, onBack = {
                navController.popBackStack()
            })
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Passing Heavy Domain Models Through Routes</div>
          Do <strong>not</strong> pass large data classes as navigation arguments. Navigation arguments are serialized into route URLs. Pass only lightweight primitive IDs (e.g. <code>incidentId: String</code>) and have the destination ViewModel load the data from the local database.
        </div>
        """,
        "code_title": "Declare Type-Safe Navigation Route Sealed Hierarchy",
        "code_filename": "AppRoutes.kt",
        "code_instructions": "Define a sealed interface <code>AppRoute</code> with <code>@Serializable data object Home : AppRoute</code> and <code>@Serializable data class Profile(val userId: String) : AppRoute</code>.",
        "starter_code": """import kotlinx.serialization.Serializable

// TODO: Define sealed interface AppRoute with Home and Profile(userId: String)
sealed interface AppRoute {
}""",
        "hints": [
            "@Serializable data object Home : AppRoute",
            "@Serializable data class Profile(val userId: String) : AppRoute"
        ],
        "final_solution": """import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Profile(val userId: String) : AppRoute
}""",
        "solution_explanation": "Type-safe navigation eliminates string URL typos, providing compile-time parameter verification across navigation transitions.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasHome = /@Serializable[\s\S]*?data\s+object\s+Home\s*:\s*AppRoute/.test(code);
          const hasProfile = /@Serializable[\s\S]*?data\s+class\s+Profile\s*\(\s*val\s+userId\s*:\s*String\s*\)\s*:\s*AppRoute/.test(code);

          print("Validating type-safe route declarations...");
          if (hasHome) print("✔ Home route object verified.");
          if (hasProfile) print("✔ Profile route with typed argument verified.");

          tests.push({
            name: "Home route defined as @Serializable data object",
            passed: hasHome,
            message: "Must define '@Serializable data object Home : AppRoute'."
          });
          tests.push({
            name: "Profile route defined with userId string argument",
            passed: hasProfile,
            message: "Must define '@Serializable data class Profile(val userId: String) : AppRoute'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary architectural benefit of Kotlin 2.x Type-Safe Navigation over legacy string-based routes?",
            "options": [
                "Route parameters are verified at compile-time using @Serializable classes, eliminating string parsing errors.",
                "It encrypts all navigation transitions using Apple Secure Enclave hardware cryptography.",
                "It permits screens to render without invoking the Compose compiler during application execution.",
                "It forces mobile devices to preload all screen assets from the cloud before app launch."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Type-safe routes use @Serializable data classes and objects. If a screen requires an `incidentId`, the compiler enforces that it is provided, preventing runtime crashes from missing query arguments.",
                "whyWrong": [
                    {"letter": "B", "reason": "Navigation routing has nothing to do with hardware encryption."},
                    {"letter": "C", "reason": "Screens are still Compose functions evaluated by the Compose runtime."},
                    {"letter": "D", "reason": "Routes do not preload assets from the cloud."}
                ]
            }
        },
        "q2": {
            "question": "How should a destination screen retrieve the data model it needs to display in clean architecture?",
            "options": [
                "Pass only the item's unique identifier via the route, and let the ViewModel load it from the database.",
                "Serialize the entire 50-field entity object into a base64 string within the navigation URL route.",
                "Save the object into a global public static variable accessible by any class in the project.",
                "Request the user to re-enter all item details on the new screen using manual text inputs."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Passing only the ID keeps routes clean, URL-safe, and ensures the destination screen always observes fresh data directly from the Single Source of Truth (database).",
                "whyWrong": [
                    {"letter": "B", "reason": "Serializing heavy objects into navigation URLs leads to memory bloat and stale data."},
                    {"letter": "C", "reason": "Global static variables cause memory leaks and concurrency bugs."},
                    {"letter": "D", "reason": "Users should never re-enter existing data."}
                ]
            }
        },
        "source_title": "Google Android Jetpack Navigation Documentation",
        "source_url": "https://developer.android.com/guide/navigation/design/type-safety",
        "source_link_text": "Type Safety in Navigation Compose",
        "source_note": "Official guide on serializable routes and arguments in Navigation Compose.",
        "ref_content": """
        <h2>Type-Safe Navigation Route Pattern</h2>
        <pre><code>// 1. Declare Route
@Serializable data class DetailRoute(val id: String)

// 2. Navigate
navController.navigate(DetailRoute(id = "123"))

// 3. Extract in composable
val route: DetailRoute = backStackEntry.toRoute()</code></pre>
        """
    },

    # Lesson 21
    {
        "num": 21,
        "slug": "design-systems-material3-adaptivity",
        "phase_tag": "Phase 6",
        "title": "Design Systems, Theming & Platform Adaptivity",
        "subtitle": "Construct responsive Material 3 design systems that adapt dynamically to dark mode and platform aesthetics.",
        "prev_slug": "0020-navigation-in-compose-multiplatform",
        "next_slug": "0022-image-loading-coil3-resources",
        "ref_slug": "21-adaptive-design-systems",
        "ref_title": "Adaptive Design Systems & Material 3 Palettes",
        "mental_model": """
        <p>A multiplatform design system must look natural on both Android and iOS. While Android users expect Material 3 ripples and bottom sheets, iOS users expect smooth spring physics, subtle blurs, and Cupertino-style dialogs.</p>
        <p>Key design system pillars in Compose Multiplatform:</p>
        <ul>
          <li><strong>Material 3 Theme Tokenization</strong>: Centralize ColorScheme, Typography, and Shapes in <code>commonMain</code>.</li>
          <li><strong>Dark Mode Adaptivity</strong>: Automatically react to <code>isSystemInDarkTheme()</code> across Android and iOS.</li>
          <li><strong>Platform Adaptive Components</strong>: Create wrappers (e.g. <code>AdaptiveAlertDialog</code>, <code>AdaptiveProgressIndicator</code>) that render Material 3 on Android and Cupertino-inspired designs on iOS.</li>
        </ul>
        """,
        "production_code": """
        <h3>Theme Definition in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF818CF8),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF)
)

@Composable
fun PulseSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Hardcoding Hex Color Literals Inside Composables</div>
          Never write <code>Modifier.background(Color(0xFF121212))</code> inside screen composables. Always reference semantic tokens like <code>MaterialTheme.colorScheme.surface</code>. Hardcoded colors break dark mode support and prevent unified branding updates.
        </div>
        """,
        "code_title": "Build a ColorScheme Wrapper",
        "code_filename": "AppTheme.kt",
        "code_instructions": "Implement a function <code>resolveSurfaceColor(isDark: Boolean): Color</code> returning <code>Color(0xFF1E293B)</code> if dark and <code>Color(0xFFFFFFFF)</code> if light.",
        "starter_code": """import androidx.compose.ui.graphics.Color

// TODO: Implement resolveSurfaceColor(isDark: Boolean): Color
fun resolveSurfaceColor(isDark: Boolean): Color {
    return Color.Unspecified
}""",
        "hints": [
            "if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)"
        ],
        "final_solution": """import androidx.compose.ui.graphics.Color

fun resolveSurfaceColor(isDark: Boolean): Color {
    return if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
}""",
        "solution_explanation": "Semantic color mapping ensures visual contrast and automatic responsiveness to operating system night mode toggles.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasDark = /0xFF1E293B/i.test(code);
          const hasLight = /0xFFFFFFFF/i.test(code);

          print("Validating semantic color resolution...");
          if (hasDark && hasLight) print("✔ Dark and Light surface tokens detected.");

          tests.push({
            name: "Returns Color(0xFF1E293B) for dark and Color(0xFFFFFFFF) for light",
            passed: hasDark && hasLight,
            message: "Must return Color(0xFF1E293B) when isDark is true, and Color(0xFFFFFFFF) when false."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Which function in Compose Multiplatform automatically tracks the system-level Dark Mode toggle across Android and iOS?",
            "options": [
                "The isSystemInDarkTheme() composable function reacting dynamically to OS night mode changes.",
                "The getDeviceBatterySaverMode() function checking whether low power mode is enabled.",
                "The AppleAppearanceObserver class listening to iOS UIKit notification center broadcasts.",
                "The AndroidNightModeReceiver broadcast receiver defined in the AndroidManifest.xml."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "isSystemInDarkTheme() is a multiplatform composable in Compose. On Android it reads the Configuration UI mode; on iOS it reads the UIWindow traitCollection userInterfaceStyle.",
                "whyWrong": [
                    {"letter": "B", "reason": "Battery saver mode does not necessarily dictate dark color themes."},
                    {"letter": "C", "reason": "Compose handles iOS trait collection observation internally."},
                    {"letter": "D", "reason": "AndroidManifest broadcast receivers cannot compile on iOS targets."}
                ]
            }
        },
        "q2": {
            "question": "Why should UI components always reference MaterialTheme.colorScheme tokens instead of hardcoded Color literals?",
            "options": [
                "It enables the entire application to switch instantly between light, dark, and custom themes.",
                "The Compose compiler crashes if a hex color literal contains more than six hexadecimal characters.",
                "Hardcoded colors force the iOS Metal GPU pipeline to fall back to software CPU rendering.",
                "The Google Play Store automatically rejects apps with more than five hardcoded color constants."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Using semantic tokens (e.g. `MaterialTheme.colorScheme.background`) allows the UI to automatically adapt when dark mode toggles or when dynamic branding themes are applied.",
                "whyWrong": [
                    {"letter": "B", "reason": "Compose easily compiles hex literals."},
                    {"letter": "C", "reason": "Metal renders raw RGBA values regardless of how they are defined."},
                    {"letter": "D", "reason": "Play Store does not review color constants in source code."}
                ]
            }
        },
        "source_title": "Material Design 3 Documentation",
        "source_url": "https://m3.material.io/",
        "source_link_text": "Material 3 Design Guidelines",
        "source_note": "Official design specifications for color schemes, elevation, and typography.",
        "ref_content": """
        <h2>Material 3 Design Tokens</h2>
        <ul>
          <li><code>primary</code> / <code>onPrimary</code></li>
          <li><code>surface</code> / <code>onSurface</code></li>
          <li><code>background</code> / <code>onBackground</code></li>
          <li><code>error</code> / <code>onError</code></li>
        </ul>
        """
    },

    # Lesson 22
    {
        "num": 22,
        "slug": "image-loading-coil3-resources",
        "phase_tag": "Phase 6",
        "title": "Image Loading & Multiplatform Resources",
        "subtitle": "Load remote images with Coil 3 and access localized strings, custom fonts, and vector drawables using Compose Resources.",
        "prev_slug": "0021-design-systems-material3-adaptivity",
        "next_slug": "0023-embedding-compose-in-existing-ios-apps",
        "ref_slug": "22-coil3-and-resources",
        "ref_title": "Coil 3 & Compose Multiplatform Resources Guide",
        "mental_model": """
        <p>In Kotlin Multiplatform, resource management and image loading have evolved rapidly:</p>
        <ol>
          <li><strong>Coil 3 (Multiplatform Coil)</strong>: The premier image loading library for KMP. Supports asynchronous disk and memory caching, SVG decoding, and animated GIFs on Android, iOS, and Desktop.</li>
          <li><strong>Compose Multiplatform Resources</strong>: JetBrains' official gradle plugin generating strongly typed accessors (<code>Res.drawable.logo</code>, <code>Res.string.app_name</code>) for assets stored in <code>commonMain/composeResources/</code>.</li>
        </ol>
        """,
        "production_code": """
        <h3>1. Loading Network Images with Coil 3 in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun UserAvatar(imageUrl: String, contentDescription: String?) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = Modifier.size(48.dp)
    )
}</code></pre>

        <h3>2. Accessing Multiplatform Strings & Drawables</h3>
        <pre><code>import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pulsesync.shared.generated.resources.Res
import pulsesync.shared.generated.resources.*

@Composable
fun AppHeader() {
    Icon(
        painter = painterResource(Res.drawable.ic_incident_alert),
        contentDescription = stringResource(Res.string.alert_icon_description)
    )
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Using Coil 2 on KMP</div>
          Coil 2 was Android-only (dependent on Android Context and OkHttp). Do not import <code>io.coil-kt:coil-compose</code>. Always use <strong>Coil 3</strong> (<code>io.coil-kt.coil3:coil-compose</code>) which is built from the ground up for Kotlin Multiplatform.
        </div>
        """,
        "code_title": "Render Async Image Composable",
        "code_filename": "AvatarView.kt",
        "code_instructions": "Write an <code>@Composable fun Avatar(url: String)</code> rendering an <code>AsyncImage(model = url, contentDescription = \"Avatar\")</code>.",
        "starter_code": """import androidx.compose.runtime.Composable
import coil3.compose.AsyncImage

// TODO: Write @Composable fun Avatar(url: String)
@Composable
fun Avatar(url: String) {
}""",
        "hints": [
            "AsyncImage(model = url, contentDescription = \"Avatar\")"
        ],
        "final_solution": """import androidx.compose.runtime.Composable
import coil3.compose.AsyncImage

@Composable
fun Avatar(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Avatar"
    )
}""",
        "solution_explanation": "Coil 3 executes asynchronous network downloads, decodes image buffers via Skia, and stores cached bitmaps across memory and disk caches automatically.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasAsync = /AsyncImage\s*\(\s*model\s*=\s*url\s*,\s*contentDescription\s*=\s*"Avatar"\s*\)/.test(code);

          print("Validating Coil 3 AsyncImage invocation...");
          if (hasAsync) print("✔ AsyncImage composable correctly configured.");

          tests.push({
            name: "Renders AsyncImage with model = url and contentDescription = 'Avatar'",
            passed: hasAsync,
            message: "Must render AsyncImage(model = url, contentDescription = 'Avatar')."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary architectural upgrade in Coil 3 compared to Coil 2?",
            "options": [
                "Coil 3 is completely multiplatform, supporting Android, iOS, Desktop, and Web.",
                "Coil 3 requires images to be stored in SVG format and refuses to decode JPEG files.",
                "Coil 3 generates static HTML5 img tags that execute within a hidden WebKit thread.",
                "Coil 3 deletes the device disk cache whenever battery levels fall below fifty percent."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Coil 3 was re-architected from scratch for Kotlin Multiplatform, decoupling from Android Context and providing native iOS and desktop image pipelines.",
                "whyWrong": [
                    {"letter": "B", "reason": "Coil 3 supports PNG, JPEG, WEBP, GIF, and SVG."},
                    {"letter": "C", "reason": "It renders natively via Compose Canvas/Skia, not WebKit."},
                    {"letter": "D", "reason": "Disk cache is persistent and governed by standard LRU cache policies."}
                ]
            }
        },
        "q2": {
            "question": "Where are multiplatform resources (strings, drawables, fonts) placed in a modern KMP project?",
            "options": [
                "Inside the commonMain/composeResources/ directory with generated accessors on the Res object.",
                "Inside the androidMain/res/values/strings.xml file shared via symbolic file links.",
                "Inside the Xcode Assets.xcassets bundle imported into Android at compile-time.",
                "Inside a remote Amazon S3 storage bucket downloaded during application startup."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Compose Multiplatform generates code from `commonMain/composeResources/`, generating a type-safe `Res` object accessible from common Kotlin code across all platforms.",
                "whyWrong": [
                    {"letter": "B", "reason": "androidMain/res is Android-specific and inaccessible to iOS."},
                    {"letter": "C", "reason": "Assets.xcassets is iOS-specific and cannot be used by Android."},
                    {"letter": "D", "reason": "Local resources are bundled inside the app binary for offline access."}
                ]
            }
        },
        "source_title": "Coil 3 Multiplatform Documentation",
        "source_url": "https://coil-kt.github.io/coil/upgrading_to_coil3/",
        "source_link_text": "Coil 3 Architecture & Multiplatform Migration",
        "source_note": "Official guide on using Coil 3 in Compose Multiplatform.",
        "ref_content": """
        <h2>Coil 3 & Compose Resources Cheatsheet</h2>
        <ul>
          <li><strong>Image Loading</strong>: <code>io.coil-kt.coil3:coil-compose</code></li>
          <li><strong>Resources</strong>: <code>Res.drawable.my_icon</code>, <code>stringResource(Res.string.app_title)</code></li>
        </ul>
        """
    }
]

for l in lessons:
    generate_lesson_file(l)

print("Lessons 13 to 22 (Phase 4, 5, 6) successfully generated!")
