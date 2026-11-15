import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleTest {
    @Test fun `minLength counts characters`() {
        assertFalse("7 characters", minLength(8).check("hunter2"))
        assertTrue("exactly 8 characters", minLength(8).check("hunter22"))
    }

    @Test fun `each minLength rule keeps its own limit`() {
        val short = minLength(3)
        val long = minLength(10)
        assertTrue(short.check("abcd"))
        assertFalse(long.check("abcd"))
    }

    @Test fun `hasDigit finds a digit anywhere`() {
        assertTrue(hasDigit.check("abc7def"))
        assertFalse(hasDigit.check("abcdef"))
        assertFalse("an empty password has no digit", hasDigit.check(""))
    }

    @Test fun `failures lists the broken rules in order`() {
        val rules = mapOf(
            "length" to minLength(8),
            "digit" to hasDigit,
            "no spaces" to Rule { ' ' !in it },
        )
        assertEquals(listOf("length", "digit", "no spaces"), failures("a b", rules))
        assertEquals(listOf("digit"), failures("correcthorse", rules))
        assertEquals(emptyList<String>(), failures("correct7horse", rules))
    }

    @Test fun `no rules means no failures`() {
        assertEquals(emptyList<String>(), failures("x", emptyMap()))
    }
}
