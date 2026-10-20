import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun busyFor(ms: Long, check: () -> Unit) {
    val end = System.currentTimeMillis() + ms
    while (System.currentTimeMillis() < end) check()
}

fun main() = runBlocking {
    val stubborn = launch(Dispatchers.Default) {
        busyFor(500) { }                       // never checks
        println("stubborn: loop ran to the end anyway")
    }
    delay(50)
    val t1 = measureTimeMillis { stubborn.cancelAndJoin() }
    println("stubborn: cancelAndJoin took $t1 ms")

    val polite = launch(Dispatchers.Default) {
        busyFor(500) { ensureActive() }        // throws once cancelled
        println("polite: never printed")
    }
    delay(50)
    val t2 = measureTimeMillis { polite.cancelAndJoin() }
    println("polite: cancelAndJoin took $t2 ms")

    val r = withTimeoutOrNull(200) { delay(1000); "done" }
    println("withTimeoutOrNull -> $r")

    try {
        withTimeout(200) {
            try {
                delay(1000)
            } finally {
                withContext(NonCancellable) {
                    delay(50)                  // suspending cleanup
                    println("cleanup ran after cancellation")
                }
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("caught ${e::class.simpleName}: ${e.message}")
    }
}
