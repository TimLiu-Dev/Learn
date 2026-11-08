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

class BookApi(private val auth: Auth, port: Int = 0) {
    private val json = Json { explicitNulls = false }
    private val books = ConcurrentHashMap<Long, Book>()          // L27: safe for concurrent handlers
    private val nextId = AtomicLong(1)
    private val pool = Executors.newVirtualThreadPerTaskExecutor()   // L28: one virtual thread per request
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0)

    init {
        server.createContext("/books") { exchange -> exchange.use { handle(it, ::route) } }
        server.createContext("/login") { exchange -> exchange.use { handle(it, ::login) } }
        server.createContext("/logout") { exchange -> exchange.use { handle(it, ::logout) } }
        server.executor = pool
    }

    val port: Int get() = server.address.port
    fun start() = server.start()
    fun stop() {
        server.stop(0)
        pool.close()
    }

    // ---- the "framework": catch-all error handling ----

    private fun handle(ex: HttpExchange, handler: (HttpExchange) -> Unit) {
        try {
            handler(ex)
        } catch (e: SerializationException) {
            problem(ex, 400, "Bad Request", e.message?.lines()?.first() ?: "bad JSON")
        } catch (e: Exception) {
            System.err.println("500 for ${ex.requestURI}: $e")              // details to the log, not the client (L33)
            problem(ex, 500, "Internal Server Error", "something went wrong")
        }
    }

    // ---- authentication ----

    private fun sessionId(ex: HttpExchange): String? =
        ex.requestHeaders.getFirst("Cookie")?.split(";")?.map { it.trim() }
            ?.firstOrNull { it.startsWith("${Auth.COOKIE}=") }?.substringAfter("=")

    private fun login(ex: HttpExchange) {
        if (ex.requestMethod != "POST") return notAllowed(ex, "POST")
        val input = json.decodeFromString<Login>(ex.requestBody.readAllBytes().decodeToString())
        val (id, session) = auth.login(input.name, input.password)
            ?: return problem(ex, 401, "Unauthorized", "invalid name or password")   // same words either way
        ex.responseHeaders.add("Set-Cookie", Auth.cookie(id))
        send(ex, 200, json.encodeToString(LoggedIn(session.user, session.csrf)))
    }

    private fun logout(ex: HttpExchange) {
        if (ex.requestMethod != "POST") return notAllowed(ex, "POST")
        auth.logout(sessionId(ex))                                       // forget it on the server
        ex.responseHeaders.add("Set-Cookie", Auth.CLEAR)                 // and ask the browser to drop it
        send(ex, 204, null)
    }

    /** The three gates every unsafe request passes, in order. Returns the session, or null after replying. */
    private fun guard(ex: HttpExchange): Auth.Session? {
        val s = auth.session(sessionId(ex))
        if (s == null) {
            // RFC 9110 §15.5.2: a 401 MUST carry WWW-Authenticate. No registered scheme fits cookies, so we name ours.
            ex.responseHeaders.set("WWW-Authenticate", "Session realm=\"books\"")
            problem(ex, 401, "Unauthorized", "log in first: POST /login")
            return null
        }
        if (!auth.csrfOk(s, ex.requestHeaders.getFirst("X-CSRF-Token"))) {
            problem(ex, 403, "Forbidden", "missing or wrong X-CSRF-Token")
            return null
        }
        return s
    }

    private fun mayChange(s: Auth.Session, book: Book) = s.admin || book.owner == s.user   // ownership (A01)

    // ---- routing by hand, as in L32 ----

    private fun route(ex: HttpExchange) {
        val parts = ex.requestURI.path.trim('/').split('/')
        if (parts[0] != "books") return notFound(ex)
        when (parts.size) {
            1 -> when (ex.requestMethod) {
                "GET" -> list(ex)                                        // the catalogue is public
                "POST" -> guard(ex)?.let { create(ex, it) }
                else -> notAllowed(ex, "GET, POST")
            }
            2 -> {
                val id = parts[1].toLongOrNull() ?: return notFound(ex)
                when (ex.requestMethod) {
                    "GET" -> books[id]?.let { send(ex, 200, json.encodeToString(it)) } ?: notFound(ex)
                    "PUT", "DELETE" -> {
                        val s = guard(ex) ?: return
                        val book = books[id] ?: return notFound(ex)
                        if (!mayChange(s, book)) return problem(ex, 403, "Forbidden", "only the owner or an admin may change this book")
                        if (ex.requestMethod == "PUT") replace(ex, book) else { books.remove(id); send(ex, 204, null) }
                    }
                    else -> notAllowed(ex, "GET, PUT, DELETE")
                }
            }
            else -> notFound(ex)
        }
    }

    // ---- handlers ----

    private fun list(ex: HttpExchange) {                                 // unchanged from L32
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
        if (errors.isNotEmpty()) return problem(ex, 400, "Invalid query", "${errors.size} problem(s) with the request", errors)
        val all = books.values.sortedBy { it.id }
        val next = if (offset + limit < all.size) "/books?limit=$limit&offset=${offset + limit}" else null
        send(ex, 200, json.encodeToString(Page(all.drop(offset).take(limit), limit, offset, all.size, next)))
    }

    private fun create(ex: HttpExchange, s: Auth.Session) {
        val input = json.decodeFromString<NewBook>(ex.requestBody.readAllBytes().decodeToString())
        val errors = validate(input)
        if (errors.isNotEmpty()) return problem(ex, 400, "Invalid book", "${errors.size} problem(s)", errors)
        val book = Book(nextId.getAndIncrement(), input.title!!.trim(), input.year!!, owner = s.user)   // owner from the session, never the body
        books[book.id] = book
        ex.responseHeaders.set("Location", "/books/${book.id}")
        send(ex, 201, json.encodeToString(book))
    }

    private fun replace(ex: HttpExchange, old: Book) {
        val input = json.decodeFromString<NewBook>(ex.requestBody.readAllBytes().decodeToString())
        val errors = validate(input)
        if (errors.isNotEmpty()) return problem(ex, 400, "Invalid book", "${errors.size} problem(s)", errors)
        val book = old.copy(title = input.title!!.trim(), year = input.year!!)   // the owner can't be changed here
        books[book.id] = book
        send(ex, 200, json.encodeToString(book))
    }

    // ---- responses ----

    private fun notFound(ex: HttpExchange) = problem(ex, 404, "Not Found", "No resource at ${ex.requestURI.path}")

    private fun notAllowed(ex: HttpExchange, allow: String) {
        ex.responseHeaders.set("Allow", allow)
        problem(ex, 405, "Method Not Allowed", "${ex.requestMethod} is not supported here")
    }

    private fun problem(ex: HttpExchange, status: Int, title: String, detail: String, errors: List<String>? = null) =
        send(ex, status, json.encodeToString(Problem("about:blank", title, status, detail, errors)), "application/problem+json")

    private fun send(ex: HttpExchange, status: Int, body: String?, type: String = "application/json") {
        if (body == null) {
            ex.sendResponseHeaders(status, -1)
            return
        }
        val bytes = body.encodeToByteArray()
        ex.responseHeaders.set("Content-Type", type)
        ex.sendResponseHeaders(status, bytes.size.toLong())
        ex.responseBody.use { it.write(bytes) }
    }
}
