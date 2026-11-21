data class Request(val client: String, val path: String)
data class Response(val status: Int, val headers: Map<String, String> = emptyMap())

fun interface Handler {
    fun handle(req: Request): Response
}

// Like HttpServer's Filter: do something, then call next (or don't), then maybe do more.
fun interface Filter {
    fun filter(req: Request, next: Handler): Response
}

// Filters run in the order given: the first one wraps all the others, and the handler.
// Build from the inside out: the last filter wraps the handler, the first one wraps everything.
fun chain(filters: List<Filter>, handler: Handler): Handler =
    filters.foldRight(handler) { f, next -> Handler { req -> f.filter(req, next) } }

// Answers 429 with Retry-After: 1 when allow(client) says no, without calling next.
class RateLimit(private val allow: (String) -> Boolean) : Filter {
    override fun filter(req: Request, next: Handler): Response =
        if (allow(req.client)) next.handle(req)
        else Response(429, mapOf("Retry-After" to "1"))   // the chain stops here
}

// Records one line per request it sees, once the rest of the chain has answered.
class AccessLog(val lines: MutableList<String>) : Filter {
    override fun filter(req: Request, next: Handler): Response {
        val res = next.handle(req)
        lines += "${req.client} ${req.path} ${res.status}"
        return res
    }
}
