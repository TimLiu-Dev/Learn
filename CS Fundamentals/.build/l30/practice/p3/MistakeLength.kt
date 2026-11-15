import java.nio.ByteBuffer

fun frame(message: String): ByteArray {
    val bytes = message.toByteArray(Charsets.UTF_8)
    return ByteBuffer.allocate(4 + bytes.size)
        .putInt(message.length)
        .put(bytes)
        .array()
}

class FrameDecoder {
    private var pending = ByteArray(0)                            // received, but not yet a whole message

    fun feed(chunk: ByteArray): List<String> {
        pending += chunk
        val messages = mutableListOf<String>()
        while (pending.size >= 4) {                               // is the length prefix itself complete?
            val length = ByteBuffer.wrap(pending, 0, 4).int
            if (pending.size < 4 + length) break                  // the rest hasn't arrived yet
            messages += String(pending, 4, length, Charsets.UTF_8)
            pending = pending.copyOfRange(4 + length, pending.size)
        }
        return messages
    }
}
