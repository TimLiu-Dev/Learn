import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Calc's back end, part 1: compile the tree to stack bytecode.
public class Compiler {
    private final List<Integer> code = new ArrayList<>();
    private final List<String> where = new ArrayList<>();         // source position of each instruction
    private final Map<String, Integer> slots = new LinkedHashMap<>(); // variable name -> local slot
    private int depth = 0, maxStack = 0;

    public Chunk compile(Program p) {
        for (var let : p.lets()) {
            expr(let.value());
            int slot = slots.computeIfAbsent(let.name(), k -> slots.size());
            emit(Op.STORE, "", slot, -1);
        }
        expr(p.result());
        emit(Op.RETURN, "", null, -1);
        return new Chunk(code.stream().mapToInt(Integer::intValue).toArray(),
                where.toArray(String[]::new), maxStack, List.copyOf(slots.keySet()));
    }

    private void expr(Expr e) {
        switch (e) {
            case Expr.Num n -> emit(Op.PUSH, "", n.value(), +1);
            case Expr.Var v -> {
                Integer slot = slots.get(v.name());
                if (slot == null) throw new CalcError("undefined variable '" + v.name() + "' at " + v.at().at());
                emit(Op.LOAD, "", slot, +1);
            }
            case Expr.Neg n -> { expr(n.operand()); emit(Op.NEG, n.at().at(), null, 0); }
            case Expr.Bin b -> {
                expr(b.left());                                    // post-order, just like eval:
                expr(b.right());                                   // both operands end up on the stack,
                int op = switch (b.op()) {                         // then one instruction combines them
                    case PLUS -> Op.ADD; case MINUS -> Op.SUB; case STAR -> Op.MUL;
                    case SLASH -> Op.DIV; case PERCENT -> Op.MOD; case CARET -> Op.POW;
                    default -> throw new IllegalStateException("not an operator: " + b.op());
                };
                emit(op, b.at().at(), null, -1);
            }
        }
    }

    private void emit(int op, String at, Integer operand, int delta) {
        code.add(op); where.add(at);
        if (operand != null) { code.add(operand); where.add(at); }
        depth += delta;
        maxStack = Math.max(maxStack, depth);
    }
}
