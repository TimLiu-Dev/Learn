import java.util.*;

public class Practice {
    sealed interface Expr {}
    record Num(int value) implements Expr {}
    record Neg(Expr operand) implements Expr {}
    record Bin(char op, Expr left, Expr right) implements Expr {}   // op is one of + - * /

    static List<String> compile(Expr e) {
        throw new UnsupportedOperationException("TODO: emit the children's code first, then the node's instruction");
    }

    static int execute(List<String> code) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String instr : code) {
            throw new UnsupportedOperationException("TODO: push a number, or pop operands, combine them and push the result");
        }
        return stack.removeLast();
    }

    // CHECKS
    // The tree-walking interpreter, used as the reference: the compiled code must agree with it.
    static int value(Expr e) {
        return switch (e) {
            case Num n -> n.value();
            case Neg n -> -value(n.operand());
            case Bin b -> switch (b.op()) {
                case '+' -> value(b.left()) + value(b.right());
                case '-' -> value(b.left()) - value(b.right());
                case '*' -> value(b.left()) * value(b.right());
                default -> value(b.left()) / value(b.right());
            };
        };
    }

    public static void main(String[] args) {
        Expr onePlusTwoTimesThree = new Bin('+', new Num(1), new Bin('*', new Num(2), new Num(3)));
        Expr eightMinusThreeMinusTwo = new Bin('-', new Bin('-', new Num(8), new Num(3)), new Num(2));
        check("operands are emitted before their operator", List.of("push 1", "push 2", "push 3", "mul", "add"), () -> compile(onePlusTwoTimesThree));
        check("a left-leaning tree compiles left to right", List.of("push 8", "push 3", "sub", "push 2", "sub"), () -> compile(eightMinusThreeMinusTwo));
        check("negation comes after its operand", List.of("push 4", "neg"), () -> compile(new Neg(new Num(4))));
        check("the machine runs 1 + 2 * 3", 7, () -> execute(List.of("push 1", "push 2", "push 3", "mul", "add")));
        check("the machine runs (1 + 2) * 3", 9, () -> execute(List.of("push 1", "push 2", "add", "push 3", "mul")));
        check("operand order matters for sub", 5, () -> execute(List.of("push 8", "push 3", "sub")));
        check("operand order matters for div", 5, () -> execute(List.of("push 20", "push 4", "div")));
        List<Expr> trees = List.of(onePlusTwoTimesThree, eightMinusThreeMinusTwo,
                new Bin('-', new Num(8), new Bin('-', new Num(3), new Num(2))),
                new Bin('/', new Neg(new Bin('*', new Num(7), new Num(6))), new Bin('-', new Num(10), new Num(3))));
        for (int i = 0; i < trees.size(); i++) {
            Expr t = trees.get(i);
            check("compiled code agrees with the interpreter, tree " + (i + 1), value(t), () -> execute(compile(t)));
        }
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
