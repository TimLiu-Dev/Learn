import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Test

class BufferTest {
    private fun drain(s: InputStream): List<Int> {
        val out = mutableListOf<Int>()
        while (true) {
            val b = s.read()
            if (b == -1) return out
            out += b
        }
    }

    private val hundred = ByteArray(100) { it.toByte() }

    @Test fun `every byte comes back in order`() {
        assertEquals((0 until 100).toList(), drain(MyBufferedStream(CountingStream(hundred), 16)))
    }

    @Test fun `byte 0xFF is 255, not the end of the stream`() {
        val data = byteArrayOf(1, -1, 2)
        assertEquals(listOf(1, 255, 2), drain(MyBufferedStream(CountingStream(data), 16)))
    }

    @Test fun `100 bytes through a 16-byte buffer cost 8 calls`() {
        val src = CountingStream(hundred)
        drain(MyBufferedStream(src, 16))
        assertEquals("7 refills with data, then 1 that finds the end", 8, src.calls)
    }

    @Test fun `unbuffered, the same 100 bytes cost 101 calls`() {
        val src = CountingStream(hundred)
        drain(src)
        assertEquals(101, src.calls)
    }

    @Test fun `short reads are handled`() {
        val src = CountingStream(hundred, maxPerCall = 5)
        assertEquals("a source that returns at most 5 bytes per call", (0 until 100).toList(), drain(MyBufferedStream(src, 16)))
    }

    @Test fun `an empty source is -1 straight away`() {
        assertEquals(-1, MyBufferedStream(CountingStream(ByteArray(0))).read())
    }
}
