data class Session(val name: String, val start: Int, val end: Int)   // half-open [start, end)

// The most sessions that don't overlap, as names in time order.
fun schedule(sessions: List<Session>): List<String> {
    val taken = mutableListOf<String>()
    var lastEnd = Int.MIN_VALUE
    for (s in sessions.sortedBy { it.end }) {      // earliest finish first
        if (s.start >= lastEnd) {                  // >=: starting as the last one ends is fine
            taken += s.name
            lastEnd = s.end                        // the only thing a later session can clash with
        }
    }
    return taken
}
