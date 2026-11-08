import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.util.zip.CRC32

// Where a key's newest record lives in the file.
data class Loc(val offset: Long, val size: Int)

// A Bitcask-style store: an append-only log file plus an in-memory hash index.
// Record layout: [crc 4][keyLen 4][valLen 4][key bytes][value bytes]; valLen = -1 is a tombstone.
class LogKV(private val path: Path) : AutoCloseable {
    private var ch: FileChannel = FileChannel.open(path, CREATE, READ, WRITE)
    private val index = HashMap<String, Loc>()
    var deadBytes = 0L
        private set
    val keys: Int get() = index.size
    val fileSize: Long get() = ch.size()

    init { recover() }

    fun put(key: String, value: String) = append(key, value)
    fun delete(key: String) { if (key in index) append(key, null) }

    fun get(key: String): String? {
        val loc = index[key] ?: return null
        return readAt(loc.offset)?.second
    }

    fun indexSnapshot(): Map<String, Loc> = index.toSortedMap()

    fun sync() = ch.force(false)

    // Rewrite only the live records into a new file, then swap it in with one atomic rename.
    fun compact() {
        val tmp = path.resolveSibling("${path.fileName}.compact")
        val fresh = HashMap<String, Loc>()
        FileChannel.open(tmp, CREATE, WRITE).use { out ->
            var pos = 0L
            for ((key, loc) in index) {
                val buf = encode(key, readAt(loc.offset)!!.second)
                val size = buf.remaining()
                while (buf.hasRemaining()) out.write(buf)
                fresh[key] = Loc(pos, size)
                pos += size
            }
            out.force(true)
        }
        ch.close()
        Files.move(tmp, path, ATOMIC_MOVE)
        ch = FileChannel.open(path, READ, WRITE)
        index.clear(); index.putAll(fresh)
        deadBytes = 0
    }

    override fun close() = ch.close()

    private fun append(key: String, value: String?) {
        val buf = encode(key, value)
        val offset = ch.size()
        val size = buf.remaining()
        var pos = offset
        while (buf.hasRemaining()) pos += ch.write(buf, pos)
        val old = if (value == null) index.remove(key) else index.put(key, Loc(offset, size))
        if (old != null) deadBytes += old.size
        if (value == null) deadBytes += size          // a tombstone is garbage once compacted away
    }

    // Scan the whole file and rebuild the index: Bitcask's startup. Stops at the first bad record.
    private fun recover() {
        var pos = 0L
        while (true) {
            val rec = readAt(pos) ?: break
            val (key, value, size) = rec
            val old = if (value == null) index.remove(key) else index.put(key, Loc(pos, size))
            if (old != null) deadBytes += old.size
            if (value == null) deadBytes += size
            pos += size
        }
    }

    // Returns (key, value or null for a tombstone, record size), or null if the record is missing or corrupt.
    private fun readAt(pos: Long): Triple<String, String?, Int>? {
        val header = ByteBuffer.allocate(HEADER)
        if (ch.read(header, pos) < HEADER) return null
        header.flip()
        val crc = header.getInt(); val keyLen = header.getInt(); val valLen = header.getInt()
        if (keyLen !in 0..MAX_LEN || valLen !in -1..MAX_LEN) return null
        val size = HEADER + keyLen + maxOf(valLen, 0)
        if (pos + size > ch.size()) return null
        val body = ByteBuffer.allocate(size)
        ch.read(body, pos)
        val check = CRC32().apply { update(body.array(), 4, size - 4) }
        if (check.value.toInt() != crc) return null
        val key = String(body.array(), HEADER, keyLen)
        val value = if (valLen < 0) null else String(body.array(), HEADER + keyLen, valLen)
        return Triple(key, value, size)
    }

    companion object {
        const val HEADER = 12
        const val MAX_LEN = 1 shl 20

        fun encode(key: String, value: String?): ByteBuffer {
            val k = key.toByteArray()
            val v = value?.toByteArray()
            val buf = ByteBuffer.allocate(HEADER + k.size + (v?.size ?: 0))
            buf.position(4)
            buf.putInt(k.size).putInt(v?.size ?: -1).put(k)
            if (v != null) buf.put(v)
            val crc = CRC32().apply { update(buf.array(), 4, buf.capacity() - 4) }
            buf.putInt(0, crc.value.toInt())
            return buf.flip()
        }
    }
}
