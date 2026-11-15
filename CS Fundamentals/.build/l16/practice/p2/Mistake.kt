import kotlin.math.log2

fun doublingRatios(count: (Int) -> Long, start: Int, rows: Int): List<Double> {
    val counts = List(rows) { i -> count(start shl i).toDouble() }
    return counts.zipWithNext { before, after -> after / before }
}

fun exponent(ratio: Double): Int = log2(ratio).toInt()     // truncates: 3.9 becomes 1
