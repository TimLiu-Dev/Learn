import java.security.MessageDigest

const val OUR_ORIGIN = "https://books.example"
val SAFE_METHODS = setOf("GET", "HEAD", "OPTIONS")

data class Request(val method: String, val origin: String?, val csrfHeader: String?)

fun tokensMatch(expected: String, presented: String?): Boolean =
    presented != null && MessageDigest.isEqual(expected.toByteArray(), presented.toByteArray())

fun csrfAllowed(req: Request, sessionToken: String): Boolean {
    if (req.method in SAFE_METHODS) return true
    return req.origin?.startsWith(OUR_ORIGIN) == true && tokensMatch(sessionToken, req.csrfHeader)
}
