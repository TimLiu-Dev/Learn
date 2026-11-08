import java.util.List;
import java.util.Objects;

public class Practice {
    static final List<String> DAYS = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    static String weekday(int offset) {
        throw new UnsupportedOperationException("TODO: pick from DAYS; weekday(-1) must be Sun");
    }

    static double average(int a, int b) {
        throw new UnsupportedOperationException("TODO: 3 and 4 average to 3.5");
    }

    static boolean closeEnough(double x, double y) {
        throw new UnsupportedOperationException("TODO: compare the difference, not the values");
    }

    // CHECKS
    public static void main(String[] args) {
        check("weekday counts forward", "Tue", () -> weekday(8));
        check("one day before a Monday", "Sun", () -> weekday(-1));
        check("nine days before a Monday", "Sat", () -> weekday(-9));
        check("average keeps the half", 3.5, () -> average(3, 4));
        check("average of negatives", -3.5, () -> average(-3, -4));
        check("average of two huge ints does not overflow", 2147483647.0, () -> average(Integer.MAX_VALUE, Integer.MAX_VALUE));
        check("0.1 + 0.2 is close enough to 0.3", true, () -> closeEnough(0.1 + 0.2, 0.3));
        check("a millionth apart is too far", false, () -> closeEnough(1.0, 1.000001));
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
