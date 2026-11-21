val INTERVAL_DAYS = listOf(1, 3, 7, 14, 30)   // box 1..5

fun nextBox(box: Int, solid: Boolean) = if (solid) minOf(box + 1, INTERVAL_DAYS.size) else 1

fun main() {
    var box = 4
    val gaps = mutableListOf<Int>()
    repeat(3) {
        box = nextBox(box, solid = true)
        gaps += INTERVAL_DAYS[box - 1]
    }
    println(gaps)
}
