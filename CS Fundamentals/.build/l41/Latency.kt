import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.concurrent.Executors
import kotlin.random.Random

// Measure four latencies on this machine: a HashMap get, an indexed SQLite
// lookup, a localhost HTTP round trip, and a cold vs warm function call.
fun main() {
    // 1. HashMap get: 100,000 entries, 10 million random lookups
    val rnd = Random(41)
    val map = HashMap<Int, String>()
    for (i in 0 until 100_000) map[i] = "book-$i"
    val keys = IntArray(10_000_000) { rnd.nextInt(100_000) }
    var sink = 0L
    for (k in keys) sink += map.getValue(k).length          // warm-up pass
    var t = System.nanoTime()
    for (k in keys) sink += map.getValue(k).length
    val mapNs = (System.nanoTime() - t) / keys.size.toDouble()

    // 2. SQLite: 100,000 rows, lookup by an indexed column
    val db = Files.createTempFile("l41-latency", ".db")
    val sqlUs = DriverManager.getConnection("jdbc:sqlite:$db").use { c ->
        c.createStatement().use { s ->
            s.execute("CREATE TABLE books(id INTEGER PRIMARY KEY, isbn TEXT, title TEXT)")
            s.execute("CREATE INDEX books_isbn ON books(isbn)")
        }
        c.autoCommit = false
        c.prepareStatement("INSERT INTO books(isbn, title) VALUES (?, ?)").use { ins ->
            for (i in 0 until 100_000) {
                ins.setString(1, "978${1_000_000 + i}"); ins.setString(2, "Title $i"); ins.addBatch()
            }
            ins.executeBatch()
        }
        c.commit()
        c.prepareStatement("SELECT title FROM books WHERE isbn = ?").use { q ->
            repeat(2_000) { sink += lookup(q, rnd) }            // warm-up
            t = System.nanoTime()
            repeat(20_000) { sink += lookup(q, rnd) }
            (System.nanoTime() - t) / 20_000.0 / 1_000
        }
    }
    Files.delete(db)

    // 3. HTTP round trip to a server on 127.0.0.1
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/ping") { ex ->
        val body = "ok".toByteArray()
        ex.sendResponseHeaders(200, body.size.toLong())
        ex.responseBody.use { it.write(body) }
    }
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.start()
    val client = HttpClient.newHttpClient()
    val req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:${server.address.port}/ping")).build()
    repeat(500) { client.send(req, HttpResponse.BodyHandlers.ofString()) }   // warm-up
    t = System.nanoTime()
    repeat(5_000) { sink += client.send(req, HttpResponse.BodyHandlers.ofString()).body().length }
    val httpUs = (System.nanoTime() - t) / 5_000.0 / 1_000
    server.stop(0)

    // 4. Cold vs warm: the first call loads and links classes; later calls are JIT-compiled
    t = System.nanoTime()
    sink += "%08.3f|%s".format(3.14159, listOf(1, 2)).length
    val coldUs = (System.nanoTime() - t) / 1_000.0
    repeat(100_000) { sink += "%08.3f|%s".format(3.14159, listOf(1, 2)).length }
    t = System.nanoTime()
    repeat(10_000) { sink += "%08.3f|%s".format(3.14159, listOf(1, 2)).length }
    val warmUs = (System.nanoTime() - t) / 10_000.0 / 1_000

    println("HashMap get            %10.1f ns".format(mapNs))
    println("SQLite indexed lookup  %10.1f µs".format(sqlUs))
    println("HTTP round trip (lo0)  %10.1f µs".format(httpUs))
    println("String.format, cold    %10.1f µs".format(coldUs))
    println("String.format, warm    %10.1f µs".format(warmUs))
    println("(sink ${sink % 10})")
}

fun lookup(q: PreparedStatement, rnd: Random): Int {
    q.setString(1, "978${1_000_000 + rnd.nextInt(100_000)}")
    return q.executeQuery().use { rs -> if (rs.next()) rs.getString(1).length else 0 }
}
