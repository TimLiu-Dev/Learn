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
fun chain(filters: List<Filter>, handler: Handler): Handler =
    TODO("wrap the handler in each filter, so that filters[0] runs first")

// Answers 429 with Retry-After: 1 when allow(client) says no, without calling next.
class RateLimit(private val allow: (String) -> Boolean) : Filter {
    override fun filter(req: Request, next: Handler): Response =
        TODO("refuse, or pass the request on")
}

// Records one line per request it sees, once the rest of the chain has answered.
class AccessLog(val lines: MutableList<String>) : Filter {
    override fun filter(req: Request, next: Handler): Response {
        val res = next.handle(req)
        lines += "${req.client} ${req.path} ${res.status}"
        return res
    }
}
