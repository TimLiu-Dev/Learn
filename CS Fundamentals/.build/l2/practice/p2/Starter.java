import java.util.Objects;

public class Practice {
    static boolean isLeap(int year) {
        throw new UnsupportedOperationException("TODO: L1's leap-year rule");
    }

    static int daysIn(int year) {
        throw new UnsupportedOperationException("TODO: 366 in a leap year, otherwise 365");
    }

    // Seconds from 1 January 1970 to 1 January of year (a day has 86,400 seconds)
    static long secondsSince1970(int year) {
        int total = 0;
        for (int y = 1970; y < year; y++) {
            total += daysIn(y) * 86_400;
        }
        return total;     // Java widens int to long here without a word
    }

    // CHECKS
    public static void main(String[] args) {
        check("2024 is a leap year", true, () -> isLeap(2024));
        check("2023 is not", false, () -> isLeap(2023));
        check("1900 is a century", false, () -> isLeap(1900));
        check("2000 is divisible by 400", true, () -> isLeap(2000));
        check("days in a leap year", 366, () -> daysIn(2024));
        check("1970 itself is second zero", 0L, () -> secondsSince1970(1970));
        check("the year 2000 starts at second 946684800", 946_684_800L, () -> secondsSince1970(2000));
        check("2039 is past Integer.MAX_VALUE", 2_177_452_800L, () -> secondsSince1970(2039));
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
