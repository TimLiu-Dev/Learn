// The bound promises that every T can be compared with another T.
class Span<T : Comparable<T>>(val lo: T, val hi: T) {
    init {
        require(lo <= hi) { "lo ($lo) must not be greater than hi ($hi)" }
    }

    operator fun contains(x: T): Boolean = lo <= x && x <= hi

    fun clamp(x: T): T = when {
        x < lo -> lo
        x > hi -> hi
        else -> x
    }

    override fun toString() = "$lo..$hi"
}

fun <T : Comparable<T>> spanOf(items: List<T>): Span<T>? {
    if (items.isEmpty()) return null
    var lo = items[0]
    var hi = items[0]
    for (item in items) {
        if (item < lo) lo = item
        if (item > hi) hi = item
    }
    return Span(lo, hi)
}
