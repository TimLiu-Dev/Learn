import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.Executors
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// One backend server. Runs as its own JVM process: java -cp l42.jar BackendKt b1 200 1
fun main(args: Array<String>) {
    val name = args[0]
    val slowMs = args.getOrElse(1) { "0" }.toLong()
    val threads = args.getOrElse(2) { "1" }.toInt()
    val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 100)
    server.createContext("/health") { reply(it, 200, "ok") }
    server.createContext("/name") { reply(it, 200, name) }
    server.createContext("/slow") { Thread.sleep(slowMs); reply(it, 200, name) }
    server.createContext("/work") { hashPassword(); reply(it, 200, name) }
    if (threads > 1) server.executor = Executors.newFixedThreadPool(threads)
    server.start()                       // no executor: ONE thread serves every request
    println("READY ${server.address.port}")
}

fun reply(ex: HttpExchange, status: Int, body: String) {
    val bytes = body.toByteArray()
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

// CPU-bound work: one PBKDF2 hash (L33), about 5 ms
fun hashPassword(): ByteArray {
    val spec = PBEKeySpec("hunter2".toCharArray(), ByteArray(16), 20_000, 256)
    return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
}

class BackendProcess(val name: String, val process: Process, port: Int) {
    val uri: URI = URI("http://127.0.0.1:$port")
}

// Starts a backend as a separate JVM, running this same jar, and waits for READY.
fun startBackend(name: String, slowMs: Long = 0, threads: Int = 1): BackendProcess {
    val java = ProcessHandle.current().info().command().orElse("java")
    val process = ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
        "BackendKt", name, slowMs.toString(), threads.toString()).redirectErrorStream(true).start()
    val ready = process.inputReader().readLine()          // "READY 51234"
    return BackendProcess(name, process, ready.removePrefix("READY ").toInt())
}
