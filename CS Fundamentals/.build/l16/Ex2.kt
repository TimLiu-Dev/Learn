import kotlin.time.measureTime

fun uniqueSlow(words: List<String>): List<String> {
    val result = mutableListOf<String>()
    for (w in words) if (w !in result) result.add(w)
    return result
}

fun uniqueFast(words: List<String>): List<String> {
    val seen = HashSet<String>()
    val result = mutableListOf<String>()
    for (w in words) if (seen.add(w)) result.add(w)
    return result
}

// n words, about half of them repeats
fun makeWords(n: Int): List<String> = List(n) { i -> "w${(i * 7919) % (n / 2)}" }

fun bestMs(words: List<String>, f: (List<String>) -> List<String>): Double {
    var sink = 0
    repeat(3) { sink += f(words).size }                       // warm-up
    var best = Double.MAX_VALUE
    repeat(3) {
        val d = measureTime { sink += f(words).size }
        best = minOf(best, d.inWholeMicroseconds / 1e3)
    }
    check(sink > 0)                                           // use the results
    return best
}

fun main() {
    println("%7s %10s %6s %10s %6s".format("n", "slow ms", "ratio", "fast ms", "ratio"))
    var prevSlow = 0.0
    var prevFast = 0.0
    for (n in listOf(4_000, 8_000, 16_000, 32_000, 64_000)) {
        val words = makeWords(n)
        val slow = bestMs(words, ::uniqueSlow)
        val fast = bestMs(words, ::uniqueFast)
        val rs = if (prevSlow > 0) "%.1f".format(slow / prevSlow) else "-"
        val rf = if (prevFast > 0) "%.1f".format(fast / prevFast) else "-"
        println("%7d %10.2f %6s %10.2f %6s".format(n, slow, rs, fast, rf))
        prevSlow = slow
        prevFast = fast
    }
}
