import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class Shop {
    private val nextId = AtomicLong(1)
    val orders = ConcurrentHashMap<Long, String>()
    private val byKey = ConcurrentHashMap<String, Long>()

    fun post(item: String): Long {                 // POST /orders: creates a NEW order every time
        val id = nextId.getAndIncrement()
        orders[id] = item
        return id
    }
    fun put(id: Long, item: String): Long {        // PUT /orders/{id}: "make order id look like this"
        orders[id] = item
        return id
    }
    fun postWithKey(key: String, item: String): Long =   // POST + Idempotency-Key header
        byKey.computeIfAbsent(key) { post(item) }        // same key -> same result, no second order
}

// The server does the work, then the connection drops before the reply arrives. Once.
class FlakyNetwork {
    private var calls = 0
    fun <T> send(request: () -> T): T {
        val reply = request()
        if (calls++ == 0) throw IOException("connection reset before reply")
        return reply
    }
}

fun <T> withRetry(net: FlakyNetwork, request: () -> T): T {
    repeat(3) { attempt ->
        try {
            return net.send(request)
        } catch (e: IOException) {
            println("  attempt ${attempt + 1}: ${e.message}, retrying")
        }
    }
    error("gave up")
}

fun main() {
    val scenarios = listOf<Pair<String, (Shop) -> Long>>(
        "POST" to { it.post("Dune") },
        "PUT" to { it.put(42, "Dune") },
        "POST + key" to { it.postWithKey("c0ffee", "Dune") },
    )
    for ((name, call) in scenarios) {
        val shop = Shop()
        val net = FlakyNetwork()
        println("$name:")
        withRetry(net) { call(shop) }
        println("  orders on the server: ${shop.orders}")
    }
}
