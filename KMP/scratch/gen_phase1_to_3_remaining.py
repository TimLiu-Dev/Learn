import sys
import os
sys.path.append(os.path.dirname(__file__))
from generator_base import generate_lesson_file

lessons = [
    # Lesson 06
    {
        "num": 6,
        "slug": "hardware-and-system-interop",
        "phase_tag": "Phase 1",
        "title": "Hardware & Native System Interop: Secure Key Storage",
        "subtitle": "Bridge secure hardware enclaves using Android Keystore and Apple Keychain through clean dependency-injected interfaces.",
        "prev_slug": "0005-expect-actual-mechanisms-antipatterns",
        "next_slug": "0007-multiplatform-coroutines-concurrency",
        "ref_slug": "06-secure-storage-bridges",
        "ref_title": "Secure Token Storage: Android Keystore vs. iOS Keychain",
        "mental_model": """
        <p>Production mobile applications must securely persist sensitive session tokens (OAuth refresh tokens, encryption keys, biometrics). Storing these in plaintext preferences or databases is a major security vulnerability.</p>
        <p>Both mobile platforms provide hardware-backed security enclaves:</p>
        <ul>
          <li><strong>Android</strong>: <code>EncryptedSharedPreferences</code> backed by the <strong>Android Keystore</strong> hardware-backed master key.</li>
          <li><strong>iOS</strong>: The <strong>Apple Keychain</strong> service (<code>SecItemAdd</code>, <code>SecItemCopyMatching</code>) backed by the Secure Enclave processor.</li>
        </ul>
        <p>In KMP, we model this with a pure Kotlin interface <code>SecureStorage</code> in <code>commonMain</code>, implemented independently in <code>androidMain</code> and <code>iosMain</code>.</p>
        """,
        "production_code": """
        <h3>1. Common Interface in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.security

interface SecureStorage {
    suspend fun saveToken(key: String, token: String)
    suspend fun getToken(key: String): String?
    suspend fun clearToken(key: String)
}</code></pre>

        <h3>2. iOS Implementation in <code>iosMain</code> (Apple Keychain)</h3>
        <pre><code>package com.pulsesync.security

import platform.Foundation.*
import platform.Security.*

class IosSecureStorage : SecureStorage {
    override suspend fun saveToken(key: String, token: String) {
        val query = CFDictionaryCreateMutable(null, 0, null, null)
        // SecItemAdd with kSecClassGenericPassword
    }
    override suspend fun getToken(key: String): String? { /* SecItemCopyMatching */ return null }
    override suspend fun clearToken(key: String) { /* SecItemDelete */ }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Main-Thread Keychain / Keystore I/O</div>
          Accessing Apple Keychain or Android EncryptedSharedPreferences performs disk I/O and cryptographic hardware operations. Always dispatch secure storage operations onto background dispatchers (<code>Dispatchers.Default</code> or <code>Dispatchers.IO</code>) to avoid UI frame drops.
        </div>
        """,
        "code_title": "Define Secure Storage Contract",
        "code_filename": "SecureStorage.kt",
        "code_instructions": "Define a thread-safe <code>SecureStorage</code> interface in commonMain with suspending methods: <code>saveSecret(key: String, value: String)</code> and <code>getSecret(key: String): String?</code>.",
        "starter_code": """// TODO: Define interface SecureStorage with saveSecret and getSecret methods""",
        "hints": [
            "Use suspend functions for asynchronous crypto operations.",
            "fun saveSecret(key: String, value: String): Unit",
            "fun getSecret(key: String): String?"
        ],
        "final_solution": """interface SecureStorage {
    suspend fun saveSecret(key: String, value: String)
    suspend fun getSecret(key: String): String?
}""",
        "solution_explanation": "Suspending methods ensure callers can execute cryptographic hardware calls without blocking the UI thread on both iOS and Android.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasInterface = /interface\s+SecureStorage\s*\{/.test(code);
          const hasSave = /suspend\s+fun\s+saveSecret\s*\(\s*key\s*:\s*String\s*,\s*value\s*:\s*String\s*\)/.test(code);
          const hasGet = /suspend\s+fun\s+getSecret\s*\(\s*key\s*:\s*String\s*\)\s*:\s*String\?/.test(code);

          print("Validating SecureStorage interface...");
          if (hasInterface) print("✔ SecureStorage interface declared.");
          if (hasSave) print("✔ Suspending saveSecret signature verified.");
          if (hasGet) print("✔ Suspending getSecret signature verified.");

          tests.push({
            name: "SecureStorage interface declared",
            passed: hasInterface,
            message: "Must declare 'interface SecureStorage'."
          });
          tests.push({
            name: "suspend fun saveSecret(key: String, value: String) defined",
            passed: hasSave,
            message: "Must define suspend fun saveSecret(key: String, value: String)."
          });
          tests.push({
            name: "suspend fun getSecret(key: String): String? defined",
            passed: hasGet,
            message: "Must define suspend fun getSecret(key: String): String?."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Which native Apple framework provides hardware-backed encrypted credential storage on iOS devices?",
            "options": [
                "The Security framework providing Keychain Services API backed by the Secure Enclave.",
                "The CoreData framework managing persistent relational object graphs and SQLite schemas.",
                "The Foundation framework providing NSUserDefaults key-value property list storage.",
                "The UIKit framework managing visual touch events and application window hierarchies."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Apple Keychain Services (part of the Security framework) is the dedicated secure storage mechanism on iOS, encrypting data using keys managed by the device Secure Enclave.",
                "whyWrong": [
                    {"letter": "B", "reason": "CoreData is an ORM database framework, not a secure credential store."},
                    {"letter": "C", "reason": "NSUserDefaults stores unencrypted property lists and is insecure for secret tokens."},
                    {"letter": "D", "reason": "UIKit is the legacy imperative UI framework, having nothing to do with crypto."}
                ]
            }
        },
        "q2": {
            "question": "Why should hardware cryptographic calls (like Keychain access) always be marked with the suspend keyword?",
            "options": [
                "They access secure hardware chips and disk storage which can block the main rendering thread.",
                "The Apple LLVM compiler will fail with a linker error if cryptographic calls lack suspend.",
                "Suspending functions automatically encrypt all network traffic routed through cellular towers.",
                "It enables the Android operating system to display biometric prompts without user permission."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Keychain and Keystore operations perform I/O and cryptographic calculations that take several milliseconds. Running them on the main thread causes UI stutter and dropped frames.",
                "whyWrong": [
                    {"letter": "B", "reason": "LLVM compiles non-suspending cryptographic calls just fine, but they run synchronously."},
                    {"letter": "C", "reason": "Suspending functions do not affect cellular network encryption."},
                    {"letter": "D", "reason": "Biometric prompts always require user interaction and explicit platform permissions."}
                ]
            }
        },
        "source_title": "Apple Security Documentation",
        "source_url": "https://developer.apple.com/documentation/security/keychain_services",
        "source_link_text": "Keychain Services Overview",
        "source_note": "Apple's official guide to Keychain Services and item access control.",
        "ref_content": """
        <h2>Secure Storage Platform Mapping</h2>
        <ul>
          <li><strong>Android</strong>: <code>EncryptedSharedPreferences</code> using MasterKey from Android Keystore.</li>
          <li><strong>iOS</strong>: <code>kSecClassGenericPassword</code> queries via <code>platform.Security.*</code>.</li>
        </ul>
        """
    },

    # Lesson 07
    {
        "num": 7,
        "slug": "multiplatform-coroutines-concurrency",
        "phase_tag": "Phase 2",
        "title": "Multiplatform Coroutines & Concurrency",
        "subtitle": "Master structured concurrency across JVM and Kotlin/Native, lifecycle cancellation, and multiplatform dispatchers.",
        "prev_slug": "0006-hardware-and-system-interop",
        "next_slug": "0008-reactive-state-flow-channels",
        "ref_slug": "07-coroutines-native-concurrency",
        "ref_title": "Multiplatform Coroutines & Dispatchers Reference",
        "mental_model": """
        <p>In Kotlin Multiplatform, asynchronous code is powered by <code>kotlinx.coroutines</code>. Since Kotlin 1.9+, Kotlin/Native features the modern <strong>new memory model</strong>, meaning objects can be shared between threads without freezing or complex worker boundaries.</p>
        <p>Key Coroutine rules in KMP:</p>
        <ul>
          <li><strong>Dispatchers.Main</strong>: Binds to Android Looper on Android and Apple Grand Central Dispatch (GCD) Main Queue on iOS.</li>
          <li><strong>Dispatchers.Default</strong>: CPU-bound background thread pool across all platforms.</li>
          <li><strong>Dispatchers.IO</strong>: Officially available across both JVM and Native (backed by background GCD queues on Apple targets).</li>
          <li><strong>Structured Concurrency</strong>: CoroutineScopes must be tied to lifecycles (e.g. ViewModel or lifecycle scope) to cancel background tasks when screens exit.</li>
        </ul>
        """,
        "production_code": """
        <h3>Structured Concurrency Worker in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.sync

import kotlinx.coroutines.*

class IncidentSyncWorker(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    fun startSync(action: suspend () -> Unit): Job {
        return scope.launch {
            try {
                action()
            } catch (e: CancellationException) {
                // Cooperative cancellation: rethrow to allow scope shutdown
                throw e
            } catch (e: Exception) {
                println("Sync failed safely: ${e.message}")
            }
        }
    }

    fun cancel() {
        scope.cancel()
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Swallowing CancellationException</div>
          Never catch generic <code>Throwable</code> or <code>Exception</code> without rethrowing <code>CancellationException</code>. Swallowing cancellation breaks Kotlin Coroutines' cooperative cancellation model, causing memory leaks and zombie background workers.
        </div>
        """,
        "code_title": "Build a Resilient Background Coroutine Worker",
        "code_filename": "TelemetryWorker.kt",
        "code_instructions": "Implement a <code>TelemetryWorker</code> class with a <code>runTelemetry(block: suspend () -> Unit): Job</code> method that launches on a background scope and re-throws <code>CancellationException</code>.",
        "starter_code": """import kotlinx.coroutines.*

class TelemetryWorker(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    fun runTelemetry(block: suspend () -> Unit): Job {
        // TODO: Launch coroutine, call block, and safely handle CancellationException
        return scope.launch {
            block()
        }
    }
}""",
        "hints": [
            "Wrap block() in try/catch block.",
            "catch (e: CancellationException) { throw e }",
            "catch (e: Exception) { println(e.message) }"
        ],
        "final_solution": """import kotlinx.coroutines.*

class TelemetryWorker(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    fun runTelemetry(block: suspend () -> Unit): Job {
        return scope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Error: " + e.message)
            }
        }
    }
}""",
        "solution_explanation": "Cooperative cancellation requires rethrowing CancellationException so that parent jobs and scopes can cleanly tear down resources.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasLaunch = /scope\.launch/.test(code);
          const hasCatchCancel = /catch\s*\(\s*\w+\s*:\s*CancellationException\s*\)\s*\{\s*throw/.test(code);

          print("Verifying structured concurrency principles...");
          if (hasLaunch) print("✔ Scope launch pattern detected.");
          if (hasCatchCancel) print("✔ CancellationException properly rethrown.");

          tests.push({
            name: "Coroutine launched on supervisor scope",
            passed: hasLaunch,
            message: "Must launch coroutine using scope.launch."
          });
          tests.push({
            name: "CancellationException rethrown cooperatively",
            passed: hasCatchCancel,
            message: "Must explicitly catch and rethrow CancellationException."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What happens if a coroutine catches CancellationException and fails to rethrow it?",
            "options": [
                "The coroutine ignores cancellation and continues running, causing memory leaks and zombie jobs.",
                "The JVM and iOS operating systems immediately force-close the host mobile application.",
                "The Kotlin compiler issues a fatal syntax error and prevents binary artifact generation.",
                "The Dispatchers.Main thread gets permanently blocked and halts all user touch gestures."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Coroutines rely on CancellationException to propagate cancellation signals down the job hierarchy. Swallowing it prevents the coroutine from stopping.",
                "whyWrong": [
                    {"letter": "B", "reason": "The OS does not crash; the job simply keeps executing in the background."},
                    {"letter": "C", "reason": "The compiler allows standard try/catch blocks; it cannot detect swallowed cancellations at compile-time."},
                    {"letter": "D", "reason": "Background dispatchers are independent of the main UI thread."}
                ]
            }
        },
        "q2": {
            "question": "Which dispatcher should be used for CPU-intensive tasks (like JSON parsing or image filtering) in commonMain?",
            "options": [
                "Dispatchers.Default which is backed by a shared pool of background worker threads.",
                "Dispatchers.Main which executes code directly on the platform UI event queue.",
                "Dispatchers.Unconfined which executes the first step of the coroutine in the current thread.",
                "Dispatchers.Immediate which bypasses coroutine dispatchers and executes sequentially."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Dispatchers.Default is designed for compute-heavy work and scales with the number of available CPU cores on both Android and iOS.",
                "whyWrong": [
                    {"letter": "B", "reason": "Dispatchers.Main is for UI updates; heavy computation freezes the screen."},
                    {"letter": "C", "reason": "Dispatchers.Unconfined is for unconfined testing, not compute pools."},
                    {"letter": "D", "reason": "Main.immediate is for micro-optimizing main-thread dispatches when already on the main thread."}
                ]
            }
        },
        "source_title": "Kotlin Coroutines Guide",
        "source_url": "https://kotlinlang.org/docs/coroutines-overview.html",
        "source_link_text": "Kotlin Coroutines Official Guide",
        "source_note": "JetBrains comprehensive reference for coroutines and structured concurrency.",
        "ref_content": """
        <h2>Coroutine Dispatchers in KMP</h2>
        <ul>
          <li><code>Dispatchers.Main</code>: Android UI looper / Apple main queue.</li>
          <li><code>Dispatchers.Default</code>: Background thread pool for CPU computation.</li>
          <li><code>Dispatchers.IO</code>: Optimized for blocking disk/network I/O across JVM and Native.</li>
        </ul>
        """
    },

    # Lesson 08
    {
        "num": 8,
        "slug": "reactive-state-flow-channels",
        "phase_tag": "Phase 2",
        "title": "Reactive State with Flow, StateFlow & Channels",
        "subtitle": "Architect unidirectional MVI state machines using StateFlow for state and Channels for one-off side effects.",
        "prev_slug": "0007-multiplatform-coroutines-concurrency",
        "next_slug": "0009-bridging-coroutines-to-swift-skie",
        "ref_slug": "08-stateflow-mvi-patterns",
        "ref_title": "StateFlow vs. SharedFlow vs. Channels Cheatsheet",
        "mental_model": """
        <p>In modern multiplatform UI architecture, unidirectional data flow (UDF / MVI) is standard. We distinguish between two kinds of data:</p>
        <ol>
          <li><strong>Persistent UI State</strong> (e.g. Loading, Content, Error): Modeled via <code>StateFlow&lt;UiState&gt;</code>. It always retains the latest value and replays it to new subscribers (conflated).</li>
          <li><strong>Transient Side Effects</strong> (e.g. Navigate to screen, Show Toast/Snackbar): Modeled via <code>Channel&lt;UiEffect&gt;</code> consumed as a Flow. Effects must be delivered exactly once and never re-triggered on screen rotation.</li>
        </ol>
        """,
        "production_code": """
        <div class="callout tip">
          <div class="callout-title">Official Jetpack Multiplatform ViewModel</div>
          Starting with AndroidX Lifecycle 2.8+, Google's official <code>androidx.lifecycle.ViewModel</code> and <code>viewModelScope</code> are fully multiplatform in <code>commonMain</code>. You no longer need third-party MVVM wrapper libraries!
        </div>

        <h3>Multiplatform MVI ViewModel using Official AndroidX Lifecycle</h3>
        <pre><code>package com.pulsesync.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

data class IncidentListState(
    val isLoading: Boolean = false,
    val incidents: List<String> = emptyList(),
    val errorMessage: String? = null
)

sealed interface IncidentEffect {
    data class ShowToast(val message: String) : IncidentEffect
    data class NavigateToDetail(val id: String) : IncidentEffect
}

class IncidentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IncidentListState())
    val uiState: StateFlow<IncidentListState> = _uiState.asStateFlow()

    private val _effects = Channel<IncidentEffect>(Channel.BUFFERED)
    val effects: Flow<IncidentEffect> = _effects.receiveAsFlow()

    fun onRefreshClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Using StateFlow for One-Off Navigation Events</div>
          If you put <code>navigateToDetail: String?</code> inside your UI state StateFlow, any configuration change (such as rotating an Android phone or resizing an iPad split window) will re-emit the state and trigger navigation again. Use <code>Channel</code> for events!
        </div>
        """,
        "code_title": "Build a Reactive State & Effect Holder",
        "code_filename": "IncidentStateHolder.kt",
        "code_instructions": "Declare a <code>StateHolder</code> with a <code>MutableStateFlow<Int></code> initialized to 0, exposed as a read-only <code>StateFlow<Int></code>.",
        "starter_code": """import kotlinx.coroutines.flow.*

class StateHolder {
    // TODO: Create private _count = MutableStateFlow(0)
    // TODO: Expose public val count: StateFlow<Int> = _count.asStateFlow()
}""",
        "hints": [
            "private val _count = MutableStateFlow(0)",
            "val count: StateFlow<Int> = _count.asStateFlow()"
        ],
        "final_solution": """import kotlinx.coroutines.flow.*

class StateHolder {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()
}""",
        "solution_explanation": "Encapsulating MutableStateFlow behind a read-only StateFlow guarantees unidirectional data flow: only the ViewModel can mutate state.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasPrivate = /private\s+val\s+_count\s*=\s*MutableStateFlow\(0\)/.test(code);
          const hasPublic = /val\s+count\s*:\s*StateFlow<Int>\s*=\s*_count\.asStateFlow\(\)/.test(code);

          print("Inspecting reactive state encapsulation...");
          if (hasPrivate) print("✔ Private MutableStateFlow declared.");
          if (hasPublic) print("✔ Public read-only StateFlow exposed.");

          tests.push({
            name: "Private MutableStateFlow initialized with 0",
            passed: hasPrivate,
            message: "Must declare 'private val _count = MutableStateFlow(0)'."
          });
          tests.push({
            name: "Public read-only StateFlow exposed via asStateFlow()",
            passed: hasPublic,
            message: "Must expose 'val count: StateFlow<Int> = _count.asStateFlow()'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why should transient events (like displaying a snackbar) be modeled using Channels rather than StateFlow?",
            "options": [
                "Channels guarantee each event is delivered once and not replayed when views re-subscribe.",
                "StateFlow can only emit primitive integer types and cannot hold complex data classes.",
                "Channels execute synchronously on the main thread without requiring a coroutine context.",
                "The Apple SwiftUI framework cannot observe any Kotlin class that declares a StateFlow."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "StateFlow retains its latest value. If a new view subscribes (e.g. after screen rotation), it would re-trigger the snackbar. Channels buffer events and drop them once consumed.",
                "whyWrong": [
                    {"letter": "B", "reason": "StateFlow can hold any Kotlin object, data class, or collection."},
                    {"letter": "C", "reason": "Channels are suspending and asynchronous; they do not bypass coroutine context."},
                    {"letter": "D", "reason": "SwiftUI can observe StateFlow when bridged with SKIE."}
                ]
            }
        },
        "q2": {
            "question": "Which operator safely updates a MutableStateFlow atomically without race conditions under concurrency?",
            "options": [
                "The update { it.copy(...) } extension function performing atomic compare-and-set loops.",
                "The assign() operator which forces the underlying thread to acquire a hardware mutex.",
                "The mutate() method which cancels all concurrent collectors before applying the change.",
                "The postValue() method inherited from the legacy Android Jetpack LiveData class."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "MutableStateFlow.update { } executes an atomic compare-and-set (CAS) loop, preventing race conditions when multiple coroutines update state concurrently.",
                "whyWrong": [
                    {"letter": "B", "reason": "There is no assign() operator on StateFlow."},
                    {"letter": "C", "reason": "StateFlow does not cancel collectors when state updates."},
                    {"letter": "D", "reason": "postValue is from Android LiveData, not Kotlin StateFlow."}
                ]
            }
        },
        "source_title": "Kotlin Asynchronous Flow Documentation",
        "source_url": "https://kotlinlang.org/docs/flow.html",
        "source_link_text": "StateFlow and SharedFlow Guide",
        "source_note": "Authoritative guide on hot vs cold streams in Kotlin Coroutines.",
        "ref_content": """
        <h2>StateFlow vs Channel Decision Table</h2>
        <ul>
          <li><strong>StateFlow</strong>: UI State (loading, error, content). Conflated, retains last value, replay = 1.</li>
          <li><strong>Channel</strong>: Side effects (Navigation, Toast, Dialog trigger). Delivered once, no replay.</li>
        </ul>
        """
    },

    # Lesson 09
    {
        "num": 9,
        "slug": "bridging-coroutines-to-swift-skie",
        "phase_tag": "Phase 2",
        "title": "Bridging Kotlin Coroutines to Swift with SKIE",
        "subtitle": "Eliminate unwieldy completion blocks in Swift and bridge Kotlin StateFlow to Swift AsyncSequence and @Observable.",
        "prev_slug": "0008-reactive-state-flow-channels",
        "next_slug": "0010-ktor3-client-architecture",
        "ref_slug": "09-skie-swift-concurrency",
        "ref_title": "SKIE Configuration & Swift Concurrency Cheat Sheet",
        "mental_model": """
        <p>By default, Kotlin/Native's Objective-C export converts suspending functions into legacy completion handlers:</p>
        <pre><code>// Default Kotlin/Native export in Swift:
viewModel.fetchData(completionHandler: { data, error in ... })</code></pre>
        <p>Moreover, Kotlin <code>StateFlow</code> and sealed interfaces lose their exhaustiveness and reactivity in Swift. <strong>SKIE (by Touchlab)</strong> is the industry-standard compiler plugin that fixes this:</p>
        <ul>
          <li>Converts <code>suspend fun</code> directly into Swift <code>async/await</code>.</li>
          <li>Converts <code>StateFlow&lt;T&gt;</code> into Swift <code>AsyncSequence</code> and integrates with SwiftUI's <code>@Observable</code>.</li>
          <li>Translates Kotlin sealed interfaces into exhaustive native Swift <code>enum</code>s.</li>
        </ul>
        """,
        "production_code": """
        <h3>1. Enable SKIE in <code>gradle/libs.versions.toml</code> and <code>build.gradle.kts</code></h3>
        <pre><code>// libs.versions.toml
[plugins]
skie = { id = "co.touchlab.skie", version = "0.9.3" }

// shared/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.skie) // Enables SKIE code generation
}</code></pre>

        <h3>2. Consuming Kotlin StateFlow in SwiftUI</h3>
        <pre><code>import SwiftUI
import SharedKit

struct IncidentListView: View {
    @StateObject var viewModel = ObservableIncidentViewModel()

    var body: some View {
        List(viewModel.state.incidents, id: \\.id) { incident in
            Text(incident.title)
        }
        .task {
            // SKIE generates Swift async sequence observation:
            for await state in viewModel.uiState {
                viewModel.state = state
            }
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall 1: Cancellation Across the Swift/Kotlin Boundary</div>
          When consuming a Kotlin coroutine via Swift's <code>.task { }</code> modifier, canceling the Swift <code>Task</code> must cooperatively cancel the underlying Kotlin coroutine. SKIE handles this automatically, but raw Objective-C completion handlers will leave background Kotlin jobs running forever!
        </div>

        <div class="callout warning">
          <div class="callout-title">Pitfall 2: ARC vs. GC Cross-Runtime Retain Cycles</div>
          Swift uses Automatic Reference Counting (ARC) while Kotlin/Native uses tracing Garbage Collection (GC). If a Swift class holds a strong reference to a Kotlin object and passes a closure (capturing <code>self</code>) into a Kotlin callback, a cross-runtime cycle is created that neither ARC nor GC can break. Always use <code>[weak self]</code> in Swift closures passed to Kotlin!
        </div>
        """,
        "code_title": "Configure SKIE Compiler Plugin in Gradle",
        "code_filename": "build.gradle.kts",
        "code_instructions": "Add the SKIE plugin alias to the <code>plugins { ... }</code> block.",
        "starter_code": """plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // TODO: Add alias(libs.plugins.skie)
}""",
        "hints": [
            "Use: alias(libs.plugins.skie)"
        ],
        "final_solution": """plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.skie)
}""",
        "solution_explanation": "The Touchlab SKIE plugin hooks into the Kotlin/Native linking phase to generate Swift extension wrappers alongside the compiled framework.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasSkie = /alias\(libs\.plugins\.skie\)/.test(code);

          print("Checking SKIE plugin configuration...");
          if (hasSkie) print("✔ Touchlab SKIE plugin activated.");

          tests.push({
            name: "SKIE plugin alias configured",
            passed: hasSkie,
            message: "Must add alias(libs.plugins.skie) to plugins block."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary function of Touchlab's SKIE plugin during KMP compilation?",
            "options": [
                "It enhances Swift interop by bridging suspend to async/await and Flow to AsyncSequence.",
                "It converts all Kotlin code into pure Objective-C source files before invoking Clang.",
                "It replaces the Kotlin compiler with Apple's Swift compiler for all multiplatform code.",
                "It removes all memory management checks to speed up native execution on older iPhones."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "SKIE post-processes the Objective-C framework generated by Kotlin/Native and injects high-level Swift code so Swift developers can use async/await, AsyncSequence, and exhaustive enums.",
                "whyWrong": [
                    {"letter": "B", "reason": "SKIE enhances the exported framework; it does not replace Kotlin with Obj-C source."},
                    {"letter": "C", "reason": "Kotlin code is still compiled by Kotlin/Native."},
                    {"letter": "D", "reason": "SKIE does not alter Kotlin/Native's garbage collection rules."}
                ]
            }
        },
        "q2": {
            "question": "How does SKIE improve the developer experience when working with Kotlin sealed interfaces in Swift?",
            "options": [
                "It generates native Swift enums with associated values enabling exhaustive switch statements.",
                "It converts sealed interfaces into un-typed Objective-C dictionary maps.",
                "It forces all sealed interface implementations to be declared as global singletons.",
                "It prohibits Swift code from instantiating any subclasses created outside Xcode."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Normally, Kotlin sealed interfaces become empty Objective-C protocols in Swift, requiring messy `is` checks. SKIE generates a Swift enum with associated values, giving Swift developers 100% compile-time exhaustive `switch` matching.",
                "whyWrong": [
                    {"letter": "B", "reason": "SKIE provides strongly typed Swift enums, not untyped dictionaries."},
                    {"letter": "C", "reason": "Sealed interface subclasses are not singletons unless declared as `object`."},
                    {"letter": "D", "reason": "SKIE improves interop, it does not restrict subclassing capabilities."}
                ]
            }
        },
        "source_title": "Touchlab SKIE Documentation",
        "source_url": "https://skie.touchlab.co/",
        "source_link_text": "SKIE Official Documentation & Guide",
        "source_note": "Comprehensive guide to modern Swift/Kotlin coroutines and Flow interop.",
        "ref_content": """
        <h2>SKIE Feature Matrix</h2>
        <ul>
          <li><code>suspend fun</code> &rarr; Swift <code>async/await</code></li>
          <li><code>Flow&lt;T&gt;</code> &rarr; Swift <code>AsyncSequence</code></li>
          <li><code>sealed interface</code> &rarr; Swift <code>enum</code> with associated values (exhaustive switch)</li>
        </ul>
        """
    },

    # Lesson 10
    {
        "num": 10,
        "slug": "ktor3-client-architecture",
        "phase_tag": "Phase 3",
        "title": "Ktor 3.x Client Architecture & Platform Engines",
        "subtitle": "Build an enterprise HTTP client with Ktor 3.x, platform-specific engines (OkHttp & Darwin), and auto content negotiation.",
        "prev_slug": "0009-bridging-coroutines-to-swift-skie",
        "next_slug": "0011-kotlinx-serialization-advanced",
        "ref_slug": "10-ktor3-client-setup",
        "ref_title": "Ktor 3.x Engine Selection & Plugin Configuration",
        "mental_model": """
        <p>In KMP, networking is unified using <strong>Ktor 3.x</strong>. Unlike Retrofit (which is JVM-only), Ktor provides a multiplatform HTTP client engine abstraction:</p>
        <ul>
          <li><strong>Android Target</strong>: Uses the <code>OkHttp</code> engine, benefiting from Android's connection pooling and HTTP/2 multiplexing.</li>
          <li><strong>iOS Target</strong>: Uses the <code>Darwin</code> engine, backed by Apple's native <code>NSURLSession</code>. This ensures the app respects iOS system proxy settings, VPN tunnels, and cellular low-data modes.</li>
        </ul>
        <p>We declare an <code>expect fun createHttpClient(): HttpClient</code> or use dependency injection to supply the engine.</p>
        """,
        "production_code": """
        <h3>Factory Function with Platform Engine in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createKtorClient(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Using CIO Engine on iOS in Production</div>
          Ktor provides a pure-Kotlin engine called <code>CIO</code> (Coroutine I/O). While it works on iOS, it bypasses Apple's native <code>NSURLSession</code>, meaning system VPN configurations, custom corporate certificates, and cellular network constraints will not be respected. Always use <code>Darwin</code> on Apple targets!
        </div>
        """,
        "code_title": "Configure Multiplatform Ktor Client Factory",
        "code_filename": "HttpClientFactory.kt",
        "code_instructions": "Implement <code>createHttpClient(engine: HttpClientEngine): HttpClient</code> installing the <code>ContentNegotiation</code> plugin with Kotlinx JSON.",
        "starter_code": """import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun createHttpClient(engine: HttpClientEngine): HttpClient {
    // TODO: Instantiate HttpClient with engine and install ContentNegotiation { json() }
    return HttpClient(engine) {
    }
}""",
        "hints": [
            "Inside HttpClient block: install(ContentNegotiation) { json() }"
        ],
        "final_solution": """import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun createHttpClient(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }
    }
}""",
        "solution_explanation": "ContentNegotiation automates JSON serialization and deserialization based on incoming Content-Type headers, bridging directly to Kotlin data classes.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasInstall = /install\s*\(\s*ContentNegotiation\s*\)/.test(code);
          const hasJson = /json\s*\(\s*\)/.test(code);

          print("Inspecting Ktor 3 client configuration...");
          if (hasInstall) print("✔ ContentNegotiation plugin installed.");
          if (hasJson) print("✔ JSON serializer attached.");

          tests.push({
            name: "ContentNegotiation installed",
            passed: hasInstall,
            message: "Must install ContentNegotiation plugin."
          });
          tests.push({
            name: "JSON serializer registered",
            passed: hasJson,
            message: "Must register json() serializer inside ContentNegotiation."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why is the Darwin engine preferred over the CIO engine for iOS targets in production KMP apps?",
            "options": [
                "Darwin delegates to Apple's NSURLSession, respecting system proxies, VPNs, and network allowances.",
                "The CIO engine fails to compile on Apple targets due to LLVM thread limitations.",
                "Apple App Store review policies automatically reject applications containing CIO binaries.",
                "Darwin converts all HTTP response payloads directly into Objective-C NSDictionary instances."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Apple devices require NSURLSession to properly handle background network transfers, system-level VPN tunnels, corporate proxy profiles, and cellular low-data mode.",
                "whyWrong": [
                    {"letter": "B", "reason": "CIO compiles on iOS, but lacks system networking integration."},
                    {"letter": "C", "reason": "App Store review does not reject CIO, but users experience connectivity issues on corporate VPNs."},
                    {"letter": "D", "reason": "Darwin returns standard Ktor HttpResponse objects in Kotlin, not NSDictionary."}
                ]
            }
        },
        "q2": {
            "question": "Which Ktor 3.x plugin handles automatic serialization and deserialization of request/response payloads?",
            "options": [
                "The ContentNegotiation plugin configuring serializers like Kotlinx JSON or Protocol Buffers.",
                "The HttpTimeout plugin setting socket, connect, and request duration boundaries.",
                "The Auth plugin managing Bearer tokens and automatic 401 challenge refreshes.",
                "The WebSockets plugin establishing continuous full-duplex TCP streaming connections."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "ContentNegotiation inspects HTTP Content-Type headers (e.g. application/json) and converts bytes into strongly typed Kotlin @Serializable data classes.",
                "whyWrong": [
                    {"letter": "B", "reason": "HttpTimeout manages socket/connection timeouts."},
                    {"letter": "C", "reason": "Auth plugin manages tokens and headers."},
                    {"letter": "D", "reason": "WebSockets is for real-time duplex channels."}
                ]
            }
        },
        "source_title": "Ktor 3.x Official Documentation",
        "source_url": "https://ktor.io/docs/client-create-multiplatform-application.html",
        "source_link_text": "Creating Multiplatform HTTP Clients with Ktor",
        "source_note": "Official guide on multiplatform Ktor 3.x engine selection and configuration.",
        "ref_content": """
        <h2>Ktor 3 Platform Engine Mapping</h2>
        <ul>
          <li><strong>androidMain</strong>: <code>io.ktor:ktor-client-okhttp</code></li>
          <li><strong>iosMain</strong>: <code>io.ktor:ktor-client-darwin</code></li>
        </ul>
        """
    },

    # Lesson 11
    {
        "num": 11,
        "slug": "kotlinx-serialization-advanced",
        "phase_tag": "Phase 3",
        "title": "Kotlinx.Serialization & Advanced Data Parsing",
        "subtitle": "Master polymorphic JSON decoding, custom date/time serializers, and robust parsing of unknown or missing API fields.",
        "prev_slug": "0010-ktor3-client-architecture",
        "next_slug": "0012-network-resilience-auth-token-refresh",
        "ref_slug": "11-kotlinx-serialization-recipes",
        "ref_title": "Kotlinx.Serialization Custom Serializers & Polymorphism",
        "mental_model": """
        <p>In multiplatform development, we cannot use reflection-based parsers like Gson or Jackson because Kotlin/Native does not support full runtime Java reflection. Instead, we use <strong>kotlinx.serialization</strong>, which generates serialization logic at <strong>compile-time</strong> via a compiler plugin.</p>
        <p>Key production techniques:</p>
        <ul>
          <li><strong>ignoreUnknownKeys = true</strong>: Prevents client crashes when backend APIs introduce new fields.</li>
          <li><strong>Custom Serializers</strong>: Used for parsing types without native support (such as ISO-8601 timestamps or UUIDs).</li>
          <li><strong>Polymorphic Deserialization</strong>: Handling heterogeneous event feeds using sealed classes tagged with a <code>"type"</code> discriminator.</li>
        </ul>
        """,
        "production_code": """
        <h3>Polymorphic Event Stream Parsing</h3>
        <pre><code>package com.pulsesync.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
sealed interface ServerEvent {
    @Serializable
    @SerialName("incident_created")
    data class IncidentCreated(val id: String, val title: String) : ServerEvent

    @Serializable
    @SerialName("incident_resolved")
    data class IncidentResolved(val id: String, val resolvedAt: Long) : ServerEvent
}

val jsonConfig = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "event_type" // Matches backend JSON discriminator
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Omitting ignoreUnknownKeys</div>
          By default, <code>Json { }</code> has <code>ignoreUnknownKeys = false</code>. If your backend adds a single new field to an API response, your app will immediately crash with a <code>SerializationException</code>. Always configure <code>ignoreUnknownKeys = true</code> in production!
        </div>
        """,
        "code_title": "Configure Robust Json Parser & Data Class",
        "code_filename": "PayloadParser.kt",
        "code_instructions": "Define an <code>@Serializable</code> data class <code>UserSession(val token: String, val userId: String)</code> and a <code>Json</code> instance with <code>ignoreUnknownKeys = true</code>.",
        "starter_code": """import kotlinx.serialization.*
import kotlinx.serialization.json.*

// TODO: 1. Annotate UserSession with @Serializable
data class UserSession(val token: String, val userId: String)

// TODO: 2. Configure appJson with ignoreUnknownKeys = true
val appJson = Json {
}""",
        "hints": [
            "@Serializable data class UserSession(...)",
            "val appJson = Json { ignoreUnknownKeys = true }"
        ],
        "final_solution": """import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class UserSession(val token: String, val userId: String)

val appJson = Json {
    ignoreUnknownKeys = true
}""",
        "solution_explanation": "The @Serializable annotation generates the KSerializer companion at compile time. Setting ignoreUnknownKeys = true provides forward compatibility against backend API changes.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasSerializable = /@Serializable\s+data\s+class\s+UserSession/.test(code);
          const hasIgnore = /ignoreUnknownKeys\s*=\s*true/.test(code);

          print("Verifying compile-time serialization configuration...");
          if (hasSerializable) print("✔ @Serializable annotation present.");
          if (hasIgnore) print("✔ ignoreUnknownKeys enabled.");

          tests.push({
            name: "@Serializable applied to UserSession",
            passed: hasSerializable,
            message: "Must annotate UserSession with @Serializable."
          });
          tests.push({
            name: "ignoreUnknownKeys set to true in Json configuration",
            passed: hasIgnore,
            message: "Must configure ignoreUnknownKeys = true inside Json { }."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why can reflection-based JSON libraries (like Jackson or Gson) not be used in Kotlin Multiplatform projects?",
            "options": [
                "Kotlin/Native targets compile to machine code and lack full Java Virtual Machine reflection runtime.",
                "Apple App Store policies explicitly forbid any network parsing library that consumes JSON.",
                "The Gradle Kotlin DSL plugin fails to link any library containing more than ten classes.",
                "Reflection libraries force Android devices to run code in 32-bit compatibility mode."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Kotlin/Native compiles to native machine binaries via LLVM. It has no JVM classloader or Java reflection runtime. kotlinx.serialization works because it generates serializers at compile-time.",
                "whyWrong": [
                    {"letter": "B", "reason": "Apple permits JSON parsing; all modern apps parse JSON."},
                    {"letter": "C", "reason": "Gradle handles thousands of classes with ease."},
                    {"letter": "D", "reason": "Reflection has no relationship to 32-bit vs 64-bit architecture."}
                ]
            }
        },
        "q2": {
            "question": "What is the purpose of the @SerialName annotation in kotlinx.serialization?",
            "options": [
                "It specifies the exact JSON key name when it differs from the Kotlin property identifier.",
                "It enables the Kotlin compiler to encrypt the annotated property before disk storage.",
                "It converts snake_case network payloads into camelCase variables using runtime regex.",
                "It registers the annotated field inside the Apple iOS Keychain hardware enclave."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "@SerialName('user_id') maps the JSON property 'user_id' directly to a Kotlin property like 'userId', avoiding manual string key mapping.",
                "whyWrong": [
                    {"letter": "B", "reason": "@SerialName does not encrypt data."},
                    {"letter": "C", "reason": "Mapping is compile-time, not runtime regex."},
                    {"letter": "D", "reason": "It has no relationship to the Apple Keychain."}
                ]
            }
        },
        "source_title": "Kotlinx Serialization Guide",
        "source_url": "https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md",
        "source_link_text": "Kotlinx Serialization Basics & Recipes",
        "source_note": "Official guide on compile-time serialization, custom serializers, and JSON options.",
        "ref_content": """
        <h2>Essential Json Configuration Options</h2>
        <pre><code>val json = Json {
    ignoreUnknownKeys = true // Never crash on new backend fields
    isLenient = true         // Accept non-strict JSON values
    prettyPrint = false      // Production speed
    encodeDefaults = true   // Include default property values in output
}</code></pre>
        """
    },

    # Lesson 12
    {
        "num": 12,
        "slug": "network-resilience-auth-token-refresh",
        "phase_tag": "Phase 3",
        "title": "Production Network Resilience & Authentication",
        "subtitle": "Implement automated OAuth2 token refresh mutexes, exponential backoff retries, and typed Result error modeling.",
        "prev_slug": "0011-kotlinx-serialization-advanced",
        "next_slug": "0013-room-kmp-vs-sqldelight",
        "ref_slug": "12-ktor-auth-token-refresh",
        "ref_title": "Ktor 3 Auth Plugin & Exponential Backoff Mutex",
        "mental_model": """
        <p>In production mobile apps, network requests fail due to flaky cellular towers, expired JWT tokens, and rate limits. A robust KMP networking layer requires:</p>
        <ol>
          <li><strong>Ktor Auth Plugin</strong>: Automatically intercepts <code>401 Unauthorized</code> responses, calls a refresh endpoint, stores the new token in <code>SecureStorage</code>, and retries the original request seamlessly.</li>
          <li><strong>Refresh Mutex</strong>: If 5 concurrent requests fail with 401 simultaneously, they must wait for a single refresh call rather than firing 5 duplicate refresh requests.</li>
          <li><strong>Domain Result Modeling</strong>: Never let raw HTTP exceptions bubble into ViewModels. Map responses into a typed <code>Result&lt;T, NetworkError&gt;</code>.</li>
        </ol>
        """,
        "production_code": """
        <h3>Ktor 3.x Bearer Auth with Token Refresh</h3>
        <pre><code>package com.pulsesync.network

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

fun configureAuth(client: HttpClient, tokenProvider: TokenStorage) {
    client.plugin(Auth).bearer {
        loadTokens {
            val access = tokenProvider.getAccessToken()
            val refresh = tokenProvider.getRefreshToken()
            if (access != null && refresh != null) {
                BearerTokens(access, refresh)
            } else null
        }
        refreshTokens {
            // Executed automatically on 401 Unauthorized (Thread-safe mutex built into Ktor!)
            val newTokens = callRefreshApi(oldTokens?.refreshToken)
            tokenProvider.saveTokens(newTokens.accessToken, newTokens.refreshToken)
            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
        }
    }
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Refresh Endpoint Returning 401 Triggers Infinite Loop</div>
          Ensure your token refresh API call does NOT use the authenticated HttpClient instance! If the refresh token itself is expired, the refresh call will return 401, triggering an infinite recursive refresh loop. Use an unauthenticated client for the refresh call.
        </div>
        """,
        "code_title": "Model Domain Network Error Types",
        "code_filename": "NetworkError.kt",
        "code_instructions": "Define a sealed interface <code>NetworkError</code> with subtypes: <code>Unauthorized</code>, <code>NoInternet</code>, and <code>ServerError(val code: Int)</code>.",
        "starter_code": """// TODO: Define sealed interface NetworkError with Unauthorized, NoInternet, ServerError(code: Int)""",
        "hints": [
            "sealed interface NetworkError",
            "data object Unauthorized : NetworkError",
            "data object NoInternet : NetworkError",
            "data class ServerError(val code: Int) : NetworkError"
        ],
        "final_solution": """sealed interface NetworkError {
    data object Unauthorized : NetworkError
    data object NoInternet : NetworkError
    data class ServerError(val code: Int) : NetworkError
}""",
        "solution_explanation": "Sealed interfaces enable exhaustive compile-time error handling across ViewModels without generic catch-all blocks.",
        "execute_fn_js": r"""function(code, print) {
          const tests = [];
          const hasInterface = /sealed\s+interface\s+NetworkError/.test(code);
          const hasUnauth = /(data\s+object|object)\s+Unauthorized\s*:\s*NetworkError/.test(code);
          const hasNoInternet = /(data\s+object|object)\s+NoInternet\s*:\s*NetworkError/.test(code);
          const hasServerErr = /data\s+class\s+ServerError\s*\(\s*val\s+code\s*:\s*Int\s*\)\s*:\s*NetworkError/.test(code);

          print("Validating domain NetworkError model...");
          if (hasInterface) print("✔ Sealed interface NetworkError declared.");
          if (hasUnauth && hasNoInternet && hasServerErr) print("✔ All error variants defined.");

          tests.push({
            name: "sealed interface NetworkError declared",
            passed: hasInterface,
            message: "Must declare 'sealed interface NetworkError'."
          });
          tests.push({
            name: "All required error subtypes declared",
            passed: hasUnauth && hasNoInternet && hasServerErr,
            message: "Must declare Unauthorized, NoInternet, and ServerError(code: Int)."
          });
          return { tests };
        }""",
        "q1": {
            "question": "How does Ktor's Bearer Auth plugin handle multiple concurrent requests that fail with 401 simultaneously?",
            "options": [
                "It uses an internal mutex to execute the refreshTokens block once and suspends other callers.",
                "It fires parallel refresh requests for every failing request and keeps the fastest token.",
                "It cancels all pending requests and triggers an immediate application restart.",
                "It redirects all network traffic through a local mock HTTP server until app relaunch."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Ktor's Auth plugin includes built-in concurrency synchronization. When a 401 occurs, it acquires a mutex, executes refreshTokens once, saves the new tokens, and replays all queued requests.",
                "whyWrong": [
                    {"letter": "B", "reason": "Parallel refresh calls would cause race conditions and invalidate refresh tokens."},
                    {"letter": "C", "reason": "It does not restart the application; it handles it seamlessly."},
                    {"letter": "D", "reason": "It never redirects to a mock server."}
                ]
            }
        },
        "q2": {
            "question": "Why must the HTTP client used to call the refresh endpoint NOT install the Auth plugin?",
            "options": [
                "An expired refresh token returning 401 would trigger an infinite recursive refresh loop.",
                "Apple's Darwin networking framework rejects any request that lacks an Authorization header.",
                "The K2 compiler prohibits installing the Auth plugin on more than one client in a project.",
                "Android Keystore keys are permanently deleted whenever an unauthenticated request is made."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "If the refresh client has the Auth plugin, a 401 from the refresh endpoint triggers the plugin again, causing an infinite loop. The refresh client must be unauthenticated.",
                "whyWrong": [
                    {"letter": "B", "reason": "Darwin supports unauthenticated requests without issue."},
                    {"letter": "C", "reason": "A project can configure multiple HttpClient instances with different plugins."},
                    {"letter": "D", "reason": "Keystore keys are unaffected by network responses."}
                ]
            }
        },
        "source_title": "Ktor Auth Plugin Documentation",
        "source_url": "https://ktor.io/docs/client-bearer-auth.html",
        "source_link_text": "Ktor Client Bearer Authentication",
        "source_note": "Detailed guide on token refresh, loadTokens, and BearerTokens lifecycle.",
        "ref_content": """
        <h2>Token Refresh Architecture</h2>
        <ul>
          <li><strong>loadTokens</strong>: Reads initial access + refresh tokens from SecureStorage.</li>
          <li><strong>refreshTokens</strong>: Executed on 401 Unauthorized via built-in mutex.</li>
          <li><strong>Rule</strong>: Use a dedicated unauthenticated HttpClient for the refresh call!</li>
        </ul>
        """
    }
]

for l in lessons:
    generate_lesson_file(l)

print("Lessons 06 to 12 (Phase 1, 2, 3) successfully generated!")
