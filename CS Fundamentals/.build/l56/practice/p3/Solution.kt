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
        if (mem.isEmpty()) return
        runs.add(0, mem)                    // the run keeps this map, so start a new one, don't clear it
        mem = TreeMap()
    }

    fun get(key: String): String? {
        if (key in mem) return mem[key]
        for (run in runs) {
            if (key in run) return run[key] // a tombstone (null) here hides every older run
        }
        return null
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
    val merged = TreeMap<String, String?>()
    for (run in runs.asReversed()) merged.putAll(run)     // oldest first, so newer versions overwrite
    if (dropTombstones) merged.values.removeIf { it == null }
    return merged
}
