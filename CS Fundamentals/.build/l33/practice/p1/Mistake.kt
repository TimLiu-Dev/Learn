import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmacHex(key: ByteArray, message: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return HexFormat.of().formatHex(mac.doFinal(message.toByteArray()))
}

fun sign(key: ByteArray, message: String): String = message + "." + hmacHex(key, message)

// Splits at the FIRST dot.
fun verify(key: ByteArray, token: String): String? {
    val parts = token.split(".", limit = 2)
    if (parts.size != 2) return null
    val (message, tag) = parts
    return if (MessageDigest.isEqual(hmacHex(key, message).toByteArray(), tag.toByteArray())) message else null
}
