package shortener

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Thread-safe LRU (L41): an access-ordered LinkedHashMap behind one lock. capacity 0 = cache off. */
class LruCache<K, V>(private val capacity: Int) {
    private val lock = ReentrantLock()
    private val map = object : LinkedHashMap<K, V>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>) = size > capacity
    }
    val hits = AtomicLong()
    val misses = AtomicLong()

    fun get(key: K): V? = lock.withLock { map[key] }
        .also { if (it == null) misses.incrementAndGet() else hits.incrementAndGet() }

    fun put(key: K, value: V) {
        if (capacity > 0) lock.withLock { map[key] = value }
    }

    fun remove(key: K) {
        lock.withLock { map.remove(key) }
    }
}
