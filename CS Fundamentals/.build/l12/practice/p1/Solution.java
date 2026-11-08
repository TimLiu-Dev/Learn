import java.util.*;

public class Practice {
    // The middle reading. For an even count, the lower of the two middle ones.
    static int median(List<Integer> readings) {
        List<Integer> sorted = new ArrayList<>(readings);   // a copy; the caller's list is untouched
        Collections.sort(sorted);
        return sorted.get((sorted.size() - 1) / 2);
    }

    // Readings arrive in the order they were taken.
    static String summary(List<Integer> readings) {
        int mid = median(readings);
        return "first=" + readings.get(0) + " last=" + readings.get(readings.size() - 1) + " median=" + mid;
    }

    // CHECKS
    static List<Integer> list(Integer... xs) { return new ArrayList<>(Arrays.asList(xs)); }

    public static void main(String[] args) {
        check("median of an odd count is the middle value", 75, () -> median(list(80, 72, 75)));
        check("median of an even count is the lower middle", 75, () -> median(list(79, 72, 80, 75)));
        check("summary reports first and last in the order taken", "first=80 last=75 median=75", () -> summary(list(80, 72, 75)));
        check("the caller's list is unchanged afterwards", List.of(80, 72, 75), () -> { List<Integer> r = list(80, 72, 75); summary(r); return r; });
        check("a single reading is first, last and median", "first=70 last=70 median=70", () -> summary(list(70)));
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
