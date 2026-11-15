import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// The hex HMAC-SHA256 of message under key: 64 hex digits.
fun hmacHex(key: ByteArray, message: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return HexFormat.of().formatHex(mac.doFinal(message.toByteArray()))
}

fun sign(key: ByteArray, message: String): String =
    TODO("return the message, a dot, and its tag")

// The message if the tag is genuine; null for anything else. Never throws.
fun verify(key: ByteArray, token: String): String? =
    TODO("split off the tag, recompute it, compare with MessageDigest.isEqual")
