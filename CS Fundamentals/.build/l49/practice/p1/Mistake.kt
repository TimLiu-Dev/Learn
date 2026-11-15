data class Session(val name: String, val start: Int, val end: Int)   // half-open [start, end)

// Earliest START first: looks just as reasonable, and is wrong.
fun schedule(sessions: List<Session>): List<String> {
    val taken = mutableListOf<String>()
    var lastEnd = Int.MIN_VALUE
    for (s in sessions.sortedBy { it.start }) {
        if (s.start >= lastEnd) {
            taken += s.name
            lastEnd = s.end
        }
    }
    return taken
}
