fun main() {
    // L19's three-line LRU, capacity 2
    val cache = object : LinkedHashMap<Char, Unit>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Char, Unit>?) = size > 2
    }
    var hits = 0
    for (key in "abcabcabc") {
        if (cache[key] != null) hits++ else cache[key] = Unit
    }
    println("hits $hits of 9")
}
