import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Practice {
    // Each thread runs count++ as three steps: 0 = read, 1 = add, 2 = write.
    // schedule.charAt(k) is the thread ('A' or 'B') that takes its next step.
    static int finalCount(int start, String schedule) {
        int count = start;                       // shared, on the heap
        int[] register = new int[2];             // one per thread: registers are never shared
        int[] step = new int[2];
        for (char who : schedule.toCharArray()) {
            int t = who - 'A';                   // 'A' -> 0, 'B' -> 1
            switch (step[t]) {
                case 0 -> register[t] = count;   // getfield
                case 1 -> register[t] += 1;      // iconst_1; iadd
                case 2 -> count = register[t];   // putfield
            }
            step[t]++;
        }
        return count;
    }

    // Every string with exactly a 'A's and b 'B's, e.g. allSchedules(1, 1) = [AB, BA].
    static List<String> allSchedules(int a, int b) {
        if (a == 0 && b == 0) return List.of("");   // base case: one empty schedule
        List<String> result = new ArrayList<>();
        if (a > 0) for (String s : allSchedules(a - 1, b)) result.add("A" + s);
        if (b > 0) for (String s : allSchedules(a, b - 1)) result.add("B" + s);
        return result;
    }

    // CHECKS
    public static void main(String[] args) {
        check("AAABBB gives 43", 43, () -> finalCount(41, "AAABBB"));
        check("BBBAAA gives 43", 43, () -> finalCount(41, "BBBAAA"));
        check("the classic lost update ABAABB gives 42", 42, () -> finalCount(41, "ABAABB"));
        check("ABABAB from 0 gives 1", 1, () -> finalCount(0, "ABABAB"));
        check("allSchedules(1, 1)", Set.of("AB", "BA"), () -> new HashSet<>(allSchedules(1, 1)));
        check("allSchedules(2, 0)", List.of("AA"), () -> allSchedules(2, 0));
        check("allSchedules(3, 3) has 20 schedules", 20, () -> allSchedules(3, 3).size());
        check("allSchedules(3, 3) has no duplicates", 20, () -> new HashSet<>(allSchedules(3, 3)).size());
        check("only 2 of the 20 orders are safe", Set.of("AAABBB", "BBBAAA"),
            () -> allSchedules(3, 3).stream().filter(s -> finalCount(41, s) == 43).collect(Collectors.toSet()));
        check("every order gives 42 or 43, never 41", Set.of(42, 43),
            () -> allSchedules(3, 3).stream().map(s -> finalCount(41, s)).collect(Collectors.toSet()));
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
