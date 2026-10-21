import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.HttpTimeoutException
import java.time.Duration

// Run TinyServer in serve mode first: java -jar tiny.jar serve 60
const val BASE = "http://127.0.0.1:18131"

fun get(path: String): HttpRequest = HttpRequest.newBuilder(URI.create(BASE + path)).build()

fun main() {
    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(2))
        .build()                                           // redirects: NEVER by default

    // 1. synchronous GET: send() blocks until the whole response has arrived
    val res = client.send(get("/hello?name=Ada"), BodyHandlers.ofString())
    println("1 GET     : ${res.statusCode()} ${res.version()} " +
        "${res.headers().firstValue("content-type").orElse("?")} | ${res.body().trim()}")

    // 2. redirects: the default client hands you the 301; NORMAL follows it
    val moved = client.send(get("/old"), BodyHandlers.discarding())
    println("2 NEVER   : ${moved.statusCode()} Location: ${moved.headers().firstValue("location").orElse("?")}")
    val follower = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
    val landed = follower.send(get("/old"), BodyHandlers.ofString())
    println("2 NORMAL  : ${landed.statusCode()} from ${landed.uri()} | ${landed.body().trim()}")

    // 3. POST with a body
    val post = HttpRequest.newBuilder(URI.create("$BASE/echo"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("""{"x":1}"""))
        .build()
    println("3 POST    : ${client.send(post, BodyHandlers.ofString()).body().trim()}")

    // 4. asynchronous: three requests in flight at once, awaited from coroutines (L29)
    val results = runBlocking {
        listOf("/", "/hello?name=Bo", "/nope").map { p ->
            async { "$p -> ${client.sendAsync(get(p), BodyHandlers.ofString()).await().statusCode()}" }
        }.awaitAll()
    }
    println("4 async   : $results")

    // 5. a per-request timeout
    val slow = HttpRequest.newBuilder(URI.create("$BASE/slow")).timeout(Duration.ofMillis(500)).build()
    val t0 = System.nanoTime()
    try {
        client.send(slow, BodyHandlers.ofString())
    } catch (e: HttpTimeoutException) {
        println("5 timeout : ${e.javaClass.simpleName}: ${e.message} after ${(System.nanoTime() - t0) / 1_000_000} ms")
    }

    // 6. HTTPS to a real site: the same client negotiates HTTP/2
    val head = HttpRequest.newBuilder(URI.create("https://example.com/")).HEAD().build()
    val remote = client.send(head, BodyHandlers.discarding())
    println("6 HTTPS   : ${remote.statusCode()} ${remote.version()}")
}
