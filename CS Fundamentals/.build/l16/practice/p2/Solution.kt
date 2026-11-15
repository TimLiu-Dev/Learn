import kotlin.math.log2
import kotlin.math.roundToInt

// count(n) = how many steps some code takes at size n.
fun doublingRatios(count: (Int) -> Long, start: Int, rows: Int): List<Double> {
    val counts = List(rows) { i -> count(start shl i).toDouble() }   // start shl i = start * 2^i
    return counts.zipWithNext { before, after -> after / before }
}

// T(2n) / T(n) ≈ 2^b, so b ≈ log2(ratio). Round, don't truncate: 3.9 means b = 2.
fun exponent(ratio: Double): Int = log2(ratio).roundToInt()
