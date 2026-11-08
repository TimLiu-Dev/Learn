class Span<T>(val lo: T, val hi: T) {
    init {
        // TODO: reject lo > hi
    }

    operator fun contains(x: T): Boolean = TODO("lo <= x <= hi")

    fun clamp(x: T): T = TODO("lo if x is below, hi if x is above, else x")

    override fun toString() = "$lo..$hi"
}

fun <T> spanOf(items: List<T>): Span<T>? = TODO("null for an empty list, else smallest..largest")
