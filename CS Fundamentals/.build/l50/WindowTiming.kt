import kotlin.time.measureTime

// Both versions track characters in a 65,536-slot IntArray (one per Char value, L2),
// so the timing compares the algorithms, not HashMap overhead.

// Restart from every start position: O(n * window length), O(n^2) when nothing repeats.
fun longestUniqueBrute(s: String): Int {
    val stamp = IntArray(65_536)                         // stamp[c] == start + 1 means "seen from this start"
    var best = 0
    for (start in s.indices) {
        var end = start
        while (end < s.length && stamp[s[end].code] != start + 1) {
            stamp[s[end].code] = start + 1
            end++
        }
        best = maxOf(best, end - start)
    }
    return best
}

// The sliding window: each index enters once and the left edge only moves forward. O(n).
fun longestUniqueWindow(s: String): Int {
    val lastSeen = IntArray(65_536) { -1 }
    var left = 0
    var best = 0
    for (right in s.indices) {
        val c = s[right].code
        if (lastSeen[c] >= left) left = lastSeen[c] + 1
        lastSeen[c] = right
        best = maxOf(best, right - left + 1)
    }
    return best
}

var sink = 0L   // L16: every result is used

fun bestOf3Ms(f: () -> Int): Double {
    repeat(10) { sink += f() }                         // warm-up: these calls are fast, so give the JIT more of them
    var best = Double.MAX_VALUE
    repeat(5) { best = minOf(best, measureTime { sink += f() }.inWholeNanoseconds / 1e6) }
    return best
}

fun main() {
    println("worst case: n distinct characters (nothing ever repeats)")
    println("         n      brute ms  ratio     window ms")
    var prev = 0.0
    for (n in listOf(2_500, 5_000, 10_000, 20_000)) {
        val s = String(CharArray(n) { (0x4E00 + it).toChar() })   // n different CJK characters
        check(longestUniqueBrute(s) == n && longestUniqueWindow(s) == n)
        val b = bestOf3Ms { longestUniqueBrute(s) }
        val w = bestOf3Ms { longestUniqueWindow(s) }
        val ratio = if (prev > 0) "%.1f".format(b / prev) else "-"
        println("%,10d  %12.3f  %5s  %12.3f".format(n, b, ratio, w))
        prev = b
    }
    println("(sink $sink)")
}
