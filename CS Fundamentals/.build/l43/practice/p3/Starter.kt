// One span of a trace: start and end are ms since the trace began.
data class Span(val id: String, val parent: String?, val service: String, val start: Long, val end: Long)

// Span id -> its duration minus the time covered by its children (overlaps counted once).
fun selfTimes(spans: List<Span>): Map<String, Long> = TODO("span id -> self time in ms")

// Where to look first: the service of the span with the largest self time.
fun culprit(spans: List<Span>): String = TODO("the service with the largest self time")
