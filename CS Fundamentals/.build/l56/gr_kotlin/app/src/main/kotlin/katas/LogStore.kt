package katas

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.util.zip.CRC32

enum class Sync { EVERY_WRITE, NEVER }

// A crash-safe key-value store: an append-only log of CRC-checked records plus an in-memory hash index.
// Record: [crc 4][keyLen 4][valLen 4][key][value]; valLen = -1 is a tombstone.
class LogStore private constructor(private val dir: Path, private val sync: Sync) : AutoCloseable {
    private val file = dir.resolve(DATA)
    private var ch: FileChannel
    private val index = HashMap<String, Long>()            // key -> offset of its newest record

    init {
        Files.deleteIfExists(dir.resolve(COMPACT))         // a compaction that crashed never happened
        ch = FileChannel.open(file, CREATE, READ, WRITE)
        recover()
    }

    val size: Int get() = index.size
    val fileBytes: Long get() = ch.size()

    fun put(key: String, value: String) {
        require(key.isNotEmpty()) { "key must not be empty" }
        index[key] = append(key, value)
    }

    fun delete(key: String) {
        if (index.remove(key) != null) append(key, null)
    }

    fun get(key: String): String? = index[key]?.let { read(ch, it)!!.value }

    // Write the live records to a new file, make it durable, then rename it over the old one in one step.
    fun compact() {
        val tmp = dir.resolve(COMPACT)
        val fresh = HashMap<String, Long>()
        FileChannel.open(tmp, CREATE, WRITE).use { out ->
            for ((key, offset) in index) {
                fresh[key] = out.position()
                val buf = encode(key, read(ch, offset)!!.value)
                while (buf.hasRemaining()) out.write(buf)
            }
            out.force(true)
        }
        ch.close()
        Files.move(tmp, file, ATOMIC_MOVE)
        ch = FileChannel.open(file, READ, WRITE)
        index.clear(); index.putAll(fresh)
    }

    override fun close() = ch.close()

    private fun append(key: String, value: String?): Long {
        val buf = encode(key, value)
        val offset = ch.size()
        var pos = offset
        while (buf.hasRemaining()) pos += ch.write(buf, pos)
        if (sync == Sync.EVERY_WRITE) ch.force(false)       // only now is the write acknowledged as durable
        return offset
    }

    // Rebuild the index, then cut off anything after the last good record (a torn or garbage tail).
    private fun recover() {
        var pos = 0L
        while (true) {
            val rec = read(ch, pos) ?: break
            if (rec.value == null) index.remove(rec.key) else index[rec.key] = pos
            pos += rec.size
        }
        if (pos < ch.size()) { ch.truncate(pos); ch.force(true) }
    }

    private class Rec(val key: String, val value: String?, val size: Int)

    companion object {
        const val DATA = "data.log"
        const val COMPACT = "data.log.compact"
        private const val HEADER = 12
        private const val MAX_LEN = 1 shl 20

        fun open(dir: Path, sync: Sync = Sync.EVERY_WRITE) = LogStore(dir, sync)

        private fun encode(key: String, value: String?): ByteBuffer {
            val k = key.toByteArray()
            val v = value?.toByteArray()
            val buf = ByteBuffer.allocate(HEADER + k.size + (v?.size ?: 0))
            buf.position(4)
            buf.putInt(k.size).putInt(v?.size ?: -1).put(k)
            if (v != null) buf.put(v)
            buf.putInt(0, CRC32().apply { update(buf.array(), 4, buf.capacity() - 4) }.value.toInt())
            return buf.flip()
        }

        private fun read(ch: FileChannel, pos: Long): Rec? {
            val header = ByteBuffer.allocate(HEADER)
            if (ch.read(header, pos) < HEADER) return null
            val crc = header.getInt(0); val keyLen = header.getInt(4); val valLen = header.getInt(8)
            if (keyLen !in 0..MAX_LEN || valLen !in -1..MAX_LEN) return null
            val size = HEADER + keyLen + maxOf(valLen, 0)
            if (pos + size > ch.size()) return null
            val body = ByteBuffer.allocate(size).also { ch.read(it, pos) }
            if (CRC32().apply { update(body.array(), 4, size - 4) }.value.toInt() != crc) return null
            val value = if (valLen < 0) null else String(body.array(), HEADER + keyLen, valLen)
            return Rec(String(body.array(), HEADER, keyLen), value, size)
        }
    }
}
