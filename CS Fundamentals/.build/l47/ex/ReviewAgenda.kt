import java.time.LocalDate

// Exercise solution: the same Leitner rules as Review.kt, printed as a day-by-day agenda.
val INTERVALS = listOf(1, 3, 7, 14, 30)

enum class Rating { SOLID, NEEDED_DOCS, STUCK }

fun nextBox(box: Int, r: Rating) = when (r) {
    Rating.SOLID -> minOf(box + 1, INTERVALS.size)
    Rating.NEEDED_DOCS -> box
    Rating.STUCK -> 1
}

fun main() {
    val today = LocalDate.of(2026, 11, 2)
    val ratings = mapOf("concurrency" to Rating.STUCK, "databases" to Rating.SOLID,
                        "system-design" to Rating.NEEDED_DOCS, "networks-and-web" to Rating.SOLID)
    val due = ratings.mapValues { (_, r) -> nextBox(2, r).let { today.plusDays(INTERVALS[it - 1].toLong()) } }
    due.entries.groupBy({ it.value }, { it.key }).toSortedMap()
        .forEach { (day, sheets) -> println("${day.dayOfWeek.name.take(3)} $day: ${sheets.sorted().joinToString()}") }
}
