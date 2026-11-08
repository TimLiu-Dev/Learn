import java.time.LocalDate

fun <T : Comparable<T>> latest(items: List<T>): T =
    items.reduce { best, x -> if (x > best) x else best }

fun main() {
    val dates = listOf(LocalDate.of(2024, 3, 1), LocalDate.of(2025, 1, 9), LocalDate.of(2023, 7, 4))
    println(latest(dates))
}
