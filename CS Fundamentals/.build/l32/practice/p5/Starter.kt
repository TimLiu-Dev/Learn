import java.time.*

sealed interface Slot
data class Exact(val at: ZonedDateTime) : Slot
data class Skipped(val requested: LocalDateTime, val movedTo: ZonedDateTime) : Slot   // spring-forward gap
data class Twice(val earlier: ZonedDateTime, val later: ZonedDateTime) : Slot        // fall-back overlap

fun slot(local: LocalDateTime, zone: ZoneId): Slot = TODO("ask the zone's rules how many offsets this local time has")

// e.g. 2026-07-01T09:30:00-04:00
fun rfc3339(t: Instant, zone: ZoneId): String = t.atZone(zone).toOffsetDateTime().toString()
