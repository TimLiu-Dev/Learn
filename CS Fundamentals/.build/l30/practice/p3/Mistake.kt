import java.nio.ByteBuffer

fun frame(message: String): ByteArray {
    val bytes = message.toByteArray(Charsets.UTF_8)
    return ByteBuffer.allocate(4 + bytes.size).putInt(bytes.size).put(bytes).array()
}

// Treats every chunk as if it held whole messages: one write = one read.
class FrameDecoder {
    fun feed(chunk: ByteArray): List<String> {
        val messages = mutableListOf<String>()
        var at = 0
        while (at + 4 <= chunk.size) {
            val length = ByteBuffer.wrap(chunk, at, 4).int
            if (at + 4 + length > chunk.size) break
            messages += String(chunk, at + 4, length, Charsets.UTF_8)
            at += 4 + length
        }
        return messages
    }
}
