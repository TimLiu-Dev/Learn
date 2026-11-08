import java.io.File
import java.util.PriorityQueue

data class Arc(val to: Int, val weight: Double)

// algs4 format: V, then E, then one "from to weight" line per directed edge.
fun load(path: String): List<List<Arc>> {
    val lines = File(path).readLines().filter { it.isNotBlank() }
    val adj = List(lines[0].trim().toInt()) { mutableListOf<Arc>() }
    for (line in lines.drop(2)) {
        val (v, w, weight) = line.trim().split(Regex("\\s+"))
        adj[v.toInt()] += Arc(w.toInt(), weight.toDouble())
    }
    return adj
}

// Returns (distance to target, how many vertices were settled before stopping).
fun search(adj: List<List<Arc>>, s: Int, target: Int?): Pair<Double, Int> {
    val dist = DoubleArray(adj.size) { Double.POSITIVE_INFINITY }
    val settled = BooleanArray(adj.size)
    var count = 0
    val pq = PriorityQueue(compareBy<Pair<Double, Int>> { it.first })
    dist[s] = 0.0
    pq.add(0.0 to s)
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (settled[v]) continue
        settled[v] = true
        count++
        if (v == target) return d to count          // its distance is final: stop here
        for ((w, weight) in adj[v]) {
            if (!settled[w] && d + weight < dist[w]) { dist[w] = d + weight; pq.add(dist[w] to w) }
        }
    }
    return (if (target == null) 0.0 else dist[target]) to count
}

fun main() {
    val adj = load("mediumEWD.txt")
    println("full run from 0 settles ${search(adj, 0, null).second} of ${adj.size} vertices")
    for (t in listOf(44, 4, 1, 2)) {
        val (d, n) = search(adj, 0, t)
        println("0 -> %-3d %.2f  after settling %3d vertices".format(t, d, n))
    }
}
