import org.junit.Assert.assertEquals
import org.junit.Test

class AtMostKTest {
    @Test fun `eceba with k = 2 gives ece`() {
        assertEquals("ece", longestAtMostK("eceba", 2))
    }

    @Test fun `k = 1 finds the longest run of one letter`() {
        assertEquals("bbb", longestAtMostK("abbbcc", 1))
    }

    @Test fun `a tie goes to the window that starts first`() {
        assertEquals("aabb", longestAtMostK("aabbcc", 2))
    }

    @Test fun `the window shrinks until a letter really leaves`() {
        assertEquals("accc", longestAtMostK("abaccc", 2))
    }

    @Test fun `a large enough k takes the whole string`() {
        assertEquals("abcabc", longestAtMostK("abcabc", 3))
        assertEquals("abcabc", longestAtMostK("abcabc", 10))
    }

    @Test fun `k = 0 and the empty string give an empty answer`() {
        assertEquals("k = 0", "", longestAtMostK("abc", 0))
        assertEquals("empty string", "", longestAtMostK("", 2))
    }
}
