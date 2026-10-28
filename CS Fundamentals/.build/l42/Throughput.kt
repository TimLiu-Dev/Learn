import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val procs = (1..3).map { startBackend("b$it") }
    try {
        for (n in 1..3) {
            val targets = procs.take(n).map { Target(it.name, it.uri) }
            LoadBalancer(targets, Strategy.ROUND_ROBIN).use { lb ->
                load(lb.uri.resolve("/work"), clients = 24, millis = 1000)              // warm-up
                val perSecond = load(lb.uri.resolve("/work"), clients = 24, millis = 3000) / 3.0
                println("%d backend(s): %4.0f hashes/s   %s".format(n, perSecond,
                    targets.joinToString(" ") { "${it.name}=${it.served}" }))
            }
        }
        val big = startBackend("big", threads = 3)          // scale UP: one server, 3 threads
        LoadBalancer(listOf(Target(big.name, big.uri)), Strategy.ROUND_ROBIN).use { lb ->
            load(lb.uri.resolve("/work"), clients = 24, millis = 1000)
            println("1 backend, 3 threads: %4.0f hashes/s".format(load(lb.uri.resolve("/work"), 24, 3000) / 3.0))
        }
        big.process.destroyForcibly()
    } finally {
        procs.forEach { it.process.destroyForcibly() }
    }
}

// `clients` virtual threads call `uri` in a loop until time is up; returns how many calls succeeded.
fun load(uri: URI, clients: Int, millis: Long): Int {
    val done = AtomicInteger()
    val deadline = System.nanoTime() + millis * 1_000_000
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        repeat(clients) {
            pool.submit {
                while (System.nanoTime() < deadline) {
                    if (get(uri).statusCode() == 200) done.incrementAndGet()
                }
            }
        }
    }
    return done.get()
}
