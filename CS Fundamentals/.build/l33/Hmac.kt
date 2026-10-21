import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmac(key: ByteArray, msg: String): ByteArray =
    Mac.getInstance("HmacSHA256").run {
        init(SecretKeySpec(key, "HmacSHA256"))
        doFinal(msg.toByteArray())
    }

fun main() {
    val key = "server-side-secret".toByteArray()  // in real code: from an env var
    val msg = "user=42&role=member"
    val tag = hmac(key, msg)
    println("tag: ${tag.joinToString("") { "%02x".format(it) }.take(16)}…")

    // The server recomputes the tag and compares in constant time
    println("genuine: ${MessageDigest.isEqual(tag, hmac(key, msg))}")
    println("edited:  ${MessageDigest.isEqual(tag, hmac(key, "user=42&role=admin"))}")
}
