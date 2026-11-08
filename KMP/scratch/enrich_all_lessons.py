import os
import sys

# Ensure generator_base can be found
sys.path.append(os.path.dirname(__file__))

def enrich():
    import gen_phase0_to_3
    import gen_phase1_to_3_remaining
    import gen_phase4_to_6
    import gen_phase7_to_9
    from generator_base import generate_lesson_file

    # ==========================================
    # Phase 0 & 1: Lessons 02 - 05
    # ==========================================
    # Lesson 02: K2 Compiler
    l2 = gen_phase0_to_3.lessons[0]
    l2['q1']['code_snippet'] = """// shared/build.gradle.kts (Kotlin 2.1)
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("androidx.compose.compiler") version "1.5.8" // Legacy AndroidX plugin
}"""
    l2['hints'] = [
        "In the kotlin block, access compilerOptions: <code>kotlin { compilerOptions { ... } }</code>.",
        "Set languageVersion: <code>languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)</code>.",
        "Enable warnings as errors: <code>allWarningsAsErrors.set(true)</code>."
    ]

    # Lesson 03: Apple Toolchain & SPM
    l3 = gen_phase0_to_3.lessons[1]
    l3['q1']['code_snippet'] = """// Package.swift
let package = Package(
    name: "SharedKit",
    platforms: [.iOS(.v16)],
    products: [.library(name: "SharedKit", targets: ["SharedKit"])],
    targets: [
        .binaryTarget(name: "SharedKit", path: "./SharedKit.xcframework")
    ]
)"""
    l3['hints'] = [
        "Instantiate the XCFramework task: <code>val xcf = XCFramework(\"SharedKit\")</code>.",
        "Iterate over iOS targets: <code>listOf(iosArm64(), iosSimulatorArm64()).forEach { it.binaries.framework { xcf.add(this) } }</code>.",
        "Remember that xcframework aggregates both device (arm64) and simulator slices into one bundle."
    ]

    # Lesson 04: Architecture Mindset
    l4 = gen_phase0_to_3.lessons[2]
    l4['q1']['code_snippet'] = """// commonMain/src/domain/IncidentRepository.kt
package com.pulsesync.domain

import android.content.Context // Architecture smell!

class IncidentRepository(private val context: Context) {
    fun getLogPath(): String = context.filesDir.absolutePath
}"""
    l4['hints'] = [
        "Declare enum class Severity with constants LOW, HIGH.",
        "Declare interface IncidentRepository with suspend fun getActiveIncidents(): List<String>.",
        "Ensure no platform SDKs (Android Context or iOS Foundation) are imported in commonMain."
    ]

    # Lesson 05: expect/actual
    l5 = gen_phase0_to_3.lessons[3]
    l5['q1']['code_snippet'] = """// commonMain
expect class DeviceBatteryManager() {
    fun getBatteryLevel(): Int
}

// androidMain
actual class DeviceBatteryManager {
    actual fun getBatteryLevel(): Int = 85
}"""
    l5['hints'] = [
        "Use the expect keyword: <code>expect fun getCurrentTimeMillis(): Long</code>.",
        "Remember: expected functions declare signatures without a body in commonMain.",
        "In actual implementations, the platform supplies the concrete body (System.currentTimeMillis() or gettimeofday)."
    ]

    # ==========================================
    # Phase 1, 2, 3: Lessons 06 - 12
    # ==========================================
    # Lesson 06: Hardware & Native System Interop
    l6 = gen_phase1_to_3_remaining.lessons[0]
    l6['q1']['code_snippet'] = """// commonMain
interface SecureVault {
    suspend fun saveToken(key: String, token: String)
    suspend fun getToken(key: String): String?
}"""
    l6['hints'] = [
        "Define interface SecureStorage with suspend fun store(key: String, value: String).",
        "Add suspend fun get(key: String): String? returning nullable string.",
        "On iOS, back this with Keychain Services; on Android, use EncryptedSharedPreferences."
    ]

    # Lesson 07: Coroutines Concurrency
    l7 = gen_phase1_to_3_remaining.lessons[1]
    l7['q1']['code_snippet'] = """fun CoroutineScope.startHeartbeat() = launch {
    while (isActive) {
        try {
            sendPing()
            delay(5000)
        } catch (e: Exception) {
            // Swallowing CancellationException!
            println("Ping error: ${e.message}")
        }
    }
}"""
    l7['hints'] = [
        "Launch on the supervisor scope: <code>return scope.launch { ... }</code>.",
        "Wrap the block() invocation in a try/catch block.",
        "In the catch block: <code>catch (e: CancellationException) { throw e }</code> to re-throw cooperative cancellation."
    ]

    # Lesson 08: Reactive State Flow
    l8 = gen_phase1_to_3_remaining.lessons[2]
    l8['q1']['code_snippet'] = """class IncidentViewModel : ViewModel() {
    // Storing navigation one-off event inside StateFlow!
    val navigateToDetail = MutableStateFlow<String?>(null)

    fun onSelect(id: String) {
        navigateToDetail.value = id
    }
}"""
    l8['hints'] = [
        "Declare MutableStateFlow with initial value: <code>private val _state = MutableStateFlow(\"INITIAL\")</code>.",
        "Expose read-only StateFlow: <code>val state: StateFlow<String> = _state.asStateFlow()</code>.",
        "Declare Channel for one-off side effects: <code>val effectChannel = Channel<String>(Channel.BUFFERED)</code>."
    ]

    # Lesson 09: SKIE Swift Interop
    l9 = gen_phase1_to_3_remaining.lessons[3]
    l9['q1']['code_snippet'] = """// SwiftUI View using SKIE
struct IncidentListView: View {
    @StateObject var vm = ObservableIncidentViewModel()
    var body: some View {
        List(vm.state.items, id: \\.id) { item in Text(item.title) }
            .task {
                // Consuming StateFlow as Swift AsyncSequence
                for await state in vm.uiState {
                    vm.state = state
                }
            }
    }
}"""
    l9['hints'] = [
        "In plugins block, add: <code>alias(libs.plugins.skie)</code>.",
        "SKIE automatically enhances Kotlin/Native Objective-C exports for Swift.",
        "It transforms StateFlow into Swift AsyncSequence and sealed interfaces into Swift enums."
    ]

    # Lesson 10: Ktor 3 Client
    l10 = gen_phase1_to_3_remaining.lessons[4]
    l10['q1']['code_snippet'] = """val client = HttpClient() // No ContentNegotiation installed!

suspend fun fetchAlert(): AlertDto {
    // Throws NoTransformationFoundException at runtime!
    return client.get("https://api.incident.com/alert").body()
}"""
    l10['hints'] = [
        "Call HttpClient { ... } builder.",
        "Install ContentNegotiation: <code>install(ContentNegotiation) { json() }</code>.",
        "This attaches the kotlinx.serialization JSON serializer to the HTTP pipeline."
    ]

    # Lesson 11: Kotlinx Serialization
    l11 = gen_phase1_to_3_remaining.lessons[5]
    l11['q1']['code_snippet'] = """@Serializable
data class AlertPayload(val id: String, val level: String)

// Server payload: {"id": "1", "level": "HIGH", "extraField": 123}
val json = Json { ignoreUnknownKeys = false } // Will throw UnknownKeyException!"""
    l11['hints'] = [
        "Annotate data class UserSession with <code>@Serializable</code>.",
        "Configure Json: <code>val appJson = Json { ignoreUnknownKeys = true }</code>.",
        "Setting ignoreUnknownKeys = true prevents client crashes when backend adds new fields."
    ]

    # Lesson 12: Network Resilience & Auth
    l12 = gen_phase1_to_3_remaining.lessons[6]
    l12['q1']['code_snippet'] = """sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>
}"""
    l12['hints'] = [
        "Declare sealed interface NetworkError.",
        "Add data class Http(val code: Int) : NetworkError.",
        "Add data object Connectivity : NetworkError.",
        "Add data object Unauthorized : NetworkError."
    ]

    # ==========================================
    # Phase 4, 5, 6: Lessons 13 - 22
    # ==========================================
    # Lesson 13: Room vs SQLDelight
    l13 = gen_phase4_to_6.lessons[0]
    l13['q1']['code_snippet'] = """-- SQLDelight file: Incident.sq
CREATE TABLE incidentEntity (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL
);
selectById:
SELECT * FROM incidentEntity WHERE id = :id;"""
    l13['hints'] = [
        "Define enum class StorageEngine { ROOM, SQLDELIGHT }.",
        "Room is annotation-driven with full Jetpack ecosystem parity.",
        "SQLDelight is schema-first and generates type-safe Kotlin from pure SQL queries."
    ]

    # Lesson 14: Jetpack Room KMP
    l14 = gen_phase4_to_6.lessons[1]
    l14['q1']['code_snippet'] = """@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): List<AlertEntity> // Notice non-Flow return!
}"""
    l14['hints'] = [
        "Annotate data class with <code>@Entity(tableName = \"alerts\")</code>.",
        "Mark the id property with <code>@PrimaryKey val id: String</code>.",
        "Annotate interface AlertDao with <code>@Dao</code> and write <code>@Query(\"SELECT * FROM alerts\") fun getAlerts(): Flow<List<AlertEntity>></code>."
    ]

    # Lesson 15: Key-Value Settings
    l15 = gen_phase4_to_6.lessons[2]
    l15['q1']['code_snippet'] = """val settings = Settings()
// Storing unencrypted access token in standard UserDefaults / SharedPreferences!
settings.putString("jwt_token", secretJwt)"""
    l15['hints'] = [
        "Wrap Settings instance in a repository: <code>class PreferencesRepository(private val settings: Settings)</code>.",
        "Implement storeUserId: <code>settings.putString(\"user_id\", id)</code>.",
        "Implement getUserId: <code>return settings.getStringOrNull(\"user_id\")</code>."
    ]

    # Lesson 16: Offline-First Repository
    l16 = gen_phase4_to_6.lessons[3]
    l16['q1']['code_snippet'] = """// Single Source of Truth Flow
fun streamIncidents(): Flow<List<Incident>> = dao.observeAll().map { entities ->
    entities.map { it.toDomain() }
}"""
    l16['hints'] = [
        "Call flow.map: <code>return source.map { list -> list.map { it.uppercase() } }</code>.",
        "In offline-first, local database acts as Single Source of Truth (SSOT).",
        "Network operations update the database; the UI only observes database flows."
    ]

    # Lesson 17: DI with Koin
    l17 = gen_phase4_to_6.lessons[4]
    l17['q1']['code_snippet'] = """val appModule = module {
    single { HttpClient() }
    single<IncidentRepository> { IncidentRepositoryImpl(get()) }
    viewModel { IncidentViewModel(get()) }
}"""
    l17['hints'] = [
        "Use module { ... } DSL from org.koin.dsl.module.",
        "Register singleton: <code>single { \"KoinService\" }</code>.",
        "In Compose Multiplatform, inject ViewModels using <code>koinViewModel()</code>."
    ]

    # Lesson 18: Compile-Time DI with Kotlin-Inject
    l18 = gen_phase4_to_6.lessons[5]
    l18['q1']['code_snippet'] = """@Component
abstract class NetworkComponent {
    abstract val client: HttpClient

    @Provides
    fun provideClient(): HttpClient = HttpClient()
}"""
    l18['hints'] = [
        "Annotate class with <code>@Component</code>.",
        "Declare abstract property: <code>abstract val serviceName: String</code>.",
        "Provide implementation with <code>@Provides fun provideName(): String = \"KotlinInject\"</code>."
    ]

    # Lesson 19: Compose Multiplatform Pipeline
    l19 = gen_phase4_to_6.lessons[6]
    l19['q1']['code_snippet'] = """// iosMain: ComposeUIViewController hosts Skia canvas inside UIKit
fun MainViewController() = ComposeUIViewController {
    MaterialTheme {
        IncidentApp()
    }
}"""
    l19['hints'] = [
        "Declare <code>@Composable fun IncidentBadge(status: String)</code>.",
        "Use Box or Column modifier: <code>Box(modifier = Modifier.padding(16.dp))</code>.",
        "Add Text(text = status) inside the layout."
    ]

    # Lesson 20: Navigation in Compose
    l20 = gen_phase4_to_6.lessons[7]
    l20['q1']['code_snippet'] = """@Serializable
sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data class Detail(val incidentId: String) : Route
}

NavHost(navController, startDestination = Route.Home) {
    composable<Route.Home> { ... }
    composable<Route.Detail> { entry ->
        val route: Route.Detail = entry.toRoute()
    }
}"""
    l20['hints'] = [
        "Declare sealed interface AppRoute.",
        "Annotate data object Home : AppRoute with <code>@Serializable</code>.",
        "Annotate data class Profile(val userId: String) : AppRoute with <code>@Serializable</code>."
    ]

    # Lesson 21: Design Systems & Adaptivity
    l21 = gen_phase4_to_6.lessons[8]
    l21['q1']['code_snippet'] = """val isDark = isSystemInDarkTheme()
val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

MaterialTheme(colorScheme = colorScheme) {
    // Shared UI adapts to system appearance on Android and iOS
}"""
    l21['hints'] = [
        "Define primary Color: <code>val primaryBlue = Color(0xFF1E88E5)</code>.",
        "Call lightColorScheme: <code>val appColors = lightColorScheme(primary = primaryBlue)</code>.",
        "Pass appColors into MaterialTheme(colorScheme = appColors) { ... }."
    ]

    # Lesson 22: Image Loading Coil 3
    l22 = gen_phase4_to_6.lessons[9]
    l22['q1']['code_snippet'] = """// Coil 3 Multiplatform AsyncImage in commonMain
AsyncImage(
    model = "https://images.example.com/incident.png",
    contentDescription = stringResource(Res.string.incident_photo),
    modifier = Modifier.size(120.dp)
)"""
    l22['hints'] = [
        "Define @Composable fun IncidentIcon(url: String).",
        "Use AsyncImage with model parameter: <code>AsyncImage(model = url, contentDescription = null)</code>.",
        "In Compose Multiplatform, resources are accessed via Res.drawable and Res.string."
    ]

    # ==========================================
    # Phase 7, 8, 9: Lessons 23 - 31
    # ==========================================
    # Lesson 23: Embedding Compose in iOS
    l23 = gen_phase7_to_9.lessons[0]
    l23['q1']['code_snippet'] = """// SwiftUI Host embedding Compose
struct ContentView: View {
    var body: some View {
        ComposeViewControllerRepresentable()
            .ignoresSafeArea(.keyboard) // Handle iOS keyboard insets
    }
}"""
    l23['hints'] = [
        "In iosMain, write: <code>fun MainViewController() = ComposeUIViewController { Text(\"Shared iOS View\") }</code>.",
        "ComposeUIViewController bridges Compose layout tree onto Apple Skia layer.",
        "In Swift, embed this via UIViewControllerRepresentable."
    ]

    # Lesson 24: Hosting UIKit in Compose
    l24 = gen_phase7_to_9.lessons[1]
    l24['q1']['code_snippet'] = """// commonMain
@Composable
expect fun PlatformMapView(modifier: Modifier = Modifier)

// iosMain UIKitView interop
@Composable
actual fun PlatformMapView(modifier: Modifier) {
    UIKitView(factory = { MKMapView() }, modifier = modifier)
}"""
    l24['hints'] = [
        "Declare expect function in commonMain: <code>@Composable expect fun NativeMapView()</code>.",
        "In iosMain, implement using UIKitView factory: <code>UIKitView(factory = { MKMapView() })</code>.",
        "On Android, implement actual using AndroidView factory: <code>AndroidView(factory = { MapView(it) })</code>."
    ]

    # Lesson 25: Dual-UI Architecture
    l25 = gen_phase7_to_9.lessons[2]
    l25['q1']['code_snippet'] = """// Shared KMP ViewModel bound to SwiftUI
struct IncidentSwiftUIView: View {
    @ObservedObject var viewModel: ObservableIncidentViewModel
    var body: some View {
        List(viewModel.state.incidents, id: \\.id) { incident in
            Text(incident.title)
        }
    }
}"""
    l25['hints'] = [
        "Declare data class PassiveUiState(val title: String, val count: Int).",
        "Passive state structures contain zero platform dependencies.",
        "SwiftUI binds to this via SKIE @Observable / AsyncSequence."
    ]

    # Lesson 26: Multiplatform Unit Testing
    l26 = gen_phase7_to_9.lessons[3]
    l26['q1']['code_snippet'] = """// commonTest/src/IncidentTest.kt
import kotlin.test.*

class IncidentTest {
    @Test
    fun testSeverityPriority() {
        assertTrue(Severity.CRITICAL > Severity.LOW)
    }
}"""
    l26['hints'] = [
        "Annotate test method with <code>@Test</code> from kotlin.test.",
        "Use <code>assertEquals(4, 2 + 2)</code>.",
        "commonTest runs identically on JVM (Gradle testDebugUnitTest) and iOS (iosSimulatorArm64Test)."
    ]

    # Lesson 27: Turbine Flow Testing
    l27 = gen_phase7_to_9.lessons[4]
    l27['q1']['code_snippet'] = """@Test
fun testIncidentStream() = runTest {
    val flow = flowOf("Alert 1", "Alert 2")
    flow.test {
        assertEquals("Alert 1", awaitItem())
        // Ends here without awaiting second item -> Turbine throws AssertionError!
    }
}"""
    l27['hints'] = [
        "Wrap test in runTest: <code>@Test fun testFlow() = runTest { ... }</code>.",
        "Call flow.test { ... } from app.cash.turbine.test.",
        "Consume items: <code>assertEquals(10, awaitItem())</code>, then assert completion with <code>awaitComplete()</code>."
    ]

    # Lesson 28: Mocking with Fakes & MockEngine
    l28 = gen_phase7_to_9.lessons[5]
    l28['q1']['code_snippet'] = """val mockEngine = MockEngine { request ->
    if (request.url.encodedPath.contains("incidents")) {
        respond(
            content = ByteReadChannel(\"\"\"[{"id":"1"}]\"\"\"),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    } else {
        respondError(HttpStatusCode.NotFound)
    }
}"""
    l28['hints'] = [
        "Instantiate MockEngine: <code>val engine = MockEngine { request -> ... }</code>.",
        "Return mock response: <code>respond(content = \"OK\", status = HttpStatusCode.OK)</code>.",
        "MockEngine works across JVM and Kotlin/Native, avoiding reflection-based Mockito crashes."
    ]

    # Lesson 29: Compose UI Testing
    l29 = gen_phase7_to_9.lessons[6]
    l29['q1']['code_snippet'] = """@Test
fun testResolveIncidentButton() = runComposeUiTest {
    setContent { IncidentCard(title = "Outage", onResolve = {}) }
    onNodeWithText("Resolve").performClick()
    onNodeWithText("Resolved").assertIsDisplayed()
}"""
    l29['hints'] = [
        "In test scope, find element: <code>onNodeWithText(\"Submit\")</code>.",
        "Trigger user interaction: <code>.performClick()</code>.",
        "Assert presence on semantics tree: <code>.assertExists()</code>."
    ]

    # Lesson 30: Gradle Build Optimization
    l30 = gen_phase7_to_9.lessons[7]
    l30['q1']['code_snippet'] = """# gradle.properties
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true
kotlin.daemon.jvmargs=-Xmx4096m"""
    l30['hints'] = [
        "Enable build cache: <code>org.gradle.caching=true</code>.",
        "Enable configuration cache: <code>org.gradle.configuration-cache=true</code>.",
        "Enable parallel project execution: <code>org.gradle.parallel=true</code>."
    ]

    # Lesson 31: GitHub Actions CI
    l31 = gen_phase7_to_9.lessons[8]
    l31['q1']['code_snippet'] = """jobs:
  build:
    strategy:
      matrix:
        os: [ubuntu-latest, macos-latest]
    runs-on: ${{ matrix.os }}"""
    # Fix starter code in Lesson 31 so it is NOT identical to final solution
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

    # ==========================================
    # Re-generate all lessons cleanly!
    # ==========================================
    for l in gen_phase0_to_3.lessons:
        generate_lesson_file(l)
    for l in gen_phase1_to_3_remaining.lessons:
        generate_lesson_file(l)
    for l in gen_phase4_to_6.lessons:
        generate_lesson_file(l)
    for l in gen_phase7_to_9.lessons:
        generate_lesson_file(l)

    print("All 30 generator lessons enriched and re-generated successfully!")

if __name__ == "__main__":
    enrich()
