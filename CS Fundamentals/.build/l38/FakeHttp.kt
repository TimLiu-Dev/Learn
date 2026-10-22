import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CopyOnWriteArrayList

// The service's view of the outside world: one small interface it owns.
fun interface HttpPoster {
    fun post(url: String, body: String): Int            // returns the status code
}

class FeeNotifier(private val http: HttpPoster, private val baseUrl: String) {
    fun lateFee(member: String, cents: Int): Boolean =
        http.post("$baseUrl/payments", "member=$member&cents=$cents") in 200..299
}

// Real: wraps the JDK's HttpClient (L31).
class JdkHttpPoster(private val client: HttpClient = HttpClient.newHttpClient()) : HttpPoster {
    override fun post(url: String, body: String): Int =
        client.send(HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                    HttpResponse.BodyHandlers.discarding()).statusCode()
}

// Fake: records every call, answers with a status we choose. No network at all.
class RecordingHttpPoster(private val status: Int = 201) : HttpPoster {
    val calls = mutableListOf<String>()
    override fun post(url: String, body: String): Int { calls += "POST $url $body"; return status }
}

fun main() {
    // A throwaway payments server on localhost, so the real poster has something to talk to.
    val received = CopyOnWriteArrayList<String>()
    val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0)
    server.createContext("/payments") { ex ->
        received += ex.requestBody.readAllBytes().decodeToString()
        ex.sendResponseHeaders(201, -1); ex.close()
    }
    server.start()
    try {
        val base = "http://127.0.0.1:${server.address.port}"
        var t = System.nanoTime()
        val real = FeeNotifier(JdkHttpPoster(), base).lateFee("ada", 75)
        println("real:  ok=$real server got=$received  (${(System.nanoTime() - t) / 1_000_000} ms)")

        val fake = RecordingHttpPoster()
        t = System.nanoTime()
        val ok = FeeNotifier(fake, "https://payments.example").lateFee("ada", 75)
        println("fake:  ok=$ok calls=${fake.calls}  (${(System.nanoTime() - t) / 1_000} µs)")
        println("fake 503: ok=${FeeNotifier(RecordingHttpPoster(503), "https://payments.example").lateFee("ada", 75)}")
    } finally {
        server.stop(0)
    }
}
