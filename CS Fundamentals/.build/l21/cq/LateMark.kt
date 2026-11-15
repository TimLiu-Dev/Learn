val graph = mapOf(
    "A" to listOf("B", "C"),
    "B" to listOf("D"),
    "C" to listOf("D"),
    "D" to listOf(),
)

fun main() {
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque(listOf("A"))
    val order = mutableListOf<String>()
    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        visited += v                      // marked when dequeued, not when enqueued
        order += v
        for (n in graph.getValue(v)) if (n !in visited) queue.addLast(n)
    }
    println(order)
}
