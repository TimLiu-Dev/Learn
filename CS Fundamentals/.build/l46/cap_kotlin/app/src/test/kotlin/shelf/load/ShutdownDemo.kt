package shelf.load

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import shelf.Config
import shelf.Shelf
import shelf.store.Db
import shelf.store.SqliteBookStore

// Graceful shutdown, for real: three slow requests are in flight when close() is called.
fun main() {
    for (drain in listOf(0, 5)) {                                // v1 called stop(0); v2 drains for up to 5 s
        println("drainSeconds = $drain")
        demo(drain)
    }
}

private fun demo(drain: Int) {
    val file = Files.createTempFile("shelf-stop", ".db")
    Db(file).use { db -> db.createSchema(); SqliteBookStore(db).add("Dune", "Frank Herbert", 1965) }
    val t0 = System.nanoTime()
    fun at() = "%4d ms".format((System.nanoTime() - t0) / 1_000_000)
    val shelf = Shelf(Config(port = 0, dbFile = file, cacheSize = 0, drainSeconds = drain), Clock.systemUTC(), log = {}) {
        SlowBookStore(it, 800)                                   // every read takes 0.8 s
    }.start()
    val port = shelf.port
    val client = HttpClient.newHttpClient()
    val inFlight = (1..3).map { n ->
        client.sendAsync(HttpRequest.newBuilder(URI("http://127.0.0.1:$port/books/1")).build(), HttpResponse.BodyHandlers.ofString())
            .handle { r, e -> println("${at()}  request $n " + (r?.let { "finished: ${it.statusCode()} ${it.body()}" } ?: "FAILED: ${e.cause?.javaClass?.simpleName}")) }
    }
    Thread.sleep(200)
    println("${at()}  close() called with 3 requests in flight")
    shelf.close()
    println("${at()}  close() returned")
    inFlight.forEach { it.join() }
    try {
        client.send(HttpRequest.newBuilder(URI("http://127.0.0.1:$port/health")).build(), HttpResponse.BodyHandlers.ofString())
    } catch (e: Exception) {
        println("${at()}  new request after close: ${e.javaClass.simpleName}")
    }
    Files.deleteIfExists(file)
}
