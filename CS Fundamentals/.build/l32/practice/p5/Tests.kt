import java.time.*
import org.junit.Assert.assertEquals
import org.junit.Test

val NY: ZoneId = ZoneId.of("America/New_York")
fun ny(text: String): ZonedDateTime = ZonedDateTime.parse(text + "[America/New_York]")

class SlotTest {
    @Test fun `an ordinary time is exact`() {
        assertEquals(Exact(ny("2026-07-01T09:30-04:00")), slot(LocalDateTime.of(2026, 7, 1, 9, 30), NY))
    }

    @Test fun `02 30 on the spring-forward night is skipped`() {
        val requested = LocalDateTime.of(2026, 3, 8, 2, 30)
        assertEquals(Skipped(requested, ny("2026-03-08T03:30-04:00")), slot(requested, NY))
    }

    @Test fun `01 30 on the fall-back night happens twice, an hour apart`() {
        val s = slot(LocalDateTime.of(2026, 11, 1, 1, 30), NY)
        assertEquals(Twice(ny("2026-11-01T01:30-04:00"), ny("2026-11-01T01:30-05:00")), s)
    }

    @Test fun `Paris has its own gap, on another date`() {
        val paris = ZoneId.of("Europe/Paris")
        val requested = LocalDateTime.of(2026, 3, 29, 2, 15)
        assertEquals(Skipped(requested, ZonedDateTime.of(LocalDateTime.of(2026, 3, 29, 3, 15), paris)), slot(requested, paris))
        assertEquals("New York's gap date is an ordinary night in Paris",
            Exact(ZonedDateTime.of(LocalDateTime.of(2026, 3, 8, 2, 30), paris)), slot(LocalDateTime.of(2026, 3, 8, 2, 30), paris))
    }

    @Test fun `rfc3339 keeps the seconds and the offset`() {
        assertEquals("2026-07-01T09:30:00-04:00", rfc3339(Instant.parse("2026-07-01T13:30:00Z"), NY))
    }

    @Test fun `rfc3339 writes Z for UTC`() {
        assertEquals("2026-11-01T06:30:00Z", rfc3339(Instant.parse("2026-11-01T06:30:00Z"), ZoneOffset.UTC))
    }
}
