const val MINUTES_PER_DAY = 1440

// Reduce delta first (MCS: take remainders at every step), so start + delta can't overflow.
fun minuteAfter(start: Int, delta: Int): Int =
    (start + delta.mod(MINUTES_PER_DAY)).mod(MINUTES_PER_DAY)

// How many times n can be halved before it reaches 1: floor(log2 n). n must be at least 1.
fun halvings(n: Long): Int {
    require(n >= 1) { "n must be at least 1, was $n" }
    var x = n
    var count = 0
    while (x > 1) {
        x /= 2
        count++
    }
    return count
}
