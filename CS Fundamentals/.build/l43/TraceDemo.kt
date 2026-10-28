import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.SecureRandom
import java.util.HexFormat
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors

// Three services in one JVM: gateway -> books -> pricing.
// Each one reads the W3C `traceparent` header, starts its own span,
// passes the trace on, and writes one structured (JSON) log line per span.

data class Span(val service: String, val name: String, val traceId: String, val spanId: String,
                val parentId: String?, val startMs: Long, val durMs: Long)

val spans = ConcurrentLinkedQueue<Span>()          // stand-in for a trace collector (OpenTelemetry, Jaeger...)
val client: HttpClient = HttpClient.newHttpClient()
val random = SecureRandom()
val t0 = System.nanoTime()
fun nowMs() = (System.nanoTime() - t0) / 1_000_000
fun hex(bytes: Int) = HexFormat.of().formatHex(ByteArray(bytes).also { random.nextBytes(it) })

fun traced(service: String, ex: HttpExchange, work: (traceparent: String) -> String) {
    // traceparent = version "-" trace-id (32 hex) "-" parent-id (16 hex) "-" flags
    val incoming = ex.requestHeaders.getFirst("traceparent")?.split("-")
    val traceId = incoming?.get(1) ?: hex(16)                 // no header: we start a new trace
    val parentId = incoming?.get(2)
    val spanId = hex(8)
    val start = nowMs()
    val body = work("00-$traceId-$spanId-01")                // children see *our* span as their parent
    val dur = nowMs() - start
    spans += Span(service, "${ex.requestMethod} ${ex.requestURI}", traceId, spanId, parentId, start, dur)
    println("""{"level":"INFO","service":"$service","msg":"${ex.requestMethod} ${ex.requestURI}","status":200,""" +
            """"duration_ms":$dur,"trace_id":"$traceId","span_id":"$spanId","parent_id":${parentId?.let { "\"$it\"" } ?: "null"}}""")
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(200, bytes.size.toLong()); ex.responseBody.use { it.write(bytes) }
}

fun call(port: Int, path: String, traceparent: String): String =
    client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:$port$path")).header("traceparent", traceparent).build(),
        HttpResponse.BodyHandlers.ofString()).body()

fun server(): HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply { executor = Executors.newVirtualThreadPerTaskExecutor() }

fun main() {
    val pricing = server(); val books = server(); val gateway = server()
    pricing.createContext("/price") { ex -> traced("pricing", ex) { Thread.sleep(120); "12.99" } }      // the slow hop (artificial)
    books.createContext("/books") { ex -> traced("books", ex) { tp ->
        Thread.sleep(10)                                                                            // "database"
        "Dune, " + call(pricing.address.port, "/price/42", tp) } }
    gateway.createContext("/api") { ex -> traced("gateway", ex) { tp -> call(books.address.port, "/books/42", tp) } }
    listOf(pricing, books, gateway).forEach { it.start() }

    val reply = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:${gateway.address.port}/api/books/42")).build(),
        HttpResponse.BodyHandlers.ofString()).body()
    println("client got: $reply\n")

    val all = spans.sortedBy { it.startMs }
    val root = all.first { it.parentId == null }
    println("trace ${root.traceId}  (${root.durMs} ms)")
    for (s in all) {
        val depth = generateSequence(s) { c -> all.firstOrNull { it.spanId == c.parentId } }.count() - 1
        val from = ((s.startMs - root.startMs) * 30 / root.durMs).toInt()
        val width = maxOf(1, (s.durMs * 30 / root.durMs).toInt())
        val self = s.durMs - all.filter { it.parentId == s.spanId }.sumOf { it.durMs }
        println("%-18s %s%s%s %4d ms (self %d, starts +%d)".format("  ".repeat(depth) + s.service, " ".repeat(from), "█".repeat(width),
            " ".repeat(maxOf(0, 30 - from - width)), s.durMs, self, s.startMs - root.startMs))
    }
    listOf(gateway, books, pricing).forEach { it.stop(0) }
}
