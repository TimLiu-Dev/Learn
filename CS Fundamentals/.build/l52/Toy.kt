// A toy 8-bit computer: 4 registers, 256 bytes for code AND data.
// Each instruction is 2 bytes: [op:4 | rd:2 | rs:2] [value or address]
const val HLT = 0x0
const val LDI = 0x1
const val LD = 0x2
const val ST = 0x3
const val ADD = 0x4
const val SUB = 0x5
const val JMP = 0x6
const val JZ = 0x7

fun hex(x: Int) = "%02X".format(x)

fun disassemble(b0: Int, b1: Int): String {
    val rd = (b0 ushr 2) and 3
    val rs = b0 and 3
    return when (b0 ushr 4) {
        HLT -> "HLT"
        LDI -> "LDI R$rd, ${hex(b1)}"
        LD -> "LD  R$rd, [${hex(b1)}]"
        ST -> "ST  R$rd, [${hex(b1)}]"
        ADD -> "ADD R$rd, R$rs"
        SUB -> "SUB R$rd, R$rs"
        JMP -> "JMP ${hex(b1)}"
        JZ -> "JZ  R$rd, ${hex(b1)}"
        else -> "???"
    }
}

class Cpu(program: IntArray) {
    val mem = IntArray(256)
    val reg = IntArray(4)
    var pc = 0
    var halted = false
    var steps = 0

    init { program.copyInto(mem) }

    fun step() {
        // FETCH the two bytes the program counter points at, then move pc on
        val b0 = mem[pc]
        val b1 = mem[(pc + 1) and 0xFF]
        pc = (pc + 2) and 0xFF
        // DECODE: cut the first byte into its fields with shifts and masks (L48)
        val op = b0 ushr 4
        val rd = (b0 ushr 2) and 3
        val rs = b0 and 3
        // EXECUTE
        when (op) {
            HLT -> halted = true
            LDI -> reg[rd] = b1
            LD -> reg[rd] = mem[b1]
            ST -> mem[b1] = reg[rd]
            ADD -> reg[rd] = (reg[rd] + reg[rs]) and 0xFF   // Part 1 proved add8 does exactly this
            SUB -> reg[rd] = (reg[rd] - reg[rs]) and 0xFF
            JMP -> pc = b1
            JZ -> if (reg[rd] == 0) pc = b1                  // isZero8, then a mux picks the next pc
            else -> error("unknown opcode ${hex(op)} at ${hex((pc - 2) and 0xFF)}")
        }
        steps++
    }
}

// sum = 1 + 2 + ... + n, with n in mem[80] and the answer stored to mem[81]
val sumProgram = intArrayOf(
    0x10, 0x00,   // 00  LDI R0, 00     sum = 0
    0x24, 0x80,   // 02  LD  R1, [80]   n
    0x18, 0x01,   // 04  LDI R2, 01     the constant 1
    0x74, 0x0E,   // 06  JZ  R1, 0E     while n != 0
    0x41, 0x00,   // 08  ADD R0, R1       sum += n
    0x56, 0x00,   // 0A  SUB R1, R2       n -= 1
    0x60, 0x06,   // 0C  JMP 06
    0x30, 0x81,   // 0E  ST  R0, [81]   store the answer
    0x00, 0x00,   // 10  HLT
)

fun main() {
    val cpu = Cpu(sumProgram)
    cpu.mem[0x80] = 5
    while (!cpu.halted) {
        val at = cpu.pc
        val text = disassemble(cpu.mem[at], cpu.mem[at + 1])
        cpu.step()
        val regs = cpu.reg.withIndex().joinToString(" ") { (i, v) -> "R$i=${hex(v)}" }
        println("${hex(at)}  ${text.padEnd(14)}  $regs  next pc=${hex(cpu.pc)}")
    }
    println("mem[81] = ${cpu.mem[0x81]} after ${cpu.steps} instructions")
}
