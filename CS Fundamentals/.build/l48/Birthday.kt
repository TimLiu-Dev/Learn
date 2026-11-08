import kotlin.random.Random

// Exact: P(no match) = (d/d) * ((d-1)/d) * ... * ((d-n+1)/d)
fun pShared(n: Int, d: Double): Double {
    var noMatch = 1.0
    for (i in 0 until n) noMatch *= 1 - i / d
    return 1 - noMatch
}

// Monte Carlo: fill n random birthdays, stop at the first repeat
fun simulate(n: Int, d: Int, trials: Int, rnd: Random): Double {
    var hits = 0
    repeat(trials) {
        val seen = BooleanArray(d)
        for (i in 0 until n) {
            val day = rnd.nextInt(d)
            if (seen[day]) { hits++; break }
            seen[day] = true
        }
    }
    return hits.toDouble() / trials
}

fun main() {
    val rnd = Random(42)
    for (n in listOf(10, 23, 50, 70)) {
        println("n=%2d  exact %.4f  simulated %.4f".format(n, pShared(n, 365.0), simulate(n, 365, 100_000, rnd)))
    }
    // Expected number of matching PAIRS = C(n,2) / d, by linearity of expectation
    println("n=23  expected matching pairs = %.3f".format(23 * 22 / 2 / 365.0))

    // Hash codes: d = 2^32 possible Int values
    val d = 4_294_967_296.0
    var n = 1
    while (pShared(n, d) < 0.5) n++
    println("2^32 hash codes: 50%% chance of a collision at n = %,d".format(n))
}
