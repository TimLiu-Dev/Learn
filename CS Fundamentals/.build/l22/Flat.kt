const val N = 4096

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
    val nested = Array(N) { r -> IntArray(N) { c -> (r + c) % 7 } }  // N separate row objects
    val flat = IntArray(N * N) { i -> (i / N + i % N) % 7 }          // one block; cell (r, c) at r * N + c

    val results = listOf(
        "nested, row-major" to bestMs { var s = 0L; for (r in 0 until N) for (c in 0 until N) s += nested[r][c]; s },
        "flat, row-major" to bestMs { var s = 0L; for (r in 0 until N) for (c in 0 until N) s += flat[r * N + c]; s },
        "nested, column-major" to bestMs { var s = 0L; for (c in 0 until N) for (r in 0 until N) s += nested[r][c]; s },
        "flat, column-major" to bestMs { var s = 0L; for (c in 0 until N) for (r in 0 until N) s += flat[r * N + c]; s },
    )
    for ((name, r) in results) println("%-21s %6.1f ms  (sum ${r.second})".format(name, r.first))
}
