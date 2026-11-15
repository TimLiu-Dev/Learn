import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun pbkdf2(pw: String, salt: ByteArray, iterations: Int): ByteArray {
    val spec = PBEKeySpec(pw.toCharArray(), salt, iterations, 256)
    return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
}

// pbkdf2-sha256$<iterations>$<base64 salt>$<base64 hash>
fun store(pw: String, salt: ByteArray, iterations: Int): String {
    val b64 = Base64.getEncoder()
    return "pbkdf2-sha256\$$iterations\$${b64.encodeToString(salt)}\$${b64.encodeToString(pbkdf2(pw, salt, iterations))}"
}

const val ITERATIONS = 1000

// Uses today's cost instead of the one stored with the hash.
fun verify(pw: String, stored: String): Boolean {
    val parts = stored.split("$")
    if (parts.size != 4 || parts[0] != "pbkdf2-sha256") return false
    return try {
        val salt = Base64.getDecoder().decode(parts[2])
        val expected = Base64.getDecoder().decode(parts[3])
        MessageDigest.isEqual(pbkdf2(pw, salt, ITERATIONS), expected)
    } catch (e: IllegalArgumentException) {
        false
    }
}
