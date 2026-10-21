import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

// An HTTP/1.1 server on raw sockets: one request per connection.
// java -jar tiny.jar          -> serves 6 requests from its own client, then stops
// java -jar tiny.jar serve 30 -> serves on 127.0.0.1:18131 for 30 s (for curl)
data class Request(val method: String, val target: String, val headers: Map<String, String>, val body: String)

fun readRequest(raw: InputStream): Request {
    val input = raw.buffered()
    val (method, target) = readLine(input).split(" ")     // GET /hello HTTP/1.1
    val headers = linkedMapOf<String, String>()
    while (true) {
        val line = readLine(input)
        if (line.isEmpty()) break
        headers[line.substringBefore(':').trim().lowercase()] = line.substringAfter(':').trim()
    }
    val length = headers["content-length"]?.toInt() ?: 0
    return Request(method, target, headers, input.readNBytes(length).decodeToString())
}

// HTTP lines end in CRLF (\r\n)
fun readLine(input: InputStream): String {
    val buf = ByteArrayOutputStream()
    var b = input.read()
    while (b != -1 && b != '\n'.code) {
        if (b != '\r'.code) buf.write(b)
        b = input.read()
    }
    return buf.toString(Charsets.UTF_8)
}

fun respond(out: OutputStream, status: Int, reason: String, body: String,
            head: Boolean = false, vararg extra: String) {
    val bytes = body.encodeToByteArray()
    val headers = buildString {
        append("HTTP/1.1 $status $reason\r\n")
        append("Content-Type: text/plain; charset=utf-8\r\n")
        append("Content-Length: ${bytes.size}\r\n")
        append("Connection: close\r\n")
        extra.forEach { append(it).append("\r\n") }
        append("\r\n")                                     // blank line: end of headers
    }
    out.write(headers.encodeToByteArray())
    if (!head) out.write(bytes)                            // HEAD: same headers, no body
    out.flush()
}

fun handle(socket: Socket) = socket.use {
    val req = readRequest(it.getInputStream())
    val path = req.target.substringBefore('?')
    val query = req.target.substringAfter('?', "")
    val head = req.method == "HEAD"
    val get = head || req.method == "GET"
    val out = it.getOutputStream()
    val status = when {
        get && path == "/" -> 200.also { respond(out, 200, "OK", "home\n", head) }
        get && path == "/hello" -> {
            val name = query.removePrefix("name=").ifEmpty { "world" }
            200.also { respond(out, 200, "OK", "hello, $name\n", head) }
        }
        get && path == "/old" -> 301.also { respond(out, 301, "Moved Permanently", "", head, "Location: /new") }
        get && path == "/new" -> 200.also { respond(out, 200, "OK", "you made it to /new\n", head) }
        get && path == "/slow" -> { Thread.sleep(2000); 200.also { respond(out, 200, "OK", "finally\n", head) } }
        req.method == "POST" && path == "/echo" ->
            200.also { respond(out, 200, "OK", "got ${req.body.length} bytes: ${req.body}\n") }
        else -> 404.also { respond(out, 404, "Not Found", "no route for $path\n", head) }
    }
    println("  [server] ${req.method} ${req.target} -> $status")
}

// The client side, by hand: write a request, read until the server closes.
fun send(port: Int, request: String): String =
    Socket("127.0.0.1", port).use { s ->
        s.getOutputStream().write(request.encodeToByteArray())
        s.getInputStream().readAllBytes().decodeToString()
    }

fun main(args: Array<String>) {
    val serve = args.firstOrNull() == "serve"
    val server = ServerSocket()
    server.bind(InetSocketAddress("127.0.0.1", if (serve) 18131 else 0))
    if (serve) {
        val until = System.currentTimeMillis() + 1000L * args[1].toInt()
        server.soTimeout = 200
        println("serving on http://127.0.0.1:18131 for ${args[1]} s")
        while (System.currentTimeMillis() < until) {
            try { val s = server.accept(); Thread.ofVirtual().start { handle(s) } }
            catch (again: SocketTimeoutException) { }
        }
        server.close()
        return
    }
    val port = server.localPort
    val acceptor = Thread.ofPlatform().start {
        server.use { repeat(6) { _ -> val s = it.accept(); Thread.ofVirtual().start { handle(s) }.join() } }
    }

    val full = send(port, "GET /hello?name=Ada HTTP/1.1\r\nHost: localhost\r\n\r\n")
    print("--- full response ---\n" + full.replace("\r\n", "\\r\\n\n"))
    println("--- status lines ---")
    val post = """{"x":1}"""
    val requests = listOf(
        "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n",
        "HEAD / HTTP/1.1\r\nHost: localhost\r\n\r\n",
        "GET /old HTTP/1.1\r\nHost: localhost\r\n\r\n",
        "POST /echo HTTP/1.1\r\nHost: localhost\r\nContent-Length: ${post.length}\r\n\r\n$post",
        "GET /nope HTTP/1.1\r\nHost: localhost\r\n\r\n",
    )
    for (r in requests) {
        val resp = send(port, r)
        val status = resp.lineSequence().first()
        val body = resp.substringAfter("\r\n\r\n").trim()
        println(status + if (body.isEmpty()) "   (no body)" else "   body: $body")
    }
    acceptor.join()
}
