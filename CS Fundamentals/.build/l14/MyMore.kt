fun <T, K> Iterable<T>.myGroupBy(key: (T) -> K): Map<K, List<T>> {
    val groups = LinkedHashMap<K, MutableList<T>>()
    for (item in this) groups.getOrPut(key(item)) { mutableListOf() }.add(item)
    return groups
}

fun <T, C : Comparable<C>> Iterable<T>.myMaxByOrNull(selector: (T) -> C): T? {
    var best: T? = null
    var bestKey: C? = null
    for (item in this) {
        val k = selector(item)
        if (bestKey == null || k > bestKey) { best = item; bestKey = k }
    }
    return best
}

inline fun <T> Iterable<T>.myCount(predicate: (T) -> Boolean): Int {
    var n = 0
    for (item in this) if (predicate(item)) n++
    return n
}

fun main() {
    val words = listOf("apple", "avocado", "banana", "blueberry", "cherry")
    println(words.myGroupBy { it.first() })
    println(words.myMaxByOrNull { it.length })
    println(emptyList<String>().myMaxByOrNull { it.length })
    println(words.myCount { it.length > 5 })
    println(words.myGroupBy { it.first() } == words.groupBy { it.first() })
}
