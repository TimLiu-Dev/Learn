import kotlin.random.Random

fun pShared(n: Int, d: Double): Double {
    var noMatch = 1.0
    for (i in 0 until n) noMatch *= 1 - i / d
    return 1 - noMatch
}

// Smallest n whose collision probability reaches p
fun firstN(d: Double, p: Double): Int {
    var n = 1
    while (pShared(n, d) < p) n++
    return n
}

fun main() {
    val big = Math.pow(36.0, 6.0)                 // 6 chars of [a-z0-9]
    println("d = %,.0f".format(big))
    println("1%%: n = %,d   50%%: n = %,d".format(firstN(big, 0.01), firstN(big, 0.5)))

    // Check the maths by simulation on a space small enough to simulate: 3 chars
    val small = 36 * 36 * 36
    val n = firstN(small.toDouble(), 0.5)
    val rnd = Random(7)
    val trials = 20_000
    var hits = 0
    repeat(trials) {
        val seen = HashSet<Int>()
        for (i in 0 until n) if (!seen.add(rnd.nextInt(small))) { hits++; break }
    }
    println("d = $small: predicted 50%% at n = $n, simulated %.3f".format(hits.toDouble() / trials))
}
