// One span of a trace: start and end are ms since the trace began.
data class Span(val id: String, val parent: String?, val service: String, val start: Long, val end: Long)

// The Part 3 demo's shortcut: subtract the SUM of the children's durations.
fun selfTimes(spans: List<Span>): Map<String, Long> =
    spans.associate { s -> s.id to (s.end - s.start) - spans.filter { it.parent == s.id }.sumOf { it.end - it.start } }

fun culprit(spans: List<Span>): String {
    val self = selfTimes(spans)
    return spans.maxBy { self.getValue(it.id) }.service
}
