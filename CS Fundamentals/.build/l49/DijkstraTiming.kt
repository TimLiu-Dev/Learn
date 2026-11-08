import java.util.PriorityQueue
import java.util.Random

// A random sparse digraph with V vertices and 4V edges, weights 1..100.
// Compressed adjacency: v's edges are to[first[v] until first[v + 1]].
class Graph(val v: Int, seed: Long) {
    val first = IntArray(v + 1)
    val to: IntArray
    val w: IntArray
    init {
        val e = 4 * v
        val rnd = Random(seed)
        val from = IntArray(e) { rnd.nextInt(v) }
        val dst = IntArray(e) { rnd.nextInt(v) }
        val wt = IntArray(e) { 1 + rnd.nextInt(100) }
        to = IntArray(e); w = IntArray(e)
        for (f in from) first[f + 1]++
        for (i in 0 until v) first[i + 1] += first[i]
        val next = first.copyOf()
        for (i in 0 until e) { val k = next[from[i]]++; to[k] = dst[i]; w[k] = wt[i] }
    }
}

// Lazy Dijkstra. Each queue entry packs (dist, vertex) into one Long: dist in the high 32 bits.
fun dijkstra(g: Graph, s: Int): IntArray {
    val dist = IntArray(g.v) { Int.MAX_VALUE }
    val settled = BooleanArray(g.v)
    val pq = PriorityQueue<Long>()
    dist[s] = 0
    pq.add(s.toLong())
    while (pq.isNotEmpty()) {
        val top = pq.poll()
        val v = (top and 0xFFFFFFFFL).toInt()
        if (settled[v]) continue
        settled[v] = true
        for (k in g.first[v] until g.first[v + 1]) {
            val nd = dist[v] + g.w[k]
            val x = g.to[k]
            if (!settled[x] && nd < dist[x]) { dist[x] = nd; pq.add((nd.toLong() shl 32) or x.toLong()) }
        }
    }
    return dist
}

fun main() {
    var sink = 0L
    var prev = 0.0
    for (v in listOf(125_000, 250_000, 500_000, 1_000_000)) {
        val g = Graph(v, 49)
        var best = Double.MAX_VALUE
        repeat(4) { run ->                                 // run 0 is the warm-up
            val t0 = System.nanoTime()
            sink += dijkstra(g, 0).sumOf { if (it == Int.MAX_VALUE) 0L else it.toLong() }
            val ms = (System.nanoTime() - t0) / 1e6
            if (run > 0) best = minOf(best, ms)
        }
        val ratio = if (prev > 0) "  (x%.2f)".format(best / prev) else ""
        println("V=%,9d  E=%,9d  %7.1f ms%s".format(v, 4 * v, best, ratio))
        prev = best
    }
    println("sink $sink")
}
