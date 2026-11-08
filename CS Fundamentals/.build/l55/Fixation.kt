import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap

// Session fixation: the attacker doesn't steal your session id. They give you one, and wait.
val rng = SecureRandom()
val sessions = ConcurrentHashMap<String, String>()          // id -> user name, or "" before login
var rotateOnLogin = false

fun newId(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also { rng.nextBytes(it) })
fun cookieId(ex: HttpExchange) = ex.requestHeaders.getFirst("Cookie")?.substringAfter("sid=")?.substringBefore(";")

fun reply(ex: HttpExchange, status: Int, body: String) {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

fun setCookie(ex: HttpExchange, id: String) =
    ex.responseHeaders.add("Set-Cookie", "sid=$id; Secure; HttpOnly; SameSite=Lax; Path=/")

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/shop") { ex ->                    // anyone browsing gets an anonymous session (a cart)
        val id = newId().also { sessions[it] = "" }
        setCookie(ex, id)
        reply(ex, 200, "welcome")
    }
    server.createContext("/login") { ex ->                   // password check skipped here: see Sessions
        val old = cookieId(ex)
        if (!rotateOnLogin && old != null && sessions.containsKey(old)) {   // not `old in sessions`: see L32
            sessions[old] = "ana"                            // upgrade the session the browser already had
        } else {
            old?.let { sessions.remove(it) }                 // privilege changed: throw the old id away
            setCookie(ex, newId().also { sessions[it] = "ana" })
        }
        reply(ex, 200, "logged in")
    }
    server.createContext("/me") { ex ->
        val user = cookieId(ex)?.let { sessions[it] }
        if (user.isNullOrEmpty()) reply(ex, 401, "please log in") else reply(ex, 200, "you are $user")
    }
    server.start()
    val base = "http://127.0.0.1:${server.address.port}"
    val client = HttpClient.newHttpClient()
    fun call(path: String, cookie: String?, post: Boolean = false): HttpResponse<String> {
        val req = HttpRequest.newBuilder(URI("$base$path"))
        cookie?.let { req.header("Cookie", "sid=$it") }
        if (post) req.POST(HttpRequest.BodyPublishers.noBody())
        return client.send(req.build(), HttpResponse.BodyHandlers.ofString())
    }
    try {
        for (rotate in listOf(false, true)) {
            rotateOnLogin = rotate
            println(if (rotate) "-- fixed: new id on login --" else "-- vulnerable: same id before and after login --")
            val planted = call("/shop", null).headers().firstValue("Set-Cookie").get()
                .substringAfter("sid=").substringBefore(";")
            println("attacker gets an anonymous id and plants it in Ana's browser: ${planted.take(8)}…")
            val login = call("/login", planted, post = true)
            val anaId = login.headers().firstValue("Set-Cookie").map { it.substringAfter("sid=").substringBefore(";") }
                .orElse(planted)
            println("Ana logs in; her id is now ${anaId.take(8)}…")
            println("attacker, using the planted id: ${call("/me", planted).let { "${it.statusCode()} ${it.body()}" }}")
            println("Ana, using her id:              ${call("/me", anaId).let { "${it.statusCode()} ${it.body()}" }}")
        }
    } finally {
        server.stop(0)
    }
}
