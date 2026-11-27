import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RetirementHeadersTest {
    private val docs = "https://shelf.example.com/docs/changes"

    @Test fun `Shelf's own dates give Exercise 5's headers`() {
        val h = retirementHeaders(Instant.parse("2027-01-01T00:00:00Z"), Instant.parse("2028-01-01T00:00:00Z"), docs)
        assertEquals("@1798761600", h["Deprecation"])
        assertEquals("Sat, 01 Jan 2028 00:00:00 GMT", h["Sunset"])
        assertEquals("<https://shelf.example.com/docs/changes>; rel=\"deprecation\"", h["Link"])
    }

    @Test fun `an early-month day still has two digits`() {
        val h = retirementHeaders(Instant.parse("2027-11-09T14:30:00Z"), Instant.parse("2028-03-05T09:07:03Z"), docs)
        assertEquals("@1825770600", h["Deprecation"])
        assertEquals("IMF-fixdate needs a 2-digit day", "Sun, 05 Mar 2028 09:07:03 GMT", h["Sunset"])
    }

    @Test fun `only the three headers are sent`() {
        val at = Instant.parse("2027-01-01T00:00:00Z")
        assertEquals(setOf("Deprecation", "Sunset", "Link"), retirementHeaders(at, at, docs).keys)
    }

    @Test fun `a sunset before the deprecation is refused`() {
        assertThrows(IllegalArgumentException::class.java) {
            retirementHeaders(Instant.parse("2028-01-01T00:00:00Z"), Instant.parse("2027-12-31T23:59:59Z"), docs)
        }
    }
}
