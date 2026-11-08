import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class Practice {
    // Eager: every id is looked up before the list is cut down to n.
    static List<String> firstFound(List<Integer> ids, int n, Function<Integer, String> lookup) {
        List<String> all = ids.stream().map(lookup).filter(Objects::nonNull).toList();
        return all.subList(0, Math.min(n, all.size()));
    }

    static Stream<Integer> collatz(int start) {
        throw new UnsupportedOperationException("TODO: Stream.iterate(seed, hasNext, next)");
    }

    // CHECKS
    static List<Integer> calls = new ArrayList<>();

    static String evenNames(int id) {
        calls.add(id);
        return id % 2 == 0 ? "user" + id : null;
    }

    static List<Integer> range(int from, int to) {
        List<Integer> xs = new ArrayList<>();
        for (int i = from; i <= to; i++) xs.add(i);
        return xs;
    }

    public static void main(String[] args) {
        check("firstFound keeps the first n names", List.of("user2", "user4"), () -> firstFound(range(1, 10), 2, Practice::evenNames));
        check("firstFound stops looking up once it has n names", List.of(1, 2, 3, 4), () -> { calls.clear(); firstFound(range(1, 10), 2, Practice::evenNames); return calls; });
        check("firstFound returns fewer names when the ids run out", List.of("user2"), () -> firstFound(List.of(1, 2, 3), 5, Practice::evenNames));
        check("asking for zero names looks nothing up", List.of(), () -> { calls.clear(); firstFound(range(1, 10), 0, Practice::evenNames); return calls; });
        check("collatz ends with 1", List.of(6, 3, 10, 5, 16, 8, 4, 2, 1), () -> collatz(6).toList());
        check("collatz of 1 is just 1", List.of(1), () -> collatz(1).toList());
        check("collatz works with limit on a long run", List.of(27, 82, 41, 124, 62), () -> collatz(27).limit(5).toList());
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
