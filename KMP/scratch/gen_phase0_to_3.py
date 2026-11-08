import sys
import os
sys.path.append(os.path.dirname(__file__))
from generator_base import generate_lesson_file

lessons = [
    # Lesson 02
    {
        "num": 2,
        "slug": "kotlin-2-k2-compiler-pipeline",
        "phase_tag": "Phase 0",
        "title": "Kotlin 2.x & The K2 Compiler Pipeline",
        "subtitle": "Understand the K2 frontend architecture, IR backend transformations, and the integrated Compose compiler plugin.",
        "prev_slug": "0001-anatomy-of-a-modern-kmp-project",
        "next_slug": "0003-apple-toolchain-xcframework-spm",
        "ref_slug": "02-k2-compiler-flags",
        "ref_title": "K2 Compiler Flags & Diagnostic Matrix",
        "mental_model": """
        <p>In pre-Kotlin 2.0, the compiler (K1) analyzed code through tightly coupled frontend passes that caused long build times and inconsistent IDE analysis. <strong>Kotlin 2.0 replaces this with K2</strong>, featuring a unified frontend Intermediate Representation (FIR) shared between the IDE and build tools.</p>
        <p>Key architectural upgrades in K2:</p>
        <ul>
          <li><strong>Frontend IR (FIR)</strong>: Faster semantic type analysis and unified frontend checks across JVM and Native.</li>
          <li><strong>Built-in Compose Compiler</strong>: The Compose compiler plugin is now maintained directly in the Kotlin repository, eliminating Kotlin/Compose version mismatch headaches.</li>
          <li><strong>Consistent Smart Casts</strong>: K2 can smart cast properties across closures and open variables where K1 failed.</li>
        </ul>
        """,
        "production_code": """
        <h3>Activating K2 & Compose Compiler in <code>build.gradle.kts</code></h3>
        <pre><code>plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler) // Native Kotlin 2.x Compose plugin
}

kotlin {
    compilerOptions {
        // Enforce K2 language features and strict multiplatform checks
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

composeCompiler {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Legacy Compose Compiler Coordinates</div>
          Do not import <code>androidx.compose.compiler:compiler</code> in Kotlin 2.x projects. Modern KMP requires the JetBrains Compose Compiler plugin defined via <code>org.jetbrains.kotlin.plugin.compose</code>.
        </div>
        """,
        "code_title": "Configure Kotlin 2.x Compiler Flags",
        "code_filename": "build.gradle.kts",
        "code_instructions": "Configure the <code>kotlin { compilerOptions { ... } }</code> block to enforce <code>KotlinVersion.KOTLIN_2_1</code> and enable <code>allWarningsAsErrors.set(true)</code>.",
        "starter_code": """kotlin {
    compilerOptions {
        // TODO: Set languageVersion to KotlinVersion.KOTLIN_2_1
        // TODO: Enable allWarningsAsErrors
    }
}""",
        "hints": [
            "Use: languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)",
            "Use: allWarningsAsErrors.set(true)"
        ],
        "final_solution": """kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        allWarningsAsErrors.set(true)
    }
}""",
        "solution_explanation": "Configuring explicit languageVersion and warning enforcement in the root compilerOptions block ensures both Kotlin/JVM and Kotlin/Native targets maintain identical compilation standards.",
        "execute_fn_js": """function(code, print) {
          const tests = [];
          const hasVersion = /languageVersion\.set\(.*?KOTLIN_2_1\)/.test(code);
          const hasWarnings = /allWarningsAsErrors\.set\(true\)/.test(code);
          
          print("Checking K2 compiler options...");
          if (hasVersion) print("✔ Language version 2.1 detected.");
          if (hasWarnings) print("✔ Strict warnings as errors enabled.");

          tests.push({
            name: "languageVersion set to KOTLIN_2_1",
            passed: hasVersion,
            message: "Must configure languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)."
          });
          tests.push({
            name: "allWarningsAsErrors set to true",
            passed: hasWarnings,
            message: "Must set allWarningsAsErrors.set(true)."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary benefit of the unified Frontend IR (FIR) in the K2 compiler architecture?",
            "options": [
                "It enables the IDE and build tools to share identical semantic analysis algorithms.",
                "It eliminates the need for LLVM backend compilation when compiling Kotlin to Apple iOS.",
                "It automatically converts all Java libraries into native Objective-C header declarations.",
                "It converts suspend functions into blocking threads to speed up runtime execution."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "K2 unifies the frontend code analysis so that the IDE analyzer and command-line Gradle compiler use identical FIR pipelines, preventing mismatched errors.",
                "whyWrong": [
                    {"letter": "B", "reason": "Kotlin/Native still relies on the LLVM backend for code generation; FIR only handles frontend semantic parsing."},
                    {"letter": "C", "reason": "FIR does not convert Java to Objective-C; interop is handled at the target linkage stage."},
                    {"letter": "D", "reason": "Suspending functions are never converted to blocking threads; Coroutines preserve non-blocking semantics."}
                ]
            }
        },
        "q2": {
            "question": "Where is the Compose compiler plugin officially hosted starting with Kotlin 2.0?",
            "options": [
                "Directly inside the official Kotlin repository under the org.jetbrains.kotlin namespace.",
                "Inside the AndroidX Jetpack Maven repository under the androidx.compose.compiler groupId.",
                "Inside the Google Play Services repository alongside the Android Gradle Plugin tooling.",
                "Inside an independent third-party GitHub repository maintained by the Cash App team."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Starting in Kotlin 2.0, JetBrains integrated the Compose compiler directly into the Kotlin compiler repository, ensuring zero version lag on new Kotlin releases.",
                "whyWrong": [
                    {"letter": "B", "reason": "AndroidX stopped developing the standalone compiler plugin after Kotlin 1.9.x."},
                    {"letter": "C", "reason": "Google Play Services is a client runtime SDK, not a Gradle compiler plugin repository."},
                    {"letter": "D", "reason": "Square/Cash App maintains libraries like SQLDelight, not the Kotlin Compose compiler plugin."}
                ]
            }
        },
        "source_title": "Kotlin Foundation Official Announcement",
        "source_url": "https://kotlinlang.org/docs/whatsnew20.html",
        "source_link_text": "What's New in Kotlin 2.0.0",
        "source_note": "Read the official JetBrains architectural deep-dive into K2 and Compose compiler bundling.",
        "ref_content": """
        <h2>1. K2 Compiler Flags Cheatsheet</h2>
        <pre><code>kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        allWarningsAsErrors.set(true)
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xexpect-actual-classes"
        )
    }
}</code></pre>
        <h2>2. Compose Compiler Strong Skipping</h2>
        <pre><code>composeCompiler {
    enableStrongSkippingMode = true
}</code></pre>
        """
    },

    # Lesson 03
    {
        "num": 3,
        "slug": "apple-toolchain-xcframework-spm",
        "phase_tag": "Phase 0",
        "title": "The Apple Toolchain: CocoaPods vs. SPM vs. Direct XCFrameworks",
        "subtitle": "Master Kotlin/Native Apple compilation targets, XCFramework generation, and modern Swift Package Manager (SPM) distribution.",
        "prev_slug": "0002-kotlin-2-k2-compiler-pipeline",
        "next_slug": "0004-multiplatform-architecture-mindset",
        "ref_slug": "03-apple-xcframework-spm",
        "ref_title": "Apple Target Linkage & SPM Matrix",
        "mental_model": """
        <p>To run on iOS, Kotlin code must be compiled by Kotlin/Native into an Apple-compatible binary artifact. Historically, CocoaPods was used. Today, the industry has migrated heavily toward <strong>XCFrameworks and Swift Package Manager (SPM)</strong>.</p>
        <p>An <strong>XCFramework</strong> is an Apple binary package containing multiple architecture slices (e.g. <code>ios-arm64</code> for real devices and <code>ios-arm64_x86_64-simulator</code> for Xcode simulators) in a single bundle.</p>
        """,
        "production_code": """
        <h3>Exporting an XCFramework in <code>build.gradle.kts</code></h3>
        <pre><code>import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

