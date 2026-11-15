fun rowMajor(n: Int): List<Int> = (0 until n).flatMap { r -> (0 until n).map { c -> r * n + c } }

fun columnMajor(n: Int): List<Int> = (0 until n).flatMap { c -> (0 until n).map { r -> r * n + c } }

// FIFO, not LRU: a hit doesn't refresh the line, so a busy line can still be evicted first.
fun misses(indices: List<Int>, intsPerLine: Int, lines: Int): Int {
    val cache = LinkedHashSet<Int>()
    var count = 0
    for (i in indices) {
        val line = i / intsPerLine
        if (line !in cache) {
            count++
            if (cache.size == lines) cache.remove(cache.first())
            cache.add(line)
        }
    }
    return count
}
