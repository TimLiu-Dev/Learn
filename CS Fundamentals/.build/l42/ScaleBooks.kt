import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Files
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Build with: kotlinc Backend.kt LoadBalancer.kt JobQueue.kt Replication.kt ScaleBooks.kt (+ sqlite-jdbc)

fun signToken(user: String, key: ByteArray): String {
    val mac = Mac.getInstance("HmacSHA256").apply { init(SecretKeySpec(key, "HmacSHA256")) }
    return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(user.toByteArray())).take(16)
}

// A stateless books backend: everything it knows lives in the database, the queue or the token.
class BooksBackend(val name: String, private val db: ReplicatedBooks, private val jobs: JobQueue, private val key: ByteArray) {
    val server: HttpServer = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 100)
    val uri: URI get() = URI("http://127.0.0.1:${server.address.port}")

    init {
        server.executor = Executors.newVirtualThreadPerTaskExecutor()
        server.createContext("/health") { reply(it, 200, "ok") }      // without this, the LB marks us DOWN
        server.createContext("/login") { ex ->
            val user = ex.requestURI.query.removePrefix("user=")
            reply(ex, 200, "$user.${signToken(user, key)}")
        }
        server.createContext("/books") { ex -> if (ex.requestMethod == "POST") create(ex) else read(ex) }
        server.start()
    }

    private fun user(ex: HttpExchange): String? {
        val (user, sig) = (ex.requestHeaders.getFirst("Token") ?: ".").split(".", limit = 2)
        return user.takeIf { MessageDigest.isEqual(sig.toByteArray(), signToken(user, key).toByteArray()) }
    }

    private fun create(ex: HttpExchange) {
        user(ex) ?: return reply(ex, 401, "bad token")
        val written = db.write(ex.requestBody.readAllBytes().decodeToString())   // to the leader
        jobs.push("thumbnail ${written.id}")                                     // slow work: later
        ex.responseHeaders.add("X-Version", written.version.toString())
        reply(ex, 201, written.id.toString())
    }

    private fun read(ex: HttpExchange) {
        val id = ex.requestURI.path.substringAfterLast('/').toLong()
        val needed = ex.requestHeaders.getFirst("X-Min-Version")?.toLong() ?: 0
        val fromReplica = db.followerVersion() >= needed                          // read-your-writes
        val title = if (fromReplica) db.readFollower(id) else db.readLeader(id)
        ex.responseHeaders.add("X-Served-By", "$name/${if (fromReplica) "replica" else "leader"}")
        if (title == null) reply(ex, 404, "no book $id") else reply(ex, 200, title)
    }
}

fun main() {
    val dir = Files.createTempDirectory("l42final")
    val key = "shared-secret-from-config".toByteArray()
    val db = ReplicatedBooks(dir, lagMs = 300)
    val jobs = JobQueue(capacity = 100, visibilityMs = 300, maxAttempts = 3)
    val backends = (1..3).map { BooksBackend("b$it", db, jobs, key) }
    val targets = backends.map { Target(it.name, it.uri) }
    val lb = LoadBalancer(targets, Strategy.ROUND_ROBIN, retryOnFailure = true)

    // Two thumbnail workers. Idempotent: a job whose thumbnail exists is acked and skipped.
    val thumbnails = ConcurrentHashMap.newKeySet<String>()
    val made = AtomicInteger()
    val skipped = AtomicInteger()
    val crashOnce = AtomicBoolean(true)
    val workers = (1..2).map { w ->
        Thread.ofVirtual().start {
            while (true) {
                val m = jobs.receive(800) ?: break
                if (m.body in thumbnails) { skipped.incrementAndGet(); jobs.ack(m.id); continue }
                Thread.sleep(30)                                   // "resize the cover image"
                thumbnails += m.body
                made.incrementAndGet()
                if (w == 1 && crashOnce.getAndSet(false)) { lb.log("worker 1 made '${m.body}', then CRASHED before ack"); continue }
                jobs.ack(m.id)
            }
        }
    }

    val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
    fun send(b: HttpRequest.Builder) = client.send(b.build(), BodyHandlers.ofString())
    val token = send(HttpRequest.newBuilder(lb.uri.resolve("/login?user=ada"))).body()

    var errors = 0
    var leaderReads = 0
    var staleReads = 0
    repeat(20) { i ->
        if (i == 10) { backends[1].server.stop(0); lb.log("b2 crashed") }
        val post = send(HttpRequest.newBuilder(lb.uri.resolve("/books")).header("Token", token)
            .POST(HttpRequest.BodyPublishers.ofString("Book $i")))
        if (post.statusCode() != 201) { errors++; lb.log("POST Book $i -> ${post.statusCode()} ${post.body()}"); return@repeat }
        val id = post.body()
        val version = post.headers().firstValue("X-Version").orElseThrow()
        // the writer reads its own write: it sends the version it saw
        val mine = send(HttpRequest.newBuilder(lb.uri.resolve("/books/$id")).header("X-Min-Version", version))
        if (mine.statusCode() != 200) errors++
        if (mine.headers().firstValue("X-Served-By").orElse("").endsWith("leader")) leaderReads++
        // someone else reads the same book straight away, without a version
        val other = send(HttpRequest.newBuilder(lb.uri.resolve("/books/$id")))
        if (other.statusCode() == 404) staleReads++
        Thread.sleep(50)
    }
    workers.forEach { it.join() }

    println("per-backend requests: " + targets.joinToString(" ") { "${it.name}=${it.served}" })
    println("errors seen by the client: $errors")
    println("own reads routed to the leader (replica behind): $leaderReads of 20")
    println("other client's reads that got 404 (replica behind): $staleReads of 20")
    println("thumbnail jobs: ${thumbnails.size} distinct, made ${made.get()} times, duplicates skipped: ${skipped.get()}")

    lb.close(); jobs.close(); db.close()
    backends.forEach { it.server.stop(0) }
    dir.toFile().deleteRecursively()
}
