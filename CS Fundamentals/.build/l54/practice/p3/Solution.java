import java.util.*;

public class Practice {
    sealed interface Expr {}
    record Num(int value) implements Expr {}
    record Var(String name) implements Expr {}
    record Neg(Expr operand) implements Expr {}
    record Bin(char op, Expr left, Expr right) implements Expr {}   // op is one of + - * / %

    record Let(String name, Expr value) {}
    record Program(List<Let> lets, Expr result) {}

    static class CalcError extends RuntimeException {
        CalcError(String message) { super(message); }
    }

    static int eval(Expr e, Map<String, Integer> env) {
        return switch (e) {
            case Num n -> n.value();
            case Var v -> {
                Integer value = env.get(v.name());
                if (value == null) throw new CalcError("undefined variable '" + v.name() + "'");
                yield value;
            }
            case Neg n -> -eval(n.operand(), env);
            case Bin b -> {
                int x = eval(b.left(), env);              // post-order: both children first...
                int y = eval(b.right(), env);
                yield switch (b.op()) {                   // ...then the node itself
                    case '+' -> x + y;
                    case '-' -> x - y;
                    case '*' -> x * y;
                    case '/' -> { if (y == 0) throw new CalcError("division by zero"); yield x / y; }
                    case '%' -> { if (y == 0) throw new CalcError("division by zero"); yield x % y; }
                    default -> throw new CalcError("unknown operator '" + b.op() + "'");
                };
            }
        };
    }

    static int run(Program p) {
        Map<String, Integer> env = new HashMap<>();
        for (Let let : p.lets()) env.put(let.name(), eval(let.value(), env));   // evaluate first, then rebind
        return eval(p.result(), env);
    }

    // CHECKS
    static final Map<String, Integer> NONE = Map.of();

    static Object errorOf(Check c) {
        try { return "no error: " + c.get(); } catch (CalcError e) { return e.getMessage(); } catch (Exception e) { return "threw " + e; }
    }

    public static void main(String[] args) {
        check("children are evaluated before their operator", 7, () -> eval(new Bin('+', new Num(1), new Bin('*', new Num(2), new Num(3))), NONE));
        check("negation of a left-leaning tree", -3, () -> eval(new Neg(new Bin('-', new Bin('-', new Num(8), new Num(3)), new Num(2))), NONE));
        check("variables come from the environment", 12, () -> eval(new Bin('*', new Var("x"), new Bin('+', new Var("x"), new Num(1))), Map.of("x", 3)));
        check("a let reads the old value before rebinding", 24, () -> run(new Program(
                List.of(new Let("x", new Num(5)), new Let("x", new Bin('*', new Var("x"), new Var("x")))), new Bin('-', new Var("x"), new Num(1)))));
        check("an undefined variable is a CalcError", "undefined variable 'b'", () -> errorOf(() -> run(new Program(
                List.of(new Let("a", new Num(1))), new Bin('+', new Var("a"), new Var("b"))))));
        check("division by zero is a CalcError", "division by zero", () -> errorOf(() -> eval(new Bin('/', new Num(1), new Num(0)), NONE)));
        check("remainder by zero is a CalcError", "division by zero", () -> errorOf(() -> eval(new Bin('%', new Num(1), new Num(0)), NONE)));
        check("2147483647 + 1 wraps", Integer.MIN_VALUE, () -> eval(new Bin('+', new Num(Integer.MAX_VALUE), new Num(1)), NONE));
        check("-7 / 2 truncates", -3, () -> eval(new Bin('/', new Neg(new Num(7)), new Num(2)), NONE));
        check("-7 % 2 takes the dividend's sign", -1, () -> eval(new Bin('%', new Neg(new Num(7)), new Num(2)), NONE));
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
