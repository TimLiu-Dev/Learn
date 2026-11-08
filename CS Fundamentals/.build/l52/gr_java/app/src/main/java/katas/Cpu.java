package katas;

// The toy ISA from Part 2. Every instruction is 2 bytes: [op:4 | rd:2 | rs:2] [immediate or address]
public class Cpu {
    public static final int HLT = 0x0, LDI = 0x1, LD = 0x2, ST = 0x3, ADD = 0x4, SUB = 0x5, JMP = 0x6, JZ = 0x7;

    public final int[] mem = new int[256];
    public final int[] reg = new int[4];
    private int pc = 0;
    private boolean halted = false;
    private int steps = 0;

    public Cpu(int[] program) {
        if (program.length > 256)
            throw new IllegalArgumentException("program is " + program.length + " bytes; memory is 256");
        System.arraycopy(program, 0, mem, 0, program.length);
    }

    public int pc() { return pc; }
    public boolean halted() { return halted; }
    public int steps() { return steps; }

    public void step() {
        if (halted) throw new IllegalStateException("the CPU has halted");
        int at = pc;
        int b0 = mem[at];
        int b1 = mem[(at + 1) & 0xFF];
        pc = (at + 2) & 0xFF;
        int rd = (b0 >>> 2) & 3;
        int rs = b0 & 3;
        switch (b0 >>> 4) {
            case HLT -> halted = true;
            case LDI -> reg[rd] = b1;
            case LD -> reg[rd] = mem[b1];
            case ST -> mem[b1] = reg[rd];
            case ADD -> reg[rd] = (reg[rd] + reg[rs]) & 0xFF;
            case SUB -> reg[rd] = (reg[rd] - reg[rs]) & 0xFF;
            case JMP -> pc = b1;
            case JZ -> { if (reg[rd] == 0) pc = b1; }
            default -> throw new IllegalStateException("unknown opcode " + (b0 >>> 4) + " at address " + at);
        }
        steps++;
    }

    // No program can decide whether every program halts (L51), so an emulator sets a budget instead.
    public void run(int maxSteps) {
        while (!halted) {
            if (steps >= maxSteps)
                throw new IllegalStateException("no HLT after " + maxSteps + " steps (pc=" + pc + ")");
            step();
        }
    }

    public void run() { run(100_000); }
}
