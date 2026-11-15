const val MINUTES_PER_DAY = 1440

// % keeps the sign of the left operand, so going back past midnight gives a negative minute.
fun minuteAfter(start: Int, delta: Int): Int = (start + delta) % MINUTES_PER_DAY

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
