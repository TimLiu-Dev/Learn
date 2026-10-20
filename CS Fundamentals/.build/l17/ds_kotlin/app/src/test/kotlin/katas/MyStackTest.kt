package katas

import kotlin.test.*

class MyStackTest {
    @Test fun `pop returns the most recently pushed item`() {
        val stack = MyStack<String>()
        stack.push("a"); stack.push("b"); stack.push("c")
        assertEquals("c", stack.pop())
        assertEquals("b", stack.pop())
        assertEquals(1, stack.size)
    }

    @Test fun `peek looks without removing`() {
        val stack = MyStack<Int>()
        stack.push(7)
        assertEquals(7, stack.peek())
        assertEquals(1, stack.size)
    }

    @Test fun `peek on an empty stack is null`() {
        assertNull(MyStack<Int>().peek())
    }

    @Test fun `pop on an empty stack throws`() {
        assertFailsWith<NoSuchElementException> { MyStack<Int>().pop() }
    }
}
