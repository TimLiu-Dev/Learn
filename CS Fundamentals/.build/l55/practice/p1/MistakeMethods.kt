import java.security.MessageDigest

const val OUR_ORIGIN = "https://books.example"

data class Request(val method: String, val origin: String?, val csrfHeader: String?)

fun tokensMatch(expected: String, presented: String?): Boolean =
    presented != null && MessageDigest.isEqual(expected.toByteArray(), presented.toByteArray())

fun csrfAllowed(req: Request, sessionToken: String): Boolean {
    if (req.method !in setOf("POST", "PUT", "DELETE")) return true
    return req.origin == OUR_ORIGIN && tokensMatch(sessionToken, req.csrfHeader)
}
