enum class Colour { WHITE, GREY, BLACK }   // not started, on the current path, finished

// DFS over "needs" edges (task -> its prerequisites).
// Returns a topological order, or the first cycle it finds.
fun check(needs: Map<String, List<String>>): String {
    val colour = needs.keys.associateWith { Colour.WHITE }.toMutableMap()
    val path = mutableListOf<String>()      // the GREY vertices, in order
    val order = mutableListOf<String>()     // postorder: prerequisites come first

    fun visit(v: String): List<String>? {
        colour[v] = Colour.GREY
        path += v
        for (n in needs.getValue(v)) {
            when (colour.getValue(n)) {
                Colour.GREY -> return path.subList(path.indexOf(n), path.size) + n   // back edge: a cycle
                Colour.WHITE -> visit(n)?.let { return it }
                Colour.BLACK -> {}          // finished earlier, by another route: no cycle through it
            }
        }
        path.removeAt(path.lastIndex)       // not removeLast(): see Part 4
        colour[v] = Colour.BLACK
        order += v
        return null
    }

    for (v in needs.keys) {
        if (colour[v] == Colour.WHITE) visit(v)?.let { return "cycle: " + it.joinToString(" -> ") }
    }
    return "order: $order"
}

fun main() {
    val build = mapOf(
        "compile" to listOf(),
        "resources" to listOf(),
        "classes" to listOf("compile", "resources"),
        "compileTest" to listOf("classes"),
        "test" to listOf("compileTest"),
        "jar" to listOf("classes"),
        "build" to listOf("jar", "test"),
    )
    println(check(build))

    val circular = mapOf("a" to listOf("c"), "b" to listOf("a"), "c" to listOf("b"), "d" to listOf())
    println(check(circular))
}
