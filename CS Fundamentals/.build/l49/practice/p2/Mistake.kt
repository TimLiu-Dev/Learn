import java.util.PriorityQueue

data class Edge(val from: Int, val to: Int, val weight: Int)   // one-way, weight >= 0

// Treats the FIRST distance found for a vertex as final, like BFS does.
fun shortest(n: Int, edges: List<Edge>, source: Int): List<Int?> {
    val out = List(n) { v -> edges.filter { it.from == v } }
    val dist = arrayOfNulls<Int>(n)
    val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
    dist[source] = 0
    pq.add(0 to source)
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        for (e in out[v]) {
            if (dist[e.to] == null) {          // "seen" = done: never improved later
                dist[e.to] = d + e.weight
                pq.add(d + e.weight to e.to)
            }
        }
    }
    return dist.toList()
}
