# Phase 1: Swift Compiler & Language Internals Through an Android Lens
# Lessons 0001 - 0008

PHASE1_LESSONS = [
    # 0001: Lesson 1.1
    {
        "id": "0001",
        "slug": "0001-swift-types-and-mutability",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.1",
        "title": "The Swift Type System, Mutability & Optionals",
        "subtitle": "Memory layout of Optionals, Spare Bit Optimization, Transitive Value Immutability, mutating semantics, and the Law of Exclusivity.",
        "prev_url": "../index.html",
        "next_url": "0002-value-vs-reference-types.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin, <code>val</code> guarantees <em>reference immutability</em>: the reference cannot be reassigned, but the underlying object on the JVM heap remains mutable unless declared as an immutable collection (e.g. <code>val list = mutableListOf(1); list.add(2)</code> is completely valid).</p>
        <p>In Swift, collections (<code>Array</code>, <code>Dictionary</code>, <code>Set</code>) and custom <code>struct</code> models are <strong>value types</strong>. Binding a struct with <code>let</code> establishes <strong>deep transitive immutability</strong>: the entire contiguous memory block on the stack or CPU register is frozen.</p>
        <div class="table-wrapper">
          <table class="comparison-table">
            <thead>
              <tr><th>Dimension</th><th>Kotlin (Android)</th><th>Swift (iOS)</th><th>Senior Architectural Distinction</th></tr>
            </thead>
            <tbody>
              <tr><td><strong>Object Immutability</strong></td><td><code>val list = mutableListOf(1)</code></td><td><code>let list = [1]</code></td><td>In Kotlin, the heap object can still mutate. In Swift, <code>let</code> forbids all mutation because value semantics guarantee zero side effects across threads.</td></tr>
              <tr><td><strong>Method Mutation</strong></td><td>Methods mutate <code>this</code> freely</td><td>Must declare <code>mutating func</code></td><td>Under the hood, Swift passes <code>self</code> as an implicit <code>inout self</code> parameter. Disallowed on <code>let</code> constants.</td></tr>
              <tr><td><strong>Memory Layout of Null</strong></td><td>Null pointer (0x0) on JVM heap</td><td><code>Optional&lt;Wrapped&gt;</code> enum</td><td>Swift uses <strong>Spare Bit Optimization</strong> for pointers (0 bytes overhead), but adds a 1-byte discriminator tag for saturated types like <code>Int</code>.</td></tr>
              <tr><td><strong>Memory Exclusivity</strong></td><td>Concurrent race condition hazard</td><td>Law of Exclusivity (SE-0176)</td><td>The compiler strictly enforces that two accesses to the same memory location cannot overlap if one is a write, even within a single thread.</td></tr>
            </tbody>
          </table>
        </div>
        """,
        "core_sections_html": """
        <h3>1. Memory Layout of Optionals (Senior Interview Classic)</h3>
        <p>Swift Optionals are defined in the standard library as an algebraic enum:</p>
        <pre><code>@frozen public enum Optional&lt;Wrapped&gt; {
    case none
    case some(Wrapped)
}</code></pre>
        <p>How does the compiler lay this out in memory?</p>
        <ul>
          <li><strong>Spare Bit Optimization (Pointers & References):</strong> On 64-bit Darwin, an object reference cannot be <code>0x0</code>. Swift exploits this: <code>0x0</code> represents <code>.none</code>, while valid addresses represent <code>.some(ptr)</code>.<br><code>MemoryLayout&lt;Optional&lt;AnyObject&gt;&gt;.size == 8</code> (0 bytes overhead!).</li>
          <li><strong>Saturated Value Types (e.g. <code>Int</code>):</strong> A 64-bit integer uses all $2^{64}$ bit combinations. No spare bits exist. The compiler must append an extra 1-byte discriminator tag:<br><code>MemoryLayout&lt;Int&gt;.size == 8</code>, but <code>MemoryLayout&lt;Optional&lt;Int&gt;&gt;.size == 9</code> (stride of 16 for 8-byte CPU alignment).</li>
        </ul>

        <h3>2. <code>mutating func</code> & Copy-In Copy-Out (<code>inout</code>)</h3>
        <p>When you call a mutating method on a struct, Swift passes <code>self</code> as an implicit <code>inout</code> parameter. The modified value is written back upon method return:</p>
        <pre><code>struct UserSession {
    let sessionId: String
    var expiresAtEpoch: Int

    mutating func extendSession(seconds: Int) {
        self.expiresAtEpoch += seconds
    }
}</code></pre>

        <h3>3. The Law of Exclusivity (SE-0176)</h3>
        <p>If two accesses to the same memory location overlap in time, both must be reads. Passing a variable as <code>inout</code> grants exclusive write access for the entire function call duration:</p>
        <pre><code>func swapVals(_ a: inout Int, _ b: inout Int) { a += b }
var x = 10
// swapVals(&x, &x) // 💥 FATAL COMPILER / RUNTIME TRAP: Simultaneous accesses violate exclusivity!</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Implicitly Unwrapped Optionals (IUOs) in Production:</strong> Declaring properties with <code>!</code> (e.g. <code>var client: APIClient!</code>) bypasses compiler safety. While historically common in UIKit for <code>@IBOutlet</code>, modern Senior codebases ban IUOs via SwiftLint. If initialization is deferred, use explicit dependency injection or Optional with <code>guard let</code>.</p>
        """,
        "quizzes": [
            {
                "question": "Why does MemoryLayout<Optional<Int>>.size equal 9 bytes on 64-bit Apple platforms?",
                "correct_idx": 1,
                "options": [
                    {"text": "The integer pointer is duplicated.", "reason": "Option A is wrong because Int is a value type, not an object reference pointer."},
                    {"text": "One discriminator byte tag is added.", "reason": "Option B is correct because Int uses all 64 bits with no spare bit values; the compiler appends a 1-byte enum tag."},
                    {"text": "The entire struct allocates on heap.", "reason": "Option C is wrong because Optionals of value types are stored inline on the stack, not on the heap."},
                    {"text": "Dynamic vtable pointer paddings add.", "reason": "Option D is wrong because structs and enums do not contain vtable headers."}
                ]
            },
            {
                "question": "What happens when swapValues(&x, &x) is called where both parameters are declared inout?",
                "correct_idx": 0,
                "options": [
                    {"text": "Simultaneous access crashes immediately.", "reason": "Option A is correct because the Law of Exclusivity (SE-0176) prohibits overlapping write access to the same memory location."},
                    {"text": "Compiler silently clones the variable.", "reason": "Option B is wrong because Swift never silently clones inout references."},
                    {"text": "Dynamic dispatch skips the function call.", "reason": "Option C is wrong because inout passing uses direct memory pointers, not dynamic dispatch."},
                    {"text": "Reference counts decrement two times.", "reason": "Option D is wrong because value types like Int do not participate in reference counting."}
                ]
            }
        ],
        "exercise": {
            "title": "Defensive Unwrapping & Struct Mutability Engine",
            "instructions": "Complete the implementation of <code>renewSession</code> and <code>normalizeUsername</code>. Implement safe dictionary payload validation with <code>guard let</code>. Replace all <code>TODO</code> markers and click Run & Check to verify test assertions.",
            "starter_code": """import Foundation

struct UserSession: Equatable {
    let sessionId: String
    var userId: String
    var expiresAtEpoch: Int

    // TODO: Add a mutating func extendSession(seconds: Int) that increments expiresAtEpoch
}

// TODO: Implement renewSession(session: UserSession) -> UserSession
// Create a mutable copy of session, extend it by 3600 seconds, and return it.
func renewSession(session: UserSession) -> UserSession {
    fatalError("TODO: Implement renewSession")
}

// TODO: Implement normalizeUsername(_ username: inout String)
// - Trim leading/trailing whitespace and newlines
// - Lowercase the string
// - If empty after trimming, set username to "guest_user"
func normalizeUsername(_ username: inout String) {
    fatalError("TODO: Implement normalizeUsername")
}

// MARK: - Verification Test Suite
func runTests() {
    print("🧪 Running Lesson 1.1 Verification Tests...")

    // Test 1: Struct Mutability
    let initialSession = UserSession(sessionId: "sess_001", userId: "u123", expiresAtEpoch: 1000)
    let renewed = renewSession(session: initialSession)
    assert(initialSession.expiresAtEpoch == 1000, "Test 1A Failed: Original struct was mutated unexpectedly")
    assert(renewed.expiresAtEpoch == 4600, "Test 1B Failed: Renewed session not extended")
    print("  ✅ Challenge 1 Passed: Struct value immutability & mutating func")

    // Test 2: inout Normalization
    var name1 = "   AlexDeveloper\\n  "
    normalizeUsername(&name1)
    assert(name1 == "alexdeveloper", "Test 2A Failed: whitespace/lowercase not normalized")

    var name2 = "   "
    normalizeUsername(&name2)
    assert(name2 == "guest_user", "Test 2B Failed: empty string not reset to guest_user")
    print("  ✅ Challenge 2 Passed: inout parameter copy-in copy-out")

    print("\\n🎉 All Lesson 1.1 tests passed successfully!")
}

runTests()""",
            "hints": [
                "Mark the method with 'mutating func extendSession(seconds: Int)' so the compiler permits updating expiresAtEpoch.",
                "In renewSession, write 'var copy = session' then call 'copy.extendSession(seconds: 3600)' and return copy.",
                "In normalizeUsername, use username.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() and check .isEmpty."
            ],
            "solution_code": """import Foundation

struct UserSession: Equatable {
    let sessionId: String
    var userId: String
    var expiresAtEpoch: Int

    mutating func extendSession(seconds: Int) {
        expiresAtEpoch += seconds
    }
}

func renewSession(session: UserSession) -> UserSession {
    var copy = session
    copy.extendSession(seconds: 3600)
    return copy
}

func normalizeUsername(_ username: inout String) {
    let cleaned = username.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
    if cleaned.isEmpty {
        username = "guest_user"
    } else {
        username = cleaned
    }
}

func runTests() {
    print("🧪 Running Lesson 1.1 Verification Tests...")
    let initialSession = UserSession(sessionId: "sess_001", userId: "u123", expiresAtEpoch: 1000)
    let renewed = renewSession(session: initialSession)
    assert(initialSession.expiresAtEpoch == 1000)
    assert(renewed.expiresAtEpoch == 4600)
    var name1 = "   AlexDeveloper\\n  "
    normalizeUsername(&name1)
    assert(name1 == "alexdeveloper")
    var name2 = "   "
    normalizeUsername(&name2)
    assert(name2 == "guest_user")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "In Swift, struct instances have independent value identity. Mutating methods require explicit mutating declarations because self is passed by inout. In renewSession, assigning to var copy creates an independent stack value, leaving initialSession untouched."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0176: Enforce Exclusive Access to Memory", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0176-enforce-exclusive-access-to-memory.md", "desc": "Canonical specification of Swift memory exclusivity."},
            {"title": "The Swift Programming Language: The Basics & Optionals", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/thebasics/", "desc": "Official Apple language guide for Optionals and type safety."}
        ],
        "coaching_prompt": "Try mutating a struct held by let in Xcode or the editor above to observe how the compiler catches side-effect mutations before runtime."
    },

    # 0002: Lesson 1.2
    {
        "id": "0002",
        "slug": "0002-value-vs-reference-types",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.2",
        "title": "Value Types vs. Reference Types & Custom Copy-on-Write (CoW)",
        "subtitle": "Deep dive into memory layouts (size, stride, alignment), stack vs heap allocation, and implementing production-grade Copy-on-Write using isKnownUniquelyReferenced.",
        "prev_url": "0001-swift-types-and-mutability.html",
        "next_url": "0003-arc-and-memory-safety.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), every class and <code>data class</code> is a reference type allocated on the garbage-collected JVM heap. Passing an object passes a reference pointer; two variables pointing to the same instance share mutable state unless cloned via <code>copy()</code>.</p>
        <p>In Swift, <code>struct</code> and <code>enum</code> are <strong>value types</strong>. They are allocated inline on the stack or in CPU registers. When assigned or passed into functions, they have independent identity.</p>
        """,
        "core_sections_html": """
        <h3>1. MemoryLayout: Size, Stride & Alignment</h3>
        <p>Senior iOS engineers understand how the 64-bit compiler packs data into memory:</p>
        <ul>
          <li><strong>Size:</strong> Exact contiguous bytes of payload data.</li>
          <li><strong>Alignment:</strong> Address boundary multiple the type must align with (e.g. 8 bytes for Double/Int on 64-bit).</li>
          <li><strong>Stride:</strong> Distance in bytes between consecutive elements in an array (Size rounded up to Alignment multiple).</li>
        </ul>
        <pre><code>struct SensorPacket {
    var timestamp: Double // 8 bytes (align 8)
    var reading: Double   // 8 bytes (align 8)
    var flag: Bool        // 1 byte  (align 1)
}
MemoryLayout&lt;SensorPacket&gt;.size      // 17 bytes
MemoryLayout&lt;SensorPacket&gt;.alignment // 8 bytes
MemoryLayout&lt;SensorPacket&gt;.stride    // 24 bytes (17 rounded up to multiple of 8)</code></pre>

        <h3>2. Implementing Custom Copy-on-Write (CoW)</h3>
        <p>Swift collections (<code>Array</code>, <code>Dictionary</code>) are value types backed by heap buffers. To avoid expensive copies on assignment, they share heap buffers until a mutation occurs. If the reference count is greater than 1, a deep copy is made just-in-time:</p>
        <pre><code>final class BackendBuffer&lt;T&gt; {
    var value: T
    init(value: T) { self.value = value }
}

struct CoWWrapper&lt;T&gt; {
    private var buffer: BackendBuffer&lt;T&gt;

    init(value: T) { self.buffer = BackendBuffer(value: value) }

    var value: T {
        get { buffer.value }
        set {
            if !isKnownUniquelyReferenced(&buffer) {
                buffer = BackendBuffer(value: newValue) // Diverge!
            } else {
                buffer.value = newValue
            }
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Over-allocating Structs (>3 words in Protocols):</strong> While structs are stack-allocated, boxing a large struct (>24 bytes) into an existential container (<code>any Protocol</code>) forces Swift to allocate heap memory behind the scenes. Keep structs lightweight or use generics (<code>some Protocol</code>).</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary role of isKnownUniquelyReferenced in custom Copy-on-Write implementations?",
                "correct_idx": 1,
                "options": [
                    {"text": "It frees heap buffer memory.", "reason": "Option A is wrong because deallocation is performed automatically by ARC when reference count hits 0."},
                    {"text": "It checks strong reference count.", "reason": "Option B is correct: it returns true only if the object reference has a strong retain count of exactly 1."},
                    {"text": "It locks running worker threads.", "reason": "Option C is wrong because isKnownUniquelyReferenced is not a mutual exclusion lock."},
                    {"text": "It computes payload hash values.", "reason": "Option D is wrong because it inspects ARC runtime metadata, not hashing."}
                ]
            },
            {
                "question": "For a struct with size 17 bytes and alignment 8 bytes, what is its MemoryLayout stride?",
                "correct_idx": 2,
                "options": [
                    {"text": "Stride equals exactly 17 bytes.", "reason": "Option A is wrong because 17 is not a multiple of 8-byte alignment."},
                    {"text": "Stride equals exactly 18 bytes.", "reason": "Option B is wrong because 18 is not divisible by 8."},
                    {"text": "Stride equals exactly 24 bytes.", "reason": "Option C is correct because 17 rounded up to the nearest multiple of 8 is 24."},
                    {"text": "Stride equals exactly 32 bytes.", "reason": "Option D is wrong because 24 is the smallest multiple of 8 greater than or equal to 17."}
                ]
            }
        ],
        "exercise": {
            "title": "Custom Copy-On-Write Cache Store",
            "instructions": "Implement the setter in <code>CoWStore</code> so that if the underlying storage is shared (!isKnownUniquelyReferenced), a new <code>Storage</code> instance is created before updating. Replace the <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

final class Storage<T> {
    var payload: T
    init(payload: T) { self.payload = payload }
}

struct CoWStore<T> {
    private var storage: Storage<T>

    init(payload: T) {
        self.storage = Storage(payload: payload)
    }

    var value: T {
        get { storage.payload }
        set {
            // TODO: Use isKnownUniquelyReferenced(&storage)
            // If NOT uniquely referenced, clone storage with newValue.
            // If uniquely referenced, mutate storage.payload directly.
            fatalError("TODO: Implement CoW setter")
        }
    }

    var pointerAddress: UnsafeMutableRawPointer {
        Unmanaged.passUnretained(storage).toOpaque()
    }
}

// MARK: - Verification
func runTests() {
    print("🧪 Running Lesson 1.2 Verification Tests...")
    let store1 = CoWStore(payload: "initial_data")
    var store2 = store1

    assert(store1.pointerAddress == store2.pointerAddress, "Test 1A Failed: Storage should be shared before write")
    store2.value = "modified_data"
    assert(store1.pointerAddress != store2.pointerAddress, "Test 1B Failed: Storage must diverge after write")
    assert(store1.value == "initial_data", "Test 1C Failed: store1 corrupted")
    assert(store2.value == "modified_data", "Test 1D Failed: store2 update failed")
    print("  ✅ Challenge Passed: Custom CoW storage isolation verified")
}
runTests()""",
            "hints": [
                "Call isKnownUniquelyReferenced(&storage).",
                "If it returns false, reassign self.storage = Storage(payload: newValue).",
                "If it returns true, simply assign self.storage.payload = newValue."
            ],
            "solution_code": """import Foundation

final class Storage<T> {
    var payload: T
    init(payload: T) { self.payload = payload }
}

struct CoWStore<T> {
    private var storage: Storage<T>

    init(payload: T) {
        self.storage = Storage(payload: payload)
    }

    var value: T {
        get { storage.payload }
        set {
            if !isKnownUniquelyReferenced(&storage) {
                storage = Storage(payload: newValue)
            } else {
                storage.payload = newValue
            }
        }
    }

    var pointerAddress: UnsafeMutableRawPointer {
        Unmanaged.passUnretained(storage).toOpaque()
    }
}

func runTests() {
    print("🧪 Running Lesson 1.2 Verification Tests...")
    let store1 = CoWStore(payload: "initial_data")
    var store2 = store1
    assert(store1.pointerAddress == store2.pointerAddress)
    store2.value = "modified_data"
    assert(store1.pointerAddress != store2.pointerAddress)
    assert(store1.value == "initial_data")
    assert(store2.value == "modified_data")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "isKnownUniquelyReferenced checks the strong reference count of the object pointer in O(1) time without locking. When store2 mutates, the buffer was shared (count=2), so store2 cloned its own storage, isolating both structs with zero unwanted side-effects."
        },
        "primary_sources": [
            {"title": "Swift Standard Library: isKnownUniquelyReferenced", "url": "https://developer.apple.com/documentation/swift/isknownuniquelyreferenced(_:)", "desc": "Official Apple documentation for reference count uniqueness checks."},
            {"title": "Swift MemoryLayout Reference", "url": "https://developer.apple.com/documentation/swift/memorylayout", "desc": "Apple documentation for size, stride, and alignment."}
        ],
        "coaching_prompt": "How does CoW enable Swift to pass 1,000,000-element arrays between functions with zero allocation overhead until mutation?"
    },

    # 0003: Lesson 1.3
    {
        "id": "0003",
        "slug": "0003-arc-and-memory-safety",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.3",
        "title": "Automatic Reference Counting (ARC) & Memory Safety",
        "subtitle": "Strong, Weak, Unowned references, closure capture lists [weak self], retain cycles, and autorelease pools.",
        "prev_url": "0002-value-vs-reference-types.html",
        "next_url": "0004-enums-and-pattern-matching.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Android (ART), cyclical references between heap objects (e.g. Object A points to B, and B points to A) are collected harmlessly by the generational tracing garbage collector as long as the cluster is unreachable from a GC Root.</p>
        <p>In iOS, memory is managed deterministically via <strong>Automatic Reference Counting (ARC)</strong>. Each class instance tracks its retain count. When count reaches zero, it deallocates immediately. <strong>Retain cycles are never collected</strong> and cause silent, permanent memory leaks that eventually trigger Darwin Kernel Jetsam termination.</p>
        """,
        "core_sections_html": """
        <h3>1. The Three Reference Types</h3>
        <ul>
          <li><strong>Strong (Default):</strong> Increments reference count by 1. Keeps object alive.</li>
          <li><strong>Weak:</strong> Does not increment count. Always optional (<code>weak var delegate: Delegate?</code>). Automatically zeroed to <code>nil</code> when target deallocates.</li>
          <li><strong>Unowned:</strong> Does not increment count. Assumes object never deallocates before the reference. <strong>Hazard:</strong> Accessing an unowned reference after target deallocates crashes immediately with <code>EXC_BAD_ACCESS</code>.</li>
        </ul>

        <h3>2. Closure Capture Lists</h3>
        <p>In Swift, closures capture variables by strong reference by default. Escaping closures stored on <code>self</code> create instant retain cycles unless broken with <code>[weak self]</code>:</p>
        <pre><code>class NetworkClient {
    var onResponse: (() -> Void)?

    func execute() {
        // ❌ Retain Cycle: self -> onResponse -> self
        // onResponse = { self.handleSuccess() }

        // ✅ Senior Solution: capture list
        onResponse = { [weak self] in
            guard let self else { return }
            self.handleSuccess()
        }
    }
    func handleSuccess() {}
}</code></pre>

        <h3>3. Autorelease Pools in High-Throughput Loops</h3>
        <p>When interacting with Objective-C bridged APIs (e.g. <code>UIImage</code>, <code>CoreGraphics</code>), temporary objects are added to the current runloop autorelease pool. In tight loops (processing 10,000 images), memory balloons until the runloop drains. Wrapping iterations in <code>autoreleasepool { ... }</code> drains memory immediately on each loop cycle.</p>
        """,
        "failure_mode_html": """
        <p><strong>Unowned Self in Asynchronous Callbacks:</strong> Using <code>[unowned self]</code> in network or timer callbacks assumes the view controller will still exist when the network returns. If the user dismisses the screen, the response callback fires on a deallocated instance, instantly crashing the app.</p>
        """,
        "quizzes": [
            {
                "question": "What happens if code attempts to read an unowned reference whose target instance was deallocated?",
                "correct_idx": 1,
                "options": [
                    {"text": "The reference returns nil safely.", "reason": "Option A is wrong because only weak references zero out to nil; unowned does not."},
                    {"text": "The application crashes with memory trap.", "reason": "Option B is correct: unowned leaves a dangling pointer; accessing it triggers a fatal runtime EXC_BAD_ACCESS crash."},
                    {"text": "The Darwin tracing GC reclaims it.", "reason": "Option C is wrong because iOS uses ARC, not a tracing garbage collector."},
                    {"text": "The compiler logs a runtime warning.", "reason": "Option D is wrong because memory safety violations trigger hard crash traps, not warnings."}
                ]
            },
            {
                "question": "Why is autoreleasepool { ... } utilized in high-iteration image processing loops?",
                "correct_idx": 0,
                "options": [
                    {"text": "Drains autoreleased memory immediately.", "reason": "Option A is correct: it releases Objective-C bridged temporary allocations on each iteration rather than waiting for runloop drain."},
                    {"text": "Compiles Swift code ahead of time.", "reason": "Option B is wrong because autoreleasepool is a runtime memory management construct."},
                    {"text": "Enforces thread safety across actors.", "reason": "Option C is wrong because autorelease pools operate per-thread on memory allocations, not synchronization."},
                    {"text": "Prevents retain cycles in closures.", "reason": "Option D is wrong because capture lists [weak self] break closure retain cycles, not autoreleasepool."}
                ]
            }
        ],
        "exercise": {
            "title": "Diagnosing and Breaking Retain Cycles",
            "instructions": "In <code>WorkerSession</code>, <code>completionHandler</code> captures <code>self</code> strongly, preventing <code>deinit</code> from firing. Fix the retain cycle using a closure capture list <code>[weak self]</code>. Run & Check to verify.",
            "starter_code": """import Foundation

final class WorkerSession {
    let id: String
    var completionHandler: (() -> Void)?
    static var deinitCount = 0

    init(id: String) {
        self.id = id
        // TODO: Fix retain cycle by using [weak self] in closure capture list
        self.completionHandler = {
            self.doWork()
        }
    }

    func doWork() {
        // Work simulation
    }

    deinit {
        WorkerSession.deinitCount += 1
    }
}

func runTests() {
    print("🧪 Running Lesson 1.3 Verification Tests...")
    WorkerSession.deinitCount = 0

    do {
        let session = WorkerSession(id: "sess_42")
        session.completionHandler?()
    }
    // Outside scope, session must deallocate immediately!
    assert(WorkerSession.deinitCount == 1, "Test Failed: WorkerSession leaked due to retain cycle!")
    print("  ✅ Challenge Passed: Retain cycle broken; deinit invoked successfully")
}
runTests()""",
            "hints": [
                "Change the closure to: self.completionHandler = { [weak self] in ... }",
                "Inside the closure, use 'guard let self else { return }' before calling doWork()."
            ],
            "solution_code": """import Foundation

final class WorkerSession {
    let id: String
    var completionHandler: (() -> Void)?
    static var deinitCount = 0

    init(id: String) {
        self.id = id
        self.completionHandler = { [weak self] in
            guard let self else { return }
            self.doWork()
        }
    }

    func doWork() {}

    deinit {
        WorkerSession.deinitCount += 1
    }
}

func runTests() {
    print("🧪 Running Lesson 1.3 Verification Tests...")
    WorkerSession.deinitCount = 0
    do {
        let session = WorkerSession(id: "sess_42")
        session.completionHandler?()
    }
    assert(WorkerSession.deinitCount == 1)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "By adding [weak self], the closure captures a non-owning optional pointer to WorkerSession. When the outer variable goes out of scope, the session's strong reference count drops to 0 and deinit runs deterministically."
        },
        "primary_sources": [
            {"title": "The Swift Programming Language: Automatic Reference Counting", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/automaticreferencecounting/", "desc": "Official Apple documentation on ARC and strong reference cycles."},
            {"title": "Apple Developer: autoreleasepool Documentation", "url": "https://developer.apple.com/documentation/foundation/1447281-autoreleasepool", "desc": "Official guide to draining autoreleased memory."}
        ],
        "coaching_prompt": "When is [unowned self] appropriate? Only when the closure and self have identical lifecycles (e.g. an internal private helper closure executed synchronously)."
    },

    # 0004: Lesson 1.4
    {
        "id": "0004",
        "slug": "0004-enums-and-pattern-matching",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.4",
        "title": "Enums with Associated Values & Algebraic State Modeling",
        "subtitle": "Sum types, recursive enums with indirect, pattern matching with switch and case let, and modeling bulletproof UI state machines.",
        "prev_url": "0003-arc-and-memory-safety.html",
        "next_url": "0005-protocol-oriented-programming.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin, complex state hierarchies are modeled using <code>sealed class</code> or <code>sealed interface</code> with data subclasses (each subclass allocating an object on the JVM heap).</p>
        <p>In Swift, enums are first-class <strong>algebraic sum types</strong> with associated values. They are value types stored inline without separate class allocations. Swift supports recursive enums via the <code>indirect</code> keyword.</p>
        """,
        "core_sections_html": """
        <h3>1. Exhaustive Algebraic State Modeling</h3>
        <p>Avoid boolean flag anti-patterns (<code>isLoading</code>, <code>isError</code>, <code>data</code> all optional). Model states mutually exclusively:</p>
        <pre><code>enum ViewState&lt;T: Equatable&gt;: Equatable {
    case idle
    case loading(message: String)
    case success(data: T)
    case failure(reason: String)
}</code></pre>

        <h3>2. Recursive Enums with <code>indirect</code></h3>
        <p>Enums need a fixed size at compile time. A recursive enum (like a binary tree or arithmetic expression) has infinite size unless boxed. The <code>indirect</code> modifier tells the compiler to insert a heap reference pointer (8 bytes) for recursive cases:</p>
        <pre><code>indirect enum BinaryTree&lt;T&gt; {
    case leaf(T)
    case node(left: BinaryTree&lt;T&gt;, right: BinaryTree&lt;T&gt;)
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Catch-All <code>default:</code> in Feature Switches:</strong> Adding <code>default:</code> to a switch statement on an internal enum disables compiler exhaustiveness checks. When a new enum case is added, the compiler will not warn you to handle it, leading to silent unhandled states.</p>
        """,
        "quizzes": [
            {
                "question": "What architectural problem occurs when adding a 'default:' case to a switch over an internal enum?",
                "correct_idx": 2,
                "options": [
                    {"text": "The binary size doubles instantly.", "reason": "Option A is wrong because switch statements do not double binary size."},
                    {"text": "The runtime throws an EXC_BAD_ACCESS.", "reason": "Option B is wrong because default compiles and runs fine."},
                    {"text": "Compiler exhaustiveness safety is disabled.", "reason": "Option C is correct: adding default prevents the compiler from flagging new unhandled cases added in future releases."},
                    {"text": "ARC decrements object pointers twice.", "reason": "Option D is wrong because enums do not participate in reference counting unless wrapping reference types."}
                ]
            },
            {
                "question": "Why does a recursive enum case require the indirect keyword in Swift?",
                "correct_idx": 0,
                "options": [
                    {"text": "Boxes recursive cases with heap pointers.", "reason": "Option A is correct: indirect inserts an 8-byte pointer box so the enum has a finite, fixed MemoryLayout size."},
                    {"text": "Makes the enum conform to Equatable.", "reason": "Option B is wrong because Equatable synthesis is separate from indirect memory boxing."},
                    {"text": "Enforces thread safety in actor methods.", "reason": "Option C is wrong because indirect affects memory layout, not concurrency synchronization."},
                    {"text": "Enables Objective-C runtime bridging.", "reason": "Option D is wrong because Swift enums with associated values cannot bridge to Objective-C."}
                ]
            }
        ],
        "exercise": {
            "title": "Algebraic UI State Machine & Reducer",
            "instructions": "Implement the <code>reduce</code> function for <code>AuthStateMachine</code>. When action is <code>.loginSuccess(let token)</code>, transition state to <code>.authenticated(token: token)</code>. When action is <code>.logout</code>, transition to <code>.unauthenticated</code>. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

enum AuthState: Equatable {
    case unauthenticated
    case authenticating
    case authenticated(token: String)
    case error(message: String)
}

enum AuthAction {
    case startLogin
    case loginSuccess(token: String)
    case loginFailure(error: String)
    case logout
}

func reduce(state: AuthState, action: AuthAction) -> AuthState {
    switch (state, action) {
    case (.unauthenticated, .startLogin):
        return .authenticating
    // TODO: Handle (.authenticating, .loginSuccess(let token)) -> return .authenticated(token: token)
    // TODO: Handle (.authenticating, .loginFailure(let err)) -> return .error(message: err)
    // TODO: Handle (.authenticated, .logout) -> return .unauthenticated
    default:
        return state
    }
}

func runTests() {
    print("🧪 Running Lesson 1.4 Verification Tests...")
    let s1 = reduce(state: .unauthenticated, action: .startLogin)
    assert(s1 == .authenticating, "Test 1 Failed")

    let s2 = reduce(state: s1, action: .loginSuccess(token: "jwt_token_999"))
    assert(s2 == .authenticated(token: "jwt_token_999"), "Test 2 Failed")

    let s3 = reduce(state: s2, action: .logout)
    assert(s3 == .unauthenticated, "Test 3 Failed")
    print("  ✅ Challenge Passed: Algebraic state transitions verified")
}
runTests()""",
            "hints": [
                "Add case (.authenticating, .loginSuccess(let token)): return .authenticated(token: token)",
                "Add case (.authenticating, .loginFailure(let err)): return .error(message: err)",
                "Add case (.authenticated, .logout): return .unauthenticated"
            ],
            "solution_code": """import Foundation

enum AuthState: Equatable {
    case unauthenticated
    case authenticating
    case authenticated(token: String)
    case error(message: String)
}

enum AuthAction {
    case startLogin
    case loginSuccess(token: String)
    case loginFailure(error: String)
    case logout
}

func reduce(state: AuthState, action: AuthAction) -> AuthState {
    switch (state, action) {
    case (.unauthenticated, .startLogin):
        return .authenticating
    case (.authenticating, .loginSuccess(let token)):
        return .authenticated(token: token)
    case (.authenticating, .loginFailure(let err)):
        return .error(message: err)
    case (.authenticated, .logout):
        return .unauthenticated
    default:
        return state
    }
}

func runTests() {
    print("🧪 Running Lesson 1.4 Verification Tests...")
    let s1 = reduce(state: .unauthenticated, action: .startLogin)
    assert(s1 == .authenticating)
    let s2 = reduce(state: s1, action: .loginSuccess(token: "jwt_token_999"))
    assert(s2 == .authenticated(token: "jwt_token_999"))
    let s3 = reduce(state: s2, action: .logout)
    assert(s3 == .unauthenticated)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Tuple pattern matching switch (state, action) enables clean 2D state-machine transitions. Invalid actions return current state without side effects, eliminating impossible UI states."
        },
        "primary_sources": [
            {"title": "The Swift Programming Language: Enumerations", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/enumerations/", "desc": "Official guide on associated values, recursive enums, and pattern matching."},
            {"title": "Swift Evolution SE-0192: Handling Future Enum Cases", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0192-non-exhaustive-enums.md", "desc": "Detailed explanation of @unknown default and exhaustiveness."}
        ],
        "coaching_prompt": "Notice how enums make impossible UI states unrepresentable. You cannot have both isLoading=true and data!=nil because each is an exclusive state case."
    },

    # 0005: Lesson 1.5
    {
        "id": "0005",
        "slug": "0005-protocol-oriented-programming",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.5",
        "title": "Protocol-Oriented Programming (POP) & Method Dispatch",
        "subtitle": "Witness Tables (PWT/VWT), Static vs Dynamic Dispatch, Protocol Extension Traps, and Opaque (some) vs Existential (any) Types.",
        "prev_url": "0004-enums-and-pattern-matching.html",
        "next_url": "0006-closures-and-error-handling.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Java and Kotlin, polymorphism is class-centric: methods on classes use <strong>vtable dispatch</strong> (virtual method table), and interfaces require JVM interface table lookup (itable). All interface instances are heap-boxed objects.</p>
        <p>Swift favors <strong>composition over inheritance</strong>. It uses <strong>Protocol Witness Tables (PWT)</strong> and <strong>Value Witness Tables (VWT)</strong> to resolve protocol requirements. Furthermore, Swift lets you choose compile-time static specialization (<code>some</code>) or runtime existential boxing (<code>any</code>).</p>
        """,
        "core_sections_html": """
        <h3>1. The Method Dispatch Spectrum</h3>
        <ul>
          <li><strong>Static (Direct) Dispatch:</strong> The fastest dispatch mechanism. The compiler inlines instructions directly into the caller. Used on structs, enums, final classes, and methods defined <em>only</em> in protocol extensions.</li>
          <li><strong>Table Dispatch:</strong> Pointer lookup through a function table. Used for class inheritance (vtable) and protocol requirements (PWT).</li>
          <li><strong>Message Send:</strong> Dynamic runtime lookup via <code>objc_msgSend</code>. Used for Objective-C dynamic features.</li>
        </ul>

        <h3>2. The Protocol Extension Dispatch Trap (Senior Interview Classic)</h3>
        <pre><code>protocol Worker {
    func execute() // Declared in protocol header -> PWT dispatch (Dynamic/Polymorphic)
}
extension Worker {
    func execute() { print("Default execute") }
    func inspect() { print("Default inspect") } // NOT in header -> Static dispatch!
}
struct LeadDeveloper: Worker {
    func execute() { print("Lead executing") }
    func inspect() { print("Lead inspecting") }
}

let w: Worker = LeadDeveloper()
w.execute() // Outputs: "Lead executing" (Resolved dynamically via PWT)
w.inspect() // Outputs: "Default inspect" (Bound statically to Worker extension!)</code></pre>

        <h3>3. Opaque Types (<code>some</code>) vs Existential Containers (<code>any</code>)</h3>
        <p><strong><code>some Shape</code>:</strong> Monomorphic. The concrete type is fixed at compile time. Static dispatch, zero allocation overhead.</p>
        <p><strong><code>any Shape</code>:</strong> Existential container. 5-word memory layout (3-word inline buffer + VWT + PWT). If payload > 24 bytes, Swift allocates heap memory.</p>
        """,
        "failure_mode_html": """
        <p><strong>Defaulting to 'any' Protocol Everywhere:</strong> In Swift 5.7+, using <code>any Service</code> incurs existential container boxing overhead on every call. Prefer <code>some Service</code> or generic constraints <code>&lt;T: Service&gt;</code> for zero-overhead static specialization.</p>
        """,
        "quizzes": [
            {
                "question": "Why does a method defined solely in a protocol extension use static dispatch rather than witness table dispatch?",
                "correct_idx": 0,
                "options": [
                    {"text": "No entry exists in witness table.", "reason": "Option A is correct: because the method was not declared in the protocol definition, no slot is allocated in the Protocol Witness Table."},
                    {"text": "Virtual methods strictly require classes.", "reason": "Option B is wrong because protocols can dispatch dynamically on structs using PWTs."},
                    {"text": "The compiler moves method to heap.", "reason": "Option C is wrong because static dispatch has zero heap involvement."},
                    {"text": "ARC memory safety rules disallow it.", "reason": "Option D is wrong because dispatch has nothing to do with ARC retain counts."}
                ]
            },
            {
                "question": "What is the memory size of a standard Swift existential container (any Protocol)?",
                "correct_idx": 3,
                "options": [
                    {"text": "Exactly one word (8 bytes).", "reason": "Option A is wrong because an existential needs more than an object pointer."},
                    {"text": "Exactly two words (16 bytes).", "reason": "Option B is wrong because 16 bytes only covers 2 words."},
                    {"text": "Exactly four words (32 bytes).", "reason": "Option C is wrong because 4 words is insufficient for 3-word buffer + 2 witness tables."},
                    {"text": "Exactly five words (40 bytes).", "reason": "Option D is correct: 3 words for inline value buffer + 1 word for VWT + 1 word for PWT."}
                ]
            }
        ],
        "exercise": {
            "title": "Protocol Witness Dispatch & Polymorphic Engine",
            "instructions": "Demonstrate understanding of the Protocol Extension Dispatch trap. Fix <code>PaymentProcessor</code> so that <code>refund()</code> is declared in the protocol contract, enabling dynamic witness dispatch when called through an existential type. Run tests to verify.",
            "starter_code": """import Foundation

protocol PaymentProcessor {
    func process(amount: Double) -> String
    // TODO: Declare func refund(amount: Double) -> String in the protocol header
    // so that it uses dynamic PWT dispatch rather than static extension dispatch!
}

extension PaymentProcessor {
    func refund(amount: Double) -> String {
        return "Default refund of $\\(amount)"
    }
}

struct StripeProcessor: PaymentProcessor {
    func process(amount: Double) -> String {
        return "Stripe charged $\\(amount)"
    }

    func refund(amount: Double) -> String {
        return "Stripe refunded $\\(amount)"
    }
}

func testDispatch(processor: any PaymentProcessor) -> String {
    return processor.refund(amount: 50.0)
}

func runTests() {
    print("🧪 Running Lesson 1.5 Verification Tests...")
    let stripe = StripeProcessor()
    let result = testDispatch(processor: stripe)

    // With refund declared in protocol header, dynamic PWT dispatch will invoke StripeProcessor's implementation!
    assert(result == "Stripe refunded $50.0", "Test Failed: Expected dynamic PWT dispatch, but received static fallback!")
    print("  ✅ Challenge Passed: Protocol Witness Table dynamic dispatch verified")
}
runTests()""",
            "hints": [
                "In protocol PaymentProcessor, add: func refund(amount: Double) -> String",
                "When declared in the protocol header, the method gets a slot in the Protocol Witness Table."
            ],
            "solution_code": """import Foundation

protocol PaymentProcessor {
    func process(amount: Double) -> String
    func refund(amount: Double) -> String
}

extension PaymentProcessor {
    func refund(amount: Double) -> String {
        return "Default refund of $\\(amount)"
    }
}

struct StripeProcessor: PaymentProcessor {
    func process(amount: Double) -> String {
        return "Stripe charged $\\(amount)"
    }

    func refund(amount: Double) -> String {
        return "Stripe refunded $\\(amount)"
    }
}

func testDispatch(processor: any PaymentProcessor) -> String {
    return processor.refund(amount: 50.0)
}

func runTests() {
    print("🧪 Running Lesson 1.5 Verification Tests...")
    let stripe = StripeProcessor()
    let result = testDispatch(processor: stripe)
    assert(result == "Stripe refunded $50.0")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Declaring refund in the protocol contract allocates a slot in the Protocol Witness Table. When called via an existential (any PaymentProcessor), the runtime looks up StripeProcessor's custom implementation via PWT instead of defaulting to static extension dispatch."
        },
        "primary_sources": [
            {"title": "WWDC Session: Understanding Swift Performance", "url": "https://developer.apple.com/videos/play/wwdc2016/416/", "desc": "Canonical Apple talk on structs, existential containers, and witness tables."},
            {"title": "Swift Evolution SE-0335: Introduce Existential any", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0335-existential-any.md", "desc": "Design rationale for explicit any keyword."}
        ],
        "coaching_prompt": "Remember: methods declared only in extensions are statically dispatched. Methods declared in the protocol signature are dynamically dispatched via witness tables."
    },

    # 0006: Lesson 1.6
    {
        "id": "0006",
        "slug": "0006-closures-and-error-handling",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.6",
        "title": "Closures, Capture Semantics & Defensive Error Handling",
        "subtitle": "Escaping closures (@escaping), autoclosures (@autoclosure), typed throws (Swift 6), rethrows, and deterministic LIFO defer cleanup.",
        "prev_url": "0005-protocol-oriented-programming.html",
        "next_url": "0007-generics-and-metaprogramming.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin, all lambdas are objects on the JVM heap (unless inlined via <code>inline fun</code>). Exceptions are unchecked at runtime (no checked exceptions exist in Kotlin).</p>
        <p>In Swift, closures are <strong>non-escaping by default</strong>: they cannot outlive the function call, allowing the compiler to allocate them on the stack with zero heap allocation! Only when marked <code>@escaping</code> do they escape to the heap. Furthermore, Swift error handling is compile-time checked with <code>throws</code>, Swift 6 typed throws <code>throws(AppError)</code>, and deterministic <code>defer</code> blocks executed in LIFO order.</p>
        """,
        "core_sections_html": """
        <h3>1. Escaping vs Non-Escaping Closures</h3>
        <ul>
          <li><strong>Non-Escaping (Default):</strong> Executed synchronously within the function. Guaranteed not to escape. Does not require <code>[weak self]</code> because retain cycles are impossible!</li>
          <li><strong><code>@escaping</code>:</strong> Stored in a property or dispatched asynchronously. Escapes the current scope. Retains referenced objects; requires explicit capture list.</li>
          <li><strong><code>@autoclosure</code>:</strong> Automatically wraps an expression argument into a zero-argument closure, delaying evaluation (used by <code>assert</code> and <code>??</code>).</li>
        </ul>

        <h3>2. Swift 6 Typed Throws</h3>
        <pre><code>enum NetworkError: Error, Equatable {
    case unauthenticated
    case rateLimited(retryAfter: Int)
}

// Swift 6 Typed Throws: caller knows exact concrete error type!
func executeRequest() throws(NetworkError) -> String {
    throw NetworkError.rateLimited(retryAfter: 30)
}</code></pre>

        <h3>3. Deterministic <code>defer</code> Cleanup (LIFO Order)</h3>
        <p>A <code>defer</code> block executes whenever execution leaves the current scope (via return, throw, or break). Multiple defer blocks execute in Last-In First-Out (LIFO) stack order:</p>
        <pre><code>func processFile() {
    openFile()
    defer { closeFile() } // Executes LAST
    defer { flushBuffer() } // Executes FIRST upon exit
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Swallowing Errors with <code>try?</code>:</strong> Using <code>try?</code> indiscriminately converts any thrown error into <code>nil</code>, discarding critical debugging info, telemetry, and error categorization. Use <code>do { try ... } catch { ... }</code> or typed throws in production.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary performance benefit of non-escaping closures being the default in Swift?",
                "correct_idx": 1,
                "options": [
                    {"text": "Enforces thread safety in actor pools.", "reason": "Option A is wrong because closure escaping rules govern lifetime, not thread locks."},
                    {"text": "Allocates closure memory directly on stack.", "reason": "Option B is correct: because the closure cannot outlive the call, the compiler avoids heap allocation and reference counting."},
                    {"text": "Prevents any error throwing from closure.", "reason": "Option C is wrong because non-escaping closures can throw errors freely."},
                    {"text": "Enables automatic Objective-C runtime export.", "reason": "Option D is wrong because escaping rules do not alter ObjC selectors."}
                ]
            },
            {
                "question": "In what order do multiple defer blocks within the same scope execute when exiting?",
                "correct_idx": 2,
                "options": [
                    {"text": "In first-in first-out order (FIFO).", "reason": "Option A is wrong because defer behaves as a stack, not a queue."},
                    {"text": "In random non-deterministic thread order.", "reason": "Option B is wrong because defer is strictly deterministic and synchronous."},
                    {"text": "In last-in first-out order (LIFO).", "reason": "Option C is correct: reverse order of declaration (LIFO stack unwinding)."},
                    {"text": "Only the final declared defer executes.", "reason": "Option D is wrong because all valid defer blocks execute."}
                ]
            }
        ],
        "exercise": {
            "title": "Typed Throws & LIFO Resource Acquisition Is Initialization (RAII)",
            "instructions": "Implement <code>executeSecureTransaction</code>. Ensure it acquires a lock, sets up <code>defer</code> to release the lock, and throws typed error <code>TransactionError.insufficientFunds</code> when amount exceeds balance. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

enum TransactionError: Error, Equatable {
    case insufficientFunds
    case accountLocked
}

final class BankVault {
    var balance: Double = 500.0
    var isLocked: Bool = false
    var auditLog: [String] = []

    func acquireLock() {
        isLocked = true
        auditLog.append("LOCK_ACQUIRED")
    }

    func releaseLock() {
        isLocked = false
        auditLog.append("LOCK_RELEASED")
    }

    // TODO: Implement executeSecureTransaction(amount: Double) throws(TransactionError) -> Double
    // 1. acquireLock()
    // 2. Use defer { releaseLock() } to guarantee lock is ALWAYS released even on error!
    // 3. If amount > balance, throw TransactionError.insufficientFunds
    // 4. Otherwise subtract amount and return new balance
    func executeSecureTransaction(amount: Double) throws(TransactionError) -> Double {
        fatalError("TODO: Implement executeSecureTransaction")
    }
}

func runTests() {
    print("🧪 Running Lesson 1.6 Verification Tests...")
    let vault = BankVault()

    // Test 1: Successful Transaction
    do {
        let newBal = try vault.executeSecureTransaction(amount: 200.0)
        assert(newBal == 300.0, "Test 1A Failed")
        assert(vault.isLocked == false, "Test 1B Failed: Lock not released")
        assert(vault.auditLog == ["LOCK_ACQUIRED", "LOCK_RELEASED"], "Test 1C Failed")
        print("  ✅ Challenge 1 Passed: Successful transaction with defer cleanup")
    } catch {
        fatalError("Unexpected error: \\(error)")
    }

    // Test 2: Error path with defer release
    vault.auditLog.removeAll()
    do {
        _ = try vault.executeSecureTransaction(amount: 999.0)
        fatalError("Test 2 Failed: Should have thrown insufficientFunds")
    } catch TransactionError.insufficientFunds {
        assert(vault.isLocked == false, "Test 2B Failed: Lock not released on error exit!")
        assert(vault.auditLog == ["LOCK_ACQUIRED", "LOCK_RELEASED"], "Test 2C Failed: Defer not executed on throw")
        print("  ✅ Challenge 2 Passed: Typed throws with guaranteed defer cleanup")
    } catch {
        fatalError("Unexpected error type: \\(error)")
    }
}
runTests()""",
            "hints": [
                "Start with vault: acquireLock()",
                "Immediately write: defer { releaseLock() }",
                "Check: guard amount <= balance else { throw TransactionError.insufficientFunds }"
            ],
            "solution_code": """import Foundation

enum TransactionError: Error, Equatable {
    case insufficientFunds
    case accountLocked
}

final class BankVault {
    var balance: Double = 500.0
    var isLocked: Bool = false
    var auditLog: [String] = []

    func acquireLock() {
        isLocked = true
        auditLog.append("LOCK_ACQUIRED")
    }

    func releaseLock() {
        isLocked = false
        auditLog.append("LOCK_RELEASED")
    }

    func executeSecureTransaction(amount: Double) throws(TransactionError) -> Double {
        acquireLock()
        defer {
            releaseLock()
        }
        guard amount <= balance else {
            throw TransactionError.insufficientFunds
        }
        balance -= amount
        return balance
    }
}

func runTests() {
    print("🧪 Running Lesson 1.6 Verification Tests...")
    let vault = BankVault()
    do {
        let newBal = try vault.executeSecureTransaction(amount: 200.0)
        assert(newBal == 300.0)
        assert(vault.isLocked == false)
    } catch {
        fatalError()
    }
    vault.auditLog.removeAll()
    do {
        _ = try vault.executeSecureTransaction(amount: 999.0)
        fatalError()
    } catch TransactionError.insufficientFunds {
        assert(vault.isLocked == false)
        assert(vault.auditLog == ["LOCK_ACQUIRED", "LOCK_RELEASED"])
        print("  ✅ All tests passed!")
    } catch {
        fatalError()
    }
}
runTests()""",
            "solution_explanation": "Swift 6 typed throws (throws(TransactionError)) enforces exhaustiveness at the callsite. The defer block guarantees that releaseLock() is executed as the stack unwinds, regardless of whether the function returns normally or throws an error."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0413: Typed Throws", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0413-typed-throws.md", "desc": "Official specification of Swift 6 typed throws."},
            {"title": "The Swift Programming Language: Closures", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/closures/", "desc": "Apple documentation on escaping closures and autoclosures."}
        ],
        "coaching_prompt": "Notice how defer prevents lock leaks and resource leaks even during unexpected early returns or throws."
    },

    # 0007: Lesson 1.7
    {
        "id": "0007",
        "slug": "0007-generics-and-metaprogramming",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.7",
        "title": "Generics, Associated Types & Type Erasure",
        "subtitle": "Generic specialization, associatedtype, primary associated types, and building type-erasing AnyRepository wrappers.",
        "prev_url": "0006-closures-and-error-handling.html",
        "next_url": "0008-property-wrappers-and-macros.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Kotlin/Java, generics suffer from <strong>type erasure</strong>: at runtime, all generic type parameters (like <code>List&lt;User&gt;</code>) are erased to <code>List&lt;Object&gt;</code>, requiring JVM boxing for primitives.</p>
        <p>In Swift, generics are <strong>reified and specialized at compile time</strong>: when you write <code>Array&lt;Int&gt;</code>, the compiler monomorphizes and generates specialized machine code with direct scalar 64-bit storage and zero runtime boxing overhead!</p>
        """,
        "core_sections_html": """
        <h3>1. Protocols with Associated Types (PATs)</h3>
        <pre><code>protocol Repository&lt;Item&gt; { // Primary associated type syntax (Swift 5.7+)
    associatedtype Item: Identifiable
    func fetch(id: String) -> Item?
    func save(item: Item)
}</code></pre>

        <h3>2. Type Erasure: Why and How (Senior Interview Favorite)</h3>
        <p>Historically, protocols with associated types could not be used as standalone types (<code>var repo: Repository</code> threw 'can only be used as a generic constraint'). While Swift 5.7 introduced <code>any Repository&lt;User&gt;</code>, Senior engineers still use manual type erasers (like Combine's <code>AnyPublisher</code> or <code>AnyRepository</code>) to hide concrete implementation classes across SPM module boundaries:</p>
        <pre><code>final class AnyRepository&lt;Item: Identifiable&gt;: Repository {
    private let _fetch: (String) -> Item?
    private let _save: (Item) -> Void

    init&lt;R: Repository&gt;(_ repository: R) where R.Item == Item {
        self._fetch = repository.fetch
        self._save = repository.save
    }

    func fetch(id: String) -> Item? { _fetch(id) }
    func save(item: Item) { _save(item) }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Code Bloat via Over-Specialization:</strong> Generic specialization generates new machine instructions for each concrete type combination. While blazingly fast, excessive indiscriminate generic specialization across 50+ types can increase application binary size. Balance with opaque types (<code>some</code>).</p>
        """,
        "quizzes": [
            {
                "question": "What is generic specialization in the Swift compiler?",
                "correct_idx": 0,
                "options": [
                    {"text": "Generates concrete machine code for types.", "reason": "Option A is correct: the compiler emits specialized type-specific functions (e.g. for Int, Double), eliminating runtime dynamic boxing."},
                    {"text": "Erases all generic types down to AnyObject.", "reason": "Option B is wrong because that describes JVM type erasure, the exact opposite of Swift specialization."},
                    {"text": "Boxes all values into existential containers.", "reason": "Option C is wrong because existential boxing occurs with 'any', not specialized generics."},
                    {"text": "Forces all generic calls through vtables.", "reason": "Option D is wrong because specialized generic calls are inlined with static dispatch."}
                ]
            },
            {
                "question": "What architectural role does a custom AnyRepository<T> type eraser fulfill?",
                "correct_idx": 3,
                "options": [
                    {"text": "Converts reference types into value types.", "reason": "Option A is wrong because type erasers do not alter value/reference semantics."},
                    {"text": "Automates SQLite database schema migrations.", "reason": "Option B is wrong because type erasure is a type-system pattern, not a database ORM."},
                    {"text": "Enforces thread safety in actor methods.", "reason": "Option C is wrong because type erasure is orthogonal to concurrency."},
                    {"text": "Hides underlying concrete implementation types.", "reason": "Option D is correct: it encapsulates a concrete generic conformance behind closures, presenting a clean uniform API across module boundaries."}
                ]
            }
        ],
        "exercise": {
            "title": "Type-Erasing Repository Wrapper",
            "instructions": "Complete the implementation of <code>AnyDataStore&lt;T&gt;</code> by storing closure forwarding wrappers in its initializer. Replace the <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

protocol DataStore<T> {
    associatedtype T
    func retrieve() -> T
}

struct InMemoryDataStore<T>: DataStore {
    let value: T
    func retrieve() -> T { value }
}

final class AnyDataStore<T>: DataStore {
    private let _retrieve: () -> T

    // TODO: Implement init<D: DataStore>(_ store: D) where D.T == T
    // Store the closure { store.retrieve() } into self._retrieve
    init<D: DataStore>(_ store: D) where D.T == T {
        fatalError("TODO: Implement AnyDataStore init")
    }

    func retrieve() -> T {
        _retrieve()
    }
}

func runTests() {
    print("🧪 Running Lesson 1.7 Verification Tests...")
    let concreteStore = InMemoryDataStore(value: "Senior iOS Engineer")
    let erasedStore = AnyDataStore(concreteStore)

    assert(erasedStore.retrieve() == "Senior iOS Engineer", "Test Failed: Erased store did not return expected value")
    print("  ✅ Challenge Passed: Type-erased wrapper verified")
}
runTests()""",
            "hints": [
                "Inside the init, write: self._retrieve = { store.retrieve() }",
                "Or simply: self._retrieve = store.retrieve"
            ],
            "solution_code": """import Foundation

protocol DataStore<T> {
    associatedtype T
    func retrieve() -> T
}

struct InMemoryDataStore<T>: DataStore {
    let value: T
    func retrieve() -> T { value }
}

final class AnyDataStore<T>: DataStore {
    private let _retrieve: () -> T

    init<D: DataStore>(_ store: D) where D.T == T {
        self._retrieve = store.retrieve
    }

    func retrieve() -> T {
        _retrieve()
    }
}

func runTests() {
    print("🧪 Running Lesson 1.7 Verification Tests...")
    let concreteStore = InMemoryDataStore(value: "Senior iOS Engineer")
    let erasedStore = AnyDataStore(concreteStore)
    assert(erasedStore.retrieve() == "Senior iOS Engineer")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Type erasure wraps the concrete store.retrieve method pointer into an internal closure _retrieve: () -> T. Callers interact exclusively with AnyDataStore<T>, decoupling clients from specific storage implementations."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0346: Lightweight Associated Type Constraints", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0346-light-weight-same-type-syntax.md", "desc": "Official specification for primary associated types syntax (Protocol<T>)."},
            {"title": "The Swift Programming Language: Generics", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/generics/", "desc": "Apple canonical guide on associated types and where clauses."}
        ],
        "coaching_prompt": "Think of type erasure as boxing with intent: it trades static compile-time specialization for dynamic composability when crossing modular boundaries."
    },

    # 0008: Lesson 1.8
    {
        "id": "0008",
        "slug": "0008-property-wrappers-and-macros",
        "phase_num": 1,
        "phase_title": "Phase 1: Swift Compiler & Language Internals",
        "lesson_num": "Lesson 1.8",
        "title": "Metaprogramming: Property Wrappers, KeyPaths & Swift Macros",
        "subtitle": "@propertyWrapper mechanics, projected values ($), KeyPaths, and compile-time Swift Macros vs Kotlin KSP.",
        "prev_url": "0007-generics-and-metaprogramming.html",
        "next_url": "../lessons/0009-concurrency-fundamentals.html",
        "cheatsheet_url": "../reference/swift-syntax-cheatsheet.html",
        "cheatsheet_title": "Swift Syntax Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), compile-time metaprogramming relies on <strong>Kotlin Symbol Processing (KSP)</strong> or annotation processors (kapt) which generate separate source files during Gradle build. Kotlin also has property delegation (<code>by lazy</code>, <code>by Delegates.observable</code>).</p>
        <p>In Swift, property delegation is achieved via <strong><code>@propertyWrapper</code></strong> (backing <code>@State</code> and <code>@Binding</code>). Furthermore, Swift 5.9+ introduced <strong>Swift Macros</strong> (e.g. <code>@Observable</code>, <code>@Model</code>): they operate directly on the Abstract Syntax Tree (AST) at compile time without reflection!</p>
        """,
        "core_sections_html": """
        <h3>1. Property Wrapper Anatomy</h3>
        <pre><code>@propertyWrapper
struct Clamped&lt;T: Comparable&gt; {
    private var value: T
    let range: ClosedRange&lt;T&gt;

    init(wrappedValue: T, _ range: ClosedRange&lt;T&gt;) {
        self.range = range
        self.value = min(max(wrappedValue, range.lowerBound), range.upperBound)
    }

    var wrappedValue: T {
        get { value }
        set { value = min(max(newValue, range.lowerBound), range.upperBound) }
    }

    // Projected value exposed via '$' prefix operator
    var projectedValue: Bool {
        value == range.lowerBound || value == range.upperBound
    }
}

struct AudioSettings {
    @Clamped(0...100) var volume: Int = 50
}
var audio = AudioSettings()
audio.volume = 150 // Clamped to 100!
print(audio.$volume) // true (projectedValue indicates clamped at boundary)</code></pre>

        <h3>2. Type-Safe KeyPaths</h3>
        <p>KeyPaths (<code>\\User.email</code>) are strongly typed property references. Senior engineers use them for sorting, filtering, and functional transforms without closure overhead:</p>
        <pre><code>extension Sequence {
    func sorted&lt;T: Comparable&gt;(by keyPath: KeyPath&lt;Element, T&gt;) -> [Element] {
        sorted { $0[keyPath: keyPath] < $1[keyPath: keyPath] }
    }
}</code></pre>

        <h3>3. Swift Macros (Compile-Time AST Manipulation)</h3>
        <p>Unlike runtime reflection or source generation, Swift Macros expand directly into the compiler AST during compilation. They are sandboxed, deterministic, and inspectable in Xcode.</p>
        """,
        "failure_mode_html": """
        <p><strong>Overusing Property Wrappers for Heavy Business Logic:</strong> Property wrappers execute on property get/set. Putting blocking I/O, network requests, or database transactions inside a property wrapper creates silent performance stalls during simple property assignments.</p>
        """,
        "quizzes": [
            {
                "question": "What property on a @propertyWrapper struct is accessed when prefixing the variable with a dollar sign ($)?",
                "correct_idx": 1,
                "options": [
                    {"text": "The wrappedValue stored payload.", "reason": "Option A is wrong because wrappedValue is accessed through the bare variable name without $."},
                    {"text": "The projectedValue custom projection.", "reason": "Option B is correct: prefixing with $ accesses the projectedValue (e.g. Binding in SwiftUI)."},
                    {"text": "The memory address raw pointer.", "reason": "Option C is wrong because pointers require UnsafePointer or & syntax."},
                    {"text": "The dynamic witness table header.", "reason": "Option D is wrong because property wrappers are standard structs with no dynamic dispatch headers."}
                ]
            },
            {
                "question": "How do Swift 5.9+ Macros differ fundamentally from Android Java reflection?",
                "correct_idx": 0,
                "options": [
                    {"text": "Operate at compile time on the AST.", "reason": "Option A is correct: Swift Macros are compiled executables that transform the compiler Abstract Syntax Tree with zero runtime reflection overhead."},
                    {"text": "Inspect class metadata at runtime.", "reason": "Option B is wrong because that is how Java reflection operates."},
                    {"text": "Are restricted strictly to unit tests.", "reason": "Option C is wrong because macros are used in production code (@Observable, @Model)."},
                    {"text": "Require JVM bytecode interpreter runs.", "reason": "Option D is wrong because Swift compiles to native Mach-O ARM64 binaries."}
                ]
            }
        ],
        "exercise": {
            "title": "Custom Property Wrapper & KeyPath Sorter",
            "instructions": "Implement the <code>Trimmed</code> property wrapper. In its setter and initializer, strip all leading and trailing whitespace from the string. Implement the <code>projectedValue</code> to return true if any trimming occurred. Run tests.",
            "starter_code": """import Foundation

@propertyWrapper
struct Trimmed {
    private var text: String
    private(set) var wasTrimmed: Bool = false

    init(wrappedValue: String) {
        // TODO: Clean whitespace and set wasTrimmed to true if cleaned != wrappedValue
        fatalError("TODO: Implement Trimmed init")
    }

    var wrappedValue: String {
        get { text }
        set {
            // TODO: Clean whitespace on newValue and update wasTrimmed
            fatalError("TODO: Implement Trimmed setter")
        }
    }

    var projectedValue: Bool {
        wasTrimmed
    }
}

struct UserInput {
    @Trimmed var username: String
}

func runTests() {
    print("🧪 Running Lesson 1.8 Verification Tests...")
    var input = UserInput(username: "  AlexiOS  ")
    assert(input.username == "AlexiOS", "Test 1 Failed: Whitespace not trimmed")
    assert(input.$username == true, "Test 2 Failed: Projected value wasTrimmed should be true")

    input.username = "CleanName"
    assert(input.username == "CleanName", "Test 3 Failed")
    assert(input.$username == false, "Test 4 Failed: Projected value should be false for clean name")
    print("  ✅ Challenge Passed: Custom property wrapper and projectedValue verified")
}
runTests()""",
            "hints": [
                "Use wrappedValue.trimmingCharacters(in: .whitespacesAndNewlines)",
                "Compare cleaned != wrappedValue to set wasTrimmed."
            ],
            "solution_code": """import Foundation

@propertyWrapper
struct Trimmed {
    private var text: String
    private(set) var wasTrimmed: Bool = false

    init(wrappedValue: String) {
        let cleaned = wrappedValue.trimmingCharacters(in: .whitespacesAndNewlines)
        self.text = cleaned
        self.wasTrimmed = (cleaned != wrappedValue)
    }

    var wrappedValue: String {
        get { text }
        set {
            let cleaned = newValue.trimmingCharacters(in: .whitespacesAndNewlines)
            self.text = cleaned
            self.wasTrimmed = (cleaned != newValue)
        }
    }

    var projectedValue: Bool {
        wasTrimmed
    }
}

struct UserInput {
    @Trimmed var username: String
}

func runTests() {
    print("🧪 Running Lesson 1.8 Verification Tests...")
    var input = UserInput(username: "  AlexiOS  ")
    assert(input.username == "AlexiOS")
    assert(input.$username == true)
    input.username = "CleanName"
    assert(input.username == "CleanName")
    assert(input.$username == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Property wrappers encapsulate property read/write transformations. By providing projectedValue, consumers can query auxiliary state using the dollar-sign prefix ($input.username) cleanly."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0258: Property Wrappers", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0258-property-wrappers.md", "desc": "Canonical specification of property wrappers and projected values."},
            {"title": "Swift Evolution SE-0389: Attached Macros", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0389-attached-macros.md", "desc": "Official proposal defining Swift compile-time AST macros."}
        ],
        "coaching_prompt": "Congratulations on completing Phase 1! You now possess a rigorous mental model of Swift compiler mechanics, memory layout, ARC, POP, and metaprogramming. Next stop: Phase 2 Concurrency!"
    }
]

print("Phase 1 data loaded: 8 lessons.")
