package katas

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.TempDir

class LogStoreTest {
    @TempDir lateinit var dir: Path

    private fun open(sync: Sync = Sync.NEVER) = LogStore.open(dir, sync)
    private val data get() = dir.resolve(LogStore.DATA)

    @Test fun `an empty store has nothing`() = open().use { db ->
        assertNull(db.get("dune")); assertEquals(0, db.size)
    }

    @Test fun `put then get`() = open().use { db ->
        db.put("dune", "Herbert")
        assertEquals("Herbert", db.get("dune"))
    }

    @Test fun `an overwrite keeps the latest value`() = open().use { db ->
        db.put("dune", "Herbert"); db.put("dune", "Frank Herbert")
        assertEquals("Frank Herbert", db.get("dune")); assertEquals(1, db.size)
    }

    @Test fun `a delete survives reopening`() {
        open().use { db -> db.put("emma", "Austen"); db.delete("emma") }
        open().use { db -> assertNull(db.get("emma")); assertEquals(0, db.size) }
    }

    @Test fun `reopening recovers every key`() {
        open(Sync.EVERY_WRITE).use { db -> for (i in 0 until 100) db.put("k$i", "v$i") }
        open().use { db -> assertEquals(100, db.size); assertEquals("v42", db.get("k42")) }
    }

    @Test fun `a torn tail is cut off at every possible length, and new writes survive`() {
        open().use { db -> for (i in 1..3) db.put("k$i", "v$i") }      // three 16-byte records
        val full = data.readBytes()
        for (len in 33 until full.size) {
            data.writeBytes(full.copyOf(len))
            open().use { db ->
                assertEquals(setOf("k1", "k2"), (1..3).map { "k$it" }.filter { db.get(it) != null }.toSet(), "len $len")
                db.put("k4", "v4")
            }
            open().use { db -> assertEquals("v4", db.get("k4"), "write after repair lost, len $len") }
        }
    }

    @Test fun `garbage after the last record is ignored`() {
        open().use { db -> db.put("dune", "Herbert") }
        data.writeBytes(data.readBytes() + Random(56).nextBytes(100))
        open().use { db -> assertEquals("Herbert", db.get("dune")); assertEquals(1, db.size) }
        assertEquals(23, Files.size(data))                              // the junk was truncated
    }

    @Test fun `compact shrinks the file and keeps the latest values`() = open().use { db ->
        repeat(1_000) { i -> db.put("key${i % 10}", "value-$i") }
        db.delete("key3")
        val before = db.fileBytes
        db.compact()
        assertTrue(db.fileBytes < before / 50, "${db.fileBytes} vs $before")
        assertEquals("value-999", db.get("key9")); assertNull(db.get("key3")); assertEquals(9, db.size)
        db.put("after", "compaction")
        assertEquals("compaction", db.get("after"))
    }

    @Test fun `a compaction file left by a crash is ignored`() {
        open().use { db -> db.put("dune", "Herbert") }
        dir.resolve(LogStore.COMPACT).writeBytes(Random(1).nextBytes(500))
        open().use { db -> assertEquals("Herbert", db.get("dune")) }
        assertFalse(Files.exists(dir.resolve(LogStore.COMPACT)))
    }

    @Test fun `an empty key is rejected`() = open().use { db ->
        assertFailsWith<IllegalArgumentException> { db.put("", "x") }
        Unit
    }

    @Test fun `kill -9 loses no acknowledged write`() {
        val java = ProcessHandle.current().info().command().orElseThrow()
        val child = ProcessBuilder(java, "-cp", System.getProperty("java.class.path"), "katas.KillChildKt", dir.toString())
            .redirectErrorStream(true).start()
        val out = child.inputReader()
        var acked = -1
        while (acked < 2_000) acked = out.readLine().removePrefix("acked ").toInt()
        child.toHandle().destroyForcibly()      // SIGKILL mid-write; Process.destroyForcibly() would also close our pipe
        assertEquals(137, child.waitFor())
        out.lineSequence().forEach { acked = it.removePrefix("acked ").toInt() }   // acks already in the pipe
        open().use { db ->
            for (i in 0..acked) assertEquals("v$i", db.get("k$i"), "acked k$i lost")
            println("acked ${acked + 1}, recovered ${db.size}")
        }
    }
}
