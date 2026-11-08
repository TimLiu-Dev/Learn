import java.util.Objects;

public class Practice {
    // One frame, however big n is: the loop reuses the same local variables.
    static long sumTo(int n) {
        long total = 0;                       // long: the sum outgrows an int long before the loop ends
        for (int i = 1; i <= n; i++) total += i;
        return total;
    }

    // CHECKS
    public static void main(String[] args) {
        check("small sums are right", 10L, () -> sumTo(4));
        check("zero gives 0", 0L, () -> sumTo(0));
        check("a negative n gives 0", 0L, () -> sumTo(-3));
        check("the total doesn't overflow an int", 5_000_050_000L, () -> sumTo(100_000));
        check("a million doesn't overflow the stack", 500_000_500_000L, () -> sumTo(1_000_000));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception | StackOverflowError e) {   // StackOverflowError is an Error, not an Exception
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
