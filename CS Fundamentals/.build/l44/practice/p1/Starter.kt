// Assumptions in, numbers out. Each name says its unit.
const val SECONDS_PER_MONTH = 30L * 24 * 3600   // 2,592,000

data class Assumptions(
    val newPerMonth: Double,     // new rows (links, pastes) per month
    val readsPerWrite: Double,
    val bytesPerRow: Double,
    val years: Int,
    val hotFraction: Double,     // the share of one month's new rows that is hot
)

data class Estimate(
    val writesPerSec: Double,
    val readsPerSec: Double,
    val storageBytes: Double,    // every row written in `years`
    val cacheBytes: Double,      // the hot share of one month's rows
)

fun estimate(a: Assumptions): Estimate =
    TODO("writes/s, reads/s, storage and cache, each from the assumptions")

// The shortest base62 length with at least `codes` distinct codes (62^k >= codes).
fun minCodeLength(codes: Long): Int =
    TODO("count base62 digits, without letting anything overflow")
