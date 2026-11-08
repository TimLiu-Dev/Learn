package katas

// The toy ISA from Part 2. Every instruction is 2 bytes: [op:4 | rd:2 | rs:2] [immediate or address]
const val HLT = 0x0
const val LDI = 0x1
const val LD = 0x2
const val ST = 0x3
const val ADD = 0x4
const val SUB = 0x5
const val JMP = 0x6
const val JZ = 0x7

class Cpu(program: IntArray) {
    val mem = IntArray(256)
    val reg = IntArray(4)
    var pc = 0
        private set
    var halted = false
        private set
    var steps = 0
        private set

    init {
        require(program.size <= 256) { "program is ${program.size} bytes; memory is 256" }
        program.copyInto(mem)
    }

    fun step() {
        check(!halted) { "the CPU has halted" }
        val at = pc
        val b0 = mem[at]
        val b1 = mem[(at + 1) and 0xFF]
        pc = (at + 2) and 0xFF
        val rd = (b0 ushr 2) and 3
        val rs = b0 and 3
        when (b0 ushr 4) {
            HLT -> halted = true
            LDI -> reg[rd] = b1
            LD -> reg[rd] = mem[b1]
            ST -> mem[b1] = reg[rd]
            ADD -> reg[rd] = (reg[rd] + reg[rs]) and 0xFF
            SUB -> reg[rd] = (reg[rd] - reg[rs]) and 0xFF
            JMP -> pc = b1
            JZ -> if (reg[rd] == 0) pc = b1
            else -> throw IllegalStateException("unknown opcode ${b0 ushr 4} at address $at")
        }
        steps++
    }

    // No program can decide whether every program halts (L51), so an emulator sets a budget instead.
    fun run(maxSteps: Int = 100_000) {
        while (!halted) {
            check(steps < maxSteps) { "no HLT after $maxSteps steps (pc=$pc)" }
            step()
        }
    }
}
