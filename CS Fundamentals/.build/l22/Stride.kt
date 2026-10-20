const val SIZE = 64 * 1024 * 1024                    // 64 M ints = 256 MB, far bigger than any cache

fun touch(a: IntArray, step: Int): Double {
    var best = Double.MAX_VALUE
    repeat(5) {
        val t = System.nanoTime()
        var i = 0
        while (i < SIZE) { a[i]++; i += step }      // SIZE / step accesses
        best = minOf(best, (System.nanoTime() - t) / 1e6)
    }
    return best
}

fun main() {
    val a = IntArray(SIZE)
    println("step  accesses    total ms   ns/access")
    for (step in intArrayOf(1, 2, 4, 8, 16, 32, 64, 128)) {
        val ms = touch(a, step)
        val n = SIZE / step
        println("%4d  %10d  %8.1f  %8.2f".format(step, n, ms, ms * 1e6 / n))
    }
}
