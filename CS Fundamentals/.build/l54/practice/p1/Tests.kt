import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TokenizeTest {
    @Test fun `numbers can have many digits`() {
        assertEquals(listOf("12", "+", "345"), tokenize("12+345"))
    }

    @Test fun `spaces separate tokens but never become tokens`() {
        assertEquals(listOf("1", "*", "(", "2", "-", "30", ")"), tokenize("  1 *( 2-  30 ) "))
        assertEquals("only spaces", emptyList<String>(), tokenize("   "))
    }

    @Test fun `a space splits a number in two`() {
        assertEquals(listOf("1", "2"), tokenize("1 2"))
    }

    @Test fun `the longest operator wins`() {
        assertEquals(listOf("2", "**", "3", "*", "4"), tokenize("2**3*4"))
        assertEquals(listOf("2", "**", "*", "3"), tokenize("2***3"))
        assertEquals(listOf("2", "*", "3"), tokenize("2*3"))
    }

    @Test fun `an unknown character is reported with its column`() {
        val e = assertThrows(CalcError::class.java) { tokenize("1 + #") }
        assertEquals("unexpected '#' at column 5", e.message)
    }

    @Test fun `the error is found even after a long number`() {
        val e = assertThrows(CalcError::class.java) { tokenize("123x") }
        assertEquals("unexpected 'x' at column 4", e.message)
    }
}
