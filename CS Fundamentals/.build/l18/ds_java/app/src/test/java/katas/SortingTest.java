package katas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SortingTest {
    private final Map<String, Consumer<List<Integer>>> sorts = Map.of(
        "insertion", Sorting::insertionSort,
        "merge", Sorting::mergeSort,
        "quick", Sorting::quickSort);

    // Run every sort on a copy of input and compare with the JDK's sort.
    private void checkAll(List<Integer> input) {
        List<Integer> expected = new ArrayList<>(input);
        Collections.sort(expected);
        sorts.forEach((name, sort) -> {
            List<Integer> xs = new ArrayList<>(input);
            sort.accept(xs);
            assertEquals(expected, xs, name + " on " + input);
        });
    }

    private static List<Integer> range(int from, int to) {
        return IntStream.rangeClosed(from, to).boxed().toList();
    }

    @Test void emptyList() { checkAll(List.of()); }
    @Test void oneElement() { checkAll(List.of(42)); }
    @Test void twoElementsOutOfOrder() { checkAll(List.of(2, 1)); }
    @Test void duplicates() { checkAll(List.of(3, 1, 3, 2, 1, 3, 3)); }
    @Test void alreadySorted() { checkAll(range(1, 100)); }
    @Test void reversed() { checkAll(range(1, 100).reversed()); }
    @Test void negativeNumbers() { checkAll(List.of(0, -5, 7, -5, Integer.MIN_VALUE, Integer.MAX_VALUE)); }

    @Test
    void randomListsWithAFixedSeed() {
        Random rnd = new Random(42);
        for (int run = 0; run < 100; run++) {
            List<Integer> xs = new ArrayList<>();
            int n = rnd.nextInt(300);
            for (int i = 0; i < n; i++) xs.add(rnd.nextInt(50) - 25);
            checkAll(xs);
        }
    }

    @Test
    void stringsSortToo() {
        List<String> xs = new ArrayList<>(List.of("pear", "fig", "kiwi", "apple"));
        Sorting.mergeSort(xs);
        assertEquals(List.of("apple", "fig", "kiwi", "pear"), xs);
    }

    // Equal by compareTo, but distinguishable by suit.
    record Card(int rank, char suit) implements Comparable<Card> {
        public int compareTo(Card other) { return Integer.compare(rank, other.rank); }
    }

    @Test
    void insertionAndMergeAreStable() {
        List<Card> cards = List.of(new Card(5, 'h'), new Card(2, 's'), new Card(5, 'c'), new Card(2, 'd'), new Card(5, 's'));
        List<Card> expected = List.of(new Card(2, 's'), new Card(2, 'd'), new Card(5, 'h'), new Card(5, 'c'), new Card(5, 's'));
        List<Card> a = new ArrayList<>(cards);
        Sorting.insertionSort(a);
        assertEquals(expected, a);
        List<Card> b = new ArrayList<>(cards);
        Sorting.mergeSort(b);
        assertEquals(expected, b);
    }

    @Test
    void quickSurvivesBigSortedAndAllEqualInput() {
        List<Integer> sorted = new ArrayList<>(range(1, 100_000));
        Sorting.quickSort(sorted);
        assertEquals(range(1, 100_000), sorted);
        List<Integer> same = new ArrayList<>(Collections.nCopies(100_000, 7));
        Sorting.quickSort(same);
        assertEquals(Collections.nCopies(100_000, 7), same);
    }
}
