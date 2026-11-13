# Phase 8: 2027 Senior iOS Frontier & Platform Expansion (Lessons 0049 - 0060)

PHASE8_FRONTIER_LESSONS = [
    # 0049: Lesson 8.1
    {
        "id": "0049",
        "slug": "0049-objc-interop-and-runtime-bridging",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.1",
        "title": "Objective-C Interoperability & Legacy Runtime Bridging",
        "subtitle": "Bridging headers, @objc / dynamic dispatch, NSError** bridging, Unmanaged memory ownership, and wrapping legacy Obj-C SDKs for Swift 6.",
        "prev_url": "0048-cicd-pipelines-and-release-automation.html",
        "next_url": "0050-swift62-approachable-concurrency-and-mutex.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, senior engineers rarely write new Java, yet routinely read legacy Java SDKs, nullability annotations (<code>@Nullable</code>/<code>@NonNull</code>), and JNI boundaries from Kotlin.</p>
        <p>In iOS, you will rarely author new Objective-C in 2027, but Senior engineers must fluently read Obj-C headers, wrap legacy vendor SDKs into Swift 6 <code>Sendable</code> boundaries, understand <code>objc_msgSend</code> dynamic dispatch, and safely bridge CoreFoundation pointers using <code>Unmanaged&lt;T&gt;</code>.</p>
        """,
        "core_sections_html": """
        <h3>1. Reading Obj-C Headers &amp; Nullability Auditing</h3>
        <pre><code>// Legacy Obj-C Header (.h)
NS_ASSUME_NONNULL_BEGIN
@interface LegacyPaymentClient : NSObject
@property (nonatomic, copy, readonly) NSString *merchantID;
- (nullable NSString *)tokenizeCard:(NSString *)pan error:(NSError **)error;
@end
NS_ASSUME_NONNULL_END</code></pre>
        <p>Swift imports <code>error:(NSError **)</code> automatically as a <code>throws</code> method returning <code>String?</code>. Without <code>NS_ASSUME_NONNULL_BEGIN</code>, every parameter imports as an implicitly unwrapped optional (<code>String!</code>)—a major crash hazard.</p>

        <h3>2. @objc, dynamic, and Unmanaged&lt;T&gt;</h3>
        <ul>
          <li><strong><code>@objc</code>:</strong> Exposes Swift declarations to the Objective-C runtime (required for selectors, <code>NSCoding</code>, or KVO).</li>
          <li><strong><code>@objc dynamic</code>:</strong> Forces message dispatch via <code>objc_msgSend</code> instead of vtable/static dispatch (needed for Key-Value Observing and runtime swizzling).</li>
          <li><strong><code>Unmanaged&lt;T&gt;</code>:</strong> Manual retain/release bridge when passing Swift objects through opaque C <code>void *</code> context pointers (<code>passRetained</code> vs <code>takeRetainedValue</code>).</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Leaking or Over-Releasing C Context Pointers:</strong> Calling <code>Unmanaged.passRetained(obj).toOpaque()</code> and later consuming it with <code>takeUnretainedValue()</code> permanently leaks the object. Conversely, calling <code>takeRetainedValue()</code> after <code>passUnretained</code> causes an immediate <code>EXC_BAD_ACCESS</code> use-after-free crash.</p>
        """,
        "quizzes": [
            {
                "question": "How does Swift automatically import an Objective-C method whose last parameter is `(NSError **)error`?",
                "correct_idx": 1,
                "options": [
                    {"text": "As a completion closure taking Result.", "reason": "Option A is wrong because NSError** maps to synchronous throwing syntax, not closures."},
                    {"text": "As a synchronous throwing Swift function.", "reason": "Option B is correct: Swift automatically bridges the Obj-C NSError** out-parameter convention into Swift's throws error mechanism."},
                    {"text": "As an async actor-isolated property.", "reason": "Option C is wrong because NSError** is synchronous and unrelated to actor isolation."},
                    {"text": "As an unsafe raw pointer tuple.", "reason": "Option D is wrong because Swift eliminates the manual double-pointer pattern during import."}
                ]
            },
            {
                "question": "Why should you prefer `passUnretained` only when the Swift object outlives the C callback?",
                "correct_idx": 0,
                "options": [
                    {"text": "passUnretained does not increment ARC count.", "reason": "Option A is correct: if the Swift instance deallocates before the C callback fires, takeUnretainedValue dereferences freed memory."},
                    {"text": "passUnretained converts structs into classes.", "reason": "Option B is wrong because Unmanaged requires AnyObject reference types."},
                    {"text": "passUnretained triggers garbage collection.", "reason": "Option C is wrong because Swift uses ARC, not garbage collection."},
                    {"text": "passUnretained disables Swift strict concurrency.", "reason": "Option D is wrong because Unmanaged governs reference counting, not compiler concurrency flags."}
                ]
            }
        ],
        "exercise": {
            "title": "Safe Unmanaged C-Context Bridge Wrapper",
            "instructions": "Implement <code>BridgeBox.wrapRetained</code> and <code>BridgeBox.unwrapAndRelease</code> using <code>Unmanaged&lt;Payload&gt;</code> so the opaque <code>UnsafeMutableRawPointer</code> balances retains and releases without leaking. Run tests.",
            "starter_code": """import Foundation

final class Payload {
    let tag: String
    var onDeinit: (() -> Void)?
    init(tag: String, onDeinit: (() -> Void)? = nil) {
        self.tag = tag
        self.onDeinit = onDeinit
    }
    deinit { onDeinit?() }
}

enum BridgeBox {
    // TODO: Convert Payload into an opaque UnsafeMutableRawPointer with +1 retain count
    static func wrapRetained(_ payload: Payload) -> UnsafeMutableRawPointer {
        fatalError("TODO: Implement wrapRetained")
    }

    // TODO: Recover Payload from opaque pointer and consume the +1 retain count
    static func unwrapAndRelease(_ pointer: UnsafeMutableRawPointer) -> Payload {
        fatalError("TODO: Implement unwrapAndRelease")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.1 Verification Tests...")
    var deinitialized = false
    do {
        let raw: UnsafeMutableRawPointer
        do {
            let p = Payload(tag: "legacy-sdk-token") { deinitialized = true }
            raw = BridgeBox.wrapRetained(p)
        }
        assert(deinitialized == false, "Test 1 Failed: Object freed prematurely while retained in opaque pointer")
        let recovered = BridgeBox.unwrapAndRelease(raw)
        assert(recovered.tag == "legacy-sdk-token", "Test 2 Failed: Tag mismatch")
    }
    assert(deinitialized == true, "Test 3 Failed: Object leaked due to unbalanced retain")
    print("  ✅ Challenge Passed: Unmanaged C-context retain/release balance verified")
}
runTests()""",
            "hints": [
                "Use Unmanaged.passRetained(payload).toOpaque() inside wrapRetained.",
                "Use Unmanaged<Payload>.fromOpaque(pointer).takeRetainedValue() inside unwrapAndRelease.",
                "takeRetainedValue() transfers the +1 retain count into ARC so the object frees when recovered leaves scope."
            ],
            "solution_code": """import Foundation

final class Payload {
    let tag: String
    var onDeinit: (() -> Void)?
    init(tag: String, onDeinit: (() -> Void)? = nil) {
        self.tag = tag
        self.onDeinit = onDeinit
    }
    deinit { onDeinit?() }
}

enum BridgeBox {
    static func wrapRetained(_ payload: Payload) -> UnsafeMutableRawPointer {
        Unmanaged.passRetained(payload).toOpaque()
    }

    static func unwrapAndRelease(_ pointer: UnsafeMutableRawPointer) -> Payload {
        Unmanaged<Payload>.fromOpaque(pointer).takeRetainedValue()
    }
}

func runTests() {
    print("🧪 Running Lesson 8.1 Verification Tests...")
    var deinitialized = false
    do {
        let raw: UnsafeMutableRawPointer
        do {
            let p = Payload(tag: "legacy-sdk-token") { deinitialized = true }
            raw = BridgeBox.wrapRetained(p)
        }
        assert(deinitialized == false)
        let recovered = BridgeBox.unwrapAndRelease(raw)
        assert(recovered.tag == "legacy-sdk-token")
    }
    assert(deinitialized == true)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "<code>Unmanaged.passRetained(_:).toOpaque()</code> increments ARC by +1 before erasing to <code>void *</code>, and <code>fromOpaque(_:).takeRetainedValue()</code> consumes that +1 when returning control to ARC—preventing both premature deallocation and memory leaks."
        },
        "primary_sources": [
            {"title": "Apple Developer: Imported C and Objective-C APIs", "url": "https://developer.apple.com/documentation/swift/imported-c-and-objective-c-apis", "desc": "Official Swift documentation on Objective-C nullability, NSError bridging, and Unmanaged pointers."},
            {"title": "Mike Ash: Friday Q&A — objc_msgSend", "url": "https://www.mikeash.com/pyblog/friday-qa-2017-06-30-dissecting-objc_msgsend-on-arm64.html", "desc": "ARM64 assembly deep dive into Objective-C dynamic message dispatch."}
        ],
        "coaching_prompt": "You now know how to wrap legacy Obj-C and C APIs safely without memory leaks. Ready for Lesson 8.2: Swift 6.2 & 6.3 Approachable Concurrency and Synchronization.Mutex?"
    },

    # 0050: Lesson 8.2
    {
        "id": "0050",
        "slug": "0050-swift62-approachable-concurrency-and-mutex",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.2",
        "title": "Swift 6.2/6.3 Approachable Concurrency & Mutex",
        "subtitle": "SE-0461 nonisolated(nonsending), @concurrent opt-in, default @MainActor isolation, and Synchronization.Mutex for synchronous critical sections.",
        "prev_url": "0049-objc-interop-and-runtime-bridging.html",
        "next_url": "0051-noncopyable-types-inlinearray-and-span.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin Coroutines, calling a <code>suspend</code> function stays on the caller's dispatcher until you explicitly call <code>withContext(Dispatchers.IO)</code>. In Swift 6.0, calling a <code>nonisolated async</code> function unexpectedly hopped <em>off</em> <code>@MainActor</code> to the global cooperative pool, triggering confusing <code>Sendable</code> errors.</p>
        <p>Swift 6.2/6.3 introduces <strong>Approachable Concurrency</strong>: under SE-0461, <code>nonisolated(nonsending)</code> keeps async helpers on the caller's executor by default, while <code>@concurrent</code> explicitly requests background execution. Meanwhile, <code>Synchronization.Mutex</code> replaces legacy GCD barrier queues and <code>NSLock</code> for fast synchronous locking.</p>
        """,
        "core_sections_html": """
        <h3>1. SE-0461: nonisolated(nonsending) vs. @concurrent</h3>
        <pre><code>// Swift 6.2+ (NonisolatedNonsendingByDefault enabled):
// Runs on caller's actor (e.g., stays on MainActor if called from ViewModel!)
func formatUserSummary(_ user: User) async -> String { ... }

// Explicitly hops to the global concurrent executor for heavy CPU/IO work:
@concurrent
func parseLargeTelemetryBlob(_ data: Data) async -> [Metric] { ... }</code></pre>

        <h3>2. Synchronization.Mutex vs. Actor</h3>
        <ul>
          <li><strong>When to use <code>actor</code>:</strong> You need asynchronous coordination, suspension points (<code>await</code>), or task serialization.</li>
          <li><strong>When to use <code>Mutex&lt;State&gt;</code>:</strong> You need <em>synchronous</em> thread-safe access (e.g., an in-memory cache, OS callback, or metric counter) without <code>await</code> suspension overhead or actor reentrancy!</li>
          <li><strong>Compiler safety:</strong> Unlike <code>NSLock</code>, <code>Mutex</code> owns the protected value inside its closure (<code>withLock { state in ... }</code>) and enforces <code>Sendable</code> conformance automatically.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Overusing Actors for Synchronous Hot-Path State:</strong> Wrapping a tiny counter or LRU cache in an <code>actor</code> forces every caller into an async context and introduces suspension points where actor reentrancy bugs can occur. Use <code>Synchronization.Mutex</code> for synchronous, sub-microsecond critical sections.</p>
        """,
        "quizzes": [
            {
                "question": "Under Swift 6.2+ Approachable Concurrency (SE-0461), where does a `nonisolated` async function execute by default?",
                "correct_idx": 2,
                "options": [
                    {"text": "Always on a brand-new GCD thread.", "reason": "Option A is wrong because Swift Concurrency uses cooperative executors, not per-call GCD threads."},
                    {"text": "Always on the background concurrent pool.", "reason": "Option B describes the older Swift 5.5/6.0 behavior before SE-0461."},
                    {"text": "On the caller's current actor executor.", "reason": "Option C is correct: nonisolated(nonsending) inherits the caller's isolation unless annotated with @concurrent."},
                    {"text": "Always on the MainActor runloop.", "reason": "Option D is wrong because if called from a background actor, it stays on that background actor."}
                ]
            },
            {
                "question": "Why does `Synchronization.Mutex<Value>` prevent forgotten unlocks and data races better than `NSLock`?",
                "correct_idx": 1,
                "options": [
                    {"text": "It automatically pauses across await boundaries.", "reason": "Option A is wrong because Mutex is strictly synchronous and cannot be held across await."},
                    {"text": "It owns the state inside withLock.", "reason": "Option B is correct: the protected value can only be accessed inside the scoped withLock closure, guaranteeing unlock and non-escaping access."},
                    {"text": "It dispatches closures onto the MainActor.", "reason": "Option C is wrong because Mutex locks synchronously on the calling thread."},
                    {"text": "It delegates locking to Objective-C runtime.", "reason": "Option D is wrong because Mutex is a pure Swift noncopyable wrapper over Darwin os_unfair_lock."}
                ]
            }
        ],
        "exercise": {
            "title": "Thread-Safe Synchronous Counter Cache with Synchronization.Mutex",
            "instructions": "Use Swift's <code>Synchronization.Mutex</code> to implement <code>ThreadSafeMetrics</code> with synchronous <code>recordHit(for:)</code> and <code>count(for:)</code> methods without any actors or <code>await</code>. Run tests.",
            "starter_code": """import Foundation
import Synchronization

final class ThreadSafeMetrics: Sendable {
    // TODO: Store a Mutex<[String: Int]> initialized to [:]
    
    func recordHit(for endpoint: String) {
        // TODO: Increment endpoint count atomically inside withLock
        fatalError("TODO: Implement recordHit")
    }

    func count(for endpoint: String) -> Int {
        // TODO: Read endpoint count inside withLock
        fatalError("TODO: Implement count")
    }
}

func runTests() async {
    print("🧪 Running Lesson 8.2 Verification Tests...")
    let metrics = ThreadSafeMetrics()
    await withTaskGroup(of: Void.self) { group in
        for _ in 0..<200 {
            group.addTask {
                metrics.recordHit(for: "/v1/feed")
            }
        }
    }
    assert(metrics.count(for: "/v1/feed") == 200, "Test 1 Failed: Expected 200 hits under contention")
    assert(metrics.count(for: "/v1/missing") == 0, "Test 2 Failed: Missing key should return 0")
    print("  ✅ Challenge Passed: Synchronization.Mutex contention test verified")
}
await runTests()""",
            "hints": [
                "Declare: private let storage = Mutex<[String: Int]>([:])",
                "In recordHit: storage.withLock { $0[endpoint, default: 0] += 1 }",
                "In count: storage.withLock { $0[endpoint, default: 0] }"
            ],
            "solution_code": """import Foundation
import Synchronization

final class ThreadSafeMetrics: Sendable {
    private let storage = Mutex<[String: Int]>([:])

    func recordHit(for endpoint: String) {
        storage.withLock { state in
            state[endpoint, default: 0] += 1
        }
    }

    func count(for endpoint: String) -> Int {
        storage.withLock { state in
            state[endpoint, default: 0]
        }
    }
}

func runTests() async {
    print("🧪 Running Lesson 8.2 Verification Tests...")
    let metrics = ThreadSafeMetrics()
    await withTaskGroup(of: Void.self) { group in
        for _ in 0..<200 {
            group.addTask {
                metrics.recordHit(for: "/v1/feed")
            }
        }
    }
    assert(metrics.count(for: "/v1/feed") == 200)
    assert(metrics.count(for: "/v1/missing") == 0)
    print("  ✅ All tests passed!")
}
await runTests()""",
            "solution_explanation": "<code>Synchronization.Mutex</code> is itself <code>~Copyable</code> and <code>Sendable</code>, allowing <code>ThreadSafeMetrics</code> to conform to <code>Sendable</code> cleanly while guaranteeing zero data races across 200 concurrent tasks without actor-hopping overhead."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0461: Run nonisolated async functions on the caller's actor", "url": "https://github.com/swiftlang/swift-evolution/blob/main/proposals/0461-async-nonsending-by-default.md", "desc": "Canonical Swift 6.2 proposal defining nonisolated(nonsending) and @concurrent."},
            {"title": "Apple Developer: Synchronization Framework (Mutex)", "url": "https://developer.apple.com/documentation/synchronization/mutex", "desc": "Official Apple reference for Mutex and Atomic primitives."}
        ],
        "coaching_prompt": "Notice how clean Synchronization.Mutex is compared to DispatchQueue barriers! Next up is Lesson 8.3: Zero-Copy & Noncopyable Swift (~Copyable, InlineArray, Span)."
    },

    # 0051: Lesson 8.3
    {
        "id": "0051",
        "slug": "0051-noncopyable-types-inlinearray-and-span",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.3",
        "title": "Zero-Copy & Noncopyable Swift: ~Copyable, InlineArray & Span",
        "subtitle": "Unique ownership with ~Copyable (consuming/borrowing/deinit), fixed-size stack InlineArray, and lifetime-safe Span<T> views.",
        "prev_url": "0050-swift62-approachable-concurrency-and-mutex.html",
        "next_url": "0052-tca-and-modern-state-architectures.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android/Kotlin, deterministic resource closing relies on <code>use { ... }</code> (<code>AutoCloseable</code>), and avoiding GC churn on tight buffers requires off-heap <code>ByteBuffer</code>s.</p>
        <p>In Swift 6.x, <strong>noncopyable structs (<code>~Copyable</code>)</strong> give you Rust-like unique ownership on stack-allocated value types—including deterministic <code>deinit</code> without ARC heap overhead! Paired with <strong><code>InlineArray&lt;N, T&gt;</code></strong> and <strong><code>Span&lt;T&gt;</code></strong>, you can process telemetry frames with zero heap allocations and compile-time lifetime safety.</p>
        """,
        "core_sections_html": """
        <h3>1. Noncopyable Resource Guards (~Copyable)</h3>
        <pre><code>struct FileHandleGuard: ~Copyable {
    let fd: Int32
    borrowing func inspectDescriptor() -> Int32 { fd }
    consuming func closeExplicitly() {
        // Explicit consumption suppresses deinit via `discard self`
        discard self
    }
    deinit { /* Deterministic cleanup when scope ends! */ }
}</code></pre>

        <h3>2. Stack InlineArray &amp; Non-Escaping Span&lt;T&gt;</h3>
        <pre><code>func sumSamples(_ span: Span&lt;Int&gt;) -> Int {
    var total = 0
    for i in span.indices { total += span[i] }
    return total
}

let frame: InlineArray&lt;4, Int&gt; = [12, 18, 25, 5] // Zero heap allocation!
let total = sumSamples(frame.span) // Lifetime-bound zero-copy view</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Escaping a Lifetime-Dependent <code>Span</code>:</strong> Because <code>Span&lt;T&gt;</code> does not own memory or increment ARC counts, storing <code>let s = arr.span</code> beyond the borrowed scope triggers a compile-time lifetime error (<code>lifetime-dependent value escapes its scope</code>). Always pass <code>.span</code> directly into functions that consume <code>Span&lt;T&gt;</code> synchronously.</p>
        """,
        "quizzes": [
            {
                "question": "What unique capability does marking a Swift `struct` as `~Copyable` unlock?",
                "correct_idx": 0,
                "options": [
                    {"text": "Deterministic deinit on a value type.", "reason": "Option A is correct: normal structs cannot define deinit because copies exist; ~Copyable guarantees a single owner so deinit runs deterministically."},
                    {"text": "Automatic bridging to Objective-C classes.", "reason": "Option B is wrong because ~Copyable structs cannot bridge to Objective-C."},
                    {"text": "Implicit multi-copy sharing via ARC.", "reason": "Option C is wrong because ~Copyable explicitly forbids copying and avoids ARC."},
                    {"text": "Bypassing Swift 6 Sendable checks.", "reason": "Option D is wrong because ~Copyable types still obey Sendable isolation rules."}
                ]
            },
            {
                "question": "Why does `InlineArray<4, Int>` outperform `[Int]` inside tight audio or graphics loops?",
                "correct_idx": 2,
                "options": [
                    {"text": "It moves execution onto the GPU.", "reason": "Option A is wrong because InlineArray is a CPU memory layout construct."},
                    {"text": "It uses Objective-C NSArray caching.", "reason": "Option B is wrong because InlineArray is pure Swift with zero Obj-C involvement."},
                    {"text": "It stores elements inline without heap allocation.", "reason": "Option C is correct: standard Array allocates a reference-counted heap buffer, whereas InlineArray stores fixed elements inline on the stack."},
                    {"text": "It disables bounds checking at runtime.", "reason": "Option D is wrong because InlineArray retains Swift memory and bounds safety."}
                ]
            }
        ],
        "exercise": {
            "title": "Noncopyable File Descriptor Guard & Zero-Copy Span Checksum",
            "instructions": "1) Implement <code>FileDescriptorGuard.transferOwnership()</code> as a <code>consuming</code> method that returns <code>fd</code> and calls <code>discard self</code> so <code>deinit</code> does not close the transferred descriptor.<br>2) Implement <code>computeChecksum(_ span: Span&lt;Int&gt;)</code> summing elements over an <code>InlineArray</code>'s <code>.span</code>. Run tests.",
            "starter_code": """import Foundation

nonisolated(unsafe) var closedDescriptors: [Int32] = []

struct FileDescriptorGuard: ~Copyable {
    let fd: Int32

    init(fd: Int32) {
        self.fd = fd
    }

    // TODO: Implement consuming func transferOwnership() -> Int32
    // Return fd and call discard self so deinit does NOT fire
    consuming func transferOwnership() -> Int32 {
        fatalError("TODO: Implement transferOwnership")
    }

    deinit {
        closedDescriptors.append(fd)
    }
}

// TODO: Implement computeChecksum(_ span: Span<Int>) -> Int without allocating an Array
func computeChecksum(_ span: Span<Int>) -> Int {
    fatalError("TODO: Implement computeChecksum")
}

func runTests() {
    print("🧪 Running Lesson 8.3 Verification Tests...")
    closedDescriptors.removeAll()
    var transferredFD: Int32 = -1
    do {
        let g1 = FileDescriptorGuard(fd: 101)
        transferredFD = g1.transferOwnership()
        let _ = FileDescriptorGuard(fd: 202) // Dropped at scope exit -> deinit closes it
    }
    assert(transferredFD == 101, "Test 1 Failed: Expected fd 101 returned")
    assert(closedDescriptors == [202], "Test 2 Failed: Only un-transferred fd 202 should close in deinit")

    let samples: InlineArray<4, Int> = [10, 20, 30, 40]
    assert(computeChecksum(samples.span) == 100, "Test 3 Failed: Expected Span sum of 100")
    print("  ✅ Challenge Passed: ~Copyable ownership and InlineArray/Span verified")
}
runTests()""",
            "hints": [
                "Inside transferOwnership(): let raw = fd; discard self; return raw.",
                "In computeChecksum: iterate with `for i in span.indices { total += span[i] }`.",
                "Because fd (Int32) is trivially destroyed, discard self cleanly suppresses deinit."
            ],
            "solution_code": """import Foundation

nonisolated(unsafe) var closedDescriptors: [Int32] = []

struct FileDescriptorGuard: ~Copyable {
    let fd: Int32

    init(fd: Int32) {
        self.fd = fd
    }

    consuming func transferOwnership() -> Int32 {
        let raw = fd
        discard self
        return raw
    }

    deinit {
        closedDescriptors.append(fd)
    }
}

func computeChecksum(_ span: Span<Int>) -> Int {
    var total = 0
    for i in span.indices {
        total += span[i]
    }
    return total
}

func runTests() {
    print("🧪 Running Lesson 8.3 Verification Tests...")
    closedDescriptors.removeAll()
    var transferredFD: Int32 = -1
    do {
        let g1 = FileDescriptorGuard(fd: 101)
        transferredFD = g1.transferOwnership()
        let _ = FileDescriptorGuard(fd: 202)
    }
    assert(transferredFD == 101)
    assert(closedDescriptors == [202])

    let samples: InlineArray<4, Int> = [10, 20, 30, 40]
    assert(computeChecksum(samples.span) == 100)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "<code>~Copyable</code> turns resource ownership into a compiler-enforced invariant: dropping <code>g2</code> runs <code>deinit</code> deterministically, while <code>transferOwnership()</code> consumes <code>self</code> and calls <code>discard self</code> (supported when stored properties are trivial like <code>Int32</code>) to prevent double-closing. <code>InlineArray</code> + <code>Span</code> achieve zero heap allocation."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0390: Noncopyable Structs and Enums", "url": "https://github.com/swiftlang/swift-evolution/blob/main/proposals/0390-noncopyable-structs-and-enums.md", "desc": "Ownership semantics, consuming/borrowing, and deinit on value types."},
            {"title": "Swift Evolution SE-0447: Span — Safe Access to Contiguous Storage", "url": "https://github.com/swiftlang/swift-evolution/blob/main/proposals/0447-span-access-contiguous-storage.md", "desc": "Lifetime-safe zero-copy buffer views in Swift 6.2+."}
        ],
        "coaching_prompt": "That's Rust-grade ownership and zero-copy performance inside pure Swift! Ready for Lesson 8.4: The Composable Architecture (TCA) vs. @Observable UDF?"
    },

    # 0052: Lesson 8.4
    {
        "id": "0052",
        "slug": "0052-tca-and-modern-state-architectures",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.4",
        "title": "The Composable Architecture (TCA) vs. @Observable UDF",
        "subtitle": "Point-Free's @Reducer, @ObservableState, Effect cancellation, DependencyKey, TestStore exhaustive assertions, and senior architecture trade-offs.",
        "prev_url": "0051-noncopyable-types-inlinearray-and-span.html",
        "next_url": "0053-filesystem-data-protection-and-sqlite.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, teams debate Jetpack ViewModel + StateFlow (MVVM) vs. structured MVI frameworks (Orbit, Slack Circuit, Mavericks).</p>
        <p>In iOS, Senior technical interviews almost always test the trade-off between lightweight <strong><code>@Observable</code> MVVM/UDF</strong> and Point-Free's <strong>The Composable Architecture (TCA)</strong>—especially how TCA models side effects as values (<code>Effect</code>), tree-based navigation (<code>@Presents</code>), and exhaustive async testing (<code>TestStore</code>).</p>
        """,
        "core_sections_html": """
        <h3>1. Anatomy of a Modern TCA Feature</h3>
        <pre><code>// Point-Free TCA (ComposableArchitecture)
@Reducer
struct FeedFeature {
    @ObservableState
    struct State: Equatable {
        var items: [String] = []
        var isLoading = false
    }
    enum Action { case refreshTapped, feedLoaded([String]) }
    @Dependency(\\.feedClient) var feedClient

    var body: some ReducerOf&lt;Self&gt; {
        Reduce { state, action in
            switch action {
            case .refreshTapped:
                state.isLoading = true
                return .run { send in
                    let items = try await feedClient.fetch()
                    await send(.feedLoaded(items))
                }.cancellable(id: CancelID.refresh, cancelInFlight: true)
            case .feedLoaded(let items):
                state.isLoading = false
                state.items = items
                return .none
            }
        }
    }
}</code></pre>

        <h3>2. Senior Trade-Off Matrix: @Observable MVVM vs. TCA</h3>
        <ul>
          <li><strong>Choose <code>@Observable</code> UDF when:</strong> You want zero third-party macro coupling, fast Xcode compile times, and straightforward ViewModel unit tests.</li>
          <li><strong>Choose TCA when:</strong> You need deterministic parent-child composition, built-in effect cancellation, and <code>TestStore</code> guarantees that <em>every</em> emitted action and state mutation is explicitly asserted.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Mutating State Inside Async Effects or Skipping Exhaustivity:</strong> In TCA, mutating state outside the synchronous <code>Reduce</code> closure causes race bugs—effects must send actions back to the reducer. Equally, adopting TCA without enforcing <code>TestStore</code> exhaustive assertions pays all the boilerplate cost with none of the correctness guarantees.</p>
        """,
        "quizzes": [
            {
                "question": "What does TCA's `TestStore` enforce by default that standard ViewModel tests often miss?",
                "correct_idx": 1,
                "options": [
                    {"text": "Pixel-perfect SwiftUI snapshot rendering.", "reason": "Option A is wrong because TestStore verifies state and actions, not UI pixels."},
                    {"text": "Every state change and received action.", "reason": "Option B is correct: TestStore fails the test if any state mutation or async effect action goes unasserted."},
                    {"text": "Automatic App Store screenshot generation.", "reason": "Option C is wrong because screenshots are handled by Fastlane or UI tests."},
                    {"text": "Zero-copy stack memory allocation.", "reason": "Option D is wrong because TestStore is an architectural testing harness."}
                ]
            },
            {
                "question": "Why might a Senior iOS lead reject TCA for a small 3-screen utility app?",
                "correct_idx": 3,
                "options": [
                    {"text": "TCA does not compile under Swift 6.", "reason": "Option A is wrong because modern TCA fully supports Swift 6 strict concurrency."},
                    {"text": "TCA cannot interoperate with SwiftUI views.", "reason": "Option B is wrong because TCA is built primarily for SwiftUI."},
                    {"text": "TCA forbids unit testing async calls.", "reason": "Option C is wrong because async effect testing is TCA's flagship strength."},
                    {"text": "Macro build cost and steep onboarding.", "reason": "Option D is correct: heavy macro expansion, third-party coupling, and steep learning curve can outweigh benefits on small teams."}
                ]
            }
        ],
        "exercise": {
            "title": "Build a Deterministic Reducer + Exhaustive TestStore Engine",
            "instructions": "Implement a lightweight pure-Swift <code>Reducer</code> and <code>MiniTestStore</code> that records effects and <strong>fails assertions</strong> if any unasserted effect action remains pending at the end of a test. Run tests.",
            "starter_code": """import Foundation

struct CounterState: Equatable {
    var count: Int = 0
    var isSyncing: Bool = false
}

enum CounterAction: Equatable {
    case increment
    case syncRequested
    case syncCompleted(Int)
}

enum MiniEffect {
    case none
    case emit(CounterAction)
}

func reduce(state: inout CounterState, action: CounterAction) -> MiniEffect {
    // TODO:
    // .increment -> state.count += 1, return .none
    // .syncRequested -> state.isSyncing = true, return .emit(.syncCompleted(state.count))
    // .syncCompleted(let serverVal) -> state.isSyncing = false, state.count = serverVal, return .none
    fatalError("TODO: Implement reduce")
}

final class MiniTestStore {
    private(set) var state: CounterState
    private var pendingActions: [CounterAction] = []

    init(initialState: CounterState) { self.state = initialState }

    func send(_ action: CounterAction, assertState: (inout CounterState) -> Void) {
        var expected = state
        assertState(&expected)
        let effect = reduce(state: &state, action: action)
        assert(state == expected, "State mismatch after send(\\(action))")
        if case .emit(let next) = effect {
            pendingActions.append(next)
        }
    }

    func receive(_ expectedAction: CounterAction, assertState: (inout CounterState) -> Void) {
        // TODO: Pop first pending action, assert it equals expectedAction, reduce it, and assert state matches expected
        fatalError("TODO: Implement receive")
    }

    func finish() {
        assert(pendingActions.isEmpty, "Exhaustivity failure: unreceived actions \\(pendingActions)")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.4 Verification Tests...")
    let store = MiniTestStore(initialState: CounterState())
    store.send(.increment) { $0.count = 1 }
    store.send(.syncRequested) { $0.isSyncing = true }
    store.receive(.syncCompleted(1)) { $0.isSyncing = false; $0.count = 1 }
    store.finish()
    print("  ✅ Challenge Passed: Exhaustive Reducer & TestStore loop verified")
}
runTests()""",
            "hints": [
                "Use a switch statement inside reduce(state:action:) and return MiniEffect.",
                "In receive: assert(!pendingActions.isEmpty); let actual = pendingActions.removeFirst(); assert(actual == expectedAction).",
                "Then run reduce(state: &state, action: actual) and compare against expected."
            ],
            "solution_code": """import Foundation

struct CounterState: Equatable {
    var count: Int = 0
    var isSyncing: Bool = false
}

enum CounterAction: Equatable {
    case increment
    case syncRequested
    case syncCompleted(Int)
}

enum MiniEffect {
    case none
    case emit(CounterAction)
}

func reduce(state: inout CounterState, action: CounterAction) -> MiniEffect {
    switch action {
    case .increment:
        state.count += 1
        return .none
    case .syncRequested:
        state.isSyncing = true
        return .emit(.syncCompleted(state.count))
    case .syncCompleted(let serverVal):
        state.isSyncing = false
        state.count = serverVal
        return .none
    }
}

final class MiniTestStore {
    private(set) var state: CounterState
    private var pendingActions: [CounterAction] = []

    init(initialState: CounterState) { self.state = initialState }

    func send(_ action: CounterAction, assertState: (inout CounterState) -> Void) {
        var expected = state
        assertState(&expected)
        let effect = reduce(state: &state, action: action)
        assert(state == expected, "State mismatch")
        if case .emit(let next) = effect {
            pendingActions.append(next)
        }
    }

    func receive(_ expectedAction: CounterAction, assertState: (inout CounterState) -> Void) {
        assert(!pendingActions.isEmpty, "No pending action to receive")
        let actual = pendingActions.removeFirst()
        assert(actual == expectedAction, "Action mismatch")
        var expected = state
        assertState(&expected)
        let effect = reduce(state: &state, action: actual)
        assert(state == expected, "State mismatch after receive")
        if case .emit(let next) = effect {
            pendingActions.append(next)
        }
    }

    func finish() {
        assert(pendingActions.isEmpty, "Exhaustivity failure")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.4 Verification Tests...")
    let store = MiniTestStore(initialState: CounterState())
    store.send(.increment) { $0.count = 1 }
    store.send(.syncRequested) { $0.isSyncing = true }
    store.receive(.syncCompleted(1)) { $0.isSyncing = false; $0.count = 1 }
    store.finish()
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "This mirrors TCA's core invariant: state mutations happen exclusively in the pure reducer, side effects return as values, and <code>MiniTestStore</code> enforces exhaustive assertions on every state delta and emitted action."
        },
        "primary_sources": [
            {"title": "Point-Free: The Composable Architecture", "url": "https://github.com/pointfreeco/swift-composable-architecture", "desc": "Canonical repository and documentation for @Reducer, Dependencies, and TestStore."},
            {"title": "Point-Free: Swift Dependencies", "url": "https://github.com/pointfreeco/swift-dependencies", "desc": "Compile-time and runtime test-scoped dependency injection library."}
        ],
        "coaching_prompt": "You now understand both @Observable UDF and TCA at an architectural review level. Next is Lesson 8.5: File System Sandboxing, Data Protection & Direct SQLite / GRDB."
    },

    # 0053: Lesson 8.5
    {
        "id": "0053",
        "slug": "0053-filesystem-data-protection-and-sqlite",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.5",
        "title": "File System Sandboxing, Data Protection & Direct SQLite / GRDB",
        "subtitle": "Documents vs Application Support vs Caches, iCloud backup exclusion, NSFileProtectionCompleteUnlessOpen, and SQLite/GRDB vs SwiftData.",
        "prev_url": "0052-tca-and-modern-state-architectures.html",
        "next_url": "0054-xcframeworks-and-binary-distribution.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, you distinguish <code>filesDir</code> from <code>cacheDir</code> and choose between Room ORM and SQLDelight for SQL control.</p>
        <p>In iOS, misplacing downloaded media inside <code>Documents/</code> triggers <strong>App Store rejection</strong> for bloating iCloud backups! Senior iOS engineers master sandbox directory rules, hardware-backed <code>FileProtectionType</code> classes, and know when to step below SwiftData into direct <strong>SQLite / GRDB</strong> (or Point-Free's <code>SQLiteData</code>) for high-throughput background writes.</p>
        """,
        "core_sections_html": """
        <h3>1. The iOS Sandbox Directory Contract</h3>
        <ul>
          <li><strong><code>Documents/</code>:</strong> User-created files only (visible in Files app if enabled). Backed up to iCloud automatically.</li>
          <li><strong><code>Library/Application Support/</code>:</strong> App-managed databases &amp; state. Backed up to iCloud (set <code>isExcludedFromBackup = true</code> for re-downloadable assets!).</li>
          <li><strong><code>Library/Caches/</code>:</strong> Ephemeral cache; iOS purges it under storage pressure (never backed up).</li>
          <li><strong><code>tmp/</code>:</strong> Scratch files; purged on termination/reboot.</li>
        </ul>

        <h3>2. Hardware File Protection &amp; SQLite WAL Mode</h3>
        <pre><code>var resourceValues = URLResourceValues()
resourceValues.isExcludedFromBackup = true
try cacheURL.setResourceValues(resourceValues)

try data.write(to: secureURL, options: [.atomic, .completeFileProtectionUnlessOpen])</code></pre>
        <p>Use <code>.completeFileProtectionUnlessOpen</code> for background sync SQLite/GRDB databases: files already open stay readable during background wakeups while locked, whereas new opens require device unlock.</p>
        """,
        "failure_mode_html": """
        <p><strong>Writing Databases to <code>Documents/</code> or Using <code>.completeFileProtection</code> for Background Sync:</strong> Putting 500MB of synced cache in <code>Documents</code> fails App Store review. Worse, encrypting a background-synced SQLite file with <code>.complete</code> causes <code>SQLITE_CANTOPEN</code> errors whenever <code>BGAppRefreshTask</code> wakes the app while the user's iPhone is locked in their pocket.</p>
        """,
        "quizzes": [
            {
                "question": "Which directory and flag combination is correct for a large, re-downloadable offline media database?",
                "correct_idx": 0,
                "options": [
                    {"text": "Application Support with isExcludedFromBackup.", "reason": "Option A is correct: Application Support keeps data safe from sudden cache eviction while excluding it from iCloud backup bloat."},
                    {"text": "Documents directory with default iCloud sync.", "reason": "Option B is wrong because large re-downloadable data in Documents violates iOS Storage Guidelines and risks App Store rejection."},
                    {"text": "tmp directory with atomic write flags.", "reason": "Option C is wrong because tmp is wiped by the OS between launches."},
                    {"text": "Main Bundle resources directory.", "reason": "Option D is wrong because the app bundle is signed and strictly read-only at runtime."}
                ]
            },
            {
                "question": "Why do Senior teams often choose GRDB/SQLite over SwiftData for complex offline-first sync?",
                "correct_idx": 2,
                "options": [
                    {"text": "SwiftData cannot run on ARM64 iPhones.", "reason": "Option A is wrong because SwiftData is native to all modern Apple Silicon devices."},
                    {"text": "SQLite bypasses iOS sandbox permissions.", "reason": "Option B is wrong because SQLite still operates strictly inside the app sandbox."},
                    {"text": "Explicit SQL, WAL concurrency, and predictable migrations.", "reason": "Option C is correct: direct SQLite/GRDB gives deterministic WAL reader/writer concurrency, FTS5 full-text search, and zero @Model macro surprises."},
                    {"text": "GRDB automatically compiles into Android Room.", "reason": "Option D is wrong because GRDB is a native Swift/Apple SQLite toolkit."}
                ]
            }
        ],
        "exercise": {
            "title": "Sandbox Path Router & Atomic Protected File Writer",
            "instructions": "Implement <code>SandboxRouter.destinationURL(base:kind:filename:)</code> routing <code>.userDocument</code> to <code>Documents</code>, <code>.database</code> to <code>Application Support</code>, and <code>.ephemeralCache</code> to <code>Caches</code>, plus <code>writeProtected</code> using atomic writes. Run tests.",
            "starter_code": """import Foundation

enum StorageKind {
    case userDocument
    case database
    case ephemeralCache
}

enum SandboxRouter {
    // TODO: Return base/<Directory>/<filename>
    // .userDocument -> "Documents", .database -> "ApplicationSupport", .ephemeralCache -> "Caches"
    static func destinationURL(base: URL, kind: StorageKind, filename: String) -> URL {
        fatalError("TODO: Implement destinationURL")
    }

    // TODO: Write data atomically with .completeFileProtectionUnlessOpen after creating parent directories
    static func writeProtected(_ data: Data, to url: URL) throws {
        fatalError("TODO: Implement writeProtected")
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.5 Verification Tests...")
    let tempBase = FileManager.default.temporaryDirectory.appendingPathComponent("pulse_sandbox_\\(UUID().uuidString)")
    defer { try? FileManager.default.removeItem(at: tempBase) }

    let dbURL = SandboxRouter.destinationURL(base: tempBase, kind: .database, filename: "sync.sqlite")
    assert(dbURL.path.contains("ApplicationSupport/sync.sqlite"), "Test 1 Failed: Wrong database path")

    let docURL = SandboxRouter.destinationURL(base: tempBase, kind: .userDocument, filename: "export.pdf")
    assert(docURL.path.contains("Documents/export.pdf"), "Test 2 Failed: Wrong documents path")

    let payload = Data("WAL_HEADER_V1".utf8)
    try SandboxRouter.writeProtected(payload, to: dbURL)
    let readBack = try Data(contentsOf: dbURL)
    assert(readBack == payload, "Test 3 Failed: Written data mismatch")
    print("  ✅ Challenge Passed: Sandbox routing and atomic protected write verified")
}
try runTests()""",
            "hints": [
                "Switch on kind to map to \"Documents\", \"ApplicationSupport\", or \"Caches\" using appendingPathComponent.",
                "In writeProtected: call try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true).",
                "Then call try data.write(to: url, options: [.atomic, .completeFileProtectionUnlessOpen])."
            ],
            "solution_code": """import Foundation

enum StorageKind {
    case userDocument
    case database
    case ephemeralCache
}

enum SandboxRouter {
    static func destinationURL(base: URL, kind: StorageKind, filename: String) -> URL {
        let folder: String
        switch kind {
        case .userDocument: folder = "Documents"
        case .database: folder = "ApplicationSupport"
        case .ephemeralCache: folder = "Caches"
        }
        return base.appendingPathComponent(folder).appendingPathComponent(filename)
    }

    static func writeProtected(_ data: Data, to url: URL) throws {
        try FileManager.default.createDirectory(
            at: url.deletingLastPathComponent(),
            withIntermediateDirectories: true
        )
        try data.write(to: url, options: [.atomic, .completeFileProtectionUnlessOpen])
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.5 Verification Tests...")
    let tempBase = FileManager.default.temporaryDirectory.appendingPathComponent("pulse_sandbox_\\(UUID().uuidString)")
    defer { try? FileManager.default.removeItem(at: tempBase) }

    let dbURL = SandboxRouter.destinationURL(base: tempBase, kind: .database, filename: "sync.sqlite")
    assert(dbURL.path.contains("ApplicationSupport/sync.sqlite"))

    let docURL = SandboxRouter.destinationURL(base: tempBase, kind: .userDocument, filename: "export.pdf")
    assert(docURL.path.contains("Documents/export.pdf"))

    let payload = Data("WAL_HEADER_V1".utf8)
    try SandboxRouter.writeProtected(payload, to: dbURL)
    let readBack = try Data(contentsOf: dbURL)
    assert(readBack == payload)
    print("  ✅ All tests passed!")
}
try runTests()""",
            "solution_explanation": "Creating parent directories before atomic writes prevents <code>ENOENT</code> crashes on fresh installs, and pairing <code>ApplicationSupport</code> with <code>.completeFileProtectionUnlessOpen</code> satisfies both iCloud backup rules and background sync accessibility."
        },
        "primary_sources": [
            {"title": "Apple Developer: File System Basics & Data Protection", "url": "https://developer.apple.com/documentation/uikit/protecting-the-user-s-privacy/encrypting-your-app-s-files", "desc": "Official guide on iOS sandbox folders and NSFileProtection classes."},
            {"title": "GRDB.swift Documentation", "url": "https://github.com/groue/GRDB.swift", "desc": "Industry-standard Swift SQLite toolkit for concurrent WAL reads/writes and migrations."}
        ],
        "coaching_prompt": "File protection and SQLite WAL knowledge saves production apps from silent background failures. Ready for Lesson 8.6: XCFrameworks, Library Evolution & Mergeable Libraries?"
    },

    # 0054: Lesson 8.6
    {
        "id": "0054",
        "slug": "0054-xcframeworks-and-binary-distribution",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.6",
        "title": "XCFrameworks, Library Evolution & Mergeable Libraries",
        "subtitle": "Static vs Dynamic vs Mergeable binaries, BUILD_LIBRARY_FOR_DISTRIBUTION (.swiftinterface), and xcodebuild -create-xcframework.",
        "prev_url": "0053-filesystem-data-protection-and-sqlite.html",
        "next_url": "0055-code-quality-linting-and-test-plans.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, binary SDK distribution ships a single platform-neutral <code>.aar</code> bytecode archive via Maven.</p>
        <p>In iOS, native Mach-O binaries are CPU-architecture and platform-slice specific (iOS <code>arm64</code> device vs <code>arm64</code>/<code>x86_64</code> simulator). Senior iOS engineers package closed-source or prebuilt modules as <strong><code>XCFramework</code></strong> bundles with <strong>Library Evolution</strong> enabled, and leverage <strong>Mergeable Libraries</strong> to get fast Debug incremental builds with zero Release <code>dyld4</code> launch overhead.</p>
        """,
        "core_sections_html": """
        <h3>1. Building an ABI-Stable XCFramework</h3>
        <pre><code># 1. Archive with BUILD_LIBRARY_FOR_DISTRIBUTION=YES (generates .swiftinterface!)
xcodebuild archive -scheme PulseSDK -destination "generic/platform=iOS" \\
  -archivePath build/ios.xcarchive SKIP_INSTALL=NO BUILD_LIBRARY_FOR_DISTRIBUTION=YES

xcodebuild archive -scheme PulseSDK -destination "generic/platform=iOS Simulator" \\
  -archivePath build/sim.xcarchive SKIP_INSTALL=NO BUILD_LIBRARY_FOR_DISTRIBUTION=YES

# 2. Bundle into a single XCFramework
xcodebuild -create-xcframework \\
  -framework build/ios.xcarchive/Products/Library/Frameworks/PulseSDK.framework \\
  -framework build/sim.xcarchive/Products/Library/Frameworks/PulseSDK.framework \\
  -output build/PulseSDK.xcframework</code></pre>

        <h3>2. Mergeable Libraries (Xcode 15+ / 2027 Standard)</h3>
        <p>Setting <code>MERGED_BINARY_TYPE = automatic</code> links internal frameworks <strong>dynamically</strong> in Debug (instant incremental rebuilds) and <strong>merges them statically</strong> into the main app binary in Release (eliminating <code>dyld4</code> rebase/bind launch cost).</p>
        """,
        "failure_mode_html": """
        <p><strong>Shipping Binary Swift Frameworks Without <code>BUILD_LIBRARY_FOR_DISTRIBUTION=YES</code>:</strong> Without Library Evolution, Xcode emits opaque <code>.swiftmodule</code> files tied to the exact compiler version—meaning your SDK breaks the moment a client upgrades Xcode! Always enable Library Evolution and mark public enums <code>@frozen</code> only when binary layout stability is guaranteed.</p>
        """,
        "quizzes": [
            {
                "question": "What file inside an XCFramework allows clients on newer Swift compilers to import a precompiled binary SDK?",
                "correct_idx": 1,
                "options": [
                    {"text": "A compiled .dSYM debug symbol table.", "reason": "Option A is wrong because dSYMs map stack addresses for crash symbolication, not module interfaces."},
                    {"text": "A textual .swiftinterface module file.", "reason": "Option B is correct: BUILD_LIBRARY_FOR_DISTRIBUTION emits textual .swiftinterface files enabling module stability across Swift compiler versions."},
                    {"text": "A dynamic Info.plist manifest.", "reason": "Option C is wrong because Info.plist describes bundle metadata, not Swift ABI interfaces."},
                    {"text": "A CocoaPods podspec lockfile.", "reason": "Option D is wrong because XCFrameworks are native Apple binary bundles independent of CocoaPods."}
                ]
            },
            {
                "question": "How do Mergeable Libraries solve the Debug vs. Release linkage trade-off?",
                "correct_idx": 0,
                "options": [
                    {"text": "Dynamic in Debug, static in Release.", "reason": "Option A is correct: Debug stays fast with incremental dynamic linking, while Release merges modules into the app Mach-O to slash dyld4 launch time."},
                    {"text": "Static in Debug, dynamic in Release.", "reason": "Option B is backwards and would slow both incremental builds and launch times."},
                    {"text": "Converts Swift code into JVM bytecode.", "reason": "Option C is wrong because iOS targets native ARM64 Mach-O binaries."},
                    {"text": "Eliminates code signing for Release.", "reason": "Option D is wrong because all App Store binaries must be signed."}
                ]
            }
        ],
        "exercise": {
            "title": "XCFramework & SPM BinaryTarget Integrity Validator",
            "instructions": "Implement <code>BinaryPackageValidator.validate</code> verifying that an SPM <code>binaryTarget</code> has both <code>ios-arm64</code> and <code>ios-arm64-simulator</code> slices, a textual <code>.swiftinterface</code>, and a matching SHA-256 checksum. Run tests.",
            "starter_code": """import Foundation

struct XCFrameworkManifest {
    let slices: Set<String>
    let hasSwiftInterface: Bool
    let sha256Checksum: String
}

enum BinaryPackageValidator {
    // TODO: Return true iff slices contains both "ios-arm64" and "ios-arm64-simulator",
    // hasSwiftInterface is true, and sha256Checksum matches expectedChecksum
    static func validate(_ manifest: XCFrameworkManifest, expectedChecksum: String) -> Bool {
        fatalError("TODO: Implement validate")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.6 Verification Tests...")
    let valid = XCFrameworkManifest(
        slices: ["ios-arm64", "ios-arm64-simulator"],
        hasSwiftInterface: true,
        sha256Checksum: "abc123"
    )
    let missingSim = XCFrameworkManifest(
        slices: ["ios-arm64"],
        hasSwiftInterface: true,
        sha256Checksum: "abc123"
    )
    let noEvolution = XCFrameworkManifest(
        slices: ["ios-arm64", "ios-arm64-simulator"],
        hasSwiftInterface: false,
        sha256Checksum: "abc123"
    )
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "abc123") == true, "Test 1 Failed")
    assert(BinaryPackageValidator.validate(missingSim, expectedChecksum: "abc123") == false, "Test 2 Failed")
    assert(BinaryPackageValidator.validate(noEvolution, expectedChecksum: "abc123") == false, "Test 3 Failed")
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "tampered") == false, "Test 4 Failed")
    print("  ✅ Challenge Passed: XCFramework module stability & slice integrity verified")
}
runTests()""",
            "hints": [
                "Check manifest.slices.contains(\"ios-arm64\") && manifest.slices.contains(\"ios-arm64-simulator\").",
                "Also require manifest.hasSwiftInterface == true and manifest.sha256Checksum == expectedChecksum."
            ],
            "solution_code": """import Foundation

struct XCFrameworkManifest {
    let slices: Set<String>
    let hasSwiftInterface: Bool
    let sha256Checksum: String
}

enum BinaryPackageValidator {
    static func validate(_ manifest: XCFrameworkManifest, expectedChecksum: String) -> Bool {
        manifest.slices.contains("ios-arm64")
        && manifest.slices.contains("ios-arm64-simulator")
        && manifest.hasSwiftInterface
        && manifest.sha256Checksum == expectedChecksum
    }
}

func runTests() {
    print("🧪 Running Lesson 8.6 Verification Tests...")
    let valid = XCFrameworkManifest(slices: ["ios-arm64", "ios-arm64-simulator"], hasSwiftInterface: true, sha256Checksum: "abc123")
    let missingSim = XCFrameworkManifest(slices: ["ios-arm64"], hasSwiftInterface: true, sha256Checksum: "abc123")
    let noEvolution = XCFrameworkManifest(slices: ["ios-arm64", "ios-arm64-simulator"], hasSwiftInterface: false, sha256Checksum: "abc123")
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "abc123") == true)
    assert(BinaryPackageValidator.validate(missingSim, expectedChecksum: "abc123") == false)
    assert(BinaryPackageValidator.validate(noEvolution, expectedChecksum: "abc123") == false)
    assert(BinaryPackageValidator.validate(valid, expectedChecksum: "tampered") == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Validating simulator+device slices, module stability (<code>.swiftinterface</code>), and SPM SHA-256 checksums prevents broken binary SDK releases and supply-chain tampering."
        },
        "primary_sources": [
            {"title": "Apple Developer: Creating a Multiplatform Binary Framework Bundle", "url": "https://developer.apple.com/documentation/xcode/creating-a-multi-platform-binary-framework-bundle", "desc": "Official XCFramework and BUILD_LIBRARY_FOR_DISTRIBUTION guide."},
            {"title": "WWDC23: Meet Mergeable Libraries", "url": "https://developer.apple.com/videos/play/wwdc2023/10268/", "desc": "Apple engineering walkthrough of Debug dynamic + Release static merged binaries."}
        ],
        "coaching_prompt": "Binary distribution and Mergeable Libraries are classic Staff/Senior architecture topics. Ready for Lesson 8.7: Code Quality Gates (swift-format, SwiftLint & Xcode Test Plans)?"
    },

    # 0055: Lesson 8.7
    {
        "id": "0055",
        "slug": "0055-code-quality-linting-and-test-plans",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.7",
        "title": "Code Quality Gates: swift-format, SwiftLint & Xcode Test Plans",
        "subtitle": "Toolchain swift-format, SwiftLint SPM Build Tool Plugins, and .xctestplan matrices (TSan, randomization, and localization).",
        "prev_url": "0054-xcframeworks-and-binary-distribution.html",
        "next_url": "0056-liquid-glass-animations-and-localization.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, teams enforce code hygiene via Gradle tasks (<code>ktlint</code>, <code>detekt</code>, Android Lint) and Gradle managed devices/test variants.</p>
        <p>In iOS 2027, Apple ships <strong><code>swift-format</code></strong> directly inside the Swift 6 toolchain, paired with <strong>SwiftLint</strong> for semantic anti-pattern rules (e.g., banning <code>force_unwrapping</code>) and <strong>Xcode Test Plans (<code>.xctestplan</code>)</strong> to run randomized, parallel, Thread-Sanitizer, and RTL localization matrices in CI.</p>
        """,
        "core_sections_html": """
        <h3>1. Official Toolchain Formatting &amp; Semantic Linting</h3>
        <pre><code># Built directly into Swift 6+ toolchain:
swift format lint --strict Sources/ Tests/
swift format --in-place Sources/

# .swiftlint.yml Senior Guardrails:
opt_in_rules:
  - force_unwrapping
  - implicitly_unwrapped_optional
  - weak_delegate</code></pre>

        <h3>2. Xcode Test Plans (.xctestplan)</h3>
        <p>Instead of duplicating Xcode schemes, a single <code>PulseFlow.xctestplan</code> defines multiple configurations:</p>
        <ul>
          <li><strong>Default PR Matrix:</strong> Parallel execution + <code>executionOrder: random</code> (catches hidden shared global state!).</li>
          <li><strong>Sanitizer Matrix:</strong> <code>Thread Sanitizer (TSan)</code> + <code>Address Sanitizer (ASan)</code> on nightly builds.</li>
          <li><strong>Localization Matrix:</strong> Pseudo-language / Arabic RTL runs to catch truncated SwiftUI layouts automatically.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Running Tests Deterministically Without Randomization or TSan:</strong> Tests that only pass in alphabetical order hide global singleton mutation. Equally, skipping Thread Sanitizer on mixed Obj-C/C/Swift codebases lets subtle data races slip past Swift 6's checked boundaries.</p>
        """,
        "quizzes": [
            {
                "question": "Why should `.xctestplan` enable `executionOrder: random` for unit test configurations?",
                "correct_idx": 0,
                "options": [
                    {"text": "Exposes hidden shared mutable state bugs.", "reason": "Option A is correct: randomizing test execution order immediately surfaces order-dependent tests mutating shared singletons or static caches."},
                    {"text": "Doubles device battery life in Xcode.", "reason": "Option B is wrong because test order does not alter device battery circuitry."},
                    {"text": "Enables automatic App Store publishing.", "reason": "Option C is wrong because Test Plans govern test execution, not release uploads."},
                    {"text": "Compiles Swift code without ARC.", "reason": "Option D is wrong because ARC cannot be disabled via Test Plans."}
                ]
            },
            {
                "question": "Which tool ships natively inside modern Swift 6 toolchains without needing third-party binaries?",
                "correct_idx": 2,
                "options": [
                    {"text": "Tailor", "reason": "Option A is wrong because Tailor is an abandoned third-party linter."},
                    {"text": "Carthage", "reason": "Option B is wrong because Carthage is a legacy third-party dependency manager."},
                    {"text": "swift-format", "reason": "Option C is correct: Apple integrated swift-format directly into the official Swift 6 toolchain (`swift format`)."},
                    {"text": "CocoaPods", "reason": "Option D is wrong because CocoaPods is a Ruby-based third-party dependency manager."}
                ]
            }
        ],
        "exercise": {
            "title": "CI Quality Gate & TestPlan Policy Evaluator",
            "instructions": "Implement <code>QualityGate.evaluate</code> checking that a CI configuration enforces <code>swiftFormatStrict</code>, bans force-unwrapping in SwiftLint, enables randomized test execution, and includes a Thread Sanitizer pass. Run tests.",
            "starter_code": """import Foundation

struct CIPolicy {
    let swiftFormatStrict: Bool
    let swiftLintRules: Set<String>
    let testPlanRandomized: Bool
    let sanitizers: Set<String>
}

enum QualityGate {
    // TODO: Return true iff swiftFormatStrict is true,
    // swiftLintRules contains "force_unwrapping",
    // testPlanRandomized is true, and sanitizers contains "TSan"
    static func evaluate(_ policy: CIPolicy) -> Bool {
        fatalError("TODO: Implement evaluate")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.7 Verification Tests...")
    let senior = CIPolicy(swiftFormatStrict: true, swiftLintRules: ["force_unwrapping", "weak_delegate"], testPlanRandomized: true, sanitizers: ["TSan"])
    let weak = CIPolicy(swiftFormatStrict: true, swiftLintRules: [], testPlanRandomized: false, sanitizers: [])
    assert(QualityGate.evaluate(senior) == true, "Test 1 Failed")
    assert(QualityGate.evaluate(weak) == false, "Test 2 Failed")
    print("  ✅ Challenge Passed: CI quality gates and TestPlan policy verified")
}
runTests()""",
            "hints": [
                "Verify policy.swiftFormatStrict && policy.testPlanRandomized.",
                "Verify policy.swiftLintRules.contains(\"force_unwrapping\") && policy.sanitizers.contains(\"TSan\")."
            ],
            "solution_code": """import Foundation

struct CIPolicy {
    let swiftFormatStrict: Bool
    let swiftLintRules: Set<String>
    let testPlanRandomized: Bool
    let sanitizers: Set<String>
}

enum QualityGate {
    static func evaluate(_ policy: CIPolicy) -> Bool {
        policy.swiftFormatStrict
        && policy.swiftLintRules.contains("force_unwrapping")
        && policy.testPlanRandomized
        && policy.sanitizers.contains("TSan")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.7 Verification Tests...")
    let senior = CIPolicy(swiftFormatStrict: true, swiftLintRules: ["force_unwrapping", "weak_delegate"], testPlanRandomized: true, sanitizers: ["TSan"])
    let weak = CIPolicy(swiftFormatStrict: true, swiftLintRules: [], testPlanRandomized: false, sanitizers: [])
    assert(QualityGate.evaluate(senior) == true)
    assert(QualityGate.evaluate(weak) == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Combining deterministic toolchain formatting (<code>swift format lint --strict</code>), anti-crash SwiftLint rules, randomized Test Plans, and Thread Sanitizer checks catches concurrency and state bugs before code review."
        },
        "primary_sources": [
            {"title": "Apple swift-format Repository", "url": "https://github.com/swiftlang/swift-format", "desc": "Official Swift formatter and linter integrated into Swift 6 toolchains."},
            {"title": "Apple Developer: Improving Code Assessment by Organizing Tests into Test Plans", "url": "https://developer.apple.com/documentation/xcode/organizing-tests-to-improve-feedback", "desc": "Configuring parallel, randomized, localized, and sanitizer test matrices."}
        ],
        "coaching_prompt": "Automated formatting and randomized Test Plans keep large teams fast and bug-free. Ready for Lesson 8.8: iOS 26 Liquid Glass, Modern Animators & String Catalogs?"
    },

    # 0056: Lesson 8.8
    {
        "id": "0056",
        "slug": "0056-liquid-glass-animations-and-localization",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.8",
        "title": "iOS 26 Liquid Glass, Modern Animators & String Catalogs",
        "subtitle": "Apple's Liquid Glass material rules, PhaseAnimator & KeyframeAnimator, @Animatable, and .xcstrings String Catalogs at scale.",
        "prev_url": "0055-code-quality-linting-and-test-plans.html",
        "next_url": "0057-foundation-models-and-coreml.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, modern UI polish centers on Material 3 Expressive surfaces, Motion specs, and <code>strings.xml</code> + <code>plurals.xml</code>.</p>
        <p>In 2027 iOS (iOS 26+), Apple's visual language centers on <strong>Liquid Glass</strong>—optical, refractive floating navigation/control layers over content—paired with declarative <strong><code>PhaseAnimator</code> / <code>KeyframeAnimator</code></strong> and Xcode <strong>String Catalogs (<code>.xcstrings</code>)</strong> for automatic pluralization, device variations, and RTL support.</p>
        """,
        "core_sections_html": """
        <h3>1. Liquid Glass Material Hierarchy</h3>
        <pre><code>// Apply Liquid Glass to floating controls &amp; chrome—NOT scrolling content rows!
HStack(spacing: 12) {
    Button("Sync", systemImage: "arrow.triangle.2.circlepath") { ... }
    Button("Filter", systemImage: "line.3.horizontal.decrease") { ... }
}
.padding(.horizontal, 16)
.padding(.vertical, 10)
.glassEffect(.regular.interactive(), in: .capsule)</code></pre>

        <h3>2. KeyframeAnimator &amp; String Catalogs (.xcstrings)</h3>
        <ul>
          <li><strong><code>PhaseAnimator</code> / <code>KeyframeAnimator</code>:</strong> Multi-step spring/cubic animations without manual timer chains or Lottie overhead.</li>
          <li><strong><code>.xcstrings</code> String Catalogs:</strong> JSON-backed localization table automatically tracking string extraction, state (<code>translated</code>, <code>needs_review</code>), and complex plural rules (<code>one</code>, <code>few</code>, <code>many</code>, <code>other</code>).</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Stacking Glass Effects Inside Scrolling Lists or Using String Interpolation for Plurals:</strong> Applying refractive glass modifiers onto 100 <code>List</code> cells wrecks GPU frame budgets and breaks visual hierarchy (HIG reserves Liquid Glass for floating control layers). Likewise, writing <code>"\\(count) item(s)"</code> breaks languages with complex plural rules—always use <code>String(localized:)</code> with <code>.xcstrings</code>.</p>
        """,
        "quizzes": [
            {
                "question": "Per Apple HIG for iOS 26 Liquid Glass, where should refractive glass materials be applied?",
                "correct_idx": 1,
                "options": [
                    {"text": "Every row inside a scrolling List.", "reason": "Option A is wrong because glass on scrolling content rows causes visual clutter and heavy offscreen GPU blending passes."},
                    {"text": "Floating controls and navigation chrome.", "reason": "Option B is correct: Liquid Glass creates an optical navigation layer floating above underlying content."},
                    {"text": "Background SQLite worker actors.", "reason": "Option C is wrong because Liquid Glass is a SwiftUI/UIKit visual material system."},
                    {"text": "Static Mach-O binary headers.", "reason": "Option D is wrong because Mach-O headers are executable metadata."}
                ]
            },
            {
                "question": "What problem do `.xcstrings` String Catalogs solve compared to legacy `.strings` and `.stringsdict` files?",
                "correct_idx": 0,
                "options": [
                    {"text": "Unifies extraction and pluralization rules.", "reason": "Option A is correct: String Catalogs combine base extraction, pluralization, device variations, and translation completion tracking in one file."},
                    {"text": "Encrypts user Keychain tokens automatically.", "reason": "Option B is wrong because Keychain encryption uses SecItem and Secure Enclave."},
                    {"text": "Eliminates the need for Dynamic Type.", "reason": "Option C is wrong because localization does not replace accessible font scaling."},
                    {"text": "Compiles Swift views into Kotlin Compose.", "reason": "Option D is wrong because .xcstrings is Apple's localization format."}
                ]
            }
        ],
        "exercise": {
            "title": "Liquid Glass Layer Validator & Pluralization Rule Engine",
            "instructions": "Implement <code>UIHygieneChecker.canApplyGlass(layer:)</code> (returning true only for <code>.floatingToolbar</code> / <code>.navigationChrome</code>, never <code>.scrollingCell</code>) and <code>UIHygieneChecker.pluralKey(for:)</code> mapping <code>0 -&gt; 'zero'</code>, <code>1 -&gt; 'one'</code>, else <code>'other'</code>. Run tests.",
            "starter_code": """import Foundation

enum UILayer {
    case scrollingCell
    case floatingToolbar
    case navigationChrome
}

enum UIHygieneChecker {
    // TODO: Return true only for .floatingToolbar and .navigationChrome
    static func canApplyGlass(layer: UILayer) -> Bool {
        fatalError("TODO: Implement canApplyGlass")
    }

    // TODO: Return "zero" for 0, "one" for 1, "other" for all other non-negative counts
    static func pluralKey(for count: Int) -> String {
        fatalError("TODO: Implement pluralKey")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.8 Verification Tests...")
    assert(UIHygieneChecker.canApplyGlass(layer: .floatingToolbar) == true, "Test 1 Failed")
    assert(UIHygieneChecker.canApplyGlass(layer: .navigationChrome) == true, "Test 2 Failed")
    assert(UIHygieneChecker.canApplyGlass(layer: .scrollingCell) == false, "Test 3 Failed")
    assert(UIHygieneChecker.pluralKey(for: 0) == "zero", "Test 4 Failed")
    assert(UIHygieneChecker.pluralKey(for: 1) == "one", "Test 5 Failed")
    assert(UIHygieneChecker.pluralKey(for: 42) == "other", "Test 6 Failed")
    print("  ✅ Challenge Passed: Liquid Glass layering & String Catalog pluralization verified")
}
runTests()""",
            "hints": [
                "Switch on layer: return true for .floatingToolbar and .navigationChrome, false for .scrollingCell.",
                "Switch on count: case 0: return \"zero\"; case 1: return \"one\"; default: return \"other\"."
            ],
            "solution_code": """import Foundation

enum UILayer {
    case scrollingCell
    case floatingToolbar
    case navigationChrome
}

enum UIHygieneChecker {
    static func canApplyGlass(layer: UILayer) -> Bool {
        switch layer {
        case .floatingToolbar, .navigationChrome:
            return true
        case .scrollingCell:
            return false
        }
    }

    static func pluralKey(for count: Int) -> String {
        switch count {
        case 0: return "zero"
        case 1: return "one"
        default: return "other"
        }
    }
}

func runTests() {
    print("🧪 Running Lesson 8.8 Verification Tests...")
    assert(UIHygieneChecker.canApplyGlass(layer: .floatingToolbar) == true)
    assert(UIHygieneChecker.canApplyGlass(layer: .navigationChrome) == true)
    assert(UIHygieneChecker.canApplyGlass(layer: .scrollingCell) == false)
    assert(UIHygieneChecker.pluralKey(for: 0) == "zero")
    assert(UIHygieneChecker.pluralKey(for: 1) == "one")
    assert(UIHygieneChecker.pluralKey(for: 42) == "other")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Keeping Liquid Glass strictly on floating control chrome preserves GPU frame budgets and legibility, while structured plural keys match Apple's <code>.xcstrings</code> localization model."
        },
        "primary_sources": [
            {"title": "Apple HIG: Materials & Liquid Glass", "url": "https://developer.apple.com/design/human-interface-guidelines/materials", "desc": "Official Apple guidance on Liquid Glass hierarchy and legibility."},
            {"title": "Apple Developer: Localizing and Varying Text with a String Catalog", "url": "https://developer.apple.com/documentation/xcode/localizing-and-varying-text-with-a-string-catalog", "desc": "Complete guide to .xcstrings files and plural/device variations."}
        ],
        "coaching_prompt": "Next, let's bring on-device Apple Intelligence into your Senior toolkit in Lesson 8.9: FoundationModels Framework & Core ML!"
    },

    # 0057: Lesson 8.9
    {
        "id": "0057",
        "slug": "0057-foundation-models-and-coreml",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.9",
        "title": "On-Device AI: FoundationModels Framework & Core ML",
        "subtitle": "Apple Intelligence FoundationModels (@Generable guided generation, Tool calling, multimodal prompts) and Core ML Neural Engine profiling.",
        "prev_url": "0056-liquid-glass-animations-and-localization.html",
        "next_url": "0058-appintents-and-system-intelligence.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, on-device GenAI uses Gemini Nano via ML Kit / AICore and LiteRT for custom TFLite models.</p>
        <p>In 2027 iOS, Apple gives native apps zero-cost, offline-capable access to Apple Intelligence via the <strong><code>FoundationModels</code></strong> framework—featuring compile-time typed structured output (<strong><code>@Generable</code></strong>) and <strong><code>Tool</code></strong> calling—alongside <strong><code>Core ML</code></strong> for custom vision/audio models running on the Apple Neural Engine (ANE).</p>
        """,
        "core_sections_html": """
        <h3>1. Guided Generation with @Generable &amp; LanguageModelSession</h3>
        <pre><code>import FoundationModels

@Generable
struct IncidentTriage {
    @Guide(description: "Severity from 1 (low) to 5 (critical)", .range(1...5))
    var severity: Int
    var summary: String
}

let session = LanguageModelSession(instructions: "Triage mobile telemetry logs concisely.")
let response = try await session.respond(to: crashLog, generating: IncidentTriage.self)
// response.content is a strongly-typed IncidentTriage—never brittle regex JSON parsing!</code></pre>

        <h3>2. Core ML &amp; Availability Guardrails</h3>
        <ul>
          <li><strong>Check Device Availability First:</strong> Always inspect <code>SystemLanguageModel.default.availability</code> and gracefully degrade to deterministic heuristics or cloud endpoints on unsupported hardware.</li>
          <li><strong>Core ML Compute Units:</strong> Profile <code>MLComputeUnits.all</code> in Xcode Instruments (Core ML template) to confirm operators execute on the Neural Engine rather than falling back to CPU.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Prompting Unconstrained Text and Parsing JSON Manually:</strong> Asking an on-device 3B model for raw JSON frequently yields markdown fences or malformed braces. Always use <code>@Generable</code> constrained decoding, and never assume Apple Intelligence is available without checking device eligibility and user opt-in status.</p>
        """,
        "quizzes": [
            {
                "question": "Why does Apple's FoundationModels framework provide the `@Generable` macro?",
                "correct_idx": 2,
                "options": [
                    {"text": "To upload user photos to third-party clouds.", "reason": "Option A is wrong because FoundationModels runs on-device or Private Cloud Compute with strict privacy."},
                    {"text": "To convert Core ML weights into SwiftUI.", "reason": "Option B is wrong because @Generable describes output schemas, not Core ML weight conversion."},
                    {"text": "To enforce constrained decoding into Swift structs.", "reason": "Option C is correct: @Generable compiles a schema that constrains token generation directly into a valid, strongly-typed Swift struct."},
                    {"text": "To replace Swift 6 Sendable checking.", "reason": "Option D is wrong because @Generable models still respect Swift 6 concurrency rules."}
                ]
            },
            {
                "question": "What must a production app always check before invoking `LanguageModelSession`?",
                "correct_idx": 1,
                "options": [
                    {"text": "The device's current cellular carrier name.", "reason": "Option A is wrong because FoundationModels runs locally and does not depend on carrier."},
                    {"text": "SystemLanguageModel availability and fallback paths.", "reason": "Option B is correct: older devices or disabled Apple Intelligence states require graceful fallback handling."},
                    {"text": "Whether CocoaPods is installed locally.", "reason": "Option C is wrong because FoundationModels is a first-party system framework."},
                    {"text": "The user's Xcode version string.", "reason": "Option D is wrong because Xcode is a development IDE, not present on customer iPhones."}
                ]
            }
        ],
        "exercise": {
            "title": "On-Device AI Availability Router & Structured Output Validator",
            "instructions": "Implement <code>AITriageRouter.selectBackend(availability:requiresOffline:)</code> returning <code>.onDeviceFoundationModel</code> when available, <code>.cloudFallback</code> when unavailable and online, or <code>.deterministicRuleEngine</code> when unavailable and offline. Also validate severity bounds (1...5). Run tests.",
            "starter_code": """import Foundation

enum ModelAvailability {
    case available
    case unavailable(reason: String)
}

enum TriageBackend: Equatable {
    case onDeviceFoundationModel
    case cloudFallback
    case deterministicRuleEngine
}

enum AITriageRouter {
    // TODO: Select appropriate backend based on availability & offline constraint
    static func selectBackend(availability: ModelAvailability, isOffline: Bool) -> TriageBackend {
        fatalError("TODO: Implement selectBackend")
    }

    // TODO: Clamp/validate @Generable severity into 1...5; return nil if summary is empty
    static func validateTriage(severity: Int, summary: String) -> (Int, String)? {
        fatalError("TODO: Implement validateTriage")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.9 Verification Tests...")
    assert(AITriageRouter.selectBackend(availability: .available, isOffline: true) == .onDeviceFoundationModel, "Test 1 Failed")
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Older chip"), isOffline: false) == .cloudFallback, "Test 2 Failed")
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Disabled"), isOffline: true) == .deterministicRuleEngine, "Test 3 Failed")
    assert(AITriageRouter.validateTriage(severity: 9, summary: "Memory spike")?.0 == 5, "Test 4 Failed")
    assert(AITriageRouter.validateTriage(severity: 3, summary: "   ") == nil, "Test 5 Failed")
    print("  ✅ Challenge Passed: FoundationModels availability routing & guide validation verified")
}
runTests()""",
            "hints": [
                "Switch on availability: if .available, return .onDeviceFoundationModel regardless of isOffline.",
                "If .unavailable, return isOffline ? .deterministicRuleEngine : .cloudFallback.",
                "Trim whitespace from summary; if empty return nil, else return (min(max(severity, 1), 5), trimmed)."
            ],
            "solution_code": """import Foundation

enum ModelAvailability {
    case available
    case unavailable(reason: String)
}

enum TriageBackend: Equatable {
    case onDeviceFoundationModel
    case cloudFallback
    case deterministicRuleEngine
}

enum AITriageRouter {
    static func selectBackend(availability: ModelAvailability, isOffline: Bool) -> TriageBackend {
        switch availability {
        case .available:
            return .onDeviceFoundationModel
        case .unavailable:
            return isOffline ? .deterministicRuleEngine : .cloudFallback
        }
    }

    static func validateTriage(severity: Int, summary: String) -> (Int, String)? {
        let trimmed = summary.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return nil }
        let clamped = min(max(severity, 1), 5)
        return (clamped, trimmed)
    }
}

func runTests() {
    print("🧪 Running Lesson 8.9 Verification Tests...")
    assert(AITriageRouter.selectBackend(availability: .available, isOffline: true) == .onDeviceFoundationModel)
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Older chip"), isOffline: false) == .cloudFallback)
    assert(AITriageRouter.selectBackend(availability: .unavailable(reason: "Disabled"), isOffline: true) == .deterministicRuleEngine)
    assert(AITriageRouter.validateTriage(severity: 9, summary: "Memory spike")?.0 == 5)
    assert(AITriageRouter.validateTriage(severity: 3, summary: "   ") == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Senior AI integration prioritizes on-device execution (zero API cost, works offline) while providing deterministic fallback paths when hardware or user settings disable Apple Intelligence."
        },
        "primary_sources": [
            {"title": "Apple Developer: Foundation Models Framework", "url": "https://developer.apple.com/documentation/foundationmodels", "desc": "Official documentation for LanguageModelSession, @Generable, and Tool calling."},
            {"title": "Apple Developer: Core ML", "url": "https://developer.apple.com/documentation/coreml", "desc": "Deploying custom quantized models onto the Apple Neural Engine."}
        ],
        "coaching_prompt": "On-device AI shines brightest when paired with OS-wide actions! Let's move to Lesson 8.10: AppIntents, Siri, Spotlight & System Intelligence."
    },

    # 0058: Lesson 8.10
    {
        "id": "0058",
        "slug": "0058-appintents-and-system-intelligence",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.10",
        "title": "AppIntents, Siri, Spotlight & System Intelligence",
        "subtitle": "Exposing domain actions & AppEntities to Siri, Apple Intelligence on-screen awareness, Spotlight, Control Center, and interactive Widgets.",
        "prev_url": "0057-foundation-models-and-coreml.html",
        "next_url": "0059-storekit2-and-passkeys.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, system integration uses App Actions, <code>shortcuts.xml</code>, and Assistant capabilities.</p>
        <p>In 2027 iOS, <strong><code>AppIntents</code></strong> is the unified backbone connecting your app to <strong>Siri, Apple Intelligence on-screen context, Spotlight, Shortcuts, Action Button, Control Center, and interactive WidgetKit</strong>. Senior engineers design headless <code>AppIntent</code>s + <code>AppEntity</code> schemas that execute cleanly even when the main UI is terminated.</p>
        """,
        "core_sections_html": """
        <h3>1. Headless AppIntent &amp; AppEntity Design</h3>
        <pre><code>import AppIntents

struct SyncMetricsIntent: AppIntent {
    static let title: LocalizedStringResource = "Sync PulseFlow Metrics"
    @Parameter(title: "Project") var project: ProjectEntity

    @Dependency var syncService: SyncServiceProtocol

    func perform() async throws -&gt; some IntentResult &amp; ProvidesDialog {
        let count = try await syncService.syncNow(projectID: project.id)
        return .result(dialog: "Synced \\(count) metrics for \\(project.name).")
    }
}</code></pre>

        <h3>2. Entity Queries &amp; System Discovery</h3>
        <ul>
          <li><strong><code>AppEntity</code> + <code>EntityQuery</code>:</strong> Allows Siri and Shortcuts to resolve domain objects by ID or search string without launching your app UI.</li>
          <li><strong><code>AppShortcutsProvider</code>:</strong> Registers zero-setup voice phrases and Spotlight suggestions automatically upon app install.</li>
          <li><strong><code>AppDependencyManager</code>:</strong> Injects shared services into headless intents cleanly without massive global singletons.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Coupling AppIntents to SwiftUI ViewModels:</strong> When Siri or an interactive Widget executes an <code>AppIntent</code>, your app may launch briefly in the background with zero SwiftUI views instantiated. If <code>perform()</code> reaches into UI-bound ViewModels, it fails or crashes—inject headless Core/Repository services via <code>@Dependency</code> instead.</p>
        """,
        "quizzes": [
            {
                "question": "Why must an `AppIntent.perform()` method avoid depending on instantiated SwiftUI Views?",
                "correct_idx": 1,
                "options": [
                    {"text": "SwiftUI cannot render on Apple Silicon.", "reason": "Option A is wrong because SwiftUI renders natively on all Apple Silicon devices."},
                    {"text": "Intents often execute headless in background.", "reason": "Option B is correct: Siri, Shortcuts, and Widget buttons run perform() out-of-view without building the SwiftUI view hierarchy."},
                    {"text": "AppIntents only run inside Objective-C.", "reason": "Option C is wrong because AppIntents is a modern Swift-first framework."},
                    {"text": "Intents automatically disable ARC memory.", "reason": "Option D is wrong because all Swift code uses ARC."}
                ]
            },
            {
                "question": "What role does `EntityQuery` play in the AppIntents framework?",
                "correct_idx": 0,
                "options": [
                    {"text": "Resolves domain entities by ID or search.", "reason": "Option A is correct: EntityQuery lets Siri, Spotlight, and Shortcuts fetch matching AppEntity records cleanly."},
                    {"text": "Compiles Metal shaders for animations.", "reason": "Option B is wrong because EntityQuery handles data lookup, not graphics."},
                    {"text": "Uploads crash logs to MetricKit.", "reason": "Option C is wrong because MetricKit operates independently."},
                    {"text": "Signs TestFlight provisioning profiles.", "reason": "Option D is wrong because signing is handled by Xcode/codesign."}
                ]
            }
        ],
        "exercise": {
            "title": "Headless AppIntent & EntityQuery Resolver",
            "instructions": "Implement <code>ProjectEntityQuery.entities(for:)</code> and <code>suggestedEntities()</code>, plus <code>SyncProjectIntent.perform()</code> returning a human-readable confirmation string without touching any UI layer. Run tests.",
            "starter_code": """import Foundation

struct ProjectEntity: Identifiable, Equatable {
    let id: String
    let name: String
}

final class HeadlessProjectStore {
    private let projects: [ProjectEntity]
    init(projects: [ProjectEntity]) { self.projects = projects }

    func entities(for identifiers: [String]) -> [ProjectEntity] {
        // TODO: Return matching ProjectEntity items preserving identifiers order
        fatalError("TODO: Implement entities(for:)")
    }

    func suggestedEntities() -> [ProjectEntity] {
        // TODO: Return up to first 3 projects as Spotlight/Shortcuts suggestions
        fatalError("TODO: Implement suggestedEntities")
    }
}

struct SyncProjectIntent {
    let projectID: String
    let store: HeadlessProjectStore

    // TODO: Resolve project by ID; throw NSError if missing, else return "Synced:<name>"
    func perform() throws -> String {
        fatalError("TODO: Implement perform")
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.10 Verification Tests...")
    let store = HeadlessProjectStore(projects: [
        ProjectEntity(id: "p1", name: "Checkout"),
        ProjectEntity(id: "p2", name: "Search"),
        ProjectEntity(id: "p3", name: "Telemetry"),
        ProjectEntity(id: "p4", name: "Auth")
    ])
    assert(store.entities(for: ["p3", "p1"]).map(\\.id) == ["p3", "p1"], "Test 1 Failed")
    assert(store.suggestedEntities().count == 3, "Test 2 Failed")
    let output = try SyncProjectIntent(projectID: "p2", store: store).perform()
    assert(output == "Synced:Search", "Test 3 Failed")
    print("  ✅ Challenge Passed: Headless AppIntent & EntityQuery resolution verified")
}
try runTests()""",
            "hints": [
                "In entities(for:): use identifiers.compactMap { id in projects.first { $0.id == id } }.",
                "In suggestedEntities(): return Array(projects.prefix(3)).",
                "In perform(): guard let entity = store.entities(for: [projectID]).first else { throw NSError(domain: \"AppIntent\", code: 404) }."
            ],
            "solution_code": """import Foundation

struct ProjectEntity: Identifiable, Equatable {
    let id: String
    let name: String
}

final class HeadlessProjectStore {
    private let projects: [ProjectEntity]
    init(projects: [ProjectEntity]) { self.projects = projects }

    func entities(for identifiers: [String]) -> [ProjectEntity] {
        identifiers.compactMap { id in projects.first { $0.id == id } }
    }

    func suggestedEntities() -> [ProjectEntity] {
        Array(projects.prefix(3))
    }
}

struct SyncProjectIntent {
    let projectID: String
    let store: HeadlessProjectStore

    func perform() throws -> String {
        guard let entity = store.entities(for: [projectID]).first else {
            throw NSError(domain: "AppIntent", code: 404)
        }
        return "Synced:\\(entity.name)"
    }
}

func runTests() throws {
    print("🧪 Running Lesson 8.10 Verification Tests...")
    let store = HeadlessProjectStore(projects: [
        ProjectEntity(id: "p1", name: "Checkout"),
        ProjectEntity(id: "p2", name: "Search"),
        ProjectEntity(id: "p3", name: "Telemetry"),
        ProjectEntity(id: "p4", name: "Auth")
    ])
    assert(store.entities(for: ["p3", "p1"]).map(\\.id) == ["p3", "p1"])
    assert(store.suggestedEntities().count == 3)
    let output = try SyncProjectIntent(projectID: "p2", store: store).perform()
    assert(output == "Synced:Search")
    print("  ✅ All tests passed!")
}
try runTests()""",
            "solution_explanation": "Designing <code>EntityQuery</code> resolution and headless <code>perform()</code> execution cleanly decoupled from UI ensures Siri, Spotlight, and interactive Widgets stay reliable even on cold background launches."
        },
        "primary_sources": [
            {"title": "Apple Developer: App Intents Framework", "url": "https://developer.apple.com/documentation/appintents", "desc": "Official reference for AppIntent, AppEntity, EntityQuery, and AppShortcutsProvider."},
            {"title": "WWDC24/25: Bring Your App's Core Features to Users with App Intents", "url": "https://developer.apple.com/videos/", "desc": "Architectural best practices for Siri, Spotlight, and Apple Intelligence integration."}
        ],
        "coaching_prompt": "System intelligence makes your app feel truly native to iOS. Ready for Lesson 8.11: Monetization & Passwordless Auth (StoreKit 2 & Passkeys)?"
    },

    # 0059: Lesson 8.11
    {
        "id": "0059",
        "slug": "0059-storekit2-and-passkeys",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.11",
        "title": "Monetization & Passwordless Auth: StoreKit 2 & Passkeys",
        "subtitle": "Async JWS-verified StoreKit 2 subscriptions, Transaction.updates listeners, local .storekit testing, and AuthenticationServices Passkeys.",
        "prev_url": "0058-appintents-and-system-intelligence.html",
        "next_url": "0060-xcode-cloud-and-release-engineering.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, monetization uses Google Play Billing Library (<code>BillingClient</code>) and passwordless sign-in uses Jetpack Credential Manager (<code>GetCredentialRequest</code>).</p>
        <p>In iOS, <strong><code>StoreKit 2</code></strong> replaces legacy delegate-based receipts with async/await APIs and built-in <strong>JWS cryptographic verification</strong> (<code>VerificationResult&lt;Transaction&gt;</code>). For auth, <strong>Passkeys</strong> (<code>ASAuthorizationPlatformPublicKeyCredentialProvider</code>) bind WebAuthn credentials directly to Face ID and iCloud Keychain.</p>
        """,
        "core_sections_html": """
        <h3>1. StoreKit 2 Entitlement Verification &amp; Transaction.updates</h3>
        <pre><code>// Always run a background listener at launch for renewals, Ask-to-Buy, and refunds!
Task.detached {
    for await result in Transaction.updates {
        if case .verified(let transaction) = result {
            await entitlementStore.apply(transaction)
            await transaction.finish() // Only call finish() AFTER persisting entitlement!
        }
    }
}</code></pre>

        <h3>2. Passkeys with AuthenticationServices</h3>
        <ul>
          <li><strong>Associated Domains Entitlement:</strong> Requires <code>webcredentials:example.com</code> hosted via <code>/.well-known/apple-app-site-association</code>.</li>
          <li><strong>Zero Password Storage:</strong> The server sends a cryptographic challenge; Secure Enclave signs it after Face ID verification, eliminating credential-stuffing attacks.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Forgetting <code>Transaction.updates</code> or Granting Unverified Purchases:</strong> Only handling <code>product.purchase()</code> misses Family Sharing grants, Ask-to-Buy approvals, and subscription renewals that arrive later. Worse, unlocking pro features on <code>.unverified</code> transactions exposes your app to trivial jailbreak/proxy spoofing.</p>
        """,
        "quizzes": [
            {
                "question": "When should your app call `await transaction.finish()` in StoreKit 2?",
                "correct_idx": 2,
                "options": [
                    {"text": "Immediately before checking JWS verification.", "reason": "Option A is wrong because unverified transactions should never unlock content or be finished blindly."},
                    {"text": "Only inside an Objective-C delegate.", "reason": "Option B is wrong because StoreKit 2 uses Swift async/await sequences, not Obj-C delegates."},
                    {"text": "After verifying and persisting the entitlement.", "reason": "Option C is correct: finishing signals StoreKit that delivery is complete; finish only after durable entitlement update."},
                    {"text": "Never for auto-renewable subscriptions.", "reason": "Option D is wrong because unfinished transactions keep re-delivering via Transaction.updates."}
                ]
            },
            {
                "question": "What configuration is mandatory on your backend domain for iOS Passkeys to function?",
                "correct_idx": 0,
                "options": [
                    {"text": "An apple-app-site-association JSON file.", "reason": "Option A is correct: webcredentials entitlement requires a valid AASA file served over HTTPS."},
                    {"text": "A legacy ASN.1 receipt validator.", "reason": "Option B is wrong because receipts belong to legacy StoreKit 1."},
                    {"text": "An unencrypted HTTP port 80 endpoint.", "reason": "Option C is wrong because Passkeys strictly require HTTPS and ATS compliance."},
                    {"text": "A CocoaPods bridging header file.", "reason": "Option D is wrong because Passkeys use Apple's first-party AuthenticationServices."}
                ]
            }
        ],
        "exercise": {
            "title": "StoreKit 2 JWS Entitlement Reducer",
            "instructions": "Implement <code>EntitlementEngine.process(_:)</code> handling <code>.verified</code> vs <code>.unverified</code> transactions, ignoring revoked transactions (<code>revocationDate != nil</code>), and returning only active non-revoked product IDs that should be marked finished. Run tests.",
            "starter_code": """import Foundation

struct StoreTransaction {
    let id: UInt64
    let productID: String
    let revocationDate: Date?
}

enum VerificationOutcome {
    case verified(StoreTransaction)
    case unverified(StoreTransaction)
}

final class EntitlementEngine {
    private(set) var activeProducts: Set<String> = []
    private(set) var finishedIDs: [UInt64] = []

    // TODO:
    // - Ignore .unverified transactions (do not grant, do not finish)
    // - For .verified(tx): if tx.revocationDate != nil, remove tx.productID from activeProducts;
    //   otherwise insert tx.productID into activeProducts. Always append tx.id to finishedIDs.
    func process(_ outcome: VerificationOutcome) {
        fatalError("TODO: Implement process")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.11 Verification Tests...")
    let engine = EntitlementEngine()
    engine.process(.unverified(StoreTransaction(id: 1, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs.isEmpty, "Test 1 Failed: Unverified tx must not unlock or finish")

    engine.process(.verified(StoreTransaction(id: 2, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts == ["pro.yearly"] && engine.finishedIDs == [2], "Test 2 Failed")

    engine.process(.verified(StoreTransaction(id: 3, productID: "pro.yearly", revocationDate: Date())))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs == [2, 3], "Test 3 Failed: Revoked tx must remove entitlement")
    print("  ✅ Challenge Passed: StoreKit 2 verification & revocation handling verified")
}
runTests()""",
            "hints": [
                "Switch on outcome: case .unverified: return.",
                "For .verified(let tx): check if tx.revocationDate != nil to remove or insert tx.productID.",
                "Append tx.id to finishedIDs after updating activeProducts."
            ],
            "solution_code": """import Foundation

struct StoreTransaction {
    let id: UInt64
    let productID: String
    let revocationDate: Date?
}

enum VerificationOutcome {
    case verified(StoreTransaction)
    case unverified(StoreTransaction)
}

final class EntitlementEngine {
    private(set) var activeProducts: Set<String> = []
    private(set) var finishedIDs: [UInt64] = []

    func process(_ outcome: VerificationOutcome) {
        switch outcome {
        case .unverified:
            return
        case .verified(let tx):
            if tx.revocationDate != nil {
                activeProducts.remove(tx.productID)
            } else {
                activeProducts.insert(tx.productID)
            }
            finishedIDs.append(tx.id)
        }
    }
}

func runTests() {
    print("🧪 Running Lesson 8.11 Verification Tests...")
    let engine = EntitlementEngine()
    engine.process(.unverified(StoreTransaction(id: 1, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs.isEmpty)

    engine.process(.verified(StoreTransaction(id: 2, productID: "pro.yearly", revocationDate: nil)))
    assert(engine.activeProducts == ["pro.yearly"] && engine.finishedIDs == [2])

    engine.process(.verified(StoreTransaction(id: 3, productID: "pro.yearly", revocationDate: Date())))
    assert(engine.activeProducts.isEmpty && engine.finishedIDs == [2, 3])
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Filtering out <code>.unverified</code> payloads and honoring <code>revocationDate</code> for refunds protects revenue without server-side receipt polling, while calling <code>finish()</code> after entitlement update prevents duplicate processing."
        },
        "primary_sources": [
            {"title": "Apple Developer: StoreKit 2 Documentation", "url": "https://developer.apple.com/documentation/storekit", "desc": "Official guide on Product, Transaction, VerificationResult, and SubscriptionStoreView."},
            {"title": "Apple Developer: Supporting Passkeys", "url": "https://developer.apple.com/documentation/authenticationservices/supporting-passkeys", "desc": "Implementing FIDO2 passwordless authentication with Face ID and iCloud Keychain."}
        ],
        "coaching_prompt": "One final frontier module before your upgraded Capstone: Lesson 8.12 — Xcode Cloud & Multi-Channel Release Engineering!"
    },

    # 0060: Lesson 8.12
    {
        "id": "0060",
        "slug": "0060-xcode-cloud-and-release-engineering",
        "phase_num": 8,
        "phase_title": "Phase 8: 2027 Senior iOS Frontier & Platform Expansion",
        "lesson_num": "Lesson 8.12",
        "title": "Xcode Cloud & Multi-Channel Release Engineering",
        "subtitle": "Apple's native Xcode Cloud (ci_post_clone.sh), headless xcodebuild + xcresulttool, phased App Store rollouts, and Fastlane vs. Xcode Cloud.",
        "prev_url": "0059-storekit2-and-passkeys.html",
        "next_url": "0061-capstone-portfolio-blueprint.html",
        "cheatsheet_url": "../reference/2027-frontier-cheatsheet.html",
        "cheatsheet_title": "2027 Frontier Cheatsheet",
        "android_anchor_html": """
        <p>In Android, Play Console staged rollouts and GitHub Actions Gradle runners are standard.</p>
        <p>In iOS 2027, <strong>Xcode Cloud</strong> eliminates manual certificate management and macOS runner maintenance by living directly inside App Store Connect and Xcode. Senior leads evaluate when to use <strong>Xcode Cloud</strong> vs <strong>GitHub Actions + Fastlane</strong>, inspect failures via <code>xcresulttool</code>, and safeguard production via <strong>7-day Phased Releases</strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. Xcode Cloud Custom Scripts (ci_scripts/)</h3>
        <pre><code>#!/bin/sh
# ci_scripts/ci_post_clone.sh — Runs after Xcode Cloud clones your repo
set -e
brew install swiftlint
swift format lint --strict Sources/</code></pre>

        <h3>2. Headless CI &amp; Phased Rollout Guardrails</h3>
        <ul>
          <li><strong>Xcode Cloud Advantages:</strong> Zero <code>fastlane match</code> certificate sync needed, native TestFlight/App Store Connect triggers, Xcode IDE inline build failure logs.</li>
          <li><strong>GitHub Actions + Fastlane Advantages:</strong> Complex cross-repo monorepos, self-hosted Apple Silicon M-series runners, custom Slack/Jira release bots.</li>
          <li><strong>Phased Release:</strong> Rolls App Store updates out over 7 days (1% → 2% → 5% → 10% → 20% → 50% → 100%) so MetricKit hang/crash regressions can be paused immediately!</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Shipping 100% Day-1 Releases Without Phased Rollout or Automated MetricKit Gates:</strong> Unlike Android APK hotfixes, Apple App Store reviews take hours to days. Pushing a crash regression to 100% of users simultaneously strands customers until an expedited review clears—always enable Phased Release and monitor crash-free sessions at the 1%–5% cohorts.</p>
        """,
        "quizzes": [
            {
                "question": "Which directory and script name does Xcode Cloud automatically execute right after cloning your repository?",
                "correct_idx": 1,
                "options": [
                    {"text": ".github/workflows/post_clone.yml", "reason": "Option A is wrong because .github/workflows is GitHub Actions configuration."},
                    {"text": "ci_scripts/ci_post_clone.sh", "reason": "Option B is correct: Xcode Cloud invokes executable scripts inside ci_scripts/ at defined lifecycle hooks like ci_post_clone.sh."},
                    {"text": "fastlane/Matchfile.sh", "reason": "Option C is wrong because Fastlane uses Ruby configuration files, not Xcode Cloud hooks."},
                    {"text": "Pods/post_install.rb", "reason": "Option D is wrong because that belongs to legacy CocoaPods."}
                ]
            },
            {
                "question": "Why is enabling App Store Phased Release critical even when CI tests pass 100%?",
                "correct_idx": 3,
                "options": [
                    {"text": "It skips Apple App Store binary review.", "reason": "Option A is wrong because Phased Release begins only after App Store review approval."},
                    {"text": "It reduces Mach-O binary download size.", "reason": "Option B is wrong because app thinning handles binary size optimization."},
                    {"text": "It converts SwiftUI views into UIKit.", "reason": "Option C is wrong because rollout scheduling has no effect on UI frameworks."},
                    {"text": "Limits blast radius if regressions appear.", "reason": "Option D is correct: starting at 1% allows pausing the rollout if MetricKit or crash reports spike."}
                ]
            }
        ],
        "exercise": {
            "title": "Phased Rollout Safety Gate Simulator",
            "instructions": "Implement <code>RolloutController.nextStage(currentPercent:crashFreeRate:)</code> following Apple's 7-day phased schedule (<code>[1, 2, 5, 10, 20, 50, 100]</code>). Advance to the next stage only if <code>crashFreeRate &gt;= 0.995</code>; otherwise return <code>.paused(at: currentPercent)</code>. Run tests.",
            "starter_code": """import Foundation

enum RolloutDecision: Equatable {
    case advance(to: Int)
    case completed
    case paused(at: Int)
}

enum RolloutController {
    static let schedule = [1, 2, 5, 10, 20, 50, 100]

    // TODO: If crashFreeRate < 0.995 -> .paused(at: currentPercent)
    // If currentPercent == 100 -> .completed
    // Otherwise advance to the next percent in schedule
    static func nextStage(currentPercent: Int, crashFreeRate: Double) -> RolloutDecision {
        fatalError("TODO: Implement nextStage")
    }
}

func runTests() {
    print("🧪 Running Lesson 8.12 Verification Tests...")
    assert(RolloutController.nextStage(currentPercent: 1, crashFreeRate: 0.999) == .advance(to: 2), "Test 1 Failed")
    assert(RolloutController.nextStage(currentPercent: 5, crashFreeRate: 0.980) == .paused(at: 5), "Test 2 Failed")
    assert(RolloutController.nextStage(currentPercent: 100, crashFreeRate: 0.999) == .completed, "Test 3 Failed")
    print("  ✅ Challenge Passed: Phased App Store rollout safety gate verified")
}
runTests()""",
            "hints": [
                "First guard crashFreeRate >= 0.995 else { return .paused(at: currentPercent) }.",
                "Find index of currentPercent in schedule. If it's the last element (100), return .completed.",
                "Otherwise return .advance(to: schedule[index + 1])."
            ],
            "solution_code": """import Foundation

enum RolloutDecision: Equatable {
    case advance(to: Int)
    case completed
    case paused(at: Int)
}

enum RolloutController {
    static let schedule = [1, 2, 5, 10, 20, 50, 100]

    static func nextStage(currentPercent: Int, crashFreeRate: Double) -> RolloutDecision {
        guard crashFreeRate >= 0.995 else {
            return .paused(at: currentPercent)
        }
        guard let idx = schedule.firstIndex(of: currentPercent) else {
            return .paused(at: currentPercent)
        }
        if idx + 1 >= schedule.count {
            return .completed
        }
        return .advance(to: schedule[idx + 1])
    }
}

func runTests() {
    print("🧪 Running Lesson 8.12 Verification Tests...")
    assert(RolloutController.nextStage(currentPercent: 1, crashFreeRate: 0.999) == .advance(to: 2))
    assert(RolloutController.nextStage(currentPercent: 5, crashFreeRate: 0.980) == .paused(at: 5))
    assert(RolloutController.nextStage(currentPercent: 100, crashFreeRate: 0.999) == .completed)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Gating each phased cohort on production crash-free health contains regressions at 1%–5% of users before an expedited App Store hotfix can ship."
        },
        "primary_sources": [
            {"title": "Apple Developer: Xcode Cloud Overview & Custom Build Scripts", "url": "https://developer.apple.com/documentation/xcode/xcode-cloud", "desc": "Configuring ci_post_clone.sh, workflows, and automatic TestFlight distribution."},
            {"title": "Apple Developer: Release a Version Update in Phases", "url": "https://developer.apple.com/help/app-store-connect/update-your-app/release-a-version-update-in-phases/", "desc": "Official 7-day App Store phased rollout documentation."}
        ],
        "coaching_prompt": "You've conquered all 12 lessons of the 2027 Senior iOS Frontier! Proceed to Milestone 9.1 to assemble the complete PulseFlow Enterprise Capstone."
    }
]

print("Phase 8 Frontier data loaded (Lessons 8.1 - 8.12).")
