import java.io.InputStream

// Stands in for the kernel: every call to it counts as one "system call".
class CountingStream(private val data: ByteArray, private val maxPerCall: Int = Int.MAX_VALUE) : InputStream() {
    var calls = 0
        private set
    private var pos = 0

    override fun read(): Int {
        calls++
        return if (pos < data.size) data[pos++].toInt() and 0xFF else -1
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        calls++
        if (pos >= data.size) return -1
        val n = minOf(len, maxPerCall, data.size - pos)    // may return fewer bytes than asked
        data.copyInto(b, off, pos, pos + n)
        pos += n
        return n
    }
}

// Assumes every refill fills the whole buffer, so short reads hand out stale bytes.
class MyBufferedStream(private val src: InputStream, size: Int = 8192) : InputStream() {
    private val buf = ByteArray(size)
    private var pos = 0
    private var count = 0

    override fun read(): Int {
        if (pos == count) {
            if (src.read(buf, 0, buf.size) == -1) return -1
            pos = 0
            count = buf.size
        }
        return buf[pos++].toInt() and 0xFF
    }
}
