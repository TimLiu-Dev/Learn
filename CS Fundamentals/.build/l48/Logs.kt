import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2

// How many times can you halve n before reaching 1?
fun halvings(n: Int): Int {
    var x = n
    var count = 0
    while (x > 1) { x /= 2; count++ }
    return count
}

fun main() {
    println("n              halvings  31-leadingZeros  floor(log2)")
    for (n in listOf(1, 8, 1000, 1_000_000, 1_000_000_000, Int.MAX_VALUE)) {
        println("%-14d %8d  %15d  %11.0f".format(n, halvings(n), 31 - n.countLeadingZeroBits(), floor(log2(n.toDouble()))))
    }
    // The floating-point trap from L2: change of base is not exact
    println("ln(1000)/ln(10) = ${ln(1000.0) / ln(10.0)}")
}