kotlin {
    val xcf = XCFramework("SharedKit")
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedKit"
            isStatic = true
            xcf.add(this)
        }
    }
}</code></pre>
        <p>Running <code>./gradlew assembleSharedKitXCFramework</code> produces the complete bundle inside <code>build/XCFrameworks/release/SharedKit.xcframework</code>.</p>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Missing Simulator Arm64 Slice</div>
          On Apple Silicon Macs (M1/M2/M3/M4), Xcode runs simulators in <code>arm64</code>. If your build only includes <code>iosX64()</code> (Intel simulator), Xcode will fail to link on Apple Silicon hardware. Always include <code>iosSimulatorArm64()</code>!
        </div>
        """,
        "code_title": "Declare Multi-Slice XCFramework Packaging",
        "code_filename": "build.gradle.kts",
        "code_instructions": "Create an XCFramework named <code>\"SharedKit\"</code> and add both <code>iosArm64()</code> and <code>iosSimulatorArm64()</code> targets to it.",
        "starter_code": """import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

kotlin {
    // TODO: Create val xcf = XCFramework("SharedKit")
    // TODO: Iterate over listOf(iosArm64(), iosSimulatorArm64()) and add to xcf
}""",
        "hints": [
            "Instantiate: val xcf = XCFramework(\"SharedKit\")",
            "In target.binaries.framework: call xcf.add(this)"
        ],
        "final_solution": """import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

