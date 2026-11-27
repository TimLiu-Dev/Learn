import java.util.*;

// x³ on the toy CPU, with a multiply subroutine called twice. Uses Toy.Cpu from Toy.java.
// Calling convention: arguments in R0 and R1, the result in R0; MUL may overwrite R1, R2 and R3.
public class Cube {
    static final int[] CUBE_PROGRAM = {
        0x20, 0x80,   // 00  LD  R0, [80]   x
        0x24, 0x80,   // 02  LD  R1, [80]   x
        0x80, 0x10,   // 04  CALL 10        R0 = x * x
        0x24, 0x80,   // 06  LD  R1, [80]   reload x: MUL counted R1 down to 0
        0x80, 0x10,   // 08  CALL 10        R0 = x * x * x
        0x30, 0x81,   // 0A  ST  R0, [81]
        0x00, 0x00,   // 0C  HLT
        0x00, 0x00,   // 0E  (unused)
        0x18, 0x00,   // 10  LDI R2, 00     MUL: acc = 0
        0x1C, 0x01,   // 12  LDI R3, 01     the constant 1
        0x74, 0x1C,   // 14  JZ  R1, 1C     while R1 != 0
        0x48, 0x00,   // 16  ADD R2, R0       acc += R0
        0x57, 0x00,   // 18  SUB R1, R3       R1 -= 1
        0x60, 0x14,   // 1A  JMP 14
        0x10, 0x00,   // 1C  LDI R0, 00     no MOV instruction, so R0 = 0 + acc
        0x42, 0x00,   // 1E  ADD R0, R2
        0x90, 0x00,   // 20  RET
    };

    static Toy.Cpu cube(int x) {
        Toy.Cpu cpu = new Toy.Cpu(CUBE_PROGRAM);
        cpu.mem[0x80] = x;
        while (!cpu.halted) cpu.step();
        return cpu;
    }

    public static void main(String[] args) {
        List<Integer> values = new ArrayList<>(), steps = new ArrayList<>();
        for (int x = 0; x <= 7; x++) { values.add(cube(x).mem[0x81]); steps.add(cube(x).steps); }
        System.out.println("x³ for x = 0..7: " + values);
        System.out.println("instructions for x = 0..7: " + steps);
        int wrong = 0;
        for (int x = 0; x < 256; x++) if (cube(x).mem[0x81] != ((x * x * x) & 0xFF)) wrong++;
        System.out.println("all 256 inputs: wrong = " + wrong + ", SP back to " + Toy.hex(cube(200).sp));
    }
}
