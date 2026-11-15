const val MINUTES_PER_DAY = 1440

// Right remainder, but start + delta overflows Int before mod ever sees it.
fun minuteAfter(start: Int, delta: Int): Int = (start + delta).mod(MINUTES_PER_DAY)

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
