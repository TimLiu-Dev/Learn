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

    // A null from the map means "absent" here, so a tombstone falls through to older runs.
    fun get(key: String): String? = mem[key] ?: runs.firstNotNullOfOrNull { it[key] }

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
