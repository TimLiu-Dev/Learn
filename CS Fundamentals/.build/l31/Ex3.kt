import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Exercise 3: validate with Last-Modified / If-Modified-Since instead of an ETag.
val HTTP_DATE: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME   // "Tue, 20 Oct 2026 09:00:00 GMT"
val LAST_MODIFIED: ZonedDateTime = ZonedDateTime.parse("Tue, 20 Oct 2026 09:00:00 GMT", HTTP_DATE)

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/notes") { ex ->
        val ims = ex.requestHeaders.getFirst("If-Modified-Since")
        ex.responseHeaders["Last-Modified"] = HTTP_DATE.format(LAST_MODIFIED)
        ex.responseHeaders["Cache-Control"] = "no-cache"
        val unchanged = ims != null && !LAST_MODIFIED.isAfter(ZonedDateTime.parse(ims, HTTP_DATE))
        if (unchanged) { ex.sendResponseHeaders(304, -1); ex.close(); return@createContext }
        val body = "meeting notes, v3".encodeToByteArray()
        ex.sendResponseHeaders(200, body.size.toLong())
        ex.responseBody.use { it.write(body) }
    }
    server.start()
    try {
        val client = HttpClient.newHttpClient()
        val url = URI.create("http://127.0.0.1:${server.address.port}/notes")
        val first = client.send(HttpRequest.newBuilder(url).build(), HttpResponse.BodyHandlers.ofString())
        val lm = first.headers().firstValue("Last-Modified").orElseThrow()
        println("1st: ${first.statusCode()}  Last-Modified: $lm  body: ${first.body()}")
        for (since in listOf(lm, "Mon, 19 Oct 2026 09:00:00 GMT")) {
            val r = client.send(HttpRequest.newBuilder(url).header("If-Modified-Since", since).build(),
                HttpResponse.BodyHandlers.ofString())
            println("If-Modified-Since $since -> ${r.statusCode()}" +
                if (r.body().isEmpty()) "  (no body)" else "  body: ${r.body()}")
        }
    } finally {
        server.stop(0)
    }
}
