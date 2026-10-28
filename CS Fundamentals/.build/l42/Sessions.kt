import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Version 1: each server remembers sessions in its own memory.
class StatefulServer(val name: String) {
    private val sessions = ConcurrentHashMap<String, String>()    // session id -> user
    val server: HttpServer = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50)
    init {
        server.createContext("/login") { ex ->
            val id = UUID.randomUUID().toString().take(8)
            sessions[id] = ex.requestURI.query.removePrefix("user=")
            send(ex, 200, id)
        }
        server.createContext("/me") { ex ->
            val user = sessions[ex.requestHeaders.getFirst("Session") ?: ""]
            if (user == null) send(ex, 401, "who are you? ($name has no such session)") else send(ex, 200, "hello $user (from $name)")
        }
        server.start()
    }
}

// Version 2: the server remembers nothing; the client carries a signed token (L33's HMAC).
class StatelessServer(val name: String, private val key: ByteArray) {
    val server: HttpServer = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50)
    init {
        server.createContext("/login") { ex ->
            val user = ex.requestURI.query.removePrefix("user=")
            send(ex, 200, "$user.${sign(user, key)}")
        }
        server.createContext("/me") { ex ->
            val (user, sig) = (ex.requestHeaders.getFirst("Token") ?: ".").split(".", limit = 2)
            val valid = MessageDigest.isEqual(sig.toByteArray(), sign(user, key).toByteArray())   // constant time (L33)
            if (valid) send(ex, 200, "hello $user (from $name)") else send(ex, 401, "bad signature")
        }
        server.start()
    }
}

fun sign(user: String, key: ByteArray): String {
    val mac = Mac.getInstance("HmacSHA256").apply { init(SecretKeySpec(key, "HmacSHA256")) }
    return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(user.toByteArray())).take(16)
}

fun send(ex: HttpExchange, status: Int, body: String) {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

val client: HttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()

fun call(server: HttpServer, path: String, header: Pair<String, String>? = null): String {
    val b = HttpRequest.newBuilder(URI("http://127.0.0.1:${server.address.port}$path"))
    header?.let { b.header(it.first, it.second) }
    val resp = client.send(b.build(), BodyHandlers.ofString())
    return "${resp.statusCode()} ${resp.body()}"
}

fun main() {
    println("-- stateful: sessions live in each server's memory")
    val (a, b) = StatefulServer("A") to StatefulServer("B")
    val session = call(a.server, "/login?user=ada").substringAfter(" ")
    println("login    -> A: session $session")
    println("GET /me  -> B: " + call(b.server, "/me", "Session" to session))   // the balancer picked B
    println("GET /me  -> A: " + call(a.server, "/me", "Session" to session))

    println("-- stateless: a signed token, nothing remembered")
    val key = "shared-secret-from-config".toByteArray()                           // same key on every server
    val (c, d) = StatelessServer("A", key) to StatelessServer("B", key)
    val token = call(c.server, "/login?user=ada").substringAfter(" ")
    println("login    -> A: token $token")
    println("GET /me  -> B: " + call(d.server, "/me", "Token" to token))
    println("GET /me  -> A: " + call(c.server, "/me", "Token" to token))
    println("forged   -> B: " + call(d.server, "/me", "Token" to "admin.${token.substringAfter('.')}"))
    listOf(a.server, b.server, c.server, d.server).forEach { it.stop(0) }
}
