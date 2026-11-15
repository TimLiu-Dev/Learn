data class Entry(val key: Int, val rowid: Int)

class SortedIndex(entries: List<Entry>) {
    private val sorted = entries.sortedWith(compareBy({ it.key }, { it.rowid }))
    val size get() = sorted.size

    var reads = 0
        private set

    private fun at(i: Int): Entry {
        reads++
        return sorted[i]
    }

    // Textbook "find it" binary search: stops at whichever equal key it hits first.
    fun lowerBound(key: Int): Int {
        var lo = 0
        var hi = size - 1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val k = at(mid).key
            when {
                k == key -> return mid
                k < key -> lo = mid + 1
                else -> hi = mid - 1
            }
        }
        return lo
    }

    fun lookup(key: Int): List<Int> = range(key, key)

    fun range(lo: Int, hi: Int): List<Int> {
        val out = mutableListOf<Int>()
        var i = lowerBound(lo)
        while (i < size) {
            val e = at(i++)
            if (e.key > hi) break
            out += e.rowid
        }
        return out
    }
}
