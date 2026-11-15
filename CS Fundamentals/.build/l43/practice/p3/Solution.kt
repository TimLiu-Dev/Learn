// One span of a trace: start and end are ms since the trace began.
data class Span(val id: String, val parent: String?, val service: String, val start: Long, val end: Long)

// Total length of some intervals, counting overlaps once: sort by start, then sweep.
fun covered(intervals: List<Pair<Long, Long>>): Long {
    var total = 0L
    var reach = Long.MIN_VALUE                       // the furthest end seen so far
    for ((start, end) in intervals.sortedBy { it.first }) {
        val from = maxOf(start, reach)               // skip the part already counted
        if (end > from) total += end - from
        reach = maxOf(reach, end)
    }
    return total
}

fun selfTimes(spans: List<Span>): Map<String, Long> {
    val children = spans.groupBy { it.parent }
    return spans.associate { s ->
        s.id to (s.end - s.start) - covered(children[s.id].orEmpty().map { it.start to it.end })
    }
}

fun culprit(spans: List<Span>): String {
    val self = selfTimes(spans)
    return spans.maxBy { self.getValue(it.id) }.service
}
