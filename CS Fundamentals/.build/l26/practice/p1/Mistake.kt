import kotlin.concurrent.thread

fun runAll(prefix: String, tasks: List<() -> String>): List<String> {
    val results = Array(tasks.size) { "" }
    tasks.forEachIndexed { i, task ->
        thread(name = "$prefix-$i") { results[i] = task() }.join()   // waits before starting the next one
    }
    return results.toList()
}
