data class Booking(
    val room: String,
    val start: Int,                  // hour of the day
    val end: Int = start + 1,
    val people: Int = 1,
) {
    init {
        TODO("reject a blank room, hours outside 8..18, start >= end, and nobody attending")
    }

    companion object {
        fun allDay(room: String): Booking = TODO("8 to 18")

        fun parse(text: String): Booking =
            TODO("\"Ada 9-11 x4\" -> Booking(\"Ada\", 9, 11, 4); without \"x4\", one person")
    }
}
