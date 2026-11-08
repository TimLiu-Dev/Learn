package katas;

import java.util.Arrays;

// Thompson's construction: the syntax tree becomes an NFA with at most two states per node,
// and matching tracks the SET of states the NFA could be in (Part 2). Time O(pattern × input).
public class TinyRegex {
    private static final int CHAR = 0, ANY = 1, EPS = 2, SPLIT = 3, MATCH = 4;

    private int[] kind = new int[16], out1 = new int[16], out2 = new int[16];
    private char[] ch = new char[16];
    private int size = 0;
    private final int start;

    // A piece of NFA with one way in and one EPS state whose exit is still unconnected
    private record Frag(int start, int end) {}

    private int add(int k, char c, int o1, int o2) {
        if (size == kind.length) {                              // grow like MyArrayList (L16)
            kind = Arrays.copyOf(kind, size * 2); out1 = Arrays.copyOf(out1, size * 2);
            out2 = Arrays.copyOf(out2, size * 2); ch = Arrays.copyOf(ch, size * 2);
        }
        kind[size] = k; ch[size] = c; out1[size] = o1; out2[size] = o2;
        return size++;
    }
    private int add(int k) { return add(k, ' ', -1, -1); }

    private Frag build(Node node) {
        return switch (node) {                                  // exhaustive: the compiler checks every kind
            case Node.Empty e -> { int s = add(EPS); yield new Frag(s, s); }
            case Node.Lit(char c) -> { int e = add(EPS); yield new Frag(add(CHAR, c, e, -1), e); }
            case Node.AnyChar a -> { int e = add(EPS); yield new Frag(add(ANY, ' ', e, -1), e); }
            case Node.Cat(Node left, Node right) -> {
                Frag l = build(left), r = build(right);
                out1[l.end()] = r.start();
                yield new Frag(l.start(), r.end());
            }
            case Node.Alt(Node left, Node right) -> {
                Frag l = build(left), r = build(right);
                int e = add(EPS);
                out1[l.end()] = e; out1[r.end()] = e;
                yield new Frag(add(SPLIT, ' ', l.start(), r.start()), e);
            }
            case Node.Star(Node body) -> {
                Frag b = build(body);
                int e = add(EPS), s = add(SPLIT, ' ', b.start(), e);
                out1[b.end()] = s;
                yield new Frag(s, e);
            }
            case Node.Plus(Node body) -> {
                Frag b = build(body);
                int e = add(EPS);
                int loop = add(SPLIT, ' ', b.start(), e);       // NOT out1[...] = add(...): see the note below
                out1[b.end()] = loop;
                yield new Frag(b.start(), e);
            }
            case Node.Opt(Node body) -> {
                Frag b = build(body);
                int e = add(EPS);
                out1[b.end()] = e;
                yield new Frag(add(SPLIT, ' ', b.start(), e), e);
            }
        };
    }

    public TinyRegex(String pattern) {
        Frag whole = build(new Parser(pattern).parse());
        int match = add(MATCH);
        out1[whole.end()] = match;
        start = whole.start();
    }

    public int stateCount() { return size; }

    public boolean matches(CharSequence input) {
        int[] mark = new int[size];                             // mark[s] == step: s is already in this step's set
        Arrays.fill(mark, -1);
        int[] stack = new int[2 * size + 1];                    // each state pushes at most 2 others
        int[] current = new int[size], next = new int[size];
        int count = addWithClosure(current, 0, start, 0, mark, stack);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int nextCount = 0;
            for (int k = 0; k < count; k++) {
                int s = current[k];
                if ((kind[s] == CHAR && ch[s] == c) || kind[s] == ANY)
                    nextCount = addWithClosure(next, nextCount, out1[s], i + 1, mark, stack);
            }
            int[] t = current; current = next; next = t; count = nextCount;
            if (count == 0) return false;
        }
        for (int k = 0; k < count; k++) if (kind[current[k]] == MATCH) return true;
        return false;
    }

    // Add s0 and everything reachable by ε-moves, without recursion
    private int addWithClosure(int[] list, int count, int s0, int step, int[] mark, int[] stack) {
        int top = 0;
        stack[top++] = s0;
        while (top > 0) {
            int s = stack[--top];
            if (mark[s] == step) continue;
            mark[s] = step;
            switch (kind[s]) {
                case EPS -> stack[top++] = out1[s];
                case SPLIT -> { stack[top++] = out2[s]; stack[top++] = out1[s]; }
                default -> list[count++] = s;                   // CHAR, ANY, MATCH: states that wait for input
            }
        }
        return count;
    }
}
