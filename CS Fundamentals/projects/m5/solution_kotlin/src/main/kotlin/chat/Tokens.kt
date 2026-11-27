package chat

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.time.Clock
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.time.Duration

sealed interface TokenCheck {
    data class Valid(val user: String) : TokenCheck
    data object Bad : TokenCheck
    data object Expired : TokenCheck
}

/** HS256 JSON Web Tokens with `sub` and `exp`, built by hand as in L55. */
class Tokens(private val secret: ByteArray, private val ttl: Duration, private val clock: Clock = Clock.systemUTC()) {
    val ttlSeconds: Long get() = ttl.inWholeSeconds

    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    fun issue(user: String): String {
        val header = buildJsonObject { put("alg", "HS256"); put("typ", "JWT") }
        val payload = buildJsonObject { put("sub", user); put("exp", clock.instant().epochSecond + ttl.inWholeSeconds) }
        val signingInput = "${b64(header.toString())}.${b64(payload.toString())}"
        return "$signingInput.${encoder.encodeToString(hmac(signingInput))}"
    }

    fun verify(token: String): TokenCheck {
        val parts = token.split('.')
        if (parts.size != 3) return TokenCheck.Bad
        val (header, payload, signature) = parts
        val given = runCatching { decoder.decode(signature) }.getOrNull() ?: return TokenCheck.Bad
        // The signature first: nothing inside the token is trusted until it matches.
        if (!MessageDigest.isEqual(given, hmac("$header.$payload"))) return TokenCheck.Bad
        if (json(header)?.string("alg") != "HS256") return TokenCheck.Bad
        val claims = json(payload) ?: return TokenCheck.Bad
        val user = claims.string("sub") ?: return TokenCheck.Bad
        val exp = (claims["exp"] as? JsonPrimitive)?.takeIf { !it.isString }?.longOrNull ?: return TokenCheck.Bad
        if (clock.instant().epochSecond >= exp) return TokenCheck.Expired
        return TokenCheck.Valid(user)
    }

    private fun b64(text: String) = encoder.encodeToString(text.toByteArray())

    private fun hmac(text: String): ByteArray =
        Mac.getInstance("HmacSHA256").run {
            init(SecretKeySpec(secret, "HmacSHA256"))
            doFinal(text.toByteArray())
        }

    private fun json(part: String): JsonObject? = runCatching {
        Json.parseToJsonElement(String(decoder.decode(part), Charsets.UTF_8)) as? JsonObject
    }.getOrNull()

    private fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
}
