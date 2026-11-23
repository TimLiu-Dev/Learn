import kotlin.math.ceil
import kotlin.math.log2

// log2(n!) = log2(1) + log2(2) + ... + log2(n), so it never overflows.
fun log2Factorial(n: Int): Double = (1..n).sumOf { log2(it.toDouble()) }

fun main() {
    println("        n   leaves n!   min compares ceil(log2 n!)   n log2 n")
    for (n in listOf(3, 4, 10, 1_000, 1_000_000)) {
        val leaves = if (n <= 10) "%,d".format((1..n).fold(1L) { a, k -> a * k }) else "huge"
        val minCompares = ceil(log2Factorial(n)).toLong()
        val nLogN = (n * log2(n.toDouble())).toLong()
        println("%,9d %12s %28s %10s".format(n, leaves, "%,d".format(minCompares), "%,d".format(nLogN)))
    }
}
