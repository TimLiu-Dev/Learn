import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

// A read-through cache in front of a slow loader (a database query, say).
class CoalescingCache<K : Any, V : Any>(private val loader: (K) -> V) {
    private val cache = ConcurrentHashMap<K, V>()
    private val inFlight = ConcurrentHashMap<K, CompletableFuture<V>>()

    fun get(key: K): V {
        cache[key]?.let { return it }
        val mine = CompletableFuture<V>()
        val running = inFlight.putIfAbsent(key, mine)     // atomic: exactly one caller gets null back
        if (running != null) return running.join()       // someone else is loading: wait for their result
        try {
            // Re-check: a load may have finished between our miss and our putIfAbsent.
            val value = cache[key] ?: loader(key)
            cache[key] = value                            // cache first, then leave inFlight
            mine.complete(value)
            return value
        } catch (e: Exception) {
            mine.completeExceptionally(e)                 // every waiter sees the failure
            throw e
        } finally {
            inFlight.remove(key)                          // success or failure: never wedge the key
        }
    }
}
