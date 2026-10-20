package org.example

/** Keeps at most [capacity] entries, evicting the least recently used. Wraps, not extends (L8). */
class LruCache<K, V>(private val capacity: Int) {
    init { require(capacity > 0) { "capacity must be positive: $capacity" } }

    private val map = object : LinkedHashMap<K, V>(16, 0.75f, true) {     // true = access order
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?) = size > capacity
    }

    operator fun get(key: K): V? = map[key]                // counts as a use
    operator fun set(key: K, value: V) { map[key] = value }
    val size get() = map.size
    fun keys(): List<K> = map.keys.toList()                // least recently used first
}
