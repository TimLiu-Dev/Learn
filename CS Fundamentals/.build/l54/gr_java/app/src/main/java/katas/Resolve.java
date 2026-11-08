package katas;

import java.util.HashSet;
import java.util.Set;

// A static check, before anything runs: every variable is defined before it's used.
// The compiler does this anyway when it gives names their slots; the interpreter needs it as a separate pass.
public final class Resolve {
    public static void resolve(Program p) {
        var defined = new HashSet<String>();
        for (var let : p.lets()) {
            check(let.value(), defined);
            defined.add(let.name());
        }
        check(p.result(), defined);
    }

    private static void check(Expr e, Set<String> defined) {
        switch (e) {
            case Expr.Num n -> {}
            case Expr.Var v -> {
                if (!defined.contains(v.name())) throw new CalcError("undefined variable '" + v.name() + "' at " + v.at().at());
            }
            case Expr.Neg n -> check(n.operand(), defined);
            case Expr.Bin b -> { check(b.left(), defined); check(b.right(), defined); }
        }
    }

    private Resolve() {}
}
