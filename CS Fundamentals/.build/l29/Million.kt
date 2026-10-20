import kotlinx.coroutines.*
import kotlin.concurrent.thread

// Rough peak heap: a background thread samples used memory every 5 ms
fun <T> peakHeapMb(work: () -> T): Pair<T, Long> {
    val rt = Runtime.getRuntime(); var peak = 0L; var running = true
    val sampler = thread(isDaemon = true) {
        while (running) { peak = maxOf(peak, rt.totalMemory() - rt.freeMemory()); Thread.sleep(5) }
    }
    val result = work(); running = false; sampler.join()
    return result to peak / (1024 * 1024)
}

fun main() {
    val n = Integer.getInteger("n", 100_000)        // try -Dn=10000
    val (ms, mb) = peakHeapMb {
        val t0 = System.nanoTime()
        runBlocking {
            repeat(n) { launch(Dispatchers.Default) { delay(1000) } }
        }
        (System.nanoTime() - t0) / 1_000_000
    }
    println("$n coroutines x delay(1000): $ms ms, peak heap ~$mb MB")
}
