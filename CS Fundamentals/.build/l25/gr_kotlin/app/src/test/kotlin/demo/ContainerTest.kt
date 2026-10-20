package demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContainerTest {
    @Test fun `constructor dependencies are injected by type`() {
        val c = Container()
        c.register(Config::class, Config("?"))
        assertEquals("Hello, Bo?", c.create(Greetings::class).greet(listOf("Bo")))
    }

    @Test fun `a missing dependency fails with a clear message`() {
        val e = assertFailsWith<IllegalStateException> { Container().create(Greetings::class) }
        assertEquals("No instance registered for demo.Config (needed by Greetings)", e.message)
    }
}
