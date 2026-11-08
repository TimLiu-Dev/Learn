class SeatTakenException(val seat: Int, val holder: String) :
    Exception("seat $seat is taken by $holder")

class Cinema(private val seats: Int) {
    var open = false
    private val booked = mutableMapOf<Int, String>()

    fun book(seat: Int, name: String) {
        require(seat in 1..seats) { "no seat $seat in a hall of $seats" }
        require(open) { "bookings are closed" }
        val holder = booked[seat]
        if (holder != null) throw SeatTakenException(seat, holder)
        booked[seat] = name
    }

    fun holderOf(seat: Int): String? = booked[seat]
}

fun seatOrDefault(text: String): Int =
    try {
        text.trim().toInt()
    } catch (e: NumberFormatException) {
        1
    }
