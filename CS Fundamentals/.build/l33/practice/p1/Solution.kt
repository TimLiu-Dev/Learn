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

fun sign(key: ByteArray, message: String): String = message + "." + hmacHex(key, message)

// The message if the tag is genuine; null for anything else. Never throws.
fun verify(key: ByteArray, token: String): String? {
    val dot = token.lastIndexOf('.')          // the tag never contains a dot; the message might
    if (dot < 0) return null
    val message = token.substring(0, dot)
    val tag = token.substring(dot + 1)
    val expected = hmacHex(key, message)
    // Constant-time comparison: the time taken doesn't reveal how much of the tag was right.
    return if (MessageDigest.isEqual(expected.toByteArray(), tag.toByteArray())) message else null
}
