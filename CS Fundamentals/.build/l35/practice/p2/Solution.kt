// One index entry: the indexed value, and the rowid of the table row it points to.
data class Entry(val key: Int, val rowid: Int)

// The leaf level of an index, laid end to end: entries sorted by (key, rowid).
class SortedIndex(entries: List<Entry>) {
    private val sorted = entries.sortedWith(compareBy({ it.key }, { it.rowid }))
    val size get() = sorted.size

    var reads = 0   // entries looked at so far: our stand-in for page reads
        private set

    private fun at(i: Int): Entry {
        reads++
        return sorted[i]
    }

    // The first position whose key is >= key, or size if every key is smaller.
    fun lowerBound(key: Int): Int {
        var lo = 0
        var hi = size            // the answer is always in lo..hi
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (at(mid).key < key) lo = mid + 1 else hi = mid   // equal: the first one may be further left
        }
        return lo
    }

    // The rowids of every entry with this key, in index order.
    fun lookup(key: Int): List<Int> = range(key, key)

    // The rowids of every entry with lo <= key <= hi, in index order.
    fun range(lo: Int, hi: Int): List<Int> {
        val out = mutableListOf<Int>()
        var i = lowerBound(lo)   // descend once...
        while (i < size) {       // ...then walk right
            val e = at(i++)
            if (e.key > hi) break
            out += e.rowid
        }
        return out
    }
}
