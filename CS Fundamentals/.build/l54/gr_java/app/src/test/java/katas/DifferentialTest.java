package katas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

// Generates random programs, then checks two independent properties on each one.
class DifferentialTest {
    // One generated expression: its source text, and the tree the parser should build for it.
    record Gen(String src, String tree) {}
    record Generated(String src, String tree) {}

    private static final List<String> NAMES = List.of("a", "b", "c", "d");
    private static final List<String> OPS = List.of("+", "-", "*", "+", "-", "*", "/", "%", "^"); // the risky three less often

    private static <E> E pick(Random r, List<E> list) { return list.get(r.nextInt(list.size())); }

    // Mostly names that are already defined: blind picks made 86% of programs end in an error.
    private static Gen expr(Random r, int depth, List<String> defined) {
        switch (depth == 0 ? r.nextInt(2) : r.nextInt(5)) {
            case 0 -> {
                String n = String.valueOf(r.nextInt(10));
                return new Gen(n, n);
            }
            case 1 -> {                                                  // 1 in 40 names may be undefined
                String name = r.nextInt(40) == 0 ? pick(r, NAMES)
                        : defined.isEmpty() ? String.valueOf(r.nextInt(10)) : pick(r, defined);
                return new Gen(name, name);
            }
            case 2 -> {
                Gen e = expr(r, depth - 1, defined);
                return new Gen("(-" + e.src() + ")", "(neg " + e.tree() + ")");   // parenthesised: -x ^ y is -(x ^ y)
            }
            default -> {
                Gen l = expr(r, depth - 1, defined), rt = expr(r, depth - 1, defined);
                String op = pick(r, OPS);
                return new Gen("(" + l.src() + " " + op + " " + rt.src() + ")", "(" + op + " " + l.tree() + " " + rt.tree() + ")");
            }
        }
    }

    private static Generated program(Random r) {
        var defined = new ArrayList<String>();
        var src = new StringBuilder();
        var tree = new ArrayList<String>();
        int lets = r.nextInt(4);
        for (int i = 0; i < lets; i++) {
            String name = pick(r, NAMES);
            Gen e = expr(r, 3, List.copyOf(defined));
            defined.add(name);
            src.append("let ").append(name).append(" = ").append(e.src()).append("; ");
            tree.add("let " + name + " = " + e.tree());
        }
        Gen result = expr(r, 3, defined);
        tree.add(result.tree());
        return new Generated(src + result.src(), String.join("\n", tree));
    }

    @Test void parserBuildsExactlyTheGeneratedTree() {
        var r = new Random(54);
        for (int i = 0; i < 10_000; i++) {
            Generated g = program(r);
            assertEquals(g.tree(), Parser.show(Parser.parse(g.src())), g.src());
        }
    }

    @Test void interpreterAndCompiledCodeAgreeOn10000Programs() {
        var r = new Random(54);
        var kinds = new TreeMap<String, Integer>();
        for (int i = 0; i < 10_000; i++) {
            Generated g = program(r);
            Outcome expected = Calc.runInterpreted(g.src());
            String kind = expected instanceof Outcome.Error(String m) ? m.split(" at ")[0].split(" '")[0] : "value";
            kinds.merge(kind, 1, Integer::sum);
            assertEquals(expected, Calc.runCompiled(g.src()), g.src());
        }
        System.out.println("10,000 programs agree: " + kinds);
    }
}
