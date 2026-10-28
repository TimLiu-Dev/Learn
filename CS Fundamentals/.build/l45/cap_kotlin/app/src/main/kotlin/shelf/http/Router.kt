package shelf.http

import com.sun.net.httpserver.HttpExchange
import java.net.URLDecoder
import kotlinx.serialization.SerializationException

class Request(private val exchange: HttpExchange, val params: Map<String, String>) {
    val method: String get() = exchange.requestMethod
    val path: String get() = exchange.requestURI.path
    val query: Map<String, String> by lazy { parseQuery(exchange.requestURI.rawQuery) }

    fun body(): String = exchange.requestBody.readAllBytes().decodeToString()

    // "/books/abc" names no book, so a bad id is a 404, not a 400.
    fun id(name: String = "id"): Long = params.getValue(name).toLongOrNull() ?: throw ApiError.notFound(path)

    fun intQuery(name: String, default: Int, range: IntRange): Int {
        val raw = query[name] ?: return default
        return raw.toIntOrNull()?.takeIf { it in range }
            ?: throw ApiError.invalid(listOf("$name must be an integer in ${range.first}..${range.last}, got '$raw'"))
    }

    private fun parseQuery(raw: String?): Map<String, String> =
        raw.orEmpty().split('&').filter { '=' in it }.associate {
            URLDecoder.decode(it.substringBefore('='), Charsets.UTF_8) to URLDecoder.decode(it.substringAfter('='), Charsets.UTF_8)
        }
}

data class Response(
    val status: Int,
    val body: String? = null,
    val type: String = "application/json",
    val headers: Map<String, String> = emptyMap(),
)

typealias Handler = (Request) -> Response

sealed interface Match {
    data class Found(val handler: Handler, val params: Map<String, String>) : Match
    data class WrongMethod(val allowed: List<String>) : Match
    data object NoRoute : Match
}

class Router {
    private class Route(val method: String, val segments: List<String>, val handler: Handler)

    private val routes = mutableListOf<Route>()

    fun get(path: String, handler: Handler) = add("GET", path, handler)
    fun post(path: String, handler: Handler) = add("POST", path, handler)
    fun put(path: String, handler: Handler) = add("PUT", path, handler)
    fun delete(path: String, handler: Handler) = add("DELETE", path, handler)

    private fun add(method: String, path: String, handler: Handler) {
        routes += Route(method, segments(path), handler)
    }

    // Pure function of (method, path), so it's easy to unit-test without a server.
    fun match(method: String, path: String): Match {
        val parts = segments(path)
        val hits = routes.mapNotNull { route -> bind(route.segments, parts)?.let { route to it } }
        if (hits.isEmpty()) return Match.NoRoute
        val (route, params) = hits.firstOrNull { it.first.method == method }
            ?: return Match.WrongMethod(hits.map { it.first.method }.distinct().sorted())
        return Match.Found(route.handler, params)
    }

    // "/books/{id}" against "/books/7" gives {id=7}; null if the shapes differ.
    private fun bind(template: List<String>, parts: List<String>): Map<String, String>? {
        if (template.size != parts.size) return null
        val params = mutableMapOf<String, String>()
        for ((t, p) in template.zip(parts)) {
            if (t.startsWith('{') && t.endsWith('}')) params[t.substring(1, t.length - 1)] = p
            else if (t != p) return null
        }
        return params
    }

    private fun segments(path: String) = path.split('/').filter { it.isNotEmpty() }

    // HttpServer calls this on a virtual thread for every request.
    fun handle(exchange: HttpExchange) = exchange.use {
        val path = exchange.requestURI.path
        val response = try {
            when (val m = match(exchange.requestMethod, path)) {
                is Match.Found -> m.handler(Request(exchange, m.params))
                is Match.WrongMethod -> problem(                       // RFC 9110: a 405 MUST send Allow
                    ApiError(405, "Method Not Allowed", "${exchange.requestMethod} is not supported on $path"),
                    mapOf("Allow" to m.allowed.joinToString(", ")),
                )
                Match.NoRoute -> problem(ApiError.notFound(path))
            }
        } catch (e: ApiError) {
            problem(e)
        } catch (e: SerializationException) {                        // malformed JSON or wrong types
            problem(ApiError(400, "Bad Request", e.message?.lineSequence()?.first() ?: "Malformed JSON"))
        } catch (e: Exception) {                                      // log the details, never send them (L33)
            System.err.println("ERROR ${exchange.requestMethod} $path: $e")
            problem(ApiError(500, "Internal Server Error", "Something went wrong"))
        }
        send(exchange, response)
    }

    private fun send(exchange: HttpExchange, response: Response) {
        response.headers.forEach { (name, value) -> exchange.responseHeaders.set(name, value) }
        if (response.body == null) {
            exchange.sendResponseHeaders(response.status, -1)          // -1: no body at all (204)
            return
        }
        val bytes = response.body.encodeToByteArray()
        exchange.responseHeaders.set("Content-Type", response.type)
        exchange.sendResponseHeaders(response.status, bytes.size.toLong())
        exchange.responseBody.write(bytes)
    }
}
