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

fun estimate(a: Assumptions): Estimate {
    val writesPerSec = a.newPerMonth / SECONDS_PER_MONTH
    return Estimate(
        writesPerSec = writesPerSec,
        readsPerSec = writesPerSec * a.readsPerWrite,
        storageBytes = a.newPerMonth * 12 * a.years * a.bytesPerRow,   // months, not years
        cacheBytes = a.newPerMonth * a.hotFraction * a.bytesPerRow,
    )
}

// codes distinct codes are numbered 0 .. codes-1, so count the base62 digits of codes-1.
// Dividing never overflows; multiplying up to 62^k would.
fun minCodeLength(codes: Long): Int {
    require(codes >= 1) { "need at least one code: $codes" }
    var rest = (codes - 1) / 62
    var length = 1
    while (rest > 0) {
        rest /= 62
        length++
    }
    return length
}
