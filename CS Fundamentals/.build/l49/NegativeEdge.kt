import java.util.PriorityQueue

data class Edge(val from: String, val to: String, val weight: Int)

// One-way edges. C -> B is negative: say, a road with a toll refund.
val edges = listOf(Edge("A", "B", 2), Edge("A", "C", 5), Edge("C", "B", -4))
val vertices = listOf("A", "B", "C")

// The same lazy Dijkstra as Dijkstra.kt, without the printing.
fun dijkstra(start: String): Map<String, Int> {
    val dist = mutableMapOf(start to 0)
    val settled = mutableSetOf<String>()
    val pq = PriorityQueue(compareBy<Pair<Int, String>>({ it.first }, { it.second }))
    pq.add(0 to start)
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (!settled.add(v)) continue
        for (e in edges) {
            if (e.from != v || e.to in settled) continue
            if (d + e.weight < (dist[e.to] ?: Int.MAX_VALUE)) {
                dist[e.to] = d + e.weight
                pq.add(d + e.weight to e.to)
            }
        }
    }
    return dist
}

// Bellman-Ford: relax every edge, V-1 times. Slow, O(V·E), but negative edges are fine.
fun bellmanFord(start: String): Map<String, Int> {
    val dist = mutableMapOf(start to 0)
    repeat(vertices.size - 1) {              // a shortest path uses at most V-1 edges
        for ((from, to, weight) in edges) {
            val d = dist[from] ?: continue   // not reached yet
            if (d + weight < (dist[to] ?: Int.MAX_VALUE)) dist[to] = d + weight
        }
    }
    return dist
}

fun main() {
    println("dijkstra:     ${dijkstra("A").toSortedMap()}")
    println("bellman-ford: ${bellmanFord("A").toSortedMap()}")
}
