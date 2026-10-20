// Kahn's algorithm. needs: task -> the tasks that must run before it.
fun topoSort(needs: Map<String, List<String>>): List<String>? {
    val waitingOn = needs.mapValues { it.value.size }.toMutableMap()   // in-degree
    val unlocks = mutableMapOf<String, MutableList<String>>()          // reversed edges
    for ((task, before) in needs) {
        for (b in before) unlocks.getOrPut(b) { mutableListOf() }.add(task)
    }
    val ready = ArrayDeque(waitingOn.filterValues { it == 0 }.keys)
    val order = mutableListOf<String>()
    while (ready.isNotEmpty()) {
        val task = ready.removeFirst()
        order += task
        for (next in unlocks[task].orEmpty()) {
            waitingOn[next] = waitingOn.getValue(next) - 1
            if (waitingOn[next] == 0) ready.addLast(next)
        }
    }
    return if (order.size == needs.size) order else null   // leftovers sit on a cycle
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
    println(topoSort(build))

    val circular = mapOf("a" to listOf("c"), "b" to listOf("a"), "c" to listOf("b"), "d" to listOf())
    println(topoSort(circular))
}
