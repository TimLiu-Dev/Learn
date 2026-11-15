import kotlin.math.pow

// Uses p/100 * n as an index directly: one rank too high whenever it lands on a whole number.
fun percentile(ms: DoubleArray, p: Double): Double {
    val sorted = ms.sortedArray()
    val index = (p * sorted.size / 100).toInt()
    return sorted[minOf(index, sorted.size - 1)]
}

fun fanOutSlow(perCall: Double, calls: Int): Double = 1 - (1 - perCall).pow(calls)
