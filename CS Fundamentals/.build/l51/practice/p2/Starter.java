import java.util.*;

public class Practice {
    record Move(int from, char c, int to) {}

    // An NFA: character moves, plus ε-moves {from, to} taken without reading anything.
    record Nfa(int start, int accept, List<Move> moves, List<int[]> eps) {}

    // The states themselves, plus everything reachable from them by ε-moves alone.
    static Set<Integer> closure(Nfa nfa, Set<Integer> states) {
        throw new UnsupportedOperationException("TODO: graph search over the ε-moves (L21)");
    }

    // Read c from every state in the set at once, then take the closure.
    static Set<Integer> step(Nfa nfa, Set<Integer> states, char c) {
        throw new UnsupportedOperationException("TODO: follow every move on c, then close");
    }

    static boolean matches(Nfa nfa, String input) {
        throw new UnsupportedOperationException("TODO: closure of {start}, one step per character");
    }

    // a?{n}a{n} (Cox's pattern from Part 2): states 0..2n, and each of the first n a's may be skipped.
    static Nfa coxNfa(int n) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 2 * n; i++) moves.add(new Move(i, 'a', i + 1));
        List<int[]> eps = new ArrayList<>();
        for (int i = 0; i < n; i++) eps.add(new int[] {i, i + 1});
        return new Nfa(0, 2 * n, moves, eps);
    }

    // CHECKS
    static boolean matchesAll(Nfa nfa, String... inputs) {
        for (String s : inputs) if (!matches(nfa, s)) return false;
        return true;
    }

    public static void main(String[] args) {
        Nfa aqaq = coxNfa(2);                     // a?a?aa, the NFA drawn in Part 2
        Nfa star = new Nfa(0, 1, List.of(new Move(1, 'a', 0)), List.of(new int[] {0, 1}, new int[] {1, 0}));
        check("ε-moves chain, so the start set of a?a?aa is 0, 1 and 2", Set.of(0, 1, 2), () -> closure(aqaq, Set.of(0)));
        check("closure keeps the states it starts from", Set.of(3), () -> closure(aqaq, Set.of(3)));
        check("one step reads a from every state in the set", Set.of(1, 2, 3), () -> step(aqaq, Set.of(0, 1, 2), 'a'));
        check("no move on b", Set.of(), () -> step(aqaq, Set.of(0, 1, 2), 'b'));
        check("a?a?aa matches aa, aaa and aaaa", true, () -> matchesAll(aqaq, "aa", "aaa", "aaaa"));
        check("a?a?aa rejects the empty string, a, aaaaa and ab", "false false false false",
            () -> matches(aqaq, "") + " " + matches(aqaq, "a") + " " + matches(aqaq, "aaaaa") + " " + matches(aqaq, "ab"));
        check("an ε-cycle does not loop forever", Set.of(0, 1), () -> closure(star, Set.of(0)));
        check("a* with an ε-cycle matches the empty string and aaa", true, () -> matchesAll(star, "", "aaa"));
        check("a?{40}a{40} is no problem for state sets", "true false",
            () -> matches(coxNfa(40), "a".repeat(40)) + " " + matches(coxNfa(40), "a".repeat(81)));
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
