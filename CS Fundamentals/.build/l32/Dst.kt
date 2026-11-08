import java.time.*

fun main() {
    val ny = ZoneId.of("America/New_York")

    // Gap: on 2026-03-08 New York clocks jump from 02:00 to 03:00
    val gap = LocalDateTime.of(2026, 3, 8, 2, 30)
    println("gap     $gap -> ${ZonedDateTime.of(gap, ny)}")
    println("offsets ${ny.rules.getValidOffsets(gap)}")       // none: 02:30 never happens

    // Overlap: on 2026-11-01 clocks fall back from 02:00 to 01:00, so 01:30 happens twice
    val overlap = LocalDateTime.of(2026, 11, 1, 1, 30)
    println("offsets ${ny.rules.getValidOffsets(overlap)}")   // two: 01:30 happens twice
    val first = ZonedDateTime.of(overlap, ny)
    val second = first.withLaterOffsetAtOverlap()
    println("first   $first")
    println("second  $second")
    println("between ${Duration.between(first, second)}")

    // "Tomorrow, same time" is not "24 hours later" across a DST change
    val sat = ZonedDateTime.of(LocalDateTime.of(2026, 10, 31, 9, 0), ny)
    println("+1 day  ${sat.plusDays(1)}")
    println("+24 h   ${sat.plusHours(24)}")
}
