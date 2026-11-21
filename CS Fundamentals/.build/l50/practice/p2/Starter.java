import java.util.*;
import java.util.stream.IntStream;

public class Practice {
    // Every subset of nums (positive, distinct, sorted ascending) that sums to exactly target.
    static List<List<Integer>> subsetSums(List<Integer> nums, int target) {
        List<List<Integer>> out = new ArrayList<>();
        go(nums, target, 0, 0, new ArrayList<>(), out);
        return out;
    }

    static void go(List<Integer> nums, int target, int start, int sum, List<Integer> current, List<List<Integer>> out) {
        throw new UnsupportedOperationException("TODO: record a hit; else choose, explore, un-choose (and prune)");
    }

    // CHECKS
    public static void main(String[] args) {
        check("1 to 5 with target 5 gives three subsets in search order", List.of(List.of(1, 4), List.of(2, 3), List.of(5)), () -> subsetSums(List.of(1, 2, 3, 4, 5), 5));
        check("each number is used at most once", List.of(), () -> subsetSums(List.of(2, 3), 4));
        check("no subset reaches the target", List.of(), () -> subsetSums(List.of(5, 6), 4));
        check("target 0 has one answer, the empty subset", List.of(List.of()), () -> subsetSums(List.of(1, 2), 0));
        check("every answer is a separate list", List.of(List.of(1, 2, 3), List.of(1, 5), List.of(2, 4), List.of(6)), () -> subsetSums(List.of(1, 2, 3, 4, 5, 6), 6));
        // 2^40 subsets: without the pruning break this check runs for about an hour. If the run stalls here, stop it.
        check("pruning makes 2 to the 40 subsets fast", List.of(List.of(1, 2), List.of(3)), () -> subsetSums(IntStream.rangeClosed(1, 40).boxed().toList(), 3));
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
