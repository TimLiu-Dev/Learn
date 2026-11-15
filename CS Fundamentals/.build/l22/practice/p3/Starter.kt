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

class MyBufferedStream(private val src: InputStream, size: Int = 8192) : InputStream() {
    private val buf = ByteArray(size)
    private var pos = 0     // index of the next byte to hand out
    private var count = 0   // how many bytes of buf hold real data

    override fun read(): Int = TODO("refill buf when pos == count, then return the next byte as 0..255")
}
