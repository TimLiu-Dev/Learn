package katas

import java.nio.file.Files
import kotlin.io.path.deleteRecursively

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("logstore")
    LogStore.open(dir).use { db ->
        db.put("dune", "Herbert"); db.put("emma", "Austen"); db.put("dune", "Frank Herbert"); db.delete("emma")
        println("size ${db.size}, ${db.fileBytes} bytes on disk")
    }
    LogStore.open(dir).use { db ->
        println("reopened: dune = ${db.get("dune")}, emma = ${db.get("emma")}")
        db.compact()
        println("compacted: ${db.fileBytes} bytes on disk")
    }
    dir.deleteRecursively()
}
