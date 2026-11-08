import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.roundToInt

// From k = ln2 · m/n and f = (1/2)^k: bits per key for a target false-positive rate p is -ln(p) / (ln 2)².
fun bitsPerKey(p: Double) = -ln(p) / (ln(2.0) * ln(2.0))

fun main() {
    val n = 100_000
    for (p in listOf(0.10, 0.01, 0.001)) {
        val m = ceil(n * bitsPerKey(p)).toInt()
        val k = (ln(2.0) * m / n).roundToInt()
        val bloom = Bloom(m, k)
        for (i in 0 until n) bloom.add("key-$i")
        val fp = (0 until 1_000_000).count { bloom.mightContain("absent-$it") }
        println("target %5.1f%%: %5.2f bits/key, k = %2d, measured %6.3f%%".format(100 * p, m.toDouble() / n, k, fp / 10_000.0))
    }
}
