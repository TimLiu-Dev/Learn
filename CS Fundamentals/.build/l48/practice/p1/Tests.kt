import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExhaustionTest {
    @Test fun `implies matches the truth table`() {
        val rows = listOf(true to true, true to false, false to true, false to false)
        assertEquals(listOf(true, false, true, true), rows.map { (p, q) -> implies(p, q) })
    }

    @Test fun `the contrapositive is equivalent`() {
        assertTrue(equivalent({ p, q -> implies(p, q) }, { p, q -> implies(!q, !p) }))
    }

    @Test fun `the converse is not equivalent`() {
        assertFalse("p -> q and q -> p differ when p != q", equivalent({ p, q -> implies(p, q) }, { p, q -> implies(q, p) }))
    }

    @Test fun `De Morgan's law holds`() {
        assertTrue(equivalent({ a, b -> !(a && b) }, { a, b -> !a || !b }))
        assertTrue(equivalent({ a, b -> !(a || b) }, { a, b -> !a && !b }))
    }

    @Test fun `tautologies are found and non-tautologies are not`() {
        assertTrue("(p && q) -> p", alwaysTrue { p, q -> implies(p && q, p) })
        assertFalse("p -> q is false when p is true and q is false", alwaysTrue { p, q -> implies(p, q) })
    }
}
