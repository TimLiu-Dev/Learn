class SeatTakenException(val seat: Int, val holder: String) :
    Exception("seat $seat is taken by $holder")

class Cinema(private val seats: Int) {
    var open = false
    private val booked = mutableMapOf<Int, String>()

    fun book(seat: Int, name: String) {
        require(seat in 1..seats) { "no seat $seat in a hall of $seats" }   // caller's bug
        check(open) { "bookings are closed" }                                // wrong state
        val holder = booked[seat]
        if (holder != null) throw SeatTakenException(seat, holder)           // expected, recoverable
        booked[seat] = name
    }

    fun holderOf(seat: Int): String? = booked[seat]
}

// The number in text, or seat 1 if text isn't a number.
fun seatOrDefault(text: String): Int =
    try {
        text.trim().toInt()
    } catch (e: NumberFormatException) {
        1
    }
