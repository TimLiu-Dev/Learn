import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.Socket
import java.util.TreeMap

// Exercise 1: a raw HTTP client that frames the body by Content-Length. Needs TinyServer on :18131.
data class Response(val status: Int, val reason: String, val headers: Map<String, String>, val body: String)

fun readLine(input: InputStream): String {
    val buf = ByteArrayOutputStream()
    var b = input.read()
    while (b != -1 && b != '\n'.code) { if (b != '\r'.code) buf.write(b); b = input.read() }
    return buf.toString(Charsets.UTF_8)
}

fun rawGet(host: String, port: Int, path: String): Response = Socket(host, port).use { socket ->
    socket.getOutputStream().write("GET $path HTTP/1.1\r\nHost: $host\r\n\r\n".toByteArray(Charsets.US_ASCII))
    val input = socket.getInputStream().buffered()
    val (_, code, reason) = readLine(input).split(" ", limit = 3)    // HTTP/1.1 404 Not Found
    val headers = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
    generateSequence { readLine(input) }.takeWhile { it.isNotEmpty() }
        .forEach { headers[it.substringBefore(':').trim()] = it.substringAfter(':').trim() }
    val body = headers["Content-Length"]?.let { input.readNBytes(it.toInt()) } ?: input.readAllBytes()
    Response(code.toInt(), reason, headers, body.decodeToString())
}

fun main() {
    for (path in listOf("/", "/hello?name=Ex", "/old", "/nope")) {
        val r = rawGet("127.0.0.1", 18131, path)
        val extra = r.headers["location"]?.let { "  location=$it" } ?: ""
        val body = r.body.trim().ifEmpty { null }?.let { "\"$it\"" } ?: "(empty)"
        println("%-15s %d %-17s %d headers  body=%s%s".format(path, r.status, r.reason, r.headers.size, body, extra))
    }
}
