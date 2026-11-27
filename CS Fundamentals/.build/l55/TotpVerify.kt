import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun totp(secret: ByteArray, step: Long): String {                       // RFC 6238 with 6 digits, as in Totp.kt
    val hs = Mac.getInstance("HmacSHA1").run { init(SecretKeySpec(secret, "HmacSHA1")); doFinal(ByteBuffer.allocate(8).putLong(step).array()) }
    val offset = hs[hs.size - 1].toInt() and 0x0f
    return ((ByteBuffer.wrap(hs, offset, 4).int and 0x7fffffff) % 1_000_000).toString().padStart(6, '0')
}

class TotpVerifier(private val secret: ByteArray, private val now: () -> Long) {
    private var lastUsedStep = Long.MIN_VALUE

    fun verify(code: String): Boolean {
        val current = now() / 30
        for (step in listOf(current, current - 1)) {                     // §5.2: "at most one time step" of delay
            if (step <= lastUsedStep) continue                           // §5.2: never accept the same OTP twice
            if (MessageDigest.isEqual(totp(secret, step).toByteArray(), code.toByteArray())) {   // constant time (L33)
                lastUsedStep = step
                return true
            }
        }
        return false
    }
}

fun main() {
    val secret = "12345678901234567890".toByteArray()
    var clock = 1_790_000_010L                                           // exactly where step 59666667 starts
    val v = TotpVerifier(secret) { clock }
    val step = clock / 30
    println("code for this step           -> ${v.verify(totp(secret, step))}")
    println("the same code again          -> ${v.verify(totp(secret, step))}")
    clock += 30
    println("next step, last step's code  -> ${TotpVerifier(secret) { clock }.verify(totp(secret, step))}")
    println("next step, a fresh code      -> ${v.verify(totp(secret, step + 1))}")
    println("a code from two steps ago    -> ${v.verify(totp(secret, step - 1))}")
    println("a code from the future       -> ${v.verify(totp(secret, step + 2))}")
    println("six wrong digits             -> ${v.verify("000000")}")
}
