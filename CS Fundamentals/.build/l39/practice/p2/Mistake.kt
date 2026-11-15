data class Booking(
    val room: String,
    val start: Int,
    val end: Int = start + 1,
    val people: Int = 1,
) {
    companion object {
        // Builder habit: validate in the factory. The constructor and copy() skip it.
        fun of(room: String, start: Int, end: Int = start + 1, people: Int = 1): Booking {
            require(room.isNotBlank()) { "room is required" }
            require(start >= 8 && end <= 18) { "outside opening hours: $start-$end" }
            require(start < end) { "start $start must be before end $end" }
            require(people >= 1) { "people must be at least 1, was $people" }
            return Booking(room, start, end, people)
        }

        fun allDay(room: String) = of(room, 8, 18)

        fun parse(text: String): Booking {
            val parts = text.trim().split(" ")
            val (from, to) = parts[1].split("-").map(String::toInt)
            val people = parts.getOrNull(2)?.removePrefix("x")?.toInt() ?: 1
            return of(parts[0], from, to, people)
        }
    }
}
