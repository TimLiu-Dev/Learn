import sys
import os
sys.path.append(os.path.dirname(__file__))
from generator_base import generate_lesson_file

lessons = [
    # Lesson 23
    {
        "num": 23,
        "slug": "embedding-compose-in-existing-ios-apps",
        "phase_tag": "Phase 7",
        "title": "Embedding Compose Multiplatform into Existing iOS Apps",
        "subtitle": "Export Compose screens as native UIViewController instances via ComposeUIViewController for incremental migration.",
        "prev_slug": "0022-image-loading-coil3-resources",
        "next_slug": "0024-hosting-native-uikit-in-compose",
        "ref_slug": "23-compose-in-swiftui",
        "ref_title": "ComposeUIViewController & SwiftUI Interop",
        "mental_model": """
        <p>Enterprise mobile teams rarely rewrite an existing iOS app from scratch. Instead, they migrate screen-by-screen. Compose Multiplatform provides <code>ComposeUIViewController</code> to facilitate this:</p>
        <ul>
          <li>In <code>iosMain</code>, we declare a top-level factory function: <code>fun MainViewController(): UIViewController = ComposeUIViewController { MainApp() }</code>.</li>
          <li>In Swift/SwiftUI, we wrap this inside a <code>UIViewControllerRepresentable</code> struct.</li>
          <li>The existing iOS app can present, push, or embed the Compose screen into any native navigation controller or SwiftUI tab view seamlessly.</li>
        </ul>
        """,
        "production_code": """
        <h3>1. Kotlin Entry Point in <code>iosMain</code></h3>
        <pre><code>package com.pulsesync

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App() // Root Composable in commonMain
}</code></pre>

        <h3>2. Embedding in SwiftUI (Xcode)</h3>
        <pre><code>import SwiftUI
import SharedKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea() // Allow Compose to manage insets
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: SwiftUI Safe Area Double-Padding</div>
          If your SwiftUI container applies <code>.padding()</code> or respects safe areas, and your Compose screen *also* applies <code>WindowInsets.safeDrawing</code>, your UI will be double-padded on iOS. Use <code>.ignoresSafeArea()</code> in SwiftUI so Compose has full control over edge-to-edge drawing.
        </div>
        """,
        "code_title": "Declare MainViewController in iosMain",
        "code_filename": "MainViewController.kt",
        "code_instructions": "Declare a function <code>fun createMainViewController(content: @Composable () -> Unit): UIViewController</code> returning a <code>ComposeUIViewController { content() }</code>.",
        "starter_code": """import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// TODO: Implement createMainViewController(content: @Composable () -> Unit): UIViewController
fun createMainViewController(content: @Composable () -> Unit): UIViewController {
    return ComposeUIViewController { content() }
}""",
        "hints": [
            "Use: ComposeUIViewController { content() }"
        ],
        "final_solution": """import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun createMainViewController(content: @Composable () -> Unit): UIViewController {
    return ComposeUIViewController { content() }
}""",
        "solution_explanation": "ComposeUIViewController embeds the Skia Metal rendering layer inside a standard UIKit UIViewController lifecycle.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasFn = /fun\s+createMainViewController[\s\S]*?ComposeUIViewController\s*\{\s*content\(\)\s*\}/.test(code);

          print("Validating iOS UIViewController factory...");
          if (hasFn) print("✔ ComposeUIViewController wrapper verified.");

          tests.push({
            name: "createMainViewController wraps Composable in ComposeUIViewController",
            passed: hasFn,
            message: "Must return ComposeUIViewController { content() }."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Which SwiftUI protocol allows embedding an exported Kotlin UIViewController directly into a SwiftUI view hierarchy?",
            "options": [
                "UIViewControllerRepresentable which creates and updates a native UIKit view controller inside SwiftUI.",
                "UIViewRepresentable which wraps individual primitive UIKit views like UILabel or UIButton.",
                "SwiftUIMultiplatformBridge which converts Kotlin bytecode into SwiftUI declarative struct syntax.",
                "ObservableObject which publishes change notifications to listening SwiftUI view bodies."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "UIViewControllerRepresentable is Apple's standard protocol for bridging any UIViewController (including ComposeUIViewController) into SwiftUI.",
                "whyWrong": [
                    {"letter": "B", "reason": "UIViewRepresentable is for individual UIViews, not view controllers."},
                    {"letter": "C", "reason": "There is no such protocol as SwiftUIMultiplatformBridge."},
                    {"letter": "D", "reason": "ObservableObject is for state management, not view hosting."}
                ]
            }
        },
        "q2": {
            "question": "Why is .ignoresSafeArea() commonly applied to the SwiftUI view wrapping ComposeUIViewController?",
            "options": [
                "It allows Compose to handle edge-to-edge drawing and avoid double-padding with WindowInsets.",
                "It forces iOS devices to disable all touch interactions along the top status bar area.",
                "It prevents the Apple App Store from inspecting UI layout trees during security audits.",
                "It converts all Compose text elements into native iOS Dynamic Type scalable vectors."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "If SwiftUI applies safe area padding, and Compose also applies WindowInsets.safeDrawing, the content gets padded twice. Ignoring safe areas in SwiftUI lets Compose manage edge-to-edge layouts cleanly.",
                "whyWrong": [
                    {"letter": "B", "reason": "It does not disable touch interactions."},
                    {"letter": "C", "reason": "Safe areas have nothing to do with security audits."},
                    {"letter": "D", "reason": "Dynamic Type is configured inside Compose typography."}
                ]
            }
        },
        "source_title": "JetBrains Compose iOS Guide",
        "source_url": "https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-ios-ui.html",
        "source_link_text": "Compose Multiplatform for iOS",
        "source_note": "Official guide on embedding Compose in iOS applications.",
        "ref_content": """
        <h2>iOS Host Pattern</h2>
        <pre><code>// Kotlin: iosMain
fun MainViewController() = ComposeUIViewController { App() }

// Swift: SwiftUI
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}</code></pre>
        """
    },

    # Lesson 24
    {
        "num": 24,
        "slug": "hosting-native-uikit-in-compose",
        "phase_tag": "Phase 7",
        "title": "Hosting Native UIKit & SwiftUI Views inside Compose",
        "subtitle": "Use UIKitView to embed native Apple Maps, camera previews, and Apple Pay buttons inside shared Compose screens.",
        "prev_slug": "0023-embedding-compose-in-existing-ios-apps",
        "next_slug": "0025-dual-ui-shared-logic-native-swiftui",
        "ref_slug": "24-uikitview-in-compose",
        "ref_title": "UIKitView & Native Platform View Interop",
        "mental_model": """
        <p>While Compose Multiplatform renders almost everything with Skia, certain features require <strong>real platform-native views</strong>:</p>
        <ul>
          <li><strong>Apple Maps</strong> (<code>MKMapView</code>) for native 3D vector map rendering and Apple MapKit routing.</li>
          <li><strong>Camera Previews</strong> (<code>AVCaptureVideoPreviewLayer</code>).</li>
          <li><strong>Apple Pay Buttons</strong> (<code>PKPaymentButton</code>) mandated by App Store guidelines.</li>
        </ul>
        <p>In <code>iosMain</code>, Compose provides <strong><code>UIKitView</code></strong>, which inserts a real native <code>UIView</code> into the Compose layout hierarchy, tracking position, clipping, and touch dispatching.</p>
        """,
        "production_code": """
        <h3>Embedding Apple MKMapView in <code>iosMain</code></h3>
        <pre><code>package com.pulsesync.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.MapKit.MKMapView

@Composable
actual fun PlatformMapView(modifier: Modifier) {
    UIKitView(
        factory = {
            MKMapView().apply {
                showsUserLocation = true
            }
        },
        modifier = modifier,
        update = { mapView ->
            // Update map state on recomposition
        }
    )
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Performance Cost of Excessive UIKitViews</div>
          Each <code>UIKitView</code> requires synchronizing the Skia Metal graphics pipeline with the UIKit CoreAnimation layer tree. Avoid scattering dozens of tiny UIKitViews inside a scrolling list. Use UIKitView only for complex hardware/system views like Maps or Camera.
        </div>
        """,
        "code_title": "Declare an Expect/Actual Platform Map Composable",
        "code_filename": "PlatformMap.kt",
        "code_instructions": "In commonMain, declare an expected composable function <code>expect fun PlatformMapView(modifier: Modifier)</code>.",
        "starter_code": """import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// TODO: Declare expect @Composable fun PlatformMapView(modifier: Modifier)""",
        "hints": [
            "@Composable expect fun PlatformMapView(modifier: Modifier)"
        ],
        "final_solution": """import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformMapView(modifier: Modifier)""",
        "solution_explanation": "Declaring the composable contract in commonMain allows shared screens to place PlatformMapView into the layout, fulfilled by UIKitView on iOS and Google MapView on Android.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasExpect = /@Composable\s+expect\s+fun\s+PlatformMapView\s*\(\s*modifier\s*:\s*Modifier\s*\)/.test(code);

          print("Validating expected platform map composable...");
          if (hasExpect) print("✔ Expected PlatformMapView signature verified.");

          tests.push({
            name: "Declared @Composable expect fun PlatformMapView(modifier: Modifier)",
            passed: hasExpect,
            message: "Must declare '@Composable expect fun PlatformMapView(modifier: Modifier)'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Which Compose Multiplatform API hosts a native Apple UIView inside a Composable layout on iOS?",
            "options": [
                "UIKitView which creates and synchronizes a native UIView within the Compose layer tree.",
                "AndroidView which embeds native Android View hierarchies inside Jetpack Compose.",
                "MetalSurfaceView which directly renders raw OpenGL fragment shaders to the screen.",
                "SwiftUIViewContainer which recompiles Swift source code inside the Kotlin runtime."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "UIKitView is Compose Multiplatform's dedicated bridge for iOS, analogous to AndroidView on Android, allowing any UIView (like MKMapView) to live inside Compose.",
                "whyWrong": [
                    {"letter": "B", "reason": "AndroidView is Android-specific and cannot compile on iOS."},
                    {"letter": "C", "reason": "MetalSurfaceView does not host UIKit views."},
                    {"letter": "D", "reason": "Swift source is compiled by Xcode, not by a runtime container."}
                ]
            }
        },
        "q2": {
            "question": "When should you reach for UIKitView instead of writing a pure Compose composable?",
            "options": [
                "When integrating proprietary platform views like Apple Maps, Camera preview, or Apple Pay buttons.",
                "Whenever creating simple standard buttons, text fields, or vertical scrolling lists.",
                "To optimize drawing performance when rendering thousands of vector icon glyphs.",
                "To bypass Apple App Store privacy policies regarding network permission prompts."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "UIKitView is designed specifically for complex hardware/proprietary views provided by the operating system (e.g. MapKit, AVFoundation Camera, PassKit Apple Pay) that cannot be drawn with standard Canvas APIs.",
                "whyWrong": [
                    {"letter": "B", "reason": "Standard buttons and lists should use pure Compose for performance and simplicity."},
                    {"letter": "C", "reason": "Compose renders vector glyphs faster than bridging to UIKit."},
                    {"letter": "D", "reason": "App Store privacy policies apply to all views equally."}
                ]
            }
        },
        "source_title": "JetBrains Compose Interop Guide",
        "source_url": "https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-uikit-integration.html",
        "source_link_text": "UIKit Integration with Compose Multiplatform",
        "source_note": "Official guide on using UIKitView and handling touch propagation.",
        "ref_content": """
        <h2>UIKitView Usage Recipe</h2>
        <pre><code>UIKitView(
    factory = { MKMapView() },
    modifier = Modifier.fillMaxSize(),
    update = { view -> /* sync state */ }
)</code></pre>
        """
    },

    # Lesson 25
    {
        "num": 25,
        "slug": "dual-ui-shared-logic-native-swiftui",
        "phase_tag": "Phase 7",
        "title": "The Dual-UI Architecture: Shared KMP Logic + Native SwiftUI",
        "subtitle": "Master the enterprise hybrid architecture: sharing 100% of Data, Domain, and ViewModels while building 100% native SwiftUI and Jetpack Compose.",
        "prev_slug": "0024-hosting-native-uikit-in-compose",
        "next_slug": "0026-multiplatform-unit-testing",
        "ref_slug": "25-shared-logic-native-swiftui",
        "ref_title": "Dual-UI Architecture: Shared Logic with Native Frontends",
        "mental_model": """
        <p>Many top tech companies (e.g. Netflix, Cash App, Philips, 9GAG) do not share UI code with Compose. Instead, they choose the <strong>Dual-UI Architecture</strong>:</p>
        <ul>
          <li><strong>Shared in KMP (commonMain)</strong>: 100% of Networking (Ktor), Database (Room/SQLDelight), Domain Business Rules, and <strong>ViewModels (StateFlow / MVI state machines)</strong>.</li>
          <li><strong>Native Frontends</strong>:
            <ul>
              <li><strong>Android</strong>: Built with <strong>Jetpack Compose</strong>.</li>
              <li><strong>iOS</strong>: Built with <strong>100% Native SwiftUI</strong> consuming shared Kotlin ViewModels via SKIE.</li>
            </ul>
          </li>
        </ul>
        <p>This gives iOS developers 100% native Apple UX, Cupertino animations, and immediate Day-1 iOS SDK support, while eliminating 70% of business logic duplication.</p>
        """,
        "production_code": """
        <h3>1. Shared ViewModel in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.presentation

import kotlinx.coroutines.flow.*

class SharedIncidentViewModel(private val repository: IncidentRepository) {
    val uiState: StateFlow<IncidentListState> = repository.observeIncidents()
        .map { list -> IncidentListState(incidents = list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IncidentListState())
}</code></pre>

        <h3>2. Pure SwiftUI View Consuming Shared ViewModel via SKIE</h3>
        <pre><code>import SwiftUI
import SharedKit

struct NativeIncidentListView: View {
    @StateObject var viewModel = ObservableSharedIncidentViewModel()

    var body: some View {
        NavigationStack {
            List(viewModel.uiState.incidents, id: \\.id) { incident in
                VStack(alignment: .leading) {
                    Text(incident.title).font(.headline)
                    Text("Severity: \\(incident.severity)").font(.subheadline).foregroundStyle(.secondary)
                }
            }
            .navigationTitle("Incidents")
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Re-implementing Business Validation in SwiftUI</div>
          In the Dual-UI pattern, the SwiftUI layer must be a <strong>passive rendering shell</strong>. Never write business calculations or validation logic in Swift. All validation, state transformations, and caching must remain inside the shared Kotlin ViewModel.
        </div>
        """,
        "code_title": "Define Passive View State Contract for Dual-UI",
        "code_filename": "PassiveState.kt",
        "code_instructions": "Define a data class <code>PassiveUiState(val title: String, val canSubmit: Boolean)</code> that a SwiftUI view can bind directly to UI controls.",
        "starter_code": """// TODO: Define data class PassiveUiState(val title: String, val canSubmit: Boolean)""",
        "hints": [
            "data class PassiveUiState(val title: String, val canSubmit: Boolean)"
        ],
        "final_solution": """data class PassiveUiState(val title: String, val canSubmit: Boolean)""",
        "solution_explanation": "A clean, immutable state object allows SwiftUI views to bind directly to declarative controls (e.g. .disabled(!state.canSubmit)) without business logic.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasState = /data\s+class\s+PassiveUiState\s*\(\s*val\s+title\s*:\s*String\s*,\s*val\s+canSubmit\s*:\s*Boolean\s*\)/.test(code);

          print("Validating passive UI state data class...");
          if (hasState) print("✔ PassiveUiState declared with title and canSubmit.");

          tests.push({
            name: "PassiveUiState declared with title: String and canSubmit: Boolean",
            passed: hasState,
            message: "Must declare 'data class PassiveUiState(val title: String, val canSubmit: Boolean)'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary motivation for an enterprise mobile team to adopt the Dual-UI architecture over 100% Compose Multiplatform?",
            "options": [
                "It enables iOS developers to use native SwiftUI, native Apple animations, and Cupertino aesthetics.",
                "It eliminates the need to compile Kotlin code when releasing updates to the Apple App Store.",
                "It allows Android applications to execute Swift source code on Google Pixel devices.",
                "It prevents mobile applications from requiring local SQLite database persistence on disk."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Dual-UI lets teams share 70-80% of their stack (Domain, Data, ViewModels) while giving iOS developers the freedom to build pure native SwiftUI frontends that feel completely at home on Apple devices.",
                "whyWrong": [
                    {"letter": "B", "reason": "KMP shared logic is still compiled by Kotlin/Native into an XCFramework."},
                    {"letter": "C", "reason": "Swift does not run on Android in standard mobile development."},
                    {"letter": "D", "reason": "Dual-UI applications still use Room/SQLDelight for local caching."}
                ]
            }
        },
        "q2": {
            "question": "In the Dual-UI architecture, where should input validation (e.g., verifying an email format) live?",
            "options": [
                "Inside the shared Kotlin ViewModel or Domain Use Case in commonMain.",
                "Duplicated independently inside the SwiftUI view and the Android Composable screen.",
                "Inside an external remote microservice invoked via an HTTP round-trip on every keystroke.",
                "Inside the Apple Keychain security enclave using asymmetric cryptographic keys."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "All business logic, including validation, belongs in shared Kotlin code. The native UI simply observes `state.isValidEmail` and renders feedback, eliminating duplicated bug-prone logic.",
                "whyWrong": [
                    {"letter": "B", "reason": "Duplicating validation across Swift and Kotlin defeats the purpose of KMP."},
                    {"letter": "C", "reason": "Network round-trips for basic formatting create poor, laggy UX."},
                    {"letter": "D", "reason": "Keychain is for credential storage, not string validation."}
                ]
            }
        },
        "source_title": "Netflix Technology Blog",
        "source_url": "https://netflixtechblog.com/",
        "source_link_text": "Kotlin Multiplatform at Netflix",
        "source_note": "Case study on sharing business logic, networking, and state with native mobile UIs.",
        "ref_content": """
        <h2>Dual-UI Architectural Split</h2>
        <ul>
          <li><strong>Shared (commonMain)</strong>: Ktor, Room, Domain Use Cases, MVI ViewModels (StateFlow).</li>
          <li><strong>Android UI</strong>: Jetpack Compose.</li>
          <li><strong>iOS UI</strong>: Native SwiftUI (observing StateFlow via SKIE).</li>
        </ul>
        """
    },

    # Lesson 26
    {
        "num": 26,
        "slug": "multiplatform-unit-testing",
        "phase_tag": "Phase 8",
        "title": "Multiplatform Unit Testing with kotlin.test",
        "subtitle": "Write deterministic unit tests for ViewModels, Use Cases, and Repositories running across JVM and Kotlin/Native runners.",
        "prev_slug": "0025-dual-ui-shared-logic-native-swiftui",
        "next_slug": "0027-testing-flows-coroutines-turbine",
        "ref_slug": "26-kmp-unit-testing",
        "ref_title": "Multiplatform Unit Testing with kotlin.test Reference",
        "mental_model": """
        <p>Testing in Kotlin Multiplatform is powered by <code>kotlin.test</code>, which unifies test assertions across backends:</p>
        <ul>
          <li>On the <strong>JVM (Android)</strong>, <code>kotlin.test</code> delegates to JUnit.</li>
          <li>On <strong>Kotlin/Native (iOS)</strong>, <code>kotlin.test</code> delegates to Apple's native test runner.</li>
        </ul>
        <p>Tests are placed in <code>commonTest</code>. Running <code>./gradlew check</code> or <code>./gradlew allTests</code> executes your test suite across both the JVM and native iOS simulator targets.</p>
        """,
        "production_code": """
        <h3>Unit Testing a Use Case in <code>commonTest</code></h3>
        <pre><code>package com.pulsesync.domain

import kotlin.test.*
import kotlinx.coroutines.test.runTest

class GetIncidentsUseCaseTest {

    // Test Fake (Zero Mocking framework needed)
    private val fakeRepository = object : IncidentRepository {
        val testData = mutableListOf("Server Outage", "Database Latency")
        override suspend fun getActiveIncidents() = testData
    }

    private val useCase = GetIncidentsUseCase(fakeRepository)

    @Test
    fun shouldReturnActiveIncidentsFromRepository() = runTest {
        val result = useCase()
        assertEquals(2, result.size)
        assertEquals("Server Outage", result[0])
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Using Mockito or MockK in commonTest</div>
          Libraries like Mockito and MockK rely on dynamic JVM bytecode generation (cglib/bytebuddy). They cannot compile on Kotlin/Native (iOS). Instead, use <strong>Test Fakes</strong> (clean interface implementations) or multiplatform mocking libraries (Mockative).
        </div>
        """,
        "code_title": "Write a kotlin.test Test Method",
        "code_filename": "CalculatorTest.kt",
        "code_instructions": "Write a test class <code>CalculatorTest</code> with a method <code>@Test fun testAddition()</code> that asserts <code>assertEquals(4, 2 + 2)</code>.",
        "starter_code": """import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    // TODO: Write @Test fun testAddition() asserting assertEquals(4, 2 + 2)
}""",
        "hints": [
            "@Test fun testAddition() { assertEquals(4, 2 + 2) }"
        ],
        "final_solution": """import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    @Test
    fun testAddition() {
        assertEquals(4, 2 + 2)
    }
}""",
        "solution_explanation": "The kotlin.test framework provides universal assertions (assertEquals, assertTrue, assertNull) that run seamlessly across Android and Apple test runners.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasTest = /@Test[\s\S]*?fun\s+testAddition\(\s*\)[\s\S]*?assertEquals\(\s*4\s*,\s*2\s*\+\s*2\s*\)/.test(code);

          print("Verifying multiplatform unit test...");
          if (hasTest) print("✔ @Test annotated testAddition with assertEquals verified.");

          tests.push({
            name: "testAddition declared with @Test and assertEquals(4, 2 + 2)",
            passed: hasTest,
            message: "Must define @Test fun testAddition() containing assertEquals(4, 2 + 2)."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why can popular JVM mocking libraries like Mockito and MockK not be used in commonTest?",
            "options": [
                "They depend on JVM runtime bytecode generation which is not available in Kotlin/Native on iOS.",
                "Apple App Store policies strictly prohibit any application with unit test dependencies.",
                "The Gradle check task halts if a testing library contains more than three annotations.",
                "Kotlin 2.x compiler flags permanently disable all interface inheritance in test directories."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Mockito and MockK generate JVM bytecode at runtime using Java reflection and classloaders. Kotlin/Native compiles to native machine code via LLVM and has no JVM runtime, making these libraries incompatible.",
                "whyWrong": [
                    {"letter": "B", "reason": "App Store review does not inspect test code."},
                    {"letter": "C", "reason": "Gradle does not restrict annotation counts."},
                    {"letter": "D", "reason": "Interface inheritance works identically in tests."}
                ]
            }
        },
        "q2": {
            "question": "Which coroutine test builder should be used to test suspending functions deterministically in commonTest?",
            "options": [
                "runTest { } from kotlinx-coroutines-test which skips virtual delays and controls test dispatchers.",
                "runBlocking { } which blocks the thread and forces real-time delays on background workers.",
                "GlobalScope.launch { } which fires an un-monitored fire-and-forget background coroutine.",
                "Thread.sleep() which halts the CPU for a hardcoded number of milliseconds."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "runTest automatically uses a StandardTestDispatcher, advancing virtual time automatically so `delay(5000)` executes instantaneously in unit tests.",
                "whyWrong": [
                    {"letter": "B", "reason": "runBlocking does not skip delays; delay(5000) would make your test wait 5 real seconds."},
                    {"letter": "C", "reason": "GlobalScope does not block or await completion in unit tests."},
                    {"letter": "D", "reason": "Thread.sleep is flaky, slow, and not available in commonMain."}
                ]
            }
        },
        "source_title": "Kotlin Test Documentation",
        "source_url": "https://kotlinlang.org/docs/kmp-test-common-code.html",
        "source_link_text": "Testing Multiplatform Code",
        "source_note": "Official guide on writing cross-platform tests using kotlin.test.",
        "ref_content": """
        <h2>CLI Testing Commands</h2>
        <ul>
          <li><code>./gradlew allTests</code>: Runs tests across all targets.</li>
          <li><code>./gradlew jvmTest</code> / <code>./gradlew testDebugUnitTest</code>: Android/JVM tests.</li>
          <li><code>./gradlew iosSimulatorArm64Test</code>: iOS Simulator native tests.</li>
        </ul>
        """
    },

    # Lesson 27
    {
        "num": 27,
        "slug": "testing-flows-coroutines-turbine",
        "phase_tag": "Phase 8",
        "title": "Testing Flows & Coroutines with Turbine",
        "subtitle": "Test reactive StateFlow emissions, debounce delays, and error states deterministically with Cash App's Turbine.",
        "prev_slug": "0026-multiplatform-unit-testing",
        "next_slug": "0028-mocking-multiplatform-fakes-vs-mockative",
        "ref_slug": "27-turbine-flow-testing",
        "ref_title": "Turbine Flow Testing Reference & Patterns",
        "mental_model": """
        <p>Testing asynchronous <code>Flow</code> streams by collecting into a list can lead to race conditions, hanging tests, or missing emissions. <strong>Turbine (by Cash App)</strong> solves this with a clean, sequential testing DSL:</p>
        <pre><code>flow.test {
    assertEquals(Item1, awaitItem())
    assertEquals(Item2, awaitItem())
    awaitComplete()
}</code></pre>
        <p>Turbine runs on both JVM and Native, making it the industry standard for testing KMP ViewModels and reactive repositories.</p>
        """,
        "production_code": """
        <h3>Testing ViewModel State Transitions with Turbine</h3>
        <pre><code>package com.pulsesync.presentation

import app.cash.turbine.test
import kotlin.test.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class IncidentViewModelTest {

    @Test
    fun shouldEmitLoadingThenContentState() = runTest {
        val flow = flowOf("Incident 1", "Incident 2")

        flow.test {
            assertEquals("Incident 1", awaitItem())
            assertEquals("Incident 2", awaitItem())
            awaitComplete()
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Unconsumed Events in Turbine</div>
          Turbine enforces strict assertion completeness: if your flow emits an item that you do not assert via <code>awaitItem()</code> or <code>cancelAndIgnoreRemainingEvents()</code>, Turbine will intentionally fail your test to prevent silent unhandled emissions!
        </div>
        """,
        "code_title": "Assert Flow Emissions with Turbine",
        "code_filename": "FlowTest.kt",
        "code_instructions": "Write a test using <code>flowOf(10, 20).test { ... }</code> asserting <code>assertEquals(10, awaitItem())</code>, <code>assertEquals(20, awaitItem())</code>, and <code>awaitComplete()</code>.",
        "starter_code": """import app.cash.turbine.test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

// TODO: Complete testFlowEmissions using flow.test { }
fun testFlowEmissions() = runTest {
    flowOf(10, 20).test {
        // Assert items and completion
    }
}""",
        "hints": [
            "assertEquals(10, awaitItem())",
            "assertEquals(20, awaitItem())",
            "awaitComplete()"
        ],
        "final_solution": """import app.cash.turbine.test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

fun testFlowEmissions() = runTest {
    flowOf(10, 20).test {
        assertEquals(10, awaitItem())
        assertEquals(20, awaitItem())
        awaitComplete()
    }
}""",
        "solution_explanation": "Turbine converts asynchronous Flow streams into a deterministic sequential assertion queue.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasItem1 = /assertEquals\(\s*10\s*,\s*awaitItem\(\s*\)\s*\)/.test(code);
          const hasItem2 = /assertEquals\(\s*20\s*,\s*awaitItem\(\s*\)\s*\)/.test(code);
          const hasComplete = /awaitComplete\(\s*\)/.test(code);

          print("Inspecting Turbine assertions...");
          if (hasItem1 && hasItem2) print("✔ Items 10 and 20 asserted in sequence.");
          if (hasComplete) print("✔ Stream completion asserted.");

          tests.push({
            name: "awaitItem() used to verify emissions 10 and 20",
            passed: hasItem1 && hasItem2,
            message: "Must assert both items using awaitItem()."
          });
          tests.push({
            name: "awaitComplete() verifies terminal stream event",
            passed: hasComplete,
            message: "Must call awaitComplete() to verify flow completion."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What happens if a Flow emits an unexpected extra item inside a Turbine test { } block that is never consumed?",
            "options": [
                "Turbine automatically fails the test at the end of the block, reporting an unconsumed event.",
                "Turbine ignores the extra item and marks the unit test as passed.",
                "The host operating system terminates the test runner and reboots the simulator.",
                "The compiler drops the test file from the Gradle verification task pipeline."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Turbine requires full accounting of all emissions. If a flow emits an event that wasn't consumed with `awaitItem()`, Turbine throws an `AssertionError` to prevent unnoticed side effects.",
                "whyWrong": [
                    {"letter": "B", "reason": "Turbine explicitly prevents silent dropped events."},
                    {"letter": "C", "reason": "The OS does not terminate; it is a standard unit test failure."},
                    {"letter": "D", "reason": "The test runs and fails; it is not dropped."}
                ]
            }
        },
        "q2": {
            "question": "How do you test infinite flows (like StateFlow) that never complete in Turbine?",
            "options": [
                "Assert the required state transitions, then call cancelAndIgnoreRemainingEvents().",
                "Call Thread.sleep(10000) and wait for the garbage collector to terminate the flow.",
                "Infinite flows cannot be tested under any circumstances in Kotlin Multiplatform.",
                "Convert the StateFlow into an unmanaged Java Thread and invoke stop()."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "StateFlow is a hot, infinite stream that never completes. After asserting the expected states, calling `cancelAndIgnoreRemainingEvents()` cleanly stops collection.",
                "whyWrong": [
                    {"letter": "B", "reason": "Thread.sleep is bad practice and causes flaky tests."},
                    {"letter": "C", "reason": "StateFlow is tested in every professional KMP app."},
                    {"letter": "D", "reason": "Thread.stop() is obsolete and unavailable in multiplatform."}
                ]
            }
        },
        "source_title": "Cash App Turbine Repository",
        "source_url": "https://github.com/cashapp/turbine",
        "source_link_text": "Turbine by Cash App",
        "source_note": "A small testing library for kotlinx.coroutines Flow.",
        "ref_content": """
        <h2>Turbine Assertion Methods</h2>
        <ul>
          <li><code>awaitItem(): T</code>: Awaits the next emission.</li>
          <li><code>awaitComplete()</code>: Awaits successful completion.</li>
          <li><code>awaitError(): Throwable</code>: Awaits an exception emission.</li>
          <li><code>cancelAndIgnoreRemainingEvents()</code>: Clean exit for StateFlow.</li>
        </ul>
        """
    },

    # Lesson 28
    {
        "num": 28,
        "slug": "mocking-multiplatform-fakes-vs-mockative",
        "phase_tag": "Phase 8",
        "title": "Mocking in Multiplatform: Fakes vs. Mockative",
        "subtitle": "Discover why senior architects prefer Test Fakes over mocks, and explore Ktor MockEngine for network testing.",
        "prev_slug": "0027-testing-flows-coroutines-turbine",
        "next_slug": "0029-compose-multiplatform-ui-testing",
        "ref_slug": "28-mockative-and-fakes",
        "ref_title": "Test Fakes & Ktor MockEngine Architecture",
        "mental_model": """
        <p>In multiplatform engineering, <strong>Test Fakes</strong> are vastly superior to runtime mocks:</p>
        <ul>
          <li><strong>Fakes</strong> are lightweight, in-memory implementations of your interfaces (e.g. <code>FakeIncidentRepository</code>). They have zero third-party dependencies, compile in 0ms on all targets, and are 100% type-safe.</li>
          <li><strong>Ktor MockEngine</strong>: For networking, instead of mocking the <code>HttpClient</code> class, Ktor provides a native <code>MockEngine</code> where you configure simulated HTTP status codes, headers, and JSON responses.</li>
        </ul>
        """,
        "production_code": """
        <h3>Mocking Network Responses with Ktor MockEngine</h3>
        <pre><code>package com.pulsesync.network

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class IncidentApiClientTest {

    @Test
    fun shouldParseIncidentsJsonCorrectly() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = \"\"\"[{"id":"1","title":"Database Lag","timestamp":1700000000}]\"\"\",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = createKtorClient(mockEngine)
        val incidents = IncidentApiClient(client).fetchIncidents()

        assertEquals(1, incidents.size)
        assertEquals("Database Lag", incidents[0].title)
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Mocking What You Don't Own</div>
          Never mock third-party library classes (e.g. trying to mock RoomDatabase or OkHttpClient). Mocking external classes creates brittle tests that break on library upgrades. Always mock your own repository interfaces or use Ktor's official <code>MockEngine</code>.
        </div>
        """,
        "code_title": "Configure Ktor MockEngine Response",
        "code_filename": "MockEngineTest.kt",
        "code_instructions": "Implement <code>createTestEngine(jsonResponse: String): MockEngine</code> returning a <code>MockEngine</code> that responds with <code>HttpStatusCode.OK</code> and <code>jsonResponse</code>.",
        "starter_code": """import io.ktor.client.engine.mock.*
import io.ktor.http.*

fun createTestEngine(jsonResponse: String): MockEngine {
    // TODO: Return MockEngine responding with content = jsonResponse and status = HttpStatusCode.OK
    return MockEngine {
        respond(content = jsonResponse, status = HttpStatusCode.OK)
    }
}""",
        "hints": [
            "MockEngine { respond(content = jsonResponse, status = HttpStatusCode.OK) }"
        ],
        "final_solution": """import io.ktor.client.engine.mock.*
import io.ktor.http.*

fun createTestEngine(jsonResponse: String): MockEngine {
    return MockEngine {
        respond(content = jsonResponse, status = HttpStatusCode.OK)
    }
}""",
        "solution_explanation": "Ktor's MockEngine intercepts outgoing requests at the engine level, allowing full testing of serialization plugins, headers, and error interceptors.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasEngine = /MockEngine\s*\{[\s\S]*?respond\s*\(\s*content\s*=\s*jsonResponse[\s\S]*?status\s*=\s*HttpStatusCode\.OK\s*\)/.test(code);

          print("Validating Ktor MockEngine configuration...");
          if (hasEngine) print("✔ MockEngine configured with response payload and status OK.");

          tests.push({
            name: "MockEngine configured to respond with jsonResponse and HttpStatusCode.OK",
            passed: hasEngine,
            message: "Must configure MockEngine responding with content = jsonResponse and status = HttpStatusCode.OK."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary architectural advantage of using Ktor MockEngine over third-party mocking libraries?",
            "options": [
                "It tests your actual Ktor plugins (ContentNegotiation, Auth, Logging) against realistic HTTP responses.",
                "It redirects network requests to a public Google Cloud server to simulate latency.",
                "It replaces the local SQLite database with an in-memory Redis cluster automatically.",
                "It requires the test device to be physically connected to the internet at all times."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "MockEngine is a real HttpClientEngine. It exercises your real serialization plugins, error status mapping, and auth interceptors, verifying the entire network stack except the physical network cable.",
                "whyWrong": [
                    {"letter": "B", "reason": "MockEngine executes completely in-memory on the local device."},
                    {"letter": "C", "reason": "It does not touch Redis or databases."},
                    {"letter": "D", "reason": "It runs 100% offline without internet."}
                ]
            }
        },
        "q2": {
            "question": "Why do senior software engineers prefer Test Fakes over mock frameworks in KMP repositories?",
            "options": [
                "Fakes are pure Kotlin classes with zero reflection, compiling instantly across all platform targets.",
                "Fakes execute in kernel space and bypass all operating system permission checks.",
                "Mock frameworks automatically delete all source code if a test fails in continuous integration.",
                "Fakes guarantee that code will execute with zero memory allocation on Apple Silicon."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "A Test Fake is just a regular class implementing an interface. It compiles on JVM, iOS, and Wasm without any bytecode instrumentation libraries, making tests fast and resilient.",
                "whyWrong": [
                    {"letter": "B", "reason": "Tests run in normal user space, not kernel space."},
                    {"letter": "C", "reason": "Mocks do not delete source code."},
                    {"letter": "D", "reason": "Fakes allocate standard heap objects like any other Kotlin code."}
                ]
            }
        },
        "source_title": "Ktor Testing Guide",
        "source_url": "https://ktor.io/docs/client-testing.html",
        "source_link_text": "Testing Ktor HTTP Clients",
        "source_note": "Official guide on using MockEngine for unit and integration testing.",
        "ref_content": """
        <h2>MockEngine Recipe</h2>
        <pre><code>val engine = MockEngine { request ->
    when (request.url.encodedPath) {
        "/incidents" -> respond(json, HttpStatusCode.OK, headers)
        else -> respondError(HttpStatusCode.NotFound)
    }
}</code></pre>
        """
    },

    # Lesson 29
    {
        "num": 29,
        "slug": "compose-multiplatform-ui-testing",
        "phase_tag": "Phase 8",
        "title": "Compose Multiplatform UI Testing",
        "subtitle": "Author cross-platform UI tests using Compose UI test matchers, semantics assertions, and simulated user touch gestures.",
        "prev_slug": "0028-mocking-multiplatform-fakes-vs-mockative",
        "next_slug": "0030-gradle-build-optimization-caching",
        "ref_slug": "29-compose-ui-testing",
        "ref_title": "Compose Multiplatform UI Testing & Semantics",
        "mental_model": """
        <p>In Compose, UI testing does not inspect raw pixels or platform View hierarchies. Instead, it inspects the <strong>Semantics Tree</strong>:</p>
        <ul>
          <li>Every composable (Button, Text, TextField) publishes semantics (e.g. text content, click actions, test tags).</li>
          <li>Compose UI Test runners find elements using matchers (<code>hasText("Submit")</code>, <code>hasTestTag("login_button")</code>).</li>
          <li>Gestures (<code>performClick()</code>, <code>performTextInput()</code>) simulate real user behavior.</li>
          <li>Assertions (<code>assertIsDisplayed()</code>, <code>assertTextEquals("...")</code>) verify visual and accessible state.</li>
        </ul>
        """,
        "production_code": """
        <h3>Cross-Platform Compose UI Test</h3>
        <pre><code>package com.pulsesync.ui

import androidx.compose.ui.test.*
import kotlin.test.*

class IncidentItemUiTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shouldDisplayIncidentTitleAndResolveOnTap() = runComposeUiTest {
        var isResolved = false

        setContent {
            IncidentRow(
                title = "Database Outage",
                isResolved = isResolved,
                onResolveClick = { isResolved = true }
            )
        }

        // 1. Verify text is visible
        onNodeWithText("Database Outage").assertIsDisplayed()

        // 2. Perform tap gesture
        onNodeWithText("RESOLVE").performClick()

        // 3. Verify state updated
        assertTrue(isResolved)
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Relying on Fragile Hardcoded Strings</div>
          If your UI is localized into multiple languages, tests matching <code>hasText("Cancel")</code> will break when running under a Spanish or German locale. Use <code>Modifier.testTag("btn_cancel")</code> and <code>onNodeWithTag("btn_cancel")</code> for locale-independent UI assertions!
        </div>
        """,
        "code_title": "Write a Semantics UI Matcher",
        "code_filename": "UiTestHelpers.kt",
        "code_instructions": "Implement a helper function <code>findSubmitButton(tag: String) = hasTestTag(tag) and hasClickAction()</code>.",
        "starter_code": """import androidx.compose.ui.test.*

// TODO: Implement findSubmitButton(tag: String): SemanticsMatcher
fun findSubmitButton(tag: String): SemanticsMatcher {
    return hasTestTag(tag) and hasClickAction()
}""",
        "hints": [
            "Use: hasTestTag(tag) and hasClickAction()"
        ],
        "final_solution": """import androidx.compose.ui.test.*

fun findSubmitButton(tag: String): SemanticsMatcher {
    return hasTestTag(tag) and hasClickAction()
}""",
        "solution_explanation": "Combining semantics matchers with the 'and' operator allows testing both element identity (testTag) and capability (hasClickAction).",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasMatcher = /hasTestTag\(\s*tag\s*\)\s*and\s*hasClickAction\(\s*\)/.test(code);

          print("Validating Compose semantics matcher...");
          if (hasMatcher) print("✔ hasTestTag and hasClickAction matcher verified.");

          tests.push({
            name: "findSubmitButton combines hasTestTag and hasClickAction",
            passed: hasMatcher,
            message: "Must return hasTestTag(tag) and hasClickAction()."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What tree structure does Compose Multiplatform inspect during automated UI testing?",
            "options": [
                "The Semantics Tree which exposes accessible properties, text content, and interaction actions.",
                "The Apple CoreAnimation render tree containing raw GPU texture coordinates.",
                "The Android WindowManager view hierarchy containing legacy ViewGroup objects.",
                "The Gradle build dependency tree listing compiled binary JAR dependencies."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Compose UI tests operate against the Semantics Tree. Composables emit semantic nodes describing their role, text, and capabilities, which accessibility services and UI tests consume.",
                "whyWrong": [
                    {"letter": "B", "reason": "CoreAnimation handles low-level rendering, not semantic UI tests."},
                    {"letter": "C", "reason": "Compose has its own tree, separate from legacy Android ViewGroups."},
                    {"letter": "D", "reason": "The Gradle tree is for build dependencies, not UI testing."}
                ]
            }
        },
        "q2": {
            "question": "Why is onNodeWithTag preferred over onNodeWithText for testing interactive buttons in global apps?",
            "options": [
                "Test tags remain constant across all localized languages, preventing tests from breaking in other locales.",
                "Test tags speed up Android CPU clock frequencies during unit test execution.",
                "onNodeWithText is deprecated and removed in Kotlin Multiplatform 2.x releases.",
                "Test tags encrypt user passwords entered into Compose text input fields."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "If an app is translated into German, French, or Japanese, `onNodeWithText('Save')` will fail. `onNodeWithTag('btn_save')` tests the element reliably regardless of the device language.",
                "whyWrong": [
                    {"letter": "B", "reason": "Test tags have no effect on hardware CPU clock speeds."},
                    {"letter": "C", "reason": "onNodeWithText is not deprecated; it is still actively supported."},
                    {"letter": "D", "reason": "Test tags are testing identifiers, not encryption mechanisms."}
                ]
            }
        },
        "source_title": "JetBrains Compose Testing Documentation",
        "source_url": "https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html",
        "source_link_text": "Testing Compose Multiplatform UI",
        "source_note": "Official guide on writing cross-platform Compose UI tests.",
        "ref_content": """
        <h2>Essential Compose Test Matchers</h2>
        <ul>
          <li><code>onNodeWithTag("tag")</code>: Locale-independent matching.</li>
          <li><code>onNodeWithText("title")</code>: Visible text matching.</li>
          <li><code>performClick()</code> / <code>performTextInput("text")</code>: Gestures.</li>
          <li><code>assertIsDisplayed()</code> / <code>assertDoesNotExist()</code>: Assertions.</li>
        </ul>
        """
    },

    # Lesson 30
    {
        "num": 30,
        "slug": "gradle-build-optimization-caching",
        "phase_tag": "Phase 9",
        "title": "Gradle Build Optimization & Caching",
        "subtitle": "Cut Kotlin Multiplatform compilation times by 50% using configuration caching, build cache, and daemon tuning.",
        "prev_slug": "0029-compose-multiplatform-ui-testing",
        "next_slug": "0031-github-actions-multiplatform-ci",
        "ref_slug": "30-build-optimization-caching",
        "ref_title": "Gradle KMP Build Optimization & Flags Reference",
        "mental_model": """
        <p>Because KMP projects compile for both JVM and LLVM (Kotlin/Native), clean build times can grow quickly without proper Gradle optimization. Senior KMP engineers configure three critical layers:</p>
        <ol>
          <li><strong>Gradle Configuration Cache</strong> (<code>org.gradle.configuration-cache=true</code>): Caches the build configuration phase, reducing incremental build startup times from 10s to sub-second.</li>
          <li><strong>Gradle Build Cache</strong> (<code>org.gradle.caching=true</code>): Reuses task outputs across branches and developer machines.</li>
          <li><strong>Kotlin Daemon Heap Sizing</strong>: Granting the Kotlin compile daemon sufficient memory (<code>-Xmx6g</code>) to prevent garbage collection thrashing during LLVM linking.</li>
        </ol>
        """,
        "production_code": """
        <h3>Canonical <code>gradle.properties</code> for KMP</h3>
        <pre><code># Enable Configuration Cache & Build Cache
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true

# Daemon Memory Tuning (Crucial for Kotlin/Native LLVM linking)
org.gradle.jvmargs=-Xmx6g -XX:+UseParallelGC -XX:MaxMetaspaceSize=1g

# Kotlin Multiplatform Build Flags
kotlin.mpp.enableCInteropCommonization=true
kotlin.daemon.jvmargs=-Xmx4g</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Accessing Project at Task Execution Time</div>
          Writing <code>project.findProperty("...")</code> inside an action block (<code>doLast { ... }</code>) breaks Gradle Configuration Cache! Always pass configuration values through Gradle Task <code>Property&lt;T&gt;</code> or <code>Provider&lt;T&gt;</code> inputs.
        </div>
        """,
        "code_title": "Configure Production gradle.properties Flags",
        "code_filename": "gradle.properties",
        "code_instructions": "Write the essential Gradle flags: <code>org.gradle.caching=true</code>, <code>org.gradle.configuration-cache=true</code>, and <code>org.gradle.parallel=true</code>.",
        "starter_code": """# TODO: Enable caching, configuration-cache, and parallel builds
org.gradle.caching=true""",
        "hints": [
            "org.gradle.caching=true",
            "org.gradle.configuration-cache=true",
            "org.gradle.parallel=true"
        ],
        "final_solution": """org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true""",
        "solution_explanation": "These three flags represent the foundation of modern high-performance Gradle build pipelines, maximizing multi-core CPU usage and skipping redundant tasks.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasCache = /org\.gradle\.caching\s*=\s*true/.test(code);
          const hasConfig = /org\.gradle\.configuration-cache\s*=\s*true/.test(code);
          const hasParallel = /org\.gradle\.parallel\s*=\s*true/.test(code);

          print("Verifying Gradle performance configuration...");
          if (hasCache) print("✔ Build Cache enabled.");
          if (hasConfig) print("✔ Configuration Cache enabled.");
          if (hasParallel) print("✔ Parallel project execution enabled.");

          tests.push({
            name: "org.gradle.caching=true enabled",
            passed: hasCache,
            message: "Must enable org.gradle.caching=true."
          });
          tests.push({
            name: "org.gradle.configuration-cache=true enabled",
            passed: hasConfig,
            message: "Must enable org.gradle.configuration-cache=true."
          });
          tests.push({
            name: "org.gradle.parallel=true enabled",
            passed: hasParallel,
            message: "Must enable org.gradle.parallel=true."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary benefit of enabling the Gradle Configuration Cache in a KMP repository?",
            "options": [
                "It skips evaluating build.gradle.kts scripts when inputs have not changed, reducing build start time.",
                "It compresses all Android APK binaries by sixty percent before uploading to the Play Console.",
                "It automatically signs iOS frameworks without requiring Apple Developer certificate credentials.",
                "It removes all unused Swift files from the host Xcode workspace directory."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "The Configuration Cache records the task execution graph. On subsequent builds where build scripts haven't changed, Gradle skips the configuration phase entirely and executes tasks immediately.",
                "whyWrong": [
                    {"letter": "B", "reason": "Configuration cache affects build configuration, not APK compression."},
                    {"letter": "C", "reason": "Code signing always requires valid Apple certificates."},
                    {"letter": "D", "reason": "It does not delete source files."}
                ]
            }
        },
        "q2": {
            "question": "Why is increasing JVM heap size (-Xmx) particularly important for Kotlin Multiplatform projects?",
            "options": [
                "Kotlin/Native LLVM linking and K2 frontend analysis consume substantial memory during compilation.",
                "The Android emulator refuses to boot if the host machine has less than eight gigabytes of RAM.",
                "Compose Multiplatform requires four gigabytes of RAM to render every preview composable.",
                "The Apple App Store rejects applications compiled on machines with low memory allocations."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Kotlin/Native compiles down to native machine code using LLVM. LLVM linking and symbol resolution are memory-intensive. Allocating 4-6GB prevents Gradle daemons from spending half their time in GC pauses.",
                "whyWrong": [
                    {"letter": "B", "reason": "Gradle heap size has no relationship to Android emulator boots."},
                    {"letter": "C", "reason": "Previews run with modest memory overhead."},
                    {"letter": "D", "reason": "App Store review inspects final binaries, not developer compile RAM."}
                ]
            }
        },
        "source_title": "Gradle Performance Documentation",
        "source_url": "https://docs.gradle.org/current/userguide/performance.html",
        "source_link_text": "Optimizing Gradle Build Performance",
        "source_note": "Official guide on configuration cache, build cache, and daemon tuning.",
        "ref_content": """
        <h2>Essential gradle.properties Flags</h2>
        <pre><code>org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx6g</code></pre>
        """
    },

    # Lesson 31
    {
        "num": 31,
        "slug": "github-actions-multiplatform-ci",
        "phase_tag": "Phase 9",
        "title": "GitHub Actions Multiplatform Matrix CI/CD",
        "subtitle": "Automate dual-platform builds: cross-compiling Android AABs and iOS XCFrameworks using GitHub Actions matrix runners.",
        "prev_slug": "0030-gradle-build-optimization-caching",
        "next_slug": None,
        "ref_slug": "31-github-actions-matrix-ci",
        "ref_title": "GitHub Actions KMP CI/CD Matrix Workflow Reference",
        "mental_model": """
        <p>In modern engineering teams, pull requests cannot be merged without automated CI verification. In KMP, a production pipeline requires a <strong>matrix strategy</strong>:</p>
        <ul>
          <li><strong>Linux Runner (ubuntu-latest)</strong>: Runs Android unit tests, Android lint, and compiles Android APKs/AABs (cost-effective, fast).</li>
          <li><strong>macOS Runner (macos-latest)</strong>: Required for compiling Apple targets! Compiles Kotlin/Native iOS frameworks (<code>SharedKit.xcframework</code>), runs iOS simulator tests, and builds iOS IPAs.</li>
          <li><strong>Gradle Dependency Caching</strong>: Uses <code>actions/setup-java</code> and <code>gradle/actions/setup-gradle</code> to cache Gradle dependencies across CI runs.</li>
        </ul>
        """,
        "production_code": """
        <h3>Production <code>.github/workflows/kmp-ci.yml</code></h3>
        <pre><code>name: KMP CI Pipeline

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  check-and-test:
    name: Build & Test (${{ matrix.os }})
    runs-on: ${{ matrix.os }}
    strategy:
      fail-fast: false
      matrix:
        include:
          - os: ubuntu-latest
            task: check assembleDebug
          - os: macos-latest
            task: iosSimulatorArm64Test assembleSharedKitXCFramework

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Setup Gradle Cache
        uses: gradle/actions/setup-gradle@v3

      - name: Run Multiplatform Tasks
        run: ./gradlew ${{ matrix.task }} --scan</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Running iOS Compilation on Linux Runners</div>
          Kotlin/Native Apple targets (<code>iosArm64</code>, <code>iosSimulatorArm64</code>) can only link into Apple frameworks on <strong>macOS runners</strong> because the link phase invokes the Xcode toolchain (Clang, ld64, codesign). Attempting to build iOS frameworks on Ubuntu will fail with an Xcode missing error.
        </div>
        """,
        "code_title": "Declare GitHub Actions Workflow Matrix",
        "code_filename": "ci-matrix.yml",
        "code_instructions": "Declare a workflow matrix with two OS targets: <code>ubuntu-latest</code> and <code>macos-latest</code>.",
        "starter_code": """matrix:
  os: [ubuntu-latest, macos-latest]""",
        "hints": [
            "matrix: os: [ubuntu-latest, macos-latest]"
        ],
        "final_solution": """matrix:
  os: [ubuntu-latest, macos-latest]""",
        "solution_explanation": "A matrix strategy parallelizes Android JVM compilation on Linux with Apple framework compilation on macOS runners.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasUbuntu = /ubuntu-latest/.test(code);
          const hasMac = /macos-latest/.test(code);

          print("Inspecting CI runner matrix configuration...");
          if (hasUbuntu && hasMac) print("✔ Linux and macOS matrix runners configured.");

          tests.push({
            name: "Matrix declares ubuntu-latest and macos-latest runners",
            passed: hasUbuntu && hasMac,
            message: "Must configure matrix with ubuntu-latest and macos-latest."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why must Kotlin Multiplatform iOS targets be compiled on a macOS runner in continuous integration?",
            "options": [
                "Linking Apple frameworks requires the proprietary Apple Xcode developer toolchain and SDK headers.",
                "Ubuntu Linux operating systems are prohibited by law from executing Kotlin source files.",
                "Android Gradle Plugin requires macOS hardware to generate Android App Bundle (AAB) archives.",
                "The Git version control system refuses to clone branches containing Swift source files on Linux."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Apple does not license the Xcode SDK headers, linkers (ld64), and simulator runtime for Linux. Linking Apple frameworks requires a macOS runner with Xcode installed.",
                "whyWrong": [
                    {"letter": "B", "reason": "Kotlin compiles on Linux; only Apple native target linking requires macOS."},
                    {"letter": "C", "reason": "Android builds run fastest and cheapest on Linux runners."},
                    {"letter": "D", "reason": "Git clones any repository regardless of file extensions."}
                ]
            }
        },
        "q2": {
            "question": "Which GitHub Action is officially recommended by Gradle to manage daemon lifecycles and build caching?",
            "options": [
                "gradle/actions/setup-gradle which configures dependencies caching and build scan integration.",
                "actions/upload-artifact which uploads compiled build files to the cloud storage bucket.",
                "actions/cache which requires manual configuration of hash keys and path directories.",
                "docker/build-push-action which packages applications into containerized microservice images."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "gradle/actions/setup-gradle is the official Gradle action that handles intelligent wrapper caching, build scan reporting, and daemon lifecycle management automatically.",
                "whyWrong": [
                    {"letter": "B", "reason": "upload-artifact stores output files, but does not manage build caching."},
                    {"letter": "C", "reason": "actions/cache is generic and lacks Gradle-specific daemon awareness."},
                    {"letter": "D", "reason": "Docker actions are for containers, not native mobile builds."}
                ]
            }
        },
        "source_title": "GitHub Actions Documentation",
        "source_url": "https://docs.github.com/en/actions/using-jobs/using-a-matrix-for-your-jobs",
        "source_link_text": "Using a Matrix Strategy in GitHub Actions",
        "source_note": "Official guide on parallelizing multi-platform CI pipelines.",
        "ref_content": """
        <h2>CI/CD Matrix Best Practice</h2>
        <ul>
          <li><strong>ubuntu-latest</strong>: <code>./gradlew check assembleDebug</code> (Cheap & fast).</li>
          <li><strong>macos-latest</strong>: <code>./gradlew iosSimulatorArm64Test assembleSharedKitXCFramework</code> (Apple toolchain).</li>
        </ul>
        """
    }
]

for l in lessons:
    generate_lesson_file(l)

print("Lessons 23 to 31 (Phase 7, 8, 9) successfully generated!")
