// Subroutines on the toy CPU. Uses Toy.Cpu from Toy.java (java Calls.java finds it, JEP 458).
// quad(x) = twice(twice(x)), called from main; twice is called from two places inside quad.
public class Calls {
    static final int[] CALLS_PROGRAM = {
        0x20, 0x80,   // 00  LD  R0, [80]   x
        0x80, 0x08,   // 02  CALL 08        quad(x)
        0x30, 0x81,   // 04  ST  R0, [81]   <- quad returns here
        0x00, 0x00,   // 06  HLT
        0x80, 0x0E,   // 08  CALL 0E        quad: twice(x)
        0x80, 0x0E,   // 0A  CALL 0E              twice(again)
        0x90, 0x00,   // 0C  RET
        0x40, 0x00,   // 0E  ADD R0, R0     twice: x + x
        0x90, 0x00,   // 10  RET
    };

    static String stackOf(Toy.Cpu cpu) {                    // from the top (SP) down to FF; SP = 00 means empty
        int top = cpu.sp == 0 ? 0x100 : cpu.sp;
        StringBuilder s = new StringBuilder("[");
        for (int a = top; a < 0x100; a++) s.append(a == top ? "" : " ").append(Toy.hex(cpu.mem[a]));
        return s.append("]").toString();
    }

    public static void main(String[] args) {
        Toy.Cpu cpu = new Toy.Cpu(CALLS_PROGRAM);
        cpu.mem[0x80] = 3;
        while (!cpu.halted) {
            int at = cpu.pc;
            String text = Toy.disassemble(cpu.mem[at], cpu.mem[at + 1]);
            cpu.step();
            System.out.println(Toy.hex(at) + "  " + String.format("%-12s", text) + "  R0=" + Toy.hex(cpu.reg[0])
                    + "  SP=" + Toy.hex(cpu.sp) + "  stack=" + stackOf(cpu) + "  next pc=" + Toy.hex(cpu.pc));
        }
        System.out.println("mem[81] = " + cpu.mem[0x81] + " after " + cpu.steps + " instructions");

        Toy.Cpu runaway = new Toy.Cpu(new int[] {0x80, 0x00});   // 00  CALL 00: a function that calls itself forever
        try {
            while (true) runaway.step();
        } catch (IllegalStateException e) {
            System.out.println("runaway recursion: " + e.getMessage() + " after " + runaway.steps + " calls");
        }
    }
}
