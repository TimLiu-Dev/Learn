package katas

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class GreeterTest {
    private fun clockAt(instant: String) = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC)

    @Test
    fun `one second before noon is still morning`() {
        assertEquals("Good morning", Greeter(clockAt("2026-03-02T11:59:59Z")).greeting())
    }

    @Test
    fun `noon is afternoon`() {
        assertEquals("Good afternoon", Greeter(clockAt("2026-03-02T12:00:00Z")).greeting())
    }
}
