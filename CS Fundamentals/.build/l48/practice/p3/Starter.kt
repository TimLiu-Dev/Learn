const val MINUTES_PER_DAY = 1440

// The minute of the day (0..1439) that is delta minutes after start (0..1439).
fun minuteAfter(start: Int, delta: Int): Int = TODO("a remainder that is never negative, and no overflow")

// How many times n can be halved before it reaches 1: floor(log2 n). n must be at least 1.
fun halvings(n: Long): Int = TODO("reject n < 1, then count halvings in a loop")
