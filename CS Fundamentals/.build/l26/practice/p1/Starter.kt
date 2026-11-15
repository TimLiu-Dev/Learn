import kotlin.concurrent.thread

// Runs each task on its own new thread, named "$prefix-0", "$prefix-1", …
// Waits for all of them, then returns their results in task order.
fun runAll(prefix: String, tasks: List<() -> String>): List<String> {
    TODO("start one thread per task, then join them all")
}
