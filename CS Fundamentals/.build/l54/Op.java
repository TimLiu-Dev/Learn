import java.util.List;

public final class Op {
    public static final int PUSH = 0, LOAD = 1, STORE = 2, NEG = 3, ADD = 4, SUB = 5,
                            MUL = 6, DIV = 7, MOD = 8, POW = 9, RETURN = 10;
    public static final List<String> NAMES =
            List.of("push", "load", "store", "neg", "add", "sub", "mul", "div", "mod", "pow", "return");

    public static boolean hasOperand(int op) { return op <= STORE; }

    private Op() {}
}
