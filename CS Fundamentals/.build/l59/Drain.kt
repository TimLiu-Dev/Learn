import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

// ---------- the server, run as its own process: Drain server graceful|naive ----------
fun serve(graceful: Boolean) {
    val draining = AtomicBoolean(false)
    val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50)
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    fun route(path: String, handle: () -> Pair<Int, String>) = server.createContext(path) { ex ->
        ex.use {
            val (status, body) = handle()
            ex.sendResponseHeaders(status, body.length.toLong())
            ex.responseBody.write(body.toByteArray())
        }
    }
    route("/live") { 200 to "alive" }                                 // the process works: don't restart me
    route("/ready") { if (draining.get()) 503 to "draining" else 200 to "ready" }  // send me traffic?
    route("/work") { Thread.sleep(400); 200 to "done" }               // a slow request
    server.start()
    println("PORT ${server.address.port}")

    Runtime.getRuntime().addShutdownHook(Thread {
        if (graceful) {
            draining.set(true)
            println("SIGTERM: /ready now says 503")
            Thread.sleep(1000)          // give the load balancer time to notice (it checks every 250 ms)
            println("stop accepting; waiting for requests in flight")
            server.stop(5)              // returns when the last exchange finishes, or after 5 s
            println("drained, exiting")
        } else {
            println("SIGTERM: stopping at once")
            server.stop(0)
        }
    })
}

// ---------- the driver: a load balancer that polls /ready, plus steady traffic ----------
var t0 = System.nanoTime()
fun now() = (System.nanoTime() - t0) / 1_000_000
fun log(msg: String) = println("%5d ms  %s".format(now(), msg))

fun drive(graceful: Boolean) {
    t0 = System.nanoTime()
    val java = ProcessHandle.current().info().command().get()
    val child = ProcessBuilder(java, "-cp", System.getProperty("java.class.path"), "DrainKt", "server",
        if (graceful) "graceful" else "naive").redirectErrorStream(true).start()
    val lines = child.inputReader()
    val base = URI("http://127.0.0.1:${lines.readLine().removePrefix("PORT ")}")
    Thread.ofPlatform().daemon().start { lines.forEachLine { log("server: $it") } }

    val client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(500)).build()
    fun status(path: String): Int = try {
        client.send(HttpRequest.newBuilder(base.resolve(path)).build(), HttpResponse.BodyHandlers.ofString()).statusCode()
    } catch (e: IOException) { -1 }                                    // -1: no answer at all
    fun show(s: Int) = if (s < 0) "no answer" else s.toString()

    val inRotation = AtomicBoolean(true)
    val lbStop = AtomicBoolean(false)
    val lb = Thread.ofPlatform().start {
        while (!lbStop.get()) {
            val ready = status("/ready") == 200
            if (inRotation.getAndSet(ready) != ready) { log("balancer: instance ${if (ready) "IN" else "OUT of"} rotation") }
            Thread.sleep(250)
        }
    }

    val sent = AtomicInteger(); val ok = AtomicInteger(); val failed = AtomicInteger()
    val requests = mutableListOf<Thread>()
    var killed = false
    while (now() < 3000) {
        if (!killed && now() >= 1000) {
            killed = true
            log("kill -TERM ${child.pid()}")
            ProcessBuilder("kill", "-TERM", child.pid().toString()).start().waitFor()
            Thread.sleep(300)
            log("probes: /live ${show(status("/live"))}, /ready ${show(status("/ready"))}")
        }
        if (inRotation.get()) {
            sent.incrementAndGet()
            requests += Thread.ofVirtual().start { if (status("/work") == 200) ok.incrementAndGet() else failed.incrementAndGet() }
        }
        Thread.sleep(50)
    }
    requests.forEach { it.join() }
    lbStop.set(true); lb.join()
    log("server exit code ${child.waitFor()}")
    log("requests sent ${sent.get()}, ok ${ok.get()}, failed ${failed.get()}")
}

fun main(args: Array<String>) {
    if (args.firstOrNull() == "server") return serve(args[1] == "graceful")
    for (graceful in listOf(false, true)) {
        println(if (graceful) "\n== Graceful: readiness off, drain, then stop" else "== Naive: stop at once on SIGTERM")
        drive(graceful)
    }
}
