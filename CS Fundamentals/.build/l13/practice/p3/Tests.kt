import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FavoritesTest {
    @Test fun `what you put in comes back out`() {
        val f = Favorites()
        f.put("Kotlin")
        assertEquals("Kotlin", f.get<String>())
    }

    @Test fun `each type has its own slot`() {
        val f = Favorites()
        f.put("tea")
        f.put(42)
        f.put(2.5)
        assertEquals("tea", f.get<String>())
        assertEquals(42, f.get<Int>())
        assertEquals(2.5, f.get<Double>()!!, 0.0)
    }

    @Test fun `a type with nothing stored gives null`() {
        val f = Favorites()
        f.put("tea")
        assertNull(f.get<Boolean>())
    }

    @Test fun `putting the same type again replaces the old value`() {
        val f = Favorites()
        f.put(1)
        f.put(2)
        assertEquals(2, f.get<Int>())
    }

    @Test fun `the slot is chosen by the type argument, not the runtime class`() {
        val f = Favorites()
        f.put<CharSequence>("hi")
        assertEquals("get<CharSequence>()", "hi", f.get<CharSequence>())
        assertNull("get<String>() should be empty: nothing was put as a String", f.get<String>())
    }
}
