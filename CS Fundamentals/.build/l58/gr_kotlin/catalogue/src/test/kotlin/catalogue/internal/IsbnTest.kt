package catalogue.internal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IsbnTest {
    @Test fun `hyphens don't matter, digits do`() {
        assertEquals(Isbn.parse("978-0-441-47812-5"), Isbn.parse("9780441478125"))
    }

    @Test fun `a wrong check digit is refused`() {
        assertFailsWith<IllegalArgumentException> { Isbn.parse("978-0-441-47812-4") }
    }

    @Test fun `twelve digits are refused`() {
        assertFailsWith<IllegalArgumentException> { Isbn.parse("978044147812") }
    }
}
