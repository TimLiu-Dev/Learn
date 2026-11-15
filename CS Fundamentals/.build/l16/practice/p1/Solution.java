import java.util.*;

public class Practice {
    static boolean hasPairSum(List<Integer> xs, int target) {
        Set<Integer> seen = new HashSet<>();
        for (int x : xs) {
            if (seen.contains(target - x)) return true;   // an earlier element completes the pair
            seen.add(x);                                   // add x only now, so it can't pair with itself
        }
        return false;
    }

    // CHECKS
    // A list that counts every element read, by index or by a for loop.
    static class CountingList extends AbstractList<Integer> {
        final List<Integer> items;
        int reads = 0;
        CountingList(List<Integer> items) { this.items = items; }
        @Override public int size() { return items.size(); }
        @Override public Integer get(int index) { reads++; return items.get(index); }
    }

    public static void main(String[] args) {
        check("finds a pair that adds up to the target", true, () -> hasPairSum(List.of(3, 8, 5), 13));
        check("no pair means false", false, () -> hasPairSum(List.of(3, 8, 5), 100));
        check("an empty list has no pairs", false, () -> hasPairSum(List.of(), 0));
        check("one element can't pair with itself", false, () -> hasPairSum(List.of(5), 10));
        check("two equal elements can pair", true, () -> hasPairSum(List.of(5, 1, 5), 10));
        check("negative numbers work", true, () -> hasPairSum(List.of(-4, 7, 2), 3));
        check("one pass: at most 4 reads per element", true, () -> {
            int n = 2_000;
            List<Integer> data = new ArrayList<>();
            for (int i = 0; i < n; i++) data.add(i);
            CountingList xs = new CountingList(data);
            hasPairSum(xs, -1);
            return xs.reads <= 4 * n;
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
