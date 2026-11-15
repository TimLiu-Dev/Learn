import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Compiles only because myFind is inline: the bare return leaves describe() itself
fun describe(words: List<String>): String {
    val long = words.myFind { w ->
        if (w.isEmpty()) return "blank word before any long one"
        w.length > 5
    }
    return "first long word: $long"
}

class InlineOpsTest {
    @Test fun `myFind returns the first match`() {
        assertEquals("avocado", listOf("apple", "avocado", "banana", "artichoke").myFind { it.length > 5 })
    }

    @Test fun `myFind gives null when nothing matches`() {
        assertNull(listOf(1, 3, 5).myFind { it % 2 == 0 })
        assertNull(emptyList<Int>().myFind { true })
    }

    @Test fun `myFind stops at the first match`() {
        var calls = 0
        listOf(4, 8, 15, 16, 23).myFind { calls++; it > 10 }
        assertEquals("predicate calls", 3, calls)
    }

    @Test fun `myTakeWhile stops at the first miss`() {
        assertEquals(listOf(1, 2), listOf(1, 2, 5, 1).myTakeWhile { it < 3 })
        assertEquals(emptyList<Int>(), listOf(9, 1).myTakeWhile { it < 3 })
        assertEquals(listOf("a", "b"), listOf("a", "b").myTakeWhile { true })
    }

    @Test fun `a bare return in the lambda leaves the caller`() {
        assertEquals("blank word before any long one", describe(listOf("hi", "", "banana")))
        assertEquals("first long word: banana", describe(listOf("hi", "banana", "")))
    }
}
