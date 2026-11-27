package chat

import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction

/** Largest payload either side may send, in bytes. */
const val MAX_FRAME = 4096

class FrameTooLarge(val length: Long) : IOException("frame of $length bytes")

/**
 * Reads one frame: a 4-byte big-endian unsigned length, then that many payload bytes.
 * Returns null when the stream ends cleanly between frames.
 * TCP is a byte stream (L30): one read() may return half a frame or two frames, so this keeps reading.
 */
fun readFrame(input: DataInputStream): ByteArray? {
    val first = input.read()
    if (first == -1) return null
    var length = first.toLong()
    repeat(3) { length = (length shl 8) or input.readUnsignedByte().toLong() }
    if (length > MAX_FRAME) throw FrameTooLarge(length)
    return ByteArray(length.toInt()).also { input.readFully(it) }
}

fun encodeFrame(text: String): ByteArray {
    val payload = text.encodeToByteArray()
    return ByteBuffer.allocate(4 + payload.size).putInt(payload.size).put(payload).array()
}

/** Strict UTF-8: null for malformed bytes instead of silently inserting U+FFFD. */
fun decodeUtf8(bytes: ByteArray): String? = try {
    Charsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(bytes))
        .toString()
} catch (_: CharacterCodingException) {
    null
}
