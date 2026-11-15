import kotlin.math.log2
import kotlin.math.roundToInt

// count(n) = how many steps some code takes at size n.
fun doublingRatios(count: (Int) -> Long, start: Int, rows: Int): List<Double> =
    TODO("count each size once, then divide each count by the one before it")

// T(2n) / T(n) ≈ 2^b, so b ≈ log2(ratio).
fun exponent(ratio: Double): Int = TODO("log2, then round to the nearest whole number")
