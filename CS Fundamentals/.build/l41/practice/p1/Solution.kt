import kotlin.math.ceil
import kotlin.math.pow

// Nearest-rank percentile: the value at rank ceil(p/100 * n) of the sorted data (rank 1 = smallest).
fun percentile(ms: DoubleArray, p: Double): Double {
    val sorted = ms.sortedArray()                      // a sorted copy: the caller's array stays as it was
    val rank = ceil(p * sorted.size / 100).toInt()     // 1-based
    return sorted[maxOf(rank, 1) - 1]                  // rank 1 is index 0
}

// Probability that at least one of `calls` independent calls is slow.
fun fanOutSlow(perCall: Double, calls: Int): Double = 1 - (1 - perCall).pow(calls)
