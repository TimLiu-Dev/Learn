import java.util.SortedMap
import java.util.TreeMap

class MiniLsm(private val memLimit: Int = 3) {
    private val mem = TreeMap<String, String?>()
    val runs = mutableListOf<SortedMap<String, String?>>()

    fun put(key: String, value: String) = write(key, value)
    fun delete(key: String) = write(key, null)

    private fun write(key: String, value: String?) {
        mem[key] = value
        if (mem.size >= memLimit) flush()
    }

    fun flush() {
        runs.add(0, mem)                    // the same map object as the memtable...
        mem.clear()                         // ...so this empties the run too
    }

    fun get(key: String): String? {
        if (key in mem) return mem[key]
        for (run in runs) if (key in run) return run[key]
        return null
    }

    fun compact() {
        flush()
        val merged = mergeRuns(runs, dropTombstones = true)
        runs.clear()
        runs += merged
    }
}

fun mergeRuns(runs: List<SortedMap<String, String?>>, dropTombstones: Boolean): SortedMap<String, String?> {
    val merged = TreeMap<String, String?>()
    for (run in runs.asReversed()) merged.putAll(run)
    if (dropTombstones) merged.values.removeIf { it == null }
    return merged
}
