import java.util.*;

public class Practice {
    static final int HLT = 0x0, LDI = 0x1, LD = 0x2, ST = 0x3, ADD = 0x4, SUB = 0x5, JMP = 0x6, JZ = 0x7;
    static final int CALL = 0x8, RET = 0x9;
    static final int STACK_LIMIT = 0xF0;                // the stack may use F0..FF

    static String hex(int x) { return String.format("%02X", x); }

    static class Cpu {
        final int[] mem = new int[256];
        final int[] reg = new int[4];
        int pc = 0;
        int sp = 0x00;                                  // empty stack; the first push goes to FF
        boolean halted = false;

        Cpu(int... program) { System.arraycopy(program, 0, mem, 0, program.length); }

        void step() {
            int at = pc;                                // this instruction's address, for error messages
            int b0 = mem[at], b1 = mem[(at + 1) & 0xFF];
            pc = (at + 2) & 0xFF;                       // fetch moves pc past the instruction
            int rd = (b0 >>> 2) & 3, rs = b0 & 3;
            switch (b0 >>> 4) {
                case HLT -> halted = true;
                case LDI -> reg[rd] = b1;
                case LD -> reg[rd] = mem[b1];
                case ST -> mem[b1] = reg[rd];
                case ADD -> reg[rd] = (reg[rd] + reg[rs]) & 0xFF;
                case SUB -> reg[rd] = (reg[rd] - reg[rs]) & 0xFF;
                case JMP -> pc = b1;
                case JZ -> { if (reg[rd] == 0) pc = b1; }
                case CALL -> {
                    if (sp == STACK_LIMIT) throw new IllegalStateException("stack overflow at " + hex(at) + ": SP=" + hex(sp));
                    sp = (sp - 1) & 0xFF;                   // the stack grows down
                    mem[sp] = pc;                           // pc already points past the CALL: the return address
                    pc = b1;
                }
                case RET -> {
                    if (sp == 0x00) throw new IllegalStateException("stack underflow at " + hex(at));
                    pc = mem[sp];                           // read the top first...
                    sp = (sp + 1) & 0xFF;                   // ...then pop it
                }
                default -> throw new IllegalStateException("unknown opcode at " + hex(at));
            }
        }
    }

    // CHECKS
    // Runs until HLT, failing instead of hanging if there's no HLT within 1,000 steps.
    static Cpu run(int... program) {
        Cpu cpu = new Cpu(program);
        for (int i = 0; i < 1000 && !cpu.halted; i++) cpu.step();
        if (!cpu.halted) throw new IllegalStateException("no HLT after 1000 steps (pc=" + hex(cpu.pc) + ")");
        return cpu;
    }

    static final int[] QUAD = {0x20, 0x80, 0x80, 0x08, 0x30, 0x81, 0x00, 0x00,
                               0x80, 0x0E, 0x80, 0x0E, 0x90, 0x00, 0x40, 0x00, 0x90, 0x00};

    public static void main(String[] args) {
        check("CALL pushes the return address and jumps", "pc=04 sp=FF top=02", () -> {
            Cpu cpu = new Cpu(0x80, 0x04);
            cpu.step();
            return "pc=" + hex(cpu.pc) + " sp=" + hex(cpu.sp) + " top=" + hex(cpu.mem[0xFF]);
        });
        check("RET resumes after the CALL", "R0=07 sp=00", () -> {
            Cpu cpu = run(0x80, 0x06, 0x10, 0x07, 0x00, 0x00, 0x90, 0x00);   // CALL 06; LDI R0, 07; HLT; RET
            return "R0=" + hex(cpu.reg[0]) + " sp=" + hex(cpu.sp);
        });
        check("nested calls return in reverse order", "mem[81]=12 sp=00", () -> {
            int[] p = Arrays.copyOf(QUAD, 0x81);
            p[0x80] = 3;
            Cpu cpu = run(p);
            return "mem[81]=" + cpu.mem[0x81] + " sp=" + hex(cpu.sp);
        });
        check("runaway recursion overflows after 16 calls", "stack overflow at 00: SP=F0", () -> {
            try { run(0x80, 0x00); return "no error"; } catch (IllegalStateException e) { return e.getMessage(); }
        });
        check("RET on an empty stack is an underflow", "stack underflow at 00", () -> {
            try { run(0x90, 0x00); return "no error"; } catch (IllegalStateException e) { return e.getMessage(); }
        });
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
