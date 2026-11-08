# Phase 7: App Store Readiness, Ecosystem & DevOps (Lessons 0044 - 0048)
# Phase 8: Capstone Portfolio Project (Lesson 0049)

PHASE7_AND_8_LESSONS = [
    # 0044: Lesson 7.1
    {
        "id": "0044",
        "slug": "0044-apple-hig-accessibility-and-privacy",
        "phase_num": 7,
        "phase_title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "lesson_num": "Lesson 7.1",
        "title": "Apple HIG, Accessibility & Privacy Manifests",
        "subtitle": "Human Interface Guidelines compliance, Dynamic Type scaling, VoiceOver accessibility traits, and PrivacyInfo.xcprivacy required reason declarations.",
        "prev_url": "0043-metrickit-and-unified-logging.html",
        "next_url": "0045-background-tasks-and-push-notifications.html",
        "cheatsheet_url": "../reference/release-and-ecosystem-cheatsheet.html",
        "cheatsheet_title": "Release & Ecosystem Cheatsheet",
        "android_anchor_html": """
        <p>In Android, accessibility relies on content descriptions (<code>contentDescription</code>) and font scaling with <code>sp</code> units, while permissions are declared in <code>AndroidManifest.xml</code>.</p>
        <p>In iOS, Apple strictly rejects apps that do not support <strong>Dynamic Type</strong> font scaling or lack accessible VoiceOver traits. Furthermore, Apple mandates <strong>Privacy Manifests (<code>PrivacyInfo.xcprivacy</code>)</strong> for all apps and third-party SDK dependencies accessing Required Reason APIs (file timestamps, UserDefaults, system boot time).</p>
        """,
        "core_sections_html": """
        <h3>1. Dynamic Type & VoiceOver Traits in SwiftUI</h3>
        <pre><code>Text("Account Balance")
    .font(.headline) // Automatically scales with Dynamic Type!
    .accessibilityLabel("Account Balance")
    .accessibilityValue("$1,250.00")
    .accessibilityAddTraits(.isHeader)</code></pre>

        <h3>2. Privacy Manifests (PrivacyInfo.xcprivacy)</h3>
        <p>A declarative plist in your app and SPM targets declaring:</p>
        <ul>
          <li><strong>NSPrivacyTracking:</strong> Boolean indicating whether the app tracks across third-party websites.</li>
          <li><strong>NSPrivacyCollectedDataTypes:</strong> Arrays specifying collected data (e.g. Email Address, Device ID) and purposes.</li>
          <li><strong>NSPrivacyAccessedAPITypes:</strong> Required reason API categories and explicit code rationale reasons (e.g. <code>CA92.1</code> for UserDefaults).</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Missing PrivacyInfo in SPM Packages:</strong> If your local or remote SPM package accesses <code>UserDefaults</code> or file modification dates without declaring a signed Privacy Manifest, App Store Connect will reject the binary on upload. Always bundle <code>PrivacyInfo.xcprivacy</code> with required reason codes.</p>
        """,
        "quizzes": [
            {
                "question": "Why does Apple mandate Privacy Manifests (PrivacyInfo.xcprivacy) for App Store submissions?",
                "correct_idx": 1,
                "options": [
                    {"text": "To compress binaries on the App Store CDN.", "reason": "Option A is wrong because privacy manifests govern user privacy declarations, not compression."},
                    {"text": "To document data collection and Required Reason API usage.", "reason": "Option B is correct: Privacy manifests ensure developers and third-party SDKs declare exact reasons for accessing sensitive system APIs."},
                    {"text": "To bypass developer account renewal fees.", "reason": "Option C is wrong because Apple Developer Program fees are unrelated."},
                    {"text": "To force SwiftUI views to use static dispatch.", "reason": "Option D is wrong because manifests are property list XML files."}
                ]
            },
            {
                "question": "What happens if a SwiftUI view hardcodes fixed font sizes (e.g. .font(.system(size: 16))) instead of semantic text styles?",
                "correct_idx": 0,
                "options": [
                    {"text": "Text fails to scale with user Dynamic Type settings.", "reason": "Option A is correct: hardcoded point sizes ignore the user's system accessibility font size preferences, failing accessibility guidelines."},
                    {"text": "The Darwin operating system kills the process.", "reason": "Option B is wrong because fixed fonts do not trigger kernel panics."},
                    {"text": "The app leaks memory on every redraw.", "reason": "Option C is wrong because font sizing does not cause retain cycles."},
                    {"text": "All network requests automatically time out.", "reason": "Option D is wrong because UI font scaling is independent of networking."}
                ]
            }
        ],
        "exercise": {
            "title": "Privacy Manifest Reason Code Validator",
            "instructions": "Implement <code>validateReasonCode</code>. For API category 'NSPrivacyAccessedAPICategoryUserDefaults', valid reason codes are 'CA92.1' or '1C8F.1'. Return true if category and code match, else false. Run tests.",
            "starter_code": """import Foundation

struct PrivacyManifestValidator {
    // TODO: Implement validate(category: String, reasonCode: String) -> Bool
    // For "NSPrivacyAccessedAPICategoryUserDefaults", valid codes are ["CA92.1", "1C8F.1"]
    // Return true if category and reason code are valid
    static func validate(category: String, reasonCode: String) -> Bool {
        fatalError("TODO: Implement validate")
    }
}

func runTests() {
    print("🧪 Running Lesson 7.1 Verification Tests...")
    let cat = "NSPrivacyAccessedAPICategoryUserDefaults"
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "CA92.1") == true, "Test 1 Failed")
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "1C8F.1") == true, "Test 2 Failed")
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "INVALID_CODE") == false, "Test 3 Failed")
    assert(PrivacyManifestValidator.validate(category: "UnknownCategory", reasonCode: "CA92.1") == false, "Test 4 Failed")
    print("  ✅ Challenge Passed: Privacy manifest required reason code validation verified")
}
runTests()""",
            "hints": [
                "if category == \"NSPrivacyAccessedAPICategoryUserDefaults\" { return reasonCode == \"CA92.1\" || reasonCode == \"1C8F.1\" }",
                "return false"
            ],
            "solution_code": """import Foundation

struct PrivacyManifestValidator {
    static func validate(category: String, reasonCode: String) -> Bool {
        if category == "NSPrivacyAccessedAPICategoryUserDefaults" {
            return reasonCode == "CA92.1" || reasonCode == "1C8F.1"
        }
        return false
    }
}

func runTests() {
    print("🧪 Running Lesson 7.1 Verification Tests...")
    let cat = "NSPrivacyAccessedAPICategoryUserDefaults"
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "CA92.1") == true)
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "1C8F.1") == true)
    assert(PrivacyManifestValidator.validate(category: cat, reasonCode: "INVALID_CODE") == false)
    assert(PrivacyManifestValidator.validate(category: "UnknownCategory", reasonCode: "CA92.1") == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Privacy manifests require exact Apple-approved reason strings for sensitive system APIs. Validating reason codes against Apple's official privacy taxonomy prevents App Store Connect rejection."
        },
        "primary_sources": [
            {"title": "Apple Developer: Describing data use in privacy manifests", "url": "https://developer.apple.com/documentation/bundleresources/privacy_manifest_files", "desc": "Official Apple guide to PrivacyInfo.xcprivacy and Required Reason APIs."},
            {"title": "Apple Human Interface Guidelines: Accessibility", "url": "https://developer.apple.com/design/human-interface-guidelines/accessibility", "desc": "Official design guidelines for Dynamic Type and inclusive UX."}
        ],
        "coaching_prompt": "Always test your app in the iOS Simulator with Accessibility -> Large Text enabled to verify that fonts scale without clipping."
    },

    # 0045: Lesson 7.2
    {
        "id": "0045",
        "slug": "0045-background-tasks-and-push-notifications",
        "phase_num": 7,
        "phase_title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "lesson_num": "Lesson 7.2",
        "title": "Background Processing & Apple Push Notifications (APNs)",
        "subtitle": "BGTaskScheduler (App Refresh & Processing), silent push notifications (content-available: 1), and device token registration.",
        "prev_url": "0044-apple-hig-accessibility-and-privacy.html",
        "next_url": "0046-app-extensions-and-widgetkit.html",
        "cheatsheet_url": "../reference/release-and-ecosystem-cheatsheet.html",
        "cheatsheet_title": "Release & Ecosystem Cheatsheet",
        "android_anchor_html": """
        <p>In Android, background work uses <code>WorkManager</code> (OneTimeWorkRequest, PeriodicWorkRequest) and Firebase Cloud Messaging (FCM).</p>
        <p>In iOS, background execution is strictly sandboxed and managed by Darwin's <strong><code>BGTaskScheduler</code></strong> (<code>BGAppRefreshTask</code> for quick data refreshes, <code>BGProcessingTask</code> for heavy maintenance while charging). Remote triggers rely on <strong>Apple Push Notification service (APNs)</strong> with silent notifications (<code>content-available: 1</code>) to wake the app for background data sync.</p>
        """,
        "core_sections_html": """
        <h3>1. BGTaskScheduler Architecture</h3>
        <pre><code>import BackgroundTasks

func registerBackgroundTasks() {
    BGTaskScheduler.shared.register(
        forTaskWithIdentifier: "com.pulseflow.app.refresh",
        using: nil
    ) { task in
        handleAppRefresh(task: task as! BGAppRefreshTask)
    }
}

func scheduleNextRefresh() {
    let request = BGAppRefreshTaskRequest(identifier: "com.pulseflow.app.refresh")
    request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 mins
    try? BGTaskScheduler.shared.submit(request)
}</code></pre>

        <h3>2. Handling Expiration in Background Tasks</h3>
        <p>Darwin grants a maximum of ~30 seconds for app refresh tasks. You must register an <code>expirationHandler</code> to cancel in-flight work and call <code>setTaskCompleted(success: false)</code> gracefully before the OS terminates your app.</p>
        """,
        "failure_mode_html": """
        <p><strong>Forgetting to call setTaskCompleted():</strong> Failing to call <code>task.setTaskCompleted(success: true/false)</code> causes Darwin to penalize your app's background budget. Subsequent scheduled background requests will be deferred or withheld by the system.</p>
        """,
        "quizzes": [
            {
                "question": "What happens if a BGAppRefreshTask does not call setTaskCompleted() before its time expires?",
                "correct_idx": 1,
                "options": [
                    {"text": "The app is granted unlimited extra execution time.", "reason": "Option A is wrong because Darwin enforces strict time bounds."},
                    {"text": "The OS terminates app and reduces future background budget.", "reason": "Option B is correct: Darwin kills the task and deprioritizes future background wakeups due to budget exhaustion."},
                    {"text": "The device restarts into diagnostic mode.", "reason": "Option C is wrong because task termination is an application-level event."},
                    {"text": "All local SQLite database files are erased.", "reason": "Option D is wrong because data files remain safely on disk."}
                ]
            },
            {
                "question": "Which payload flag tells APNs to wake an iOS app in the background without showing an alert banner?",
                "correct_idx": 0,
                "options": [
                    {"text": "\"content-available\": 1", "reason": "Option A is correct: content-available: 1 in the aps dictionary triggers a silent background notification wakeup."},
                    {"text": "\"badge\": 0", "reason": "Option B is wrong because badge manages app icon badge counts."},
                    {"text": "\"sound\": \"default\"", "reason": "Option C is wrong because sound plays an audio notification."},
                    {"text": "\"priority\": \"urgent\"", "reason": "Option D is wrong because apns-priority is an HTTP/2 header, not a payload key."}
                ]
            }
        ],
        "exercise": {
            "title": "Background Task Expiration Handler Simulator",
            "instructions": "Implement <code>runBackgroundTask</code>. If <code>isCancelled</code> is triggered before work completes, set <code>wasCancelled = true</code> and exit early with false. If work finishes, return true. Run tests.",
            "starter_code": """import Foundation

final class SimulatedBackgroundTask {
    private(set) var wasCancelled: Bool = false
    var shouldCancelEarly: Bool = false

    func executeWork() -> Bool {
        // TODO: If shouldCancelEarly is true, set wasCancelled = true and return false
        // Otherwise return true
        fatalError("TODO: Implement executeWork")
    }
}

func runTests() {
    print("🧪 Running Lesson 7.2 Verification Tests...")
    let successTask = SimulatedBackgroundTask()
    assert(successTask.executeWork() == true, "Test 1 Failed")
    assert(successTask.wasCancelled == false, "Test 2 Failed")

    let cancelledTask = SimulatedBackgroundTask()
    cancelledTask.shouldCancelEarly = true
    assert(cancelledTask.executeWork() == false, "Test 3 Failed")
    assert(cancelledTask.wasCancelled == true, "Test 4 Failed: wasCancelled should be true")
    print("  ✅ Challenge Passed: Background task expiration handling verified")
}
runTests()""",
            "hints": [
                "if shouldCancelEarly { wasCancelled = true; return false } else { return true }"
            ],
            "solution_code": """import Foundation

final class SimulatedBackgroundTask {
    private(set) var wasCancelled: Bool = false
    var shouldCancelEarly: Bool = false

    func executeWork() -> Bool {
        if shouldCancelEarly {
            wasCancelled = true
            return false
        }
        return true
    }
}

func runTests() {
    print("🧪 Running Lesson 7.2 Verification Tests...")
    let successTask = SimulatedBackgroundTask()
    assert(successTask.executeWork() == true)
    assert(successTask.wasCancelled == false)

    let cancelledTask = SimulatedBackgroundTask()
    cancelledTask.shouldCancelEarly = true
    assert(cancelledTask.executeWork() == false)
    assert(cancelledTask.wasCancelled == true)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "BGTaskScheduler requires registering an expirationHandler. When Darwin signals expiration, cancelling cooperative tasks and marking completion ensures the app preserves its system background execution budget."
        },
        "primary_sources": [
            {"title": "Apple Developer: BGTaskScheduler", "url": "https://developer.apple.com/documentation/backgroundtasks/bgtaskscheduler", "desc": "Official Apple reference on scheduling background app refreshes."},
            {"title": "Apple Developer: Pushing Background Updates to Your App", "url": "https://developer.apple.com/documentation/usernotifications/pushing-background-updates-to-your-app", "desc": "Configuring silent APNs push notifications."}
        ],
        "coaching_prompt": "Always schedule the next background task from inside the completion handler of the current background task to keep the periodic refresh loop alive!"
    },

    # 0046: Lesson 7.3
    {
        "id": "0046",
        "slug": "0046-app-extensions-and-widgetkit",
        "phase_num": 7,
        "phase_title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "lesson_num": "Lesson 7.3",
        "title": "App Extensions, WidgetKit, Live Activities & App Groups",
        "subtitle": "WidgetKit TimelineProvider, Dynamic Island Live Activities, sharing data via App Group containers, and the strict 30MB extension memory limit.",
        "prev_url": "0045-background-tasks-and-push-notifications.html",
        "next_url": "0047-provisioning-certificates-and-code-signing.html",
        "cheatsheet_url": "../reference/release-and-ecosystem-cheatsheet.html",
        "cheatsheet_title": "Release & Ecosystem Cheatsheet",
        "android_anchor_html": """
        <p>In Android, home screen widgets are built with <code>AppWidgetProvider</code> and Jetpack Glance.</p>
        <p>In iOS, <strong>WidgetKit</strong> and <strong>Live Activities</strong> (ActivityKit for the Dynamic Island) run in an <strong>independent extension process</strong> separate from the main app. They render via pre-computed <strong>TimelineEntry</strong> snapshots. Data sharing between the main app and the widget requires an <strong>App Group</strong> entitlement (<code>group.com.company.app</code>) accessing shared UserDefaults or shared SwiftData SQLite containers.</p>
        """,
        "core_sections_html": """
        <h3>1. The WidgetKit Timeline Architecture</h3>
        <pre><code>struct MetricTimelineProvider: TimelineProvider {
    func getTimeline(in context: Context, completion: @escaping (Timeline&lt;MetricEntry&gt;) -> Void) {
        let currentDate = Date()
        // Generate timeline entries for the next 4 hours:
        var entries: [MetricEntry] = []
        for hourOffset in 0..<4 {
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            entries.append(MetricEntry(date: entryDate, metricValue: 85.0))
        }
        let timeline = Timeline(entries: entries, policy: .after(entries.last!.date))
        completion(timeline)
    }
}</code></pre>

        <h3>2. The Strict 30MB Memory Limit (Darwin Extension Jetsam)</h3>
        <p>Widget extensions are granted a strict memory limit of <strong>~30MB of dirty memory</strong>. Exceeding 30MB triggers an immediate, silent Darwin Jetsam crash, rendering a blank or black widget. Avoid loading large bitmap images in widgets; use downscaled thumbnails.</p>
        """,
        "failure_mode_html": """
        <p><strong>Attempting to Read Main App Sandbox Files:</strong> App extensions run in an isolated sandbox. Calling <code>UserDefaults.standard</code> in a widget reads the widget's private preferences, not the main app's data! Always initialize shared storage via <code>UserDefaults(suiteName: "group.com.pulseflow.app")</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What is the approximate dirty memory limit enforced on WidgetKit extensions before Darwin Jetsam terminates them?",
                "correct_idx": 0,
                "options": [
                    {"text": "Approximately 30 megabytes of memory.", "reason": "Option A is correct: iOS enforces a strict ~30MB memory ceiling on widget extension processes to preserve system responsiveness."},
                    {"text": "Approximately 500 megabytes of memory.", "reason": "Option B is wrong because extensions have much smaller limits than main apps."},
                    {"text": "Unbounded based on available device storage.", "reason": "Option C is wrong because RAM is physical and strictly throttled by the OS."},
                    {"text": "Exactly one gigabyte of memory.", "reason": "Option D is wrong because a 1GB allocation would trigger immediate Jetsam kill."}
                ]
            },
            {
                "question": "How must the main app and a widget extension share a local SQLite or UserDefaults container?",
                "correct_idx": 2,
                "options": [
                    {"text": "Through global static singleton classes.", "reason": "Option A is wrong because static variables are process-isolated and not shared across processes."},
                    {"text": "By sharing raw Darwin process memory pointers.", "reason": "Option B is wrong because iOS processes have independent virtual memory address spaces."},
                    {"text": "Via an App Group shared container directory.", "reason": "Option C is correct: App Groups grant shared access to a common filesystem folder and shared UserDefaults suite."},
                    {"text": "By uploading all data to a public web server.", "reason": "Option D is wrong because local sharing is faster, offline, and secure."}
                ]
            }
        ],
        "exercise": {
            "title": "WidgetKit Timeline Policy Generator",
            "instructions": "Implement <code>generateTimelineIntervals</code>. Given a start hour and entry count, return an array of hour timestamps. Run tests.",
            "starter_code": """import Foundation

func generateTimelineIntervals(startHour: Int, count: Int) -> [Int] {
    // TODO: Return an array of Int representing consecutive hours from startHour to (startHour + count - 1)
    // If count <= 0 return []
    fatalError("TODO: Implement generateTimelineIntervals")
}

func runTests() {
    print("🧪 Running Lesson 7.3 Verification Tests...")
    let timeline = generateTimelineIntervals(startHour: 10, count: 4)
    assert(timeline == [10, 11, 12, 13], "Test 1 Failed: Got \\(timeline)")

    let empty = generateTimelineIntervals(startHour: 5, count: 0)
    assert(empty.isEmpty, "Test 2 Failed")
    print("  ✅ Challenge Passed: Timeline interval generation verified")
}
runTests()""",
            "hints": [
                "guard count > 0 else { return [] }",
                "return Array(startHour..<(startHour + count))"
            ],
            "solution_code": """import Foundation

func generateTimelineIntervals(startHour: Int, count: Int) -> [Int] {
    guard count > 0 else { return [] }
    return Array(startHour..<(startHour + count))
}

func runTests() {
    print("🧪 Running Lesson 7.3 Verification Tests...")
    let timeline = generateTimelineIntervals(startHour: 10, count: 4)
    assert(timeline == [10, 11, 12, 13])
    let empty = generateTimelineIntervals(startHour: 5, count: 0)
    assert(empty.isEmpty)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "WidgetKit TimelineProvider pre-computes timeline entries to avoid waking the extension every minute. Pre-computing schedules allows the OS to render fresh widget states at low battery cost."
        },
        "primary_sources": [
            {"title": "Apple Developer: WidgetKit Documentation", "url": "https://developer.apple.com/documentation/widgetkit", "desc": "Official Apple guide to building widgets, timelines, and complications."},
            {"title": "Apple Developer: ActivityKit (Live Activities)", "url": "https://developer.apple.com/documentation/activitykit", "desc": "Dynamic Island and Lock Screen live activity updates."}
        ],
        "coaching_prompt": "Always profile your widget extension in Instruments Allocations before shipping to ensure memory never spikes near the 30MB Jetsam cliff!"
    },

    # 0047: Lesson 7.4
    {
        "id": "0047",
        "slug": "0047-provisioning-certificates-and-code-signing",
        "phase_num": 7,
        "phase_title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "lesson_num": "Lesson 7.4",
        "title": "Provisioning, Certificates, Code Signing & App Attest",
        "subtitle": "Apple Developer Program, App IDs, Provisioning Profiles, Entitlements, codesign CLI, and Apple App Attest security.",
        "prev_url": "0046-app-extensions-and-widgetkit.html",
        "next_url": "0048-cicd-pipelines-and-release-automation.html",
        "cheatsheet_url": "../reference/release-and-ecosystem-cheatsheet.html",
        "cheatsheet_title": "Release & Ecosystem Cheatsheet",
        "android_anchor_html": """
        <p>In Android, APK/AAB signing uses Java keystores (<code>.jks</code>) and Play App Signing.</p>
        <p>In iOS, code signing is cryptographic and multi-layered: <strong>Certificates</strong> (Developer/Distribution X.509 certs issued by Apple CA) + <strong>App IDs</strong> (explicit Bundle Identifiers) + <strong>Entitlements</strong> (Push Notifications, App Groups, Keychain Sharing) are bound together into a signed <strong>Provisioning Profile (<code>.mobileprovision</code>)</strong>. Darwin kernel verifies the CMS signature before launching binaries.</p>
        """,
        "core_sections_html": """
        <h3>1. The Code Signing Equation</h3>
        <p><code>App Binary + Entitlements + Distribution Certificate = Cryptographically Signed Mach-O Executable</code></p>
        <ul>
          <li><strong>Development Profile:</strong> Tied to specific registered device UDIDs.</li>
          <li><strong>App Store Profile:</strong> Untied from UDIDs; verified by App Store Connect upon ingestion.</li>
          <li><strong>Ad-Hoc / Enterprise:</strong> Distribution without App Store.</li>
        </ul>

        <h3>2. DeviceCheck & DCAppAttestService</h3>
        <p>To prevent modified or jailbroken clients from abusing backend APIs, Apple provides <strong>App Attest</strong>. It generates a hardware key in the Secure Enclave and returns an Apple-signed cryptographic attestation validating that the request originates from your legitimate App Store binary.</p>
        """,
        "failure_mode_html": """
        <p><strong>Mismatched Entitlements in Provisioning Profiles:</strong> If your project enables Push Notifications in <code>Entitlements.plist</code>, but the App ID in the Apple Developer portal does not have Push enabled, Xcode will throw code signing errors (<code>Provisioning profile doesn't match entitlements</code>). Portal capabilities must match Entitlements.plist 1-to-1.</p>
        """,
        "quizzes": [
            {
                "question": "What components are bound together inside an iOS Provisioning Profile (.mobileprovision)?",
                "correct_idx": 1,
                "options": [
                    {"text": "Raw Swift source code files and images.", "reason": "Option A is wrong because provisioning profiles do not store source code."},
                    {"text": "Certificates, App ID, Entitlements, and device UDIDs.", "reason": "Option B is correct: a provisioning profile cryptographically binds developer/distribution certificates with bundle identifiers and allowed entitlements."},
                    {"text": "Compiled Metal shaders and vertex data.", "reason": "Option C is wrong because shaders are compiled into metallib files."},
                    {"text": "SQLite database migration SQL scripts.", "reason": "Option D is wrong because database schemas are app data."}
                ]
            },
            {
                "question": "What is the primary role of Apple's DCAppAttestService (App Attest)?",
                "correct_idx": 0,
                "options": [
                    {"text": "Proves API requests originate from legitimate un-tampered binaries.", "reason": "Option A is correct: App Attest uses the Secure Enclave to attest to backend servers that calls originate from an authentic, un-modified app instance."},
                    {"text": "Translates Objective-C code into Swift.", "reason": "Option B is wrong because App Attest is an anti-fraud security service."},
                    {"text": "Automates TestFlight beta invite distribution.", "reason": "Option C is wrong because TestFlight manages beta testing."},
                    {"text": "Compresses PNG images in asset catalogs.", "reason": "Option D is wrong because asset catalogs handle image assets."}
                ]
            }
        ],
        "exercise": {
            "title": "Bundle ID & Team ID Formatter",
            "instructions": "Implement <code>generateAppID</code>. An App ID consists of <code>TEAM_ID.BUNDLE_ID</code> (e.g. 'A1B2C3D4E5.com.pulseflow.app'). Format and validate that both parts are non-empty. Run tests.",
            "starter_code": """import Foundation

func generateAppID(teamId: String, bundleId: String) -> String? {
    // TODO: If teamId.isEmpty || bundleId.isEmpty return nil
    // Return "\\(teamId).\\(bundleId)"
    fatalError("TODO: Implement generateAppID")
}

func runTests() {
    print("🧪 Running Lesson 7.4 Verification Tests...")
    let appId = generateAppID(teamId: "A1B2C3D4E5", bundleId: "com.pulseflow.app")
    assert(appId == "A1B2C3D4E5.com.pulseflow.app", "Test 1 Failed: Got \\(appId ?? \"nil\")")

    assert(generateAppID(teamId: "", bundleId: "com.test") == nil, "Test 2 Failed")
    assert(generateAppID(teamId: "TEAM", bundleId: "") == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: App ID formatting verified")
}
runTests()""",
            "hints": [
                "guard !teamId.isEmpty, !bundleId.isEmpty else { return nil }",
                "return \"\\\\(teamId).\\\\(bundleId)\""
            ],
            "solution_code": r"""import Foundation

func generateAppID(teamId: String, bundleId: String) -> String? {
    guard !teamId.isEmpty, !bundleId.isEmpty else { return nil }
    return "\(teamId).\(bundleId)"
}

func runTests() {
    print("🧪 Running Lesson 7.4 Verification Tests...")
    let appId = generateAppID(teamId: "A1B2C3D4E5", bundleId: "com.pulseflow.app")
    assert(appId == "A1B2C3D4E5.com.pulseflow.app")
    assert(generateAppID(teamId: "", bundleId: "com.test") == nil)
    assert(generateAppID(teamId: "TEAM", bundleId: "") == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "In Apple's ecosystem, an App ID prefix is the developer's 10-character Team ID, concatenated with the bundle identifier. This unique prefix establishes Keychain access sharing and App Group boundaries."
        },
        "primary_sources": [
            {"title": "Apple Developer: What is code signing?", "url": "https://developer.apple.com/support/code-signing/", "desc": "Official Apple overview of certificates, identifiers, and profiles."},
            {"title": "Apple Developer: DeviceCheck and App Attest", "url": "https://developer.apple.com/documentation/devicecheck", "desc": "Official guide on validating client integrity with DCAppAttestService."}
        ],
        "coaching_prompt": "When troubleshooting code signing, remember: App IDs live in the Developer Portal, Entitlements live in your Xcode project, and the Provisioning Profile connects them!"
    },

    # 0048: Lesson 7.5
    {
        "id": "0048",
        "slug": "0048-cicd-pipelines-and-release-automation",
        "phase_num": 7,
        "phase_title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "lesson_num": "Lesson 7.5",
        "title": "Enterprise CI/CD Pipelines & TestFlight Automation",
        "subtitle": "Automated build and test workflows with Fastlane, GitHub Actions, match certificate synchronization, and TestFlight deployment trains.",
        "prev_url": "0047-provisioning-certificates-and-code-signing.html",
        "next_url": "0049-capstone-portfolio-blueprint.html",
        "cheatsheet_url": "../reference/release-and-ecosystem-cheatsheet.html",
        "cheatsheet_title": "Release & Ecosystem Cheatsheet",
        "android_anchor_html": """
        <p>In Android, release automation uses Fastlane (<code>supply</code>) or Gradle publishing tasks with Google Play Developer API.</p>
        <p>In iOS, commercial engineering teams automate release trains using <strong>Fastlane</strong> (<code>gym</code> for compilation, <code>scan</code> for testing, <code>pilot</code> for TestFlight) coupled with <strong><code>fastlane match</code></strong> to synchronize encrypted certificates and provisioning profiles across team members using a private git repo.</p>
        """,
        "core_sections_html": """
        <h3>1. The Fastlane Release Pipeline (Fastfile)</h3>
        <pre><code>default_platform(:ios)

platform :ios do
  desc "Run unit tests and build TestFlight binary"
  lane :beta do
    # 1. Sync certificates securely:
    match(type: "appstore", readonly: true)

    # 2. Run automated tests:
    scan(scheme: "PulseFlow", clean: true)

    # 3. Build signed archive:
    gym(scheme: "PulseFlow", export_method: "app-store")

    # 4. Upload to TestFlight:
    pilot(skip_waiting_for_build_processing: true)
  end
end</code></pre>

        <h3>2. GitHub Actions macOS Runner Workflow</h3>
        <p>Automate pull request validation on macOS runners (<code>runs-on: macos-14</code>) using <code>xcodebuild test -scheme PulseFlow -destination 'platform=iOS Simulator,name=iPhone 15'</code>.</p>
        """,
        "failure_mode_html": """
        <p><strong>Storing Plain-Text Signing Keys in CI Environment:</strong> Storing p12 private keys directly in CI repository secrets without encryption risks credential leaks. Use <code>fastlane match</code> with git-crypt or App Store Connect API keys with restricted permissions.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary operational purpose of 'fastlane match' in team iOS development?",
                "correct_idx": 1,
                "options": [
                    {"text": "Translates Swift code into Android Kotlin.", "reason": "Option A is wrong because match is release automation."},
                    {"text": "Synchronizes encrypted certificates and profiles via Git.", "reason": "Option B is correct: match stores certificates encrypted in a private repo, allowing one shared code-signing identity across developers and CI."},
                    {"text": "Generates AI unit tests for SwiftUI views.", "reason": "Option C is wrong because match handles code signing."},
                    {"text": "Bypasses all Apple App Store review checks.", "reason": "Option D is wrong because all binaries undergo review."}
                ]
            },
            {
                "question": "Which Fastlane tool compiles and signs the iOS Mach-O application archive (.ipa)?",
                "correct_idx": 0,
                "options": [
                    {"text": "gym (build_app)", "reason": "Option A is correct: gym compiles the Xcode scheme and packages the signed .ipa distribution archive."},
                    {"text": "scan (run_tests)", "reason": "Option B is wrong because scan executes unit and UI test suites."},
                    {"text": "pilot (upload_to_testflight)", "reason": "Option C is wrong because pilot uploads the compiled archive to TestFlight."},
                    {"text": "snapshot", "reason": "Option D is wrong because snapshot captures App Store screenshots."}
                ]
            }
        ],
        "exercise": {
            "title": "CI/CD Build Version Bumper Simulator",
            "instructions": "Implement <code>bumpBuildNumber</code>. Given a build string like '105', increment it by 1 and return '106'. If the input string is invalid or negative, return '1'. Run tests.",
            "starter_code": """import Foundation

func bumpBuildNumber(_ current: String) -> String {
    // TODO: Convert current to Int. If valid and >= 0, return String(val + 1)
    // Otherwise return "1"
    fatalError("TODO: Implement bumpBuildNumber")
}

func runTests() {
    print("🧪 Running Lesson 7.5 Verification Tests...")
    assert(bumpBuildNumber("105") == "106", "Test 1 Failed")
    assert(bumpBuildNumber("0") == "1", "Test 2 Failed")
    assert(bumpBuildNumber("invalid") == "1", "Test 3 Failed")
    print("  ✅ Challenge Passed: CI build version increment verified")
}
runTests()""",
            "hints": [
                "guard let val = Int(current), val >= 0 else { return \"1\" }",
                "return String(val + 1)"
            ],
            "solution_code": """import Foundation

func bumpBuildNumber(_ current: String) -> String {
    guard let val = Int(current), val >= 0 else {
        return "1"
    }
    return String(val + 1)
}

func runTests() {
    print("🧪 Running Lesson 7.5 Verification Tests...")
    assert(bumpBuildNumber("105") == "106")
    assert(bumpBuildNumber("0") == "1")
    assert(bumpBuildNumber("invalid") == "1")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Automating build number increments in CI workflows ensures every TestFlight upload contains a unique, ascending CFBundleVersion required by App Store Connect."
        },
        "primary_sources": [
            {"title": "Fastlane Documentation: iOS Deployment", "url": "https://docs.fastlane.tools", "desc": "Official guide on match, gym, scan, and pilot automation."},
            {"title": "Apple Developer: TestFlight Overview", "url": "https://developer.apple.com/testflight/", "desc": "Managing internal and external beta testing groups."}
        ],
        "coaching_prompt": "Congratulations! You have completed Phase 7: App Store Readiness, Ecosystem & DevOps! You are now fully prepared for the final phase: Milestone 8.1 PulseFlow Enterprise Capstone Architecture!"
    },

    # 0049: Lesson 8.1
    {
        "id": "0049",
        "slug": "0049-capstone-portfolio-blueprint",
        "phase_num": 8,
        "phase_title": "Phase 8: Capstone Portfolio Project",
        "lesson_num": "Milestone 8.1",
        "title": "Capstone Showcase: PulseFlow Enterprise Architecture",
        "subtitle": "The definitive, interview-winning portfolio project: multi-package SPM, SwiftData, Swift 6 strict concurrency, widgets, and MetricKit telemetry.",
        "prev_url": "0048-cicd-pipelines-and-release-automation.html",
        "next_url": "../index.html",
        "cheatsheet_url": "../reference/architecture-and-modularization-cheatsheet.html",
        "cheatsheet_title": "Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, senior portfolio showcases demonstrate modular architecture (Compose + Hilt + Room + Coroutines + WorkManager + Gradle convention plugins).</p>
        <p>In iOS, <strong>PulseFlow Enterprise</strong> is architected specifically to secure Senior iOS engineering offers. It demonstrates low-level compiler mechanics, multi-package SPM modularization with static linkage, complete Swift 6 data-race immunity, offline-first sync with vector clocks, WidgetKit extensions, and production MetricKit telemetry.</p>
        """,
        "core_sections_html": """
        <h3>1. The 6-Module SPM Architecture</h3>
        <pre><code>PulseFlowEnterprise (Xcode Project / App Runner Target)
    ├── FeatureAuth (SwiftUI, ViewModels, UDF Reducer)
    ├── FeatureDashboard (Swift Charts, Interactive Micro-Interactions)
    ├── CoreNetwork (URLSession, Actor OAuth Token Refresh, Lossy Codable)
    ├── CoreDatabase (SwiftData, VersionedSchema, ModelActor)
    ├── DesignSystem (Typography tokens, ViewModifiers, ButtonStyles)
    └── Telemetry (MetricKit subscriber, os.Logger subsystems)</code></pre>

        <h3>2. Senior Technical Deliverables Checklist</h3>
        <ul>
          <li><strong>Swift 6 Strict Concurrency:</strong> Compiled under <code>-strict-concurrency=complete</code> with 0 warnings.</li>
          <li><strong>Sub-400ms Pre-Main Launch Time:</strong> Static linkage (<code>type: .static</code>) eliminating dyld dylib rebasing overhead.</li>
          <li><strong>Resilient Offline-First Sync:</strong> SwiftData local caching with Last-Write-Wins conflict resolution.</li>
          <li><strong>Production Observability:</strong> MetricKit reporting daily hang rates, CPU consumption, and dirty memory limits.</li>
          <li><strong>Swift Testing Suite:</strong> >85% test coverage using <code>@Test</code>, <code>#expect</code>, and parameterization.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Shipping Generic Todo/Weather Apps in Senior Portfolios:</strong> Lead engineers reject simple tutorial apps because they reveal zero understanding of enterprise scale. PulseFlow Enterprise exhibits the exact systems maturity evaluated in Staff/Senior technical interviews.</p>
        """,
        "quizzes": [
            {
                "question": "Why does PulseFlow Enterprise link its local SPM packages as static libraries rather than dynamic frameworks?",
                "correct_idx": 0,
                "options": [
                    {"text": "Minimizes dyld4 pre-main launch time < 400ms.", "reason": "Option A is correct: static libraries link directly into the application binary, eliminating dyld dynamic framework rebasing and binding overhead."},
                    {"text": "Allows bypassing all App Store review rules.", "reason": "Option B is wrong because review applies to all apps."},
                    {"text": "Converts all SwiftUI views into UIKit controllers.", "reason": "Option C is wrong because views remain declarative SwiftUI."},
                    {"text": "Disables device battery consumption.", "reason": "Option D is wrong because library linkage does not alter battery circuits."}
                ]
            },
            {
                "question": "What concurrency setting proves that an iOS codebase has zero data races?",
                "correct_idx": 1,
                "options": [
                    {"text": "-Onone debug optimization flag.", "reason": "Option A is wrong because -Onone governs compiler optimization level."},
                    {"text": "-strict-concurrency=complete under Swift 6.", "reason": "Option B is correct: complete strict concurrency guarantees compile-time data race immunity across all actor and Task boundaries."},
                    {"text": "ENABLE_NSASSERTIONS=YES in build settings.", "reason": "Option C is wrong because NSAssertions are runtime assertion checks."},
                    {"text": "DYLD_PRINT_STATISTICS=1 in scheme arguments.", "reason": "Option D is wrong because that profiles launch time."}
                ]
            }
        ],
        "exercise": {
            "title": "PulseFlow Enterprise Core Architectural Contracts",
            "instructions": "Implement <code>PulseFlowEngine.evaluateSystemReadiness</code>. Return true only if <code>isNetworkReady</code>, <code>isDatabaseReady</code>, and <code>isTelemetryActive</code> are all true. Run tests.",
            "starter_code": """import Foundation

struct PulseFlowEngine {
    // TODO: Implement evaluateSystemReadiness(isNetworkReady: Bool, isDatabaseReady: Bool, isTelemetryActive: Bool) -> Bool
    // Return true if and only if all three components are ready
    static func evaluateSystemReadiness(isNetworkReady: Bool, isDatabaseReady: Bool, isTelemetryActive: Bool) -> Bool {
        fatalError("TODO: Implement evaluateSystemReadiness")
    }
}

func runTests() {
    print("🧪 Running Milestone 8.1 Verification Tests...")
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: true, isDatabaseReady: true, isTelemetryActive: true) == true, "Test 1 Failed")
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: false, isDatabaseReady: true, isTelemetryActive: true) == false, "Test 2 Failed")
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: true, isDatabaseReady: false, isTelemetryActive: true) == false, "Test 3 Failed")
    print("  ✅ Challenge Passed: PulseFlow Enterprise architectural contracts verified")
}
runTests()""",
            "hints": [
                "return isNetworkReady && isDatabaseReady && isTelemetryActive"
            ],
            "solution_code": """import Foundation

struct PulseFlowEngine {
    static func evaluateSystemReadiness(isNetworkReady: Bool, isDatabaseReady: Bool, isTelemetryActive: Bool) -> Bool {
        return isNetworkReady && isDatabaseReady && isTelemetryActive
    }
}

func runTests() {
    print("🧪 Running Milestone 8.1 Verification Tests...")
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: true, isDatabaseReady: true, isTelemetryActive: true) == true)
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: false, isDatabaseReady: true, isTelemetryActive: true) == false)
    assert(PulseFlowEngine.evaluateSystemReadiness(isNetworkReady: true, isDatabaseReady: false, isTelemetryActive: true) == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Coordinating component readiness across isolated SPM modules (CoreNetwork, CoreDatabase, Telemetry) provides a clean, decoupled foundation for the flagship PulseFlow Enterprise capstone application."
        },
        "primary_sources": [
            {"title": "PulseFlow Enterprise Architecture Specification", "url": "../index.html#capstone-card", "desc": "Complete senior portfolio project technical specification and milestone legend."},
            {"title": "The Swift Programming Language", "url": "https://docs.swift.org/swift-book/", "desc": "Canonical language reference for modern Swift 6 platform engineering."}
        ],
        "coaching_prompt": "Congratulations! You have completed the entire 49-lesson Senior iOS Engineering syllabus! You are now equipped with deep systems mastery of the Swift compiler, Swift 6 concurrency, SwiftUI performance, offline architectures, Instruments profiling, and commercial release engineering!"
    }
]

print("Phase 7 and 8 data loaded (Lessons 7.1 - 7.5 and Milestone 8.1).")
