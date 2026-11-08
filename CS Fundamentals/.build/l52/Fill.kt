// Writes 1..5 into mem[80..84]. The ISA can't store to an address held
// in a register, so the program edits its own ST instruction's address
// byte (09). Uses Cpu from Toy.kt.
val fillProgram = intArrayOf(
    0x10, 0x01,   // 00  LDI R0, 01     value = 1
    0x14, 0x05,   // 02  LDI R1, 05     count = 5
    0x18, 0x01,   // 04  LDI R2, 01     the constant 1
    0x74, 0x16,   // 06  JZ  R1, 16     while count != 0
    0x30, 0x80,   // 08  ST  R0, [80]     mem[?] = value   <- byte 09 is the "?"
    0x42, 0x00,   // 0A  ADD R0, R2       value += 1
    0x2C, 0x09,   // 0C  LD  R3, [09]     read our own instruction's address byte
    0x4E, 0x00,   // 0E  ADD R3, R2       add 1 to it
    0x3C, 0x09,   // 10  ST  R3, [09]     write it back into the program
    0x56, 0x00,   // 12  SUB R1, R2       count -= 1
    0x60, 0x06,   // 14  JMP 06
    0x00, 0x00,   // 16  HLT
)

fun main() {
    val cpu = Cpu(fillProgram)
    println("before: 08  ${disassemble(cpu.mem[0x08], cpu.mem[0x09])}")
    while (!cpu.halted) cpu.step()
    println("after:  08  ${disassemble(cpu.mem[0x08], cpu.mem[0x09])}")
    println("mem[80..84] = ${(0x80..0x84).map { cpu.mem[it] }}")
}
