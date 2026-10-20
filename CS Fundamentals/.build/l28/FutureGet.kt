import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun slow(name: String, ms: Long): Callable<String> = Callable {
    Thread.sleep(ms)
    "$name after $ms ms"
}

fun main() {
    Executors.newFixedThreadPool(4).use { pool ->
        // get with a timeout
        val f = pool.submit(slow("report", 500))
        try {
            println(f.get(100, TimeUnit.MILLISECONDS))
        } catch (e: TimeoutException) {
            println("gave up waiting after 100 ms; cancel = ${f.cancel(true)}")  // interrupts the task
        }

        // invokeAll: wait for every task; results come back in submission order
        var start = System.nanoTime()
        val all = pool.invokeAll(listOf(slow("a", 300), slow("b", 100), slow("c", 200)))
        println("invokeAll: ${all.map { it.get() }} in ${(System.nanoTime() - start) / 1_000_000} ms")

        // invokeAny: the first one to finish successfully wins; the rest are cancelled
        start = System.nanoTime()
        val first = pool.invokeAny(listOf(slow("mirror-1", 300), slow("mirror-2", 100), slow("mirror-3", 200)))
        println("invokeAny: $first in ${(System.nanoTime() - start) / 1_000_000} ms")
    }
}
