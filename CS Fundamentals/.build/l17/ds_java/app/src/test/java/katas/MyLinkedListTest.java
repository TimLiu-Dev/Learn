package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class MyLinkedListTest {
    private static <T> List<T> toList(MyLinkedList<T> list) {
        List<T> out = new ArrayList<>();
        for (T item : list) out.add(item);
        return out;
    }

    @Test void newListIsEmpty() {
        MyLinkedList<String> list = new MyLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test void addFirstPutsItemsAtTheFront() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addFirst("B"); list.addFirst("A");
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals(2, list.size());
    }

    @Test void addLastPutsItemsAtTheBack() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A"); list.addLast("B"); list.addFirst("start");
        assertEquals(List.of("start", "A", "B"), toList(list));
    }

    @Test void getOutsideTheListThrows() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test void removeFirstReturnsItemsInOrder() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1); list.addLast(2);
        assertEquals(1, list.removeFirst());
        assertEquals(2, list.removeFirst());
        assertTrue(list.isEmpty());
    }

    @Test void removeFirstOnAnEmptyListThrows() {
        assertThrows(NoSuchElementException.class, () -> new MyLinkedList<Integer>().removeFirst());
    }

    @Test void addLastStillWorksAfterTheListWasEmptied() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        list.removeFirst();
        list.addLast("B");                          // catches a stale tail pointer
        assertEquals(List.of("B"), toList(list));
    }

    @Test void reverseFlipsTheOrderAndMovesTheTail() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 1; i <= 4; i++) list.addLast(i);
        list.reverse();
        list.addLast(0);                            // must land after the OLD head
        assertEquals(List.of(4, 3, 2, 1, 0), toList(list));
    }

    @Test void forLoopVisitsEveryItemInOrder() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("x"); list.addLast("y"); list.addLast("z");
        StringBuilder seen = new StringBuilder();
        for (String s : list) seen.append(s);
        assertEquals("xyz", seen.toString());
    }
}
