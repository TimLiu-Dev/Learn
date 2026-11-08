import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// ---- passwords: L33's PBKDF2, compressed ----
val rng = SecureRandom()
fun pbkdf2(pw: String, salt: ByteArray): ByteArray =
    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        .generateSecret(PBEKeySpec(pw.toCharArray(), salt, 600_000, 256)).encoded
class Hash(val salt: ByteArray, val hash: ByteArray)
fun hashOf(pw: String) = ByteArray(16).also { rng.nextBytes(it) }.let { Hash(it, pbkdf2(pw, it)) }
fun Hash.matches(pw: String) = MessageDigest.isEqual(pbkdf2(pw, salt), hash)

val users = mapOf("ana" to hashOf("correct horse"))
val dummy = hashOf("no such user")          // checked when the name is unknown, so both failures cost the same

// ---- sessions: the browser holds a random id, the server holds what it means ----
val sessions = ConcurrentHashMap<String, String>()                  // session id -> user name
fun newSessionId(): String = Base64.getUrlEncoder().withoutPadding()
    .encodeToString(ByteArray(32).also { rng.nextBytes(it) })      // 256 bits from the OS's CSPRNG
const val COOKIE = "__Host-sid"

fun sessionUser(ex: HttpExchange): String? =
    ex.requestHeaders.getFirst("Cookie")?.split(";")?.map { it.trim() }
        ?.firstOrNull { it.startsWith("$COOKIE=") }?.substringAfter("=")?.let { sessions[it] }

fun reply(ex: HttpExchange, status: Int, body: String = "") {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, if (bytes.isEmpty()) -1 else bytes.size.toLong())
    if (bytes.isNotEmpty()) ex.responseBody.use { it.write(bytes) }
    ex.close()
}

fun form(ex: HttpExchange): Map<String, String> =
    ex.requestBody.readAllBytes().decodeToString().split("&").filter { "=" in it }
        .associate { it.substringBefore("=") to java.net.URLDecoder.decode(it.substringAfter("="), "UTF-8") }

fun login(ex: HttpExchange, quickExit: Boolean) {
    val f = form(ex)
    val stored = users[f["name"]]
    if (quickExit && stored == null) return reply(ex, 401, "invalid name or password")  // OWASP's "quick exit" leak
    val ok = (stored ?: dummy).matches(f["password"].orEmpty()) && stored != null
    if (!ok) return reply(ex, 401, "invalid name or password")     // same words for both failures
    val sid = newSessionId()
    sessions[sid] = f.getValue("name")
    ex.responseHeaders.add("Set-Cookie", "$COOKIE=$sid; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=1800")
    reply(ex, 204)
}

fun show(label: String, result: String) = println(label.padEnd(28) + "-> " + result)

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/login") { login(it, quickExit = false) }
    server.createContext("/login-quick") { login(it, quickExit = true) }
    server.createContext("/me") { ex ->
        sessionUser(ex)?.let { reply(ex, 200, "you are $it") } ?: reply(ex, 401, "please log in")
    }
    server.createContext("/logout") { ex ->
        ex.requestHeaders.getFirst("Cookie")?.substringAfter("$COOKIE=")?.substringBefore(";")?.let { sessions.remove(it) }
        ex.responseHeaders.add("Set-Cookie", "$COOKIE=; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=0")
        reply(ex, 204)
    }
    server.start()
    val base = "http://127.0.0.1:${server.address.port}"
    val client = HttpClient.newHttpClient()                         // no cookie jar: we handle the header, like curl
    fun post(path: String, vararg kv: Pair<String, String>): HttpResponse<String> {
        val body = kv.joinToString("&") { (k, v) -> "$k=" + URLEncoder.encode(v, "UTF-8") }
        val req = HttpRequest.newBuilder(URI("$base$path")).header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body)).build()
        return client.send(req, HttpResponse.BodyHandlers.ofString())
    }
    fun me(cookie: String?): String {
        val req = HttpRequest.newBuilder(URI("$base/me"))
        cookie?.let { req.header("Cookie", it) }
        return client.send(req.build(), HttpResponse.BodyHandlers.ofString()).let { "${it.statusCode()} ${it.body()}" }
    }
    fun timed(path: String, name: String, pw: String): String {
        repeat(3) { post(path, "name" to name, "password" to pw) }   // warm up (L16)
        val t0 = System.nanoTime()
        val r = post(path, "name" to name, "password" to pw)
        return "${r.statusCode()} ${r.body()}  (${(System.nanoTime() - t0) / 1_000_000} ms)"
    }
    try {
        show("wrong password", timed("/login", "ana", "wrong"))
        show("unknown user", timed("/login", "zed", "wrong"))
        show("unknown user, quick exit", timed("/login-quick", "zed", "wrong"))
        val ok = post("/login", "name" to "ana", "password" to "correct horse")
        val setCookie = ok.headers().firstValue("Set-Cookie").get()
        show("right password", "${ok.statusCode()}")
        println("  Set-Cookie: ${setCookie.take(24)}…${setCookie.substringAfter(";")}")
        val cookie = setCookie.substringBefore(";")                 // what a browser sends back
        show("GET /me with the cookie", me(cookie))
        show("GET /me without it", me(null))
        show("GET /me with a guessed id", me("$COOKIE=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
        val out = client.send(HttpRequest.newBuilder(URI("$base/logout")).header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString())
        show("POST /logout", "${out.statusCode()}, Set-Cookie: …" + out.headers().firstValue("Set-Cookie").get().substringAfterLast(";"))
        show("GET /me with the old cookie", me(cookie))
        println("sessions left on the server: ${sessions.size}")
    } finally {
        server.stop(0)
    }
}
