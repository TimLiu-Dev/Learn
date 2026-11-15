// Each replica stamps its writes with its OWN counter, so two replicas can produce the same ts.
data class Versioned(val value: String, val ts: Long, val node: String)

// A total order: timestamp first, node name to break ties. Both replicas compare
// the same two values the same way, so they always pick the same winner.
fun lww(a: Versioned, b: Versioned): Versioned = maxOf(a, b, compareBy({ it.ts }, { it.node }))

class Replica(val name: String) {
    private var counter = 0L
    val data = mutableMapOf<String, Versioned>()

    fun write(key: String, value: String): Versioned {
        val v = Versioned(value, ++counter, name)
        data[key] = v
        return v
    }

    // A version from the peer arrives: maybe late, maybe twice.
    fun receive(key: String, incoming: Versioned) {
        val mine = data[key]
        data[key] = if (mine == null) incoming else lww(mine, incoming)
    }
}
