fun schedule(needs: Map<String, List<String>>): List<String>? {
    val waitingOn = needs.mapValues { it.value.size }.toMutableMap()
    val unlocks = mutableMapOf<String, MutableList<String>>()
    for ((task, before) in needs) {
        for (b in before) unlocks.getOrPut(b) { mutableListOf() }.add(task)
    }
    // Sorted once at the start, then a plain FIFO queue.
    val ready = ArrayDeque(waitingOn.filterValues { it == 0 }.keys.sorted())
    val order = mutableListOf<String>()
    while (ready.isNotEmpty()) {
        val task = ready.removeFirst()
        order += task
        for (next in unlocks[task].orEmpty()) {
            waitingOn[next] = waitingOn.getValue(next) - 1
            if (waitingOn[next] == 0) ready.addLast(next)
        }
    }
    return if (order.size == needs.size) order else null
}
