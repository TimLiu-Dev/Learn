const val HLT = 0x0
const val LDI = 0x1
const val LD = 0x2
const val ST = 0x3
const val ADD = 0x4
const val SUB = 0x5
const val JMP = 0x6
const val JZ = 0x7
const val CALL = 0x8
const val RET = 0x9
const val STACK_LIMIT = 0xF0                     // the stack may use F0..FF

fun hex(x: Int) = "%02X".format(x)

class Cpu(program: IntArray) {
    val mem = IntArray(256)
    val reg = IntArray(4)
    var pc = 0
    var sp = 0x00                                // empty stack; the first push goes to FF
    var halted = false

    init { program.copyInto(mem) }

    fun step() {
        val at = pc                              // this instruction's address, for error messages
        val b0 = mem[at]
        val b1 = mem[(at + 1) and 0xFF]
        pc = (at + 2) and 0xFF                   // fetch moves pc past the instruction
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
            CALL -> {
                check(sp != STACK_LIMIT) { "stack overflow at ${hex(at)}: SP=${hex(sp)}" }
                sp = (sp - 1) and 0xFF           // the stack grows down
                mem[sp] = pc                     // pc already points past the CALL: the return address
                pc = b1
            }
            RET -> {
                check(sp != 0x00) { "stack underflow at ${hex(at)}" }
                pc = mem[sp]                     // read the top first...
                sp = (sp + 1) and 0xFF           // ...then pop it
            }
            else -> error("unknown opcode at ${hex(at)}")
        }
    }
}
