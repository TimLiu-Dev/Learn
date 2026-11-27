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
fun sign(secret: ByteArray, t: Long, body: String): String =
    TODO("t=…,v1=… where the tag covers the timestamp, a dot and the body")

// True only for a genuine, recent delivery. Never throws.
fun verify(secret: ByteArray, header: String, body: String, now: Long): Boolean =
    TODO("parse t and v1, recompute the tag, compare with MessageDigest.isEqual, then check the age")
