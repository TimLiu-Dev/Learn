import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

// A JWT is three base64url parts: header.payload.signature (RFC 7515 §7.1, compact serialization).
val b64 = Base64.getUrlEncoder().withoutPadding()
val unb64 = Base64.getUrlDecoder()

fun hs256(key: ByteArray, signingInput: String): ByteArray =
    Mac.getInstance("HmacSHA256").run { init(SecretKeySpec(key, "HmacSHA256")); doFinal(signingInput.toByteArray()) }

fun sign(key: ByteArray, header: String, payload: String): String {
    val input = b64.encodeToString(header.toByteArray()) + "." + b64.encodeToString(payload.toByteArray())
    return input + "." + b64.encodeToString(hs256(key, input))
}

fun signatureValid(key: ByteArray, token: String): Boolean {
    val parts = token.split(".")
    if (parts.size != 3) return false
    val expected = hs256(key, parts[0] + "." + parts[1])
    return MessageDigest.isEqual(expected, unb64.decode(parts[2]))   // constant time (L33)
}

fun main() {
    // 1. RFC 7515 Appendix A.1: the published key, signing input and signature
    val rfcKey = unb64.decode("AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow")
    val rfcInput = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9" +
        ".eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ"
    val ours = b64.encodeToString(hs256(rfcKey, rfcInput))
    println("RFC 7515 A.1 signature: $ours")
    println("matches the RFC: ${ours == "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"}")

    // 2. Our own token, with a fresh 256-bit key (RFC 7518 §3.2: at least the hash size)
    val key = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
    val header = buildJsonObject { put("alg", "HS256"); put("typ", "JWT") }.toString()
    val claims = buildJsonObject {
        put("iss", "https://books.example"); put("sub", "ana"); put("aud", "books-api")
        put("iat", 1_790_000_000); put("exp", 1_790_000_900)                 // 15 minutes later
        put("role", "editor")
    }.toString()
    val token = sign(key, header, claims)
    println("token: ${token.take(40)}…${token.takeLast(12)}")
    println("valid signature: ${signatureValid(key, token)}")

    // 3. Signed is not secret: anyone can read the payload without the key
    val payload = Json.parseToJsonElement(unb64.decode(token.split(".")[1]).decodeToString()).jsonObject
    println("anyone can read: sub=${payload["sub"]} role=${payload["role"]}")

    // 4. Change one claim and keep the old signature: the check fails
    val edited = claims.replace("\"editor\"", "\"admin\"")
    val tampered = token.split(".").let { (h, _, s) -> h + "." + b64.encodeToString(edited.toByteArray()) + "." + s }
    println("role edited to admin, old signature kept: valid=${signatureValid(key, tampered)}")
}
