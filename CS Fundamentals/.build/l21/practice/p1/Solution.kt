fun toList(n: Int, edges: List<Pair<Int, Int>>): List<List<Int>> {
    val adj = List(n) { mutableListOf<Int>() }   // n separate lists, one per vertex
    for ((a, b) in edges) {
        adj[a].add(b)
        adj[b].add(a)                            // undirected: both directions
    }
    return adj
}

fun toMatrix(n: Int, edges: List<Pair<Int, Int>>): Array<BooleanArray> {
    val m = Array(n) { BooleanArray(n) }
    for ((a, b) in edges) {
        m[a][b] = true
        m[b][a] = true
    }
    return m
}

fun isolated(adj: List<List<Int>>): List<Int> = adj.indices.filter { adj[it].isEmpty() }
