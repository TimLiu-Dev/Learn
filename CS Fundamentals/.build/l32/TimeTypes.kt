import java.time.*
import java.time.zone.ZoneRulesProvider

fun main() {
    val ny = ZoneId.of("America/New_York")
    val instant = Instant.parse("2026-07-01T13:30:00Z")        // one moment on the timeline
    val zoned = instant.atZone(ny)                             // that moment, as New York sees it
    println("Instant        $instant")
    println("ZonedDateTime  $zoned")
    println("OffsetDateTime ${zoned.toOffsetDateTime()}")
    println("LocalDateTime  ${zoned.toLocalDateTime()}   <- no zone: not a moment")
    println("LocalDate      ${zoned.toLocalDate()}")
    println("Tokyo, same    ${instant.atZone(ZoneId.of("Asia/Tokyo"))}")
    println("tzdb version   ${ZoneRulesProvider.getVersions("America/New_York").lastKey()}")
}
