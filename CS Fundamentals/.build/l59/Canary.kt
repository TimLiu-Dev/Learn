import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.atomic.AtomicInteger

fun reply(ex: HttpExchange, status: Int, body: String) {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

fun startServer(handler: (HttpExchange) -> Unit): HttpServer =
    HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50).apply {
        createContext("/") { ex -> ex.use { handler(it) } }
        start()
    }

/** One backend. Release v2 has a bug: every 5th request fails. */
class Backend(val version: String, failEvery: Int = 0) {
    private val count = AtomicInteger()
    val server = startServer { ex ->
        val n = count.incrementAndGet()
        if (failEvery > 0 && n % failEvery == 0) reply(ex, 500, "boom") else reply(ex, 200, version)
    }
    val uri: URI = URI("http://127.0.0.1:${server.address.port}/")
}

/** L42's round-robin balancer, plus a canary slice chosen by user. */
class CanaryBalancer(private val stable: List<Backend>, private val canary: Backend) {
    @Volatile var canaryPercent = 0               // the kill switch: set it to 0 and the canary gets nothing
    private val next = AtomicInteger()
    private val client = HttpClient.newHttpClient()
    val stats = mutableMapOf<String, IntArray>()  // version -> [requests, errors]

    fun pick(user: String): Backend =
        if (Math.floorMod(user.hashCode(), 100) < canaryPercent) canary
        else stable[Math.floorMod(next.getAndIncrement(), stable.size)]

    fun handle(user: String): Int {
        val b = pick(user)
        val status = client.send(HttpRequest.newBuilder(b.uri).build(), HttpResponse.BodyHandlers.ofString()).statusCode()
        synchronized(stats) {
            val s = stats.getOrPut(b.version) { IntArray(2) }
            s[0]++; if (status >= 500) s[1]++
        }
        return status
    }

    fun errorRate(version: String): Double = synchronized(stats) {
        val s = stats[version] ?: return 0.0
        if (s[0] == 0) 0.0 else s[1].toDouble() / s[0]
    }
}

fun main() {
    val stable = listOf(Backend("v1"), Backend("v1"))
    val canary = Backend("v2", failEvery = 5)
    val lb = CanaryBalancer(stable, canary)
    val users = (1..200).map { "user$it" }
    val unlucky = mutableSetOf<String>()

    fun traffic(requests: Int) = repeat(requests) { i ->
        val user = users[i % users.size]
        if (lb.handle(user) >= 500) unlucky += user
    }

    lb.canaryPercent = 10
    println("canary at ${lb.canaryPercent}%: ${users.count { lb.pick(it) === canary }} of ${users.size} users go to v2")
    traffic(1000)
    for (v in listOf("v1", "v2")) {
        val s = lb.stats.getValue(v)
        println("%s  %4d requests  %3d errors  %5.1f%%".format(v, s[0], s[1], 100 * lb.errorRate(v)))
    }
    // The canary analysis: compare against the control, with enough requests to mean something.
    val canaryRequests = lb.stats.getValue("v2")[0]
    if (canaryRequests >= 50 && lb.errorRate("v2") > lb.errorRate("v1") + 0.01) {
        lb.canaryPercent = 0
        println("ROLLBACK: v2 error rate is above v1's, canary set to 0%")
    }
    val before = lb.stats.getValue("v1").copyOf()
    traffic(1000)
    val after = lb.stats.getValue("v1")
    println("next 1000 requests: ${after[0] - before[0]} to v1, ${after[1] - before[1]} errors, v2 got ${lb.stats.getValue("v2")[0] - canaryRequests}")
    val failed = lb.stats.values.sumOf { it[1] }
    println("in total: $failed of 2000 requests failed (%.1f%%), seen by ${unlucky.size} of ${users.size} users".format(100.0 * failed / 2000))
    (stable + canary).forEach { it.server.stop(0) }
}
