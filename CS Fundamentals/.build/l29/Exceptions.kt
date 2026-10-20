import kotlinx.coroutines.*

fun main() = runBlocking {
    // async: the exception waits inside the Deferred until await()
    supervisorScope {
        val d = async { throw IllegalArgumentException("bad input") }
        delay(100)
        println("async failed quietly so far: isCompleted=${d.isCompleted}")
        try {
            d.await()
        } catch (e: IllegalArgumentException) {
            println("await threw: ${e.message}")
        }
    }

    // launch: the exception propagates up the Job tree at once
    val handler = CoroutineExceptionHandler { _, e ->
        println("handler got: ${e.message}")
    }
    val scope = CoroutineScope(SupervisorJob() + handler)
    scope.launch { throw IllegalStateException("launch blew up") }.join()
    println("main still running")
}
