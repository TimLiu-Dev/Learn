import java.security.MessageDigest

const val OUR_ORIGIN = "https://books.example"
val SAFE_METHODS = setOf("GET", "HEAD", "OPTIONS")

data class Request(val method: String, val origin: String?, val csrfHeader: String?)

// isEqual looks at every byte whatever it finds, so the time taken doesn't reveal the matching prefix.
fun tokensMatch(expected: String, presented: String?): Boolean =
    presented != null && MessageDigest.isEqual(expected.toByteArray(), presented.toByteArray())

fun csrfAllowed(req: Request, sessionToken: String): Boolean {
    if (req.method in SAFE_METHODS) return true          // safe methods must never change state (L32)
    // Unsafe or unknown: deny unless both checks pass. "Origin: null" and a missing Origin both fail ==.
    return req.origin == OUR_ORIGIN && tokensMatch(sessionToken, req.csrfHeader)
}
