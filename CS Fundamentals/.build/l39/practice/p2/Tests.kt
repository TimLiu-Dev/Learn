import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BookingTest {
    private fun rejected(what: String, make: () -> Booking) {
        assertThrows("$what should be rejected", IllegalArgumentException::class.java) { make() }
    }

    @Test fun `defaults fill in the optional fields`() {
        assertEquals(Booking("Ada", 9, 10, 1), Booking("Ada", 9))
        assertEquals(Booking("Ada", 9, 12, 1), Booking("Ada", 9, end = 12))
    }

    @Test fun `the constructor rejects every broken rule`() {
        rejected("a blank room") { Booking(" ", 9) }
        rejected("a start before 8") { Booking("Ada", 7) }
        rejected("an end after 18") { Booking("Ada", 17, 19) }
        rejected("an empty slot") { Booking("Ada", 10, 10) }
        rejected("an end before the start") { Booking("Ada", 12, 10) }
        rejected("nobody attending") { Booking("Ada", 9, people = 0) }
    }

    @Test fun `copy is checked too`() {
        val b = Booking("Ada", 10, 12)
        assertEquals(Booking("Ada", 10, 11), b.copy(end = 11))
        rejected("copy(end = 9)") { b.copy(end = 9) }
        rejected("copy(people = 0)") { b.copy(people = 0) }
    }

    @Test fun `allDay books the whole opening day`() {
        assertEquals(Booking("Turing", 8, 18), Booking.allDay("Turing"))
        rejected("allDay with a blank room") { Booking.allDay("") }
    }

    @Test fun `parse reads hours and an optional head count`() {
        assertEquals(Booking("Ada", 9, 11, 4), Booking.parse("Ada 9-11 x4"))
        assertEquals(Booking("Turing", 14, 15, 1), Booking.parse("Turing 14-15"))
    }

    @Test fun `parse goes through the same rules`() {
        rejected("parse(\"Ada 11-9\")") { Booking.parse("Ada 11-9") }
        rejected("parse(\"Ada 9-11 x0\")") { Booking.parse("Ada 9-11 x0") }
        rejected("parse(\"Ada nine-11\")") { Booking.parse("Ada nine-11") }
    }
}