kotlin {
    val xcf = XCFramework("SharedKit")
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "SharedKit"
            isStatic = true
            xcf.add(this)
        }
    }
}""",
        "solution_explanation": "The XCFramework packaging task unifies the device slice (iosArm64) and simulator slice (iosSimulatorArm64) into a single folder that Xcode can import with zero manual lipo commands.",
        "execute_fn_js": """function(code, print) {
          const tests = [];
          const hasXcf = /XCFramework\("SharedKit"\)/.test(code);
          const hasTargets = /iosArm64\(\)/.test(code) && /iosSimulatorArm64\(\)/.test(code);
          const hasAdd = /xcf\.add\(this\)/.test(code);

          print("Inspecting Apple target configuration...");
          if (hasXcf) print("✔ XCFramework bundle instantiated.");
          if (hasTargets) print("✔ Physical arm64 and simulator slices declared.");
          if (hasAdd) print("✔ Framework targets attached to XCFramework task.");

          tests.push({
            name: "XCFramework created with bundle name 'SharedKit'",
            passed: hasXcf,
            message: "Must instantiate XCFramework('SharedKit')."
          });
          tests.push({
            name: "Declared iosArm64 and iosSimulatorArm64 targets",
            passed: hasTargets,
            message: "Must target both iosArm64() and iosSimulatorArm64()."
          });
          tests.push({
            name: "Binary framework added to XCFramework",
            passed: hasAdd,
            message: "Must call xcf.add(this) inside binaries.framework."
          });
          return { tests };
        }""",
        "q1": {
            "question": "What is the primary advantage of XCFramework bundles over legacy universal Fat (.framework) binaries?",
            "options": [
                "They bundle distinct architecture slices sharing identical names like device and simulator arm64.",
                "They eliminate the need to compile native Kotlin source code by converting all code to JavaScript.",
                "They bypass all Apple App Store code signing checks and notary requirements upon release.",
                "They permit Android APK files to embed Apple binaries directly within the Google Play bundle."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Universal 'fat' binaries created with lipo could not contain two slices with the same architecture name (such as arm64 for physical iPhone and arm64 for Apple Silicon Mac Simulator). XCFrameworks solved this by isolating each slice in distinct subdirectories.",
                "whyWrong": [
                    {"letter": "B", "reason": "XCFrameworks contain compiled native machine code, not JavaScript."},
                    {"letter": "C", "reason": "Apple still strictly enforces code-signing and notarization on XCFrameworks."},
                    {"letter": "D", "reason": "Android APKs run on Android runtimes; Apple binaries are never embedded in APKs."}
                ]
            }
        },
        "q2": {
            "question": "Which diagnostic command-line utility from JetBrains verifies that your local macOS environment is ready for KMP?",
            "options": [
                "The kdoctor CLI tool checking Xcode, CocoaPods, JDK installations, and Apple toolchain paths.",
                "The brew doctor tool that inspects third-party package dependencies across the Homebrew system.",
                "The flutter doctor command validating Dart SDK versions and connected Android debugging bridges.",
                "The gradle check command executing all unit tests and lint checks in the local repository."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "kdoctor is JetBrains' official diagnostic CLI for Kotlin Multiplatform. It inspects JDK, Xcode, CocoaPods, and environment variables to ensure zero build hurdles.",
                "whyWrong": [
                    {"letter": "B", "reason": "brew doctor verifies Homebrew packages, not KMP/Xcode/JDK setups."},
                    {"letter": "C", "reason": "flutter doctor is for the Flutter/Dart ecosystem, not Kotlin Multiplatform."},
                    {"letter": "D", "reason": "gradle check runs project tests, but does not verify local machine SDK installations."}
                ]
            }
        },
        "source_title": "JetBrains Native Documentation",
        "source_url": "https://kotlinlang.org/docs/native-spm.html",
        "source_link_text": "Swift Package Manager Integration for KMP",
        "source_note": "Official guide to distributing KMP artifacts via Swift Package Manager.",
        "ref_content": """
        <h2>1. Assemble XCFramework CLI Command</h2>
        <pre><code>./gradlew assembleSharedKitXCFramework</code></pre>
        <h2>2. kdoctor Diagnostic Command</h2>
        <pre><code>kdoctor</code></pre>
        """
    },

    # Lesson 04
    {
        "num": 4,
        "slug": "multiplatform-architecture-mindset",
        "phase_tag": "Phase 1",
        "title": "The Multiplatform Architecture Mindset",
        "subtitle": "Adopt Clean Architecture, define strict layer boundaries, and understand what to share vs. what to keep platform-native.",
        "prev_slug": "0003-apple-toolchain-xcframework-spm",
        "next_slug": "0005-expect-actual-mechanisms-antipatterns",
        "ref_slug": "04-kmp-clean-architecture",
        "ref_title": "Clean Architecture Layer Boundaries in KMP",
        "mental_model": """
        <p>In high-performing KMP organizations, teams don't share code for the sake of sharing code. They follow the <strong>80/20 Architectural Rule</strong>:</p>
        <ul>
          <li><strong>Share (80-90%)</strong>: Domain Use Cases, Repositories, Database caching, HTTP networking, Serialization, Reactive State machines (ViewModels/MVI).</li>
          <li><strong>Specialize (10-20%)</strong>: Platform hardware (Bluetooth, Camera, Biometrics), native push notification handlers, and system-level widgets.</li>
        </ul>
        <p>Clean Architecture enforces dependency inversion: the <strong>Domain Layer (commonMain)</strong> has zero dependencies on Android SDK or Apple UIKit/Foundation.</p>
        """,
        "production_code": """
        <h3>Clean Architecture Domain Interface in <code>commonMain</code></h3>
        <pre><code>package com.pulsesync.domain.model

