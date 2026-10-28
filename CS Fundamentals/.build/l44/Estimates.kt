// Back-of-envelope estimates for a URL shortener: every number comes from a stated assumption.
import kotlin.math.ceil
import kotlin.math.ln

const val SECONDS_PER_MONTH = 30L * 24 * 3600          // 2,592,000 ≈ "2.5 million"

fun human(n: Double): String = when {
    n >= 1e12 -> "%.1f T".format(n / 1e12)
    n >= 1e9 -> "%.1f G".format(n / 1e9)
    n >= 1e6 -> "%.1f M".format(n / 1e6)
    n >= 1e3 -> "%.1f k".format(n / 1e3)
    else -> "%.1f".format(n)
}

fun main() {
    // Assumptions (say them out loud: change one and every number below moves)
    val newLinksPerMonth = 100e6
    val readsPerWrite = 100.0
    val peakFactor = 3.0
    val bytesPerLinkRow = 500.0        // code + long URL (~100 B) + timestamps + index overhead, rounded up
    val years = 5
    val hotFraction = 0.2              // the hot 20% of last month's links serve most reads
    val bytesPerRedirectResponse = 500.0   // status line + headers, no body

    val writesPerSec = newLinksPerMonth / SECONDS_PER_MONTH
    val readsPerSec = writesPerSec * readsPerWrite
    println("writes/s: avg ${human(writesPerSec)}, peak ${human(writesPerSec * peakFactor)}")
    println("reads/s:  avg ${human(readsPerSec)}, peak ${human(readsPerSec * peakFactor)}")

    val totalLinks = newLinksPerMonth * 12 * years
    println("links after $years years: ${human(totalLinks)}")
    println("link storage: ${human(totalLinks * bytesPerLinkRow)}B")

    val rawClicks = readsPerSec * SECONDS_PER_MONTH * 12 * years
    println("raw click rows after $years years: ${human(rawClicks)} (x 50 B = ${human(rawClicks * 50)}B)")

    println("cache for hot links: ${human(hotFraction * newLinksPerMonth * bytesPerLinkRow)}B")
    println("redirect bandwidth: avg ${human(readsPerSec * bytesPerRedirectResponse)}B/s, " +
            "peak ${human(readsPerSec * peakFactor * bytesPerRedirectResponse)}B/s")

    val minChars = ceil(ln(totalLinks) / ln(62.0)).toInt()
    println("base62 chars needed for ${human(totalLinks)} codes: $minChars " +
            "(62^$minChars = ${human(Math.pow(62.0, minChars.toDouble()))})")
}
