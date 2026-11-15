import kotlin.math.ceil
import kotlin.math.pow

// Nearest-rank percentile: the value at rank ceil(p/100 * n) of the sorted data (rank 1 = smallest).
fun percentile(ms: DoubleArray, p: Double): Double {
    TODO("sort a copy, compute the rank, and remember that arrays start at index 0")
}

// Probability that at least one of `calls` independent calls is slow.
fun fanOutSlow(perCall: Double, calls: Int): Double {
    TODO("start from the chance that every call is fast")
}
