package katas

import java.util.PriorityQueue

data class Edge(val from: Int, val to: Int, val weight: Double)
data class Graph(val vertexCount: Int, val edges: List<Edge>)
data class Route(val distance: Double, val vertices: List<Int>)

// algs4 format: V, then E, then one "from to weight" line per edge.
fun parseGraph(text: String): Graph {
    val lines = text.lines().filter { it.isNotBlank() }
    val edges = lines.drop(2).map { line ->
        val (v, w, weight) = line.trim().split(Regex("\\s+"))
        Edge(v.toInt(), w.toInt(), weight.toDouble())
    }
    return Graph(lines[0].trim().toInt(), edges)
}

// Dijkstra, lazy version. Edges are one-way. Null if `to` can't be reached.
fun shortestPath(g: Graph, from: Int, to: Int): Route? {
    require(g.edges.none { it.weight < 0 }) { "Dijkstra needs non-negative weights" }
    val adj = List(g.vertexCount) { mutableListOf<Edge>() }
    for (e in g.edges) adj[e.from] += e
    val dist = DoubleArray(g.vertexCount) { Double.POSITIVE_INFINITY }
    val edgeTo = arrayOfNulls<Edge>(g.vertexCount)
    val settled = BooleanArray(g.vertexCount)
    val pq = PriorityQueue(compareBy<Pair<Double, Int>> { it.first })
    dist[from] = 0.0
    pq.add(0.0 to from)
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (settled[v]) continue
        settled[v] = true
        if (v == to) break                                    // final once settled
        for (e in adj[v]) {
            if (!settled[e.to] && d + e.weight < dist[e.to]) {
                dist[e.to] = d + e.weight
                edgeTo[e.to] = e
                pq.add(dist[e.to] to e.to)
            }
        }
    }
    if (dist[to] == Double.POSITIVE_INFINITY) return null
    val path = generateSequence(to) { v -> edgeTo[v]?.from }.toList().reversed()
    return Route(dist[to], path)
}

// The slow, simple oracle for tests: relax every edge V-1 times.
fun bellmanFord(g: Graph, from: Int): DoubleArray {
    val dist = DoubleArray(g.vertexCount) { Double.POSITIVE_INFINITY }
    dist[from] = 0.0
    repeat(g.vertexCount - 1) {
        for (e in g.edges) if (dist[e.from] + e.weight < dist[e.to]) dist[e.to] = dist[e.from] + e.weight
    }
    return dist
}

// Kruskal. Edges count as two-way here. Null if the graph isn't connected.
fun cheapestNetwork(g: Graph): List<Edge>? {
    val sets = DisjointSets(g.vertexCount)
    val tree = mutableListOf<Edge>()
    for (e in g.edges.sortedBy { it.weight }) {
        if (tree.size == g.vertexCount - 1) break
        if (sets.union(e.from, e.to)) tree += e
    }
    return if (tree.size == g.vertexCount - 1) tree else null
}
