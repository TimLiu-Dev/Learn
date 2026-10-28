import java.time.LocalDate

// Leitner boxes: a sheet you know well moves to a box you review less often.
val INTERVAL_DAYS = listOf(1, 3, 7, 14, 30)            // box 1..5

enum class Rating { SOLID, NEEDED_DOCS, STUCK }

data class Sheet(val name: String, val box: Int, val due: LocalDate)

fun review(sheet: Sheet, rating: Rating, today: LocalDate): Sheet {
    val box = when (rating) {
        Rating.SOLID -> minOf(sheet.box + 1, INTERVAL_DAYS.size)
        Rating.NEEDED_DOCS -> sheet.box
        Rating.STUCK -> 1
    }
    return sheet.copy(box = box, due = today.plusDays(INTERVAL_DAYS[box - 1].toLong()))
}

fun main() {
    val today = LocalDate.of(2026, 11, 2)
    val ratings = mapOf(
        "kotlin-java-basics" to Rating.SOLID, "memory-and-equality" to Rating.NEEDED_DOCS,
        "generics-and-functions" to Rating.STUCK, "concurrency" to Rating.STUCK,
        "databases" to Rating.SOLID, "system-design" to Rating.NEEDED_DOCS,
    )
    val sheets = ratings.keys.map { Sheet(it, box = 2, due = today) }
        .map { review(it, ratings.getValue(it.name), today) }
        .sortedWith(compareBy({ it.due }, { it.name }))
    sheets.forEach { println("${it.due}  box ${it.box}  ${it.name}") }
    println("due within a week: " + sheets.count { !it.due.isAfter(today.plusDays(7)) })
}
