import java.util.ArrayList;
import java.util.List;

public class NfaSim {
    // Thompson's NFA, as in Russ Cox's article: every state is one of three kinds.
    //   CHAR: read char c, go to out1    SPLIT: go to out1 AND out2 (an ε-choice)    MATCH: accept
    static final int CHAR = 0, SPLIT = 1, MATCH = 2;

    static class Nfa {
        final List<Integer> kind = new ArrayList<>(), out1 = new ArrayList<>(), out2 = new ArrayList<>();
        final List<Character> ch = new ArrayList<>();
        int add(int k, char c, int o1, int o2) {
            kind.add(k); ch.add(c); out1.add(o1); out2.add(o2); return kind.size() - 1;
        }
    }
    record Built(Nfa nfa, int start) {}

    // a?{n}a{n}, built back to front: a? is SPLIT(CHAR a -> next, next)
    static Built coxNfa(int n) {
        Nfa nfa = new Nfa();
        int next = nfa.add(MATCH, ' ', -1, -1);
        for (int i = 0; i < n; i++) next = nfa.add(CHAR, 'a', next, -1);
        for (int i = 0; i < n; i++) { int a = nfa.add(CHAR, 'a', next, -1); next = nfa.add(SPLIT, ' ', a, next); }
        return new Built(nfa, next);
    }

    // Track the SET of states we could be in. Never more than one copy of each state.
    static boolean simulate(Nfa nfa, int start, String input) {
        int states = nfa.kind.size();
        int[] mark = new int[states];                     // mark[s] == step: s already in this step's set
        java.util.Arrays.fill(mark, -1);
        int[] current = new int[states], next = new int[states];
        int[] stack = new int[2 * states + 1];            // each state pushes at most 2 others
        int size = addState(nfa, current, 0, start, 0, mark, stack);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int nextSize = 0;
            for (int k = 0; k < size; k++) {
                int s = current[k];
                if (nfa.kind.get(s) == CHAR && nfa.ch.get(s) == c)
                    nextSize = addState(nfa, next, nextSize, nfa.out1.get(s), i + 1, mark, stack);
            }
            int[] t = current; current = next; next = t; size = nextSize;
            if (size == 0) return false;                  // no state survives: reject early
        }
        for (int k = 0; k < size; k++) if (nfa.kind.get(current[k]) == MATCH) return true;
        return false;
    }

    // Add s0 and its ε-closure to list; returns the new count
    static int addState(Nfa nfa, int[] list, int count, int s0, int step, int[] mark, int[] stack) {
        int top = 0;
        stack[top++] = s0;
        while (top > 0) {
            int s = stack[--top];
            if (mark[s] == step) continue;
            mark[s] = step;
            if (nfa.kind.get(s) == SPLIT) { stack[top++] = nfa.out2.get(s); stack[top++] = nfa.out1.get(s); }
            else list[count++] = s;
        }
        return count;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) { Built b = coxNfa(50); simulate(b.nfa(), b.start(), "a".repeat(50)); }  // warm-up
        for (int n : new int[] {28, 500, 1000, 2000, 4000}) {
            Built b = coxNfa(n);
            long t0 = System.nanoTime();
            boolean ok = simulate(b.nfa(), b.start(), "a".repeat(n));
            double ms = (System.nanoTime() - t0) / 1e6;
            System.out.printf("a?{n}a{n}  n=%-5d states=%-5d match=%s  %8.3f ms%n", n, b.nfa().kind.size(), ok, ms);
        }
    }
}
