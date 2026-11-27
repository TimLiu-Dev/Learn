import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Catalogue {
    fun titleOf(barcode: String): String = if (barcode == "C-001") "Kindred" else "unknown"
}

// Median time of one call, in nanoseconds, over n calls.
fun medianNanos(n: Int, call: () -> String): Long {
    var sink = 0                                        // use every result, so the JIT can't drop the call (L16)
    val times = LongArray(n) {
        val start = System.nanoTime()
        sink += call().length
        System.nanoTime() - start
    }
    check(sink > 0)
    return times.sorted()[n / 2]
}

fun main() {
    val catalogue = Catalogue()
    // The same catalogue, behind HTTP on this machine: the smallest "microservice" there is.
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/title") { exchange ->
        val body = catalogue.titleOf(exchange.requestURI.query).toByteArray()
        exchange.sendResponseHeaders(200, body.size.toLong())
        exchange.responseBody.use { it.write(body) }
    }
    server.start()
    val client = HttpClient.newHttpClient()
    val uri = URI.create("http://127.0.0.1:${server.address.port}/title?C-001")
    val remote = { client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString()).body() }

    medianNanos(5_000) { catalogue.titleOf("C-001") }   // warm-up for both (L16)
    medianNanos(2_000, remote)
    val local = medianNanos(5_000) { catalogue.titleOf("C-001") }
    val http = medianNanos(2_000, remote)
    println("same answer?      ${catalogue.titleOf("C-001") == remote()}")
    println("in-process call:  $local ns")
    println("HTTP, localhost:  ${http / 1_000} µs  (about ${http / maxOf(local, 1)} times slower)")
    server.stop(0)
}
