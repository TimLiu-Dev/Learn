fun average(readings: List<Int>): Double {
    var sum = 0
    for (r in readings) sum += r
    return (sum / readings.size).toDouble()
}

fun main() {
    val readings = listOf(72, 75, 79, 80)
    println("Average heart rate: ${average(readings)} bpm")
}
