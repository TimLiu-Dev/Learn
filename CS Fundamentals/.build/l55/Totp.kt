import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// HOTP (RFC 4226 §5.3): HMAC the 8-byte counter, then "dynamic truncation" down to a few decimal digits.
fun hotp(secret: ByteArray, counter: Long, digits: Int): String {
    val hs = Mac.getInstance("HmacSHA1").run { init(SecretKeySpec(secret, "HmacSHA1")); doFinal(ByteBuffer.allocate(8).putLong(counter).array()) }
    val offset = hs[hs.size - 1].toInt() and 0x0f                      // low 4 bits of the last byte pick a position
    val bin = ByteBuffer.wrap(hs, offset, 4).int and 0x7fffffff        // 4 bytes from there, top bit cleared: 31 bits
    var mod = 1; repeat(digits) { mod *= 10 }
    return (bin % mod).toString().padStart(digits, '0')
}

// TOTP (RFC 6238 §4.2): the counter is the number of 30-second steps since the epoch. Long, not Int: 2038 is coming.
fun totp(secret: ByteArray, unixSeconds: Long, digits: Int = 6): String = hotp(secret, unixSeconds / 30, digits)

fun main() {
    val secret = "12345678901234567890".toByteArray()                  // RFC 6238 Appendix B's test secret
    val vectors = listOf(59L to "94287082", 1111111109L to "07081804", 1111111111L to "14050471",
        1234567890L to "89005924", 2000000000L to "69279037", 20000000000L to "65353130")
    for ((t, expected) in vectors) {
        val got = totp(secret, t, digits = 8)
        println("%11d  T=%-9d %s  %s".format(t, t / 30, got, if (got == expected) "matches the RFC" else "WRONG, RFC says $expected"))
    }
    println("6-digit codes at 1790000000..1790000089: " + (0L..89L step 30).joinToString(" ") { totp(secret, 1_790_000_000L + it) })
}
