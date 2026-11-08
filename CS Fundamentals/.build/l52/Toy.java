// A toy 8-bit computer: 4 registers, 256 bytes for code AND data.
// Each instruction is 2 bytes: [op:4 | rd:2 | rs:2] [value or address]
public class Toy {
    static final int HLT = 0x0, LDI = 0x1, LD = 0x2, ST = 0x3;
    static final int ADD = 0x4, SUB = 0x5, JMP = 0x6, JZ = 0x7;

    static String hex(int x) { return String.format("%02X", x); }

    static String disassemble(int b0, int b1) {
        int rd = (b0 >>> 2) & 3, rs = b0 & 3;
        return switch (b0 >>> 4) {
            case HLT -> "HLT";
            case LDI -> "LDI R" + rd + ", " + hex(b1);
            case LD -> "LD  R" + rd + ", [" + hex(b1) + "]";
            case ST -> "ST  R" + rd + ", [" + hex(b1) + "]";
            case ADD -> "ADD R" + rd + ", R" + rs;
            case SUB -> "SUB R" + rd + ", R" + rs;
            case JMP -> "JMP " + hex(b1);
            case JZ -> "JZ  R" + rd + ", " + hex(b1);
            default -> "???";
        };
    }

    static class Cpu {
        final int[] mem = new int[256];
        final int[] reg = new int[4];
        int pc = 0;
        boolean halted = false;
        int steps = 0;

        Cpu(int[] program) { System.arraycopy(program, 0, mem, 0, program.length); }

        void step() {
            // FETCH the two bytes the program counter points at, then move pc on
            int b0 = mem[pc];
            int b1 = mem[(pc + 1) & 0xFF];
            pc = (pc + 2) & 0xFF;
            // DECODE: cut the first byte into its fields with shifts and masks (L48)
            int op = b0 >>> 4;
            int rd = (b0 >>> 2) & 3;
            int rs = b0 & 3;
            // EXECUTE
            switch (op) {
                case HLT -> halted = true;
                case LDI -> reg[rd] = b1;
                case LD -> reg[rd] = mem[b1];
                case ST -> mem[b1] = reg[rd];
                case ADD -> reg[rd] = (reg[rd] + reg[rs]) & 0xFF;   // Part 1 proved add8 does exactly this
                case SUB -> reg[rd] = (reg[rd] - reg[rs]) & 0xFF;
                case JMP -> pc = b1;
                case JZ -> { if (reg[rd] == 0) pc = b1; }          // isZero8, then a mux picks the next pc
                default -> throw new IllegalStateException("unknown opcode " + hex(op) + " at " + hex((pc - 2) & 0xFF));
            }
            steps++;
        }
    }

    // sum = 1 + 2 + ... + n, with n in mem[80] and the answer stored to mem[81]
    static final int[] SUM_PROGRAM = {
        0x10, 0x00,   // 00  LDI R0, 00     sum = 0
        0x24, 0x80,   // 02  LD  R1, [80]   n
        0x18, 0x01,   // 04  LDI R2, 01     the constant 1
        0x74, 0x0E,   // 06  JZ  R1, 0E     while n != 0
        0x41, 0x00,   // 08  ADD R0, R1       sum += n
        0x56, 0x00,   // 0A  SUB R1, R2       n -= 1
        0x60, 0x06,   // 0C  JMP 06
        0x30, 0x81,   // 0E  ST  R0, [81]   store the answer
        0x00, 0x00,   // 10  HLT
    };

    public static void main(String[] args) {
        Cpu cpu = new Cpu(SUM_PROGRAM);
        cpu.mem[0x80] = 5;
        while (!cpu.halted) {
            int at = cpu.pc;
            String text = disassemble(cpu.mem[at], cpu.mem[at + 1]);
            cpu.step();
            StringBuilder regs = new StringBuilder();
            for (int i = 0; i < 4; i++) regs.append(i == 0 ? "" : " ").append("R").append(i).append("=").append(hex(cpu.reg[i]));
            System.out.println(hex(at) + "  " + String.format("%-14s", text) + "  " + regs + "  next pc=" + hex(cpu.pc));
        }
        System.out.println("mem[81] = " + cpu.mem[0x81] + " after " + cpu.steps + " instructions");
    }
}
