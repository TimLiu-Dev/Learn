import org.junit.Assert.assertEquals
import org.junit.Test

class LazyTest {
    private val calls = mutableListOf<Int>()
    private fun evenNames(id: Int): String? {
        calls += id
        return if (id % 2 == 0) "user$id" else null
    }

    @Test fun `firstFound keeps the first n names`() {
        assertEquals(listOf("user2", "user4"), firstFound((1..10).toList(), 2, ::evenNames))
    }

    @Test fun `firstFound stops looking up once it has n names`() {
        firstFound((1..10).toList(), 2, ::evenNames)
        assertEquals("ids passed to lookup", listOf(1, 2, 3, 4), calls)
    }

    @Test fun `firstFound returns fewer names when the ids run out`() {
        assertEquals(listOf("user2"), firstFound(listOf(1, 2, 3), 5, ::evenNames))
    }

    @Test fun `asking for zero names looks nothing up`() {
        assertEquals(emptyList<String>(), firstFound((1..10).toList(), 0, ::evenNames))
        assertEquals("ids passed to lookup", emptyList<Int>(), calls)
    }

    @Test fun `collatz ends with 1`() {
        assertEquals(listOf(6, 3, 10, 5, 16, 8, 4, 2, 1), collatz(6).toList())
        assertEquals(listOf(1), collatz(1).toList())
    }

    @Test fun `collatz works with take on a long run`() {
        assertEquals(listOf(27, 82, 41, 124, 62), collatz(27).take(5).toList())
        assertEquals("numbers in collatz(27)", 112, collatz(27).count())
    }
}