data class Incident(
    val id: String,
    val title: String,
    val severity: Severity,
    val timestamp: Long
)

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

// Domain Repository Contract (Zero platform dependencies)
interface IncidentRepository {
    fun observeIncidents(): kotlinx.coroutines.flow.Flow<List<Incident>>
    suspend fun resolveIncident(id: String): Result<Unit>
}</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Pitfall: Leaking Android Context into Domain Models</div>
          Never pass <code>android.content.Context</code> into domain use cases or shared repositories. If an operation requires a file path or system handle, wrap it behind a platform-agnostic interface injected via DI.
        </div>
        """,
        "code_title": "Define Domain Model & Repository Contract",
        "code_filename": "IncidentDomain.kt",
        "code_instructions": "Define an enum <code>Severity</code> with <code>LOW, HIGH</code>, and an <code>IncidentRepository</code> interface with <code>suspend fun getActiveIncidents(): List<String></code>.",
        "starter_code": """// TODO: 1. Define enum class Severity
// TODO: 2. Define interface IncidentRepository with suspend fun getActiveIncidents(): List<String>""",
        "hints": [
            "enum class Severity { LOW, HIGH }",
            "interface IncidentRepository { suspend fun getActiveIncidents(): List<String> }"
        ],
        "final_solution": """enum class Severity { LOW, HIGH }

interface IncidentRepository {
    suspend fun getActiveIncidents(): List<String>
}""",
        "solution_explanation": "Domain interfaces in commonMain remain pure Kotlin without platform imports, ensuring they can be compiled and unit tested on any JVM or Native target.",
        "execute_fn_js": """function(code, print) {
          const tests = [];
          const hasEnum = /enum\s+class\s+Severity\s*\{[\s\S]*?LOW[\s\S]*?HIGH[\s\S]*?\}/.test(code);
          const hasRepo = /interface\s+IncidentRepository\s*\{[\s\S]*?suspend\s+fun\s+getActiveIncidents\(\s*\)\s*:\s*List<String>[\s\S]*?\}/.test(code);

          print("Validating Clean Architecture domain declarations...");
          if (hasEnum) print("✔ Severity enum declared.");
          if (hasRepo) print("✔ IncidentRepository suspend contract defined.");

          tests.push({
            name: "Severity enum declared with LOW and HIGH",
            passed: hasEnum,
            message: "Must declare enum class Severity { LOW, HIGH }."
          });
          tests.push({
            name: "IncidentRepository defines suspend fun getActiveIncidents",
            passed: hasRepo,
            message: "Must define interface IncidentRepository with suspend fun getActiveIncidents(): List<String>."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why should the Domain layer in a Kotlin Multiplatform app remain completely free of platform-specific imports?",
            "options": [
                "It guarantees domain business rules can compile, execute, and be tested across all target runtimes.",
                "It enables the Kotlin compiler to convert all domain logic into static C header files automatically.",
                "It prevents the Android Gradle Plugin from applying code minification and ProGuard optimization.",
                "It forces mobile devices to execute business logic entirely inside a remote cloud server function."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "A pure Kotlin Domain layer has zero dependencies on Android or Apple SDKs, meaning it can be compiled for Android, iOS, Desktop, and tested with standard kotlin.test without mocks for Android Context or UIKit.",
                "whyWrong": [
                    {"letter": "B", "reason": "C headers are only generated if Kotlin/Native explicitly exports an objective-c/c binary, not automatically."},
                    {"letter": "C", "reason": "ProGuard/R8 can optimize pure Kotlin code just as effectively as platform code."},
                    {"letter": "D", "reason": "Domain code executes locally on the device, not remotely."}
                ]
            }
        },
        "q2": {
            "question": "Which layer in a KMP application is responsible for orchestrating caching, remote networking, and database persistence?",
            "options": [
                "The Data Layer which implements repository contracts defined by the pure Domain layer.",
                "The Presentation Layer which hosts Compose composables and SwiftUI view structures.",
                "The UI Layer which handles touch dispatching, animations, and typography themes.",
                "The Platform Layer which interacts directly with Apple App Store receipt validators."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "In Clean Architecture, the Data layer contains Repository implementations that coordinate local database caching (Room/SQLDelight) and remote networking (Ktor).",
                "whyWrong": [
                    {"letter": "B", "reason": "The Presentation layer only manages UI state and ViewModel orchestration."},
                    {"letter": "C", "reason": "The UI layer renders visuals, not data caching or networking."},
                    {"letter": "D", "reason": "Platform layer handles platform-specific hardware/APIs, not general persistence."}
                ]
            }
        },
        "source_title": "Touchlab Multiplatform Architecture",
        "source_url": "https://touchlab.co/kmp-clean-architecture",
        "source_link_text": "Clean Architecture for KMP Mobile Teams",
        "source_note": "Touchlab's architectural guide to layer boundaries and shared mobile modules.",
        "ref_content": """
        <h2>Clean Architecture Boundary Rules</h2>
        <ul>
          <li><strong>commonMain (Domain)</strong>: Entities, Use Cases, Repository Interfaces. Zero Android/Apple SDK dependencies.</li>
          <li><strong>commonMain (Data)</strong>: Repository Implementations, Ktor Client, SQLDelight/Room Database.</li>
          <li><strong>Presentation</strong>: ViewModels, StateFlow, Compose Multiplatform or SwiftUI.</li>
        </ul>
        """
    },

    # Lesson 05
    {
        "num": 5,
        "slug": "expect-actual-mechanisms-antipatterns",
        "phase_tag": "Phase 1",
        "title": "The expect/actual Mechanism & Anti-Patterns",
        "subtitle": "Learn the mechanics of expected declarations and why senior engineers favor polymorphism and DI over expect/actual.",
        "prev_slug": "0004-multiplatform-architecture-mindset",
        "next_slug": "0006-hardware-and-system-interop",
        "ref_slug": "05-expect-actual-best-practices",
        "ref_title": "expect/actual Rules & Anti-Patterns Reference",
        "mental_model": """
        <p>The <code>expect</code>/<code>actual</code> mechanism is Kotlin's language-level feature for declaring a contract in <code>commonMain</code> and fulfilling it in platform source sets (<code>androidMain</code>, <code>iosMain</code>).</p>
        <p>However, <strong>overusing <code>expect</code>/<code>actual</code> is one of the most common anti-patterns in KMP</strong>:</p>
        <ul>
          <li>It tightly couples common code to compile-time platform branches.</li>
          <li>It makes unit testing difficult because you cannot easily swap in a mock or fake implementation.</li>
          <li><strong>Best Practice</strong>: Use <code>expect</code>/<code>actual</code> for low-level foundational primitives (e.g. platform dispatchers, UUID generation, timestamps). For business services and hardware APIs, use <strong>polymorphic interfaces injected via Dependency Injection</strong>.</li>
        </ul>
        """,
        "production_code": """
        <h3>Idiomatic <code>expect</code>/<code>actual</code>: Generating UUIDs</h3>
        <p><strong>commonMain:</strong></p>
        <pre><code>package com.pulsesync.core

