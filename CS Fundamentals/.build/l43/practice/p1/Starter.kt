// Each replica stamps its writes with its OWN counter, so two replicas can produce the same ts.
data class Versioned(val value: String, val ts: Long, val node: String)

// The winner of two versions of one key. Every replica must pick the same one,
// whichever order the two arrive in.
fun lww(a: Versioned, b: Versioned): Versioned = TODO("newer ts wins; on a tie, the larger node name wins")

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
        TODO("keep lww(mine, incoming), or incoming if there's nothing yet")
    }
}
