import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random

// A local service where 2% of requests are slow. Measure 10,000 requests,
// then look at the distribution instead of the average.
fun main() {
    val count = AtomicInteger()
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/book") { ex ->
        if (count.incrementAndGet() % 50 == 7) Thread.sleep(20)   // exactly 1 request in 50
        val body = "Dune".toByteArray()
        ex.sendResponseHeaders(200, body.size.toLong())
        ex.responseBody.use { it.write(body) }
    }
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.start()

    val client = HttpClient.newHttpClient()
    val req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:${server.address.port}/book")).build()
    repeat(500) { client.send(req, HttpResponse.BodyHandlers.discarding()) }   // warm-up
    count.set(0)

    val ms = DoubleArray(10_000) {
        val t = System.nanoTime()
        client.send(req, HttpResponse.BodyHandlers.discarding())
        (System.nanoTime() - t) / 1e6
    }
    server.stop(0)

    val sorted = ms.sortedArray()                               // L18: sort, then index
    println("requests %d, slow %.0f%%".format(ms.size, 100.0 * ms.count { it > 10 } / ms.size))
    println("mean   %6.2f ms".format(ms.average()))
    for (p in listOf(50.0, 95.0, 99.0, 99.9)) {
        val label = if (p % 1 == 0.0) p.toInt().toString() else p.toString()
        println("p%-5s %6.2f ms".format(label, pct(sorted, p)))
    }
    println("max    %6.2f ms".format(sorted.last()))

    // Fan-out: a page calls 10 services, each with this distribution.
    val p99 = pct(sorted, 99.0)
    val rnd = Random(41)
    val pages = 100_000
    val hit = (1..pages).count { (1..10).any { ms[rnd.nextInt(ms.size)] > p99 } }
    println("pages with a call slower than p99: computed %.1f%%, simulated %.1f%%"
        .format(100 * (1 - 0.99.pow(10)), 100.0 * hit / pages))
}

// Nearest-rank percentile: the value at position ceil(p/100 * n) in sorted order
fun pct(sorted: DoubleArray, p: Double): Double {
    val rank = ceil(p / 100 * sorted.size).toInt()
    return sorted[maxOf(0, rank - 1)]
}
