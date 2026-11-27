package shelf.load

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import shelf.Shelf

// Fire many requests at the same instant and count the status codes.
fun main() {
    val dbFile = Files.createTempFile("shelf-race", ".db")
    Shelf(dbFile, 0, Clock.systemUTC()).start().use { shelf ->
        val api = Api(shelf.port)
        repeat(20) { api.post("/members", """{"name":"M$it","email":"m$it@example.com"}""") }

        println("Race 1: 20 members borrow the SAME book at once (5 rounds)")
        repeat(5) { round ->
            val bookId = api.newBook("Dune ${round + 1}")
            val codes = api.burst(20) { i -> api.post("/loans", """{"bookId":$bookId,"memberId":${i + 1}}""") }
            println("  round ${round + 1}: ${codes.tally()}")
        }

        println("Race 2: ONE member borrows 10 DIFFERENT books at once (limit is 3)")
        val member = api.post("/members", """{"name":"Greedy","email":"greedy@example.com"}""").id()
        val books = (1..10).map { api.newBook("Book $it") }
        val codes = api.burst(10) { i -> api.post("/loans", """{"bookId":${books[i]},"memberId":$member}""") }
        val loans = api.get("/members/$member/loans").body()
        val open = Regex("\\{[^}]*\\}").findAll(loans).count { "returned" !in it.value }   // open loans have no "returned"
        println("  ${codes.tally()} -> member $member now has $open open loans")
    }
    Files.deleteIfExists(dbFile)
}

class Api(port: Int) {
    private val base = "http://127.0.0.1:$port"
    private val client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build()

    fun get(path: String): HttpResponse<String> =
        client.send(HttpRequest.newBuilder(URI(base + path)).build(), HttpResponse.BodyHandlers.ofString())

    fun post(path: String, json: String): HttpResponse<String> = client.send(
        HttpRequest.newBuilder(URI(base + path)).header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
        HttpResponse.BodyHandlers.ofString(),
    )

    fun newBook(title: String): Long = post("/books", """{"title":"$title","author":"Frank Herbert"}""").id()

    /** n virtual threads wait at a start line, then all call [call] together. */
    fun burst(n: Int, call: (Int) -> HttpResponse<String>): List<Int> {
        val start = CountDownLatch(1)
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            val futures = (0 until n).map { i -> pool.submit<Int> { start.await(); call(i).statusCode() } }
            start.countDown()
            return futures.map { it.get() }
        }
    }
}

fun HttpResponse<String>.id(): Long = Regex("\"id\":(\\d+)").find(body())!!.groupValues[1].toLong()
fun List<Int>.tally(): String = groupingBy { it }.eachCount().toSortedMap().entries.joinToString("  ") { "${it.key}×${it.value}" }
