package katas;

public class Programs {
    // a * b by repeated addition (mod 256): a in mem[80], b in mem[81], product to mem[82].
    public static final int[] MULTIPLY = {
        0x10, 0x00,   // 00  LDI R0, 00     product = 0
        0x24, 0x80,   // 02  LD  R1, [80]   a
        0x28, 0x81,   // 04  LD  R2, [81]   b counts down
        0x1C, 0x01,   // 06  LDI R3, 01     the constant 1
        0x78, 0x10,   // 08  JZ  R2, 10     while b != 0
        0x41, 0x00,   // 0A  ADD R0, R1       product += a
        0x5B, 0x00,   // 0C  SUB R2, R3       b -= 1
        0x60, 0x08,   // 0E  JMP 08
        0x30, 0x82,   // 10  ST  R0, [82]
        0x00, 0x00,   // 12  HLT
    };

    public static Cpu multiply(int a, int b) {
        Cpu cpu = new Cpu(MULTIPLY);
        cpu.mem[0x80] = a;
        cpu.mem[0x81] = b;
        cpu.run();
        return cpu;
    }
}
