import org.junit.Assert.assertEquals
import org.junit.Test

// A decorator your code has never seen
class Shouting(private val inner: Log) : Log by inner {
    override fun write(line: String) = inner.write(line.uppercase())
}

class LogDecoratorTest {
    private fun Log.writeAll(vararg lines: String) = apply { lines.forEach(::write) }

    @Test fun `each decorator does its one job`() {
        assertEquals(listOf("1: a", "2: b"), Numbered(MemoryLog()).writeAll("a", "b").lines())
        assertEquals(listOf("hello", "hi"), Shortened(MemoryLog(), 5).writeAll("hello world", "hi").lines())
        assertEquals(listOf("a", "b"), SkipBlank(MemoryLog()).writeAll("a", "", "   ", "b").lines())
    }

    @Test fun `numbering outside the filter means nothing is blank any more`() {
        val log = Numbered(SkipBlank(MemoryLog())).writeAll("a", "", "b")
        assertEquals(listOf("1: a", "2: ", "3: b"), log.lines())
    }

    @Test fun `numbering inside the filter counts only what is kept`() {
        val log = SkipBlank(Numbered(MemoryLog())).writeAll("a", "", "b")
        assertEquals(listOf("1: a", "2: b"), log.lines())
    }

    @Test fun `shortening outside the numbers cuts the text, inside it cuts the number too`() {
        val outside = Shortened(Numbered(MemoryLog()), 3).writeAll("abcdef")
        val inside = Numbered(Shortened(MemoryLog(), 3)).writeAll("abcdef")
        assertEquals("text cut, then numbered", listOf("1: abc"), outside.lines())
        assertEquals("numbered, then cut", listOf("1: "), inside.lines())
    }

    @Test fun `a new decorator stacks with yours`() {
        val log = Shouting(SkipBlank(Numbered(MemoryLog()))).writeAll("dune", " ", "emma")
        assertEquals(listOf("1: DUNE", "2: EMMA"), log.lines())
    }
}
