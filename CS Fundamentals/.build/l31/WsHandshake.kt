import java.io.DataInputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.CountDownLatch
import kotlin.experimental.xor

// A WebSocket server on a raw socket, just enough to talk to the JDK's WebSocket client.
const val GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"                  // fixed by RFC 6455

fun accept(key: String): String =
    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((key + GUID).toByteArray()))

fun main() {
    val a = accept("dGhlIHNhbXBsZSBub25jZQ==")                           // RFC 6455's own example
    println("RFC example: $a ${a == "s3pPLMBiTxaQ9kYGzzhZRbK+xOo="}")

    val serverGotText = CountDownLatch(1)
    ServerSocket(0, 50, InetAddress.getLoopbackAddress()).use { listener ->
        val server = Thread.ofVirtual().start { serve(listener, serverGotText) }
        val greeting = CompletableFuture<String>()
        val ws = HttpClient.newHttpClient().newWebSocketBuilder()
            .buildAsync(URI("ws://127.0.0.1:${listener.localPort}/chat"), object : WebSocket.Listener {
                override fun onText(w: WebSocket, text: CharSequence, last: Boolean): CompletionStage<*>? {
                    greeting.complete(text.toString())
                    w.request(1)                                           // ready for one more message
                    return null
                }
            }).join()                                                      // returns after the 101
        println("client got : ${greeting.join()}")
        ws.sendText("hi from java.net.http", true).join()
        serverGotText.await()
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join()
        server.join()                                                      // let the server print its last line
    }
}

fun serve(listener: ServerSocket, gotText: CountDownLatch) = try {
    listener.accept().use { s ->
        val reader = s.getInputStream().bufferedReader(Charsets.ISO_8859_1)
        var key: String? = null
        generateSequence { reader.readLine() }.takeWhile { it.isNotEmpty() }.forEach { line ->
            println("  > $line")
            if (line.lowercase().startsWith("sec-websocket-key:")) key = line.substringAfter(':').trim()
        }
        val acc = accept(checkNotNull(key) { "not a WebSocket request" })
        val out = s.getOutputStream()
        out.write(("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\n" +
            "Sec-WebSocket-Accept: $acc\r\n\r\n").toByteArray())
        println("  < HTTP/1.1 101 Switching Protocols, Sec-WebSocket-Accept: $acc")
        val hello = "hello from a raw socket".toByteArray()
        out.write(byteArrayOf(0x81.toByte(), hello.size.toByte()))        // FIN + text opcode; unmasked, short length
        out.write(hello)
        out.flush()
        // The client sends no frame before it has our 101, so the reader above holds no frame bytes.
        val din = DataInputStream(s.getInputStream())
        while (true) {
            val b0 = din.readUnsignedByte()
            val b1 = din.readUnsignedByte()
            val mask = din.readNBytes(4)                                   // clients MUST mask
            val payload = din.readNBytes(b1 and 0x7F)
            for (i in payload.indices) payload[i] = payload[i] xor mask[i % 4]
            println("  frame %02x %02x: opcode=%d masked=%b length=%d".format(b0, b1, b0 and 0x0F, b1 and 0x80 != 0, b1 and 0x7F))
            when (b0 and 0x0F) {
                1 -> { println("server got : ${payload.decodeToString()}"); gotText.countDown() }
                8 -> {
                    val code = ((payload[0].toInt() and 0xFF) shl 8) or (payload[1].toInt() and 0xFF)
                    println("server got : close $code ${payload.decodeToString(2, payload.size)}")
                    out.write(byteArrayOf(0x88.toByte(), 0))               // answer the close, then hang up
                    break
                }
            }
        }
    }
} catch (e: Exception) {
    println("server: $e")
}
