fun main() {
    // accessOrder = true: get() moves an entry to the end (most recently used)
    val byAccess = LinkedHashMap<String, Int>(16, 0.75f, true)
    for (w in listOf("a", "b", "c")) byAccess[w] = 0
    byAccess["a"]
    println("access order: ${byAccess.keys}")

    // Override removeEldestEntry: put() asks it after every insert
    val cache = object : LinkedHashMap<String, Int>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Int>?) = size > 3
    }
    for (w in listOf("a", "b", "c")) cache[w] = w[0].code
    cache["a"]                                       // touch a: b is now least recent
    cache["d"] = 100                                 // 4 entries > 3: evict the eldest
    println("cache: ${cache.keys}")
}
