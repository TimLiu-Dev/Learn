// 2^n by repeated doubling: n in mem[80], answer to mem[81].
// Uses Toy.Cpu from Toy.java.
public class Pow2 {
    static final int[] POW2_PROGRAM = {
        0x10, 0x01,   // 00  LDI R0, 01     result = 1
        0x24, 0x80,   // 02  LD  R1, [80]   n
        0x18, 0x01,   // 04  LDI R2, 01     the constant 1
        0x74, 0x0E,   // 06  JZ  R1, 0E     while n != 0
        0x40, 0x00,   // 08  ADD R0, R0       result += result
        0x56, 0x00,   // 0A  SUB R1, R2       n -= 1
        0x60, 0x06,   // 0C  JMP 06
        0x30, 0x81,   // 0E  ST  R0, [81]
        0x00, 0x00,   // 10  HLT
    };

    public static void main(String[] args) {
        for (int n = 0; n <= 8; n++) {
            Toy.Cpu cpu = new Toy.Cpu(POW2_PROGRAM);
            cpu.mem[0x80] = n;
            while (!cpu.halted) cpu.step();
            System.out.println("2^" + n + " = " + cpu.mem[0x81] + "  (" + cpu.steps + " instructions)");
        }
    }
}
