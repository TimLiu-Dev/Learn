/**
 * Shortens this string to at most [max] characters, ending in "…" if anything was cut.
 * Never throws: a [max] of 0 or less gives "".
 */
fun String.ellipsize(max: Int): String = when {
    max <= 0 -> ""
    length <= max -> this
    else -> take(max - 1) + "…"
}

fun main() {
    val title = "The Left Hand of Darkness"
    for (max in listOf(40, 25, 12, 1, 0, -3)) println("max=$max -> [${title.ellipsize(max)}]")
}
