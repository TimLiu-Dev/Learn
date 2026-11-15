// Each replica stamps its writes with its OWN counter, so two replicas can produce the same ts.
data class Versioned(val value: String, val ts: Long, val node: String)

// The lesson's rule: only a strictly newer version replaces mine. On a tie, each side keeps its own.
fun lww(a: Versioned, b: Versioned): Versioned = if (b.ts > a.ts) b else a

class Replica(val name: String) {
    private var counter = 0L
    val data = mutableMapOf<String, Versioned>()

    fun write(key: String, value: String): Versioned {
        val v = Versioned(value, ++counter, name)
        data[key] = v
        return v
    }

    fun receive(key: String, incoming: Versioned) {
        val mine = data[key]
        data[key] = if (mine == null) incoming else lww(mine, incoming)
    }
}
