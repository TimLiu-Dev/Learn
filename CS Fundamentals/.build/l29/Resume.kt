import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

// A 2-thread pool with short thread names, so the output is easy to read
val ids = AtomicInteger(0)
val pool = Executors.newFixedThreadPool(2) { r -> Thread(r, "T${ids.incrementAndGet()}") }
    .asCoroutineDispatcher()

suspend fun worker(name: String, wait: Long) {
    println("$name runs on ${Thread.currentThread().name}, then suspends")
    delay(wait)
    println("$name resumes on ${Thread.currentThread().name}")
}

fun main() = runBlocking {
    withContext(pool) {
        launch { worker("A", 100) }
        launch { worker("B", 50) }
        launch { worker("C", 75) }
    }
    pool.close()
}
