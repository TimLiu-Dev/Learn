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

    // Correct answers, but it reads from the start: a full scan wearing an index's clothes.
    fun lowerBound(key: Int): Int {
        var i = 0
        while (i < size && at(i).key < key) i++
        return i
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
