data class Booking(
    val room: String,
    val start: Int,                  // hour of the day
    val end: Int = start + 1,
    val people: Int = 1,
) {
    // The one place every path goes through: constructor, copy(), allDay, parse
    init {
        require(room.isNotBlank()) { "room is required" }
        require(start >= 8 && end <= 18) { "outside opening hours: $start-$end" }
        require(start < end) { "start $start must be before end $end" }
        require(people >= 1) { "people must be at least 1, was $people" }
    }

    companion object {
        fun allDay(room: String) = Booking(room, start = 8, end = 18)

        fun parse(text: String): Booking {
            val parts = text.trim().split(" ")
            // "nine".toInt() throws NumberFormatException, a subclass of IllegalArgumentException
            val (from, to) = parts[1].split("-").map(String::toInt)
            val people = parts.getOrNull(2)?.removePrefix("x")?.toInt() ?: 1
            return Booking(parts[0], from, to, people)          // the rules run in init, as for everyone
        }
    }
}
