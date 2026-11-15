import java.util.PriorityQueue

data class Edge(val from: Int, val to: Int, val weight: Int)   // one-way, weight >= 0

// Shortest distance from source to each vertex 0 until n; null where there's no route.
fun shortest(n: Int, edges: List<Edge>, source: Int): List<Int?> {
    val out = List(n) { v -> edges.filter { it.from == v } }    // out[v] = the edges leaving v
    val dist = arrayOfNulls<Int>(n)                              // null = no route found (yet)
    val settled = BooleanArray(n)
    val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })   // (distance so far, vertex)
    dist[source] = 0
    pq.add(0 to source)
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (settled[v]) continue              // stale: a shorter entry already settled v
        settled[v] = true                     // the smallest in the queue, so d is final
        for (e in out[v]) {
            val nd = d + e.weight
            val old = dist[e.to]
            if (old == null || nd < old) {    // relax v -> e.to
                dist[e.to] = nd
                pq.add(nd to e.to)
            }
        }
    }
    return dist.toList()
}
