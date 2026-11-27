// rings: module -> ring number (0 = the domain, bigger = further out)
// deps:  module -> the modules it depends on
fun violations(rings: Map<String, Int>, deps: Map<String, Set<String>>): List<String> =
    deps.flatMap { (from, tos) ->
        tos.filter { to -> rings.getValue(to) > rings.getValue(from) }.map { to -> "$from -> $to" }
    }.sorted()

fun findCycle(deps: Map<String, Set<String>>): List<String>? {
    val done = mutableSetOf<String>()        // fully explored: no cycle through here
    val path = mutableListOf<String>()       // the modules on the current DFS path, in order

    fun visit(m: String): List<String>? {
        if (m in path) return path.subList(path.indexOf(m), path.size) + m   // back on our own path: a cycle
        if (m in done) return null
        path += m
        for (next in deps[m].orEmpty()) visit(next)?.let { return it }
        path.removeAt(path.lastIndex)
        done += m
        return null
    }

    return deps.keys.firstNotNullOfOrNull { visit(it) }
}
