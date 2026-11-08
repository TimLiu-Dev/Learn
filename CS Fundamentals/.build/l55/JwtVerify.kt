import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

// A strict verifier. Each check is a rule from RFC 7519 (JWT) or RFC 8725 (JWT Best Current Practices).
sealed interface Verdict {
    data class Valid(val subject: String) : Verdict
    data class Rejected(val reason: String) : Verdict
}

class JwtVerifier(
    private val key: ByteArray,
    private val issuer: String,
    private val audience: String,
    private val now: () -> Long,                       // seconds since the epoch, injected for tests
    private val leeway: Long = 60,                     // RFC 7519 §4.1.4: "usually no more than a few minutes"
) {
    private val unb64 = Base64.getUrlDecoder()

    private fun json(part: String): JsonObject? =
        runCatching { Json.parseToJsonElement(unb64.decode(part).decodeToString()).jsonObject }.getOrNull()

    fun verify(token: String): Verdict {
        val parts = token.split(".")
        if (parts.size != 3) return Verdict.Rejected("not three parts")
        val header = json(parts[0]) ?: return Verdict.Rejected("header is not JSON")
        // We decide the algorithm; the token's header is only checked against it (RFC 8725 §3.1)
        if (header["alg"]?.jsonPrimitive?.content != "HS256") return Verdict.Rejected("alg must be HS256")
        val mac = Mac.getInstance("HmacSHA256").run {
            init(SecretKeySpec(key, "HmacSHA256")); doFinal("${parts[0]}.${parts[1]}".toByteArray())
        }
        val sig = runCatching { unb64.decode(parts[2]) }.getOrNull() ?: return Verdict.Rejected("bad signature encoding")
        if (!MessageDigest.isEqual(mac, sig)) return Verdict.Rejected("bad signature")
        // Only now are the claims worth reading
        val c = json(parts[1]) ?: return Verdict.Rejected("payload is not JSON")
        val exp = c["exp"]?.jsonPrimitive?.longOrNull ?: return Verdict.Rejected("no exp")
        if (now() >= exp + leeway) return Verdict.Rejected("expired")
        if (c["iss"]?.jsonPrimitive?.content != issuer) return Verdict.Rejected("wrong issuer")
        if (c["aud"]?.jsonPrimitive?.content != audience) return Verdict.Rejected("wrong audience")   // RFC 8725 §3.9
        val sub = c["sub"]?.jsonPrimitive?.content ?: return Verdict.Rejected("no subject")
        return Verdict.Valid(sub)
    }
}

// The issuer side, used here to make test tokens
fun issue(key: ByteArray, header: JsonObject, claims: JsonObject): String {
    val b64 = Base64.getUrlEncoder().withoutPadding()
    val input = b64.encodeToString(header.toString().toByteArray()) + "." + b64.encodeToString(claims.toString().toByteArray())
    val mac = Mac.getInstance("HmacSHA256").run { init(SecretKeySpec(key, "HmacSHA256")); doFinal(input.toByteArray()) }
    return input + "." + b64.encodeToString(mac)
}

// RFC 7519 §6 allows "unsecured" JWTs with alg "none" and an empty signature. A server must never accept one.
fun unsigned(token: String): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString("""{"alg":"none"}""".toByteArray()) + "." + token.split(".")[1] + "."

fun main() {
    val key = ByteArray(32).also { SecureRandom().nextBytes(it) }
    val t0 = 1_790_000_000L
    val verifier = JwtVerifier(key, "https://books.example", "books-api", now = { t0 + 300 })
    val hs = buildJsonObject { put("alg", "HS256"); put("typ", "JWT") }
    fun claims(exp: Long = t0 + 900, iss: String = "https://books.example", aud: String = "books-api") =
        buildJsonObject { put("iss", iss); put("sub", "ana"); put("aud", aud); put("exp", exp) }
    val good = issue(key, hs, claims())
    val cases = listOf(
        "genuine token" to good,
        "unsigned (alg none)" to unsigned(good),
        "other algorithm" to issue(key, buildJsonObject { put("alg", "HS512") }, claims()),
        "signed by another key" to issue(ByteArray(32).also { SecureRandom().nextBytes(it) }, hs, claims()),
        "expired 10 min ago" to issue(key, hs, claims(exp = t0 + 300 - 600)),
        "expired 30 s ago (leeway)" to issue(key, hs, claims(exp = t0 + 300 - 30)),
        "for another service" to issue(key, hs, claims(aud = "billing-api")),
        "from another issuer" to issue(key, hs, claims(iss = "https://other.example")),
    )
    for ((name, token) in cases) println("%-26s -> %s".format(name, verifier.verify(token)))
}
