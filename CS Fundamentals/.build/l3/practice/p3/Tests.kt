import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NullsTest {
    private val bosses = mapOf("ada" to "grace", "grace" to "linus")

    @Test fun `a plain number is an age`() {
        assertEquals(42, parseAge("42"))
        assertEquals(0, parseAge("0"))
        assertEquals(150, parseAge("150"))
    }

    @Test fun `null and junk give null`() {
        assertNull("null text", parseAge(null))
        assertNull("not a number", parseAge("abc"))
        assertNull("empty text", parseAge(""))
    }

    @Test fun `ages outside 0 to 150 give null`() {
        assertNull("-1", parseAge("-1"))
        assertNull("151", parseAge("151"))
    }

    @Test fun `the boss of the boss is two steps up`() {
        assertEquals("linus", bossOfBoss("ada", bosses))
    }

    @Test fun `a missing step gives null, not a crash`() {
        assertNull("linus has no boss, so grace's boss has none either", bossOfBoss("grace", bosses))
        assertNull("alan isn't in the map at all", bossOfBoss("alan", bosses))
    }
}
