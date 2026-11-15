import java.util.*;
import java.util.function.Predicate;

public class Practice {
    // Delete elements from a failing input while it keeps failing; return a list where
    // deleting any single element makes the failure go away.
    static <T> List<T> shrink(List<T> input, Predicate<List<T>> fails) {
        throw new UnsupportedOperationException("TODO: check that input fails, then delete one element at a time");
    }

    // CHECKS
    // Part 4's buggy window (`if` where it needs `while`) and the slow version it disagrees with.
    static int shortestSlow(List<Integer> a, int target) {
        int best = 0;
        for (int i = 0; i < a.size(); i++) {
            int sum = 0;
            for (int j = i; j < a.size(); j++) {
                sum += a.get(j);
                if (sum >= target) { if (best == 0 || j - i + 1 < best) best = j - i + 1; break; }
            }
        }
        return best;
    }

    static int shortestBuggy(List<Integer> a, int target) {
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < a.size(); right++) {
            sum += a.get(right);
            if (sum >= target) { best = Math.min(best, right - left + 1); sum -= a.get(left++); }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    static String shrinkWindowBug() {
        Predicate<List<Integer>> fails = a -> shortestSlow(a, 5) != shortestBuggy(a, 5);
        List<Integer> small = shrink(List.of(2, 1, 1, 1, 3, 1, 5, 2), fails);
        if (!fails.test(small) || small.size() > 3) return small + " is not a small failing case";
        for (int i = 0; i < small.size(); i++) {
            List<Integer> less = new ArrayList<>(small);
            less.remove(i);
            if (fails.test(less)) return small + " without index " + i + " still fails";
        }
        return "fully shrunk";
    }

    public static void main(String[] args) {
        check("an input with nothing to delete comes back unchanged", List.of(7), () -> shrink(List.of(7), a -> a.contains(7)));
        check("everything the failure doesn't need is deleted", List.of(2, 5), () -> shrink(List.of(1, 2, 3, 4, 5, 6), a -> a.contains(2) && a.contains(5)));
        check("the result keeps the original order", List.of("c", "b"), () -> shrink(List.of("c", "a", "b", "a"), a -> !a.isEmpty() && a.get(0).equals("c") && a.contains("b")));
        check("an input that passes is rejected", "IllegalArgumentException", () -> {
            try { shrink(List.of(1, 2), a -> false); return "no exception"; }
            catch (IllegalArgumentException e) { return "IllegalArgumentException"; }
        });
        check("the window bug shrinks to a tiny case", "fully shrunk", Practice::shrinkWindowBug);
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
