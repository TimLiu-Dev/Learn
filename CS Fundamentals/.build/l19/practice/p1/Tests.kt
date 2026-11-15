import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ProbingMapTest {
    @Test fun `get returns what put stored`() {
        val m = ProbingMap(8)
        m.put("cat", 1)
        m.put("dog", 2)
        assertEquals(1, m.get("cat"))
        assertEquals(2, m.get("dog"))
        assertNull("a key never put", m.get("cow"))
    }

    @Test fun `putting an existing key replaces its value`() {
        val m = ProbingMap(8)
        m.put("cat", 1)
        m.put("cat", 5)
        assertEquals(5, m.get("cat"))
        assertEquals("size", 1, m.size)
    }

    @Test fun `colliding keys sit in neighbouring slots`() {
        val m = ProbingMap(8)
        m.put("Aa", 1)                     // hash 2112, home slot 0
        m.put("BB", 2)                     // hash 2112 too
        assertEquals(listOf("Aa", "BB", null, null, null, null, null, null), m.slots())
        assertEquals(2, m.get("BB"))
    }

    @Test fun `a negative hash code still gets a valid slot`() {
        val m = ProbingMap(16)
        m.put("banana", 6)                 // hash -1396355227
        assertEquals("banana's home slot", "banana", m.slots()[5])
        assertEquals(6, m.get("banana"))
    }

    @Test fun `probing wraps around to slot 0`() {
        val m = ProbingMap(4)
        m.put("c", 1)                      // 99 floor-mod 4 = 3, the last slot
        m.put("g", 2)                      // 103 floor-mod 4 = 3, taken: wrap to 0
        assertEquals(listOf("g", null, null, "c"), m.slots())
        assertEquals(2, m.get("g"))
    }

    @Test fun `a full table still answers but rejects new keys`() {
        val m = ProbingMap(2)
        m.put("a", 1)
        m.put("b", 2)
        assertNull("a missing key in a full table", m.get("z"))
        m.put("a", 9)                      // replacing is fine
        assertEquals(9, m.get("a"))
        assertThrows(IllegalStateException::class.java) { m.put("c", 3) }
    }
}
