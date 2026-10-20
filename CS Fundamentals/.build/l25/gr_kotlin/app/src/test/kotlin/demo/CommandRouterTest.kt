package demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CommandRouterTest {
    private val router = CommandRouter(Greetings(Config("!")))

    @Test fun `routes a command and passes the remaining args`() =
        assertEquals("Hello, Ada!", router.dispatch(listOf("greet", "Ada")))

    @Test fun `return values are turned into text`() =
        assertEquals("6", router.dispatch(listOf("add", "1", "2", "3")))

    @Test fun `unknown commands get a helpful message`() =
        assertEquals("Unknown command 'fly'. Try 'help'.", router.dispatch(listOf("fly")))

    @Test fun `unannotated methods are not commands`() =
        assertEquals("Unknown command 'internalHelper'. Try 'help'.", router.dispatch(listOf("internalHelper")))

    @Test fun `help is generated from the annotations, sorted`() {
        val expected = """
            Commands:
              add    add N...     sum whole numbers
              greet  greet NAME   say hello
              shout  shout WORDS  uppercase them
        """.trimIndent()
        assertEquals(expected, router.dispatch(listOf("help")))
        assertEquals(expected, router.dispatch(emptyList()))
    }

    @Test fun `an exception inside a handler is reported, not thrown`() {
        val out = router.dispatch(listOf("add", "1", "x"))
        assertTrue(out.startsWith("error in 'add': java.lang.NumberFormatException"), out)
    }

    class Clash {
        @Command(name = "go") fun one(args: List<String>) = "1"
        @Command(name = "go") fun two(args: List<String>) = "2"
    }

    @Test fun `duplicate command names are rejected at startup`() {
        val e = assertFailsWith<IllegalArgumentException> { CommandRouter(Clash()) }
        assertEquals("Duplicate command 'go' in Clash", e.message)
    }

    class BadSignature {
        @Command(name = "bad") fun bad(a: String, b: String) = a + b
    }

    @Test fun `command methods must take one list`() {
        val e = assertFailsWith<IllegalArgumentException> { CommandRouter(BadSignature()) }
        assertEquals("@Command bad must take exactly one List<String>", e.message)
    }
}
