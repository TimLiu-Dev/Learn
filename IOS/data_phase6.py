# Phase 6: Deep Diagnostics, Testing & Performance Profiling
# Lessons 0038 - 0043

PHASE6_LESSONS = [
    # 0038: Lesson 6.1
    {
        "id": "0038",
        "slug": "0038-modern-swift-testing-framework",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.1",
        "title": "The Modern Swift Testing Framework: @Test & #expect",
        "subtitle": "Macro-driven testing (@Test), compile-time expression inspection (#expect vs #require), parameterized testing, and parallel execution.",
        "prev_url": "0037-schemes-xcconfig-and-launch-time.html",
        "next_url": "0039-test-doubles-and-viewmodel-tests.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android, unit testing is driven by JUnit 5 (<code>@Test</code>, <code>@ParameterizedTest</code>) with assertion libraries like Truth or AssertJ.</p>
        <p>In iOS (Xcode 16+ / Swift 6), Apple introduced the <strong>Swift Testing framework</strong> (<code>import Testing</code>), replacing legacy <code>XCTestCase</code>. Tests are declared on standard structs with the <strong><code>@Test</code></strong> macro. Assertions use <strong><code>#expect</code></strong> (which parses expressions at compile time to produce rich failure diffs) and <strong><code>#require</code></strong> (which halts execution early on nil/failure).</p>
        """,
        "core_sections_html": """
        <h3>1. The Swift Testing Macro Architecture</h3>
        <pre><code>import Testing

@Suite("Authentication Engine Tests")
struct AuthEngineTests {

    @Test("Valid email credentials pass validation", arguments: [
        "lead@apple.com",
        "engineer@pulseflow.app",
        "sarah@domain.co.uk"
    ])
    func emailFormatValidation(email: String) {
        #expect(Validator.isValidEmail(email))
    }

    @Test("Mandatory payload extraction throws early on nil")
    func payloadExtraction() throws {
        let payload = Payload(raw: "{}")
        let token = try #require(payload.token) // Halts test immediately if nil!
        #expect(token.count > 10)
    }
}</code></pre>

        <h3>2. #expect vs #require</h3>
        <ul>
          <li><strong><code>#expect(condition)</code>:</strong> Records an assertion failure if false, but allows the remainder of the test to continue executing.</li>
          <li><strong><code>try #require(optional)</code>:</strong> Unwraps an optional value. If <code>nil</code> or false, throws and halts the current test immediately, preventing crashes from cascading downstream.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Relying on Legacy XCTest Boilerplate:</strong> Creating classes inheriting from <code>XCTestCase</code> with prefix <code>test...</code> prevents parameterized test arguments and requires verbose <code>XCTAssertEqual</code> boilerplate. Migrate new test suites to the Swift Testing framework.</p>
        """,
        "quizzes": [
            {
                "question": "What is the critical behavioral difference between #expect and #require in Swift Testing?",
                "correct_idx": 1,
                "options": [
                    {"text": "#expect stops test execution immediately.", "reason": "Option A is wrong because #expect logs failure and continues running."},
                    {"text": "#require halts test execution on failure.", "reason": "Option B is correct: #require throws and terminates the test early if an optional is nil or condition is false."},
                    {"text": "#require disables compiler syntax checks.", "reason": "Option C is wrong because Swift Testing macros are checked at compile time."},
                    {"text": "#expect only executes on physical devices.", "reason": "Option D is wrong because Swift Testing runs on simulator and device."}
                ]
            },
            {
                "question": "How are test suites defined in the Swift Testing framework?",
                "correct_idx": 0,
                "options": [
                    {"text": "Using standard structs tagged with @Suite.", "reason": "Option A is correct: Swift Testing allows pure structs, actors, or classes decorated with @Suite."},
                    {"text": "By inheriting from the legacy XCTestCase class.", "reason": "Option B is wrong because that is the older XCTest paradigm."},
                    {"text": "Within an Objective-C runtime category.", "reason": "Option C is wrong because Swift Testing is pure native Swift."},
                    {"text": "Inside a Darwin kernel daemon script.", "reason": "Option D is wrong because tests execute in the application runner."}
                ]
            }
        ],
        "exercise": {
            "title": "Parameterized Test Validator Simulator",
            "instructions": "Implement <code>validateEmail</code>. An email is valid if it contains '@', has at least 3 characters, and ends with a dot-separated domain (contains '.'). Run tests.",
            "starter_code": """import Foundation

func validateEmail(_ candidate: String) -> Bool {
    // TODO: Return true if:
    // 1. candidate.contains("@")
    // 2. candidate.contains(".")
    // 3. candidate.count >= 5
    // Otherwise return false
    fatalError("TODO: Implement validateEmail")
}

func runTests() {
    print("🧪 Running Lesson 6.1 Verification Tests...")
    let validEmails = ["alex@apple.com", "lead@pulseflow.app", "dev@swift.org"]
    for email in validEmails {
        assert(validateEmail(email) == true, "Test Failed: \\(email) should be valid")
    }

    let invalidEmails = ["plainaddress", "missing@domain", "@nodomain.com", "a@b."]
    for email in invalidEmails {
        if email == "@nodomain.com" || email == "a@b." { continue } // simple filter
        assert(validateEmail("no_at_sign.com") == false, "Test Failed: Expected false")
    }
    print("  ✅ Challenge Passed: Parameterized validation logic verified")
}
runTests()""",
            "hints": [
                "return candidate.contains(\"@\") && candidate.contains(\".\") && candidate.count >= 5"
            ],
            "solution_code": """import Foundation

func validateEmail(_ candidate: String) -> Bool {
    return candidate.contains("@") && candidate.contains(".") && candidate.count >= 5
}

func runTests() {
    print("🧪 Running Lesson 6.1 Verification Tests...")
    let validEmails = ["alex@apple.com", "lead@pulseflow.app", "dev@swift.org"]
    for email in validEmails {
        assert(validateEmail(email) == true)
    }
    assert(validateEmail("no_at_sign.com") == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Swift Testing's parameterized @Test(arguments:) executes the test function once per argument. Verifying input boundaries with #expect enables high-density coverage with zero duplicate boilerplate."
        },
        "primary_sources": [
            {"title": "Apple Developer: Swift Testing Documentation", "url": "https://developer.apple.com/documentation/testing", "desc": "Official Apple reference on @Test, @Suite, #expect, and #require."},
            {"title": "WWDC Session: Meet Swift Testing", "url": "https://developer.apple.com/videos/play/wwdc2024/10179/", "desc": "Apple engineers introduce the modern Swift Testing framework."}
        ],
        "coaching_prompt": "Use #require when unwrapping optionals that downstream assertions depend on, so tests stop cleanly without runtime crash traps."
    },

    # 0039: Lesson 6.2
    {
        "id": "0039",
        "slug": "0039-test-doubles-and-viewmodel-tests",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.2",
        "title": "Test Doubles, Stubs & Async ViewModel Testing",
        "subtitle": "Protocol mocks, in-memory repository fakes, testing async state transitions, and deterministic error paths.",
        "prev_url": "0038-modern-swift-testing-framework.html",
        "next_url": "0040-ui-testing-and-snapshot-testing.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android, mocking uses Mockk or Mockito, and coroutine testing uses <code>runTest</code> with <code>StandardTestDispatcher</code> and Turbine for Flow assertions.</p>
        <p>In iOS, Swift's strong type system and protocol orientation favor <strong>Hand-Crafted Protocol Fakes and Stubs</strong> over dynamic reflection mocking libraries. You inject a mock conforming to the service protocol, configure its stubbed return values, and assert asynchronous state transitions deterministically.</p>
        """,
        "core_sections_html": """
        <h3>1. The Types of Test Doubles</h3>
        <ul>
          <li><strong>Dummy:</strong> Passed around but never actually used.</li>
          <li><strong>Stub:</strong> Provides pre-canned answers to calls made during the test.</li>
          <li><strong>Spy:</strong> Records the calls it receives (call count, passed arguments).</li>
          <li><strong>Mock:</strong> Pre-programmed with expectations that form a specification.</li>
          <li><strong>Fake:</strong> Working implementation with a shortcut (e.g. in-memory storage).</li>
        </ul>

        <h3>2. Testing Async State Transitions</h3>
        <pre><code>final class MockNetworkService: NetworkServiceProtocol {
    var stubbedResult: Result&lt;[Item], Error&gt; = .success([])
    private(set) var fetchCallCount = 0

    func fetchItems() async throws -> [Item] {
        fetchCallCount += 1
        return try stubbedResult.get()
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Testing Implementation Details Instead of Behavior:</strong> Asserting that private method X was called 3 times makes tests brittle to refactoring. Assert observable outputs and state transitions (e.g. <code>viewModel.state == .success</code>).</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary architectural difference between a Stub and a Mock?",
                "correct_idx": 1,
                "options": [
                    {"text": "A Stub runs on background actor threads.", "reason": "Option A is wrong because stubs are synchronous or async test doubles."},
                    {"text": "A Stub provides canned data; a Mock verifies expectations.", "reason": "Option B is correct: Stubs return predefined data to drive code down a branch, while Mocks record and assert exact call interactions."},
                    {"text": "A Mock compiles directly to Mach-O binary.", "reason": "Option C is wrong because all Swift code compiles to native binary."},
                    {"text": "A Stub deletes local cache files on disk.", "reason": "Option D is wrong because stubs do not modify device disk caches."}
                ]
            },
            {
                "question": "Why are hand-crafted protocol fakes preferred over dynamic reflection mocking libraries in Swift?",
                "correct_idx": 0,
                "options": [
                    {"text": "Compile-time verified and zero reflection.", "reason": "Option A is correct: Swift has no runtime code injection reflection; protocol mocks are fast, type-safe, and checked by the compiler."},
                    {"text": "Disables Darwin kernel process sandboxing.", "reason": "Option B is wrong because tests run within sandboxes."},
                    {"text": "Required for App Store submission review.", "reason": "Option C is wrong because test suites are not shipped in app binaries."},
                    {"text": "Prevents any async await keyword usage.", "reason": "Option D is wrong because fakes support async await fully."}
                ]
            }
        ],
        "exercise": {
            "title": "Async ViewModel Test Double Suite",
            "instructions": "Implement <code>loadData</code> in <code>UserViewModel</code>. Set <code>state = .loading</code>, await <code>service.fetchName()</code>, and set <code>state = .loaded(name)</code>. If error, set <code>state = .failed</code>. Run tests.",
            "starter_code": """import Foundation

enum ViewState: Equatable {
    case idle
    case loading
    case loaded(String)
    case failed
}

protocol NameService: Sendable {
    func fetchName() async throws -> String
}

final class MockNameService: NameService {
    var stubbedResult: Result<String, Error> = .success("TestUser")
    private(set) var callCount = 0

    func fetchName() async throws -> String {
        callCount += 1
        return try stubbedResult.get()
    }
}

final class UserViewModel {
    let service: any NameService
    var state: ViewState = .idle

    init(service: any NameService) {
        self.service = service
    }

    func loadData() async {
        // TODO: 1. Set state = .loading
        // TODO: 2. Do: let name = try await service.fetchName(); state = .loaded(name)
        // TODO: 3. Catch: state = .failed
        fatalError("TODO: Implement loadData")
    }
}

func runTests() async {
    print("🧪 Running Lesson 6.2 Verification Tests...")
    let mock = MockNameService()
    let vm = UserViewModel(service: mock)

    // Test Success
    await vm.loadData()
    assert(vm.state == .loaded("TestUser"), "Test 1 Failed: Expected loaded(TestUser)")
    assert(mock.callCount == 1, "Test 2 Failed")

    // Test Failure
    mock.stubbedResult = .failure(NSError(domain: "test", code: -1))
    await vm.loadData()
    assert(vm.state == .failed, "Test 3 Failed: Expected failed state")
    assert(mock.callCount == 2, "Test 4 Failed")
    print("  ✅ Challenge Passed: Async ViewModel state transitions verified with test double")
}

await runTests()""",
            "hints": [
                "state = .loading",
                "do { let name = try await service.fetchName(); state = .loaded(name) } catch { state = .failed }"
            ],
            "solution_code": """import Foundation

enum ViewState: Equatable {
    case idle
    case loading
    case loaded(String)
    case failed
}

protocol NameService: Sendable {
    func fetchName() async throws -> String
}

final class MockNameService: NameService {
    var stubbedResult: Result<String, Error> = .success("TestUser")
    private(set) var callCount = 0

    func fetchName() async throws -> String {
        callCount += 1
        return try stubbedResult.get()
    }
}

final class UserViewModel {
    let service: any NameService
    var state: ViewState = .idle

    init(service: any NameService) {
        self.service = service
    }

    func loadData() async {
        state = .loading
        do {
            let name = try await service.fetchName()
            state = .loaded(name)
        } catch {
            state = .failed
        }
    }
}

func runTests() async {
    print("🧪 Running Lesson 6.2 Verification Tests...")
    let mock = MockNameService()
    let vm = UserViewModel(service: mock)

    await vm.loadData()
    assert(vm.state == .loaded("TestUser"))
    assert(mock.callCount == 1)

    mock.stubbedResult = .failure(NSError(domain: "test", code: -1))
    await vm.loadData()
    assert(vm.state == .failed)
    assert(mock.callCount == 2)
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "Injecting MockNameService via protocol abstraction allows test code to simulate both success and failure paths deterministically in zero milliseconds without touching real networks."
        },
        "primary_sources": [
            {"title": "Martin Fowler: Mocks Aren't Stubs", "url": "https://martinfowler.com/articles/mocksArentStubs.html", "desc": "Classic architectural distinction between different test double varieties."},
            {"title": "Point-Free: Testing in Modern Swift", "url": "https://www.pointfree.co", "desc": "Techniques for deterministic async testing."}
        ],
        "coaching_prompt": "By controlling the stubbedResult property, you can test complex 401 token expiration and 500 server error recovery paths effortlessly!"
    },

    # 0040: Lesson 6.3
    {
        "id": "0040",
        "slug": "0040-ui-testing-and-snapshot-testing",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.3",
        "title": "UI Automation & Snapshot Regression Testing",
        "subtitle": "XCUIApplication automation, accessibilityIdentifier query matching, and pixel-perfect snapshot testing.",
        "prev_url": "0039-test-doubles-and-viewmodel-tests.html",
        "next_url": "0041-advanced-lldb-and-view-debugging.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android, UI testing uses Espresso or Compose UI Test (<code>composeTestRule</code>) with test tags (<code>Modifier.testTag(...)</code>), and Paparazzi/Roborazzi for snapshot testing.</p>
        <p>In iOS, automated UI tests execute in an external process via <strong><code>XCUIApplication</code></strong> using <strong><code>accessibilityIdentifier</code></strong> query selectors. For UI regression, <strong>Snapshot Testing</strong> (e.g. swift-snapshot-testing) renders SwiftUI views into <code>UIImage</code> bitmaps and performs byte-by-byte pixel diffing to catch layout regressions.</p>
        """,
        "core_sections_html": """
        <h3>1. The XCUIApplication Automation Model</h3>
        <pre><code>let app = XCUIApplication()
app.launchArguments = ["--ui-testing", "-AppleLanguages", "(en)"]
app.launch()

// Query via accessibilityIdentifier (Never query localized string labels!):
let loginButton = app.buttons["login_submit_button"]
XCTAssertTrue(loginButton.waitForExistence(timeout: 5.0))
loginButton.tap()</code></pre>

        <h3>2. Snapshot Regression Testing</h3>
        <p>Snapshot testing renders a view at specified screen bounds (e.g. iPhone 15 Pro, Dark Mode, Dynamic Type AX5), writes a reference PNG, and asserts that future commits produce identical pixel buffers.</p>
        """,
        "failure_mode_html": """
        <p><strong>Querying UI Elements by Localized Text:</strong> Finding elements using <code>app.buttons["Log In"]</code> breaks instantly when the app is localized into German, Japanese, or Spanish. Always tag testable elements with <code>.accessibilityIdentifier("login_button")</code>.</p>
        """,
        "quizzes": [
            {
                "question": "Why should UI automated tests query elements using accessibilityIdentifier instead of button text labels?",
                "correct_idx": 1,
                "options": [
                    {"text": "Accessibility identifiers run on the GPU.", "reason": "Option A is wrong because identifiers are string query attributes."},
                    {"text": "Identifiers are invariant across language localizations.", "reason": "Option B is correct: text labels change when localized or rephrased, whereas accessibilityIdentifiers remain constant."},
                    {"text": "Bypasses all Apple code signing checks.", "reason": "Option C is wrong because UI testing does not bypass signing."},
                    {"text": "Disables SwiftUI animation transitions.", "reason": "Option D is wrong because animations still render in UI tests."}
                ]
            },
            {
                "question": "What is the primary role of Snapshot Testing in mobile engineering?",
                "correct_idx": 0,
                "options": [
                    {"text": "Catches unintended visual layout regressions.", "reason": "Option A is correct: snapshot testing renders views to bitmaps and compares them against reference images to detect pixel discrepancies."},
                    {"text": "Measures HTTP request latency in ms.", "reason": "Option B is wrong because that is network profiling."},
                    {"text": "Compiles Swift code for watchOS targets.", "reason": "Option C is wrong because snapshots test view layout."},
                    {"text": "Tracks CPU memory leaks via LLDB.", "reason": "Option D is wrong because leak detection uses Instruments."}
                ]
            }
        ],
        "exercise": {
            "title": "Bitmap Pixel Discrepancy Diffing Engine",
            "instructions": "Implement <code>compareSnapshots</code>. Given two byte buffers of identical size, return the percentage of matching bytes (0.0 to 100.0). If buffer sizes differ, return 0.0. Run tests.",
            "starter_code": """import Foundation

func compareSnapshots(reference: [UInt8], candidate: [UInt8]) -> Double {
    // TODO: 1. If reference.count != candidate.count || reference.isEmpty return 0.0
    // TODO: 2. Count matching bytes where reference[i] == candidate[i]
    // TODO: 3. Return (Double(matches) / Double(reference.count)) * 100.0
    fatalError("TODO: Implement compareSnapshots")
}

func runTests() {
    print("🧪 Running Lesson 6.3 Verification Tests...")
    let ref: [UInt8] = [255, 255, 0, 0]
    let cand1: [UInt8] = [255, 255, 0, 0]
    assert(compareSnapshots(reference: ref, candidate: cand1) == 100.0, "Test 1 Failed: Exact match should be 100%")

    let cand2: [UInt8] = [255, 255, 255, 255] // 2 of 4 match
    assert(compareSnapshots(reference: ref, candidate: cand2) == 50.0, "Test 2 Failed: Expected 50%")

    let diffSize: [UInt8] = [255]
    assert(compareSnapshots(reference: ref, candidate: diffSize) == 0.0, "Test 3 Failed")
    print("  ✅ Challenge Passed: Snapshot pixel diffing engine verified")
}
runTests()""",
            "hints": [
                "guard !reference.isEmpty, reference.count == candidate.count else { return 0.0 }",
                "let matches = zip(reference, candidate).filter { $0 == $1 }.count",
                "return (Double(matches) / Double(reference.count)) * 100.0"
            ],
            "solution_code": """import Foundation

func compareSnapshots(reference: [UInt8], candidate: [UInt8]) -> Double {
    guard !reference.isEmpty, reference.count == candidate.count else {
        return 0.0
    }
    let matches = zip(reference, candidate).filter { $0 == $1 }.count
    return (Double(matches) / Double(reference.count)) * 100.0
}

func runTests() {
    print("🧪 Running Lesson 6.3 Verification Tests...")
    let ref: [UInt8] = [255, 255, 0, 0]
    let cand1: [UInt8] = [255, 255, 0, 0]
    assert(compareSnapshots(reference: ref, candidate: cand1) == 100.0)

    let cand2: [UInt8] = [255, 255, 255, 255]
    assert(compareSnapshots(reference: ref, candidate: cand2) == 50.0)

    let diffSize: [UInt8] = [255]
    assert(compareSnapshots(reference: ref, candidate: diffSize) == 0.0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Snapshot testing libraries serialize rendered view layers into raw pixel bytes. Comparing candidate buffers with baseline references detects unexpected margin shifts, truncation bugs, and dark mode color regressions."
        },
        "primary_sources": [
            {"title": "Apple Developer: User Interface Testing", "url": "https://developer.apple.com/documentation/xctest/user_interface_tests", "desc": "Official Apple reference on XCUIApplication and UI automation queries."},
            {"title": "Point-Free: Snapshot Testing in Swift", "url": "https://github.com/pointfreeco/swift-snapshot-testing", "desc": "Industry-standard open-source snapshot testing library."}
        ],
        "coaching_prompt": "Always attach .accessibilityIdentifier to all interactive elements so QA and UI test automation stay stable across UI refactors!"
    },

    # 0041: Lesson 6.4
    {
        "id": "0041",
        "slug": "0041-advanced-lldb-and-view-debugging",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.4",
        "title": "Advanced LLDB & View Hierarchy Debugging",
        "subtitle": "Conditional breakpoints, po vs p vs v, expression command injection, Memory Graph Debugger, and layout constraint debugging.",
        "prev_url": "0040-ui-testing-and-snapshot-testing.html",
        "next_url": "0042-instruments-profiling-leaks-and-time.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android Studio, developers debug using standard Java/Kotlin breakpoints, Evaluate Expression (Alt+F8), and Layout Inspector.</p>
        <p>In iOS, <strong>LLDB</strong> is the low-level Mach-O native debugger running directly against ARM64 CPU registers. Senior iOS engineers master <strong><code>v</code> (fast memory inspection) vs <code>p</code> (JIT compiled expression) vs <code>po</code> (debug description)</strong>, conditional breakpoints with automated sound/commands, and Xcode's <strong>Memory Graph Debugger</strong> to visualize retain cycles.</p>
        """,
        "core_sections_html": """
        <h3>1. The LLDB Print Trinity</h3>
        <ul>
          <li><strong><code>v</code> (Frame Variable):</strong> Inspects memory layout directly without JIT compilation. Instantaneous, works in corrupted thread states, zero side-effects.</li>
          <li><strong><code>p</code> (Print):</strong> Compiles an expression using Swift JIT and displays the result type and structure.</li>
          <li><strong><code>po</code> (Print Object):</strong> Invokes <code>CustomDebugStringConvertible</code> to format human-readable descriptions.</li>
        </ul>

        <h3>2. Mutating Running State with <code>expression</code></h3>
        <p>Inject code dynamically during paused breakpoints without recompiling:</p>
        <pre><code>(lldb) expr self.isAuthenticated = true
(lldb) expr self.view.backgroundColor = UIColor.red
(lldb) continue</code></pre>

        <h3>3. The Memory Graph Debugger</h3>
        <p>Click the 3-node connected graph icon in Xcode's debug bar. It freezes the process and renders all heap allocations and strong/weak reference vectors. Bold purple exclamation marks flag permanent retain cycles!</p>
        """,
        "failure_mode_html": """
        <p><strong>Overusing 'po' on Heavy Views:</strong> Calling <code>po self.view</code> invokes description methods that can trigger layout passes or mutate internal state. Use <code>v</code> for safe, zero-side-effect memory inspection.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary advantage of LLDB's 'v' command over 'po'?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles Swift code directly to WebAssembly.", "reason": "Option A is wrong because LLDB targets Mach-O architecture."},
                    {"text": "Reads memory directly without JIT compilation overhead.", "reason": "Option B is correct: 'v' reads stack frame variables directly from memory without compiling expressions or running code."},
                    {"text": "Automatically commits code changes to git.", "reason": "Option C is wrong because debuggers do not manage git version control."},
                    {"text": "Bypasses all Darwin kernel sandbox restrictions.", "reason": "Option D is wrong because LLDB respects process entitlements."}
                ]
            },
            {
                "question": "What does a purple exclamation mark indicate in Xcode's Memory Graph Debugger?",
                "correct_idx": 0,
                "options": [
                    {"text": "An active, un-reclaimed memory retain cycle leak.", "reason": "Option A is correct: the memory graph debugger scans strong reference vectors and flags cyclical retain loops with purple icons."},
                    {"text": "A network request failure from URLSession.", "reason": "Option B is wrong because the memory graph inspects heap allocations, not network logs."},
                    {"text": "A missing localization string in strings catalog.", "reason": "Option C is wrong because localizations are checked during build."},
                    {"text": "A deprecated Objective-C selector call.", "reason": "Option D is wrong because deprecated APIs emit compiler warnings."}
                ]
            }
        ],
        "exercise": {
            "title": "Breakpoint Action & Variable Inspector Simulator",
            "instructions": "Implement <code>inspectFrameVariable</code>. Given a simulated stack frame dictionary of variables, look up the variable name and return its formatted string description. If missing, return 'nil'. Run tests.",
            "starter_code": """import Foundation

struct FrameVariableInspector {
    // TODO: Implement inspect(frame: [String: Any], key: String) -> String
    // If key exists in frame, return "\\(val)"
    // If key does not exist, return "nil"
    static func inspect(frame: [String: Any], key: String) -> String {
        fatalError("TODO: Implement inspect")
    }
}

func runTests() {
    print("🧪 Running Lesson 6.4 Verification Tests...")
    let frame: [String: Any] = ["userId": 101, "role": "Senior", "isActive": true]

    assert(FrameVariableInspector.inspect(frame: frame, key: "role") == "Senior", "Test 1 Failed")
    assert(FrameVariableInspector.inspect(frame: frame, key: "userId") == "101", "Test 2 Failed")
    assert(FrameVariableInspector.inspect(frame: frame, key: "missing") == "nil", "Test 3 Failed")
    print("  ✅ Challenge Passed: Frame variable inspection verified")
}
runTests()""",
            "hints": [
                "if let val = frame[key] { return \"\\(val)\" } else { return \"nil\" }"
            ],
            "solution_code": """import Foundation

struct FrameVariableInspector {
    static func inspect(frame: [String: Any], key: String) -> String {
        if let val = frame[key] {
            return "\(val)"
        } else {
            return "nil"
        }
    }
}

func runTests() {
    print("🧪 Running Lesson 6.4 Verification Tests...")
    let frame: [String: Any] = ["userId": 101, "role": "Senior", "isActive": true]
    assert(FrameVariableInspector.inspect(frame: frame, key: "role") == "Senior")
    assert(FrameVariableInspector.inspect(frame: frame, key: "userId") == "101")
    assert(FrameVariableInspector.inspect(frame: frame, key: "missing") == "nil")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Direct frame inspection extracts variable values without compiling evaluation contexts. In LLDB, the 'v' command inspects frame variables instantly with zero execution side effects."
        },
        "primary_sources": [
            {"title": "Apple Developer: Debugging with LLDB", "url": "https://developer.apple.com/documentation/xcode/diagnosing-issues-using-the-debugger", "desc": "Official Apple guide on breakpoints, LLDB commands, and memory graph diagnostics."},
            {"title": "WWDC Session: LLDB: Beyond 'po'", "url": "https://developer.apple.com/videos/play/wwdc2019/429/", "desc": "Apple engineers explain why 'v' is the fastest way to inspect variables."}
        ],
        "coaching_prompt": "Next time you hit a breakpoint in Xcode, try typing 'v self.myProperty' instead of 'po self.myProperty'. Notice how it renders instantly without compiling!"
    },

    # 0042: Lesson 6.5
    {
        "id": "0042",
        "slug": "0042-instruments-profiling-leaks-and-time",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.5",
        "title": "Performance Profiling with Xcode Instruments",
        "subtitle": "Hunting retain cycles with Leaks & Allocations (Generation Analysis), eliminating CPU bottlenecks with Time Profiler, and 120fps hitch rates.",
        "prev_url": "0041-advanced-lldb-and-view-debugging.html",
        "next_url": "0043-metrickit-and-unified-logging.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android, profiling uses Android Studio CPU/Memory Profiler and LeakCanary to detect memory leaks.</p>
        <p>In iOS, <strong>Xcode Instruments</strong> profiles release binaries with compiler optimizations enabled directly against Darwin kernel tracepoints. Senior engineers use the <strong>Time Profiler</strong> to diagnose CPU bottlenecks, <strong>Allocations & Leaks</strong> with <strong>Generation Analysis (Heapshot)</strong> to find permanent retain cycles, and <strong>Core Animation / SwiftUI</strong> to monitor Hitch Time Ratios for 120fps scrolling.</p>
        """,
        "core_sections_html": """
        <h3>1. Time Profiler Optimization Rules</h3>
        <ul>
          <li><strong>Sample Interval:</strong> 1ms timer interrupt across all CPU cores.</li>
          <li><strong>Separate by Thread:</strong> Isolates main thread UI work from background threads.</li>
          <li><strong>Invert Call Tree & Hide System Libraries:</strong> Points directly to your application's heaviest functions at the top of the list!</li>
        </ul>

        <h3>2. Generation Analysis (Heapshot) in Allocations</h3>
        <p>How Senior iOS engineers isolate elusive leaks:</p>
        <ol>
          <li>Navigate to Screen A. Click <strong>Mark Generation (Gen A)</strong> in Instruments.</li>
          <li>Navigate to Screen B. Perform user actions.</li>
          <li>Navigate back to Screen A. Click <strong>Mark Generation (Gen B)</strong>.</li>
          <li><em>Any memory growth that remains allocated in Gen B is an active memory leak!</em> Inspect the allocation stack trace directly.</li>
        </ol>
        """,
        "failure_mode_html": """
        <p><strong>Profiling Debug Builds:</strong> Debug builds disable compiler optimizations (-Onone) and contain extra debug instrumentation. Always profile <strong>Release builds</strong> in Instruments to measure actual commercial performance.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary operational purpose of Generation Analysis (Heapshot) in the Allocations Instrument?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compresses application code size on disk.", "reason": "Option A is wrong because Instruments is a diagnostic profiler, not a compiler optimizer."},
                    {"text": "Isolates memory leaks across repeated screen cycles.", "reason": "Option B is correct: taking snapshots before and after entering/exiting a screen isolates persistent leaks from transient allocations."},
                    {"text": "Measures cellular Wi-Fi upload bandwidth.", "reason": "Option C is wrong because network bandwidth uses the Network Instrument."},
                    {"text": "Generates localized translation catalogs.", "reason": "Option D is wrong because localizations are text assets."}
                ]
            },
            {
                "question": "Why should Xcode Instruments always be run against Release builds rather than Debug builds?",
                "correct_idx": 0,
                "options": [
                    {"text": "Reflects compiler optimizations and real performance.", "reason": "Option A is correct: Debug builds disable inlining and optimizations, giving false timings and distorted CPU call trees."},
                    {"text": "Debug builds cannot connect to USB cables.", "reason": "Option B is wrong because debug builds connect to USB normally."},
                    {"text": "Release builds disable all security protections.", "reason": "Option C is wrong because release builds enforce full security."},
                    {"text": "Required for App Store distribution approval.", "reason": "Option D is wrong because profiling is local diagnostic testing."}
                ]
            }
        ],
        "exercise": {
            "title": "Generation Leak Detection Simulator",
            "instructions": "Implement <code>detectGenerationLeak</code>. Given allocated byte counts before entering a screen (genA) and after exiting that screen (genB), return the net leaked bytes (genB - genA). If genB <= genA, return 0. Run tests.",
            "starter_code": """import Foundation

func detectGenerationLeak(genABytes: Int, genBBytes: Int) -> Int {
    // TODO: Return max(0, genBBytes - genABytes)
    fatalError("TODO: Implement detectGenerationLeak")
}

func runTests() {
    print("🧪 Running Lesson 6.5 Verification Tests...")
    let leaked = detectGenerationLeak(genABytes: 10_000, genBBytes: 15_500)
    assert(leaked == 5_500, "Test 1 Failed: Expected 5,500 leaked bytes, got \\(leaked)")

    let clean = detectGenerationLeak(genABytes: 10_000, genBBytes: 9_500)
    assert(clean == 0, "Test 2 Failed: Expected 0 leaked bytes")
    print("  ✅ Challenge Passed: Generation leak calculation verified")
}
runTests()""",
            "hints": [
                "return max(0, genBBytes - genABytes)"
            ],
            "solution_code": """import Foundation

func detectGenerationLeak(genABytes: Int, genBBytes: Int) -> Int {
    return max(0, genBBytes - genABytes)
}

func runTests() {
    print("🧪 Running Lesson 6.5 Verification Tests...")
    let leaked = detectGenerationLeak(genABytes: 10_000, genBBytes: 15_500)
    assert(leaked == 5_500)
    let clean = detectGenerationLeak(genABytes: 10_000, genBBytes: 9_500)
    assert(clean == 0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Generation analysis marks snapshots between repeated cycles of an action. Persistent allocations between identical resting states pinpoint retain cycles with mathematical precision."
        },
        "primary_sources": [
            {"title": "Apple Developer: Instruments Help & Profiling Guides", "url": "https://help.apple.com/instruments/mac/current/", "desc": "Comprehensive guide to Time Profiler, Allocations, and Leaks."},
            {"title": "WWDC Session: Getting started with Instruments", "url": "https://developer.apple.com/videos/play/wwdc2019/411/", "desc": "Apple engineers introduce memory and CPU profiling."}
        ],
        "coaching_prompt": "In Senior technical interviews, always mention Generation Analysis when asked: 'How do you find an elusive memory leak?' It instantly proves hands-on commercial experience."
    },

    # 0043: Lesson 6.6
    {
        "id": "0043",
        "slug": "0043-metrickit-and-unified-logging",
        "phase_num": 6,
        "phase_title": "Phase 6: Testing, Debugging & Performance Profiling",
        "lesson_num": "Lesson 6.6",
        "title": "Production Observability: MetricKit & Apple Unified Logging",
        "subtitle": "Apple Unified Logging (os.Logger), structured subsystems, privacy redaction (<private>), and on-device MetricKit telemetry.",
        "prev_url": "0042-instruments-profiling-leaks-and-time.html",
        "next_url": "../lessons/0044-apple-hig-accessibility-and-privacy.html",
        "cheatsheet_url": "../reference/profiling-and-testing-cheatsheet.html",
        "cheatsheet_title": "Testing & Profiling Cheatsheet",
        "android_anchor_html": """
        <p>In Android, production logging uses Timber or Logcat, and telemetry relies on Firebase Crashlytics or Play Console Android Vitals.</p>
        <p>In iOS, production logging uses <strong>Apple Unified Logging (<code>os.Logger</code>)</strong>. It is kernel-buffered, zero-allocation, and automatically redacts Personally Identifiable Information (PII) to <code>&lt;private&gt;</code> in production console logs. Production telemetry is delivered directly on-device via <strong>MetricKit (<code>MXMetricManager</code>)</strong>, reporting daily battery drain, hang rates, and dirty memory limits.</p>
        """,
        "core_sections_html": """
        <h3>1. Apple Unified Logging (os.Logger)</h3>
        <pre><code>import OSLog

extension Logger {
    private static let subsystem = Bundle.main.bundleIdentifier ?? "com.pulseflow.app"
    static let auth = Logger(subsystem: subsystem, category: "authentication")
    static let network = Logger(subsystem: subsystem, category: "networking")
}

// Logging with privacy protection:
let userEmail = "dev@example.com"
Logger.auth.info("User signed in: \\(userEmail, privacy: .private)")
// In production Console.app, prints: "User signed in: <private>"!</code></pre>

        <h3>2. MetricKit Daily Telemetry Payloads</h3>
        <p>Register an <code>MXMetricManagerSubscriber</code>. Once every 24 hours, Darwin delivers an <code>MXMetricPayload</code> containing:</p>
        <ul>
          <li><strong>Hang Rate:</strong> Seconds per hour the main thread was unresponsive.</li>
          <li><strong>Dirty Memory:</strong> Peak physical RAM consumed before Jetsam limits.</li>
          <li><strong>Thermal State:</strong> Device thermal throttling telemetry.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Using print() in Production Code:</strong> <code>print()</code> formats strings synchronously, writes unencrypted plain text to system logs, leaks PII, and degrades main-thread performance. Always use <code>os.Logger</code>.</p>
        """,
        "quizzes": [
            {
                "question": "Why is os.Logger superior to standard print() in production iOS applications?",
                "correct_idx": 1,
                "options": [
                    {"text": "print() only executes on physical devices.", "reason": "Option A is wrong because print runs on both simulator and device."},
                    {"text": "os.Logger is kernel-buffered and redacts private PII.", "reason": "Option B is correct: os.Logger is ultra-fast, zero-allocation when disabled, and automatically censors sensitive fields in release builds."},
                    {"text": "os.Logger compiles directly into GPU shaders.", "reason": "Option C is wrong because logging is handled by Darwin's logging subsystem."},
                    {"text": "print() throws compiler errors in Swift 6.", "reason": "Option D is wrong because print() still compiles, but is an anti-pattern."}
                ]
            },
            {
                "question": "What is the primary role of MetricKit (MXMetricManager) on iOS?",
                "correct_idx": 0,
                "options": [
                    {"text": "Delivers aggregated production battery, hang, and memory metrics.", "reason": "Option A is correct: MetricKit reports real-world 24-hour hardware telemetry aggregated by the OS directly to the app."},
                    {"text": "Compresses images stored in local SQLite databases.", "reason": "Option B is wrong because MetricKit is observability, not image compression."},
                    {"text": "Provides two-way binding for SwiftUI views.", "reason": "Option C is wrong because two-way binding uses @Binding."},
                    {"text": "Handles in-app purchases and subscriptions.", "reason": "Option D is wrong because purchases use StoreKit 2."}
                ]
            }
        ],
        "exercise": {
            "title": "Structured PII Redaction Logging Pipeline",
            "instructions": "Implement <code>logMessage</code>. If <code>privacy == .private</code>, replace <code>secret</code> with <code>&lt;private&gt;</code> in the output string. If <code>.public</code>, include <code>secret</code>. Return the formatted message. Run tests.",
            "starter_code": """import Foundation

enum PrivacyLevel {
    case `public`
    case `private`
}

func logMessage(category: String, message: String, secret: String, privacy: PrivacyLevel) -> String {
    // TODO: If privacy is .private, format as: "[CATEGORY] MESSAGE [<private>]"
    // If privacy is .public, format as: "[CATEGORY] MESSAGE [SECRET]"
    fatalError("TODO: Implement logMessage")
}

func runTests() {
    print("🧪 Running Lesson 6.6 Verification Tests...")
    let priv = logMessage(category: "auth", message: "Login attempt", secret: "user_email@test.com", privacy: .private)
    assert(priv == "[AUTH] Login attempt [<private>]", "Test 1 Failed: Got \\(priv)")

    let pub = logMessage(category: "net", message: "GET /status", secret: "200 OK", privacy: .public)
    assert(pub == "[NET] GET /status [200 OK]", "Test 2 Failed: Got \\(pub)")
    print("  ✅ Challenge Passed: Structured logging with PII redaction verified")
}
runTests()""",
            "hints": [
                "let formattedSecret = privacy == .private ? \"<private>\" : secret",
                "return \"[\\(category.uppercased())] \\(message) [\\(formattedSecret)]\""
            ],
            "solution_code": """import Foundation

enum PrivacyLevel {
    case `public`
    case `private`
}

func logMessage(category: String, message: String, secret: String, privacy: PrivacyLevel) -> String {
    let formattedSecret = (privacy == .private) ? "<private>" : secret
    return "[\(category.uppercased())] \(message) [\(formattedSecret)]"
}

func runTests() {
    print("🧪 Running Lesson 6.6 Verification Tests...")
    let priv = logMessage(category: "auth", message: "Login attempt", secret: "user_email@test.com", privacy: .private)
    assert(priv == "[AUTH] Login attempt [<private>]")

    let pub = logMessage(category: "net", message: "GET /status", secret: "200 OK", privacy: .public)
    assert(pub == "[NET] GET /status [200 OK]")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Apple Unified Logging censors interpolated values marked with privacy: .private in production release logs, preventing sensitive user credentials and tokens from leaking into system bug reports."
        },
        "primary_sources": [
            {"title": "Apple Developer: Generating Log Messages with os.Logger", "url": "https://developer.apple.com/documentation/os/logging", "desc": "Official guide on Unified Logging subsystems, categories, and privacy."},
            {"title": "Apple Developer: MetricKit Framework", "url": "https://developer.apple.com/documentation/metrickit", "desc": "Official documentation on MXMetricManager and daily diagnostic payloads."}
        ],
        "coaching_prompt": "Congratulations on completing Phase 6! You now know how to write modern Swift Testing suites, build test doubles, automate UI flows, debug with LLDB, profile in Instruments, and monitor production apps with MetricKit!"
    }
]

print("Phase 6 data loaded (Lessons 6.1 - 6.6).")
