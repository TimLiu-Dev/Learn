import Foundation

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
    assert(items.map(\.title) == ["Home", "Admin", "Logs", "Traces"], "Test 1 Failed: DSL output mismatch")
    print("  ✅ Challenge Passed: Custom @resultBuilder DSL verified")
}
runTests()