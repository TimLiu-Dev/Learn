import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class FramingTest {
    @Test fun `a frame is a big-endian length then the bytes`() {
        assertEquals(listOf<Byte>(0, 0, 0, 2, 104, 105), frame("hi").toList())
    }

    @Test fun `the length counts bytes, not characters`() {
        assertEquals("héllo is 6 UTF-8 bytes", listOf<Byte>(0, 0, 0, 6), frame("héllo").take(4))
    }

    @Test fun `two messages in one read come out as two`() {
        assertEquals(listOf("hello", "world"), FrameDecoder().feed(frame("hello") + frame("world")))
    }

    @Test fun `a message split across reads waits for the rest`() {
        val bytes = frame("hello")
        val decoder = FrameDecoder()
        assertEquals("half a length prefix", emptyList<String>(), decoder.feed(bytes.copyOfRange(0, 2)))
        assertEquals("prefix done, 3 of 5 bytes", emptyList<String>(), decoder.feed(bytes.copyOfRange(2, 7)))
        assertEquals("the last 2 bytes", listOf("hello"), decoder.feed(bytes.copyOfRange(7, bytes.size)))
    }

    @Test fun `every way of cutting the stream gives the same messages`() {
        val messages = listOf("hello", "", "héllo", "bye")
        val wire = messages.map(::frame).reduce { a, b -> a + b }
        for (size in 1..wire.size) {
            val decoder = FrameDecoder()
            val got = wire.toList().chunked(size).flatMap { decoder.feed(it.toByteArray()) }
            assertEquals("reads of $size bytes", messages, got)
        }
    }

    @Test fun `a real socket, read 3 bytes at a time`() {
        val wire = frame("hello") + frame("wörld") + frame("bye")
        val loopback = InetAddress.getLoopbackAddress()
        ServerSocket(0, 50, loopback).use { server ->
            val sender = Thread { server.accept().use { it.getOutputStream().write(wire) } }  // closing = end of stream
            sender.start()
            val decoder = FrameDecoder()
            val got = mutableListOf<String>()
            Socket(loopback, server.localPort).use { socket ->
                socket.soTimeout = 2_000
                val input = socket.getInputStream()
                val buf = ByteArray(3)
                while (true) {
                    val n = input.read(buf)
                    if (n == -1) break
                    got += decoder.feed(buf.copyOf(n))
                }
            }
            sender.join()
            assertEquals(listOf("hello", "wörld", "bye"), got)
        }
    }
}
