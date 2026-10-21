import java.security.MessageDigest

fun sha256(s: String): ByteArray =
    MessageDigest.getInstance("SHA-256").digest(s.toByteArray())

fun hex(b: ByteArray) = b.joinToString("") { "%02x".format(it) }

fun bitsDiffer(a: ByteArray, b: ByteArray): Int =
    a.indices.sumOf { Integer.bitCount((a[it].toInt() xor b[it].toInt()) and 0xff) }

fun main() {
    val h1 = sha256("hello")
    val h2 = sha256("hellp")
    println("hello -> ${hex(h1)}")
    println("hellp -> ${hex(h2)}")
    println("bits that differ: ${bitsDiffer(h1, h2)} of ${h1.size * 8}")
    // L19's hashCode is built for speed, not security
    println("\"Aa\".hashCode() = ${"Aa".hashCode()}, \"BB\".hashCode() = ${"BB".hashCode()}")
}
