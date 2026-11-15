import java.util.*;

public class Practice {
    // -1 (all ones) when x < 0, otherwise 0
    static int signMask(int x) {
        throw new UnsupportedOperationException("TODO: which shift copies the sign bit into every bit?");
    }

    // The smaller of a and b, with no branch: no if, ?:, <, > or Math.min.
    static int minBranchless(int a, int b) {
        throw new UnsupportedOperationException("TODO: a mask that is all ones when a < b, then pick with & and ^");
    }

    // CHECKS
    public static void main(String[] args) {
        check("signMask is all ones for negative numbers", "-1 -1 -1", () -> signMask(-1) + " " + signMask(-128) + " " + signMask(Integer.MIN_VALUE));
        check("signMask is 0 for zero and positive numbers", "0 0 0 0", () -> signMask(0) + " " + signMask(1) + " " + signMask(128) + " " + signMask(Integer.MAX_VALUE));
        check("min agrees with Math.min on small numbers", 0, () -> {
            int wrong = 0;
            for (int a = -100; a <= 100; a++) for (int b = -100; b <= 100; b++) if (minBranchless(a, b) != Math.min(a, b)) wrong++;
            return wrong;
        });
        check("min agrees with Math.min at the extremes", 0, () -> {
            int[] edges = { Integer.MIN_VALUE, Integer.MIN_VALUE + 1, -1, 0, 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE };
            int wrong = 0;
            for (int a : edges) for (int b : edges) if (minBranchless(a, b) != Math.min(a, b)) wrong++;
            return wrong;
        });
        check("min agrees with Math.min on random ints", 0, () -> {
            Random rng = new Random(52);
            int wrong = 0;
            for (int i = 0; i < 10_000; i++) {
                int a = rng.nextInt(), b = rng.nextInt();
                if (minBranchless(a, b) != Math.min(a, b)) wrong++;
            }
            return wrong;
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
