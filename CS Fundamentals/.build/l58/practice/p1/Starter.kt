// rings: module -> ring number (0 = the domain, bigger = further out)
// deps:  module -> the modules it depends on
fun violations(rings: Map<String, Int>, deps: Map<String, Set<String>>): List<String> =
    TODO("every \"a -> b\" where b's ring is bigger than a's, sorted")

fun findCycle(deps: Map<String, Set<String>>): List<String>? =
    TODO("a path that starts and ends at the same module, or null")
