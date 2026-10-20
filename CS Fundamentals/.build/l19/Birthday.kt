import java.util.Random

// How many random keys until two land in the same bucket?
fun firstCollision(buckets: Int, rnd: Random): Int {
    val used = BooleanArray(buckets)
    var keys = 0
    while (true) {
        val i = rnd.nextInt(buckets)
        keys++
        if (used[i]) return keys
        used[i] = true
    }
}

fun main() {
    val rnd = Random(42)
    val trials = 100_000

    var shared = 0
    repeat(trials) { if (firstCollision(365, rnd) <= 23) shared++ }
    println("23 people, 365 days: shared birthday in ${100.0 * shared / trials}% of trials")

    var exact = 1.0
    for (k in 0 until 23) exact *= (365.0 - k) / 365
    println("exact: ${"%.1f".format(100 * (1 - exact))}%")

    var total = 0L
    repeat(1_000) { total += firstCollision(1_000_000, rnd) }
    println("1,000,000 buckets: first collision after ~${total / 1_000} keys on average")
}
