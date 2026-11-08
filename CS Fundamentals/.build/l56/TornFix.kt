import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.util.zip.CRC32
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

// Length of the longest prefix made of whole records whose CRC matches.
fun validPrefix(ch: FileChannel): Long {
    var pos = 0L
    val header = ByteBuffer.allocate(LogKV.HEADER)
    while (true) {
        header.clear()
        if (ch.read(header, pos) < LogKV.HEADER) return pos
        val crc = header.getInt(0); val keyLen = header.getInt(4); val valLen = header.getInt(8)
        if (keyLen !in 0..LogKV.MAX_LEN || valLen !in -1..LogKV.MAX_LEN) return pos
        val size = LogKV.HEADER + keyLen + maxOf(valLen, 0)
        if (pos + size > ch.size()) return pos
        val body = ByteBuffer.allocate(size).also { ch.read(it, pos) }
        if (CRC32().apply { update(body.array(), 4, size - 4) }.value.toInt() != crc) return pos
        pos += size
    }
}

// Cut the file back to its valid prefix, so the next append lands right after the last good record.
fun repair(file: Path): Long = FileChannel.open(file, READ, WRITE).use { ch ->
    val end = validPrefix(ch)
    val cut = ch.size() - end
    ch.truncate(end)
    ch.force(true)
    cut
}

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val t = dir.resolve("t.log")
    LogKV(t).use { db -> for (i in 1..3) db.put("k$i", "v$i") }
    t.writeBytes(t.readBytes().copyOf(40))                   // the same torn file as before
    println("repair cut ${repair(t)} bytes")
    LogKV(t).use { db -> db.put("k4", "v4") }
    LogKV(t).use { db -> println("reopen: keys = ${db.indexSnapshot().keys}, get(k4) = ${db.get("k4")}") }
    dir.deleteRecursively()
}
