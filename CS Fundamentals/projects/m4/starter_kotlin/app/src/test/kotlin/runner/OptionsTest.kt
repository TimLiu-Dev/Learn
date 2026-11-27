package runner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OptionsTest {
    @Test
    fun `fills in the defaults`() {
        assertEquals(Options("jobs.txt", workers = 4, retries = 0, timeoutMs = 0, queue = 16, graceMs = 3000),
            Options.parse(arrayOf("jobs.txt", "--workers", "4")))
    }

    @Test
    fun `rejects zero workers with a message that says why`() {
        val e = assertFailsWith<InputError> { Options.parse(arrayOf("jobs.txt", "--workers", "0")) }
        assertEquals("--workers must be at least 1, got 0", e.message)
    }
}