expect fun generateUuid(): String</code></pre>

        <p><strong>androidMain:</strong></p>
        <pre><code>package com.pulsesync.core

actual fun generateUuid(): String = java.util.UUID.randomUUID().toString()</code></pre>

        <p><strong>iosMain:</strong></p>
        <pre><code>package com.pulsesync.core
import platform.Foundation.NSUUID

actual fun generateUuid(): String = NSUUID().UUIDString()</code></pre>
        """,
        "pitfalls": """
        <div class="callout warning">
          <div class="callout-title">Anti-Pattern: Expecting Entire Classes with State</div>
          Avoid <code>expect class LocationTracker</code>. Instead, declare <code>interface LocationTracker</code> in <code>commonMain</code>, implement <code>AndroidLocationTracker</code> in <code>androidMain</code>, <code>IosLocationTracker</code> in <code>iosMain</code>, and bind them with Koin or Kotlin-Inject.
        </div>
        """,
        "code_title": "Declare an Expected Timestamp Provider",
        "code_filename": "PlatformClock.kt",
        "code_instructions": "Declare an expected function <code>expect fun getCurrentTimeMillis(): Long</code> in common code.",
        "starter_code": """// TODO: Declare expect fun getCurrentTimeMillis(): Long""",
        "hints": [
            "Use the expect keyword: expect fun getCurrentTimeMillis(): Long"
        ],
        "final_solution": """expect fun getCurrentTimeMillis(): Long""",
        "solution_explanation": "Expected functions allow platform targets to provide native system clocks (System.currentTimeMillis() on JVM, gettimeofday on iOS) while common code accesses a single unified signature.",
        "execute_fn_js": """function(code, print) {
          const tests = [];
          const hasExpect = /expect\s+fun\s+getCurrentTimeMillis\(\s*\)\s*:\s*Long/.test(code);

          print("Validating expect declaration...");
          if (hasExpect) print("✔ Expected function signature verified.");

          tests.push({
            name: "expect fun getCurrentTimeMillis(): Long declared",
            passed: hasExpect,
            message: "Must declare 'expect fun getCurrentTimeMillis(): Long'."
          });
          return { tests };
        }""",
        "q1": {
            "question": "Why do senior KMP architects recommend using standard interfaces and DI instead of expect/actual for high-level services?",
            "options": [
                "Interfaces allow test fakes to be substituted in unit tests without compile-time platform linkage.",
                "The expect keyword incurs heavy runtime reflection overhead on both JVM and iOS devices.",
                "Apple App Store guidelines explicitly ban apps compiled with expected class declarations.",
                "Interfaces force the Kotlin compiler to compile code directly into assembly language."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "Interfaces enable polymorphism. In unit tests, you can easily provide a FakeLocationService without having to compile against real Android or iOS platform runtimes.",
                "whyWrong": [
                    {"letter": "B", "reason": "expect/actual has zero reflection overhead; it is resolved at compile-time."},
                    {"letter": "C", "reason": "Apple evaluates compiled native binaries and has no knowledge of Kotlin language keywords."},
                    {"letter": "D", "reason": "Compilation targets are determined by the Gradle plugin, not interface usage."}
                ]
            }
        },
        "q2": {
            "question": "Which of the following use cases is most appropriate for the expect/actual mechanism?",
            "options": [
                "Low-level platform primitives such as retrieving the current epoch timestamp or UUID string.",
                "High-level business use cases such as processing a recurring checkout payment order.",
                "Remote network REST clients coordinating OAuth2 refresh tokens and API retries.",
                "Navigation backstack graphs coordinating deep links across complex screens."
            ],
            "correct_index": 0,
            "explanation": {
                "whyCorrect": "expect/actual excels at simple, stateless platform utilities (generating UUIDs, reading epoch timestamps, getting platform names) where full interfaces would add unnecessary boilerplate.",
                "whyWrong": [
                    {"letter": "B", "reason": "Business use cases should be 100% shared in commonMain without platform branching."},
                    {"letter": "C", "reason": "Networking is shared using Ktor in commonMain."},
                    {"letter": "D", "reason": "Navigation should be shared via Jetpack Navigation Multiplatform or Decompose."}
                ]
            }
        },
        "source_title": "JetBrains Kotlin Multiplatform Documentation",
        "source_url": "https://kotlinlang.org/docs/multiplatform-expect-actual.html",
        "source_link_text": "Expected and Actual Declarations in KMP",
        "source_note": "Detailed syntax reference, rules, and best practices for expect/actual.",
        "ref_content": """
        <h2>expect / actual Best Practice Rules</h2>
        <ul>
          <li><strong>Use for:</strong> Platform primitives (UUID, epoch millis, platform dispatchers).</li>
          <li><strong>Avoid for:</strong> Complex business logic, UI state holders, repositories. Use interfaces + DI instead.</li>
        </ul>
        """
    }
]

for l in lessons:
    generate_lesson_file(l)

print(f"Phase 0 and Phase 1 (Lessons 02-05) successfully generated!")
