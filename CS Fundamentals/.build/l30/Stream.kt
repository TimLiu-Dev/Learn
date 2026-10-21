import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

// TCP delivers a stream of bytes. It does not remember where one write() ended.
fun main() {
    val loopback = InetAddress.getLoopbackAddress()
    val messages = listOf("hello", "world", "bye")
    ServerSocket(0, 50, loopback).use { server ->
        // 1. Three writes, then the receiver does ONE read
        Socket(loopback, server.localPort).use { c ->
            server.accept().use { s ->
                for (m in messages) c.getOutputStream().write(m.toByteArray())   // 3 separate writes
                Thread.sleep(100)                              // let all the bytes arrive
                val buf = ByteArray(1024)
                val n = s.getInputStream().read(buf)
                println("1. one read got $n bytes: \"${String(buf, 0, n)}\"")
            }
        }
        // 2. The same three writes, read with a 4-byte buffer
        Socket(loopback, server.localPort).use { c ->
            server.accept().use { s ->
                for (m in messages) c.getOutputStream().write(m.toByteArray())
                c.shutdownOutput()                             // tell the reader: no more bytes
                val buf = ByteArray(4)
                val input = s.getInputStream()
                val chunks = mutableListOf<String>()
                while (true) {
                    val n = input.read(buf)
                    if (n == -1) break                         // -1 = end of stream
                    chunks += String(buf, 0, n)
                }
                println("2. 4-byte reads got: $chunks")
            }
        }
        // 3. Framing: send each message as [length][bytes], read back exactly that many
        Socket(loopback, server.localPort).use { c ->
            server.accept().use { s ->
                val out = DataOutputStream(c.getOutputStream())
                for (m in messages) {
                    val bytes = m.toByteArray(Charsets.UTF_8)
                    out.writeInt(bytes.size)                   // 4-byte big-endian length prefix
                    out.write(bytes)
                }
                out.flush()
                val input = DataInputStream(s.getInputStream())
                val got = List(messages.size) {
                    val bytes = ByteArray(input.readInt())     // how long is the next message?
                    input.readFully(bytes)                     // keep reading until we have it all
                    String(bytes, Charsets.UTF_8)
                }
                println("3. length-prefixed frames: $got")
            }
        }
    }
}
