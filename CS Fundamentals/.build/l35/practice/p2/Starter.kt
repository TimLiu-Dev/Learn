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
    fun lowerBound(key: Int): Int = TODO("binary search on at(mid).key; don't stop at the first equal key")

    // The rowids of every entry with this key, in index order.
    fun lookup(key: Int): List<Int> = TODO("start at lowerBound(key), walk right while the key matches")

    // The rowids of every entry with lo <= key <= hi, in index order.
    fun range(lo: Int, hi: Int): List<Int> = TODO("same walk, a different stop condition")
}
