import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    val words = Regex("\\W+").split("the cat sat on the mat the end")
        .groupingBy { it }.eachCount().toSortedMap()
    val date = LocalDate.of(2026, 10, 20).format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"))
    println("$words $date")
}
