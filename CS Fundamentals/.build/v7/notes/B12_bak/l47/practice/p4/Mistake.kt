import java.time.LocalDate

data class Sheet(val name: String, val due: LocalDate)

fun plan(sheets: List<Sheet>, today: LocalDate, perDay: Int): Map<LocalDate, List<String>> {
    require(perDay >= 1) { "perDay must be at least 1, was $perDay" }
    val days = sortedMapOf<LocalDate, MutableList<String>>()
    for (sheet in sheets.sortedBy { it.name }) {   // alphabetical only: the most overdue sheet can be pushed back
        var day = maxOf(sheet.due, today)
        while (days[day].orEmpty().size >= perDay) day = day.plusDays(1)
        days.getOrPut(day) { mutableListOf() } += sheet.name
    }
    return days
}
