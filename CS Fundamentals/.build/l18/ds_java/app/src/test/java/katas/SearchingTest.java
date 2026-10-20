package katas;

import static katas.Searching.lowerBound;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SearchingTest {
    @Test
    void emptyListGivesZero() {
        assertEquals(0, lowerBound(List.<Integer>of(), 5));
    }

    @Test
    void findsTheFirstOfSeveralDuplicates() {
        assertEquals(1, lowerBound(List.of(1, 2, 2, 2, 3), 2));
    }

    @Test
    void missingValueGivesItsInsertionPoint() {
        assertEquals(3, lowerBound(List.of(10, 20, 30, 40), 35));
    }

    @Test
    void smallerThanEverythingGivesZeroBiggerGivesSize() {
        List<Integer> xs = List.of(10, 20, 30);
        assertEquals(0, lowerBound(xs, 1));
        assertEquals(3, lowerBound(xs, 99));
    }

    @Test
    void worksForAnyComparableType() {
        assertEquals(2, lowerBound(List.of("apple", "fig", "kiwi"), "grape"));
    }

    @Test
    void agreesWithALinearScanOnRandomData() {
        Random rnd = new Random(7);
        for (int run = 0; run < 200; run++) {
            List<Integer> xs = new ArrayList<>();
            int n = rnd.nextInt(50);
            for (int i = 0; i < n; i++) xs.add(rnd.nextInt(20));
            Collections.sort(xs);
            int target = rnd.nextInt(22) - 1;
            int expected = 0;
            while (expected < xs.size() && xs.get(expected) < target) expected++;
            assertEquals(expected, lowerBound(xs, target), "xs=" + xs + " target=" + target);
        }
    }
}
