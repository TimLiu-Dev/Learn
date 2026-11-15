const val ALG = "HS256"            // the server picks the algorithm (RFC 8725 §3.1)
const val AUDIENCE = "books-api"   // this API's name: tokens for anyone else are rejected
const val LEEWAY = 60L             // seconds of clock skew allowed (RFC 7519 §4.1.4)

// An allow-list of one: anything that isn't exactly ALG, including a missing alg, is rejected.
fun headerProblem(header: Map<String, Any?>): String? =
    if (header["alg"] == ALG) null else "alg must be HS256"

fun claimsProblem(claims: Map<String, Any?>, now: Long): String? {
    val exp = claims["exp"] as? Long ?: return "no exp"   // a string "exp" is not a number: no coercion
    if (now >= exp + LEEWAY) return "expired"
    if (claims["aud"] != AUDIENCE) return "wrong audience"
    return null
}
