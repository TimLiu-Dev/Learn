import java.util.PriorityQueue

data class Road(val to: String, val minutes: Int)
data class Entry(val dist: Int, val town: String)

val roads = listOf(
    Triple("A", "B", 4), Triple("A", "C", 2), Triple("B", "C", 1), Triple("B", "D", 5),
    Triple("C", "D", 8), Triple("C", "E", 10), Triple("D", "E", 2), Triple("D", "F", 6),
    Triple("E", "F", 2), Triple("F", "G", 3), Triple("E", "G", 4),
)

// Two-way roads: each one goes into both towns' adjacency lists.
val graph: Map<String, List<Road>> = buildMap<String, MutableList<Road>> {
    for ((a, b, m) in roads) {
        getOrPut(a) { mutableListOf() } += Road(b, m)
        getOrPut(b) { mutableListOf() } += Road(a, m)
    }
}

fun dijkstra(start: String): Pair<Map<String, Int>, Map<String, String?>> {
    val dist = mutableMapOf(start to 0)
    val parent = mutableMapOf<String, String?>(start to null)
    val settled = mutableSetOf<String>()
    // Smallest distance first; ties broken by name so every run is identical.
    val pq = PriorityQueue(compareBy<Entry>({ it.dist }, { it.town }))
    pq.add(Entry(0, start))
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (!settled.add(v)) {                       // an old, longer entry: skip it
            println("poll ($d, $v)  stale, $v is already settled")
            continue
        }
        println("poll ($d, $v)  settle $v")
        for ((w, minutes) in graph.getValue(v)) {
            if (w in settled) continue
            val nd = d + minutes
            if (nd < (dist[w] ?: Int.MAX_VALUE)) {   // relax v-w: a shorter way to w
                dist[w] = nd
                parent[w] = v
                pq.add(Entry(nd, w))
                println("    relax $v-$w  dist[$w] = $nd")
            }
        }
    }
    return dist to parent
}

fun main() {
    val (dist, parent) = dijkstra("A")
    println(dist.toSortedMap())
    val path = generateSequence("G") { parent[it] }.toList().reversed()
    println("A -> G: $path, ${dist["G"]} minutes")
}
