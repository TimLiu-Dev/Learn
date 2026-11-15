import org.junit.Assert.assertEquals
import org.junit.Test

fun listOfValues(vararg values: String): DoublyList<String> {
    val list = DoublyList<String>()
    for (v in values) list.addLast(v)
    return list
}

class ReverseTest {
    @Test fun `reversing an empty list leaves it empty`() {
        val list = DoublyList<String>()
        list.reverse()
        assertEquals(emptyList<String>(), list.toList())
        assertEquals(emptyList<String>(), list.backwards())
        assertEquals(0, list.size)
    }

    @Test fun `a one-element list stays the same`() {
        val list = listOfValues("A")
        list.reverse()
        assertEquals(listOf("A"), list.toList())
        assertEquals(listOf("A"), list.backwards())
    }

    @Test fun `reverse flips the order in both directions`() {
        val list = listOfValues("A", "B", "C", "D")
        list.reverse()
        assertEquals("forwards", listOf("D", "C", "B", "A"), list.toList())
        assertEquals("backwards", listOf("A", "B", "C", "D"), list.backwards())
        assertEquals(4, list.size)
    }

    @Test fun `adds land at the right ends after a reverse`() {
        val list = listOfValues("A", "B", "C")
        list.reverse()
        list.addLast("Z")
        list.addFirst("Y")
        assertEquals("forwards", listOf("Y", "C", "B", "A", "Z"), list.toList())
        assertEquals("backwards", listOf("Z", "A", "B", "C", "Y"), list.backwards())
    }

    @Test fun `a node you hold still works after a reverse`() {
        val list = DoublyList<String>()
        list.addLast("A")
        val b = list.addLast("B")
        list.addLast("C")
        list.reverse()
        assertEquals("B", list.remove(b))
        assertEquals("forwards", listOf("C", "A"), list.toList())
        assertEquals("backwards", listOf("A", "C"), list.backwards())
    }

    @Test fun `reversing twice gives the original back`() {
        val list = listOfValues("A", "B", "C")
        list.reverse()
        list.reverse()
        assertEquals(listOf("A", "B", "C"), list.toList())
        assertEquals(listOf("C", "B", "A"), list.backwards())
    }
}
