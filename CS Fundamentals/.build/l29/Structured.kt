import kotlinx.coroutines.*

suspend fun task(name: String, ms: Long, fail: Boolean = false): String {
    try {
        delay(ms)
        if (fail) throw IllegalStateException("$name failed")
        println("  $name finished")
        return name
    } catch (e: CancellationException) {
        println("  $name cancelled")
        throw e                        // always rethrow cancellation
    }
}

fun main() = runBlocking {
    println("1. coroutineScope waits for every child")
    coroutineScope {
        launch { task("A", 100) }
        launch { task("B", 200) }
    }
    println("   scope returned after both")

    println("2. one child fails: siblings cancelled, error rethrown")
    try {
        coroutineScope {
            launch { task("C", 300) }
            launch { task("D", 100, fail = true) }
        }
    } catch (e: IllegalStateException) {
        println("   caught: ${e.message}")
    }

    println("3. supervisorScope: a failure stays local")
    supervisorScope {
        val e = async { task("E", 300) }
        val f = async { task("F", 100, fail = true) }
        println("   E -> ${e.await()}")
        println("   F -> ${runCatching { f.await() }.exceptionOrNull()?.message}")
    }
}
