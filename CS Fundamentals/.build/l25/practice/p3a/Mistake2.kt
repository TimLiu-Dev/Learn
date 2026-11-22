fun executionOrder(tasks: Map<String, List<String>>, requested: List<String>): List<String> {
    val order = mutableListOf<String>()
    val done = mutableSetOf<String>()

    fun visit(name: String) {
        if (name in done) return
        val deps = tasks[name] ?: throw IllegalArgumentException("Task '$name' not found")
        deps.forEach { visit(it) }             // no "path": a cycle recurses until the stack overflows
        done += name
        order += name
    }

    requested.forEach { visit(it) }
    return order
}
