package katas

fun resource(name: String): String =
    checkNotNull(Graph::class.java.getResource("/$name")) { "missing $name" }.readText()

fun main() {
    val roads = parseGraph(resource("tinyEWD.txt"))
    for (to in 0 until roads.vertexCount) {
        val r = shortestPath(roads, 0, to)
        println(if (r == null) "0 to $to: unreachable" else "0 to $to (%.2f)  %s".format(r.distance, r.vertices.joinToString("->")))
    }
    val net = checkNotNull(cheapestNetwork(parseGraph(resource("tinyEWG.txt"))))
    println("cheapest network: ${net.size} edges, weight %.2f".format(net.sumOf { it.weight }))
}
