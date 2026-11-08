import java.util.HashMap;
import java.util.Map;

public class TuringMachine {
    // A Turing machine: a finite set of states plus an unbounded tape it can read, write and move on.
    static final char BLANK = '_';

    record Rule(char write, int move, String next) {}           // move is -1 (left) or +1 (right)
    record Key(String state, char symbol) {}

    sealed interface Outcome permits Accepted, Rejected, StillRunning {}
    record Accepted(long steps, String tape) implements Outcome {}
    record Rejected(long steps) implements Outcome {}
    record StillRunning(long steps) implements Outcome {}       // we gave up, it didn't

    private final Map<Key, Rule> rules;
    private final String start;

    TuringMachine(Map<Key, Rule> rules, String start) { this.rules = rules; this.start = start; }

    Outcome run(String input, long maxSteps) {
        Map<Integer, Character> tape = new HashMap<>();          // unbounded in both directions
        for (int i = 0; i < input.length(); i++) tape.put(i, input.charAt(i));
        int head = 0;
        String state = start;
        long steps = 0;
        while (true) {
            if (state.equals("accept")) return new Accepted(steps, render(tape));
            if (steps == maxSteps) return new StillRunning(steps);
            Rule rule = rules.get(new Key(state, tape.getOrDefault(head, BLANK)));
            if (rule == null) return new Rejected(steps);        // no rule: reject
            tape.put(head, rule.write());
            head += rule.move();
            state = rule.next();
            steps++;
        }
    }

    private static String render(Map<Integer, Character> tape) {
        int lo = Integer.MAX_VALUE, hi = Integer.MIN_VALUE;
        for (var e : tape.entrySet()) {
            if (e.getValue() != BLANK) { lo = Math.min(lo, e.getKey()); hi = Math.max(hi, e.getKey()); }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = lo; i <= hi; i++) sb.append(tape.getOrDefault(i, BLANK));
        return sb.toString();
    }

    // Rules written as "state symbol -> write move next", one per line
    static TuringMachine machine(String start, String table) {
        Map<Key, Rule> rules = new HashMap<>();
        for (String line : table.strip().split("\n")) {
            String[] p = line.strip().split("\\s+");                 // p[2] is the arrow
            rules.put(new Key(p[0], p[1].charAt(0)), new Rule(p[3].charAt(0), p[4].equals("R") ? 1 : -1, p[5]));
        }
        return new TuringMachine(rules, start);
    }

    static final TuringMachine INCREMENT = machine("right", """
        right 0 -> 0 R right
        right 1 -> 1 R right
        right _ -> _ L carry
        carry 1 -> 0 L carry
        carry 0 -> 1 L accept
        carry _ -> 1 L accept
        """);

    // 0^n 1^n: cross off one 0 (as X) and one matching 1 (as Y), go back, repeat
    static final TuringMachine ZEROS_THEN_ONES = machine("q0", """
        q0 0 -> X R q1
        q0 Y -> Y R q3
        q0 _ -> _ R accept
        q1 0 -> 0 R q1
        q1 Y -> Y R q1
        q1 1 -> Y L q2
        q2 0 -> 0 L q2
        q2 Y -> Y L q2
        q2 X -> X R q0
        q3 Y -> Y R q3
        q3 _ -> _ R accept
        """);

    static final TuringMachine FOREVER = machine("loop", "loop _ -> _ R loop");

    static String describe(Outcome o) {
        return switch (o) {                                       // exhaustive over the sealed type (L7)
            case Accepted a -> "accept after " + a.steps() + " steps, tape=" + a.tape();
            case Rejected r -> "reject after " + r.steps() + " steps";
            case StillRunning s -> "still running after " + s.steps() + " steps";
        };
    }

    public static void main(String[] args) {
        for (String s : new String[] {"1011", "111", "0"})
            System.out.println("increment " + s + ": " + describe(INCREMENT.run(s, 1_000)));
        for (String s : new String[] {"", "01", "0011", "000111", "0010", "011", "10"})
            System.out.println("0^n1^n \"" + s + "\": " + describe(ZEROS_THEN_ONES.run(s, 1_000)));
        for (int n : new int[] {10, 100, 1000}) {
            Accepted a = (Accepted) ZEROS_THEN_ONES.run("0".repeat(n) + "1".repeat(n), 10_000_000);
            System.out.println("0^n1^n n=" + n + ": " + a.steps() + " steps");
        }
        System.out.println("forever: " + describe(FOREVER.run("", 1_000_000)));
    }
}
