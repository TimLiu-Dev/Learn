val graph = mapOf(
    "A" to listOf("B", "C"), "B" to listOf("A", "D"), "C" to listOf("A", "D", "E"),
    "D" to listOf("B", "C", "F"), "E" to listOf("C", "F", "H"), "F" to listOf("D", "E", "G"),
    "G" to listOf("F"), "H" to listOf("E"),
)

// Recursive: the call stack (L4) remembers where to come back to.
fun dfs(v: String, visited: MutableSet<String>, order: MutableList<String>) {
    visited += v
    order += v
    for (n in graph.getValue(v)) {
        if (n !in visited) dfs(n, visited, order)
    }
}

// Iterative: an explicit stack replaces the call stack.
fun dfsIterative(start: String, reversePush: Boolean): List<String> {
    val visited = mutableSetOf<String>()
    val order = mutableListOf<String>()
    val stack = ArrayDeque(listOf(start))
    while (stack.isNotEmpty()) {
        val v = stack.removeLast()
        if (!visited.add(v)) continue      // pushed twice; already done
        order += v
        val ns = graph.getValue(v)
        for (n in if (reversePush) ns.reversed() else ns) {
            if (n !in visited) stack.addLast(n)
        }
    }
    return order
}

fun main() {
    val order = mutableListOf<String>()
    dfs("A", mutableSetOf(), order)
    println("recursive          $order")
    println("stack              ${dfsIterative("A", reversePush = false)}")
    println("stack, reversed    ${dfsIterative("A", reversePush = true)}")
}
