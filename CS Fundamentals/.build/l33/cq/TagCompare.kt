import java.security.MessageDigest

fun sha256(s: String): ByteArray = MessageDigest.getInstance("SHA-256").digest(s.toByteArray())

fun main() {
    val stored = sha256("hunter2")
    val attempt = sha256("hunter2")
    println("${stored == attempt} ${stored.contentEquals(attempt)} ${MessageDigest.isEqual(stored, attempt)}")
}
