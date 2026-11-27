import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// An SSE feed that drops the connection every 2 events, and a client that resumes with Last-Event-ID.
val MESSAGES = listOf("order placed", "payment ok", "packed", "shipped", "delivered")

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/feed") { ex ->
        val from = ex.requestHeaders.getFirst("Last-Event-ID")?.toInt() ?: 0   // ids are 1-based
        ex.responseHeaders["Content-Type"] = "text/event-stream; charset=utf-8"
        ex.sendResponseHeaders(200, 0)
        ex.responseBody.use { out ->
            val ids = (from + 1..MESSAGES.size).take(2)
            for (id in ids) {
                out.write("id: $id\ndata: ${MESSAGES[id - 1]}\n\n".encodeToByteArray())
                out.flush()
            }
            if (from + ids.size == MESSAGES.size) out.write("event: done\ndata: end\n\n".encodeToByteArray())
        }                                                                   // the "drop": just close
    }
    server.start()
    try {
        val client = HttpClient.newHttpClient()
        val uri = URI("http://127.0.0.1:${server.address.port}/feed")
        var lastId: String? = null
        var done = false
        var attempt = 0
        while (!done && attempt < 10) {
            attempt++
            println("connect #$attempt (Last-Event-ID: ${lastId ?: "none"})")
            val req = HttpRequest.newBuilder(uri).header("Accept", "text/event-stream")
            lastId?.let { req.header("Last-Event-ID", it) }
            val resp = client.send(req.build(), HttpResponse.BodyHandlers.ofLines())
            var id: String? = null
            var event = "message"
            var data: String? = null
            resp.body().use { body ->
                for (line in body.iterator()) {
                    when {
                        line.startsWith("id:") -> id = line.substringAfter(':').trim()
                        line.startsWith("event:") -> event = line.substringAfter(':').trim()
                        line.startsWith("data:") -> data = line.substringAfter(':').trim()
                        line.isEmpty() -> {                                 // a blank line ends the event
                            if (id != null) lastId = id
                            println("  $event${id?.let { " #$it" } ?: ""}: $data")
                            if (event == "done") done = true
                            id = null; event = "message"; data = null
                        }
                    }
                }
            }                                                               // stream ended: reconnect
        }
    } finally {
        server.stop(0)
    }
}
