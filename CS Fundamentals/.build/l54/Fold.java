import java.util.function.IntSupplier;

// Exercise 4: constant folding, a compile-time optimisation on the tree.
public class Fold {
    static Expr fold(Expr e) {
        return switch (e) {
            case Expr.Num n -> n;
            case Expr.Var v -> v;
            case Expr.Neg n -> {
                Expr inner = fold(n.operand());
                yield inner instanceof Expr.Num(int v) ? new Expr.Num(-v) : new Expr.Neg(inner, n.at());
            }
            case Expr.Bin b -> {
                Expr l = fold(b.left()), r = fold(b.right());
                if (l instanceof Expr.Num(int x) && r instanceof Expr.Num(int y)) {
                    try {
                        yield new Expr.Num(Interp.arith(b.op(), x, y, b.at().at()));
                    } catch (CalcError err) {
                        yield new Expr.Bin(b.op(), l, r, b.at());      // like javac: 1 / 0 stays, and fails at run time
                    }
                }
                yield new Expr.Bin(b.op(), l, r, b.at());
            }
        };
    }

    static Program fold(Program p) {
        return new Program(p.lets().stream().map(l -> new Program.Let(l.name(), fold(l.value()))).toList(), fold(p.result()));
    }

    static String outcome(IntSupplier run) {
        try {
            return String.valueOf(run.getAsInt());
        } catch (CalcError e) {
            return "CalcError: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String src = "let x = 2 * 3 + 4; x * (10 - 8)";
        System.out.print(Vm.disassemble(new Compiler().compile(Parser.parse(src))));
        System.out.println("-- folded: " + Parser.show(fold(Parser.parse(src))).replace("\n", "; "));
        System.out.print(Vm.disassemble(new Compiler().compile(fold(Parser.parse(src)))));
        for (String s : new String[] {src, "1 / 0 + 2", "let y = -(2 ^ 3); y - -1", "2147483647 + 1 + x"}) {
            String plain = outcome(() -> Vm.execute(new Compiler().compile(Parser.parse(s))));
            String folded = outcome(() -> Vm.execute(new Compiler().compile(fold(Parser.parse(s)))));
            System.out.printf("%-32s %-40s same=%s%n", s, folded, plain.equals(folded));
        }
    }
}
