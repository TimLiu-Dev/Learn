data class Task(val name: String, val remaining: Int)

fun roundRobin(tasks: List<Task>, slice: Int): List<String> {
    val ready = ArrayDeque(tasks)                   // FIFO: front = next to run
    val finished = mutableListOf<String>()
    var clock = 0
    while (ready.isNotEmpty()) {
        val t = ready.removeFirst()
        val ran = minOf(slice, t.remaining)
        clock += ran
        val left = t.remaining - ran
        if (left > 0) {
            println("t=%2d  %s ran %d, %d left -> back of the queue".format(clock, t.name, ran, left))
            ready.addLast(t.copy(remaining = left))
        } else {
            println("t=%2d  %s ran %d, done".format(clock, t.name, ran))
            finished.add(t.name)
        }
    }
    return finished
}

fun main() {
    val order = roundRobin(listOf(Task("build", 5), Task("email", 2), Task("backup", 7)), slice = 3)
    println("finished in order: $order")
}
