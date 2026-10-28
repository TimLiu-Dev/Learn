import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

// A dependency with one hung endpoint (/slow sleeps 5 s) and one healthy one (/fast).
// Our service has a pool of 8 worker threads. 50 requests need /slow, then 1 needs /fast.

val client: HttpClient = HttpClient.newHttpClient()

fun call(port: Int, path: String, timeout: Duration?): String {
    val b = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$port$path"))
    if (timeout != null) b.timeout(timeout)                       // no timeout = wait forever
    return try { client.send(b.build(), HttpResponse.BodyHandlers.ofString()).body() }
    catch (e: java.net.http.HttpTimeoutException) { "timeout" }
}

fun run(label: String, port: Int, timeout: Duration?, bulkhead: Boolean) {
    val slowPool = Executors.newFixedThreadPool(8)
    val fastPool = if (bulkhead) Executors.newFixedThreadPool(2) else slowPool
    val done = AtomicInteger(); val timedOut = AtomicInteger()
    val start = System.nanoTime()
    repeat(50) { slowPool.submit { if (call(port, "/slow", timeout) == "timeout") timedOut.incrementAndGet(); done.incrementAndGet() } }
    val fast = fastPool.submit(Callable { call(port, "/fast", timeout); (System.nanoTime() - start) / 1_000_000 })
    val fastMs = try { "answered after ${fast.get(2500, TimeUnit.MILLISECONDS)} ms" } catch (e: TimeoutException) { "still waiting after 2500 ms" }
    slowPool.shutdown()
    val finished = slowPool.awaitTermination(3, TimeUnit.SECONDS)
    println("$label")
    println("  /fast request: $fastMs")
    println("  /slow requests: ${done.get()} of 50 finished (${timedOut.get()} timed out)" +
            if (finished) " after ${(System.nanoTime() - start) / 1_000_000} ms" else ", the rest still stuck 3 s later")
    slowPool.shutdownNow(); fastPool.shutdownNow()
}

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.createContext("/slow") { ex -> Thread.sleep(5000); ex.sendResponseHeaders(200, 2); ex.responseBody.use { it.write("ok".toByteArray()) } }
    server.createContext("/fast") { ex -> ex.sendResponseHeaders(200, 2); ex.responseBody.use { it.write("ok".toByteArray()) } }
    server.start()
    val port = server.address.port
    run("1) one shared pool, no timeouts", port, null, bulkhead = false)
    run("2) one shared pool, 300 ms timeouts", port, Duration.ofMillis(300), bulkhead = false)
    run("3) separate pools (bulkhead), no timeouts", port, null, bulkhead = true)
    server.stop(0)
    System.exit(0)                                     // abandoned /slow handlers would otherwise linger
}
