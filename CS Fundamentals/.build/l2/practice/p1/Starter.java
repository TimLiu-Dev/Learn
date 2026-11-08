import java.util.Objects;

public class Practice {
    // Examples (the checks use these):
    //   1234 -> 4,  7 -> 1,  -907 -> 3,  0 -> 1,  Integer.MIN_VALUE (-2147483648) -> 10
    // Plan (in plain words, before any code):
    //

    static int digitCount(int number) {
        throw new UnsupportedOperationException("TODO: count the digits; the minus sign isn't one");
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
