data class Slot(val start: Int, val end: Int = start + 1) {
    init { require(start < end) { "start $start must be before end $end" } }
}

fun main() {
    val nine = Slot(9)
    val later = runCatching { nine.copy(start = 14) }
    println(later.getOrElse { "rejected: ${it.message}" })
}
