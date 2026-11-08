import java.util.Objects;

public class Practice {
    // Plan: every number has at least one digit, so the count starts at 1.
    // While dropping the last digit still leaves something, drop it and count one more.
    // No Math.abs(): dividing a negative number by 10 heads towards 0 as well.
    static int digitCount(int number) {
        int n = number;
        int count = 1;
        while (n / 10 != 0) {
            n /= 10;
            count++;
        }
        return count;
    }

    // CHECKS
    public static void main(String[] args) {
        check("ordinary numbers", 4, () -> digitCount(1234));
        check("a single digit", 1, () -> digitCount(7));
        check("the minus sign is not a digit", 3, () -> digitCount(-907));
        check("zero has one digit", 1, () -> digitCount(0));
        check("the largest int has ten digits", 10, () -> digitCount(Integer.MAX_VALUE));
        check("the smallest int has ten digits", 10, () -> digitCount(Integer.MIN_VALUE));
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
