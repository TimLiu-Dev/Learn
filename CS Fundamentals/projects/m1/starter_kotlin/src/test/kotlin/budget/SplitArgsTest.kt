package budget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Example tests for the code the starter gives you. They pass.
class SplitArgsTest {
    @Test
    fun `splits command, file and options`() {
        val raw = splitArgs(listOf("report", "s.csv", "--rules", "r.txt", "--month", "2026-03"))
        assertEquals(RawArgs("report", listOf("s.csv"), mapOf("--rules" to "r.txt", "--month" to "2026-03"), help = false), raw)
    }

    @Test
    fun `notices --help anywhere`() {
        assertTrue(splitArgs(listOf("top", "--help")).help)
    }
}
