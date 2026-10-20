package org.example

/** A hash map with separate chaining. Average O(1) put/get/remove; worst case O(n). */
class MyHashMap<K : Any, V> {
    private class Entry<K, V>(val key: K, var value: V, var next: Entry<K, V>?)

    private var buckets = arrayOfNulls<Entry<K, V>>(INITIAL_CAPACITY)
    var size = 0
        private set
    val capacity get() = buckets.size

    private fun indexFor(key: K, cap: Int) = key.hashCode().mod(cap)

    private fun findEntry(key: K): Entry<K, V>? {
        var e = buckets[indexFor(key, capacity)]
        while (e != null) {
            if (e.key == key) return e
            e = e.next
        }
        return null
    }

    /** Returns the previous value for [key], or null if it was absent. */
    fun put(key: K, value: V): V? {
        val existing = findEntry(key)
        if (existing != null) {
            val old = existing.value
            existing.value = value
            return old
        }
        val i = indexFor(key, capacity)
        buckets[i] = Entry(key, value, buckets[i])
        size++
        if (size > capacity * LOAD_FACTOR) resize()
        return null
    }

    operator fun get(key: K): V? = findEntry(key)?.value
    operator fun set(key: K, value: V) { put(key, value) }
    fun containsKey(key: K) = findEntry(key) != null

    /** Unlinks the entry for [key] from its chain. Returns its value, or null if absent. */
    fun remove(key: K): V? {
        val i = indexFor(key, capacity)
        var prev: Entry<K, V>? = null
        var e = buckets[i]
        while (e != null) {
            if (e.key == key) {
                if (prev == null) buckets[i] = e.next else prev.next = e.next
                size--
                return e.value
            }
            prev = e
            e = e.next
        }
        return null
    }

    fun keys(): List<K> {
        val out = ArrayList<K>(size)
        for (head in buckets) {
            var e = head
            while (e != null) { out.add(e.key); e = e.next }
        }
        return out
    }

    private fun resize() {
        val old = buckets
        buckets = arrayOfNulls(old.size * 2)
        for (head in old) {
            var e = head
            while (e != null) {
                val next = e.next
                val i = indexFor(e.key, capacity)
                e.next = buckets[i]
                buckets[i] = e
                e = next
            }
        }
    }

    private companion object {
        const val INITIAL_CAPACITY = 8
        const val LOAD_FACTOR = 0.75
    }
}
