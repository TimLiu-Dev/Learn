class SeatTakenException(val seat: Int, val holder: String) :
    Exception("seat $seat is taken by $holder")

class Cinema(private val seats: Int) {
    var open = false
    private val booked = mutableMapOf<Int, String>()

    fun book(seat: Int, name: String) {
        TODO("require a valid seat, check the cinema is open, refuse a taken seat, then book it")
    }

    fun holderOf(seat: Int): String? = booked[seat]
}

// The number in text, or seat 1 if text isn't a number.
fun seatOrDefault(text: String): Int = TODO("try { … } catch (…) { … } as one expression")
