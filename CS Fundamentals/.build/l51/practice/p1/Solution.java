import java.util.*;

public class Practice {
    // A DFA as data: table.get(q) maps an input character to the next state.
    static class Dfa {
        final List<Map<Character, Integer>> table;
        final Set<Integer> accepting;
        final int start = 0;

        Dfa(List<Map<Character, Integer>> table, Set<Integer> accepting) {
            this.table = table;
            this.accepting = accepting;
        }

        List<Integer> trace(String input) {
            List<Integer> states = new ArrayList<>(List.of(start));   // n characters visit n + 1 states
            for (char c : input.toCharArray()) {
                Integer next = table.get(states.get(states.size() - 1)).get(c);
                if (next == null) return null;                          // no move: reject
                states.add(next);
            }
            return states;
        }

        boolean accepts(String input) {
            List<Integer> states = trace(input);
            return states != null && accepting.contains(states.get(states.size() - 1));
        }
    }

    // Binary numbers divisible by m: state r means "remainder r so far".
    static Dfa divisibleBy(int m) {
        List<Map<Character, Integer>> table = new ArrayList<>();
        for (int r = 0; r < m; r++) table.add(Map.of('0', 2 * r % m, '1', (2 * r + 1) % m));
        return new Dfa(table, Set.of(0));
    }

    // CHECKS
    static final Dfa DIV3 = new Dfa(List.of(Map.of('0', 0, '1', 1), Map.of('0', 2, '1', 0), Map.of('0', 1, '1', 2)), Set.of(0));

    static boolean agreesBelow1024(Dfa dfa) {
        for (int v = 0; v < 1024; v++) if (dfa.accepts(Integer.toBinaryString(v)) != (v % 5 == 0)) return false;
        return true;
    }

    public static void main(String[] args) {
        check("the trace of 110 includes the start state", List.of(0, 1, 0, 0), () -> DIV3.trace("110"));
        check("6 is divisible by 3, 7 is not", "true false", () -> DIV3.accepts("110") + " " + DIV3.accepts("111"));
        check("the empty input stays in the start state", List.of(0), () -> DIV3.trace(""));
        check("a character with no move rejects", "null false", () -> DIV3.trace("12") + " " + DIV3.accepts("12"));
        check("divisibleBy(m) has one state per remainder", 7, () -> divisibleBy(7).table.size());
        check("divisibleBy(5) agrees with arithmetic below 1024", true, () -> agreesBelow1024(divisibleBy(5)));
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
