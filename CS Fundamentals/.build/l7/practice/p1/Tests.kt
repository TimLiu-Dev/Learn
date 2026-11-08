import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailTest {
    @Test fun `letter case does not matter`() {
        assertEquals(Email("ana@x.io"), Email("Ana@X.io"))
        assertNotEquals(Email("ana@x.io"), Email("bob@x.io"))
    }

    @Test fun `an email never equals another type or null`() {
        assertFalse("equals a String?", Email("ana@x.io").equals("ana@x.io"))
        assertFalse("equals null?", Email("ana@x.io").equals(null))
    }

    @Test fun `equal emails have equal hash codes`() {
        assertEquals(Email("ana@x.io").hashCode(), Email("ANA@X.IO").hashCode())
    }

    @Test fun `a hash set keeps one per address`() {
        val set = hashSetOf(Email("Ana@X.io"), Email("ana@x.io"), Email("ANA@x.IO"))
        assertEquals("set size", 1, set.size)
        assertTrue("contains an equal email", set.contains(Email("aNa@x.Io")))
    }

    @Test fun `a map finds a value by an equal key`() {
        val names = hashMapOf(Email("ana@x.io") to "Ana")
        assertEquals("Ana", names[Email("Ana@X.io")])
    }

    @Test fun `toString shows the address as typed`() {
        assertEquals("Email(Ana@X.io)", Email("Ana@X.io").toString())
    }
}
