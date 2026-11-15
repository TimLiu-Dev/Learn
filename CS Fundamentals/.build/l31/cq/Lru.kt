fun main() {
    val cache = object : LinkedHashMap<String, String>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>) = size > 2
    }
    cache["/a"] = "A"
    cache["/b"] = "B"
    val hit = cache["/a"]
    cache["/c"] = "C"
    println("$hit ${cache.keys}")
}
