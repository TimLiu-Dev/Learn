import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

val tokens = mapOf("tok-ana" to "ana", "tok-ben" to "ben")  // stand-in for real sessions
val orders = mapOf("ana" to "[\"book\"]", "ben" to "[\"laptop\"]")

fun reply(ex: HttpExchange, status: Int, body: String) {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

// /<mode>/users/{name}/orders
fun handle(ex: HttpExchange, checkOwner: Boolean) {
    val caller = tokens[ex.requestHeaders.getFirst("X-Token")]
        ?: return reply(ex, 401, "who are you?")             // not authenticated
    val owner = ex.requestURI.path.split("/")[3]
    if (checkOwner && owner != caller)
        return reply(ex, 404, "not found")                  // not yours: don't even confirm it exists
    reply(ex, 200, orders[owner] ?: "[]")
}

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/unsafe/users/") { handle(it, checkOwner = false) }
    server.createContext("/safe/users/") { handle(it, checkOwner = true) }
    server.start()
    try {
        val client = HttpClient.newHttpClient()
        fun get(path: String, token: String?): String {
            val req = HttpRequest.newBuilder(URI("http://127.0.0.1:${server.address.port}$path"))
            if (token != null) req.header("X-Token", token)
            val res = client.send(req.build(), HttpResponse.BodyHandlers.ofString())
            return "${res.statusCode()} ${res.body()}"
        }
        println("ben reads his own (unsafe):  ${get("/unsafe/users/ben/orders", "tok-ben")}")
        println("ben reads ana's   (unsafe):  ${get("/unsafe/users/ana/orders", "tok-ben")}")
        println("ben reads ana's   (safe):    ${get("/safe/users/ana/orders", "tok-ben")}")
        println("ben reads his own (safe):    ${get("/safe/users/ben/orders", "tok-ben")}")
        println("no token          (safe):    ${get("/safe/users/ben/orders", null)}")
    } finally {
        server.stop(0)
    }
}
