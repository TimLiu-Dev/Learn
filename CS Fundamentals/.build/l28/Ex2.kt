import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

data class Quote(val shop: String, val cents: Int?, val note: String)

fun main() {
    val shops = listOf(Triple("alpha", 120L, 1999), Triple("beta", 300L, 1899), Triple("gamma", 80L, -1))
    val start = System.nanoTime()
    Executors.newFixedThreadPool(3).use { io ->
        val futures = shops.map { (shop, ms, cents) ->
            CompletableFuture.supplyAsync({
                Thread.sleep(ms)
                if (cents < 0) throw IllegalStateException("$shop is down")
                Quote(shop, cents, "ok")
            }, io)
                .completeOnTimeout(Quote(shop, null, "too slow"), 200, TimeUnit.MILLISECONDS)
                .exceptionally { e -> Quote(shop, null, "failed: ${e.cause?.message}") }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()
        val quotes = futures.map { it.join() }
        quotes.forEach { println(it) }
        val best = quotes.filter { it.cents != null }.minByOrNull { it.cents!! }
        println("best: ${best?.shop} at ${best?.cents} cents, after ${(System.nanoTime() - start) / 1_000_000} ms")
    }
}
