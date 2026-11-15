data class Session(val name: String, val start: Int, val end: Int)   // half-open [start, end)

// The most sessions that don't overlap, as names in time order.
fun schedule(sessions: List<Session>): List<String> =
    TODO("sort by end time, then take each session that starts at or after the last end you took")
