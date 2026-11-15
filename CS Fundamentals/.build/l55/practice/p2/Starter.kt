const val ALG = "HS256"            // the server picks the algorithm (RFC 8725 §3.1)
const val AUDIENCE = "books-api"   // this API's name: tokens for anyone else are rejected
const val LEEWAY = 60L             // seconds of clock skew allowed (RFC 7519 §4.1.4)

// Checked before the signature. The token wrote this header, so it only gets compared, never obeyed.
fun headerProblem(header: Map<String, Any?>): String? =
    TODO("null if alg is exactly ALG, else \"alg must be HS256\"")

// Checked only after the signature matched. now and exp are seconds since the epoch.
fun claimsProblem(claims: Map<String, Any?>, now: Long): String? =
    TODO("exp first, then aud; null means accept")
