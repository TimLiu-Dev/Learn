import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreviewTest {
    @Test fun `composed and decomposed e-acute are the same text`() {
        assertTrue(sameText("café", "café"))
    }

    @Test fun `different text stays different`() {
        assertFalse(sameText("cafe", "café"))
    }

    @Test fun `text that fits comes back unchanged`() {
        assertEquals("Ada", preview("Ada", 5))
        assertEquals("exactly max characters: no ellipsis", "Ada", preview("Ada", 3))
    }

    @Test fun `long text is cut and marked`() {
        assertEquals("Hello…", preview("Hello world", 5))
    }

    @Test fun `an emoji counts as one character`() {
        assertEquals("😀😀…", preview("😀😀😀", 2))
    }

    @Test fun `an accent stays with its letter`() {
        assertEquals("café…", preview("café au lait", 4))
    }
}
