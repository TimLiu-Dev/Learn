import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// The five characters OWASP says to encode in HTML body text
fun escapeHtml(s: String): String = buildString {
    for (c in s) append(when (c) {
        '&' -> "&amp;"; '<' -> "&lt;"; '>' -> "&gt;"; '"' -> "&quot;"; '\'' -> "&#x27;"
        else -> c
    })
}

fun nameParam(ex: HttpExchange): String =
    URLDecoder.decode(ex.requestURI.rawQuery?.removePrefix("name=") ?: "", Charsets.UTF_8)

fun send(ex: HttpExchange, html: String) {
    val bytes = html.toByteArray()
    ex.responseHeaders["Content-Type"] = "text/html; charset=utf-8"
    ex.sendResponseHeaders(200, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)  // port 0: any free port
    server.createContext("/unsafe") { ex -> send(ex, "<p>Thanks for signing, ${nameParam(ex)}!</p>") }
    server.createContext("/safe") { ex ->
        ex.responseHeaders["Content-Security-Policy"] = "default-src 'self'"
        send(ex, "<p>Thanks for signing, ${escapeHtml(nameParam(ex))}!</p>")
    }
    server.start()
    try {
        val client = HttpClient.newHttpClient()
        val input = URLEncoder.encode("<script>alert('hi')</script>", Charsets.UTF_8)
        for (path in listOf("unsafe", "safe")) {
            val uri = URI("http://127.0.0.1:${server.address.port}/$path?name=$input")
            val res = client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString())
            println("/$path -> ${res.body()}")
        }
    } finally {
        server.stop(0)
    }
}
