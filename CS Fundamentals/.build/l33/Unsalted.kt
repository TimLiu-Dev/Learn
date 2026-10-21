import java.security.MessageDigest
import java.security.SecureRandom

fun sha256Hex(bytes: ByteArray) =
    MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }.take(16)

fun main() {
    val users = listOf("ana" to "sunshine1", "ben" to "sunshine1")

    println("unsalted SHA-256:")
    for ((name, pw) in users) println("  $name ${sha256Hex(pw.toByteArray())}…")

    println("salted (random 16-byte salt per user):")
    val rng = SecureRandom()
    for ((name, pw) in users) {
        val salt = ByteArray(16).also { rng.nextBytes(it) }
        println("  $name ${sha256Hex(salt + pw.toByteArray())}…")
    }
}
