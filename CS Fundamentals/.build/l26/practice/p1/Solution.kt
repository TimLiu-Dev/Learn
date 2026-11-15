import kotlin.concurrent.thread

// Runs each task on its own new thread, named "$prefix-0", "$prefix-1", …
// Waits for all of them, then returns their results in task order.
fun runAll(prefix: String, tasks: List<() -> String>): List<String> {
    val results = Array(tasks.size) { "" }      // one slot per thread: nothing is shared
    val threads = tasks.mapIndexed { i, task ->
        thread(name = "$prefix-$i") { results[i] = task() }
    }
    threads.forEach { it.join() }                // start them all first, then wait for all
    return results.toList()
}
