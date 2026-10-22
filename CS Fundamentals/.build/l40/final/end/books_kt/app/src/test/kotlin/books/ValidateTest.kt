package books

import kotlin.test.Test
import kotlin.test.assertEquals

// Plain function, plain tests: no server, no HTTP, runs in milliseconds.
class ValidateTest {
    @Test fun `a valid book has no errors`() {
        assertEquals(emptyList(), validate(NewBook("Dune", 1965)))
    }

    @Test fun `missing fields are both reported`() {
        assertEquals(listOf("title is required", "year is required"), validate(NewBook()))
    }

    @Test fun `a blank title and a far-future year are both reported`() {
        assertEquals(2, validate(NewBook("  ", 3000)).size)
    }
}
