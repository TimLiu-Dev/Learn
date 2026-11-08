import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    // a and b are copies of the caller's references, so they reach the caller's objects.
    static void swapCounts(Counter a, Counter b) {
        int tmp = a.count;
        a.count = b.count;
        b.count = tmp;
    }

    // Sort a copy: the caller's list is never touched.
    static List<Integer> topThree(List<Integer> scores) {
        List<Integer> copy = new ArrayList<>(scores);
        Collections.sort(copy, Collections.reverseOrder());
        return copy.subList(0, Math.min(3, copy.size()));   // a view, but of our own copy
    }

    // CHECKS
    public static void main(String[] args) {
        check("swapCounts swaps the numbers inside the objects", "2 1", () -> {
            Counter x = new Counter(1), y = new Counter(2);
            swapCounts(x, y);
            return x.count + " " + y.count;
        });
        check("swapping a counter with itself keeps its count", 5, () -> {
            Counter x = new Counter(5);
            swapCounts(x, x);
            return x.count;
        });
        check("topThree finds the three highest", List.of(9, 7, 5), () -> topThree(new ArrayList<>(List.of(5, 1, 9, 7, 3))));
        check("topThree leaves the caller's list alone", List.of(5, 1, 9, 7, 3), () -> {
            List<Integer> scores = new ArrayList<>(List.of(5, 1, 9, 7, 3));
            topThree(scores);
            return scores;
        });
        check("fewer than three scores is fine", List.of(8, 4), () -> topThree(new ArrayList<>(List.of(4, 8))));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
