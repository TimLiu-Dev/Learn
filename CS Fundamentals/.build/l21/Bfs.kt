val graph = mapOf(
    "A" to listOf("B", "C"), "B" to listOf("A", "D"), "C" to listOf("A", "D", "E"),
    "D" to listOf("B", "C", "F"), "E" to listOf("C", "F", "H"), "F" to listOf("D", "E", "G"),
    "G" to listOf("F"), "H" to listOf("E"), "Z" to listOf(),
)

// Returns each reachable vertex's parent on a shortest path from start.
fun bfs(start: String): Map<String, String?> {
    val visited = mutableSetOf(start)
    val parent = mutableMapOf<String, String?>(start to null)
    val queue = ArrayDeque(listOf(start))
    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        for (n in graph.getValue(v)) {
            if (visited.add(n)) {          // add returns false if n was already there
                parent[n] = v
                queue.addLast(n)
            }
        }
        println("visit $v  queue=$queue")
    }
    return parent
}

fun pathTo(parent: Map<String, String?>, target: String): List<String> =
    if (target !in parent) emptyList()
    else generateSequence(target) { parent[it] }.toList().reversed()

fun main() {
    val parent = bfs("A")
    for (target in listOf("G", "H", "Z")) {
        val path = pathTo(parent, target)
        println("A -> $target: $path  " + if (path.isEmpty()) "(unreachable)" else "(${path.size - 1} edges)")
    }
}
