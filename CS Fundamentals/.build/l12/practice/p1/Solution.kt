// The middle reading. For an even count, the lower of the two middle ones.
fun median(readings: List<Int>): Int {
    val sorted = readings.sorted()   // a new, sorted list; the caller's list is untouched
    return sorted[(sorted.size - 1) / 2]
}

// Readings arrive in the order they were taken.
fun summary(readings: List<Int>): String {
    val mid = median(readings)
    return "first=${readings.first()} last=${readings.last()} median=$mid"
}
