import java.time.*
import java.time.format.DateTimeFormatter

sealed interface Slot
data class Exact(val at: ZonedDateTime) : Slot
data class Skipped(val requested: LocalDateTime, val movedTo: ZonedDateTime) : Slot   // spring-forward gap
data class Twice(val earlier: ZonedDateTime, val later: ZonedDateTime) : Slot        // fall-back overlap

fun slot(local: LocalDateTime, zone: ZoneId): Slot {
    val at = ZonedDateTime.of(local, zone)             // never throws: a gap is shifted, an overlap takes the earlier offset
    return when (zone.rules.getValidOffsets(local).size) {
        0 -> Skipped(local, at)                        // this wall-clock time never happens
        2 -> Twice(at.withEarlierOffsetAtOverlap(), at.withLaterOffsetAtOverlap())
        else -> Exact(at)
    }
}

// e.g. 2026-07-01T09:30:00-04:00. toString() would drop the ":00" seconds.
fun rfc3339(t: Instant, zone: ZoneId): String =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(t.atZone(zone))
