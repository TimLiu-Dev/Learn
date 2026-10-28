import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.Executors

val http: HttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
fun get(uri: URI) = http.send(HttpRequest.newBuilder(uri).build(), BodyHandlers.ofString())

fun main() {
    val procs = mutableListOf(startBackend("b1", 20), startBackend("b2", 200), startBackend("b3", 20))
    println("3 backend processes: " + procs.joinToString { "${it.name} pid ${it.process.pid()}" })
    try {
        println("-- 60 requests to /slow, one every 10 ms (b2 takes 200 ms, b1 and b3 take 20 ms)")
        for (strategy in Strategy.entries) {
            val targets = procs.map { Target(it.name, it.uri) }
            LoadBalancer(targets, strategy).use { lb ->
                val ms = arrivals(lb.uri.resolve("/slow"), n = 60, gapMs = 10)
                println("  %-17s all done after %5d ms   %s".format(strategy, ms,
                    targets.joinToString(" ") { "${it.name}=${it.served}" }))
            }
        }
        println("-- kill b2 mid-run, active health checks only")
        killMidRun(procs, retry = false)
        procs[1] = startBackend("b2")                        // a fresh b2 for round two
        println("-- kill b2 again, now with retry on failure")
        killMidRun(procs, retry = true)
    } finally {
        procs.forEach { it.process.destroyForcibly() }
    }
}

// Open-loop load: a new request every gapMs, whether or not earlier ones have finished.
fun arrivals(uri: URI, n: Int, gapMs: Long): Long {
    val t0 = System.nanoTime()
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        repeat(n) { pool.submit { get(uri) }; Thread.sleep(gapMs) }
    }
    return (System.nanoTime() - t0) / 1_000_000
}

fun killMidRun(procs: List<BackendProcess>, retry: Boolean) {
    val targets = procs.map { Target(it.name, it.uri) }
    LoadBalancer(targets, Strategy.ROUND_ROBIN, retry).use { lb ->
        var ok = 0
        var failed = 0
        repeat(60) { i ->
            if (i == 20) {
                procs[1].process.destroyForcibly().waitFor()
                lb.log("killed ${procs[1].name} (like kill -9)")
            }
            val resp = get(lb.uri.resolve("/name"))
            if (resp.statusCode() == 200) ok++ else { failed++; lb.log("request $i -> ${resp.statusCode()} ${resp.body()}") }
            Thread.sleep(25)
        }
        println("  60 requests: $ok ok, $failed failed   " + targets.joinToString(" ") { "${it.name}=${it.served}" })
    }
}
