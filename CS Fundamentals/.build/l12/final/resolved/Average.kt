fun average(readings: List<Int>): Double {
    var sum = 0
    for (r in readings) sum += r
    return if (readings.isEmpty()) 0.0 else sum.toDouble() / readings.size
}

fun main() {
    val readings = listOf(72, 75, 79, 80)
    println("Average heart rate: ${average(readings)} bpm")
    println("Empty list: ${average(emptyList())} bpm")
}
