import java.security.MessageDigest

const val OUR_ORIGIN = "https://books.example"

data class Request(val method: String, val origin: String?, val csrfHeader: String?)

// Is the presented token the session's token? Constant time, and null never matches.
fun tokensMatch(expected: String, presented: String?): Boolean =
    TODO("null is false; otherwise compare the bytes with MessageDigest.isEqual")

// The gate every request passes before its handler runs.
fun csrfAllowed(req: Request, sessionToken: String): Boolean =
    TODO("safe methods pass; everything else needs our exact Origin and a matching token")
