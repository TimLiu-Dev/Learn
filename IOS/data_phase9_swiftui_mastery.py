# Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling (Lessons 0061 - 0066)

PHASE9_SWIFTUI_LESSONS = [
    # 0061: Lesson 9.1
    {
        "id": "0061",
        "slug": "0061-result-builders-and-viewbuilder",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.1",
        "title": "Result Builders (@resultBuilder) & @ViewBuilder Mechanics",
        "subtitle": "How buildBlock, buildOptional, buildEither, and buildArray desugar SwiftUI DSLs—and how to author custom type-safe builders.",
        "prev_url": "0060-xcode-cloud-and-release-engineering.html",
        "next_url": "0062-scrollview-performance-and-preferencekey.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, <code>@Composable</code> functions emit UI nodes imperatively into a Slot Table during execution, powered by a Kotlin compiler plugin.</p>
        <p>In SwiftUI, there is no compiler plugin—instead, <strong><code>@ViewBuilder</code></strong> is a pure Swift <strong><code>@resultBuilder</code></strong>! At compile time, Swift rewrites multi-statement view closures into nested generic value types (<code>TupleView</code> and <code>_ConditionalContent</code>). Understanding this desugaring explains both SwiftUI structural identity and slow compiler type-check errors.</p>
        """,
        "core_sections_html": """
        <h3>1. How @ViewBuilder Desugars Your Code</h3>
        <pre><code>// What you write:
@ViewBuilder
func statusBanner(isOnline: Bool) -&gt; some View {
    Text("PulseFlow")
    if isOnline {
        Label("Connected", systemImage: "checkmark.circle")
    } else {
        Label("Offline", systemImage: "wifi.slash")
    }
}

// What the Swift compiler actually generates:
// TupleView&lt;(Text, _ConditionalContent&lt;Label, Label&gt;)&gt;</code></pre>
        <ul>
          <li><strong><code>buildBlock</code>:</strong> Combines sibling statements into a single tuple/composite value (in SwiftUI, variadic parameter packs remove the old 10-view limit!).</li>
          <li><strong><code>buildOptional</code> / <code>buildEither(first:second:)</code>:</strong> Translates <code>if</code> and <code>if/else</code> or <code>switch</code> branches into distinct static wrapper types, preserving compile-time type safety.</li>
          <li><strong><code>buildArray</code> / <code>buildLimitedAvailability</code>:</strong> Handles loops (in custom builders) and <code>if #available(...)</code> checks.</li>
        </ul>

        <h3>2. Authoring Custom @resultBuilder DSLs</h3>
        <p>Senior engineers build custom <code>@resultBuilder</code> types for declarative validation pipelines, routing tables, or design-system form sections:</p>
        <pre><code>@resultBuilder
struct SectionBuilder&lt;Item&gt; {
    static func buildBlock(_ components: [Item]...) -&gt; [Item] { components.flatMap { $0 } }
    static func buildExpression(_ item: Item) -&gt; [Item] { [item] }
    static func buildOptional(_ component: [Item]?) -&gt; [Item] { component ?? [] }
    static func buildEither(first: [Item]) -&gt; [Item] { first }
    static func buildEither(second: [Item]) -&gt; [Item] { second }
    static func buildArray(_ components: [[Item]]) -&gt; [Item] { components.flatMap { $0 } }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Monolithic 200-Line <code>body</code> Closures &amp; <code>AnyView</code> Type Erasure:</strong> Deeply nested <code>@ViewBuilder</code> expressions create massive generic type trees that cause <em>"The compiler is unable to type-check this expression in reasonable time"</em>. Extract small subviews (using <code>some View</code> or <code>@ViewBuilder</code> helper properties)—never reach for <code>AnyView</code>, which destroys structural identity and disables AttributeGraph diffing.</p>
        """,
        "quizzes": [
            {
                "question": "Which `@resultBuilder` method is invoked when you write an `if / else` branch inside a `@ViewBuilder` closure?",
                "correct_idx": 2,
                "options": [
                    {"text": "buildOptional for nullable values.", "reason": "Option A is wrong because buildOptional is used for an `if` without an `else`."},
                    {"text": "buildArray for loop iterations.", "reason": "Option B is wrong because buildArray is used for `for-in` loops."},
                    {"text": "buildEither first and second.", "reason": "Option C is correct: the compiler lowers binary and switch branches into nested buildEither(first:) and buildEither(second:) calls."},
                    {"text": "buildFinalResult for type erasure.", "reason": "Option D is wrong because buildFinalResult transforms the final accumulated output, not branch arms."}
                ]
            },
            {
                "question": "Why does replacing `AnyView` with `@ViewBuilder` improve SwiftUI runtime performance?",
                "correct_idx": 0,
                "options": [
                    {"text": "Preserves concrete types for diffing.", "reason": "Option A is correct: @ViewBuilder keeps the concrete generic type tree intact so AttributeGraph can diff nodes structurally without heap boxing."},
                    {"text": "Moves rendering onto Background threads.", "reason": "Option B is wrong because SwiftUI view evaluation runs on MainActor."},
                    {"text": "Converts Swift structs into UIViews.", "reason": "Option C is wrong because @ViewBuilder works on pure SwiftUI View values."},
                    {"text": "Disables Swift strict concurrency checks.", "reason": "Option D is wrong because @resultBuilder obeys full Swift 6 concurrency rules."}
                ]
            }
        ],
        "exercise": {
            "title": "Build a Custom @resultBuilder for Declarative Route/Menu DSL",
            "instructions": "Complete <code>MenuDSLBuilder</code> supporting single <code>MenuItem</code> expressions, <code>if</code> without <code>else</code> (<code>buildOptional</code>), <code>if/else</code> (<code>buildEither</code>), and <code>for-in</code> loops (<code>buildArray</code>). Run tests.",
            "starter_code": """import Foundation

struct MenuItem: Equatable {
    let title: String
}

@resultBuilder
struct MenuDSLBuilder {
    static func buildExpression(_ item: MenuItem) -> [MenuItem] {
        [item]
    }

    // TODO: Implement buildBlock(_ components: [MenuItem]...) -> [MenuItem]
    static func buildBlock(_ components: [MenuItem]...) -> [MenuItem] {
        fatalError("TODO: Implement buildBlock")
    }

    // TODO: Implement buildOptional(_ component: [MenuItem]?) -> [MenuItem]
    static func buildOptional(_ component: [MenuItem]?) -> [MenuItem] {
        fatalError("TODO: Implement buildOptional")
    }

    // TODO: Implement buildEither(first:) and buildEither(second:)
    static func buildEither(first component: [MenuItem]) -> [MenuItem] {
        fatalError("TODO: Implement buildEither(first:)")
    }

    static func buildEither(second component: [MenuItem]) -> [MenuItem] {
        fatalError("TODO: Implement buildEither(second:)")
    }

    // TODO: Implement buildArray(_ components: [[MenuItem]]) -> [MenuItem]
    static func buildArray(_ components: [[MenuItem]]) -> [MenuItem] {
        fatalError("TODO: Implement buildArray")
    }
}

func buildMenu(@MenuDSLBuilder _ content: () -> [MenuItem]) -> [MenuItem] {
    content()
}

func runTests() {
    print("🧪 Running Lesson 9.1 Verification Tests...")
    let isAdmin = true
    let showBeta = false
    let dynamicTags = ["Logs", "Traces"]

    let items = buildMenu {
        MenuItem(title: "Home")
        if showBeta {
            MenuItem(title: "Beta")
        }
        if isAdmin {
            MenuItem(title: "Admin")
        } else {
            MenuItem(title: "User")
        }
        for tag in dynamicTags {
            MenuItem(title: tag)
        }
    }
    assert(items.map(\\.title) == ["Home", "Admin", "Logs", "Traces"], "Test 1 Failed: DSL output mismatch")
    print("  ✅ Challenge Passed: Custom @resultBuilder DSL verified")
}
runTests()""",
            "hints": [
                "In buildBlock and buildArray, use components.flatMap { $0 }.",
                "In buildOptional, return component ?? [].",
                "In buildEither(first:) and buildEither(second:), simply return component."
            ],
            "solution_code": """import Foundation

struct MenuItem: Equatable {
    let title: String
}

@resultBuilder
struct MenuDSLBuilder {
    static func buildExpression(_ item: MenuItem) -> [MenuItem] {
        [item]
    }

    static func buildBlock(_ components: [MenuItem]...) -> [MenuItem] {
        components.flatMap { $0 }
    }

    static func buildOptional(_ component: [MenuItem]?) -> [MenuItem] {
        component ?? []
    }

    static func buildEither(first component: [MenuItem]) -> [MenuItem] {
        component
    }

    static func buildEither(second component: [MenuItem]) -> [MenuItem] {
        component
    }

    static func buildArray(_ components: [[MenuItem]]) -> [MenuItem] {
        components.flatMap { $0 }
    }
}

func buildMenu(@MenuDSLBuilder _ content: () -> [MenuItem]) -> [MenuItem] {
    content()
}

func runTests() {
    print("🧪 Running Lesson 9.1 Verification Tests...")
    let isAdmin = true
    let showBeta = false
    let dynamicTags = ["Logs", "Traces"]

    let items = buildMenu {
        MenuItem(title: "Home")
        if showBeta {
            MenuItem(title: "Beta")
        }
        if isAdmin {
            MenuItem(title: "Admin")
        } else {
            MenuItem(title: "User")
        }
        for tag in dynamicTags {
            MenuItem(title: tag)
        }
    }
    assert(items.map(\\.title) == ["Home", "Admin", "Logs", "Traces"])
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "<code>@resultBuilder</code> is pure compile-time syntactic rewriting: understanding how <code>buildBlock</code>, <code>buildOptional</code>, <code>buildEither</code>, and <code>buildArray</code> compose explains every SwiftUI DSL rule and lets you build expressive internal APIs."
        },
        "primary_sources": [
            {"title": "Swift Evolution SE-0289: Result Builders", "url": "https://github.com/swiftlang/swift-evolution/blob/main/proposals/0289-result-builders.md", "desc": "Canonical language specification for @resultBuilder transformations."},
            {"title": "Apple Developer: ViewBuilder", "url": "https://developer.apple.com/documentation/swiftui/viewbuilder", "desc": "Official SwiftUI @ViewBuilder API reference."}
        ],
        "coaching_prompt": "Once @ViewBuilder clicks, SwiftUI compiler errors stop feeling mysterious! Next up is Lesson 9.2: High-Performance ScrollView, Lazy Containers & PreferenceKey."
    },

    # 0062: Lesson 9.2
    {
        "id": "0062",
        "slug": "0062-scrollview-performance-and-preferencekey",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.2",
        "title": "High-Performance ScrollView, Lazy Containers & PreferenceKey",
        "subtitle": "List vs LazyVStack memory trade-offs, scrollPosition/scrollTargetBehavior, .visualEffect, onScrollGeometryChange, and PreferenceKey upward flow.",
        "prev_url": "0061-result-builders-and-viewbuilder.html",
        "next_url": "0063-swift-charts-canvas-and-timelineview.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, <code>LazyColumn</code> recycles/composes visible items and reports scroll position via <code>LazyListState</code>, while child-to-parent layout metadata flows via <code>onGloballyPositioned</code>.</p>
        <p>In SwiftUI, Senior engineers know why <strong><code>LazyVStack</code></strong> inside a <code>ScrollView</code> never frees view state like <strong><code>List</code></strong> does, how modern <strong><code>.visualEffect</code></strong> and <strong><code>.onScrollGeometryChange</code></strong> replace expensive <code>GeometryReader</code> hacks for 120fps parallax/headers, and how <strong><code>PreferenceKey</code></strong> propagates child metrics upward safely.</p>
        """,
        "core_sections_html": """
        <h3>1. List vs. LazyVStack vs. VStack</h3>
        <ul>
          <li><strong><code>VStack</code>:</strong> Eagerly instantiates <em>all</em> child views at mount. Never use for unbounded feeds!</li>
          <li><strong><code>LazyVStack</code>:</strong> Defers body evaluation until scrolled near the viewport, <em>but</em> keeps materialized child identities alive in memory as you scroll down.</li>
          <li><strong><code>List</code>:</strong> Backed by platform cell recycling (<code>UICollectionView</code>); reclaims offscreen memory aggressively for 10,000+ row feeds.</li>
        </ul>

        <h3>2. Zero-Invalidation Geometry &amp; PreferenceKey</h3>
        <pre><code>// Modern 120fps scroll effects WITHOUT GeometryReader layout loops:
ScrollView {
    LazyVStack { ... }
}
.scrollPosition(id: $activeID)
.scrollTargetBehavior(.viewAligned)
.onScrollGeometryChange(for: CGFloat.self) { geo in
    geo.contentOffset.y
} action: { _, newOffset in
    headerOpacity = min(1, max(0, newOffset / 120))
}</code></pre>
        <p>When a child genuinely must report sizes or anchor frames upward (e.g., equal-width tab bars), implement a <strong><code>PreferenceKey</code></strong> with an associative <code>reduce(value:nextValue:)</code> combiner.</p>
        """,
        "failure_mode_html": """
        <p><strong>Creating Infinite Layout Loops with <code>GeometryReader</code> + <code>PreferenceKey</code>:</strong> Measuring a child via <code>GeometryReader</code>, writing it into a <code>PreferenceKey</code>, and using that preference to resize the parent causes a feedback cycle if floating-point sizes oscillate by <code>0.001pt</code>. Always quantize values or prefer <code>.visualEffect</code> and <code>onGeometryChange</code> (iOS 18+).</p>
        """,
        "quizzes": [
            {
                "question": "How does `List` differ from `ScrollView { LazyVStack }` when scrolling through 20,000 rows?",
                "correct_idx": 1,
                "options": [
                    {"text": "List disables all SwiftUI animations.", "reason": "Option A is wrong because List supports full row animations and transitions."},
                    {"text": "List actively recycles offscreen cells.", "reason": "Option B is correct: List is backed by UICollectionView cell reuse, keeping dirty memory bounded, whereas LazyVStack retains visited cell identities."},
                    {"text": "LazyVStack renders offscreen on GPU.", "reason": "Option C is wrong because LazyVStack defers offscreen rendering until visible."},
                    {"text": "List compiles rows into Objective-C.", "reason": "Option D is wrong because SwiftUI List rows remain Swift views."}
                ]
            },
            {
                "question": "What is the purpose of `PreferenceKey.reduce(value:nextValue:)` in SwiftUI?",
                "correct_idx": 3,
                "options": [
                    {"text": "Pushes environment values down the tree.", "reason": "Option A is wrong because Environment flows downward, whereas Preferences flow upward."},
                    {"text": "Cancels pending URLSession network tasks.", "reason": "Option B is wrong because PreferenceKey is strictly a view-tree propagation mechanism."},
                    {"text": "Encrypts UserDefaults storage keys.", "reason": "Option C is wrong because PreferenceKey has nothing to do with persistence."},
                    {"text": "Merges preference values from sibling views.", "reason": "Option D is correct: reduce combines values emitted by multiple child subtrees into a single ancestor value."}
                ]
            }
        ],
        "exercise": {
            "title": "PreferenceKey Associative Reducer & Scroll Viewport Windowing",
            "instructions": "1) Implement <code>MaxWidthPreferenceKey.reduce(value:nextValue:)</code> taking the maximum reported child width.<br>2) Implement <code>VisibleWindowCalculator.visibleRange(totalCount:offsetY:rowHeight:viewportHeight:)</code> returning the exact visible index range (clamped to <code>0..&lt;totalCount</code>) for a virtualized scroll container. Run tests.",
            "starter_code": """import Foundation

protocol SimplePreferenceKey {
    associatedtype Value
    static var defaultValue: Value { get }
    static func reduce(value: inout Value, nextValue: () -> Value)
}

enum MaxWidthPreferenceKey: SimplePreferenceKey {
    static let defaultValue: Double = 0.0

    // TODO: Update value to max(value, nextValue())
    static func reduce(value: inout Double, nextValue: () -> Double) {
        fatalError("TODO: Implement reduce")
    }
}

enum VisibleWindowCalculator {
    // TODO: Return the closed-open Range<Int> of visible row indices given scroll offsetY >= 0
    static func visibleRange(totalCount: Int, offsetY: Double, rowHeight: Double, viewportHeight: Double) -> Range<Int> {
        fatalError("TODO: Implement visibleRange")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.2 Verification Tests...")
    var acc = MaxWidthPreferenceKey.defaultValue
    MaxWidthPreferenceKey.reduce(value: &acc) { 84.0 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 142.5 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 96.0 }
    assert(acc == 142.5, "Test 1 Failed: Expected max width 142.5")

    let r1 = VisibleWindowCalculator.visibleRange(totalCount: 1000, offsetY: 250, rowHeight: 50, viewportHeight: 200)
    assert(r1 == 5..<9, "Test 2 Failed: Expected rows 5..<9, got \\(r1)")

    let r2 = VisibleWindowCalculator.visibleRange(totalCount: 10, offsetY: 420, rowHeight: 50, viewportHeight: 200)
    assert(r2 == 8..<10, "Test 3 Failed: Expected clamped rows 8..<10, got \\(r2)")
    print("  ✅ Challenge Passed: PreferenceKey reduction & scroll windowing verified")
}
runTests()""",
            "hints": [
                "In reduce: value = max(value, nextValue()).",
                "In visibleRange: guard totalCount > 0, rowHeight > 0 else { return 0..<0 }.",
                "first = max(0, Int(floor(max(0, offsetY) / rowHeight))); last = min(totalCount, Int(ceil((max(0, offsetY) + viewportHeight) / rowHeight)))."
            ],
            "solution_code": """import Foundation

protocol SimplePreferenceKey {
    associatedtype Value
    static var defaultValue: Value { get }
    static func reduce(value: inout Value, nextValue: () -> Value)
}

enum MaxWidthPreferenceKey: SimplePreferenceKey {
    static let defaultValue: Double = 0.0

    static func reduce(value: inout Double, nextValue: () -> Double) {
        value = max(value, nextValue())
    }
}

enum VisibleWindowCalculator {
    static func visibleRange(totalCount: Int, offsetY: Double, rowHeight: Double, viewportHeight: Double) -> Range<Int> {
        guard totalCount > 0, rowHeight > 0, viewportHeight > 0 else { return 0..<0 }
        let clampedOffset = max(0.0, offsetY)
        let first = min(totalCount, Int(floor(clampedOffset / rowHeight)))
        let last = min(totalCount, Int(ceil((clampedOffset + viewportHeight) / rowHeight)))
        return first..<max(first, last)
    }
}

func runTests() {
    print("🧪 Running Lesson 9.2 Verification Tests...")
    var acc = MaxWidthPreferenceKey.defaultValue
    MaxWidthPreferenceKey.reduce(value: &acc) { 84.0 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 142.5 }
    MaxWidthPreferenceKey.reduce(value: &acc) { 96.0 }
    assert(acc == 142.5)

    let r1 = VisibleWindowCalculator.visibleRange(totalCount: 1000, offsetY: 250, rowHeight: 50, viewportHeight: 200)
    assert(r1 == 5..<9)

    let r2 = VisibleWindowCalculator.visibleRange(totalCount: 10, offsetY: 420, rowHeight: 50, viewportHeight: 200)
    assert(r2 == 8..<10)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Associative <code>PreferenceKey</code> reduction merges arbitrary sibling layout measurements cleanly, while clamped viewport windowing underpins lazy scroll virtualization without overflowing bounds."
        },
        "primary_sources": [
            {"title": "Apple Developer: PreferenceKey", "url": "https://developer.apple.com/documentation/swiftui/preferencekey", "desc": "Official SwiftUI documentation on propagating child values upward."},
            {"title": "Apple Developer: ScrollView & Scroll Geometry", "url": "https://developer.apple.com/documentation/swiftui/scrollview", "desc": "Modern scrollPosition, scrollTargetBehavior, and onScrollGeometryChange APIs."}
        ],
        "coaching_prompt": "Now that your scrolling feeds stay silky at 120fps, let's master high-density data visualization in Lesson 9.3: Swift Charts, Canvas & 120fps Custom Graphics!"
    },

    # 0063: Lesson 9.3
    {
        "id": "0063",
        "slug": "0063-swift-charts-canvas-and-timelineview",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.3",
        "title": "Swift Charts, Canvas & 120fps Custom Graphics",
        "subtitle": "Declarative BarMark/LineMark/AreaMark & vectorized LinePlot, interactive chartXSelection, and immediate-mode Canvas + TimelineView.",
        "prev_url": "0062-scrollview-performance-and-preferencekey.html",
        "next_url": "0064-adaptive-navigation-transitions-and-drag-drop.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android Compose, custom 60/120fps charts and particle drawings drop down to Compose <code>Canvas(modifier)</code> (<code>DrawScope</code>) so you don't allocate thousands of composable layout nodes.</p>
        <p>In iOS, Apple provides <strong><code>Swift Charts</code></strong> for accessible, VoiceOver-ready declarative charts (with vectorized <strong><code>LinePlot</code></strong> for 10,000+ telemetry points) and <strong><code>Canvas</code> + <code>TimelineView</code></strong> for immediate-mode CoreGraphics rendering without polluting SwiftUI's <code>AttributeGraph</code>.</p>
        """,
        "core_sections_html": """
        <h3>1. Declarative Swift Charts &amp; Interactive Selection</h3>
        <pre><code>import Charts

Chart(samples) { point in
    AreaMark(x: .value("Time", point.timestamp), y: .value("Latency", point.ms))
        .foregroundStyle(.linearGradient(colors: [.blue.opacity(0.35), .clear], startPoint: .top, endPoint: .bottom))
    LineMark(x: .value("Time", point.timestamp), y: .value("Latency", point.ms))
        .interpolationMethod(.catmullRom)
}
.chartXSelection(value: $selectedTimestamp)</code></pre>
        <p>For 10,000+ samples (e.g., MetricKit CPU/memory timelines), use vectorized <strong><code>LinePlot</code> / <code>AreaPlot</code></strong> instead of iterating thousands of individual <code>LineMark</code> views!</p>

        <h3>2. Immediate-Mode Canvas + TimelineView</h3>
        <pre><code>TimelineView(.animation) { timeline in
    Canvas { context, size in
        // Draws directly into CGContext with ZERO SwiftUI child View nodes!
        let t = timeline.date.timeIntervalSinceReferenceDate
        let x = (sin(t) * 0.5 + 0.5) * size.width
        context.fill(Path(ellipseIn: CGRect(x: x - 6, y: size.height / 2 - 6, width: 12, height: 12)), with: .color(.blue))
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Rendering 5,000 Scatter Points as Individual <code>Circle()</code> Views Inside a <code>ZStack</code>:</strong> Creating thousands of SwiftUI <code>View</code> nodes exhausts CPU/memory inside <code>AttributeGraph</code> diffing and causes severe frame hitches. Use <code>Canvas</code> for custom high-density drawing or vectorized <code>LinePlot</code>/<code>PointPlot</code> in Swift Charts.</p>
        """,
        "quizzes": [
            {
                "question": "Why should you use `Canvas` instead of a `ZStack` with 2,000 `Circle()` views for a real-time sparkline or particle field?",
                "correct_idx": 1,
                "options": [
                    {"text": "ZStack cannot render circular shapes.", "reason": "Option A is wrong because ZStack renders Circle views normally, just with high per-node overhead."},
                    {"text": "Canvas draws immediately without AttributeGraph nodes.", "reason": "Option B is correct: Canvas issues direct CoreGraphics draw commands for all 2,000 shapes in one SwiftUI node."},
                    {"text": "Canvas executes code on the network thread.", "reason": "Option C is wrong because Canvas executes drawing synchronously during render passes."},
                    {"text": "Canvas bypasses Dynamic Type automatically.", "reason": "Option D is wrong because Canvas is a custom graphics surface, not a text scaling rule."}
                ]
            },
            {
                "question": "In Swift Charts, which API handles 20,000+ continuous data points far faster than `ForEach` + `LineMark`?",
                "correct_idx": 2,
                "options": [
                    {"text": "UIViewRepresentable with Storyboards.", "reason": "Option A is wrong because Storyboards are legacy UIKit builders, not high-density charting."},
                    {"text": "Nested ScrollViews with GeometryReader.", "reason": "Option B is wrong because GeometryReader adds layout pass overhead."},
                    {"text": "Vectorized LinePlot and AreaPlot.", "reason": "Option C is correct: LinePlot/AreaPlot evaluate vectorized closures over collections without per-point ChartContent nodes."},
                    {"text": "Manual Core Data fetch requests.", "reason": "Option D is wrong because Core Data is a persistence layer, not a chart renderer."}
                ]
            }
        ],
        "exercise": {
            "title": "Chart Coordinate Scale Mapper & LTTB Downsampler",
            "instructions": "Implement <code>ChartMath.mapToCanvasY(value:minValue:maxValue:height:)</code> (mapping <code>minValue</code> to <code>height</code> and <code>maxValue</code> to <code>0</code> in flipped CoreGraphics coordinates) and <code>downsample(_:bucketSize:)</code> averaging chunks for fast rendering. Run tests.",
            "starter_code": """import Foundation

enum ChartMath {
    // TODO: Map value in [minValue, maxValue] to flipped Y coordinate in [height, 0]
    // Return height / 2 if maxValue <= minValue
    static func mapToCanvasY(value: Double, minValue: Double, maxValue: Double, height: Double) -> Double {
        fatalError("TODO: Implement mapToCanvasY")
    }

    // TODO: Average consecutive chunks of size `bucketSize` (last chunk may be smaller)
    static func downsample(_ samples: [Double], bucketSize: Int) -> [Double] {
        fatalError("TODO: Implement downsample")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.3 Verification Tests...")
    assert(ChartMath.mapToCanvasY(value: 0, minValue: 0, maxValue: 100, height: 200) == 200, "Test 1 Failed: Min value maps to bottom (height)")
    assert(ChartMath.mapToCanvasY(value: 100, minValue: 0, maxValue: 100, height: 200) == 0, "Test 2 Failed: Max value maps to top (0)")
    assert(ChartMath.mapToCanvasY(value: 50, minValue: 0, maxValue: 100, height: 200) == 100, "Test 3 Failed")

    let reduced = ChartMath.downsample([10, 20, 30, 40, 50], bucketSize: 2)
    assert(reduced == [15, 35, 50], "Test 4 Failed: Expected [15, 35, 50], got \\(reduced)")
    print("  ✅ Challenge Passed: Chart coordinate mapping & downsampling verified")
}
runTests()""",
            "hints": [
                "Normalize t = (value - minValue) / (maxValue - minValue); return height * (1.0 - t).",
                "In downsample: guard bucketSize > 0 else { return samples }; stride(from: 0, to: samples.count, by: bucketSize)."
            ],
            "solution_code": """import Foundation

enum ChartMath {
    static func mapToCanvasY(value: Double, minValue: Double, maxValue: Double, height: Double) -> Double {
        guard maxValue > minValue else { return height / 2.0 }
        let normalized = (value - minValue) / (maxValue - minValue)
        return height * (1.0 - normalized)
    }

    static func downsample(_ samples: [Double], bucketSize: Int) -> [Double] {
        guard bucketSize > 0, !samples.isEmpty else { return samples }
        var result: [Double] = []
        result.reserveCapacity((samples.count + bucketSize - 1) / bucketSize)
        for start in stride(from: 0, to: samples.count, by: bucketSize) {
            let end = min(start + bucketSize, samples.count)
            let slice = samples[start..<end]
            let avg = slice.reduce(0.0, +) / Double(slice.count)
            result.append(avg)
        }
        return result
    }
}

func runTests() {
    print("🧪 Running Lesson 9.3 Verification Tests...")
    assert(ChartMath.mapToCanvasY(value: 0, minValue: 0, maxValue: 100, height: 200) == 200)
    assert(ChartMath.mapToCanvasY(value: 100, minValue: 0, maxValue: 100, height: 200) == 0)
    assert(ChartMath.mapToCanvasY(value: 50, minValue: 0, maxValue: 100, height: 200) == 100)

    let reduced = ChartMath.downsample([10, 20, 30, 40, 50], bucketSize: 2)
    assert(reduced == [15, 35, 50])
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "iOS UI coordinates put <code>y = 0</code> at the top edge, so chart heights invert normalized values; combining exact coordinate math with chunk downsampling keeps custom <code>Canvas</code> telemetry charts locked at 120fps."
        },
        "primary_sources": [
            {"title": "Apple Developer: Swift Charts", "url": "https://developer.apple.com/documentation/charts", "desc": "Official documentation for Chart, Mark types, LinePlot, and chartXSelection."},
            {"title": "Apple Developer: Canvas & TimelineView", "url": "https://developer.apple.com/documentation/swiftui/canvas", "desc": "Immediate-mode CoreGraphics rendering inside SwiftUI."}
        ],
        "coaching_prompt": "Swift Charts + Canvas give you both accessible analytics and 120fps custom drawing. Next is Lesson 9.4: Adaptive Navigation, Custom Transitions & Transferable Drag-Drop."
    },

    # 0064: Lesson 9.4
    {
        "id": "0064",
        "slug": "0064-adaptive-navigation-transitions-and-drag-drop",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.4",
        "title": "Adaptive Navigation, Custom Transitions & Transferable Drag-Drop",
        "subtitle": "NavigationSplitView & adaptive Tab/TabSection, ViewThatFits & AnyLayout, the Transition protocol, and Transferable drag-and-drop.",
        "prev_url": "0063-swift-charts-canvas-and-timelineview.html",
        "next_url": "0065-cloudkit-and-cksyncengine.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, adaptive multi-pane UIs use Material 3 <code>NavigationSuiteScaffold</code> and <code>ListDetailPaneScaffold</code>, with <code>AnimatedContent</code> for transitions and <code>dragAndDropSource/Target</code>.</p>
        <p>In modern SwiftUI, <strong><code>NavigationSplitView</code></strong> and the iOS 18+/26 <strong><code>Tab</code> / <code>TabSection</code></strong> API automatically morph between an iPhone bottom tab bar and an iPad/Mac floating sidebar. Meanwhile, <strong><code>ViewThatFits</code></strong>, the modern <strong><code>Transition</code></strong> protocol, and <strong><code>Transferable</code></strong> handle responsive layouts, GPU-accelerated transitions, and type-safe drag-and-drop.</p>
        """,
        "core_sections_html": """
        <h3>1. Adaptive Layouts &amp; NavigationSplitView</h3>
        <pre><code>TabView(selection: $selectedTab) {
    Tab("Dashboard", systemImage: "gauge", value: .dashboard) { DashboardView() }
    TabSection("Operations") {
        Tab("Incidents", systemImage: "bolt.shield", value: .incidents) { IncidentsView() }
    }
}
.tabViewStyle(.sidebarAdaptable)

// Pick first child that fits without clipping:
ViewThatFits(in: .horizontal) {
    HStack { ActionButtons() }
    VStack { ActionButtons() }
}</code></pre>

        <h3>2. Custom Transition Protocol &amp; Transferable</h3>
        <pre><code>struct SlideBlurTransition: Transition {
    func body(content: Content, phase: TransitionPhase) -&gt; some View {
        content
            .opacity(phase.isIdentity ? 1 : 0)
            .offset(y: phase == .willAppear ? 16 : (phase == .didDisappear ? -16 : 0))
    }
}

struct MetricSnapshot: Codable, Transferable {
    let title: String
    static var transferRepresentation: some TransferRepresentation {
        CodableRepresentation(contentType: .json)
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Hardcoding <code>UIDevice.current.userInterfaceIdiom == .pad</code> Checks:</strong> Checking device idiom breaks iPad Slide Over / Split View multitasking and Stage Manager, where an iPad app window has a <code>.compact</code> horizontal size class! Always branch layouts on <code>@Environment(\\.horizontalSizeClass)</code> or <code>ViewThatFits</code>.</p>
        """,
        "quizzes": [
            {
                "question": "Why should adaptive SwiftUI layouts inspect `@Environment(\\.horizontalSizeClass)` instead of `UIDevice.current.userInterfaceIdiom`?",
                "correct_idx": 0,
                "options": [
                    {"text": "Handles iPad multitasking compact windows.", "reason": "Option A is correct: an iPad app in Split View or Slide Over has a compact horizontalSizeClass even though userInterfaceIdiom is .pad."},
                    {"text": "userInterfaceIdiom fails to compile in Swift.", "reason": "Option B is wrong because UIDevice compiles fine, it just models hardware rather than window size."},
                    {"text": "horizontalSizeClass enables Keychain encryption.", "reason": "Option C is wrong because size classes govern UI layout adaptation."},
                    {"text": "UIDevice blocks App Store TestFlight.", "reason": "Option D is wrong because UIDevice is allowed, just ergonomically wrong for adaptive windows."}
                ]
            },
            {
                "question": "What does adopting Swift's `Transferable` protocol enable cleanly across SwiftUI?",
                "correct_idx": 2,
                "options": [
                    {"text": "Automatic SQLite database schema migrations.", "reason": "Option A is wrong because schema migrations use VersionedSchema or GRDB."},
                    {"text": "Zero-copy stack allocation on arrays.", "reason": "Option B is wrong because stack arrays use InlineArray."},
                    {"text": "Drag-and-drop, ShareLink, and copy-paste.", "reason": "Option C is correct: Transferable unifies UTType serialization for .draggable(), .dropDestination(), ShareLink, and pasteboard."},
                    {"text": "Direct Objective-C dynamic message dispatch.", "reason": "Option D is wrong because Transferable is a pure Swift protocol."}
                ]
            }
        ],
        "exercise": {
            "title": "Adaptive Window Layout Resolver & Transition Phase Interpolator",
            "instructions": "1) Implement <code>AdaptiveRouter.layoutMode(horizontalSizeClass:availableWidth:)</code> returning <code>.threeColumnSplit</code> for <code>.regular</code> with width &gt;= 900, <code>.twoColumnSplit</code> for <code>.regular</code> under 900, and <code>.compactStack</code> for <code>.compact</code>.<br>2) Implement <code>TransitionMath.opacity(for:)</code> across <code>.willAppear</code> (0), <code>.identity</code> (1), and <code>.didDisappear</code> (0). Run tests.",
            "starter_code": """import Foundation

enum SizeClass { case compact, regular }
enum NavigationMode: Equatable { case compactStack, twoColumnSplit, threeColumnSplit }
enum TransitionPhaseSim { case willAppear, identity, didDisappear }

enum AdaptiveRouter {
    // TODO: Return appropriate NavigationMode based on horizontalSizeClass and availableWidth
    static func layoutMode(horizontalSizeClass: SizeClass, availableWidth: Double) -> NavigationMode {
        fatalError("TODO: Implement layoutMode")
    }

    // TODO: Return 1.0 for .identity, 0.0 for .willAppear and .didDisappear
    static func opacity(for phase: TransitionPhaseSim) -> Double {
        fatalError("TODO: Implement opacity")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.4 Verification Tests...")
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .compact, availableWidth: 390) == .compactStack, "Test 1 Failed")
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .regular, availableWidth: 700) == .twoColumnSplit, "Test 2 Failed")
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .regular, availableWidth: 1100) == .threeColumnSplit, "Test 3 Failed")
    assert(AdaptiveRouter.opacity(for: .willAppear) == 0.0, "Test 4 Failed")
    assert(AdaptiveRouter.opacity(for: .identity) == 1.0, "Test 5 Failed")
    assert(AdaptiveRouter.opacity(for: .didDisappear) == 0.0, "Test 6 Failed")
    print("  ✅ Challenge Passed: Adaptive layout & TransitionPhase verified")
}
runTests()""",
            "hints": [
                "If horizontalSizeClass == .compact -> return .compactStack.",
                "If .regular -> return availableWidth >= 900 ? .threeColumnSplit : .twoColumnSplit.",
                "In opacity(for:): phase == .identity ? 1.0 : 0.0."
            ],
            "solution_code": """import Foundation

enum SizeClass { case compact, regular }
enum NavigationMode: Equatable { case compactStack, twoColumnSplit, threeColumnSplit }
enum TransitionPhaseSim { case willAppear, identity, didDisappear }

enum AdaptiveRouter {
    static func layoutMode(horizontalSizeClass: SizeClass, availableWidth: Double) -> NavigationMode {
        switch horizontalSizeClass {
        case .compact:
            return .compactStack
        case .regular:
            return availableWidth >= 900 ? .threeColumnSplit : .twoColumnSplit
        }
    }

    static func opacity(for phase: TransitionPhaseSim) -> Double {
        phase == .identity ? 1.0 : 0.0
    }
}

func runTests() {
    print("🧪 Running Lesson 9.4 Verification Tests...")
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .compact, availableWidth: 390) == .compactStack)
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .regular, availableWidth: 700) == .twoColumnSplit)
    assert(AdaptiveRouter.layoutMode(horizontalSizeClass: .regular, availableWidth: 1100) == .threeColumnSplit)
    assert(AdaptiveRouter.opacity(for: .willAppear) == 0.0)
    assert(AdaptiveRouter.opacity(for: .identity) == 1.0)
    assert(AdaptiveRouter.opacity(for: .didDisappear) == 0.0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Driving layout modes from size classes and window widths handles iPhone, iPad Split View, and macOS seamlessly, while SwiftUI's phase-based <code>Transition</code> protocol cleanly separates insertion, identity, and removal animations."
        },
        "primary_sources": [
            {"title": "Apple Developer: NavigationSplitView & Tab", "url": "https://developer.apple.com/documentation/swiftui/navigationsplitview", "desc": "Adaptive multi-column navigation and sidebar-adaptable tab bars."},
            {"title": "Apple Developer: Transferable & Transition", "url": "https://developer.apple.com/documentation/coretransferable/transferable", "desc": "Unified drag-and-drop, ShareLink, and custom SwiftUI transitions."}
        ],
        "coaching_prompt": "With adaptive UI mastered, let's bridge local persistence to Apple's first-party cloud sync in Lesson 9.5: CloudKit & CKSyncEngine!"
    },

    # 0065: Lesson 9.5
    {
        "id": "0065",
        "slug": "0065-cloudkit-and-cksyncengine",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.5",
        "title": "First-Party Cloud Sync: CloudKit & CKSyncEngine",
        "subtitle": "CKDatabase private/shared zones, CKSyncEngine state tokens & batching, SwiftData CloudKit schema constraints, and serverRecordChanged conflict resolution.",
        "prev_url": "0064-adaptive-navigation-transitions-and-drag-drop.html",
        "next_url": "0066-docc-previews-and-swiftui-inspector.html",
        "cheatsheet_url": "../reference/persistence-and-storage-cheatsheet.html",
        "cheatsheet_title": "Persistence & Storage Cheatsheet",
        "android_anchor_html": """
        <p>In Android, multi-device user sync usually requires standing up Firestore, custom REST sync endpoints, or Google Drive AppData.</p>
        <p>In Apple's ecosystem, <strong>CloudKit</strong> stores encrypted user data directly in the user's iCloud quota with zero backend server costs. Senior engineers know both <strong>SwiftData + CloudKit</strong> constraints and how to build deterministic, custom sync layers on top of <strong><code>CKSyncEngine</code></strong> (handling change tokens, batching, and <code>serverRecordChanged</code> merges).</p>
        """,
        "core_sections_html": """
        <h3>1. SwiftData + CloudKit Schema Rules</h3>
        <p>If enabling automatic CloudKit sync on a SwiftData <code>@Model</code>, CloudKit enforces strict schema rules:</p>
        <ul>
          <li><strong>No <code>@Attribute(.unique)</code>:</strong> Distributed offline devices cannot enforce global uniqueness without server coordination.</li>
          <li><strong>Every property needs a default value or Optionality:</strong> Enables schema evolution across devices running older app versions.</li>
          <li><strong>All relationships must be Optional</strong> (with explicit inverses).</li>
        </ul>

        <h3>2. CKSyncEngine (iOS 17+ / 2027 Standard for SQLite/Custom Stores)</h3>
        <pre><code>// CKSyncEngine automatically manages CKServerChangeToken persistence,
// network reachability retries, silent push subscriptions, and batch splitting!
final class SyncCoordinator: CKSyncEngineDelegate {
    func handleEvent(_ event: CKSyncEngine.Event) {
        switch event {
        case .stateUpdate(let update): saveStateSerialization(update.stateSerialization)
        case .fetchedRecordZoneChanges(let changes): applyRemoteChanges(changes)
        case .sentRecordZoneChanges(let sent): handleFailuresAndConflicts(sent.failedRecordSaves)
        default: break
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Ignoring <code>CKError.serverRecordChanged</code> or Dropping <code>CKSyncEngine.State.Serialization</code>:</strong> If you overwrite the server record blindly on <code>serverRecordChanged</code>, you silently erase edits made on the user's iPad! Always extract <code>error.serverRecord</code>, merge fields deterministically (or preserve the server's <code>recordChangeTag</code>), and persist <code>stateSerialization</code> immediately on every <code>.stateUpdate</code> event.</p>
        """,
        "quizzes": [
            {
                "question": "Why does CloudKit forbid `@Attribute(.unique)` on SwiftData models synced via iCloud?",
                "correct_idx": 1,
                "options": [
                    {"text": "SwiftData macros cannot parse property attributes.", "reason": "Option A is wrong because @Attribute(.unique) works fine for local-only SwiftData stores."},
                    {"text": "Offline devices cannot guarantee global uniqueness.", "reason": "Option B is correct: two offline devices could insert the same unique key simultaneously, making atomic uniqueness impossible without a central lock."},
                    {"text": "CloudKit only supports Objective-C NSObjects.", "reason": "Option C is wrong because CloudKit works natively with Swift and CKSyncEngine."},
                    {"text": "Unique constraints disable Face ID Passkeys.", "reason": "Option D is wrong because CloudKit schemas are unrelated to AuthenticationServices."}
                ]
            },
            {
                "question": "When handling `CKError.serverRecordChanged`, what must you update before retrying the CloudKit save?",
                "correct_idx": 3,
                "options": [
                    {"text": "Delete the local SQLite database entirely.", "reason": "Option A is wrong and causes catastrophic local data loss."},
                    {"text": "Switch the container from private to public.", "reason": "Option B is wrong because changing database scope moves data to an unauthenticated public space."},
                    {"text": "Disable App Transport Security in Info.plist.", "reason": "Option C is wrong because CloudKit uses Apple's encrypted system transport."},
                    {"text": "Merge onto serverRecord preserving its changeTag.", "reason": "Option D is correct: CloudKit uses optimistic concurrency via recordChangeTag; you must apply merged changes onto the latest serverRecord."}
                ]
            }
        ],
        "exercise": {
            "title": "CloudKit Schema Auditor & serverRecordChanged Conflict Merger",
            "instructions": "1) Implement <code>CloudKitSchemaAuditor.isSyncCompatible(_:)</code> ensuring zero unique attributes, all properties defaulted/optional, and all relationships optional.<br>2) Implement <code>RecordConflictMerger.merge(local:server:)</code> keeping the server's <code>changeTag</code>, choosing whichever record has the newer <code>modifiedAt</code> timestamp (server wins ties). Run tests.",
            "starter_code": """import Foundation

struct ModelSchemaField {
    let name: String
    let isUnique: Bool
    let hasDefaultOrOptional: Bool
    let isRelationship: Bool
    let isOptionalRelationship: Bool
}

struct CloudRecord: Equatable {
    let id: String
    let changeTag: String
    let title: String
    let modifiedAt: TimeInterval
}

enum CloudKitSchemaAuditor {
    // TODO: Return true iff NO field is unique, every field hasDefaultOrOptional,
    // and every relationship field has isOptionalRelationship == true
    static func isSyncCompatible(_ fields: [ModelSchemaField]) -> Bool {
        fatalError("TODO: Implement isSyncCompatible")
    }
}

enum RecordConflictMerger {
    // TODO: Merge local & server records:
    // Always preserve server.changeTag! Choose title & modifiedAt from local ONLY if local.modifiedAt > server.modifiedAt.
    static func merge(local: CloudRecord, server: CloudRecord) -> CloudRecord {
        fatalError("TODO: Implement merge")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.5 Verification Tests...")
    let validSchema = [
        ModelSchemaField(name: "title", isUnique: false, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false),
        ModelSchemaField(name: "tags", isUnique: false, hasDefaultOrOptional: true, isRelationship: true, isOptionalRelationship: true)
    ]
    let badSchema = [
        ModelSchemaField(name: "email", isUnique: true, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false)
    ]
    assert(CloudKitSchemaAuditor.isSyncCompatible(validSchema) == true, "Test 1 Failed")
    assert(CloudKitSchemaAuditor.isSyncCompatible(badSchema) == false, "Test 2 Failed")

    let localNewer = CloudRecord(id: "r1", changeTag: "tag-old", title: "Local Edit", modifiedAt: 200)
    let serverOlder = CloudRecord(id: "r1", changeTag: "tag-v5", title: "Server Edit", modifiedAt: 150)
    let merged = RecordConflictMerger.merge(local: localNewer, server: serverOlder)
    assert(merged == CloudRecord(id: "r1", changeTag: "tag-v5", title: "Local Edit", modifiedAt: 200), "Test 3 Failed")
    print("  ✅ Challenge Passed: CloudKit schema audit & conflict merge verified")
}
runTests()""",
            "hints": [
                "Use fields.allSatisfy { !$0.isUnique && $0.hasDefaultOrOptional && (!$0.isRelationship || $0.isOptionalRelationship) }.",
                "In merge: let localWins = local.modifiedAt > server.modifiedAt; return CloudRecord(id: server.id, changeTag: server.changeTag, title: localWins ? local.title : server.title, modifiedAt: localWins ? local.modifiedAt : server.modifiedAt)."
            ],
            "solution_code": """import Foundation

struct ModelSchemaField {
    let name: String
    let isUnique: Bool
    let hasDefaultOrOptional: Bool
    let isRelationship: Bool
    let isOptionalRelationship: Bool
}

struct CloudRecord: Equatable {
    let id: String
    let changeTag: String
    let title: String
    let modifiedAt: TimeInterval
}

enum CloudKitSchemaAuditor {
    static func isSyncCompatible(_ fields: [ModelSchemaField]) -> Bool {
        fields.allSatisfy { field in
            !field.isUnique
            && field.hasDefaultOrOptional
            && (!field.isRelationship || field.isOptionalRelationship)
        }
    }
}

enum RecordConflictMerger {
    static func merge(local: CloudRecord, server: CloudRecord) -> CloudRecord {
        let localWins = local.modifiedAt > server.modifiedAt
        return CloudRecord(
            id: server.id,
            changeTag: server.changeTag,
            title: localWins ? local.title : server.title,
            modifiedAt: localWins ? local.modifiedAt : server.modifiedAt
        )
    }
}

func runTests() {
    print("🧪 Running Lesson 9.5 Verification Tests...")
    let validSchema = [
        ModelSchemaField(name: "title", isUnique: false, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false),
        ModelSchemaField(name: "tags", isUnique: false, hasDefaultOrOptional: true, isRelationship: true, isOptionalRelationship: true)
    ]
    let badSchema = [
        ModelSchemaField(name: "email", isUnique: true, hasDefaultOrOptional: true, isRelationship: false, isOptionalRelationship: false)
    ]
    assert(CloudKitSchemaAuditor.isSyncCompatible(validSchema) == true)
    assert(CloudKitSchemaAuditor.isSyncCompatible(badSchema) == false)

    let localNewer = CloudRecord(id: "r1", changeTag: "tag-old", title: "Local Edit", modifiedAt: 200)
    let serverOlder = CloudRecord(id: "r1", changeTag: "tag-v5", title: "Server Edit", modifiedAt: 150)
    let merged = RecordConflictMerger.merge(local: localNewer, server: serverOlder)
    assert(merged == CloudRecord(id: "r1", changeTag: "tag-v5", title: "Local Edit", modifiedAt: 200))
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Validating CloudKit schema constraints upfront prevents silent sync-disablement in production, while preserving <code>server.changeTag</code> during <code>serverRecordChanged</code> merges satisfies CloudKit's optimistic locking check."
        },
        "primary_sources": [
            {"title": "Apple Developer: CKSyncEngine", "url": "https://developer.apple.com/documentation/cloudkit/cksyncengine", "desc": "Apple's modern structured sync engine for custom SQLite/GRDB and file stores."},
            {"title": "Apple Developer: Syncing Model Data Across a Person's Devices", "url": "https://developer.apple.com/documentation/swiftdata/syncing-model-data-across-a-persons-devices", "desc": "Official SwiftData + CloudKit schema requirements."}
        ],
        "coaching_prompt": "One final mastery module before your Capstone: Lesson 9.6 — DocC Documentation, #Preview Workflows & SwiftUI Inspector!"
    },

    # 0066: Lesson 9.6
    {
        "id": "0066",
        "slug": "0066-docc-previews-and-swiftui-inspector",
        "phase_num": 9,
        "phase_title": "Phase 9: Advanced SwiftUI DSL, Graphics, CloudKit & Tooling",
        "lesson_num": "Lesson 9.6",
        "title": "DocC Documentation, #Preview Workflows & SwiftUI Inspector",
        "subtitle": "Authoring interactive DocC catalogs for SPM/XCFrameworks, fast mocked #Preview macros (@Previewable), and Instruments SwiftUI Inspector profiling.",
        "prev_url": "0065-cloudkit-and-cksyncengine.html",
        "next_url": "0067-capstone-portfolio-blueprint.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, library docs use Dokka, fast UI iteration uses Compose <code>@Preview</code>, and recompositions are counted in Android Studio Layout Inspector.</p>
        <p>In iOS, Apple's ecosystem mirrors and extends these with <strong><code>DocC</code></strong> (generating Apple-style docs, tutorials, and symbol graphs right inside Xcode and static websites via <code>swift-docc-plugin</code>), modern <strong><code>#Preview</code> + <code>@Previewable</code></strong> macros, and the <strong>SwiftUI Inspector / Instruments SwiftUI template</strong> for hunting frame drops and excess view updates.</p>
        """,
        "core_sections_html": """
        <h3>1. Interactive Previews Without Booting App Singletons</h3>
        <pre><code>#Preview("Offline State", traits: .sizeThatFitsLayout) {
    @Previewable @State var filter = "All"
    DashboardView(
        store: .mock(items: [.sampleCritical]),
        filter: $filter
    )
}</code></pre>
        <p>Notice <strong><code>@Previewable</code></strong> (Xcode 16+): it lets you declare <code>@State</code> directly inside <code>#Preview</code> without wrapping everything in a disposable helper struct!</p>

        <h3>2. DocC Documentation Catalogs (.docc)</h3>
        <ul>
          <li><strong>Symbol Links &amp; Callouts:</strong> Use double-backticks <code>``PulseEngine/sync()``</code>, <code>- Parameters:</code>, <code>- Throws:</code>, and <code>&gt; Note:</code> inside <code>///</code> doc comments.</li>
          <li><strong>Static Site Publishing:</strong> Run <code>swift package generate-documentation --target PulseSDK --transform-for-static-hosting</code> in CI to publish interactive docs for internal SPM modules and XCFrameworks.</li>
          <li><strong>SwiftUI Inspector &amp; Instruments:</strong> Profile <em>View Body</em> counts, <em>Core Animation Commits</em>, and <em>Cause &amp; Effect Graphs</em> in Instruments' SwiftUI track to verify zero redundant body evaluations.</li>
        </ul>
        """,
        "failure_mode_html": """
        <p><strong>Letting <code>#Preview</code> Boot Real Network, Keychain, or SQLite Stacks:</strong> If your view's default initializer instantiates production <code>URLSession</code> or <code>ModelContainer(for:)</code> on disk, Xcode Previews become sluggish and crash randomly. Inject lightweight in-memory fakes/mocks via protocol or closure dependencies so previews render in &lt;200ms.</p>
        """,
        "quizzes": [
            {
                "question": "What boilerplate does the `@Previewable` macro eliminate inside Xcode `#Preview` blocks?",
                "correct_idx": 1,
                "options": [
                    {"text": "Declaring an AppIntent perform method.", "reason": "Option A is wrong because @Previewable is strictly a SwiftUI Preview state helper."},
                    {"text": "Wrapping @State in a helper struct.", "reason": "Option B is correct: @Previewable allows @State and @FocusState directly inside a #Preview closure."},
                    {"text": "Generating Info.plist privacy keys.", "reason": "Option C is wrong because privacy declarations live in PrivacyInfo.xcprivacy."},
                    {"text": "Writing swift-format CI workflows.", "reason": "Option D is wrong because @Previewable has nothing to do with formatting CI."}
                ]
            },
            {
                "question": "What does `swift-docc-plugin` enable for multi-module SPM libraries and XCFrameworks?",
                "correct_idx": 0,
                "options": [
                    {"text": "Static HTML API docs from Swift comments.", "reason": "Option A is correct: swift-docc-plugin compiles /// doc comments and .docc articles into hostable interactive documentation sites."},
                    {"text": "Automatic Android AAR bytecode export.", "reason": "Option B is wrong because DocC generates documentation, not Android binaries."},
                    {"text": "Runtime SQLite encryption via CryptoKit.", "reason": "Option C is wrong because DocC is a build-time documentation compiler."},
                    {"text": "Bypassing TestFlight provisioning profiles.", "reason": "Option D is wrong because DocC does not interact with code signing."}
                ]
            }
        ],
        "exercise": {
            "title": "DocC Symbol Link Extractor & Preview Readiness Auditor",
            "instructions": "1) Implement <code>DocCValidator.extractSymbolLinks(from:)</code> extracting every symbol name wrapped in double-backticks (<code>``SymbolName``</code>).<br>2) Implement <code>PreviewAuditor.isPreviewSafe(usesInMemoryStore:usesLiveNetwork:)</code> returning true only when <code>usesInMemoryStore</code> is true and <code>usesLiveNetwork</code> is false. Run tests.",
            "starter_code": """import Foundation

enum DocCValidator {
    // TODO: Extract all substrings enclosed in ``...`` (e.g., "Calls ``SyncEngine/run()`` and ``AuthToken``" -> ["SyncEngine/run()", "AuthToken"])
    static func extractSymbolLinks(from docComment: String) -> [String] {
        fatalError("TODO: Implement extractSymbolLinks")
    }
}

enum PreviewAuditor {
    // TODO: Return true iff usesInMemoryStore == true && usesLiveNetwork == false
    static func isPreviewSafe(usesInMemoryStore: Bool, usesLiveNetwork: Bool) -> Bool {
        fatalError("TODO: Implement isPreviewSafe")
    }
}

func runTests() {
    print("🧪 Running Lesson 9.6 Verification Tests...")
    let doc = "Coordinates ``PulseEngine/sync()`` using ``MockStore`` before rendering."
    assert(DocCValidator.extractSymbolLinks(from: doc) == ["PulseEngine/sync()", "MockStore"], "Test 1 Failed")
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: false) == true, "Test 2 Failed")
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: true) == false, "Test 3 Failed")
    print("  ✅ Challenge Passed: DocC symbol extraction & Preview isolation verified")
}
runTests()""",
            "hints": [
                "Split docComment by \"``\" — odd-indexed elements (1, 3, 5...) are the symbol links when delimiters are balanced!",
                "Check `parts.count >= 3` and iterate `stride(from: 1, to: parts.count - (parts.count % 2 == 0 ? 1 : 0), by: 2)` or use NSRegularExpression/Regex `try Regex(\"``([^`]+)``\")`."
            ],
            "solution_code": """import Foundation

enum DocCValidator {
    static func extractSymbolLinks(from docComment: String) -> [String] {
        var links: [String] = []
        var searchStart = docComment.startIndex
        while let openRange = docComment.range(of: "``", range: searchStart..<docComment.endIndex),
              let closeRange = docComment.range(of: "``", range: openRange.upperBound..<docComment.endIndex) {
            let symbol = String(docComment[openRange.upperBound..<closeRange.lowerBound])
            if !symbol.isEmpty {
                links.append(symbol)
            }
            searchStart = closeRange.upperBound
        }
        return links
    }
}

enum PreviewAuditor {
    static func isPreviewSafe(usesInMemoryStore: Bool, usesLiveNetwork: Bool) -> Bool {
        usesInMemoryStore && !usesLiveNetwork
    }
}

func runTests() {
    print("🧪 Running Lesson 9.6 Verification Tests...")
    let doc = "Coordinates ``PulseEngine/sync()`` using ``MockStore`` before rendering."
    assert(DocCValidator.extractSymbolLinks(from: doc) == ["PulseEngine/sync()", "MockStore"])
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: false) == true)
    assert(PreviewAuditor.isPreviewSafe(usesInMemoryStore: true, usesLiveNetwork: true) == false)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "<code>DocC</code> symbol links verify API references at build time (catching broken refactors in documentation), while enforcing in-memory mocked dependencies keeps Xcode <code>#Preview</code> iterations instant."
        },
        "primary_sources": [
            {"title": "Apple Developer: DocC Documentation", "url": "https://developer.apple.com/documentation/docc", "desc": "Official Apple guide on writing .docc catalogs, tutorials, and symbol links."},
            {"title": "Apple Developer: Previews in Xcode (@Previewable)", "url": "https://developer.apple.com/documentation/swiftui/previews-in-xcode", "desc": "Interactive #Preview macros, traits, and @Previewable state."}
        ],
        "coaching_prompt": "You've completed Phase 9! Proceed to Milestone 10.1 to inspect the complete 67-lesson PulseFlow Enterprise Capstone blueprint!"
    }
]

print("Phase 9 SwiftUI Mastery data loaded (Lessons 9.1 - 9.6).")
