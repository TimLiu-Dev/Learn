import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE
import java.util.PriorityQueue
import java.util.TreeMap

data class Entry(val key: String, val value: String?, val size: Int)        // value == null: tombstone

class Stats { var blocksRead = 0L; var bloomSkips = 0L }

// Reads one record in Part 1's format (LogKV.encode wrote it), or null at the end of the file.
fun readEntry(ch: FileChannel, pos: Long): Entry? {
    val header = ByteBuffer.allocate(LogKV.HEADER)
    if (ch.read(header, pos) < LogKV.HEADER) return null
    val keyLen = header.getInt(4); val valLen = header.getInt(8)
    val body = ByteBuffer.allocate(keyLen + maxOf(valLen, 0))
    ch.read(body, pos + LogKV.HEADER)
    val key = String(body.array(), 0, keyLen)
    val value = if (valLen < 0) null else String(body.array(), keyLen, valLen)
    return Entry(key, value, LogKV.HEADER + body.capacity())
}

// An immutable file of records sorted by key, plus a sparse index and a Bloom filter kept in memory.
class SsTable(val path: Path, private val sparse: TreeMap<String, Long>, private val bloom: Bloom, val bytes: Long) {
    fun lookup(key: String, stats: Stats, useBloom: Boolean): Pair<Boolean, String?> {
        if (useBloom && !bloom.mightContain(key)) { stats.bloomSkips++; return false to null }
        val start = sparse.floorEntry(key) ?: return false to null          // sorts before our first key
        stats.blocksRead++
        FileChannel.open(path).use { ch ->
            var pos = start.value
            repeat(BLOCK) {                                                  // the key can only be in this block
                val e = readEntry(ch, pos) ?: return false to null
                if (e.key == key) return true to e.value
                if (e.key > key) return false to null
                pos += e.size
            }
        }
        return false to null
    }

    // Records in key order, starting at the block that could hold `from`.
    fun entries(from: String = ""): Sequence<Entry> = sequence {
        FileChannel.open(path).use { ch ->
            var pos = sparse.floorEntry(from)?.value ?: 0L
            while (true) { val e = readEntry(ch, pos) ?: break; yield(e); pos += e.size }
        }
    }

    companion object {
        const val BLOCK = 16

        fun write(path: Path, sorted: List<Pair<String, String?>>): SsTable {
            val sparse = TreeMap<String, Long>()
            val bloom = Bloom(maxOf(64, sorted.size * 10), 7)                // 10 bits per key: about 1% false positives
            var pos = 0L
            FileChannel.open(path, CREATE_NEW, WRITE).use { ch ->
                sorted.forEachIndexed { i, (key, value) ->
                    if (i % BLOCK == 0) sparse[key] = pos
                    bloom.add(key)                                           // tombstones too: they must be found
                    val buf = LogKV.encode(key, value)
                    while (buf.hasRemaining()) pos += ch.write(buf)
                }
            }
            return SsTable(path, sparse, bloom, pos)
        }
    }
}

// A log-structured merge tree: writes go to a sorted in-memory memtable, which is flushed to SSTables.
class Lsm(private val dir: Path, private val memLimit: Int) {
    val stats = Stats()
    var useBloom = true
    var userBytes = 0L; private set                    // bytes a plain append-only log would have written
    var diskBytes = 0L; private set                    // bytes we actually wrote to SSTables, ever
    private var mem = TreeMap<String, String?>()
    private val tables = ArrayList<SsTable>()          // newest first
    private var nextId = 0
    val tableCount get() = tables.size

    fun put(key: String, value: String) = write(key, value)
    fun delete(key: String) = write(key, null)

    private fun write(key: String, value: String?) {
        mem[key] = value
        userBytes += LogKV.encode(key, value).remaining()
        if (mem.size >= memLimit) flush()
    }

    fun get(key: String): String? {
        if (mem.containsKey(key)) return mem[key]
        for (t in tables) {
            val (found, value) = t.lookup(key, stats, useBloom)
            if (found) return value                    // the newest version wins, even a tombstone
        }
        return null
    }

    // All live keys with from <= key < to, in order. Oldest first, so newer versions overwrite older ones.
    fun range(from: String, to: String): List<Pair<String, String>> {
        val merged = TreeMap<String, String?>()
        for (t in tables.asReversed()) for (e in t.entries(from)) {
            if (e.key >= to) break
            if (e.key >= from) merged[e.key] = e.value
        }
        merged.putAll(mem.subMap(from, to))
        return merged.mapNotNull { (k, v) -> v?.let { k to it } }
    }

    fun flush() {
        if (mem.isEmpty()) return
        val t = SsTable.write(nextPath(), mem.map { it.key to it.value })
        diskBytes += t.bytes
        tables.add(0, t)
        mem = TreeMap()
    }

    // Merge every table into one, like merge sort's merge step (L18) over k inputs with a heap (L20).
    fun compact() {
        class Cursor(val age: Int, val it: Iterator<Entry>) {
            var head: Entry? = null
            fun advance(): Boolean { head = if (it.hasNext()) it.next() else null; return head != null }
        }
        val heap = PriorityQueue(compareBy<Cursor>({ it.head!!.key }, { it.age }))   // ties: newest first
        tables.forEachIndexed { age, t -> Cursor(age, t.entries().iterator()).let { if (it.advance()) heap.add(it) } }
        val out = ArrayList<Pair<String, String?>>()
        while (heap.isNotEmpty()) {
            val c = heap.poll()
            val e = c.head!!
            while (heap.isNotEmpty() && heap.peek().head!!.key == e.key) {          // older versions: skip
                val old = heap.poll()
                if (old.advance()) heap.add(old)
            }
            if (e.value != null) out += e.key to e.value                         // a full merge drops tombstones
            if (c.advance()) heap.add(c)
        }
        val merged = SsTable.write(nextPath(), out)
        diskBytes += merged.bytes
        tables.forEach { Files.delete(it.path) }
        tables.clear(); tables.add(merged)
    }

    private fun nextPath() = dir.resolve("sst-%04d".format(nextId++))
}
