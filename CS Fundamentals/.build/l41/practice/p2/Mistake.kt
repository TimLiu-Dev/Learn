import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

// One big lock: only one load ever runs, but every key waits behind every other key's load.
class CoalescingCache<K : Any, V : Any>(private val loader: (K) -> V) {
    private val cache = ConcurrentHashMap<K, V>()
    private val inFlight = ConcurrentHashMap<K, CompletableFuture<V>>()

    @Synchronized
    fun get(key: K): V {
        cache[key]?.let { return it }
        val value = loader(key)
        cache[key] = value
        return value
    }
}
