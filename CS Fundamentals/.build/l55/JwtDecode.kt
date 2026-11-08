import java.time.Duration
import java.time.Instant
import java.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

// A debugging tool: show what a token SAYS. It checks nothing: decoding is not verifying.
fun decode(token: String, now: Instant) {
    val parts = token.split(".")
    require(parts.size == 3) { "a JWS has 3 parts, this has ${parts.size}" }
    val dec = Base64.getUrlDecoder()
    println("header:  " + dec.decode(parts[0]).decodeToString())
    val claims = Json.parseToJsonElement(dec.decode(parts[1]).decodeToString()).jsonObject
    for ((name, value) in claims) {
        val seconds = if (name in setOf("exp", "iat", "nbf")) value.jsonPrimitive.longOrNull else null
        val note = seconds?.let { "  = ${Instant.ofEpochSecond(it)}" }.orEmpty()
        println("  %-5s %s%s".format(name, value, note))
    }
    claims["exp"]?.jsonPrimitive?.longOrNull?.let {
        val left = Duration.between(now, Instant.ofEpochSecond(it))
        println(if (left.isNegative) "EXPIRED ${-left.toMinutes()} min ago" else "expires in ${left.toMinutes()} min")
    }
    println("signature: ${parts[2].length} chars, NOT checked (that needs the key)")
}

fun main() {
    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJpc3MiOiJodHRwczovL2Jvb2tzLmV4YW1wbGUiLCJzdWIiOiJhbmEiLCJhdWQiOiJib29rcy1hcGkiLCJpYXQiOjE3OTAwMDAwMDAsImV4cCI6MTc5MDAwMDkwMH0" +
        ".c2lnbmF0dXJlLWJ5dGVzLWdvLWhlcmU"
    decode(token, now = Instant.parse("2026-09-21T14:30:00Z"))
}
