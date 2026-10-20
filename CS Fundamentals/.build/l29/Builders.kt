import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun price(item: String): Int {
    delay(300)                         // pretend network call
    return item.length * 10
}

fun main() = runBlocking {
    val job: Job = launch {            // fire and forget
        delay(100)
        println("launch done")
    }
    job.join()

    val seq = measureTimeMillis {
        val a = price("apple")
        val b = price("kiwi")
        println("sequential total ${a + b}")
    }
    println("  took ~$seq ms")

    val conc = measureTimeMillis {
        val a: Deferred<Int> = async { price("apple") }
        val b = async { price("kiwi") }
        println("concurrent total ${a.await() + b.await()}")
    }
    println("  took ~$conc ms")
}
