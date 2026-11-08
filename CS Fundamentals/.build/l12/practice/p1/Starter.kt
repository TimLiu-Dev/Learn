// The middle reading. For an even count, the lower of the two middle ones.
fun median(readings: MutableList<Int>): Int {
    val sorted = readings
    sorted.sort()
    return sorted[(sorted.size - 1) / 2]
}

// Readings arrive in the order they were taken.
fun summary(readings: MutableList<Int>): String {
    val mid = median(readings)
    return "first=${readings.first()} last=${readings.last()} median=$mid"
}
