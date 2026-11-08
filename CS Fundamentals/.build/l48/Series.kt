// Count the element copies a growing array makes while n items are appended.
fun copies(n: Int, grow: (Int) -> Int): Long {
    var capacity = 1
    var total = 0L
    for (size in 0 until n) {
        if (size == capacity) { total += size; capacity = grow(capacity) }   // copy everything across
    }
    return total
}

fun main() {
    // Arithmetic series: the inner loop runs 0 + 1 + ... + (n-1) times
    val n = 1000
    var pairs = 0L
    for (i in 0 until n) for (j in 0 until i) pairs++
    println("pairs i>j for n=$n: $pairs   formula n(n-1)/2 = ${n.toLong() * (n - 1) / 2}")

    // Geometric series: 1 + 2 + 4 + ... + 2^k = 2^(k+1) - 1
    println("1+2+4+...+512 = ${(0..9).sumOf { 1 shl it }}   formula 2^10 - 1 = ${(1 shl 10) - 1}")

    println("n          doubling copies  per item   +10 copies    per item")
    for (items in listOf(1_000, 10_000, 100_000, 1_000_000)) {
        val d = copies(items) { it * 2 }
        val a = copies(items) { it + 10 }
        println("%-10d %15d  %8.2f  %12d  %10.1f".format(items, d, d.toDouble() / items, a, a.toDouble() / items))
    }
}
