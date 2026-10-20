package katas

import kotlin.test.*

class MyArrayListTest {
    private fun listOfAbc() = MyArrayList<String>().apply { add("a"); add("b"); add("c") }

    @Test fun `a new list is empty`() {
        val list = MyArrayList<String>()
        assertEquals(0, list.size)
        assertTrue(list.isEmpty())
    }

    @Test fun `add then get returns the element`() {
        val list = MyArrayList<String>()
        list.add("a")
        assertEquals(1, list.size)
        assertEquals("a", list[0])
    }

    @Test fun `get outside 0 until size throws`() {
        val list = listOfAbc()
        assertFailsWith<IndexOutOfBoundsException> { list[3] }
        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
    }

    @Test fun `capacity doubles when full`() {
        val list = MyArrayList<Int>(initialCapacity = 4)
        repeat(4) { list.add(it) }
        assertEquals(4, list.capacity)
        list.add(4)
        assertEquals(8, list.capacity)
    }

    @Test fun `keeps every element across many resizes`() {
        val list = MyArrayList<Int>(initialCapacity = 1)
        repeat(1000) { list.add(it * it) }
        assertEquals(1000, list.size)
        for (i in 0 until 1000) assertEquals(i * i, list[i])
    }

    @Test fun `set replaces and returns the old value`() {
        val list = listOfAbc()
        assertEquals("b", list.set(1, "B"))
        assertEquals("B", list[1])
        assertFailsWith<IndexOutOfBoundsException> { list[3] = "x" }
    }

    @Test fun `add at index shifts later elements right`() {
        val list = listOfAbc()
        list.add(1, "X")
        assertEquals("[a, X, b, c]", list.toString())
        list.add(0, "first")
        list.add(list.size, "last")
        assertEquals("[first, a, X, b, c, last]", list.toString())
        assertFailsWith<IndexOutOfBoundsException> { list.add(list.size + 1, "nope") }
    }

    @Test fun `removeAt shifts left and returns the removed element`() {
        val list = listOfAbc()
        assertEquals("a", list.removeAt(0))
        assertEquals("[b, c]", list.toString())
        assertEquals("c", list.removeAt(1))
        assertEquals(1, list.size)
        assertFailsWith<IndexOutOfBoundsException> { list.removeAt(1) }
    }

    @Test fun `for loop visits elements in order`() {
        val seen = mutableListOf<String>()
        for (s in listOfAbc()) seen.add(s)
        assertEquals(listOf("a", "b", "c"), seen)
    }

    @Test fun `toString of an empty list`() {
        assertEquals("[]", MyArrayList<Int>().toString())
    }

    @Test fun `nulls are allowed when T is nullable`() {
        val list = MyArrayList<String?>()
        list.add(null)
        assertNull(list[0])
    }

    @Test fun `capacity below 1 is rejected`() {
        assertFailsWith<IllegalArgumentException> { MyArrayList<Int>(0) }
    }
}
