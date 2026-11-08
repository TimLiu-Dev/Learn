import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class TinyJUnitTest {
    private fun messageOf(block: () -> Unit): String? {
        try {
            block()
        } catch (e: AssertionError) {
            return e.message
        }
        fail("expected an AssertionError, but nothing was thrown")
        return null
    }

    @Test fun `equal values pass quietly`() {
        expectEqual(4, 2 + 2)
        expectEqual("$12.05", "$" + "12.05")
        expectEqual(listOf(1, 2), listOf(1, 2))   // == means equals, so contents count
        expectEqual(null, null)
    }

    @Test fun `a mismatch throws an AssertionError with expected first`() {
        assertEquals("expected: <\$12.05> but was: <\$12.5>", messageOf { expectEqual("$12.05", "$12.5") })
    }

    @Test fun `a passing test is reported as PASS`() {
        assertEquals(listOf("PASS adds"), runAll(mapOf("adds" to { expectEqual(4, 2 + 2) })))
    }

    @Test fun `a failing test is reported with its message`() {
        val report = runAll(mapOf("pads cents" to { expectEqual("$12.05", "$12.5") }))
        assertEquals(listOf("FAIL pads cents: expected: <\$12.05> but was: <\$12.5>"), report)
    }

    @Test fun `one failure does not stop the tests after it`() {
        val report = runAll(mapOf(
            "first" to { expectEqual(1, 1) },
            "second" to { expectEqual(1, 2) },
            "third" to { expectEqual("a", "a") },
        ))
        assertEquals(listOf("PASS first", "FAIL second: expected: <1> but was: <2>", "PASS third"), report)
    }
}
