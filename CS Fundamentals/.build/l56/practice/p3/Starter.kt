import java.util.SortedMap
import java.util.TreeMap

// Part 3's LSM tree, with the SSTables kept in memory. A null value is a tombstone.
class MiniLsm(private val memLimit: Int = 3) {
    private var mem = TreeMap<String, String?>()
    val runs = mutableListOf<SortedMap<String, String?>>()      // newest first

    fun put(key: String, value: String) = write(key, value)
    fun delete(key: String) = write(key, null)

    private fun write(key: String, value: String?) {
        mem[key] = value
        if (mem.size >= memLimit) flush()
    }

    fun flush() {
        TODO("the memtable becomes the newest run; then start a new, empty memtable")
    }

    fun get(key: String): String? {
        TODO("memtable first, then runs newest first; stop at the first one that contains the key")
    }

    fun compact() {
        flush()
        val merged = mergeRuns(runs, dropTombstones = true)
        runs.clear()
        runs += merged
    }
}

// runs[0] is the newest. Returns one sorted run holding the newest version of every key.
fun mergeRuns(runs: List<SortedMap<String, String?>>, dropTombstones: Boolean): SortedMap<String, String?> {
    TODO("newest version wins; drop tombstones only when asked")
}
