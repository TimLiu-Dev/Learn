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
        throw new UnsupportedOperationException("TODO: give each thread its own register and step number, then follow the schedule");
    }

    // Every string with exactly a 'A's and b 'B's, e.g. allSchedules(1, 1) = [AB, BA].
    static List<String> allSchedules(int a, int b) {
        throw new UnsupportedOperationException("TODO: recursion: a schedule starts with A (if A has steps left) or with B");
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
