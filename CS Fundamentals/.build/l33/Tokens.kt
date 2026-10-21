import java.security.SecureRandom
import java.util.Base64
import java.util.Random

fun main() {
    // Seeded Random: the same "random" numbers every run (handy in L18, fatal here)
    val a = Random(42).nextLong()
    val b = Random(42).nextLong()
    println("Random(42) twice: $a, $b, same=${a == b}")

    // A session token: 32 bytes from the OS's cryptographic source
    val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
    val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    println("token length: ${token.length} chars")
}
