import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

// A line-framed server: one virtual thread per client, one reply line per request line.
fun main() {
    val loopback = InetAddress.getLoopbackAddress()
    ServerSocket(0, 50, loopback).use { server ->
        Thread.ofPlatform().daemon().start {
            while (true) {
                val s = runCatching { server.accept() }.getOrNull() ?: break
                Thread.ofVirtual().start {
                    s.use {
                        val out = PrintWriter(it.getOutputStream(), true)
                        it.getInputStream().bufferedReader().forEachLine { line ->
                            out.println(line.uppercase())     // newline = end of one message
                        }
                    }
                }
            }
        }
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            val results = (1..3).map { id ->
                pool.submit<String> {
                    Socket(loopback, server.localPort).use { s ->
                        // two messages in ONE write: the server still sees two lines
                        s.getOutputStream().write("client $id\nsays hi\n".toByteArray())
                        val input = s.getInputStream().bufferedReader()
                        "${input.readLine()} | ${input.readLine()}"
                    }
                }
            }
            results.forEach { println(it.get()) }
        }
    }
}
