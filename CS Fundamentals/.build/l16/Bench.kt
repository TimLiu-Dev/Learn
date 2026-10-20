import kotlin.random.Random
import kotlin.time.measureTime

fun sumAll(a: IntArray): Long {                          // O(n)
    var s = 0L
    for (x in a) s += x
    return s
}

fun countPairs(a: IntArray): Long {                      // O(n^2): Sedgewick's "2-sum"
    var count = 0L
    for (i in a.indices)
        for (j in i + 1 until a.size)
            if (a[i] + a[j] == 0) count++
    return count
}

var sink = 0L   // every result is added here and printed, so the JIT can't skip the work

fun bestOf3Ms(a: IntArray, f: (IntArray) -> Long): Double {
    repeat(3) { sink += f(a) }                           // warm-up: give the JIT time to compile f
    var best = Double.MAX_VALUE
    repeat(3) {
        val d = measureTime { sink += f(a) }             // kotlin.time: uses System.nanoTime on the JVM
        best = minOf(best, d.inWholeNanoseconds / 1e6)
    }
    return best
}

fun doubling(name: String, startN: Int, rounds: Int, f: (IntArray) -> Long) {
    println("== $name")
    var n = startN
    var prev = 0.0
    repeat(rounds) {
        val rnd = Random(n)
        val a = IntArray(n) { rnd.nextInt(-1_000_000, 1_000_000) }
        val ms = bestOf3Ms(a, f)
        val ratio = if (prev > 0) "%.1f".format(ms / prev) else "-"
        println("n=%,11d %9.2f ms   ratio %s".format(n, ms, ratio))
        prev = ms
        n *= 2
    }
}

fun main() {
    doubling("sumAll: O(n)", 1_000_000, 6, ::sumAll)
    doubling("countPairs: O(n^2)", 2_000, 6, ::countPairs)
    println("(sink $sink)")
}
