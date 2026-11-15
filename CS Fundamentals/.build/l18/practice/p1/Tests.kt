import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

fun listOfNodes(vararg xs: Int): Node? = xs.foldRight(null as Node?) { v, rest -> Node(v, rest) }

fun Node?.values(): List<Int> {
    val out = mutableListOf<Int>()
    var n = this
    while (n != null) { out += n.value; n = n.next }
    return out
}

class LinkedRecursionTest {
    @Test fun `an empty list has no max`() {
        assertNull(maxValue(null))
    }

    @Test fun `max of one element is that element`() {
        assertEquals(7, maxValue(listOfNodes(7)))
    }

    @Test fun `max works when every value is negative`() {
        assertEquals("max of -5, -1, -9", -1, maxValue(listOfNodes(-5, -1, -9)))
    }

    @Test fun `max finds the largest anywhere`() {
        assertEquals(9, maxValue(listOfNodes(3, 9, 2, 9, 4)))
    }

    @Test fun `without removes every copy, including the head`() {
        assertEquals(listOf(1, 3), without(listOfNodes(2, 2, 1, 2, 3, 2), 2).values())
        assertEquals("nothing removed", listOf(1, 3), without(listOfNodes(1, 3), 5).values())
        assertNull("empty in, empty out", without(null, 1))
        assertNull("all removed", without(listOfNodes(4, 4), 4))
    }

    @Test fun `without leaves the original list alone`() {
        val original = listOfNodes(1, 2, 3)
        without(original, 2)
        assertEquals(listOf(1, 2, 3), original.values())
    }
}
