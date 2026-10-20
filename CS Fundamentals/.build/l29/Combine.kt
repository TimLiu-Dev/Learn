import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val temps = flowOf(20, 21, 22).onEach { delay(300) }         // every 300 ms
    val units = flowOf("C", "F").onEach { delay(400) }           // every 400 ms
    val t0 = System.currentTimeMillis()

    temps.zip(units) { t, u -> "$t$u" }                          // pairs 1:1
        .collect { println("zip     ${it.padEnd(4)} at ${System.currentTimeMillis() - t0} ms") }

    val t1 = System.currentTimeMillis()
    temps.combine(units) { t, u -> "$t$u" }                      // latest of each
        .collect { println("combine ${it.padEnd(4)} at ${System.currentTimeMillis() - t1} ms") }
}
