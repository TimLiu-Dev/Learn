import java.nio.ByteBuffer

// One message on the wire: a 4-byte big-endian length (in bytes), then the UTF-8 bytes.
fun frame(message: String): ByteArray = TODO("the length prefix, then the bytes, like writeInt + write")

// Called with whatever each read() returned. Returns the messages this chunk completes.
class FrameDecoder {
    fun feed(chunk: ByteArray): List<String> = TODO("keep the leftover bytes between calls")
}
