import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MiniCaskTest {
    @Test fun `put then get, and a missing key is null`() {
        val db = MiniCask()
        db.put("dune", "Herbert")
        assertEquals("Herbert", db.get("dune"))
        assertNull(db.get("emma"))
    }

    @Test fun `an overwrite appends and the newest value wins`() {
        val db = MiniCask()
        db.put("dune", "Herbert")
        db.put("dune", "Frank Herbert")
        assertEquals("Frank Herbert", db.get("dune"))
        assertEquals("the old record stays in the log as garbage", listOf("put dune Herbert", "put dune Frank Herbert"), db.log)
    }

    @Test fun `a delete survives reopening`() {
        val db = MiniCask()
        db.put("emma", "Austen")
        db.delete("emma")
        assertNull(db.get("emma"))
        assertNull("reopened with MiniCask(db.log), emma came back: log = ${db.log}", MiniCask(db.log).get("emma"))
    }

    @Test fun `compact keeps one record per live key`() {
        val db = MiniCask()
        db.put("a", "1"); db.put("b", "2"); db.put("a", "3"); db.put("c", "4"); db.delete("b")
        db.compact()
        assertEquals("log after compact", listOf("put a 3", "put c 4"), db.log.sorted())
        assertEquals("3", db.get("a"))
        assertNull(db.get("b"))
        assertEquals("4", db.get("c"))
    }

    @Test fun `reads and writes still work after a compaction`() {
        val db = MiniCask()
        db.put("x", "old"); db.put("y", "1"); db.put("x", "new")
        db.compact()
        db.put("z", "2")
        assertEquals("new", db.get("x"))
        assertEquals("1", db.get("y"))
        assertEquals("2", db.get("z"))
        val reopened = MiniCask(db.log)
        assertEquals(3, reopened.liveKeys)
        assertEquals("new", reopened.get("x"))
    }
}
