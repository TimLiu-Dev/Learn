import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class FoolDfa {
    // Any DFA over {0, 1}: states are 0 until states, start is 0.
    record Dfa(int states, Set<Integer> accepting, BiFunction<Integer, Character, Integer> step) {
        int run(String s) {
            int q = 0;
            for (char c : s.toCharArray()) q = step.apply(q, c);
            return q;
        }
        boolean accepts(String s) { return accepting.contains(run(s)); }
    }

    // A DFA that tries to accept 0^n 1^n by counting zeros, but can only count up to max.
    // States: 0..max = "zeros seen so far" (saturating), max+1..2max+1 = "ones still needed", 2max+2 = dead.
    static Dfa countingDfa(int max) {
        int need0 = max + 1, dead = 2 * max + 2;
        return new Dfa(2 * max + 3, Set.of(0, need0), (q, c) -> {
            if (q == dead) return dead;
            if (q <= max && c == '0') return Math.min(q + 1, max);      // zeros phase: count (saturating)
            if (q <= max) return q == 0 ? dead : need0 + q - 1;         // first 1: that many ones to go
            if (c == '1' && q > need0) return q - 1;                     // ones phase: one fewer to go
            return dead;
        });
    }

    // Pigeonhole (L48): the k+1 prefixes 0^0 .. 0^k can't all land in different states of a k-state DFA.
    static String fool(Dfa dfa) {
        Map<Integer, Integer> firstSeen = new HashMap<>();   // state -> i of the first 0^i that reached it
        for (int j = 0; j <= dfa.states(); j++) {
            int q = dfa.run("0".repeat(j));
            Integer i = firstSeen.putIfAbsent(q, j);
            if (i == null) continue;
            // 0^i and 0^j end in the same state, so 0^i 1^i and 0^j 1^i get the same verdict.
            // Exactly one of them is in the language, so the DFA is wrong about one.
            String right = "0".repeat(i) + "1".repeat(i);
            return dfa.accepts(right) ? "0".repeat(j) + "1".repeat(i) : right;
        }
        throw new IllegalStateException("impossible: more prefixes than states");
    }

    public static void main(String[] args) {
        for (int max : new int[] {3, 14}) {
            Dfa dfa = countingDfa(max);
            String s = fool(dfa);
            long zeros = s.chars().filter(c -> c == '0').count();
            long ones = s.length() - zeros;
            System.out.println(dfa.states() + "-state DFA: fooled by 0^" + zeros + " 1^" + ones
                + ", says " + dfa.accepts(s) + ", truth " + (zeros == ones));
        }
    }
}
