package ds;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MyMinHeapTest {
    private static <T extends Comparable<T>> List<T> pollAll(MyMinHeap<T> h, int n) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(h.poll());
        return out;
    }

    @Test void emptyHeapGivesNull() {
        MyMinHeap<Integer> h = new MyMinHeap<>();
        assertEquals(0, h.size());
        assertNull(h.peek());
        assertNull(h.poll());
    }

    @Test void peekShowsTheSmallestWithoutRemovingIt() {
        MyMinHeap<Integer> h = new MyMinHeap<>();
        for (int x : new int[] {5, 1, 8}) h.add(x);
        assertEquals(1, h.peek());
        assertEquals(3, h.size());
    }

    @Test void pollReturnsEverythingInAscendingOrder() {
        MyMinHeap<Integer> h = new MyMinHeap<>();
        for (int x : new int[] {5, 1, 8, 3, 2, 9}) h.add(x);
        assertEquals(List.of(1, 2, 3, 5, 8, 9), pollAll(h, 6));
        assertEquals(0, h.size());
    }

    @Test void duplicatesAreKept() {
        MyMinHeap<Integer> h = new MyMinHeap<>();
        for (int x : new int[] {2, 1, 2, 1}) h.add(x);
        assertEquals(List.of(1, 1, 2, 2), pollAll(h, 4));
    }

    @Test void growsPastItsStartingCapacity() {
        MyMinHeap<Integer> h = new MyMinHeap<>();
        for (int x = 100; x >= 1; x--) h.add(x);
        assertEquals(100, h.size());
        assertEquals(1, h.poll());
    }

    @Test void agreesWithPriorityQueueOnRandomInput() {
        Random rnd = new Random(20);
        MyMinHeap<Integer> mine = new MyMinHeap<>();
        PriorityQueue<Integer> jdk = new PriorityQueue<>();
        for (int i = 0; i < 1000; i++) {
            int x = rnd.nextInt(500);
            mine.add(x); jdk.add(x);
            if (rnd.nextInt(3) == 0) assertEquals(jdk.poll(), mine.poll());
        }
        while (!jdk.isEmpty()) assertEquals(jdk.poll(), mine.poll());
        assertNull(mine.poll());
    }

    @Test void ordersByCompareToSoWordCountPutsTheMostFrequentFirst() {
        MyMinHeap<WordCount> h = new MyMinHeap<>();
        h.add(new WordCount("cat", 3)); h.add(new WordCount("the", 4)); h.add(new WordCount("a", 3));
        assertEquals(List.of("the", "a", "cat"), pollAll(h, 3).stream().map(WordCount::word).toList());
    }
}
