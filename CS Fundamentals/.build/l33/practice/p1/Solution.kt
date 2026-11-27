import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

const val TOLERANCE_SECONDS = 300L

// The hex HMAC-SHA256 of message under key: 64 hex digits.
fun hmacHex(key: ByteArray, message: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return HexFormat.of().formatHex(mac.doFinal(message.toByteArray()))
}

// The signature header a sender attaches: t=<timestamp>,v1=<tag of "t.body">
fun sign(secret: ByteArray, t: Long, body: String): String = "t=$t,v1=${hmacHex(secret, "$t.$body")}"

// True only for a genuine, recent delivery. Never throws.
fun verify(secret: ByteArray, header: String, body: String, now: Long): Boolean {
    val parts = header.split(",").associate { it.substringBefore('=') to it.substringAfter('=', "") }
    val t = parts["t"]?.toLongOrNull() ?: return false
    val tag = parts["v1"] ?: return false
    val expected = hmacHex(secret, "$t.$body")             // the timestamp is signed too
    // Constant-time comparison: the time taken doesn't reveal how much of the tag was right.
    if (!MessageDigest.isEqual(expected.toByteArray(), tag.toByteArray())) return false
    return Math.abs(now - t) <= TOLERANCE_SECONDS           // a genuine but old delivery is a replay
}
