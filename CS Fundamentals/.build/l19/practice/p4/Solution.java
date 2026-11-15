import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // The indices [i, j] of two numbers that add up to target (smallest j), or null.
    static List<Integer> twoSum(List<Integer> nums, int target) {
        Map<Integer, Integer> indexOf = new HashMap<>();   // value -> an index where we saw it
        for (int j = 0; j < nums.size(); j++) {
            int x = nums.get(j);
            Integer i = indexOf.get(target - x);           // look up the partner first...
            if (i != null) return List.of(i, j);
            indexOf.put(x, j);                             // ...then remember x
        }
        return null;
    }

    // How many index pairs i < j add up to target.
    static int countPairs(List<Integer> nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();      // value -> how many times so far
        int pairs = 0;
        for (int x : nums) {
            pairs += seen.getOrDefault(target - x, 0);
            seen.merge(x, 1, Integer::sum);
        }
        return pairs;
    }

    // CHECKS
    public static void main(String[] args) {
        check("twoSum finds the classic pair", "[0, 1] [1, 2]", () -> twoSum(List.of(2, 7, 11, 15), 9) + " " + twoSum(List.of(3, 2, 4), 6));
        check("a number can't pair with itself", "null [0, 1]", () -> twoSum(List.of(3, 4), 6) + " " + twoSum(List.of(3, 3), 6));
        check("twoSum handles negatives and no answer", "[0, 2] null null", () -> twoSum(List.of(-3, 4, 3, 90), 0) + " " + twoSum(List.of(1, 2, 3), 100) + " " + twoSum(List.of(), 0));
        check("the pair that completes first wins", List.of(0, 2), () -> twoSum(List.of(1, 2, 5, 4), 6));
        check("countPairs counts every index pair", "3 3 0 0", () -> countPairs(List.of(1, 5, 7, -1, 5), 6) + " " + countPairs(List.of(3, 3, 3), 6) + " " + countPairs(List.of(3), 6) + " " + countPairs(List.of(), 6));
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
