import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

// A read-through cache in front of a slow loader (a database query, say).
class CoalescingCache<K : Any, V : Any>(private val loader: (K) -> V) {
    private val cache = ConcurrentHashMap<K, V>()
    private val inFlight = ConcurrentHashMap<K, CompletableFuture<V>>()

    fun get(key: K): V {
        cache[key]?.let { return it }
        // Check-then-act: every thread that misses at the same moment runs the loader.
        val value = loader(key)
        cache[key] = value
        return value
    }
}
