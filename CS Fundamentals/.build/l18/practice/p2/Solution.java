import java.util.*;

public class Practice {
    // Index of the first copy of target in the sorted list xs, or -1. O(log n).
    static int firstIndexOf(List<Integer> xs, int target) {
        int lo = 0, hi = xs.size();      // [lo, hi): left of lo is < target, from hi on is >= target
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (xs.get(mid) < target) lo = mid + 1; else hi = mid;
        }
        return lo < xs.size() && xs.get(lo) == target ? lo : -1;
    }

    // Inserts x into the sorted list xs after any equal values; returns its index.
    static int insertSorted(List<Integer> xs, int x) {
        int lo = 0, hi = xs.size();      // left of lo is <= x, from hi on is > x
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (xs.get(mid) <= x) lo = mid + 1; else hi = mid;   // the one character that differs
        }
        xs.add(lo, x);
        return lo;
    }

    // CHECKS
    // A read-only list that counts every get(), to prove the search is O(log n).
    static final class CountingList extends AbstractList<Integer> {
        final int[] data;
        int gets = 0;
        CountingList(int[] data) { this.data = data; }
        @Override public int size() { return data.length; }
        @Override public Integer get(int i) { gets++; return data[i]; }
    }

    public static void main(String[] args) {
        List<Integer> xs = List.of(1, 2, 2, 2, 3, 5, 8);
        check("finds the first of several duplicates", 1, () -> firstIndexOf(xs, 2));
        check("finds the first and last elements", List.of(0, 6), () -> List.of(firstIndexOf(xs, 1), firstIndexOf(xs, 8)));
        check("missing values give -1", List.of(-1, -1, -1), () -> List.of(firstIndexOf(xs, 4), firstIndexOf(xs, 0), firstIndexOf(xs, 9)));
        check("empty and single-element lists", List.of(-1, -1, 0), () -> List.of(firstIndexOf(List.of(), 7), firstIndexOf(List.of(5), 6), firstIndexOf(List.of(5), 5)));
        check("a million equal values take about 20 reads", true, () -> {
            int[] sevens = new int[1_000_000];
            Arrays.fill(sevens, 7);
            CountingList big = new CountingList(sevens);
            return firstIndexOf(big, 7) == 0 && big.gets <= 25;
        });
        check("insertSorted goes after equal values", List.of(4, List.of(1, 2, 2, 2, 2, 3)), () -> {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 2, 2, 3));
            return List.of(insertSorted(list, 2), list);
        });
        check("insertSorted handles the ends and an empty list", List.of(0, 0, 2, List.of(1, 5, 9)), () -> {
            List<Integer> list = new ArrayList<>();
            return List.of(insertSorted(list, 5), insertSorted(list, 1), insertSorted(list, 9), list);
        });
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
