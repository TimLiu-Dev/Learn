import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TierTest {
    private fun tier(code: String): Tier {
        val t = Tier.parse(code)
        assertNotNull("Tier.parse(\"$code\") returned null", t)
        return t!!
    }

    @Test fun `characterisation - the old functions still give the same answers`() {
        val limits = listOf("basic", "student", "premium", "gold").map { loanLimit(it) }
        assertEquals("loanLimit for basic, student, premium, gold", listOf(3, 5, 10, 0), limits)
        val fees = listOf("basic", "student", "premium", "gold").map { code -> listOf(0, 2, 5).map { lateFeeCents(code, it) } }
        assertEquals(
            "lateFeeCents for 0, 2 and 5 days late",
            listOf(listOf(0, 50, 125), listOf(0, 0, 50), listOf(0, 0, 25), listOf(0, 50, 125)),
            fees,
        )
    }

    @Test fun `each tier knows its loan limit`() {
        assertEquals(3, tier("basic").loanLimit)
        assertEquals(5, tier("student").loanLimit)
        assertEquals(10, tier("premium").loanLimit)
    }

    @Test fun `each tier computes its own late fee`() {
        assertEquals("basic, 4 days", 100, tier("basic").lateFeeCents(4))
        assertEquals("student, 3 days of grace", 0, tier("student").lateFeeCents(3))
        assertEquals("student, 4 days", 25, tier("student").lateFeeCents(4))
        assertEquals("premium, 4 days", 0, tier("premium").lateFeeCents(4))
        assertEquals("premium, 10 days", 150, tier("premium").lateFeeCents(10))
        assertEquals("nobody pays for returning early", 0, tier("basic").lateFeeCents(-2))
    }

    @Test fun `an unknown code is not a tier`() {
        assertNull(Tier.parse("gold"))
        assertNull("codes are case-sensitive, as before", Tier.parse("Basic"))
    }

    @Test fun `each tier is its own type, not one class with a switch inside`() {
        val classes = listOf("basic", "student", "premium").map { tier(it)::class }.toSet()
        assertEquals("number of distinct classes", 3, classes.size)
    }
}
