package cache

import java.time.Clock
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A fixed-capacity LRU cache with a time-to-live per entry.
 * HashMap (L19) for O(1) lookup + doubly linked list (L17) for O(1) "move to front"
 * and "drop the oldest". One lock guards both, so every method is thread-safe.
 */
class LruCache<K : Any, V : Any>(
    private val capacity: Int,
    private val ttl: Duration,
    private val clock: Clock = Clock.systemUTC(),
) {
    private class Node<K, V>(val key: K?, var value: V?, var expiresAt: Long) {
        var prev: Node<K, V>? = null
        var next: Node<K, V>? = null
    }

    private val map = HashMap<K, Node<K, V>>()
    private val head = Node<K, V>(null, null, 0)      // sentinel: most recently used side
    private val tail = Node<K, V>(null, null, 0)      // sentinel: least recently used side
    private val lock = ReentrantLock()
    private var hits = 0L
    private var misses = 0L
    private var evictions = 0L

    init {
        require(capacity > 0) { "capacity must be positive" }
        head.next = tail
        tail.prev = head
    }

    fun get(key: K): V? = lock.withLock {
        val node = map[key]
        if (node == null || node.expiresAt <= clock.millis()) {
            if (node != null) { unlink(node); map.remove(key) }     // expired: drop it now
            misses++
            return null
        }
        hits++
        unlink(node)
        addFront(node)                                             // now the most recently used
        node.value
    }

    fun put(key: K, value: V) {
        lock.withLock {
            val expiresAt = clock.millis() + ttl.toMillis()
            val existing = map[key]
            if (existing != null) {
                existing.value = value
                existing.expiresAt = expiresAt
                unlink(existing)
                addFront(existing)
                return
            }
            if (map.size == capacity) {                            // full: evict the LRU entry
                val oldest = tail.prev!!
                unlink(oldest)
                map.remove(oldest.key!!)
                evictions++
            }
            val node = Node(key, value, expiresAt)
            map[key] = node
            addFront(node)
        }
    }

    fun remove(key: K) {
        lock.withLock { map.remove(key)?.let { unlink(it) } }
    }

    val size: Int get() = lock.withLock { map.size }

    fun stats(): Stats = lock.withLock { Stats(hits, misses, evictions) }

    /** Keys from most to least recently used. */
    fun keys(): List<K> = lock.withLock {
        generateSequence(head.next) { it.next }.takeWhile { it !== tail }.map { it.key!! }.toList()
    }

    private fun unlink(n: Node<K, V>) {
        n.prev!!.next = n.next
        n.next!!.prev = n.prev
    }

    private fun addFront(n: Node<K, V>) {
        n.next = head.next
        n.prev = head
        head.next!!.prev = n
        head.next = n
    }
}

data class Stats(val hits: Long, val misses: Long, val evictions: Long) {
    val hitRate: Double get() = if (hits + misses == 0L) 0.0 else hits.toDouble() / (hits + misses)
}
