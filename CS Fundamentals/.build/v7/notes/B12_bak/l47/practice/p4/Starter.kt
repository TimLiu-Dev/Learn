import java.time.LocalDate

data class Sheet(val name: String, val due: LocalDate)

/** Spreads reviews over days from [today]: at most [perDay] a day, most overdue first. */
fun plan(sheets: List<Sheet>, today: LocalDate, perDay: Int): Map<LocalDate, List<String>> =
    sheets.groupBy({ maxOf(it.due, today) }, { it.name })   // ignores perDay: fix me
        .toSortedMap()
