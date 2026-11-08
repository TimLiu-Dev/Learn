import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CinemaTest {
    private fun openCinema(): Cinema {
        val c = Cinema(10)
        c.open = true
        return c
    }

    @Test fun `a free seat can be booked`() {
        val c = openCinema()
        c.book(5, "Ada")
        assertEquals("Ada", c.holderOf(5))
    }

    @Test fun `a seat outside the hall is the caller's bug`() {
        assertThrows(IllegalArgumentException::class.java) { openCinema().book(11, "Ada") }
    }

    @Test fun `booking while closed is a state problem`() {
        assertThrows(IllegalStateException::class.java) { Cinema(10).book(5, "Ada") }
    }

    @Test fun `a bad seat is reported before the closed state`() {
        assertThrows(IllegalArgumentException::class.java) { Cinema(10).book(0, "Ada") }
    }

    @Test fun `a taken seat names its holder`() {
        val c = openCinema()
        c.book(5, "Ada")
        val e = assertThrows(SeatTakenException::class.java) { c.book(5, "Bob") }
        assertEquals("seat", 5, e.seat)
        assertEquals("holder", "Ada", e.holder)
        assertEquals("Ada keeps the seat", "Ada", c.holderOf(5))
    }

    @Test fun `seatOrDefault falls back to seat 1`() {
        assertEquals(7, seatOrDefault(" 7 "))
        assertEquals(1, seatOrDefault("seven"))
    }
}
