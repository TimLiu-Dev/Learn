import java.util.SortedMap
import java.util.TreeMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MiniLsmTest {
    private fun sorted(vararg pairs: Pair<String, String?>): SortedMap<String, String?> = TreeMap(mapOf(*pairs))

    @Test fun `a full memtable becomes the newest run`() {
        val db = MiniLsm(memLimit = 3)
        db.put("c", "3"); db.put("a", "1"); db.put("b", "2")
        db.put("d", "4")
        assertEquals("runs after three puts and a flush", listOf(sorted("a" to "1", "b" to "2", "c" to "3")), db.runs)
        assertEquals("d is still in the memtable", "4", db.get("d"))
    }

    @Test fun `the newest version wins across runs`() {
        val db = MiniLsm(memLimit = 3)
        db.put("a", "old"); db.put("b", "1"); db.put("c", "2")
        db.put("a", "new"); db.put("x", "3"); db.put("y", "4")
        assertEquals("two runs", 2, db.runs.size)
        assertEquals("new", db.get("a"))
        assertEquals("1", db.get("b"))
    }

    @Test fun `a tombstone hides every older value`() {
        val db = MiniLsm(memLimit = 3)
        db.put("a", "1"); db.put("b", "2"); db.put("c", "3")
        db.delete("a"); db.put("x", "4"); db.put("y", "5")
        assertNull("a's tombstone is in the newer run", db.get("a"))
        db.delete("b")
        assertNull("b's tombstone is still in the memtable", db.get("b"))
        assertEquals("3", db.get("c"))
    }

    @Test fun `mergeRuns keeps the newest version of each key`() {
        val newer = sorted("a" to "new", "c" to null)
        val older = sorted("a" to "old", "b" to "b1", "c" to "c1")
        val merged = mergeRuns(listOf(newer, older), dropTombstones = false)
        assertEquals(sorted("a" to "new", "b" to "b1", "c" to null), merged)
        assertEquals("keys in order", listOf("a", "b", "c"), merged.keys.toList())
    }

    @Test fun `a full merge drops tombstones and the values they hide`() {
        val newer = sorted("a" to "new", "c" to null)
        val older = sorted("a" to "old", "b" to "b1", "c" to "c1")
        assertEquals(sorted("a" to "new", "b" to "b1"), mergeRuns(listOf(newer, older), dropTombstones = true))
    }

    @Test fun `compaction changes no answer`() {
        val db = MiniLsm(memLimit = 4)
        val truth = HashMap<String, String>()
        for (i in 0 until 40) {
            val key = "k${i * 7 % 11}"
            if (i % 5 == 4) { db.delete(key); truth.remove(key) } else { db.put(key, "v$i"); truth[key] = "v$i" }
        }
        db.compact()
        assertEquals("one run after compact", 1, db.runs.size)
        for (k in 0 until 11) assertEquals("get(k$k)", truth["k$k"], db.get("k$k"))
    }
}
