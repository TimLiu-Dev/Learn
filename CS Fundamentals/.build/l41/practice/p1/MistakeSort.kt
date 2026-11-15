import kotlin.math.ceil
import kotlin.math.pow

// Sorts the caller's array in place: right answer, but the caller's data is reordered behind its back.
fun percentile(ms: DoubleArray, p: Double): Double {
    ms.sort()
    val rank = ceil(p * ms.size / 100).toInt()
    return ms[maxOf(rank, 1) - 1]
}

fun fanOutSlow(perCall: Double, calls: Int): Double = 1 - (1 - perCall).pow(calls)
