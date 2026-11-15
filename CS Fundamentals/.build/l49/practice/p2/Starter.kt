import java.util.PriorityQueue

data class Edge(val from: Int, val to: Int, val weight: Int)   // one-way, weight >= 0

// Shortest distance from source to each vertex 0 until n; null where there's no route.
fun shortest(n: Int, edges: List<Edge>, source: Int): List<Int?> {
    val out = List(n) { v -> edges.filter { it.from == v } }    // out[v] = the edges leaving v
    TODO("lazy Dijkstra: a queue of (distance, vertex); settle on poll; skip stale entries")
}
