package katas;

import java.util.HashMap;
import java.util.Map;

// Calc's tree-walking interpreter: evaluate the children first, then the node (post-order).
public class Interp {
    public static int eval(Expr e, Map<String, Integer> env) {
        return switch (e) {
            case Expr.Num n -> n.value();
            case Expr.Var v -> {
                Integer value = env.get(v.name());
                if (value == null) throw new CalcError("undefined variable '" + v.name() + "' at " + v.at().at());
                yield value;
            }
            case Expr.Neg n -> -eval(n.operand(), env);
            case Expr.Bin b -> arith(b.op(), eval(b.left(), env), eval(b.right(), env), b.at().at());
        };
    }

    public static int interpret(Program p) {
        var env = new HashMap<String, Integer>();             // the environment: name -> value
        for (var let : p.lets()) env.put(let.name(), eval(let.value(), env));
        return eval(p.result(), env);
    }

    // The arithmetic both back ends share. int wraps around on overflow, exactly like iadd (L2).
    public static int arith(T op, int a, int b, String at) {
        return switch (op) {
            case PLUS -> a + b;
            case MINUS -> a - b;
            case STAR -> a * b;
            case SLASH -> { if (b == 0) throw new CalcError("division by zero at " + at); yield a / b; }
            case PERCENT -> { if (b == 0) throw new CalcError("division by zero at " + at); yield a % b; }
            case CARET -> { if (b < 0) throw new CalcError("negative exponent at " + at); yield pow(a, b); }
            default -> throw new IllegalStateException("not an operator: " + op);
        };
    }

    private static int pow(int base, int exp) {               // square-and-multiply, as in L48's powMod
        int result = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) result *= base;
            base *= base;
            exp >>= 1;
        }
        return result;
    }
}
