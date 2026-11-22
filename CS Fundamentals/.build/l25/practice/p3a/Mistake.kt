fun executionOrder(tasks: Map<String, List<String>>, requested: List<String>): List<String> {
    val order = mutableListOf<String>()
    val path = mutableListOf<String>()

    fun visit(name: String) {
        if (name in path) throw IllegalStateException("Cycle: " + (path.drop(path.indexOf(name)) + name).joinToString(" -> "))
        val deps = tasks[name] ?: throw IllegalArgumentException("Task '$name' not found")
        path += name
        deps.forEach { visit(it) }
        path.removeAt(path.lastIndex)
        order += name                          // no "done" set: a shared dependency runs again
    }

    requested.forEach { visit(it) }
    return order
}
