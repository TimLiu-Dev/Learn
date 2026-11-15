import java.util.PriorityQueue

fun schedule(needs: Map<String, List<String>>): List<String>? {
    val waitingOn = needs.mapValues { it.value.size }.toMutableMap()
    val unlocks = mutableMapOf<String, MutableList<String>>()
    for ((task, before) in needs) {
        for (b in before) unlocks.getOrPut(b) { mutableListOf() }.add(task)
    }
    // "Repeatedly take the smallest" is the PriorityQueue row of the table.
    val ready = PriorityQueue(waitingOn.filterValues { it == 0 }.keys)
    val order = mutableListOf<String>()
    while (ready.isNotEmpty()) {
        val task = ready.poll()
        order += task
        for (next in unlocks[task].orEmpty()) {
            waitingOn[next] = waitingOn.getValue(next) - 1
            if (waitingOn[next] == 0) ready.add(next)
        }
    }
    return if (order.size == needs.size) order else null
}
