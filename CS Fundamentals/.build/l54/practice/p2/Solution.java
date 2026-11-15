import java.util.*;
import java.util.regex.*;

public class Practice {
    sealed interface Expr {}
    record Num(int value) implements Expr {}
    record Bin(char op, Expr left, Expr right) implements Expr {}

    static class CalcError extends RuntimeException {
        CalcError(String message) { super(message); }
    }

    static String show(Expr e) {
        return switch (e) {
            case Num n -> String.valueOf(n.value());
            case Bin b -> "(" + b.op() + " " + show(b.left()) + " " + show(b.right()) + ")";
        };
    }

    // Tokens are regular, so one regex scans them: numbers, operators, and any other single character.
    static List<String> tokenize(String src) {
        return Pattern.compile("\\d+|\\S").matcher(src).results().map(MatchResult::group).toList();
    }

    static Expr parse(String src) { return new Parser(tokenize(src)).parse(); }

    //   expr := term ( ( "+" | "-" ) term )*
    //   term := atom ( ( "*" | "/" ) atom )*
    //   atom := NUMBER | "(" expr ")"
    static class Parser {
        private final List<String> tokens;
        private int pos = 0;

        Parser(List<String> tokens) { this.tokens = tokens; }

        Expr parse() {
            Expr e = expr();
            if (pos < tokens.size()) throw new CalcError("unexpected '" + tokens.get(pos) + "' at token " + (pos + 1));
            return e;
        }

        private Expr expr() {
            Expr left = term();
            while (Objects.equals(peek(), "+") || Objects.equals(peek(), "-")) {
                char op = next().charAt(0);
                left = new Bin(op, left, term());     // the tree so far becomes the left child: left-associative
            }
            return left;
        }

        private Expr term() {
            Expr left = atom();
            while (Objects.equals(peek(), "*") || Objects.equals(peek(), "/")) {
                char op = next().charAt(0);
                left = new Bin(op, left, atom());
            }
            return left;
        }

private Expr atom() {
            String t = next();
            if (t == null) throw new CalcError("expected a number or '(' but found end of input");
            if (t.equals("(")) {
                Expr inside = expr();
                if (!")".equals(next())) throw new CalcError("expected ')'");
                return inside;
            }
            if (t.charAt(0) >= '0' && t.charAt(0) <= '9') return new Num(Integer.parseInt(t));
            throw new CalcError("expected a number or '(' but found '" + t + "'");
        }

        private String peek() { return pos < tokens.size() ? tokens.get(pos) : null; }
        private String next() { String t = peek(); pos++; return t; }
    }

    // CHECKS
    static int value(Expr e) {
        return switch (e) {
            case Num n -> n.value();
            case Bin b -> switch (b.op()) {
                case '+' -> value(b.left()) + value(b.right());
                case '-' -> value(b.left()) - value(b.right());
                case '*' -> value(b.left()) * value(b.right());
                default -> value(b.left()) / value(b.right());
            };
        };
    }

    static Object errorOf(String src) {
        try { return "no error: " + show(parse(src)); } catch (CalcError e) { return e.getMessage(); }
    }

    public static void main(String[] args) {
        check("multiplication binds tighter than addition", "(+ 1 (* 2 3))", () -> show(parse("1+2*3")));
        check("1+2*3 is 7", 7, () -> value(parse("1+2*3")));
        check("parentheses override precedence", "(* (+ 1 2) 3)", () -> show(parse("(1+2)*3")));
        check("(1+2)*3 is 9", 9, () -> value(parse("(1+2)*3")));
        check("subtraction is left-associative", "(- (- 8 3) 2)", () -> show(parse("8 - 3 - 2")));
        check("8-3-2 is 3", 3, () -> value(parse("8 - 3 - 2")));
        check("division is left-associative too", 2, () -> value(parse("100 / 10 / 5")));
        check("a longer mix", "(- (+ (* 2 3) (* 4 5)) 6)", () -> show(parse("2*3 + 4*5 - 6")));
        check("a missing operand is reported", "expected a number or '(' but found end of input", () -> errorOf("1 +"));
        check("leftover tokens are reported", "unexpected '2' at token 2", () -> errorOf("1 2"));
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
