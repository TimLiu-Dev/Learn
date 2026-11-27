// rings: module -> ring number (0 = the domain, bigger = further out)
// deps:  module -> the modules it depends on
fun violations(rings: Map<String, Int>, deps: Map<String, Set<String>>): List<String> =
    deps.flatMap { (from, tos) ->
        tos.filter { to -> rings.getValue(to) > rings.getValue(from) }.map { to -> "$from -> $to" }
    }.sorted()

// The mistake: "seen before" is treated as "cycle", so a diamond looks like a loop.
fun findCycle(deps: Map<String, Set<String>>): List<String>? {
    val seen = mutableSetOf<String>()
    fun visit(m: String, path: List<String>): List<String>? {
        if (m in seen) return path + m
        seen += m
        for (next in deps[m].orEmpty()) visit(next, path + m)?.let { return it }
        return null
    }
    return deps.keys.firstNotNullOfOrNull { visit(it, emptyList()) }
}
