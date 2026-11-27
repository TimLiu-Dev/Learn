package katas

import kotlin.test.Test
import kotlin.test.assertEquals

class FlakyGreeterTest {
    @Test
    fun `greets the morning shift`() {
        assertEquals("Good morning", greetingNow())   // written at 9 a.m., so it passed... then
    }
}
