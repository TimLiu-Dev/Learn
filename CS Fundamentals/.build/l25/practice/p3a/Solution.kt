fun executionOrder(tasks: Map<String, List<String>>, requested: List<String>): List<String> {
    val order = mutableListOf<String>()
    val done = mutableSetOf<String>()          // already in the order: never run twice
    val path = mutableListOf<String>()         // the chain we're inside right now: finds cycles

    fun visit(name: String) {
        if (name in done) return
        if (name in path) {
            val cycle = path.drop(path.indexOf(name)) + name
            throw IllegalStateException("Cycle: " + cycle.joinToString(" -> "))
        }
        val deps = tasks[name] ?: throw IllegalArgumentException("Task '$name' not found")
        path += name
        deps.forEach { visit(it) }
        path.removeAt(path.lastIndex)
        done += name
        order += name                          // after all its dependencies: post-order
    }

    requested.forEach { visit(it) }
    return order
}
