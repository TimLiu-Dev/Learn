package katas;

// Calc's back end, part 2: a fetch-decode-execute loop over the bytecode, plus a disassembler.
public class Vm {
    public static int execute(Chunk c) {
        int[] code = c.code();
        int[] stack = new int[c.maxStack()];                       // the compiler worked out how deep it gets
        int[] locals = new int[c.locals().size()];
        int sp = 0, pc = 0;
        while (true) {
            int at = pc;
            switch (code[pc++]) {                                  // fetch, decode ...
                case Op.PUSH -> stack[sp++] = code[pc++];          // ... execute
                case Op.LOAD -> stack[sp++] = locals[code[pc++]];
                case Op.STORE -> locals[code[pc++]] = stack[--sp];
                case Op.NEG -> stack[sp - 1] = -stack[sp - 1];
                case Op.ADD -> { sp--; stack[sp - 1] += stack[sp]; }
                case Op.SUB -> { sp--; stack[sp - 1] -= stack[sp]; }
                case Op.MUL -> { sp--; stack[sp - 1] *= stack[sp]; }
                case Op.DIV -> { sp--; stack[sp - 1] = Interp.arith(T.SLASH, stack[sp - 1], stack[sp], c.where()[at]); }
                case Op.MOD -> { sp--; stack[sp - 1] = Interp.arith(T.PERCENT, stack[sp - 1], stack[sp], c.where()[at]); }
                case Op.POW -> { sp--; stack[sp - 1] = Interp.arith(T.CARET, stack[sp - 1], stack[sp], c.where()[at]); }
                case Op.RETURN -> { return stack[--sp]; }
                default -> throw new IllegalStateException("bad opcode " + code[at] + " at " + at);
            }
        }
    }

    public static String disassemble(Chunk c) {
        var sb = new StringBuilder("stack=" + c.maxStack() + ", locals=" + c.locals().size() + " " + c.locals() + "\n");
        int pc = 0;
        while (pc < c.code().length) {
            int op = c.code()[pc];
            sb.append(String.format("%3d: %s", pc, Op.NAMES.get(op)));
            if (Op.hasOperand(op)) {
                int arg = c.code()[pc + 1];
                sb.append(" ").append(arg);
                if (op != Op.PUSH) sb.append("   // ").append(c.locals().get(arg));
                pc += 2;
            } else {
                pc += 1;
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
