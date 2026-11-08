import java.util.ArrayDeque;
import java.util.HashSet;

public class KthFromLast {
    // NFA for "the k-th symbol from the end is 'a'", alphabet {a, b}.
    // States 0..k. State 0 loops on a and b, and on 'a' it may also guess "this is the one"
    // and move to 1. States 1..k-1 move on any symbol. State k accepts.
    static int nfaStep(int states, char c, int k) {   // a set of states, as bits of an int (L48)
        int next = 0;
        for (int q = 0; q <= k; q++) {
            if ((states & (1 << q)) == 0) continue;
            if (q == 0) { next |= 1; if (c == 'a') next |= 1 << 1; }
            else if (q < k) next |= 1 << (q + 1);
            // state k has no moves: it just accepts
        }
        return next;
    }

    static boolean nfaAccepts(String s, int k) {
        int states = 1;                               // {0}
        for (char c : s.toCharArray()) states = nfaStep(states, c, k);
        return (states & (1 << k)) != 0;
    }

    static String showSet(int states, int k) {
        StringBuilder sb = new StringBuilder("{");
        for (int q = 0; q <= k; q++) {
            if ((states & (1 << q)) != 0) { if (sb.length() > 1) sb.append(','); sb.append(q); }
        }
        return sb.append('}').toString();
    }

    // Subset construction: each DFA state is a SET of NFA states. Count the reachable ones.
    static int dfaStatesNeeded(int k) {
        HashSet<Integer> seen = new HashSet<>();
        ArrayDeque<Integer> todo = new ArrayDeque<>();
        seen.add(1); todo.addLast(1);
        while (!todo.isEmpty()) {
            int set = todo.removeFirst();
            for (char c : "ab".toCharArray()) {
                int next = nfaStep(set, c, k);
                if (seen.add(next)) todo.addLast(next);
            }
        }
        return seen.size();
    }

    public static void main(String[] args) {
        int k = 3;
        for (String s : new String[] {"babb", "abab"}) {
            int states = 1;
            StringBuilder trace = new StringBuilder(showSet(states, k));
            for (char c : s.toCharArray()) {
                states = nfaStep(states, c, k);
                trace.append(" -").append(c).append("-> ").append(showSet(states, k));
            }
            System.out.println(s + ": " + trace + "  accept=" + nfaAccepts(s, k));
        }
        System.out.println(" k  NFA states  DFA states");
        for (int kk = 1; kk <= 12; kk++) System.out.printf("%2d  %10d  %10d%n", kk, kk + 1, dfaStatesNeeded(kk));
    }
}
