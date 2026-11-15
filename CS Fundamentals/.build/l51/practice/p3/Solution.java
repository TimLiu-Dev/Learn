import java.util.*;
import java.util.function.UnaryOperator;

public class Practice {
    sealed interface Verdict permits Halts, Loops {}
    record Halts(int steps) implements Verdict {}
    record Loops(int entry, int length) implements Verdict {}

    // Run a deterministic machine from start. next.apply(c) is the configuration after c, or null if the machine halts.
    // Always answers when there are finitely many configurations.
    static <C> Verdict analyse(C start, UnaryOperator<C> next) {
        Map<C, Integer> firstSeen = new HashMap<>();        // configuration -> step it was first seen at
        C c = start;
        for (int step = 0; ; step++) {
            Integer earlier = firstSeen.putIfAbsent(c, step);
            if (earlier != null) return new Loops(earlier, step - earlier);   // same config, same future
            c = next.apply(c);
            if (c == null) return new Halts(step);
        }
    }

    // CHECKS
    record Config(String state, int head) {}

    // Walks right to cell 2, turns, walks back to cell 0, turns, and so on
    static Config bounce(Config c) {
        if (c.state().equals("R")) return c.head() < 2 ? new Config("R", c.head() + 1) : new Config("L", c.head());
        return c.head() > 0 ? new Config("L", c.head() - 1) : new Config("R", c.head());
    }

    public static void main(String[] args) {
        check("a machine that halts at once takes 0 steps", new Halts(0), () -> analyse(7, n -> null));
        check("a long run is not a loop", new Halts(5000), () -> analyse(5000, n -> n == 0 ? null : n - 1));
        check("a cycle back to the start", new Loops(0, 6), () -> analyse(0, n -> (n * n + 1) % 10));
        check("a tail before the cycle", new Loops(1, 6), () -> analyse(3, n -> (n * n + 1) % 10));
        check("a configuration can be a state plus a head position", new Loops(0, 6), () -> analyse(new Config("R", 0), Practice::bounce));
        check("Collatz from 27 halts after 111 steps", new Halts(111), () -> analyse(27L, n -> n == 1 ? null : n % 2 == 0 ? n / 2 : 3 * n + 1));
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
