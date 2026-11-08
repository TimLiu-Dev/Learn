import java.util.List;
import java.util.Objects;

public class Practice {
    static final List<String> DAYS = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    // % would give -1 for an offset of -1; floorMod always lands in 0..6
    static String weekday(int offset) {
        return DAYS.get(Math.floorMod(offset, 7));
    }

    // Convert before adding: no int overflow, and no integer division
    static double average(int a, int b) {
        return ((double) a + b) / 2;
    }

    static boolean closeEnough(double x, double y) {
        return Math.abs(x - y) < 1e-9;
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
