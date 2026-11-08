import org.junit.Assert.assertEquals
import org.junit.Test

class ChangedKeysTest {
    private val none = emptyList<String>()

    @Test fun `small unchanged counts are not reported`() {
        assertEquals(none, changedKeys(mapOf("nuts" to 12), mapOf("nuts" to 12)))
    }

    @Test fun `big unchanged counts are not reported`() {
        assertEquals("500 is still 500", none, changedKeys(mapOf("bolts" to 500), mapOf("bolts" to 500)))
    }

    @Test fun `real changes are reported, sorted`() {
        val before = mapOf("nuts" to 12, "bolts" to 500, "gears" to 1000)
        val after = mapOf("nuts" to 13, "bolts" to 500, "gears" to 999)
        assertEquals(listOf("gears", "nuts"), changedKeys(before, after))
    }

    @Test fun `a count that became unknown is a change`() {
        assertEquals(listOf("bolts"), changedKeys(mapOf("bolts" to 500), mapOf("bolts" to null)))
    }

    @Test fun `two unknown counts are not a change`() {
        assertEquals(none, changedKeys(mapOf("bolts" to null), mapOf("bolts" to null)))
    }

    @Test fun `added and removed items are changes`() {
        assertEquals(listOf("gears", "nuts"), changedKeys(mapOf("nuts" to 5), mapOf("gears" to 5)))
    }
}
