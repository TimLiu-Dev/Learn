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

// True only if pw matches. Unknown or malformed input is false, never an exception.
fun verify(pw: String, stored: String): Boolean =
    TODO("split the four fields, check the algorithm, re-hash with the stored salt and iterations, compare")
