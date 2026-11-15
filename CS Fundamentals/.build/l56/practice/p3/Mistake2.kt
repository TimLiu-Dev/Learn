import java.util.SortedMap
import java.util.TreeMap

class MiniLsm(private val memLimit: Int = 3) {
    private var mem = TreeMap<String, String?>()
    val runs = mutableListOf<SortedMap<String, String?>>()

    fun put(key: String, value: String) = write(key, value)
    fun delete(key: String) = write(key, null)

    private fun write(key: String, value: String?) {
        mem[key] = value
        if (mem.size >= memLimit) flush()
    }

    fun flush() {
        if (mem.isEmpty()) return
        runs.add(0, mem)
        mem = TreeMap()
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

// Merges newest first, so every older run overwrites the newer one.
fun mergeRuns(runs: List<SortedMap<String, String?>>, dropTombstones: Boolean): SortedMap<String, String?> {
    val merged = TreeMap<String, String?>()
    for (run in runs) merged.putAll(run)
    if (dropTombstones) merged.values.removeIf { it == null }
    return merged
}
