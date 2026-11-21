import Foundation

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
runTests()