// x³ on the toy CPU, with a multiply subroutine called twice. Uses Cpu from Toy.kt.
// Calling convention: arguments in R0 and R1, the result in R0; MUL may overwrite R1, R2 and R3.
val cubeProgram = intArrayOf(
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
)

fun cube(x: Int): Cpu {
    val cpu = Cpu(cubeProgram)
    cpu.mem[0x80] = x
    while (!cpu.halted) cpu.step()
    return cpu
}

fun main() {
    println("x³ for x = 0..7: ${(0..7).map { cube(it).mem[0x81] }}")
    println("instructions for x = 0..7: ${(0..7).map { cube(it).steps }}")
    val wrong = (0..255).count { cube(it).mem[0x81] != (it * it * it) and 0xFF }
    println("all 256 inputs: wrong = $wrong, SP back to ${hex(cube(200).sp)}")
}
