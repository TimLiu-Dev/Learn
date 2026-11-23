import com.sun.net.httpserver.HttpServer
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// Server-Sent Events: a server that streams "tokens", and a client that prints each line as it arrives.
// java -jar sse.jar          -> flushes after every event
// java -jar sse.jar noflush  -> forgets to flush
fun main(args: Array<String>) {
    val flush = args.firstOrNull() != "noflush"
    fun OutputStream.event(s: String) {
        write(s.encodeToByteArray())
        if (flush) flush()                                   // push this chunk onto the wire now
    }
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/stream") { ex ->
        ex.responseHeaders["Content-Type"] = "text/event-stream; charset=utf-8"
        ex.responseHeaders["Cache-Control"] = "no-cache"
        ex.sendResponseHeaders(200, 0)                       // 0 = length unknown: chunked encoding
        ex.responseBody.use { out ->
            out.event(": stream open\n\n")                   // a comment: clients ignore it
            for (token in listOf("Hel", "lo, ", "Ada")) {
                Thread.sleep(300)                            // pretend the model is thinking
                out.event("event: delta\ndata: $token\n\n")
            }
            out.event("event: done\ndata: 3 tokens\n\n")
        }                                                    // closing ends the chunked body
    }
    server.start()
    try {
        val client = HttpClient.newHttpClient()
        val uri = URI("http://127.0.0.1:${server.address.port}/stream")
        val start = System.nanoTime()
        val resp = client.send(
            HttpRequest.newBuilder(uri).header("Accept", "text/event-stream").build(),
            HttpResponse.BodyHandlers.ofLines())             // returns before the body has finished
        println("${resp.statusCode()} ${resp.headers().firstValue("content-type").orElse("")}" +
            " | transfer-encoding: ${resp.headers().firstValue("transfer-encoding").orElse("-")}")
        resp.body().use { lines ->
            lines.forEach { line ->
                println("+%4d ms  %s".format((System.nanoTime() - start) / 1_000_000, line.ifEmpty { "(blank line)" }))
            }
        }
    } finally {
        server.stop(0)
    }
}
