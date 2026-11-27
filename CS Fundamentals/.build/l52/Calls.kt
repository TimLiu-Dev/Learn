// Subroutines on the toy CPU. Uses Cpu from Toy.kt: kotlinc Toy.kt Calls.kt -include-runtime -d c.jar
// quad(x) = twice(twice(x)), called from main; twice is called from two places inside quad.
val callsProgram = intArrayOf(
    0x20, 0x80,   // 00  LD  R0, [80]   x
    0x80, 0x08,   // 02  CALL 08        quad(x)
    0x30, 0x81,   // 04  ST  R0, [81]   <- quad returns here
    0x00, 0x00,   // 06  HLT
    0x80, 0x0E,   // 08  CALL 0E        quad: twice(x)
    0x80, 0x0E,   // 0A  CALL 0E              twice(again)
    0x90, 0x00,   // 0C  RET
    0x40, 0x00,   // 0E  ADD R0, R0     twice: x + x
    0x90, 0x00,   // 10  RET
)

fun stackOf(cpu: Cpu): String {                          // from the top (SP) down to FF; SP = 00 means empty
    val top = if (cpu.sp == 0) 0x100 else cpu.sp
    return (top until 0x100).joinToString(" ", "[", "]") { hex(cpu.mem[it]) }
}

fun main() {
    val cpu = Cpu(callsProgram)
    cpu.mem[0x80] = 3
    while (!cpu.halted) {
        val at = cpu.pc
        val text = disassemble(cpu.mem[at], cpu.mem[at + 1])
        cpu.step()
        println("${hex(at)}  ${text.padEnd(12)}  R0=${hex(cpu.reg[0])}  SP=${hex(cpu.sp)}  stack=${stackOf(cpu)}  next pc=${hex(cpu.pc)}")
    }
    println("mem[81] = ${cpu.mem[0x81]} after ${cpu.steps} instructions")

    val runaway = Cpu(intArrayOf(0x80, 0x00))                // 00  CALL 00: a function that calls itself forever
    try {
        while (true) runaway.step()
    } catch (e: IllegalStateException) {
        println("runaway recursion: ${e.message} after ${runaway.steps} calls")
    }
}
