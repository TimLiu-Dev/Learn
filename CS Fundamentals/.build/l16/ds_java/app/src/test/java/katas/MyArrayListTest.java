package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MyArrayListTest {
    private MyArrayList<String> listOfAbc() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a"); list.add("b"); list.add("c");
        return list;
    }

    @Test void newListIsEmpty() {
        MyArrayList<String> list = new MyArrayList<>();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test void addThenGetReturnsTheElement() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
    }

    @Test void getOutsideRangeThrows() {
        MyArrayList<String> list = listOfAbc();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test void capacityDoublesWhenFull() {
        MyArrayList<Integer> list = new MyArrayList<>(4);
        for (int i = 0; i < 4; i++) list.add(i);
        assertEquals(4, list.capacity());
        list.add(4);
        assertEquals(8, list.capacity());
    }

    @Test void keepsEveryElementAcrossManyResizes() {
        MyArrayList<Integer> list = new MyArrayList<>(1);
        for (int i = 0; i < 1000; i++) list.add(i * i);
        assertEquals(1000, list.size());
        for (int i = 0; i < 1000; i++) assertEquals(i * i, list.get(i));
    }

    @Test void setReplacesAndReturnsOldValue() {
        MyArrayList<String> list = listOfAbc();
        assertEquals("b", list.set(1, "B"));
        assertEquals("B", list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, "x"));
    }

    @Test void addAtIndexShiftsLaterElementsRight() {
        MyArrayList<String> list = listOfAbc();
        list.add(1, "X");
        assertEquals("[a, X, b, c]", list.toString());
        list.add(0, "first");
        list.add(list.size(), "last");
        assertEquals("[first, a, X, b, c, last]", list.toString());
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(list.size() + 1, "nope"));
    }

    @Test void removeAtShiftsLeftAndReturnsRemoved() {
        MyArrayList<String> list = listOfAbc();
        assertEquals("a", list.removeAt(0));
        assertEquals("[b, c]", list.toString());
        assertEquals("c", list.removeAt(1));
        assertEquals(1, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(1));
    }

    @Test void forLoopVisitsElementsInOrder() {
        List<String> seen = new ArrayList<>();
        for (String s : listOfAbc()) seen.add(s);
        assertEquals(List.of("a", "b", "c"), seen);
    }

    @Test void toStringOfEmptyList() {
        assertEquals("[]", new MyArrayList<Integer>().toString());
    }

    @Test void nullsAreAllowed() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add(null);
        assertNull(list.get(0));
    }

    @Test void capacityBelowOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new MyArrayList<Integer>(0));
    }
}
