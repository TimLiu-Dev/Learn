import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

data class User(val id: Int, val name: String)
data class Order(val item: String, val qty: Int)

val start = System.nanoTime()
fun log(msg: String) =
    println("%4d ms [%s] %s".format((System.nanoTime() - start) / 1_000_000, Thread.currentThread().name, msg))

// three "remote calls" with fake latency
fun fetchUser(id: Int): User { Thread.sleep(200); log("got user"); return User(id, "Ada") }
fun fetchOrders(u: User): List<Order> {
    Thread.sleep(300); log("got orders for ${u.name}")
    return listOf(Order("coffee", 2), Order("bagel", 1))
}
fun fetchPrices(): Map<String, Int> { Thread.sleep(250); log("got prices"); return mapOf("coffee" to 350, "bagel" to 275) }

fun price(orders: List<Order>, prices: Map<String, Int>) = orders.sumOf { it.qty * prices.getValue(it.item) }

fun main() {
    log("sequential: one call after another")
    val seq = price(fetchOrders(fetchUser(42)), fetchPrices())
    log("sequential total = $seq cents")

    Executors.newFixedThreadPool(4).use { io ->           // our own pool for blocking calls
        log("async: prices run while user → orders runs")
        val user = CompletableFuture.supplyAsync({ fetchUser(42) }, io)
        val orders = user.thenCompose { u -> CompletableFuture.supplyAsync({ fetchOrders(u) }, io) }
        val prices = CompletableFuture.supplyAsync({ fetchPrices() }, io)
        val total = orders.thenCombine(prices) { o, p -> price(o, p) }
        log("pipeline built, nothing waited yet")
        log("async total = ${total.join()} cents")
    }
}
