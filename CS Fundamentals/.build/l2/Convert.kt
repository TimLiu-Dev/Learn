fun main() {
    val small: Int = 2_000_000_000
    val wide: Long = small.toLong()          // widening must be explicit
    val sum = small + 1L                     // Int + Long -> Long
    val chopped = 5_000_000_000L.toInt()     // narrowing silently drops the high bits
    val truncated = 3.99.toInt()             // Double -> Int drops the fraction
    println("$wide $sum $chopped $truncated")
}
