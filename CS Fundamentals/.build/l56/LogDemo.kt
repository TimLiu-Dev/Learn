import java.nio.file.Files
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readBytes

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val file = dir.resolve("data.log")

    LogKV(file).use { db ->
        db.put("dune", "Herbert")
        db.put("emma", "Austen")
        db.put("dune", "Frank Herbert")      // an overwrite is just another append
        db.delete("emma")                     // so is a delete: a tombstone
        println("get(dune) = ${db.get("dune")}, get(emma) = ${db.get("emma")}")
        println("index     = ${db.indexSnapshot()}")
        println("file      = ${db.fileSize} bytes, ${db.deadBytes} of them garbage")
    }

    val raw = file.readBytes()
    println("record 0  = " + raw.copyOfRange(0, 23).toHexString(HexFormat { bytes.byteSeparator = " " }))
    println("            crc         keyLen=4    valLen=7    d  u  n  e  H  e  r  b  e  r  t")

    LogKV(file).use { db ->                  // a new process would do exactly this: scan and rebuild
        println("reopened  : get(dune) = ${db.get("dune")}, keys = ${db.keys}")
    }

    LogKV(dir.resolve("big.log")).use { db ->
        repeat(100_000) { i -> db.put("key${i % 1_000}", "value-$i") }
        println("100,000 puts to 1,000 keys: ${db.fileSize / 1024} KB on disk, " +
                "${db.deadBytes * 100 / db.fileSize}% garbage")
        db.compact()
        println("after compact():           ${db.fileSize / 1024} KB, get(key7) = ${db.get("key7")}")
    }
    dir.deleteRecursively()
}
