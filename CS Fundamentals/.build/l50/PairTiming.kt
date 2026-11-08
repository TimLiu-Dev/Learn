import kotlin.time.measureTime

// Worst case for both: all values even, target odd, so no pair is ever found.
fun bruteSteps(a: IntArray, target: Int): Long {
    var steps = 0L
    for (i in a.indices)
        for (j in i + 1 until a.size) {
            steps++
            if (a[i] + a[j] == target) return steps
        }
    return steps
}

fun twoPointerSteps(a: IntArray, target: Int): Long {
    var steps = 0L
    var lo = 0
    var hi = a.size - 1
    while (lo < hi) {
        steps++
        val sum = a[lo] + a[hi]
        if (sum == target) return steps
        if (sum < target) lo++ else hi--
    }
    return steps
}

var sink = 0L   // L16: every result is used, so the JIT can't skip the work

fun bestOf3Ms(f: () -> Long): Double {
    repeat(3) { sink += f() }                            // warm-up (L16)
    var best = Double.MAX_VALUE
    repeat(3) { best = minOf(best, measureTime { sink += f() }.inWholeNanoseconds / 1e6) }
    return best
}

fun doubling(name: String, startN: Int, rounds: Int, f: (IntArray, Int) -> Long) {
    println("== $name")
    var n = startN
    var prev = 0.0
    repeat(rounds) {
        val a = IntArray(n) { it * 2 }                   // sorted, all even
        val target = n + 1                               // odd: never found, in the middle of the range
        val steps = f(a, target)
        val ms = bestOf3Ms { f(a, target) }
        val ratio = if (prev > 0) "%.1f".format(ms / prev) else "-"
        println("n=%,10d  steps=%,15d  %9.3f ms  ratio %s".format(n, steps, ms, ratio))
        prev = ms
        n *= 2
    }
}

fun main() {
    doubling("brute force: O(n^2)", 5_000, 5, ::bruteSteps)
    doubling("two pointers: O(n)", 1_000_000, 5, ::twoPointerSteps)
    println("(sink $sink)")
}
