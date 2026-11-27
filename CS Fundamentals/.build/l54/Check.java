import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// A checker for a tiny typed cousin of Calc: one more walk over the tree,
// before anything runs. It resolves every name and works out every type.
public class Check {
    enum Type { INT, BOOL }

    sealed interface E {}
    record Num(int v) implements E {}
    record Bool(boolean v) implements E {}
    record Var(String name) implements E {}
    record Bin(String op, E l, E r) implements E {}          // + * < and
    record If(E cond, E then, E orElse) implements E {}
    record Let(String name, E value, E body) implements E {}

    static class TypeError extends RuntimeException {
        TypeError(String message) { super(message); }
    }

    static Type check(E e, Map<String, Type> scope) {
        return switch (e) {
            case Num n -> Type.INT;
            case Bool b -> Type.BOOL;
            case Var v -> {
                Type t = scope.get(v.name());                       // name resolution
                if (t == null) throw new TypeError("undefined variable '" + v.name() + "'");
                yield t;
            }
            case Let let -> {
                Map<String, Type> inner = new HashMap<>(scope);     // a new, inner scope
                inner.put(let.name(), check(let.value(), scope));
                yield check(let.body(), inner);
            }
            case Bin b -> {
                Type l = check(b.l(), scope);
                Type r = check(b.r(), scope);
                Type want = b.op().equals("and") ? Type.BOOL : Type.INT;
                if (l != want || r != want)
                    throw new TypeError("'" + b.op() + "' needs " + want + " and " + want + ", got " + l + " and " + r);
                yield b.op().equals("<") ? Type.BOOL : want;
            }
            case If i -> {
                if (check(i.cond(), scope) != Type.BOOL) throw new TypeError("'if' needs a BOOL condition");
                Type t = check(i.then(), scope);
                Type f = check(i.orElse(), scope);
                if (t != f) throw new TypeError("'if' branches differ: " + t + " vs " + f);
                yield t;
            }
        };
    }

    public static void main(String[] args) {
        Map<String, E> programs = new LinkedHashMap<>();
        programs.put("let x = 2 in x * 3 < 7", new Let("x", new Num(2), new Bin("<", new Bin("*", new Var("x"), new Num(3)), new Num(7))));
        programs.put("1 + (2 < 3)", new Bin("+", new Num(1), new Bin("<", new Num(2), new Num(3))));
        programs.put("if 1 then 2 else 3", new If(new Num(1), new Num(2), new Num(3)));
        programs.put("if true then 2 else false", new If(new Bool(true), new Num(2), new Bool(false)));
        programs.put("let y = 1 in x + y", new Let("y", new Num(1), new Bin("+", new Var("x"), new Var("y"))));
        for (var p : programs.entrySet()) {
            String verdict;
            try { verdict = "ok: " + check(p.getValue(), Map.of()); }
            catch (TypeError err) { verdict = "error: " + err.getMessage(); }
            System.out.printf("%-26s %s%n", p.getKey(), verdict);
        }
    }
}
