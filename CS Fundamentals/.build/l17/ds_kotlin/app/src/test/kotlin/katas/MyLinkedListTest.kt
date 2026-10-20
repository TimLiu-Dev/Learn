package katas

import kotlin.test.*

class MyLinkedListTest {
    @Test fun `new list is empty`() {
        val list = MyLinkedList<String>()
        assertTrue(list.isEmpty())
        assertEquals(0, list.size)
    }

    @Test fun `addFirst puts items at the front`() {
        val list = MyLinkedList<String>()
        list.addFirst("B"); list.addFirst("A")
        assertEquals("A", list[0])
        assertEquals("B", list[1])
        assertEquals(2, list.size)
    }

    @Test fun `addLast puts items at the back`() {
        val list = MyLinkedList<String>()
        list.addLast("A"); list.addLast("B"); list.addFirst("start")
        assertEquals(listOf("start", "A", "B"), list.toList())
    }

    @Test fun `get outside the list throws`() {
        val list = MyLinkedList<String>()
        list.addLast("A")
        assertFailsWith<IndexOutOfBoundsException> { list[1] }
        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
    }

    @Test fun `removeFirst returns items in order`() {
        val list = MyLinkedList<Int>()
        list.addLast(1); list.addLast(2)
        assertEquals(1, list.removeFirst())
        assertEquals(2, list.removeFirst())
        assertTrue(list.isEmpty())
    }

    @Test fun `removeFirst on an empty list throws`() {
        assertFailsWith<NoSuchElementException> { MyLinkedList<Int>().removeFirst() }
    }

    @Test fun `addLast still works after the list was emptied`() {
        val list = MyLinkedList<String>()
        list.addLast("A")
        list.removeFirst()
        list.addLast("B")                           // catches a stale tail pointer
        assertEquals(listOf("B"), list.toList())
    }

    @Test fun `reverse flips the order and moves the tail`() {
        val list = MyLinkedList<Int>()
        for (i in 1..4) list.addLast(i)
        list.reverse()
        list.addLast(0)                             // must land after the OLD head
        assertEquals(listOf(4, 3, 2, 1, 0), list.toList())
    }

    @Test fun `for loop visits every item in order`() {
        val list = MyLinkedList<String>()
        list.addLast("x"); list.addLast("y"); list.addLast("z")
        val seen = StringBuilder()
        for (s in list) seen.append(s)
        assertEquals("xyz", seen.toString())
    }
}
