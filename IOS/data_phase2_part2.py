# Phase 2 Part 2: Lessons 0014 - 0018

from data_phase2 import PHASE2_LESSONS

PHASE2_PART2 = [
    # 0014: Lesson 2.6
    {
        "id": "0014",
        "slug": "0014-swift6-strict-concurrency-and-sendable",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.6",
        "title": "Swift 6 Strict Concurrency, Region-Based Isolation & Sendable",
        "subtitle": "Compile-time data-race safety, Sendable protocols, non-Sendable transfer rules, and migrating legacy codebases.",
        "prev_url": "0013-global-actors-and-mainactor.html",
        "next_url": "0015-combine-essentials-and-async.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), thread safety is enforced by convention. Mutable class references can be passed freely across Coroutine contexts (<code>withContext(Dispatchers.Default)</code>), risking data races unless developer manually applies synchronization primitives.</p>
        <p>In Swift 6, data-race safety is <strong>enforced by the compiler at compile time</strong>. Types passed across actor or task boundaries must conform to <strong><code>Sendable</code></strong>. Swift 6 introduces <strong>Region-Based Isolation (SE-0414)</strong>: non-Sendable values can cross concurrency boundaries as long as the compiler proves no other reference to that memory region exists!</p>
        """,
        "core_sections_html": """
        <h3>1. The Sendable Protocol</h3>
        <p>A marker protocol indicating values can be safely transferred across threads:</p>
        <ul>
          <li><strong>Value Types (structs, enums):</strong> Implicitly <code>Sendable</code> if all stored properties are Sendable.</li>
          <li><strong>Actors:</strong> Implicitly <code>Sendable</code> because access to mutable state is serialized.</li>
          <li><strong>Classes:</strong> Must be <code>final</code>, contain only immutable <code>let</code> Sendable properties, or use internal synchronization with <code>@unchecked Sendable</code>.</li>
        </ul>

        <h3>2. Region-Based Isolation (Swift 6 SE-0414)</h3>
        <p>In Swift 5, sending a non-Sendable class into a Task was an immediate error. In Swift 6, the compiler tracks the memory region of variables. If you create an object and pass it to a background Task, but never access it again in the caller, Swift 6 proves there is no concurrent overlap and allows the transfer safely!</p>
        """,
        "failure_mode_html": """
        <p><strong>Overusing <code>@unchecked Sendable</code> to Silence Compiler Warnings:</strong> Slapping <code>@unchecked Sendable</code> on mutable classes tells the compiler "trust me, I synchronized this manually." If you don't actually implement thread synchronization (locks or serial queues), you reintroduce silent memory corruption.</p>
        """,
        "quizzes": [
            {
                "question": "What does conforming a type to Sendable communicate to the Swift 6 compiler?",
                "correct_idx": 1,
                "options": [
                    {"text": "The type can serialize into JSON data.", "reason": "Option A is wrong because JSON serialization is handled by Codable."},
                    {"text": "Values can safely cross thread boundaries.", "reason": "Option B is correct: Sendable proves values can be transferred across concurrency domains without data races."},
                    {"text": "Instances allocate memory on the stack.", "reason": "Option C is wrong because Sendable classes still allocate on heap."},
                    {"text": "All method calls use dynamic dispatch.", "reason": "Option D is wrong because Sendable does not alter dispatch mechanics."}
                ]
            },
            {
                "question": "How does Swift 6 Region-Based Isolation (SE-0414) improve on Swift 5 concurrency?",
                "correct_idx": 0,
                "options": [
                    {"text": "Allows non-Sendable transfer if unshared.", "reason": "Option A is correct: if the compiler proves the caller discards all references, it permits sending non-Sendable values safely."},
                    {"text": "Replaces the ARC memory management runtime.", "reason": "Option B is wrong because Swift 6 still relies on ARC."},
                    {"text": "Automatically converts classes into structs.", "reason": "Option C is wrong because Swift never changes class types into structs."},
                    {"text": "Eliminates all needs for actor keywords.", "reason": "Option D is wrong because actors remain essential for shared mutable state."}
                ]
            }
        ],
        "exercise": {
            "title": "Sendable State Transfer & Thread-Safe Token Container",
            "instructions": "Make <code>SecurityToken</code> Sendable by marking it <code>final</code> and making its properties immutable <code>let</code>. Implement <code>transferToken</code> to safely pass the token to a background task and return its hash. Run tests.",
            "starter_code": """import Foundation

// TODO: Fix SecurityToken so it is safe under Swift 6 strict concurrency
// 1. Mark as final class
// 2. Conform to Sendable
// 3. Make all properties immutable 'let' constants
class SecurityToken {
    var rawToken: String
    var expiresAtEpoch: Int

    init(rawToken: String, expiresAtEpoch: Int) {
        self.rawToken = rawToken
        self.expiresAtEpoch = expiresAtEpoch
    }
}

func transferToken(_ token: SecurityToken) async -> String {
    // Spawn a child task sending the token across concurrency domain:
    let task = Task {
        return "validated_\\(token.rawToken)"
    }
    return await task.value
}

func runTests() async {
    print("🧪 Running Lesson 2.6 Verification Tests...")
    let token = SecurityToken(rawToken: "sec_token_999", expiresAtEpoch: 5000)
    let result = await transferToken(token)

    assert(result == "validated_sec_token_999", "Test 1 Failed: Token transfer failed")
    print("  ✅ Challenge Passed: Sendable token transfer verified")
}

await runTests()""",
            "hints": [
                "Mark class: final class SecurityToken: Sendable",
                "Change properties from var to let: let rawToken: String; let expiresAtEpoch: Int"
            ],
            "solution_code": """import Foundation

final class SecurityToken: Sendable {
    let rawToken: String
    let expiresAtEpoch: Int

    init(rawToken: String, expiresAtEpoch: Int) {
        self.rawToken = rawToken
        self.expiresAtEpoch = expiresAtEpoch
    }
}

func transferToken(_ token: SecurityToken) async -> String {
    let task = Task {
        return "validated_\\(token.rawToken)"
    }
    return await task.value
}

func runTests() async {
    print("🧪 Running Lesson 2.6 Verification Tests...")
    let token = SecurityToken(rawToken: "sec_token_999", expiresAtEpoch: 5000)
    let result = await transferToken(token)
    assert(result == "validated_sec_token_999")
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "For a class to conform to Sendable, it must be final (preventing subclass state injection) and have immutable let stored properties that are themselves Sendable. This guarantees zero concurrent data races when passed across Tasks."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0414: Region-based Isolation", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0414-region-based-isolation.md", "desc": "Canonical specification for non-Sendable transfer in Swift 6."},
            {"title": "The Swift Programming Language: Sendable Types", "url": "https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/#Sendable-Types", "desc": "Official Apple guide to Sendable conformance."}
        ],
        "coaching_prompt": "Value types (structs and enums) with Sendable properties are automatically Sendable. That is another reason why Swift prefers structs over classes!"
    },

    # 0015: Lesson 2.7
    {
        "id": "0015",
        "slug": "0015-combine-essentials-and-async",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.7",
        "title": "Combine Essentials & Bridging to Swift Concurrency",
        "subtitle": "Publishers, AnyCancellable, sink retain cycle traps, and bridging Combine streams into async/await via .values.",
        "prev_url": "0014-swift6-strict-concurrency-and-sendable.html",
        "next_url": "0016-production-urlsession.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), reactive event streams use <code>Flow</code> or <code>RxJava</code>. Subscriptions are retained via <code>CompositeDisposable</code> or Coroutine jobs.</p>
        <p>In iOS, Apple introduced <strong>Combine</strong> in iOS 13 (analogous to Kotlin Flow). In production enterprise iOS codebases, millions of lines of Combine still exist. Senior engineers must master Combine operators, avoid retain cycles with <code>AnyCancellable</code>, and bridge Combine publishers into modern Swift Concurrency using <strong><code>publisher.values</code></strong>.</p>
        """,
        "core_sections_html": """
        <h3>1. The Combine Subscription Lifecycle</h3>
        <pre><code>import Combine

final class SearchService {
    @Published var searchQuery: String = ""
    private var cancellables = Set&lt;AnyCancellable&gt;()

    func setupDebounce() {
        $searchQuery
            .debounce(for: .milliseconds(300), scheduler: RunLoop.main)
            .removeDuplicates()
            .sink { [weak self] query in
                self?.performSearch(query: query)
            }
            .store(in: &cancellables) // Retains subscription
    }
    func performSearch(query: String) {}
}</code></pre>

        <h3>2. Bridging Combine to AsyncSequence (.values)</h3>
        <p>Instead of registering <code>.sink</code> closures, modern Swift Concurrency lets you iterate over any Combine Publisher using <code>for await value in publisher.values</code>:</p>
        <pre><code>func observeQueryStream(service: SearchService) async {
    for await query in service.$searchQuery.values {
        print("Received async query: \\(query)")
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Omitting [weak self] in Combine .sink:</strong> The <code>.sink</code> closure retains <code>self</code> if called directly. Storing the returned <code>AnyCancellable</code> in <code>self.cancellables</code> creates an immediate retain cycle (self -> cancellables -> subscription -> sink -> self). Always use <code>[weak self]</code> in sink closures.</p>
        """,
        "quizzes": [
            {
                "question": "What happens if a Combine AnyCancellable is deallocated from memory?",
                "correct_idx": 1,
                "options": [
                    {"text": "The stream stays active in the background.", "reason": "Option A is wrong because deallocation automatically cancels the subscription."},
                    {"text": "The subscription is cancelled immediately.", "reason": "Option B is correct: AnyCancellable calls cancel() on deinit, terminating the stream."},
                    {"text": "The Darwin operating system throws crash.", "reason": "Option C is wrong because cancel on deinit is safe and standard."},
                    {"text": "All downstream operators allocate memory.", "reason": "Option D is wrong because downstream resources are released."}
                ]
            },
            {
                "question": "How can you consume a Combine Publisher inside modern Swift Concurrency?",
                "correct_idx": 0,
                "options": [
                    {"text": "Iterate over publisher.values with for await.", "reason": "Option A is correct: the .values property converts any Combine Publisher into an AsyncSequence."},
                    {"text": "Dispatch to DispatchQueue.global().sync.", "reason": "Option B is wrong because synchronous blocking starves the thread pool."},
                    {"text": "Convert publisher to UnsafePointer.", "reason": "Option C is wrong because pointers have nothing to do with reactive streams."},
                    {"text": "Wrap the publisher in an autoreleasepool.", "reason": "Option D is wrong because autoreleasepool manages temporary ObjC memory, not stream iteration."}
                ]
            }
        ],
        "exercise": {
            "title": "Bridging Combine CurrentValueSubject into AsyncSequence",
            "instructions": "Implement <code>consumeThreeValues</code>. Consume 3 values from <code>subject.values</code> using <code>for await</code> and return them in an array. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation
import Combine

func consumeThreeValues(subject: CurrentValueSubject<Int, Never>) async -> [Int] {
    var output: [Int] = []
    // TODO: Iterate over subject.values using for await val in subject.values
    // Append val to output. When output.count == 3, break and return output.
    fatalError("TODO: Implement consumeThreeValues")
}

func runTests() async {
    print("🧪 Running Lesson 2.7 Verification Tests...")
    let subject = CurrentValueSubject<Int, Never>(10)

    Task {
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(20)
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(30)
    }

    let results = await consumeThreeValues(subject: subject)
    assert(results == [10, 20, 30], "Test Failed: Expected [10, 20, 30], got \\(results)")
    print("  ✅ Challenge Passed: Combine to AsyncSequence bridging verified")
}

await runTests()""",
            "hints": [
                "Write: for await val in subject.values { output.append(val); if output.count == 3 { break } }",
                "Return output."
            ],
            "solution_code": """import Foundation
import Combine

func consumeThreeValues(subject: CurrentValueSubject<Int, Never>) async -> [Int] {
    var output: [Int] = []
    for await val in subject.values {
        output.append(val)
        if output.count == 3 {
            break
        }
    }
    return output
}

func runTests() async {
    print("🧪 Running Lesson 2.7 Verification Tests...")
    let subject = CurrentValueSubject<Int, Never>(10)
    Task {
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(20)
        try? await Task.sleep(nanoseconds: 5_000_000)
        subject.send(30)
    }
    let results = await consumeThreeValues(subject: subject)
    assert(results == [10, 20, 30])
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "Combine publishers expose .values, conforming to AsyncSequence. This enables modern structured concurrency code to consume Combine streams with native for-await loops and cooperative cancellation."
        },
        "primary_sources": [
            {"title": "Apple Developer: Combine Framework", "url": "https://developer.apple.com/documentation/combine", "desc": "Official Apple reference for Publishers, Subscribers, and Operators."},
            {"title": "Apple Developer: Publisher.values", "url": "https://developer.apple.com/documentation/combine/publisher/values", "desc": "Bridging Combine streams to AsyncSequence."}
        ],
        "coaching_prompt": "Whenever you see legacy Combine in an existing app, remember that publisher.values allows you to migrate call-sites to async/await immediately without rewriting the entire pipeline!"
    },

    # 0016: Lesson 2.8
    {
        "id": "0016",
        "slug": "0016-production-urlsession",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.8",
        "title": "Production Networking with URLSession & HTTP Security",
        "subtitle": "Async URLSession.shared.data(for:), status code validation, custom HTTP headers, timeout policies, and HTTP/3 support.",
        "prev_url": "0015-combine-essentials-and-async.html",
        "next_url": "0017-robust-codable-architecture.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android, developers almost universally import third-party libraries (OkHttp + Retrofit) to execute network calls.</p>
        <p>In iOS, <strong><code>URLSession</code></strong> is Apple's first-party, high-performance HTTP client. It supports HTTP/2, HTTP/3 (QUIC), connection pooling, background downloads, and async/await natively. Third-party networking libraries (like Alamofire) are rarely needed in modern Swift.</p>
        """,
        "core_sections_html": """
        <h3>1. The Modern URLSession async Pipeline</h3>
        <pre><code>enum HTTPError: Error, Equatable {
    case invalidResponse
    case statusCode(Int)
}

func executeRequest(url: URL) async throws -> Data {
    var request = URLRequest(url: url)
    request.httpMethod = "GET"
    request.setValue("application/json", forHTTPHeaderField: "Accept")
    request.timeoutInterval = 15.0

    let (data, response) = try await URLSession.shared.data(for: request)

    guard let httpResponse = response as? HTTPURLResponse else {
        throw HTTPError.invalidResponse
    }

    guard (200...299).contains(httpResponse.statusCode) else {
        throw HTTPError.statusCode(httpResponse.statusCode)
    }

    return data
}</code></pre>

        <h3>2. URLSessionConfiguration Profiles</h3>
        <ul>
          <li><strong><code>.default</code>:</strong> Standard disk-persisted caching and credentials.</li>
          <li><strong><code>.ephemeral</code>:</strong> In-memory only (private browsing / zero cache written to disk).</li>
          <li><strong><code>.background(withIdentifier:)</code>:</strong> Transfers continue even if the app is suspended or terminated by Darwin Jetsam!</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Ignoring HTTP Status Codes on Successful <code>data(for:)</code>:</strong> A 404 Not Found or 500 Internal Server Error does NOT throw an error from <code>URLSession.data(for:)</code>. It returns valid HTML/JSON error payloads. You must always inspect <code>(response as? HTTPURLResponse)?.statusCode</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What happens when a server responds with HTTP status 500 in URLSession.shared.data(for:)?",
                "correct_idx": 1,
                "options": [
                    {"text": "The method throws URLError immediately.", "reason": "Option A is wrong because 500 is a valid HTTP network response, not a transport error."},
                    {"text": "Returns tuple with status code 500.", "reason": "Option B is correct: data(for:) succeeds and returns (Data, HTTPURLResponse) with statusCode 500."},
                    {"text": "The Darwin OS terminates app instantly.", "reason": "Option C is wrong because server errors never trigger app crashes."},
                    {"text": "Retries 3 times automatically on thread.", "reason": "Option D is wrong because URLSession does not automatically retry 500 errors unless configured."}
                ]
            },
            {
                "question": "Which URLSessionConfiguration should you use for sensitive authentication requests to avoid writing credentials to disk cache?",
                "correct_idx": 2,
                "options": [
                    {"text": "URLSessionConfiguration.default", "reason": "Option A is wrong because default writes caches and cookies to disk."},
                    {"text": "URLSessionConfiguration.background", "reason": "Option B is wrong because background is for long-running uploads/downloads."},
                    {"text": "URLSessionConfiguration.ephemeral", "reason": "Option C is correct: ephemeral stores caches, credentials, and cookies entirely in RAM with zero disk writes."},
                    {"text": "URLSessionConfiguration.diskOnly", "reason": "Option D is wrong because diskOnly is not an Apple configuration profile."}
                ]
            }
        ],
        "exercise": {
            "title": "Robust HTTP Response Validator",
            "instructions": "Implement <code>validateHTTPResponse</code>. Cast <code>URLResponse</code> to <code>HTTPURLResponse</code>. If status code is between 200 and 299, return data. If outside 200..299, throw <code>NetworkError.httpStatus(statusCode)</code>. If cast fails, throw <code>NetworkError.invalidResponse</code>. Run tests.",
            "starter_code": """import Foundation

enum NetworkError: Error, Equatable {
    case invalidResponse
    case httpStatus(Int)
}

func validateHTTPResponse(data: Data, response: URLResponse) throws -> Data {
    // TODO: 1. Guard cast response as? HTTPURLResponse else throw NetworkError.invalidResponse
    // TODO: 2. Check if (200...299).contains(httpResponse.statusCode) -> return data
    // TODO: 3. Else throw NetworkError.httpStatus(httpResponse.statusCode)
    fatalError("TODO: Implement validateHTTPResponse")
}

func runTests() {
    print("🧪 Running Lesson 2.8 Verification Tests...")
    let dummyURL = URL(string: "https://api.example.com/data")!
    let sampleData = "{\\"status\\":\\"ok\\"}".data(using: .utf8)!

    // Test 1: 200 OK
    let resp200 = HTTPURLResponse(url: dummyURL, statusCode: 200, httpVersion: nil, headerFields: nil)!
    do {
        let valid = try validateHTTPResponse(data: sampleData, response: resp200)
        assert(valid == sampleData, "Test 1 Failed")
        print("  ✅ Challenge 1 Passed: 200 OK validated")
    } catch { fatalError() }

    // Test 2: 404 Error
    let resp404 = HTTPURLResponse(url: dummyURL, statusCode: 404, httpVersion: nil, headerFields: nil)!
    do {
        _ = try validateHTTPResponse(data: sampleData, response: resp404)
        fatalError("Test 2 Failed: Should have thrown 404 error")
    } catch NetworkError.httpStatus(let code) {
        assert(code == 404, "Test 2 Failed")
        print("  ✅ Challenge 2 Passed: 404 Status error correctly thrown")
    } catch { fatalError() }
}
runTests()""",
            "hints": [
                "guard let http = response as? HTTPURLResponse else { throw NetworkError.invalidResponse }",
                "guard (200...299).contains(http.statusCode) else { throw NetworkError.httpStatus(http.statusCode) }",
                "return data"
            ],
            "solution_code": """import Foundation

enum NetworkError: Error, Equatable {
    case invalidResponse
    case httpStatus(Int)
}

func validateHTTPResponse(data: Data, response: URLResponse) throws -> Data {
    guard let http = response as? HTTPURLResponse else {
        throw NetworkError.invalidResponse
    }
    guard (200...299).contains(http.statusCode) else {
        throw NetworkError.httpStatus(http.statusCode)
    }
    return data
}

func runTests() {
    print("🧪 Running Lesson 2.8 Verification Tests...")
    let dummyURL = URL(string: "https://api.example.com/data")!
    let sampleData = "{\\"status\\":\\"ok\\"}".data(using: .utf8)!

    let resp200 = HTTPURLResponse(url: dummyURL, statusCode: 200, httpVersion: nil, headerFields: nil)!
    let valid = try! validateHTTPResponse(data: sampleData, response: resp200)
    assert(valid == sampleData)

    let resp404 = HTTPURLResponse(url: dummyURL, statusCode: 404, httpVersion: nil, headerFields: nil)!
    do {
        _ = try validateHTTPResponse(data: sampleData, response: resp404)
        fatalError()
    } catch NetworkError.httpStatus(let code) {
        assert(code == 404)
        print("  ✅ All tests passed!")
    } catch { fatalError() }
}
runTests()""",
            "solution_explanation": "URLSession returns URLResponse as an abstract base type. Inspecting status code requires downcasting to HTTPURLResponse. Bounding status codes between 200 and 299 ensures client errors (4xx) and server errors (5xx) are categorized cleanly."
        },
        "primary_sources": [
            {"title": "Apple Developer: URLSession Documentation", "url": "https://developer.apple.com/documentation/foundation/urlsession", "desc": "Official guide on URLSession async/await methods and configurations."},
            {"title": "Apple Developer: HTTPURLResponse", "url": "https://developer.apple.com/documentation/foundation/httpurlresponse", "desc": "Status code and header field documentation."}
        ],
        "coaching_prompt": "Always construct your custom URLSession with an ephemeral configuration when handling sensitive token authentication."
    },

    # 0017: Lesson 2.9
    {
        "id": "0017",
        "slug": "0017-robust-codable-architecture",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.9",
        "title": "Robust JSON Parsing with Codable Architecture",
        "subtitle": "Key decoding strategies, date formatting, custom init(from:), polymorphic payloads, and resilient lossy array decoders.",
        "prev_url": "0016-production-urlsession.html",
        "next_url": "0018-enterprise-network-layer.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Kotlin), JSON parsing typically uses Moshi or kotlinx.serialization. Unknown keys or malformed items in an array often throw parsing exceptions unless custom adapters are registered.</p>
        <p>In Swift, <strong><code>Codable</code></strong> (<code>Encodable & Decodable</code>) is synthesized by the compiler. Senior iOS engineers build <strong>lossy decoders</strong>: if one item out of 100 in an array is malformed, standard Codable throws and discards all 100 items! Lossy decoders isolate malformed items, keeping the 99 valid elements.</p>
        """,
        "core_sections_html": """
        <h3>1. Standard JSONDecoder Strategies</h3>
        <pre><code>let decoder = JSONDecoder()
decoder.keyDecodingStrategy = .convertFromSnakeCase // converts "user_id" -> "userId"
decoder.dateDecodingStrategy = .iso8601</code></pre>

        <h3>2. The Lossy Array Pattern (Senior Must-Have)</h3>
        <p>A single invalid object should never crash the entire list:</p>
        <pre><code>struct LossyArray&lt;T: Decodable&gt;: Decodable {
    let elements: [T]

    init(from decoder: Decoder) throws {
        var container = try decoder.unkeyedContainer()
        var elements: [T] = []
        while !container.isAtEnd {
            if let element = try? container.decode(T.self) {
                elements.append(element)
            } else {
                _ = try? container.decode(EmptyDecodable.self) // Skip bad item!
            }
        }
        self.elements = elements
    }
}
private struct EmptyDecodable: Decodable {}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Strict Decoding on Volatile Backends:</strong> When backends deploy breaking changes (new null values or changed type formats), rigid Codable models cause the entire app screen to fail to load. Use lossy decoding and optional fallbacks for non-critical fields.</p>
        """,
        "quizzes": [
            {
                "question": "What happens by default when decoding a standard [User] array if one element has an invalid field type?",
                "correct_idx": 1,
                "options": [
                    {"text": "The decoder replaces the element with nil.", "reason": "Option A is wrong because standard Array decoding does not insert nil."},
                    {"text": "The entire array decoding throws and fails.", "reason": "Option B is correct: standard Codable fails fast and aborts the entire list if any single item fails to decode."},
                    {"text": "The Darwin kernel logs a memory warning.", "reason": "Option C is wrong because decoding errors are Swift exceptions, not OS memory events."},
                    {"text": "The compiler skips the invalid element.", "reason": "Option D is wrong because decoding occurs at runtime, not compile time."}
                ]
            },
            {
                "question": "What decoder strategy automatically maps 'created_at' in JSON to 'createdAt' in Swift?",
                "correct_idx": 0,
                "options": [
                    {"text": "decoder.keyDecodingStrategy = .convertFromSnakeCase", "reason": "Option A is correct: convertFromSnakeCase automatically maps snake_case JSON keys to camelCase Swift properties."},
                    {"text": "decoder.keyDecodingStrategy = .useDefaultKeys", "reason": "Option B is wrong because default keys expect exact matching string names."},
                    {"text": "decoder.dataDecodingStrategy = .base64", "reason": "Option C is wrong because base64 decodes binary payloads."},
                    {"text": "decoder.dateDecodingStrategy = .iso8601", "reason": "Option D is wrong because iso8601 governs date parsing, not property names."}
                ]
            }
        ],
        "exercise": {
            "title": "Resilient Lossy Array JSON Decoder",
            "instructions": "Implement <code>decodeLossyList</code>. Given JSON containing an array of users where some items are corrupted, decode only the valid items into <code>[User]</code> without throwing an error. Run tests.",
            "starter_code": """import Foundation

struct User: Codable, Equatable {
    let id: Int
    let name: String
}

func decodeLossyList(jsonData: Data) -> [User] {
    // Helper struct for lossy element parsing
    struct LossyElement<T: Decodable>: Decodable {
        let value: T?
        init(from decoder: Decoder) throws {
            let container = try decoder.singleValueContainer()
            self.value = try? container.decode(T.self)
        }
    }

    // TODO: 1. Decode [LossyElement<User>] from jsonData
    // TODO: 2. Return compactMap { $0.value }
    // If decoding fails completely, return []
    fatalError("TODO: Implement decodeLossyList")
}

func runTests() {
    print("🧪 Running Lesson 2.9 Verification Tests...")
    let rawJSON = \"\"\"
    [
        {"id": 1, "name": "Alice"},
        {"id": "invalid_id", "name": "Bob"},
        {"id": 3, "name": "Charlie"},
        {"broken_json": true}
    ]
    \"\"\".data(using: .utf8)!

    let validUsers = decodeLossyList(jsonData: rawJSON)
    assert(validUsers.count == 2, "Test 1 Failed: Expected 2 valid users, got \\(validUsers.count)")
    assert(validUsers[0] == User(id: 1, name: "Alice"), "Test 2 Failed")
    assert(validUsers[1] == User(id: 3, name: "Charlie"), "Test 3 Failed")
    print("  ✅ Challenge Passed: Lossy array decoding rescued valid users")
}
runTests()""",
            "hints": [
                "let decoder = JSONDecoder()",
                "guard let elements = try? decoder.decode([LossyElement<User>].self, from: jsonData) else { return [] }",
                "return elements.compactMap { $0.value }"
            ],
            "solution_code": """import Foundation

struct User: Codable, Equatable {
    let id: Int
    let name: String
}

func decodeLossyList(jsonData: Data) -> [User] {
    struct LossyElement<T: Decodable>: Decodable {
        let value: T?
        init(from decoder: Decoder) throws {
            let container = try decoder.singleValueContainer()
            self.value = try? container.decode(T.self)
        }
    }

    let decoder = JSONDecoder()
    guard let elements = try? decoder.decode([LossyElement<User>].self, from: jsonData) else {
        return []
    }
    return elements.compactMap { $0.value }
}

func runTests() {
    print("🧪 Running Lesson 2.9 Verification Tests...")
    let rawJSON = \"\"\"
    [
        {"id": 1, "name": "Alice"},
        {"id": "invalid_id", "name": "Bob"},
        {"id": 3, "name": "Charlie"},
        {"broken_json": true}
    ]
    \"\"\".data(using: .utf8)!

    let validUsers = decodeLossyList(jsonData: rawJSON)
    assert(validUsers.count == 2)
    assert(validUsers[0] == User(id: 1, name: "Alice"))
    assert(validUsers[1] == User(id: 3, name: "Charlie"))
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Wrapping array elements in LossyElement catches per-item decoding failures via try?. Invalid objects decode as LossyElement(value: nil). compactMap strips out the nil values, preserving all valid elements without dropping the entire payload."
        },
        "primary_sources": [
            {"title": "Apple Developer: Encoding and Decoding Custom Types", "url": "https://developer.apple.com/documentation/swift/codable", "desc": "Canonical Apple guide on Codable, KeyedContainer, and SingleValueContainer."},
            {"title": "Swift Evolution SE-0166: Swift Archival & Serialization", "url": "https://github.com/apple/swift-evolution/blob/main/proposals/0166-swift-archival-serialization.md", "desc": "Architectural rationale behind the Codable design."}
        ],
        "coaching_prompt": "In enterprise apps, always protect feed arrays with lossy decoding. One bad entry from backend should never blank out the entire home screen!"
    },

    # 0018: Lesson 2.10
    {
        "id": "0018",
        "slug": "0018-enterprise-network-layer",
        "phase_num": 2,
        "phase_title": "Phase 2: Modern Swift 6 Concurrency & Networking",
        "lesson_num": "Lesson 2.10",
        "title": "Enterprise Network Layer: Interceptors & Token Refresh",
        "subtitle": "Request interceptors, actor-synchronized OAuth2 token refresh, exponential backoff retries, and network telemetry.",
        "prev_url": "0017-robust-codable-architecture.html",
        "next_url": "../lessons/0019-swiftui-render-tree.html",
        "cheatsheet_url": "../reference/swift-concurrency-cheatsheet.html",
        "cheatsheet_title": "Swift Concurrency Cheatsheet",
        "android_anchor_html": """
        <p>In Android (OkHttp), request modification and authentication token refreshes are handled by chaining <code>Interceptor</code> classes (<code>Authenticator</code>).</p>
        <p>In Swift, we build an <strong>Actor-Synchronized Token Engine</strong>. When multiple concurrent network requests receive an HTTP 401 Unauthorized, an actor serializes the refresh: exactly <em>one</em> refresh request is sent to the server, and all waiting concurrent requests receive the fresh token without triggering thundering-herd duplicate refresh storms!</p>
        """,
        "core_sections_html": """
        <h3>1. The Thundering Herd Problem (Senior System Design)</h3>
        <p>Imagine 5 concurrent requests hit the network when the access token expires. All 5 receive HTTP 401. If all 5 independently request a new token, the auth server invalidates refresh tokens or rate-limits the user. We must serialize token refreshes using an <code>actor</code>.</p>

        <h3>2. The Actor-Synchronized Token Refresh Pattern</h3>
        <pre><code>actor TokenManager {
    private var token: String?
    private var refreshTask: Task&lt;String, Error&gt;?

    func validToken() async throws -> String {
        if let token { return token }

        // If refresh already in progress, await the SAME Task:
        if let refreshTask { return try await refreshTask.value }

        let task = Task { () -> String in
            defer { self.refreshTask = nil }
            let fresh = try await AuthAPI.refreshToken()
            self.token = fresh
            return fresh
        }
        self.refreshTask = task
        return try await task.value
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Infinite 401 Retry Loops:</strong> If the refresh token itself is expired or revoked, retrying on 401 indefinitely hangs the app in an infinite network loop. Enforce maximum retry counts (e.g. <code>maxRetries = 1</code>) and post an authentication logout notification on failure.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary architectural purpose of deduplicating token refresh calls in an actor?",
                "correct_idx": 0,
                "options": [
                    {"text": "Prevents thundering-herd duplicate refresh storms.", "reason": "Option A is correct: multiple concurrent 401s share a single refresh task, protecting auth servers from rate-limiting."},
                    {"text": "Converts reference types into value types.", "reason": "Option B is wrong because actors are reference types."},
                    {"text": "Disables HTTP/3 QUIC connection pools.", "reason": "Option C is wrong because token management is application-level."},
                    {"text": "Forces the UI thread to freeze until done.", "reason": "Option D is wrong because freezing the UI thread causes hitches."}
                ]
            },
            {
                "question": "Why must refreshTask be cleared in a defer block inside the token refresh Task?",
                "correct_idx": 2,
                "options": [
                    {"text": "To notify the Darwin kernel of process completion.", "reason": "Option A is wrong because Darwin does not track individual Task variables."},
                    {"text": "To wipe keychain credentials from RAM.", "reason": "Option B is wrong because keychain storage is separate from this in-memory Task."},
                    {"text": "To allow future refreshes after current one completes.", "reason": "Option C is correct: clearing refreshTask ensures subsequent token expirations will trigger a new refresh."},
                    {"text": "To enforce binary compilation optimization.", "reason": "Option D is wrong because defer is runtime execution."}
                ]
            }
        ],
        "exercise": {
            "title": "Actor-Synchronized Token Refresh Manager",
            "instructions": "Implement <code>fetchToken</code> in <code>AuthTokenActor</code>. If <code>activeRefreshTask</code> is present, await its value. Otherwise, create a new <code>Task</code>, store it in <code>activeRefreshTask</code>, clear it in <code>defer</code>, and update <code>currentToken</code>. Run tests.",
            "starter_code": """import Foundation

actor AuthTokenActor {
    private var currentToken: String = "expired_token"
    private var activeRefreshTask: Task<String, Error>?
    var refreshExecutionCount = 0

    func fetchToken() async throws -> String {
        // TODO: 1. If activeRefreshTask exists, return try await activeRefreshTask.value
        // TODO: 2. Otherwise create a new Task<String, Error> { ... }
        //          Inside Task: sleep 10ms, increment refreshExecutionCount, return "fresh_token_123"
        // TODO: 3. Set activeRefreshTask = task
        // TODO: 4. Use defer { activeRefreshTask = nil }
        // TODO: 5. Await task value, update currentToken, and return it
        fatalError("TODO: Implement fetchToken")
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.10 Verification Tests...")
    let auth = AuthTokenActor()

    // Simulate 5 simultaneous requests trying to fetch a fresh token
    let tokens = await withTaskGroup(of: String?.self) { group in
        for _ in 0..<5 {
            group.addTask { try? await auth.fetchToken() }
        }
        var res: [String] = []
        for await t in group { if let t { res.append(t) } }
        return res
    }

    assert(tokens.count == 5, "Test 1 Failed")
    assert(tokens.allSatisfy { $0 == "fresh_token_123" }, "Test 2 Failed")
    let count = await auth.refreshExecutionCount
    assert(count == 1, "Test 3 Failed: Token refreshed multiple times! (Expected exactly 1, got \\(count))")
    print("  ✅ Challenge Passed: Actor-synchronized token refresh deduplicated successfully")
}

await runTests()""",
            "hints": [
                "if let existing = activeRefreshTask { return try await existing.value }",
                "let task = Task<String, Error> { try await Task.sleep(nanoseconds: 10_000_000); refreshExecutionCount += 1; return \"fresh_token_123\" }",
                "activeRefreshTask = task",
                "defer { activeRefreshTask = nil }",
                "let val = try await task.value; currentToken = val; return val"
            ],
            "solution_code": """import Foundation

actor AuthTokenActor {
    private var currentToken: String = "expired_token"
    private var activeRefreshTask: Task<String, Error>?
    var refreshExecutionCount = 0

    func fetchToken() async throws -> String {
        if let existing = activeRefreshTask {
            return try await existing.value
        }

        let task = Task<String, Error> {
            try await Task.sleep(nanoseconds: 10_000_000)
            refreshExecutionCount += 1
            return "fresh_token_123"
        }
        activeRefreshTask = task
        defer {
            activeRefreshTask = nil
        }

        let val = try await task.value
        currentToken = val
        return val
    }
}

func runTests() async {
    print("🧪 Running Lesson 2.10 Verification Tests...")
    let auth = AuthTokenActor()
    let tokens = await withTaskGroup(of: String?.self) { group in
        for _ in 0..<5 {
            group.addTask { try? await auth.fetchToken() }
        }
        var res: [String] = []
        for await t in group { if let t { res.append(t) } }
        return res
    }
    assert(tokens.count == 5)
    assert(tokens.allSatisfy { $0 == "fresh_token_123" })
    let count = await auth.refreshExecutionCount
    assert(count == 1)
    print("  ✅ All tests passed!")
}

await runTests()""",
            "solution_explanation": "By holding the in-flight refresh Task in an actor variable before awaiting, all concurrent callers discover the running Task and await its single resolution. Exactly 1 network refresh runs, protecting the auth server from rate limits."
        },
        "primary_sources": [
            {"title": "WWDC Session: Protect mutable state with Swift actors", "url": "https://developer.apple.com/videos/play/wwdc2021/10133/", "desc": "Apple engineers demonstrate actor synchronization patterns."},
            {"title": "Donny Wals: Managing in-flight tasks in Swift Concurrency", "url": "https://www.donnywals.com", "desc": "Practical guide to deduplicating async tasks."}
        ],
        "coaching_prompt": "Congratulations! You have mastered Swift 6 Concurrency and enterprise networking: from TaskGroup and AsyncStream to Actor reentrancy defense and synchronized token refresh. Next: Phase 3 High-Performance Declarative UI!"
    }
]

PHASE2_LESSONS.extend(PHASE2_PART2)
print(f"Phase 2 complete: total {len(PHASE2_LESSONS)} lessons loaded.")
