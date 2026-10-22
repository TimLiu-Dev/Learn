package books

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class BookApi(port: Int = 0) {
    private val json = Json { explicitNulls = false }
    private val books = ConcurrentHashMap<Long, Book>()          // L27: safe for concurrent handlers
    private val nextId = AtomicLong(1)
    private val pool = Executors.newVirtualThreadPerTaskExecutor()   // L28: one virtual thread per request
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0)

    init {
        server.createContext("/books") { exchange -> exchange.use { handle(it) } }
        server.executor = pool
    }

    val port: Int get() = server.address.port
    fun start() = server.start()
    fun stop() {
        server.stop(0)
        pool.close()
    }

    // ---- the "framework": catch-all error handling + routing by hand ----

    private fun handle(ex: HttpExchange) {
        try {
            route(ex)
        } catch (e: SerializationException) {                     // malformed JSON or wrong types
            problem(ex, 400, "about:blank", "Bad Request", e.message?.lines()?.first() ?: "bad JSON")
        } catch (e: Exception) {
            problem(ex, 500, "about:blank", "Internal Server Error", e.toString())
        }
    }

    private fun route(ex: HttpExchange) {
        val parts = ex.requestURI.path.trim('/').split('/')         // "/books/7" -> [books, 7]
        if (parts[0] != "books") return notFound(ex)                // the context also matches "/booksX"
        when (parts.size) {
            1 -> when (ex.requestMethod) {
                "GET" -> list(ex)
                "POST" -> create(ex)
                else -> notAllowed(ex, "GET, POST")
            }
            2 -> {
                val id = parts[1].toLongOrNull() ?: return notFound(ex)
                when (ex.requestMethod) {
                    "GET" -> books[id]?.let { send(ex, 200, json.encodeToString(it)) } ?: notFound(ex)
                    "PUT" -> replace(ex, id)
                    "DELETE" -> if (books.remove(id) != null) send(ex, 204, null) else notFound(ex)
                    else -> notAllowed(ex, "GET, PUT, DELETE")
                }
            }
            else -> notFound(ex)
        }
    }

    // ---- handlers ----

    private fun list(ex: HttpExchange) {
        val q = ex.requestURI.query.orEmpty().split('&').filter { '=' in it }
            .associate { it.substringBefore('=') to it.substringAfter('=') }
        val errors = mutableListOf<String>()
        fun intParam(name: String, default: Int, rule: String, ok: (Int) -> Boolean): Int {
            val raw = q[name] ?: return default
            raw.toIntOrNull()?.let { if (ok(it)) return it }
            errors += "$name must be $rule, got '$raw'"
            return default
        }
        val limit = intParam("limit", 10, "1..100") { it in 1..100 }
        val offset = intParam("offset", 0, ">= 0") { it >= 0 }
        if (errors.isNotEmpty()) return invalid(ex, "Invalid query", errors)

        val all = books.values.sortedBy { it.id }
        val next = if (offset + limit < all.size) "/books?limit=$limit&offset=${offset + limit}" else null
        send(ex, 200, json.encodeToString(Page(all.drop(offset).take(limit), limit, offset, all.size, next)))
    }

    private fun create(ex: HttpExchange) {
        val input = json.decodeFromString<NewBook>(ex.requestBody.readAllBytes().decodeToString())
        val errors = validate(input)
        if (errors.isNotEmpty()) return invalid(ex, "Invalid book", errors)
        val book = Book(nextId.getAndIncrement(), input.title!!.trim(), input.year!!)
        books[book.id] = book
        ex.responseHeaders.set("Location", "/books/${book.id}")
        send(ex, 201, json.encodeToString(book))
    }

    private fun replace(ex: HttpExchange, id: Long) {
        if (!books.containsKey(id)) return notFound(ex)   // not `id in books`: see the callout
        val input = json.decodeFromString<NewBook>(ex.requestBody.readAllBytes().decodeToString())
        val errors = validate(input)
        if (errors.isNotEmpty()) return invalid(ex, "Invalid book", errors)
        val book = Book(id, input.title!!.trim(), input.year!!)
        books[id] = book
        send(ex, 200, json.encodeToString(book))
    }

    // ---- responses ----

    private fun notFound(ex: HttpExchange) =
        problem(ex, 404, "about:blank", "Not Found", "No resource at ${ex.requestURI.path}")

    private fun notAllowed(ex: HttpExchange, allow: String) {
        ex.responseHeaders.set("Allow", allow)                      // RFC 9110: a 405 MUST list them
        problem(ex, 405, "about:blank", "Method Not Allowed", "${ex.requestMethod} is not supported here")
    }

    private fun invalid(ex: HttpExchange, title: String, errors: List<String>) =
        problem(ex, 400, "https://example.com/problems/validation", title,
                "${errors.size} problem(s) with the request", errors)

    private fun problem(ex: HttpExchange, status: Int, type: String, title: String, detail: String,
                        errors: List<String>? = null) =
        send(ex, status, json.encodeToString(Problem(type, title, status, detail, errors)), "application/problem+json")

    private fun send(ex: HttpExchange, status: Int, body: String?, type: String = "application/json") {
        println("${ex.requestMethod} ${ex.requestURI} -> $status")
        if (body == null) {
            ex.sendResponseHeaders(status, -1)                          // -1: no body at all
            return
        }
        val bytes = body.encodeToByteArray()
        ex.responseHeaders.set("Content-Type", type)
        ex.sendResponseHeaders(status, bytes.size.toLong())
        ex.responseBody.use { it.write(bytes) }
    }
}
