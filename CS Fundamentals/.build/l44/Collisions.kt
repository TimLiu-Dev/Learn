// How soon does "hash the URL, keep the first k base62 characters" collide?
import java.security.MessageDigest
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

fun truncatedHash(url: String, space: Long): Long {
    val d = MessageDigest.getInstance("SHA-256").digest(url.toByteArray())
    var n = 0L
    for (i in 0 until 8) n = (n shl 8) or (d[i].toLong() and 0xff)
    return Math.floorMod(n, space)
}

// Insert distinct URLs until two of them get the same truncated code.
fun firstCollision(prefix: String, space: Long): Int {
    val seen = HashSet<Long>()
    var i = 0
    while (seen.add(truncatedHash("https://example.com/$prefix/$i", space))) i++
    return i + 1
}

fun main() {
    for (chars in listOf(5, 7)) {
        val space = 62.0.pow(chars).toLong()
        val trials = if (chars == 5) 20 else 5
        val counts = (1..trials).map { firstCollision("t$it", space) }
        println("$chars chars: space %,d, birthday estimate %,.0f, measured mean %,d over %d trials".format(
            space, sqrt(PI * space / 2), counts.average().toLong(), trials))
        if (chars == 7) println("  each trial: " + counts.joinToString { "%,d".format(it) })
    }
    val n = 6e9                       // links after 5 years (Estimates)
    val space7 = 62.0.pow(7)
    println("6.0 G links in 7 chars: P(any collision) = %.6f, expected colliding pairs ≈ %,.0f".format(
        1 - exp(-n * n / (2 * space7)), n * n / (2 * space7)))
}
