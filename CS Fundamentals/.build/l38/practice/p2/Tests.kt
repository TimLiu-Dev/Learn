import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

// A hand-written fake clock: time moves only when a test says so.
class FakeClock(private var now: Instant) : Clock() {
    override fun instant(): Instant = now
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId): Clock = this
    fun advance(by: Duration) { now = now.plus(by) }
}

class HoldShelfTest {
    private val start = Instant.parse("2026-10-21T10:00:00Z")
    private val clock = FakeClock(start)
    private var n = 0
    private val shelf = HoldShelf(clock) { "H${++n}" }     // ids H1, H2, …

    @Test fun `a hold records the injected time and id`() {
        val hold = shelf.place(7, "ada")
        assertEquals("id", "H1", hold.id)
        assertEquals("placedAtMillis should be the fake clock's time", start.toEpochMilli(), hold.placedAtMillis)
    }

    @Test fun `a hold still waits one millisecond before 3 days`() {
        shelf.place(7, "ada")
        clock.advance(Duration.ofDays(3).minusMillis(1))
        assertEquals(listOf("ada"), shelf.waiting().map { it.member })
    }

    @Test fun `a hold expires at exactly 3 days`() {
        shelf.place(7, "ada")
        clock.advance(Duration.ofDays(1))
        shelf.place(8, "alan")
        clock.advance(Duration.ofDays(2))
        assertEquals("ada's hold is 3 days old now; alan's is 2", listOf("alan"), shelf.waiting().map { it.member })
    }

    @Test fun `picking up in time removes the hold`() {
        val hold = shelf.place(7, "ada")
        clock.advance(Duration.ofDays(1))
        assertTrue(shelf.pickUp(hold.id))
        assertEquals(emptyList<Hold>(), shelf.waiting())
        assertFalse("the same hold can't be picked up twice", shelf.pickUp(hold.id))
    }

    @Test fun `an expired hold cannot be picked up`() {
        val hold = shelf.place(7, "ada")
        clock.advance(Duration.ofDays(4))
        assertFalse(shelf.pickUp(hold.id))
        assertFalse("unknown id", shelf.pickUp("H99"))
    }
}
