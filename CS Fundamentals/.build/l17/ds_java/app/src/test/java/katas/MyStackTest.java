package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class MyStackTest {
    @Test void popReturnsTheMostRecentlyPushedItem() {
        MyStack<String> stack = new MyStack<>();
        stack.push("a"); stack.push("b"); stack.push("c");
        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals(1, stack.size());
    }

    @Test void peekLooksWithoutRemoving() {
        MyStack<Integer> stack = new MyStack<>();
        stack.push(7);
        assertEquals(7, stack.peek());
        assertEquals(1, stack.size());
    }

    @Test void peekOnAnEmptyStackIsNull() {
        assertNull(new MyStack<Integer>().peek());
    }

    @Test void popOnAnEmptyStackThrows() {
        assertThrows(NoSuchElementException.class, () -> new MyStack<Integer>().pop());
    }
}
