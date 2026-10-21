import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.security.MessageDigest
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.HexFormat
import java.util.concurrent.Executors

// An origin server that speaks HTTP caching. java -jar etag.jar 60 -> serves on :18132 for 60 s
const val REPORT = "Q3 report: revenue up 4%, costs flat.\n"

fun etagOf(body: String): String {
    val hash = MessageDigest.getInstance("SHA-256").digest(body.encodeToByteArray())
    return "\"" + HexFormat.of().formatHex(hash).take(16) + "\""   // quotes are part of an ETag
}

fun send(ex: HttpExchange, status: Int, cacheControl: String?, etag: String?, body: String) {
    cacheControl?.let { ex.responseHeaders["Cache-Control"] = it }
    etag?.let { ex.responseHeaders["ETag"] = it }
    ex.responseHeaders["Content-Type"] = "text/plain; charset=utf-8"
    val bytes = body.encodeToByteArray()
    val noBody = status == 304 || ex.requestMethod == "HEAD"
    ex.sendResponseHeaders(status, if (noBody) -1 else bytes.size.toLong())   // -1 = no body
    ex.responseBody.use { if (!noBody) it.write(bytes) }
    println("  [origin] ${ex.requestMethod} ${ex.requestURI} -> $status")
}

fun main(args: Array<String>) {
    val seconds = args.firstOrNull()?.toInt() ?: 30
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 18132), 0)
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.createContext("/report") { ex ->
        val etag = etagOf(REPORT)
        if (ex.requestHeaders.getFirst("If-None-Match") == etag) send(ex, 304, "no-cache", etag, "")  // you already have it
        else send(ex, 200, "no-cache", etag, REPORT)
    }
    server.createContext("/time") { ex ->
        send(ex, 200, "max-age=2", null, "${LocalTime.now().truncatedTo(ChronoUnit.MILLIS)}\n")
    }
    server.createContext("/secret") { ex -> send(ex, 200, "no-store", null, "balance: 42\n") }
    server.start()
    println("origin on http://127.0.0.1:18132 for $seconds s")
    Thread.sleep(seconds * 1000L)
    server.stop(0)
}
