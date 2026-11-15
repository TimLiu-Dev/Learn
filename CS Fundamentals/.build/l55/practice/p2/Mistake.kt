const val ALG = "HS256"
const val AUDIENCE = "books-api"
const val LEEWAY = 60L

// A deny-list: blocks the one bad value its author thought of.
fun headerProblem(header: Map<String, Any?>): String? =
    if (header["alg"] == "none") "alg must be HS256" else null

fun claimsProblem(claims: Map<String, Any?>, now: Long): String? {
    val exp = claims["exp"] as? Long ?: return "no exp"
    if (now >= exp + LEEWAY) return "expired"
    if (claims["aud"] != AUDIENCE) return "wrong audience"
    return null
}
