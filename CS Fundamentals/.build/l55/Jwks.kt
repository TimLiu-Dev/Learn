import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

val b64 = Base64.getUrlEncoder().withoutPadding()
val unb64 = Base64.getUrlDecoder()

// RFC 7518 §6.3.1: n and e are unsigned big-endian bytes, base64url. toByteArray() may add a sign byte: drop it.
fun b64(i: BigInteger): String = i.toByteArray().let { b64.encodeToString(if (it[0] == 0.toByte()) it.copyOfRange(1, it.size) else it) }

fun rs256(key: PrivateKey, input: String): ByteArray =
    Signature.getInstance("SHA256withRSA").run { initSign(key); update(input.toByteArray()); sign() }

fun jwt(header: JsonObject, claims: JsonObject, sign: (String) -> ByteArray): String {
    val input = b64.encodeToString(header.toString().toByteArray()) + "." + b64.encodeToString(claims.toString().toByteArray())
    return input + "." + b64.encodeToString(sign(input))
}

// The authorization server keeps the private keys and publishes only the public halves.
class AuthServer {
    private val keys = LinkedHashMap<String, KeyPair>()        // kid -> key pair
    private var current = ""
    fun rotate(kid: String) {                                  // RS256 needs 2048 bits or more (RFC 7518 §3.3)
        keys[kid] = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair(); current = kid
    }
    fun retire(kid: String) { keys.remove(kid) }
    fun publicKey(kid: String) = keys.getValue(kid).public as RSAPublicKey
    fun jwks(): String = buildJsonObject {                     // what GET /.well-known/jwks.json returns
        putJsonArray("keys") {
            for ((kid, pair) in keys) addJsonObject {
                val pub = pair.public as RSAPublicKey
                put("kty", "RSA"); put("kid", kid); put("use", "sig"); put("alg", "RS256")
                put("n", b64(pub.modulus)); put("e", b64(pub.publicExponent))
            }
        }
    }.toString()
    fun issue(claims: JsonObject): String =
        jwt(buildJsonObject { put("alg", "RS256"); put("typ", "JWT"); put("kid", current) }, claims) { rs256(keys.getValue(current).private, it) }
}

sealed interface Verdict {
    data class Valid(val subject: String) : Verdict
    data class Rejected(val reason: String) : Verdict
}

// The resource server holds no secret at all: only a cache of public keys, refreshed from the JWKS URL.
class JwksVerifier(private val fetchJwks: () -> String, private val now: () -> Long,
                   private val issuer: String, private val audience: String) {
    private var keys = mapOf<String, PublicKey>()
    private var fetchedAt = Long.MIN_VALUE / 2
    var fetches = 0; private set

    private fun refresh() {
        if (now() - fetchedAt < 60) return                     // at most one fetch a minute: made-up kids can't flood the AS
        keys = Json.parseToJsonElement(fetchJwks()).jsonObject["keys"]!!.jsonArray.associate { jwk ->
            val k = jwk.jsonObject.mapValues { it.value.jsonPrimitive.content }
            val spec = RSAPublicKeySpec(BigInteger(1, unb64.decode(k["n"])), BigInteger(1, unb64.decode(k["e"])))
            k.getValue("kid") to KeyFactory.getInstance("RSA").generatePublic(spec)
        }
        fetchedAt = now(); fetches++
    }

    private fun json(part: String): JsonObject? =
        runCatching { Json.parseToJsonElement(unb64.decode(part).decodeToString()).jsonObject }.getOrNull()

    fun verify(token: String): Verdict {
        val parts = token.split(".")
        if (parts.size != 3) return Verdict.Rejected("not three parts")
        val header = json(parts[0]) ?: return Verdict.Rejected("header is not JSON")
        if (header["alg"]?.jsonPrimitive?.content != "RS256") return Verdict.Rejected("alg must be RS256")   // still our choice
        val kid = header["kid"]?.jsonPrimitive?.content ?: return Verdict.Rejected("no kid")
        if (now() - fetchedAt >= 300) refresh()                // cache for 5 minutes, like Spring's default
        val key = keys[kid] ?: run { refresh(); keys[kid] } ?: return Verdict.Rejected("unknown kid")
        val ok = runCatching {
            Signature.getInstance("SHA256withRSA").run { initVerify(key); update("${parts[0]}.${parts[1]}".toByteArray()); verify(unb64.decode(parts[2])) }
        }.getOrDefault(false)
        if (!ok) return Verdict.Rejected("bad signature")
        val c = json(parts[1]) ?: return Verdict.Rejected("payload is not JSON")   // then Part 2's claim checks, unchanged
        val exp = c["exp"]?.jsonPrimitive?.longOrNull ?: return Verdict.Rejected("no exp")
        if (now() >= exp + 60) return Verdict.Rejected("expired")
        if (c["iss"]?.jsonPrimitive?.content != issuer) return Verdict.Rejected("wrong issuer")
        if (c["aud"]?.jsonPrimitive?.content != audience) return Verdict.Rejected("wrong audience")
        return Verdict.Valid(c["sub"]?.jsonPrimitive?.content ?: return Verdict.Rejected("no subject"))
    }
}

fun main() {
    var clock = 1_790_000_000L
    val claims = buildJsonObject { put("iss", "https://as.example"); put("sub", "ana"); put("aud", "photos-api"); put("exp", clock + 3600) }
    val asrv = AuthServer().apply { rotate("2026-11-a") }
    val api = JwksVerifier(asrv::jwks, { clock }, "https://as.example", "photos-api")
    fun show(what: String, token: String) = println("%-33s -> %-35s fetches: %d".format(what, api.verify(token), api.fetches))

    println("JWKS: " + asrv.jwks().replace(Regex("\"n\":\"(.{8})[^\"]*\""), "\"n\":\"$1…\""))
    val old = asrv.issue(claims)
    show("token signed with 2026-11-a", old)
    asrv.rotate("2026-12-b"); clock += 120
    println("-- the AS publishes 2026-12-b and signs with it")
    show("token signed with 2026-12-b", asrv.issue(claims))
    show("older token, 2026-11-a", old)
    val attacker = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    val rsHeader = buildJsonObject { put("alg", "RS256"); put("typ", "JWT"); put("kid", "2026-12-b") }
    show("forged: attacker's key, real kid", jwt(rsHeader, claims) { rs256(attacker.private, it) })
    val hsHeader = buildJsonObject { put("alg", "HS256"); put("kid", "2026-12-b") }
    val publicBytes = asrv.publicKey("2026-12-b").encoded       // RFC 8725 §2.1: the public key as an HMAC secret
    show("forged: HS256 with the public key", jwt(hsHeader, claims) {
        Mac.getInstance("HmacSHA256").run { init(SecretKeySpec(publicBytes, "HmacSHA256")); doFinal(it.toByteArray()) } })
    val madeUp = buildJsonObject { put("alg", "RS256"); put("kid", "made-up") }
    show("forged: a kid nobody published", jwt(madeUp, claims) { rs256(attacker.private, it) })
    asrv.retire("2026-11-a"); clock += 360
    println("-- the AS retires 2026-11-a; 6 minutes pass")
    show("older token, 2026-11-a", old)
}
