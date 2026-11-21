import java.time.LocalDate

data class Sheet(val name: String, val due: LocalDate)

/** Spreads reviews over days from [today]: at most [perDay] a day, most overdue first. */
fun plan(sheets: List<Sheet>, today: LocalDate, perDay: Int): Map<LocalDate, List<String>> {
    require(perDay >= 1) { "perDay must be at least 1, was $perDay" }
    val days = sortedMapOf<LocalDate, MutableList<String>>()
    for (sheet in sheets.sortedWith(compareBy({ it.due }, { it.name }))) {
        var day = maxOf(sheet.due, today)
        while (days[day].orEmpty().size >= perDay) day = day.plusDays(1)   // full: try the next day
        days.getOrPut(day) { mutableListOf() } += sheet.name
    }
    return days
}
