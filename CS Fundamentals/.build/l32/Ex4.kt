import java.time.*

// Due = same local time, 14 calendar days later, in the library's zone
fun dueAt(borrowed: Instant, zone: ZoneId, days: Long = 14): ZonedDateTime =
    borrowed.atZone(zone).plusDays(days)

fun main() {
    val ny = ZoneId.of("America/New_York")
    val borrowed = Instant.parse("2026-10-25T14:00:00Z")          // 10:00 in New York
    val due = dueAt(borrowed, ny)
    println("borrowed ${borrowed.atZone(ny)}")
    println("due      $due")
    println("due UTC  ${due.toInstant()}")
    println("hours    ${Duration.between(borrowed, due.toInstant()).toHours()}")   // 14 x 24 = 336?
    println("naive    ${borrowed.plus(Duration.ofDays(14)).atZone(ny)}")          // 24-hour days
}
