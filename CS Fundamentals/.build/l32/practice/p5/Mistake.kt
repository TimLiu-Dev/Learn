import java.time.*
import java.time.format.DateTimeFormatter

sealed interface Slot
data class Exact(val at: ZonedDateTime) : Slot
data class Skipped(val requested: LocalDateTime, val movedTo: ZonedDateTime) : Slot
data class Twice(val earlier: ZonedDateTime, val later: ZonedDateTime) : Slot

// Expects java.time to throw for a time that doesn't exist
fun slot(local: LocalDateTime, zone: ZoneId): Slot = try {
    val at = ZonedDateTime.of(local, zone)
    if (zone.rules.getValidOffsets(local).size == 2) Twice(at.withEarlierOffsetAtOverlap(), at.withLaterOffsetAtOverlap())
    else Exact(at)
} catch (e: DateTimeException) {
    Skipped(local, ZonedDateTime.of(local.plusHours(1), zone))
}

fun rfc3339(t: Instant, zone: ZoneId): String =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(t.atZone(zone))
