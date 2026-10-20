const val N = 4096                                  // 4096 x 4096 ints = 64 MB

fun rowMajor(g: Array<IntArray>): Long {
    var sum = 0L
    for (r in 0 until N) for (c in 0 until N) sum += g[r][c]   // walk along a row
    return sum
}

fun colMajor(g: Array<IntArray>): Long {
    var sum = 0L
    for (c in 0 until N) for (r in 0 until N) sum += g[r][c]   // jump to a new row every step
    return sum
}

fun bestMs(f: () -> Long): Pair<Double, Long> {
    var best = Double.MAX_VALUE
    var result = 0L
    repeat(5) {
        val t = System.nanoTime()
        result = f()
        best = minOf(best, (System.nanoTime() - t) / 1e6)
    }
    return best to result
}

fun main() {
    val g = Array(N) { r -> IntArray(N) { c -> (r + c) % 7 } }
    val (row, s1) = bestMs { rowMajor(g) }
    val (col, s2) = bestMs { colMajor(g) }
    println("row-major:    %6.1f ms  (sum $s1)".format(row))
    println("column-major: %6.1f ms  (sum $s2)".format(col))
    println("column-major is %.1fx slower".format(col / row))
}
