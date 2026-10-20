data class Edge(val from: String, val to: String)

// Undirected: every edge goes into BOTH vertices' lists.
fun undirected(edges: List<Edge>): Map<String, List<String>> =
    buildMap<String, MutableList<String>> {
        for ((a, b) in edges) {
            getOrPut(a) { mutableListOf() }.add(b)
            getOrPut(b) { mutableListOf() }.add(a)
        }
    }

fun main() {
    val edges = listOf(
        Edge("A", "B"), Edge("A", "C"), Edge("B", "D"), Edge("C", "D"), Edge("C", "E"),
        Edge("D", "F"), Edge("E", "F"), Edge("F", "G"), Edge("E", "H"),
    )

    val adj = undirected(edges)
    println("adjacency list:")
    for ((v, ns) in adj) println("  $v -> $ns")

    val names = adj.keys.sorted()
    val index = names.withIndex().associate { (i, name) -> name to i }
    val matrix = Array(names.size) { BooleanArray(names.size) }
    for ((a, b) in edges) {
        matrix[index.getValue(a)][index.getValue(b)] = true
        matrix[index.getValue(b)][index.getValue(a)] = true
    }
    println("adjacency matrix:")
    println("    " + names.joinToString(" "))
    for (i in names.indices) {
        println("  ${names[i]} " + matrix[i].joinToString(" ") { if (it) "1" else "." })
    }

    println("edge C-E?  list: ${"E" in adj.getValue("C")}  matrix: ${matrix[index.getValue("C")][index.getValue("E")]}")
    println("neighbours of F: ${adj.getValue("F")}")
    println("V=${names.size} E=${edges.size}  list slots=${adj.values.sumOf { it.size }}  matrix cells=${names.size * names.size}")
}
