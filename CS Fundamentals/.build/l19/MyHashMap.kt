// Part 2: separate chaining, resize at load factor 0.75
class MyHashMap<K : Any, V> {                       // K : Any means no null keys (L13)
    private class Entry<K, V>(val key: K, var value: V, var next: Entry<K, V>?)

    private var buckets = arrayOfNulls<Entry<K, V>>(8)
    var size = 0
        private set
    val capacity get() = buckets.size

    private fun indexFor(key: K, cap: Int) = key.hashCode().mod(cap)

    private fun findEntry(key: K): Entry<K, V>? {
        var e = buckets[indexFor(key, capacity)]
        while (e != null) {
            if (e.key == key) return e                 // == calls equals()
            e = e.next
        }
        return null
    }

    fun put(key: K, value: V): V? {
        val existing = findEntry(key)
        if (existing != null) {                         // same key: replace the value
            val old = existing.value
            existing.value = value
            return old
        }
        val i = indexFor(key, capacity)
        buckets[i] = Entry(key, value, buckets[i])     // new key: prepend to the chain
        size++
        if (size > capacity * 0.75) resize()
        return null
    }

    operator fun get(key: K): V? = findEntry(key)?.value
    operator fun set(key: K, value: V) { put(key, value) }
    fun containsKey(key: K) = findEntry(key) != null

    private fun resize() {
        val old = buckets
        buckets = arrayOfNulls(old.size * 2)
        for (head in old) {
            var e = head
            while (e != null) {
                val next = e.next
                val i = indexFor(e.key, capacity)       // the index depends on capacity
                e.next = buckets[i]
                buckets[i] = e
                e = next
            }
        }
    }

    fun chainLengths(): List<Int> = buckets.map { head ->
        var n = 0
        var e = head
        while (e != null) { n++; e = e.next }
        n
    }
}

fun main() {
    val m = MyHashMap<String, Int>()
    for (w in "one two three four five six seven".split(" ")) {
        m[w] = w.length
        println("put $w: size ${m.size}, capacity ${m.capacity}")
    }
    println("put(two, 22) returned ${m.put("two", 22)}; now ${m["two"]}")
    println("get(ten) = ${m["ten"]}, containsKey(six) = ${m.containsKey("six")}")

    val big = MyHashMap<String, Int>()
    for (i in 0 until 1000) big["key$i"] = i
    val lengths = big.chainLengths()
    println("1000 keys: capacity ${big.capacity}, longest chain ${lengths.max()}")
    println("buckets by chain length: ${lengths.groupingBy { it }.eachCount().toSortedMap()}")
}
