import Foundation

final class DependencyGraph {
    private var adjList: [String: [String]] = [:]

    func addEdge(from: String, to: String) {
        adjList[from, default: []].append(to)
    }

    // TODO: Implement hasCycle() -> Bool
    // Return true if any circular dependency exists in the graph
    func hasCycle() -> Bool {
        var visited: Set<String> = []
        var recStack: Set<String> = []

        func dfs(_ node: String) -> Bool {
            visited.insert(node)
            recStack.insert(node)
            for neighbor in adjList[node, default: []] {
                if !visited.contains(neighbor) {
                    if dfs(neighbor) { return true }
                } else if recStack.contains(neighbor) {
                    return true // Cycle detected!
                }
            }
            recStack.remove(node)
            return false
        }

        for node in adjList.keys {
            if !visited.contains(node) {
                if dfs(node) { return true }
            }
        }
        return false
    }
}

func runTests() {
    print("🧪 Running Lesson 5.3 Verification Tests...")
    let acyclicGraph = DependencyGraph()
    acyclicGraph.addEdge(from: "App", to: "Features")
    acyclicGraph.addEdge(from: "Features", to: "CoreNetwork")
    acyclicGraph.addEdge(from: "CoreNetwork", to: "Domain")
    assert(acyclicGraph.hasCycle() == false, "Test 1 Failed: Acyclic graph flagged as cycle")

    let cyclicGraph = DependencyGraph()
    cyclicGraph.addEdge(from: "ModuleA", to: "ModuleB")
    cyclicGraph.addEdge(from: "ModuleB", to: "ModuleA")
    assert(cyclicGraph.hasCycle() == true, "Test 2 Failed: Circular dependency not detected")
    print("  ✅ Challenge Passed: Dependency graph cycle validation verified")
}
runTests()