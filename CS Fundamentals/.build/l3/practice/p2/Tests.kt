import org.junit.Assert.assertEquals
import org.junit.Test

class FunctionsTest {
    @Test fun `every initial is followed by the suffix`() {
        assertEquals("A.L.", initials("Ada", "Lovelace"))
    }

    @Test fun `initials are uppercased`() {
        assertEquals("G.B.H.", initials("grace", "brewster", "hopper"))
    }

    @Test fun `a named suffix replaces the dot, and no names give nothing`() {
        assertEquals("AT", initials("alan", "turing", suffix = ""))
        assertEquals("no names", "", initials())
    }

    @Test fun `an array can be spread into the varargs`() {
        val parts = arrayOf("linus", "benedict", "torvalds")
        assertEquals("L.B.T.", initials(*parts))
    }

    @Test fun `clamp falls back on its defaults`() {
        assertEquals("150 is above the default max", 100, clamp(150))
        assertEquals("-5 is below the default min", 0, clamp(-5))
        assertEquals("42 is inside", 42, clamp(42))
    }

    @Test fun `clamp with one named bound keeps the other default`() {
        assertEquals("max = 40", 40, clamp(50, max = 40))
        assertEquals("min = 10", 10, clamp(5, min = 10))
        assertEquals("both bounds included", 10, clamp(10, min = 10, max = 20))
    }
}
