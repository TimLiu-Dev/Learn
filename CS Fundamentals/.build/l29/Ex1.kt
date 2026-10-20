import kotlinx.coroutines.*

suspend fun price(item: String): Int {
    delay(if (item == "durian") 50 else 200)
    if (item == "durian") throw IllegalStateException("no price for durian")
    println("  priced $item")
    return item.length * 10
}

// (a) all-or-nothing: the first failure cancels the others
suspend fun totalPrice(items: List<String>): Int = coroutineScope {
    items.map { async { price(it) } }.awaitAll().sum()
}

// (b) best effort: failures are skipped, siblings keep going
suspend fun totalPriceOrSkip(items: List<String>): Int = supervisorScope {
    items.map { async { price(it) } }
        .sumOf { d -> runCatching { d.await() }.getOrDefault(0) }
}

// (c) CPU work that can be cancelled
suspend fun countPrimes(limit: Int): Int = withContext(Dispatchers.Default) {
    var count = 0
    for (n in 2..limit) {
        ensureActive()
        if ((2..<n).none { n % it == 0 }) count++
    }
    count
}

fun main() = runBlocking {
    val cart = listOf("apple", "durian", "kiwi")
    println("a: " + runCatching { totalPrice(cart) }.exceptionOrNull()?.message)
    println("b: " + totalPriceOrSkip(cart))
    val t0 = System.nanoTime()
    val r = withTimeoutOrNull(100) { countPrimes(1_000_000) }
    println("c: $r after ${(System.nanoTime() - t0) / 1_000_000} ms")
}
