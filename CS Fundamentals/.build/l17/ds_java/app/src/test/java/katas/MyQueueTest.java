package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MyQueueTest {
    private static <T> List<T> drain(MyQueue<T> q) {
        List<T> out = new ArrayList<>();
        while (!q.isEmpty()) out.add(q.poll());
        return out;
    }

    @Test void pollReturnsItemsInArrivalOrder() {
        MyQueue<String> q = new MyQueue<>();
        q.offer("a"); q.offer("b"); q.offer("c");
        assertEquals("a", q.poll());
        assertEquals("b", q.poll());
        assertEquals(1, q.size());
    }

    @Test void pollAndPeekOnAnEmptyQueueAreNull() {
        MyQueue<String> q = new MyQueue<>();
        assertNull(q.poll());
        assertNull(q.peek());
    }

    @Test void peekLooksWithoutRemoving() {
        MyQueue<Integer> q = new MyQueue<>();
        q.offer(1); q.offer(2);
        assertEquals(1, q.peek());
        assertEquals(2, q.size());
    }

    @Test void wrapsAroundTheEndOfTheArray() {
        MyQueue<String> q = new MyQueue<>(4);
        q.offer("A"); q.offer("B"); q.offer("C");
        q.poll(); q.poll();
        q.offer("D"); q.offer("E"); q.offer("F");   // E and F wrap to slots 0 and 1
        assertEquals(List.of("C", "D", "E", "F"), drain(q));
    }

    @Test void growsWhenFullAndKeepsTheOrder() {
        MyQueue<Integer> q = new MyQueue<>(2);
        q.offer(1); q.offer(2); q.poll(); q.offer(3);   // wrapped AND full
        q.offer(4); q.offer(5);                         // forces a grow
        assertEquals(List.of(2, 3, 4, 5), drain(q));
    }
}
