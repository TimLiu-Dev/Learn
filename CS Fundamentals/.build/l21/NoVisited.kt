val graph = mapOf(
    "A" to listOf("B", "C"), "B" to listOf("A", "D"), "C" to listOf("A", "D", "E"),
    "D" to listOf("B", "C", "F"), "E" to listOf("C", "F", "H"), "F" to listOf("D", "E", "G"),
    "G" to listOf("F"), "H" to listOf("E"),
)

// BFS with the visited set deleted. Capped at 20 steps so it can't run forever.
fun main() {
    val queue = ArrayDeque(listOf("A"))
    val seen = mutableMapOf<String, Int>()
    repeat(20) {
        val v = queue.removeFirst()
        seen[v] = (seen[v] ?: 0) + 1
        queue.addAll(graph.getValue(v))
    }
    println("after 20 steps: queue size ${queue.size}, visit counts $seen")
}
