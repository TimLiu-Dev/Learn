import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegistryTest {
    @Test fun `the points follow the new name`() {
        val r = Registry()
        val ada = Member("ada", "London")
        r.add(ada, 100)
        r.rename(ada, "ada lovelace")
        assertEquals(100, r.pointsOf(Member("ada lovelace", "London")))
    }

    @Test fun `the old name is gone`() {
        val r = Registry()
        val ada = Member("ada", "London")
        r.add(ada, 100)
        r.rename(ada, "ada lovelace")
        assertNull("points under the old name", r.pointsOf(Member("ada", "London")))
        assertEquals("size", 1, r.size)
    }

    @Test fun `rename returns the new key`() {
        val r = Registry()
        r.add(Member("ada", "London"), 100)
        assertEquals(Member("ada lovelace", "London"), r.rename(Member("ada", "London"), "ada lovelace"))
    }

    @Test fun `the caller's member object is left alone`() {
        val r = Registry()
        val ada = Member("ada", "London")
        r.add(ada, 100)
        r.rename(ada, "ada lovelace")
        assertEquals("ada", ada.name)
    }

    @Test fun `an unknown member is not renamed`() {
        val r = Registry()
        r.add(Member("ada", "London"), 100)
        assertNull(r.rename(Member("bob", "Paris"), "rob"))
        assertEquals("size", 1, r.size)
        assertEquals(100, r.pointsOf(Member("ada", "London")))
    }
}
