// Visiting order of an n x n grid stored flat: cell (r, c) is at index r * n + c.
fun rowMajor(n: Int): List<Int> = (0 until n).flatMap { r -> (0 until n).map { c -> r * n + c } }

fun columnMajor(n: Int): List<Int> = (0 until n).flatMap { c -> (0 until n).map { r -> r * n + c } }

// Replay the accesses through an LRU cache holding `lines` cache lines of `intsPerLine` ints.
fun misses(indices: List<Int>, intsPerLine: Int, lines: Int): Int {
    // accessOrder = true: every get() moves the line to the "recently used" end (L19's LRU trick)
    val cache = LinkedHashMap<Int, Unit>(16, 0.75f, true)
    var count = 0
    for (i in indices) {
        val line = i / intsPerLine
        if (cache[line] == null) {                         // a hit refreshes the line; a miss loads it
            count++
            if (cache.size == lines) cache.remove(cache.keys.first())   // evict the least recently used
            cache[line] = Unit
        }
    }
    return count
}
