import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

const val ITERATIONS = 600_000  // OWASP's PBKDF2-HMAC-SHA256 recommendation
private val b64 = Base64.getEncoder()
private val unb64 = Base64.getDecoder()

fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
    val spec = PBEKeySpec(password, salt, iterations, 256)
    try {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    } finally {
        spec.clearPassword()
    }
}

// Stored form: algorithm$iterations$salt$hash, so you can raise the cost later
fun hashPassword(password: CharArray): String {
    val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val hash = pbkdf2(password, salt, ITERATIONS)
    return "pbkdf2-sha256\$$ITERATIONS\$${b64.encodeToString(salt)}\$${b64.encodeToString(hash)}"
}

fun verifyPassword(password: CharArray, stored: String): Boolean {
    val (alg, iter, salt, hash) = stored.split("$")
    require(alg == "pbkdf2-sha256") { "unknown algorithm $alg" }
    val candidate = pbkdf2(password, unb64.decode(salt), iter.toInt())
    return MessageDigest.isEqual(candidate, unb64.decode(hash))  // constant time
}

fun main() {
    repeat(2) { hashPassword("warm-up".toCharArray()) }  // let the JIT settle (L23)
    val start = System.nanoTime()
    val stored = hashPassword("sunshine1".toCharArray())
    val ms = (System.nanoTime() - start) / 1_000_000
    println("stored: ${stored.take(48)}…")
    println("one hash took $ms ms")
    println("right password: ${verifyPassword("sunshine1".toCharArray(), stored)}")
    println("wrong password: ${verifyPassword("sunshine2".toCharArray(), stored)}")
    println("same password again gives a new string: ${hashPassword("sunshine1".toCharArray()) != stored}")
}
