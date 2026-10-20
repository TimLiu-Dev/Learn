import kotlinx.coroutines.*
import kotlin.time.measureTime

fun main() = runBlocking {             // ONE thread: main
    val withDelay = measureTime {
        coroutineScope {
            repeat(10_000) { launch { delay(100) } }
        }
    }
    println("10,000 x delay(100):     $withDelay")

    val withSleep = measureTime {
        coroutineScope {
            repeat(20) { launch { Thread.sleep(100) } }
        }
    }
    println("20 x Thread.sleep(100):  $withSleep")
    println("all on thread: ${Thread.currentThread().name}")
}
