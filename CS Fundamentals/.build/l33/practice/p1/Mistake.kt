import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

const val TOLERANCE_SECONDS = 300L

fun hmacHex(key: ByteArray, message: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return HexFormat.of().formatHex(mac.doFinal(message.toByteArray()))
}

// Signs the body only: the timestamp isn't covered by the tag.
fun sign(secret: ByteArray, t: Long, body: String): String = "t=$t,v1=${hmacHex(secret, body)}"

fun verify(secret: ByteArray, header: String, body: String, now: Long): Boolean {
    val parts = header.split(",").associate { it.substringBefore('=') to it.substringAfter('=', "") }
    val t = parts["t"]?.toLongOrNull() ?: return false
    val tag = parts["v1"] ?: return false
    if (!MessageDigest.isEqual(hmacHex(secret, body).toByteArray(), tag.toByteArray())) return false
    return Math.abs(now - t) <= TOLERANCE_SECONDS
}
