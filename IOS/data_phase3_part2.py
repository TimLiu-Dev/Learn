# Phase 3 Continued: Lessons 0022 - 0027

PHASE3_PART2 = [
    # 0022: Lesson 3.4
    {
        "id": "0022",
        "slug": "0022-layout-mastery-and-custom-layouts",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.4",
        "title": "Layout Mastery & The Custom Layout Protocol",
        "subtitle": "The 3-step layout negotiation algorithm, avoiding GeometryReader performance traps, and building custom Layout containers.",
        "prev_url": "0021-observation-framework.html",
        "next_url": "0023-type-safe-navigation.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, custom layouts are built by implementing the <code>Layout(content) { measurables, constraints -> ... }</code> measuring and placing pass.</p>
        <p>In SwiftUI (iOS 16+), the <strong><code>Layout</code> protocol</strong> gives Senior engineers direct control over child sizing and placement via <code>sizeThatFits</code> and <code>placeSubviews</code>. This completely eliminates heavy, layout-thrashing <code>GeometryReader</code> wrappers.</p>
        """,
        "core_sections_html": """
        <h3>1. The 3-Step Layout Negotiation Algorithm</h3>
        <ol>
          <li><strong>Parent proposes size:</strong> The parent container proposes a size (e.g. 300x100) to the child.</li>
          <li><strong>Child chooses its size:</strong> The child determines its own actual dimensions based on its content (e.g. Text chooses 240x40).</li>
          <li><strong>Parent places the child:</strong> The parent positions the child within its coordinate space (defaulting to center alignment).</li>
        </ol>

        <h3>2. Implementing the Custom Layout Protocol</h3>
        <pre><code>struct FlowLayout: Layout {
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        // Calculate container bounds
        return CGSize(width: proposal.width ?? 300, height: 200)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var origin = bounds.origin
        for subview in subviews {
            subview.place(at: origin, proposal: .unspecified)
            origin.x += subview.sizeThatFits(.unspecified).width + 8
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Overusing GeometryReader for Simple Sizing:</strong> GeometryReader eagerly expands to fill all available parent space, breaking layout flow and forcing extra passes. Avoid it whenever the <code>Layout</code> protocol or <code>.frame(maxWidth: .infinity)</code> suffices.</p>
        """,
        "quizzes": [
            {
                "question": "In SwiftUI layout negotiation, who has final authority over a child view's actual rendered size?",
                "correct_idx": 1,
                "options": [
                    {"text": "The parent container view.", "reason": "Option A is wrong because the parent only proposes a size."},
                    {"text": "The child view itself.", "reason": "Option B is correct: in SwiftUI, the child view determines its own size in response to the proposal."},
                    {"text": "The Darwin kernel window.", "reason": "Option C is wrong because the OS provides screen bounds, not child view layout."},
                    {"text": "The GPU Metal rasterizer.", "reason": "Option D is wrong because rasterization occurs after layout resolution."}
                ]
            },
            {
                "question": "What is the primary performance issue with using GeometryReader in list rows?",
                "correct_idx": 0,
                "options": [
                    {"text": "Expands greedily and causes multi-pass layouts.", "reason": "Option A is correct: GeometryReader takes all proposed space and triggers additional layout renegotiations, causing scrolling stutter."},
                    {"text": "Forces dynamic PWT table lookup.", "reason": "Option B is wrong because layout negotiation does not alter method dispatch."},
                    {"text": "Deletes local disk image cache files.", "reason": "Option C is wrong because view layout does not affect disk caches."},
                    {"text": "Locks cooperative thread pool threads.", "reason": "Option D is wrong because UI layout runs on the main thread."}
                ]
            }
        ],
        "exercise": {
            "title": "Horizontal Flow Layout Calculator Engine",
            "instructions": "Implement <code>calculateFlowWidths</code>. Given an array of item widths and horizontal spacing, calculate the total content width and verify it fits within bounds. Replace <code>TODO</code> and run tests.",
            "starter_code": """import Foundation

func calculateFlowWidths(itemWidths: [Double], spacing: Double) -> Double {
    // TODO: Calculate the total horizontal width:
    // Sum of all item widths + (spacing * (count - 1)) if count > 0, otherwise 0.0
    fatalError("TODO: Implement calculateFlowWidths")
}

func runTests() {
    print("🧪 Running Lesson 3.4 Verification Tests...")
    let widths = [50.0, 75.0, 100.0]
    let total = calculateFlowWidths(itemWidths: widths, spacing: 10.0)
    // 50 + 75 + 100 = 225; 2 spaces of 10 = 20 -> 245.0
    assert(total == 245.0, "Test 1 Failed: Expected 245.0, got \\(total)")

    let emptyTotal = calculateFlowWidths(itemWidths: [], spacing: 10.0)
    assert(emptyTotal == 0.0, "Test 2 Failed")
    print("  ✅ Challenge Passed: Layout coordinate calculations verified")
}
runTests()""",
            "hints": [
                "guard !itemWidths.isEmpty else { return 0.0 }",
                "let itemsSum = itemWidths.reduce(0, +)",
                "let totalSpacing = Double(itemWidths.count - 1) * spacing",
                "return itemsSum + totalSpacing"
            ],
            "solution_code": """import Foundation

func calculateFlowWidths(itemWidths: [Double], spacing: Double) -> Double {
    guard !itemWidths.isEmpty else { return 0.0 }
    let itemsSum = itemWidths.reduce(0, +)
    let totalSpacing = Double(itemWidths.count - 1) * spacing
    return itemsSum + totalSpacing
}

func runTests() {
    print("🧪 Running Lesson 3.4 Verification Tests...")
    let widths = [50.0, 75.0, 100.0]
    let total = calculateFlowWidths(itemWidths: widths, spacing: 10.0)
    assert(total == 245.0)
    let emptyTotal = calculateFlowWidths(itemWidths: [], spacing: 10.0)
    assert(emptyTotal == 0.0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Custom Layout implementations in SwiftUI calculate layout dimensions during sizeThatFits. Summing child sizes with inter-element spacing enables flexible wrapping and flow containers with zero GeometryReader overhead."
        },
        "primary_sources": [
            {"title": "Apple Developer: Layout Protocol", "url": "https://developer.apple.com/documentation/swiftui/layout", "desc": "Official Apple reference on custom container layouts."},
            {"title": "WWDC Session: Compose advanced layouts with SwiftUI", "url": "https://developer.apple.com/videos/play/wwdc2022/10056/", "desc": "Apple engineers introduce the Layout protocol."}
        ],
        "coaching_prompt": "Replace GeometryReader with the Layout protocol whenever building custom grids, flow layouts, or radial layouts!"
    },

    # 0023: Lesson 3.5
    {
        "id": "0023",
        "slug": "0023-type-safe-navigation",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.5",
        "title": "Type-Safe NavigationStack & Deep Linking",
        "subtitle": "Decoupled programmatic routing with NavigationPath, .navigationDestination(for:), and serializing deep links.",
        "prev_url": "0022-layout-mastery-and-custom-layouts.html",
        "next_url": "0024-design-systems-and-viewmodifiers.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android (Compose), navigation typically uses Jetpack Navigation with type-safe routes (Navigation 2.8+ using Kotlin serialization).</p>
        <p>In SwiftUI (iOS 16+), legacy <code>NavigationLink(destination:)</code> (which eagerly instantiated destination views before being tapped!) has been replaced by <strong><code>NavigationStack</code></strong> and <strong><code>NavigationPath</code></strong>. Views register lazy route handlers using <code>.navigationDestination(for: Route.self)</code>, enabling deep linking and programmatic stack pops to root.</p>
        """,
        "core_sections_html": """
        <h3>1. The Modern Programmatic NavigationStack</h3>
        <pre><code>enum AppRoute: Hashable, Codable {
    case userProfile(id: String)
    case transactionDetail(txId: String)
}

struct RootCoordinatorView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            HomeView(path: $path)
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .userProfile(let id):
                        UserProfileView(userId: id)
                    case .transactionDetail(let txId):
                        TransactionDetailView(txId: txId)
                    }
                }
        }
    }
}</code></pre>

        <h3>2. Programmatic Pop to Root</h3>
        <p>To pop all stacked screens and return to root, reset the path: <code>path = NavigationPath()</code> or <code>path.removeLast(path.count)</code>.</p>
        """,
        "failure_mode_html": """
        <p><strong>Eager Destination Instantiation in Legacy NavigationLink:</strong> Writing <code>NavigationLink("Profile", destination: HeavyView())</code> instantiates <code>HeavyView</code> immediately when the list displays, causing massive memory spikes. Always use <code>NavigationLink(value: route)</code> with <code>.navigationDestination(for:)</code>.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary performance benefit of .navigationDestination(for:) compared to legacy NavigationLink(destination:)?",
                "correct_idx": 1,
                "options": [
                    {"text": "Compiles views to Metal GPU pipelines.", "reason": "Option A is wrong because navigation is view hierarchy coordination."},
                    {"text": "Destination views are instantiated lazily on tap.", "reason": "Option B is correct: destination views are constructed only when the route value is pushed onto the navigation stack."},
                    {"text": "Eliminates all needs for App Store review.", "reason": "Option C is wrong because navigation APIs do not affect App Store policy."},
                    {"text": "Runs network requests in parallel.", "reason": "Option D is wrong because navigation does not execute networking."}
                ]
            },
            {
                "question": "How can you programmatically pop back to the root view using NavigationPath?",
                "correct_idx": 0,
                "options": [
                    {"text": "Reassign path to NavigationPath().", "reason": "Option A is correct: resetting path to an empty NavigationPath clears all pushed destinations and returns to root instantly."},
                    {"text": "Call exit(0) on Darwin operating system.", "reason": "Option B is wrong because exit(0) terminates the entire application."},
                    {"text": "Deallocate the UIWindow root controller.", "reason": "Option C is wrong because that destroys the entire window."},
                    {"text": "Force cast NavigationPath to Array.", "reason": "Option D is wrong because NavigationPath is a type-erased collection with its own API."}
                ]
            }
        ],
        "exercise": {
            "title": "Universal Link & Deep Link Route Parser",
            "instructions": "Implement <code>parseDeepLinkURL</code>. Given a URL like <code>pulseflow://user/usr_99</code> or <code>pulseflow://tx/tx_101</code>, parse and return the appropriate <code>AppRoute</code> enum. If unknown, return <code>nil</code>. Run tests.",
            "starter_code": """import Foundation

enum AppRoute: Hashable, Equatable {
    case userProfile(id: String)
    case transactionDetail(txId: String)
}

func parseDeepLinkURL(_ url: URL) -> AppRoute? {
    // Expected host: "user" -> pathComponents[1] is id
    // Expected host: "tx" -> pathComponents[1] is txId
    // TODO: Inspect url.host and url.pathComponents to return the matching AppRoute
    fatalError("TODO: Implement parseDeepLinkURL")
}

func runTests() {
    print("🧪 Running Lesson 3.5 Verification Tests...")
    let u1 = URL(string: "pulseflow://user/usr_99")!
    let r1 = parseDeepLinkURL(u1)
    assert(r1 == .userProfile(id: "usr_99"), "Test 1 Failed")

    let u2 = URL(string: "pulseflow://tx/tx_888")!
    let r2 = parseDeepLinkURL(u2)
    assert(r2 == .transactionDetail(txId: "tx_888"), "Test 2 Failed")

    let u3 = URL(string: "pulseflow://unknown/123")!
    assert(parseDeepLinkURL(u3) == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: Deep link URL parsing verified")
}
runTests()""",
            "hints": [
                "guard let host = url.host else { return nil }",
                "let parts = url.pathComponents.filter { $0 != \"/\" }",
                "guard let firstParam = parts.first else { return nil }",
                "if host == \"user\" { return .userProfile(id: firstParam) }"
            ],
            "solution_code": """import Foundation

enum AppRoute: Hashable, Equatable {
    case userProfile(id: String)
    case transactionDetail(txId: String)
}

func parseDeepLinkURL(_ url: URL) -> AppRoute? {
    guard let host = url.host else { return nil }
    let parts = url.pathComponents.filter { $0 != "/" }
    guard let firstParam = parts.first else { return nil }

    switch host {
    case "user":
        return .userProfile(id: firstParam)
    case "tx":
        return .transactionDetail(txId: firstParam)
    default:
        return nil
    }
}

func runTests() {
    print("🧪 Running Lesson 3.5 Verification Tests...")
    let u1 = URL(string: "pulseflow://user/usr_99")!
    let r1 = parseDeepLinkURL(u1)
    assert(r1 == .userProfile(id: "usr_99"))

    let u2 = URL(string: "pulseflow://tx/tx_888")!
    let r2 = parseDeepLinkURL(u2)
    assert(r2 == .transactionDetail(txId: "tx_888"))

    let u3 = URL(string: "pulseflow://unknown/123")!
    assert(parseDeepLinkURL(u3) == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Type-safe navigation maps deep links directly into Hashable AppRoute enums. Pushing the parsed enum onto NavigationPath activates lazy .navigationDestination resolution, routing users straight to target screens."
        },
        "primary_sources": [
            {"title": "Apple Developer: NavigationStack Documentation", "url": "https://developer.apple.com/documentation/swiftui/navigationstack", "desc": "Official Apple reference on path-based programmatic navigation."},
            {"title": "WWDC Session: The craft of SwiftUI API design: Progressive disclosure", "url": "https://developer.apple.com/videos/play/wwdc2022/10059/", "desc": "Apple engineers explain NavigationStack and NavigationPath design."}
        ],
        "coaching_prompt": "By separating URL parsing from UI navigation, you can push deep links directly onto NavigationPath from push notifications or Universal Links with zero coupling!"
    },

    # 0024: Lesson 3.6
    {
        "id": "0024",
        "slug": "0024-design-systems-and-viewmodifiers",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.6",
        "title": "Enterprise Design Systems & Custom ViewModifiers",
        "subtitle": "Design tokens, custom ViewModifier pipelines, ButtonStyles, typography scales, and dynamic dark mode palettes.",
        "prev_url": "0023-type-safe-navigation.html",
        "next_url": "0025-animations-and-microinteractions.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, design systems are built around <code>MaterialTheme</code> (Typography, ColorScheme, Shapes) and custom composable modifiers (<code>Modifier.then(...)</code>).</p>
        <p>In SwiftUI, design systems are built using <strong><code>ViewModifier</code></strong>, custom <strong><code>ButtonStyle</code></strong>, and extension methods. Reusable modifiers encapsulate styling, border radius, shadows, and accessible contrast into declarative tokens.</p>
        """,
        "core_sections_html": """
        <h3>1. The ViewModifier Anatomy</h3>
        <pre><code>struct CardStyleModifier: ViewModifier {
    let cornerRadius: CGFloat
    let borderColor: Color

    func body(content: Content) -> some View {
        content
            .padding(16)
            .background(Color(.secondarySystemBackground))
            .cornerRadius(cornerRadius)
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(borderColor, lineWidth: 1)
            )
    }
}

extension View {
    func cardStyle(radius: CGFloat = 12, border: Color = .gray.opacity(0.3)) -> some View {
        modifier(CardStyleModifier(cornerRadius: radius, borderColor: border))
    }
}</code></pre>

        <h3>2. Custom ButtonStyles</h3>
        <p>A custom <code>ButtonStyle</code> controls pressed states, scale effects, and haptic feedback uniformly across the entire application.</p>
        """,
        "failure_mode_html": """
        <p><strong>Hardcoding Colors and Spacing:</strong> Scattering raw hex colors (<code>Color(hex: 0x0284C7)</code>) and magic padding numbers across views makes dark mode and branding redesigns painful. Always route styles through semantic design tokens.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary architectural purpose of a custom ViewModifier in SwiftUI?",
                "correct_idx": 1,
                "options": [
                    {"text": "Persists app preferences into UserDefaults.", "reason": "Option A is wrong because data storage is handled by @AppStorage or UserDefaults."},
                    {"text": "Encapsulates reusable view styling pipelines.", "reason": "Option B is correct: ViewModifiers package reusable styling, padding, and layout transforms into single declarative methods."},
                    {"text": "Forces dynamic table dispatch on views.", "reason": "Option C is wrong because views use opaque types and static compile-time specialization."},
                    {"text": "Enforces Swift 6 actor thread safety.", "reason": "Option D is wrong because UI views execute on MainActor."}
                ]
            },
            {
                "question": "Where should custom ViewModifier convenience methods be declared?",
                "correct_idx": 0,
                "options": [
                    {"text": "As an extension on the View protocol.", "reason": "Option A is correct: extending View allows dot-syntax chaining (e.g. .cardStyle())."},
                    {"text": "Inside a Darwin kernel daemon process.", "reason": "Option B is wrong because View modifiers are UI code."},
                    {"text": "Within an Objective-C category header.", "reason": "Option C is wrong because SwiftUI is modern pure Swift."},
                    {"text": "Directly inside standard library types.", "reason": "Option D is wrong because app code does not mutate stdlib headers."}
                ]
            }
        ],
        "exercise": {
            "title": "Design Token Style Engine",
            "instructions": "Implement <code>DesignTokens.formatBadge</code>. It formats text according to design token rules: uppercased text prefixed by a category code. Run tests.",
            "starter_code": """import Foundation

struct DesignTokens {
    // TODO: Implement formatBadge(text: String, category: String) -> String
    // Return: "[CATEGORY_UPPER] TEXT_UPPER"
    // Example: category "info", text "active" -> "[INFO] ACTIVE"
    static func formatBadge(text: String, category: String) -> String {
        fatalError("TODO: Implement formatBadge")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.6 Verification Tests...")
    let badge = DesignTokens.formatBadge(text: "verified account", category: "security")
    assert(badge == "[SECURITY] VERIFIED ACCOUNT", "Test 1 Failed: Got \\(badge)")
    print("  ✅ Challenge Passed: Design token formatter verified")
}
runTests()""",
            "hints": [
                "Return \"[\\(category.uppercased())] \\(text.uppercased())\""
            ],
            "solution_code": """import Foundation

struct DesignTokens {
    static func formatBadge(text: String, category: String) -> String {
        return "[\\(category.uppercased())] \\(text.uppercased())"
    }
}

func runTests() {
    print("🧪 Running Lesson 3.6 Verification Tests...")
    let badge = DesignTokens.formatBadge(text: "verified account", category: "security")
    assert(badge == "[SECURITY] VERIFIED ACCOUNT")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Centralizing styling logic in design tokens and ViewModifiers ensures uniform branding and simplifies testing typography, color scales, and component borders."
        },
        "primary_sources": [
            {"title": "Apple Developer: ViewModifier Documentation", "url": "https://developer.apple.com/documentation/swiftui/viewmodifier", "desc": "Official Apple reference on building custom ViewModifiers."},
            {"title": "Apple Developer: ButtonStyle Documentation", "url": "https://developer.apple.com/documentation/swiftui/buttonstyle", "desc": "Designing custom button interaction styles."}
        ],
        "coaching_prompt": "Whenever you find yourself writing the same 4 modifiers on 3 different views, immediately refactor them into a custom ViewModifier!"
    },

    # 0025: Lesson 3.7
    {
        "id": "0025",
        "slug": "0025-animations-and-microinteractions",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.7",
        "title": "Animations & Micro-Interactions: Springs & Matched Geometry",
        "subtitle": "Physics-based springs, matchedGeometryEffect hero transitions, drag gestures, and phase-based animations.",
        "prev_url": "0024-design-systems-and-viewmodifiers.html",
        "next_url": "0026-uikit-core-architecture.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Jetpack Compose, transitions use <code>AnimatedVisibility</code>, <code>updateTransition</code>, and shared element transitions (<code>SharedTransitionLayout</code>).</p>
        <p>In SwiftUI, animations are interruptible and physics-driven by default using <strong>spring models</strong> (<code>.spring(response:dampingRatio:)</code>). Hero view transitions across hierarchies are handled seamlessly with <strong><code>matchedGeometryEffect(id:in:)</code></strong>, interpolating position and size smoothly.</p>
        """,
        "core_sections_html": """
        <h3>1. Fluid Physics Springs</h3>
        <pre><code>withAnimation(.spring(response: 0.35, dampingFraction: 0.75)) {
    isExpanded.toggle()
}</code></pre>

        <h3>2. matchedGeometryEffect (Hero Transitions)</h3>
        <p>Smoothly animate an element between different layout containers:</p>
        <pre><code>struct HeroView: View {
    @Namespace private var animationNamespace
    @State private var isDetail = false

    var body: some View {
        if isDetail {
            DetailCard()
                .matchedGeometryEffect(id: "card_hero", in: animationNamespace)
        } else {
            CompactCard()
                .matchedGeometryEffect(id: "card_hero", in: animationNamespace)
        }
    }
}</code></pre>
        """,
        "failure_mode_html": """
        <p><strong>Applying Linear Animations to Natural Gestures:</strong> Using <code>.linear</code> or fixed duration animations for user touches feels unnatural and sluggish. Apple HIG strongly advocates for spring physics, which dynamically inherit velocity from user touch gestures.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary advantage of matchedGeometryEffect in SwiftUI?",
                "correct_idx": 0,
                "options": [
                    {"text": "Smoothly interpolates frame geometry across views.", "reason": "Option A is correct: matchedGeometryEffect synchronizes position and dimensions between two views sharing the same namespace id."},
                    {"text": "Compresses high-resolution image files on disk.", "reason": "Option B is wrong because animations do not perform image compression."},
                    {"text": "Runs Core Data migrations in background.", "reason": "Option C is wrong because animation APIs are purely declarative UI."},
                    {"text": "Bypasses all view invalidation diffing passes.", "reason": "Option D is wrong because the AttributeGraph actively calculates transitions."}
                ]
            },
            {
                "question": "Why does SwiftUI prefer spring animations over duration-based easeIn/easeOut curves?",
                "correct_idx": 1,
                "options": [
                    {"text": "Springs require zero CPU memory allocations.", "reason": "Option A is wrong because spring calculations use standard math routines."},
                    {"text": "Springs are interruptible and preserve velocity.", "reason": "Option B is correct: spring physics smoothly blend momentum when user touches redirect ongoing animations mid-flight."},
                    {"text": "Springs disable all accessibility settings.", "reason": "Option C is wrong because accessibility settings (Reduce Motion) override animations."},
                    {"text": "Springs enforce static method dispatch rules.", "reason": "Option D is wrong because animations are runtime render passes."}
                ]
            }
        ],
        "exercise": {
            "title": "Spring Physics Coordinate Interpolator",
            "instructions": "Implement <code>interpolatePosition</code>. Calculate the interpolated coordinate between <code>start</code> and <code>end</code> given a progress fraction (0.0 to 1.0). Clamp fraction between 0 and 1. Run tests.",
            "starter_code": """import Foundation

func interpolatePosition(start: Double, end: Double, progress: Double) -> Double {
    // TODO: 1. Clamp progress between 0.0 and 1.0
    // TODO: 2. Return start + (end - start) * clampedProgress
    fatalError("TODO: Implement interpolatePosition")
}

func runTests() {
    print("🧪 Running Lesson 3.7 Verification Tests...")
    let mid = interpolatePosition(start: 100.0, end: 200.0, progress: 0.5)
    assert(mid == 150.0, "Test 1 Failed: Expected 150.0, got \\(mid)")

    let clampedHigh = interpolatePosition(start: 0.0, end: 100.0, progress: 1.5)
    assert(clampedHigh == 100.0, "Test 2 Failed: Did not clamp upper bound")
    print("  ✅ Challenge Passed: Coordinate interpolation verified")
}
runTests()""",
            "hints": [
                "let clamped = min(max(progress, 0.0), 1.0)",
                "return start + (end - start) * clamped"
            ],
            "solution_code": """import Foundation

func interpolatePosition(start: Double, end: Double, progress: Double) -> Double {
    let clamped = min(max(progress, 0.0), 1.0)
    return start + (end - start) * clamped
}

func runTests() {
    print("🧪 Running Lesson 3.7 Verification Tests...")
    let mid = interpolatePosition(start: 100.0, end: 200.0, progress: 0.5)
    assert(mid == 150.0)
    let clampedHigh = interpolatePosition(start: 0.0, end: 100.0, progress: 1.5)
    assert(clampedHigh == 100.0)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "Smooth micro-interactions rely on linear and spring interpolation. Clamping normalized progress between 0 and 1 ensures geometry bounds never exceed physical screen parameters during hero transitions."
        },
        "primary_sources": [
            {"title": "Apple Developer: matchedGeometryEffect", "url": "https://developer.apple.com/documentation/swiftui/view/matchedgeometryeffect(id:in:properties:anchor:issource:)", "desc": "Official Apple reference on matched geometry transitions."},
            {"title": "WWDC Session: Explore SwiftUI animation", "url": "https://developer.apple.com/videos/play/wwdc2023/10156/", "desc": "Apple engineers dive into modern spring animation physics."}
        ],
        "coaching_prompt": "Spring physics make animations interruptible: if the user taps back halfway through a transition, the spring smoothly redirects without jarring jumps."
    },

    # 0026: Lesson 3.8
    {
        "id": "0026",
        "slug": "0026-uikit-core-architecture",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.8",
        "title": "UIKit Core Architecture: Lifecycles, Responders & Auto Layout",
        "subtitle": "UIViewController lifecycle, Responder Chain and hit-testing (hitTest:withEvent:), Auto Layout engine, and UICollectionView DiffableDataSource.",
        "prev_url": "0025-animations-and-microinteractions.html",
        "next_url": "0027-uikit-interoperability.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, legacy UI revolves around <code>Activity</code>, <code>Fragment</code>, <code>View</code>, and <code>RecyclerView</code> with <code>ListAdapter</code> / <code>DiffUtil</code>.</p>
        <p>In iOS, <strong>UIKit</strong> has powered production apps for over 15 years. Over 80% of Senior iOS interview screens test UIKit fundamentals: <strong><code>UIViewController</code> lifecycle</strong> (<code>viewDidLoad</code>, <code>viewWillAppear</code>, <code>viewDidLayoutSubviews</code>), the <strong>Responder Chain</strong> for touch handling, and <strong><code>UICollectionViewDiffableDataSource</code></strong> (equivalent to Android DiffUtil).</p>
        """,
        "core_sections_html": """
        <h3>1. The UIViewController Lifecycle Order</h3>
        <ol>
          <li><code>loadView()</code>: Instantiates the root <code>UIView</code> hierarchy. (Never call <code>super</code> if creating view programmatically).</li>
          <li><code>viewDidLoad()</code>: Called once when view hierarchy loads into RAM. Ideal for setup and DI.</li>
          <li><code>viewWillAppear()</code>: Called right before view animates onto screen. Refresh dynamic data.</li>
          <li><code>viewDidLayoutSubviews()</code>: Auto Layout has solved constraint equations. Frame bounds are now final and accurate!</li>
          <li><code>viewDidAppear()</code>: Screen transition complete; interactive to touch.</li>
        </ol>

        <h3>2. The Responder Chain & Hit-Testing</h3>
        <p>When a finger touches the screen, UIKit traverses the view hierarchy backwards using <code>hitTest(_:withEvent:)</code> to find the frontmost view under the touch. That view becomes the <strong>First Responder</strong>. Unhandled events bubble up the <code>nextResponder</code> chain to parent views, view controller, window, and UIApplication.</p>
        """,
        "failure_mode_html": """
        <p><strong>Inspecting Bounds in viewDidLoad():</strong> In <code>viewDidLoad()</code>, views have loaded into memory, but Auto Layout has NOT solved constraint equations yet. <code>view.bounds</code> is often inaccurate or zero! Always inspect final layout geometry in <code>viewDidLayoutSubviews()</code>.</p>
        """,
        "quizzes": [
            {
                "question": "At which UIViewController lifecycle method are Auto Layout view frame geometries final and accurate?",
                "correct_idx": 2,
                "options": [
                    {"text": "Inside the init(coder:) initializer.", "reason": "Option A is wrong because views have not even loaded into memory."},
                    {"text": "Inside the viewDidLoad() method.", "reason": "Option B is wrong: viewDidLoad runs before constraint calculation; frames are not final."},
                    {"text": "Inside viewDidLayoutSubviews() method.", "reason": "Option C is correct: viewDidLayoutSubviews fires after the Auto Layout engine has calculated final frames."},
                    {"text": "Inside the deinit deallocator call.", "reason": "Option D is wrong because view is deallocating."}
                ]
            },
            {
                "question": "What method in UIView is responsible for determining which view receives a touch event?",
                "correct_idx": 0,
                "options": [
                    {"text": "func hitTest(_:withEvent:) -> UIView?", "reason": "Option A is correct: hitTest recursively probes subviews to identify the deepest view containing the touch coordinates."},
                    {"text": "func updateConstraintsIfNeeded()", "reason": "Option B is wrong because that triggers Auto Layout calculation."},
                    {"text": "func layoutSubviews() -> Void", "reason": "Option C is wrong because layoutSubviews positions child frames."},
                    {"text": "func draw(_ rect: CGRect) -> Void", "reason": "Option D is wrong because draw is for CoreGraphics vector rendering."}
                ]
            }
        ],
        "exercise": {
            "title": "Responder Chain Touch Hit-Test Simulator",
            "instructions": "Implement <code>hitTest</code> for <code>SimulatedView</code>. If the point is inside the view's bounds and view is not hidden/disabled, iterate subviews in reverse order to find the deepest child. Otherwise return self. Run tests.",
            "starter_code": """import Foundation
import CoreGraphics

final class SimulatedView {
    let name: String
    var isHidden: Bool = false
    var isUserInteractionEnabled: Bool = true
    var frame: CGRect
    var subviews: [SimulatedView] = []

    init(name: String, frame: CGRect) {
        self.name = name
        self.frame = frame
    }

    func hitTest(point: CGPoint) -> SimulatedView? {
        guard !isHidden, isUserInteractionEnabled, frame.contains(point) else {
            return nil
        }
        // TODO: Iterate subviews in REVERSE order (subviews.reversed())
        // If a child hitTest returns non-nil, return that child view!
        // If no child catches the touch, return self
        fatalError("TODO: Implement hitTest")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.8 Verification Tests...")
    let root = SimulatedView(name: "RootView", frame: CGRect(x: 0, y: 0, width: 400, height: 800))
    let button = SimulatedView(name: "SubmitButton", frame: CGRect(x: 50, y: 100, width: 200, height: 50))
    root.subviews.append(button)

    // Touch directly inside button:
    let target1 = root.hitTest(point: CGPoint(x: 100, y: 120))
    assert(target1?.name == "SubmitButton", "Test 1 Failed: Expected SubmitButton, got \\(target1?.name ?? \"nil\")")

    // Touch outside button but inside root:
    let target2 = root.hitTest(point: CGPoint(x: 350, y: 700))
    assert(target2?.name == "RootView", "Test 2 Failed")

    // Touch completely outside root:
    let target3 = root.hitTest(point: CGPoint(x: 500, y: 900))
    assert(target3 == nil, "Test 3 Failed")
    print("  ✅ Challenge Passed: Responder chain hit-testing verified")
}
runTests()""",
            "hints": [
                "for child in subviews.reversed() { if let hit = child.hitTest(point: point) { return hit } }",
                "return self"
            ],
            "solution_code": """import Foundation
import CoreGraphics

final class SimulatedView {
    let name: String
    var isHidden: Bool = false
    var isUserInteractionEnabled: Bool = true
    var frame: CGRect
    var subviews: [SimulatedView] = []

    init(name: String, frame: CGRect) {
        self.name = name
        self.frame = frame
    }

    func hitTest(point: CGPoint) -> SimulatedView? {
        guard !isHidden, isUserInteractionEnabled, frame.contains(point) else {
            return nil
        }
        for child in subviews.reversed() {
            if let hit = child.hitTest(point: point) {
                return hit
            }
        }
        return self
    }
}

func runTests() {
    print("🧪 Running Lesson 3.8 Verification Tests...")
    let root = SimulatedView(name: "RootView", frame: CGRect(x: 0, y: 0, width: 400, height: 800))
    let button = SimulatedView(name: "SubmitButton", frame: CGRect(x: 50, y: 100, width: 200, height: 50))
    root.subviews.append(button)

    let target1 = root.hitTest(point: CGPoint(x: 100, y: 120))
    assert(target1?.name == "SubmitButton")

    let target2 = root.hitTest(point: CGPoint(x: 350, y: 700))
    assert(target2?.name == "RootView")

    let target3 = root.hitTest(point: CGPoint(x: 500, y: 900))
    assert(target3 == nil)
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "UIKit hit-testing traverses subviews in reverse order (front-to-back z-order). The deepest frontmost subview containing the touch coordinate is returned as the touch target, forming the root of the Responder Chain."
        },
        "primary_sources": [
            {"title": "Apple Developer: UIViewController Lifecycle", "url": "https://developer.apple.com/documentation/uikit/uiviewcontroller", "desc": "Official documentation on view controller presentation and lifecycle."},
            {"title": "Apple Developer: Understanding Event Handling and Responders", "url": "https://developer.apple.com/documentation/uikit/touches_presses_and_gestures/using_responders_and_the_responder_chain_to_handle_events", "desc": "Official guide on the Responder Chain."}
        ],
        "coaching_prompt": "Senior interviewers frequently ask: 'Where do you configure constraints vs where do you read final frames?' Answer: Configure in viewDidLoad / loadView, read final geometry in viewDidLayoutSubviews!"
    },

    # 0027: Lesson 3.9
    {
        "id": "0027",
        "slug": "0027-uikit-interoperability",
        "phase_num": 3,
        "phase_title": "Phase 3: High-Performance Declarative UI",
        "lesson_num": "Lesson 3.9",
        "title": "UIKit Interoperability: Representables & The Coordinator Pattern",
        "subtitle": "UIViewRepresentable, UIViewControllerRepresentable, UIHostingController, and two-way delegate bridges via Coordinators.",
        "prev_url": "0026-uikit-core-architecture.html",
        "next_url": "../lessons/0028-lightweight-storage-and-keychain.html",
        "cheatsheet_url": "../reference/swiftui-architecture-cheatsheet.html",
        "cheatsheet_title": "SwiftUI Architecture Cheatsheet",
        "android_anchor_html": """
        <p>In Android, integrating legacy Android Views (like MapView or CameraX PreviewView) into Jetpack Compose uses <code>AndroidView { context -> ... }</code>.</p>
        <p>In iOS, commercial enterprise apps contain millions of lines of UIKit. Senior engineers seamlessly bridge both frameworks using <strong><code>UIViewRepresentable</code></strong> and <strong><code>UIViewControllerRepresentable</code></strong>. The <strong>Coordinator pattern</strong> serves as the delegate bridge between UIKit target-actions and SwiftUI <code>@Binding</code> states.</p>
        """,
        "core_sections_html": """
        <h3>1. The UIViewRepresentable Lifecycle</h3>
        <pre><code>struct SearchBarRepresentable: UIViewRepresentable {
    @Binding var text: String

    // 1. Create the UIKit instance:
    func makeUIView(context: Context) -> UISearchBar {
        let searchBar = UISearchBar()
        searchBar.delegate = context.coordinator
        return searchBar
    }

    // 2. Push SwiftUI state down to UIKit:
    func updateUIView(_ uiView: UISearchBar, context: Context) {
        uiView.text = text
    }

    // 3. Coordinator bridges UIKit delegate events back up to @Binding:
    func makeCoordinator() -> Coordinator {
        Coordinator(text: $text)
    }

    final class Coordinator: NSObject, UISearchBarDelegate {
        @Binding var text: String
        init(text: Binding&lt;String&gt;) { self._text = text }

        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            self.text = searchText // Mutates SwiftUI source of truth!
        }
    }
}</code></pre>

        <h3>2. Hosting SwiftUI in UIKit (UIHostingController)</h3>
        <p>To embed SwiftUI into existing UIKit codebases, wrap views in <code>UIHostingController(rootView: MySwiftUIView())</code>.</p>
        """,
        "failure_mode_html": """
        <p><strong>Triggering State Updates inside updateUIView():</strong> Modifying SwiftUI <code>@Binding</code> or <code>@State</code> inside <code>updateUIView</code> creates an infinite render cycle (updateUIView -> state change -> view re-render -> updateUIView). State mutations must originate exclusively from user actions in the Coordinator delegate.</p>
        """,
        "quizzes": [
            {
                "question": "What is the primary architectural purpose of the Coordinator class in UIViewRepresentable?",
                "correct_idx": 1,
                "options": [
                    {"text": "Renders CoreGraphics Metal shaders directly.", "reason": "Option A is wrong because Coordinators are delegates, not graphics pipelines."},
                    {"text": "Acts as the UIKit delegate bridging events to SwiftUI.", "reason": "Option B is correct: Coordinators implement delegate protocols and update SwiftUI @Binding properties."},
                    {"text": "Manages local SQLite database operations.", "reason": "Option C is wrong because database storage is handled by SwiftData or Core Data."},
                    {"text": "Enforces thread safety in actor methods.", "reason": "Option D is wrong because UI delegation executes on MainActor."}
                ]
            },
            {
                "question": "How can you embed a modern SwiftUI view into an existing legacy UIKit UIViewController?",
                "correct_idx": 0,
                "options": [
                    {"text": "Wrap the view in a UIHostingController.", "reason": "Option A is correct: UIHostingController inherits from UIViewController and hosts SwiftUI views seamlessly in UIKit hierarchies."},
                    {"text": "Cast the view to a standard UIView.", "reason": "Option B is wrong because SwiftUI View is a struct protocol, not a UIView subclass."},
                    {"text": "Invoke dispatch_sync on the main queue.", "reason": "Option C is wrong because queue dispatching does not create view controllers."},
                    {"text": "Rebuild the application from terminal.", "reason": "Option D is wrong because UIKit and SwiftUI interoperate natively at runtime."}
                ]
            }
        ],
        "exercise": {
            "title": "Coordinator Delegate Bridge Simulator",
            "instructions": "Implement <code>textDidChange</code> in <code>MockCoordinator</code>. When the UIKit search bar fires text changes, update the upstream <code>textBinding</code>. Run tests.",
            "starter_code": """import Foundation

final class MockCoordinator {
    var textBinding: String

    init(initialText: String) {
        self.textBinding = initialText
    }

    // TODO: Implement textDidChange(newText: String) -> mutate textBinding = newText
    func textDidChange(newText: String) {
        fatalError("TODO: Implement textDidChange")
    }
}

func runTests() {
    print("🧪 Running Lesson 3.9 Verification Tests...")
    let coordinator = MockCoordinator(initialText: "initial")
    coordinator.textDidChange(newText: "searching swift 6")

    assert(coordinator.textBinding == "searching swift 6", "Test Failed: Binding was not updated")
    print("  ✅ Challenge Passed: Coordinator delegate bridge verified")
}
runTests()""",
            "hints": [
                "In textDidChange, assign: self.textBinding = newText"
            ],
            "solution_code": """import Foundation

final class MockCoordinator {
    var textBinding: String

    init(initialText: String) {
        self.textBinding = initialText
    }

    func textDidChange(newText: String) {
        self.textBinding = newText
    }
}

func runTests() {
    print("🧪 Running Lesson 3.9 Verification Tests...")
    let coordinator = MockCoordinator(initialText: "initial")
    coordinator.textDidChange(newText: "searching swift 6")
    assert(coordinator.textBinding == "searching swift 6")
    print("  ✅ All tests passed!")
}
runTests()""",
            "solution_explanation": "The Coordinator pattern bridges object-oriented UIKit delegates to declarative SwiftUI state. When UIKit controls trigger delegate callbacks, the Coordinator mutates upstream bindings, keeping both runtimes synchronized."
        },
        "primary_sources": [
            {"title": "Apple Developer: Interactivity with UIKit", "url": "https://developer.apple.com/tutorials/swiftui/interfacing-with-uikit", "desc": "Canonical Apple tutorial on UIViewRepresentable and the Coordinator pattern."},
            {"title": "Apple Developer: UIHostingController", "url": "https://developer.apple.com/documentation/swiftui/uihostingcontroller", "desc": "Official guide on embedding SwiftUI in UIKit apps."}
        ],
        "coaching_prompt": "Congratulations! You have completed Phase 3: High-Performance Declarative UI, covering the SwiftUI render tree, observation macros, layout negotiation, deep linking, animations, and UIKit interoperability. Next up: Phase 4 Local Persistence & Offline-First!"
    }
]

print("Phase 3 complete data loaded.")
