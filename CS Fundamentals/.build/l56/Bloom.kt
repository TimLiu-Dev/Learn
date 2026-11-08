import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt

// A Bloom filter: m bits, k hash positions per key. "No" is always right; "maybe" can be wrong.
class Bloom(val m: Int, val k: Int) {
    private val bits = LongArray((m + 63) / 64)

    fun add(key: String) = positions(key).forEach { bits[it ushr 6] = bits[it ushr 6] or (1L shl it) }
    fun mightContain(key: String) = positions(key).all { bits[it ushr 6] and (1L shl it) != 0L }

    // Two hashes simulate k of them: g_i = h1 + i*h2 (Kirsch & Mitzenmacher).
    private fun positions(key: String): List<Int> {
        val h = hash64(key)
        val h1 = h.toInt(); val h2 = (h ushr 32).toInt()
        return List(k) { i -> Math.floorMod(h1 + i * h2, m) }
    }

    companion object {
        fun optimalK(m: Int, n: Int) = maxOf(1, (ln(2.0) * m / n).roundToInt())
        fun predicted(m: Int, n: Int, k: Int) = Math.pow(1 - exp(-k.toDouble() * n / m), k.toDouble())

        // FNV-1a over the UTF-8 bytes, then MurmurHash3's 64-bit finaliser to mix the bits well.
        fun hash64(s: String): Long {
            var h = 0xcbf29ce484222325uL.toLong()           // the FNV-1a offset basis
            for (b in s.toByteArray()) { h = h xor (b.toLong() and 0xff); h *= 0x100000001b3L }
            h = h xor (h ushr 33); h *= 0xff51afd7ed558ccduL.toLong()
            h = h xor (h ushr 33); h *= 0xc4ceb9fe1a85ec53uL.toLong()
            return h xor (h ushr 33)
        }
    }
}
