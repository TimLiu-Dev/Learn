import java.net.Socket

// One HTTP request, typed by hand, over a plain TCP socket.
fun main() {
    val request = "GET / HTTP/1.1\r\n" +
        "Host: example.com\r\n" +
        "Connection: close\r\n" +
        "\r\n"                                             // blank line: no body follows
    Socket("example.com", 80).use { socket ->
        socket.getOutputStream().write(request.toByteArray(Charsets.US_ASCII))
        val all = socket.getInputStream().readAllBytes()  // until the server closes
        val text = all.decodeToString()
        val head = text.substringBefore("\r\n\r\n")
        val body = text.substringAfter("\r\n\r\n")
        println("status line : ${head.lines().first()}")
        head.lines().drop(1)
            .filter { it.matches(Regex("(?i)(content-type|transfer-encoding|connection):.*")) }
            .forEach { println("header      : $it") }
        println("header lines: ${head.lines().size - 1}")
        val sizeLine = body.lines()[0]
        println("body line 1 : $sizeLine   <- chunk size in hex = ${sizeLine.trim().toInt(16)} bytes")
        println("body line 2 : ${body.lines()[1].take(40)}...")
        println("bytes read  : ${all.size}")
    }
}
