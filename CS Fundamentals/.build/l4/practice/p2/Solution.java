import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    // A new list AND a new Counter for every element: no arrow leads back to the originals.
    static List<Counter> copyCounters(List<Counter> counters) {
        List<Counter> copy = new ArrayList<>();
        for (Counter c : counters) copy.add(new Counter(c.count));
        return copy;
    }

    // CHECKS
    static List<Integer> counts(List<Counter> cs) {
        List<Integer> out = new ArrayList<>();
        for (Counter c : cs) out.add(c.count);
        return out;
    }

    public static void main(String[] args) {
        check("the copy has the same counts", List.of(3, 7), () -> counts(copyCounters(List.of(new Counter(3), new Counter(7)))));
        check("adding to the original list doesn't change the copy", 2, () -> {
            List<Counter> originals = new ArrayList<>(List.of(new Counter(3), new Counter(7)));
            List<Counter> copied = copyCounters(originals);
            originals.add(new Counter(9));
            return copied.size();
        });
        check("changing an original counter doesn't change the copy", List.of(3, 7), () -> {
            List<Counter> originals = List.of(new Counter(3), new Counter(7));
            List<Counter> copied = copyCounters(originals);
            originals.get(0).count = 99;
            return counts(copied);
        });
        check("changing the copy doesn't change the originals", List.of(3, 7), () -> {
            List<Counter> originals = List.of(new Counter(3), new Counter(7));
            copyCounters(originals).get(1).count = 0;
            return counts(originals);
        });
        check("an empty list gives an empty copy", List.of(), () -> copyCounters(List.of()));
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
